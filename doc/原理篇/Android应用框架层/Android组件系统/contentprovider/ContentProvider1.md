# ContentProvider系列1-获取ContentProvider接口

###### 本篇基于Android Framework 7.1.1 的源码

### Basics
记录ContentProvider相关信息的数据对象
##### 1. ContentProviderRecord
在记录ContentProvider重要信息的数据对象中，毫无疑问ContentProviderRecord是最重要的。无论是在ProcessRecord中记录本APP内已经发布的ContentProvider，还是在AMS中记录所有APP发布的ContentProvider。
ContentProviderRecord中记录的重要内容：
![ContentProviderRecord](https://github.com/wusp/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F/contentprovider/ContentProviderRecord.jpg)
##### 2. ProviderClientRecord
另外一个重要的则是ProviderClientRecord，用于在Client APP中缓存已经获得的ContentProvider信息（包括本地的ContentProvider）。在使用ContentProvider时，如果发现缓存中已经存在对应的ContentProvider信息，则
不再向AMS请求获取ContentProvider接口，而是直接使用缓存中记录的IContentProvider接口。
![ProviderClientRecord](https://github.com/wusp/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F/contentprovider/ProviderClientRecord.jpg)
ProviderClientRecord中记录的IContentProvider是由ContentProviderHolder传来，在AMS初始化ContentProviderHolder时会将其中的IContentProvider赋值为IContentProvider的代理对象——**ContentProviderProxy**。后续通过ContentResolver执行ContentProvider的CRUD操作时，便是通过ContentProviderProxy对象Binder IPC到实际的ContentProvider中。

### Acquire ContentProvider on App
在Client APP使用过程中，一般不会直接与ContentProvider进行通信，使用ContentProvider的请求都是交由ContentResolver来执行。平时获取ContentResolver都是通过Context的getContentResolver()来执行，最终都会执行的是**ContextImpl**中的getContentResolver():

```
//ContextImpl.java
public ContentResolver getContentResolver() {
        return mContentResolver;
}
```
返回的是已经保存在Context中的mContentResolver，这一实例的初始化在ContextImpl的相关初始化函数当中。

```
//ContextImpl.java
...
mContentResolver = new ApplicationContentResolver(this, mainThread, user);
...
```
这里赋值的实例**ApplicationContentResolver**类型，这是一个ContentResolver接口的实现类，里边重写了**获取和释放ContentProvider的逻辑**。

```     
    @Override
    protected IContentProvider acquireProvider(Context context, String auth) {
        return mMainThread.acquireProvider(context,
                ContentProvider.getAuthorityWithoutUserId(auth),
                resolveUserIdFromAuthority(auth), true);
    }

    @Override
    protected IContentProvider acquireExistingProvider(Context context, String auth) {
        return mMainThread.acquireExistingProvider(context,
                ContentProvider.getAuthorityWithoutUserId(auth),
                resolveUserIdFromAuthority(auth), true);
    }

    @Override
    public boolean releaseProvider(IContentProvider provider) {
        return mMainThread.releaseProvider(provider, true);
    }

    @Override
    protected IContentProvider acquireUnstableProvider(Context c, String auth) {
        return mMainThread.acquireProvider(c,
                ContentProvider.getAuthorityWithoutUserId(auth),
                resolveUserIdFromAuthority(auth), false);
    }

    @Override
    public boolean releaseUnstableProvider(IContentProvider icp) {
        return mMainThread.releaseProvider(icp, false);
    }

    @Override
    public void unstableProviderDied(IContentProvider icp) {
        mMainThread.handleUnstableProviderDied(icp.asBinder(), true);
    }

    @Override
    public void appNotRespondingViaProvider(IContentProvider icp) {
        mMainThread.appNotRespondingViaProvider(icp.asBinder());
    }
```
不过这里实现的内容只是相当于转发了一下请求到ActivityThread当中去执行具体的逻辑，由ActivityThread来负责同AMS进行交互。

**acquireExistingProvider（）**、**acquireProvider（）**和**acquireUnstableProvider（）**最终执行的都是同一个方法***acquireProvider()***，区别在于最后一个标识符，true代表stable provider，false代表unstable provider.
在**ActivityThread**中的实现：

```
final IContentProvider provider = acquireExistingProvider(c, auth, userId, stable);
        if (provider != null) {
            return provider;
        }
        IActivityManager.ContentProviderHolder holder = null;
        try {
            holder = ActivityManagerNative.getDefault().getContentProvider(
                    getApplicationThread(), auth, userId, stable);
        } catch (RemoteException ex) {
        }
        if (holder == null) {
            Slog.e(TAG, "Failed to find provider info for " + auth);
            return null;
        }

        holder = installProvider(c, holder, holder.info,
                true /*noisy*/, holder.noReleaseNeeded, stable);
        return holder.provider;

```
通过 **acquireProvider()** 获取到provider接口主要分为两部分：

+ 1.首先尝试获取缓存的IContentProvider。
+ 2.获取不到才通过AMS获取IContentProvider。

###### 第一部分：获取缓存中的IContentProvider的逻辑：

```
        synchronized (mProviderMap) {
            final ProviderKey key = new ProviderKey(auth, userId);
            final ProviderClientRecord pr = mProviderMap.get(key);
            if (pr == null) {
                return null;
            }

            IContentProvider provider = pr.mProvider;
            IBinder jBinder = provider.asBinder();
            if (!jBinder.isBinderAlive()) {
                Log.i(TAG, "Acquiring provider " + auth + " for user " + userId
                        + ": existing object's process dead");
                handleUnstableProviderDiedLocked(jBinder, true);
                return null;
            }
            ProviderRefCount prc = mProviderRefCountMap.get(jBinder);
            if (prc != null) {
                incProviderRefLocked(prc, stable);
            }
            return provider;
        }
```
+ 由于接口可能会被多线程调用，因此这里先加了锁。
+ 首先通过auth、userId从mProviderMap中获取provider。
+ 如果该provider所在的进程已经死亡则调用handleUnstableProviderDiedLocked，并返回null。
+ 在handleUnstableProviderDiedLocked()里，如果死亡的provider保存在**需要释放的Provider列表里**，则对其进行释放清理；并通知AMS该unstable provider已经死亡。
+ 如果保存在**需要释放的Provider列表里**，则对其引用计数+1。

###### 第二部分：通过AMS.getContentProvider()获取IContentProvider接口
最后都在getContentProviderImpl()中执行：
这里是Blocking执行，lock是AMS。这把锁很多地方都用到，比方注册广播接收器时也是用的这把锁。

```
            long startTime = SystemClock.uptimeMillis();
            //获取Caller的进程信息
            ProcessRecord r = null;
            if (caller != null) {
                r = getRecordForAppLocked(caller);
                if (r == null) {
                    throw new SecurityException(
                            "Unable to find app for caller " + caller
                          + " (pid=" + Binder.getCallingPid()
                          + ") when getting content provider " + name);
                }
            }
            boolean checkCrossUser = true;
            checkTime(startTime, "getContentProviderImpl: getProviderByName");
            //检查对应的ContentProvider是否已经存在
            cpr = mProviderMap.getProviderByName(name, userId);
            if (cpr == null && userId != UserHandle.USER_SYSTEM) {
                cpr = mProviderMap.getProviderByName(name, UserHandle.USER_SYSTEM);
                if (cpr != null) {
                    cpi = cpr.info;
                    if (isSingleton(cpi.processName, cpi.applicationInfo,
                            cpi.name, cpi.flags)
                            && isValidSingletonCall(r.uid, cpi.applicationInfo.uid)) {
                        userId = UserHandle.USER_SYSTEM;
                        checkCrossUser = false;
                    } else {
                        //不是单例则清空继续执行获取
                        cpr = null;
                        cpi = null;
                    }
                }
            }

            // ContentProvider存在同时所在进程存在&该进程没被杀
            boolean providerRunning = cpr != null && cpr.proc != null && !cpr.proc.killed;
            // 后续的内容分段讲解
            ...
            ...
```
AMS会先尝试获取缓存的ContentProvider信息，并判断该ContentProvider是否保持运行，并以此为根据来进行后续的执行。后续方法体的执行分为三部分：

**1.ContentProvider保持运行**：

```
if (providerRunning) {
                cpi = cpr.info;
                String msg;
                checkTime(startTime, "getContentProviderImpl: before checkContentProviderPermission");
                if ((msg = checkContentProviderPermissionLocked(cpi, r, userId, checkCrossUser))
                        != null) {
                    throw new SecurityException(msg);
                }
                checkTime(startTime, "getContentProviderImpl: after checkContentProviderPermission");

                //如果provider已经发布或者正在发布则直接返回。
                if (r != null && cpr.canRunHere(r)) {
                    ContentProviderHolder holder = cpr.newHolder(null);
                    holder.provider = null;
                    return holder;
                }
                try {
                    if (AppGlobals.getPackageManager()
                            .resolveContentProvider(name, 0 /*flags*/, userId) == null) {
                        return null;
                    }
                } catch (RemoteException e) {
                }
                final long origId = Binder.clearCallingIdentity();
                checkTime(startTime, "getContentProviderImpl: incProviderCountLocked");

                // 增加引用计数
                conn = incProviderCountLocked(r, cpr, token, stable);
                if (conn != null && (conn.stableCount+conn.unstableCount) == 1) {
                    if (cpr.proc != null && r.setAdj <= ProcessList.PERCEPTIBLE_APP_ADJ) {
                        //更新LRU
                        checkTime(startTime, "getContentProviderImpl: before updateLruProcess");
                        updateLruProcessLocked(cpr.proc, false, null);
                        checkTime(startTime, "getContentProviderImpl: after updateLruProcess");
                    }
                }
                checkTime(startTime, "getContentProviderImpl: before updateOomAdj");
                final int verifiedAdj = cpr.proc.verifiedAdj;
                //更新进程adj
                boolean success = updateOomAdjLocked(cpr.proc, true);
                if (success && verifiedAdj != cpr.proc.setAdj && !isProcessAliveLocked(cpr.proc)) {
                    success = false;
                }
                maybeUpdateProviderUsageStatsLocked(r, cpr.info.packageName, name);
                checkTime(startTime, "getContentProviderImpl: after updateOomAdj");
                if (DEBUG_PROVIDER) Slog.i(TAG_PROVIDER, "Adjust success: " + success);
                //进程已经被杀
                if (!success) {
                    //确保自己不为同时杀掉
                    Slog.i(TAG, "Existing provider " + cpr.name.flattenToShortString()
                            + " is crashing; detaching " + r);
                    //减少引用计数
                    boolean lastRef = decProviderCountLocked(conn, cpr, token, stable);
                    checkTime(startTime, "getContentProviderImpl: before appDied");
                    appDiedLocked(cpr.proc);
                    checkTime(startTime, "getContentProviderImpl: after appDied");
                    if (!lastRef) {
                        // This wasn't the last ref our process had on
                        // the provider...  we have now been killed, bail.
                        return null;
                    }
                    providerRunning = false;
                    conn = null;
                } else {
                    cpr.proc.verifiedAdj = cpr.proc.setAdj;
                }
                Binder.restoreCallingIdentity(origId);
            }
```
关键的步骤都在上面有中文注释，值得注意的是如果减少了引用计数之后，**Client和ContentProider之间还有Connection**，会导致Client进程被杀掉，所以直接返回空。

**2.ContentProvider没有在运行**

```
            if (!providerRunning) {
                try {
                    checkTime(startTime, "getContentProviderImpl: before resolveContentProvider");
                    //先尝试从PackageManager中获取ContentProviderInfo
                    cpi = AppGlobals.getPackageManager().
                        resolveContentProvider(name,
                            STOCK_PM_FLAGS | PackageManager.GET_URI_PERMISSION_PATTERNS, userId);
                    checkTime(startTime, "getContentProviderImpl: after resolveContentProvider");
                } catch (RemoteException ex) {
                }
                if (cpi == null) {
                    return null;
                }
                boolean singleton = isSingleton(cpi.processName, cpi.applicationInfo,
                        cpi.name, cpi.flags)
                        && isValidSingletonCall(r.uid, cpi.applicationInfo.uid);
                if (singleton) {
                    userId = UserHandle.USER_SYSTEM;
                }
                cpi.applicationInfo = getAppInfoForUser(cpi.applicationInfo, userId);
                checkTime(startTime, "getContentProviderImpl: got app info for user");
                String msg;
                checkTime(startTime, "getContentProviderImpl: before checkContentProviderPermission");
                if ((msg = checkContentProviderPermissionLocked(cpi, r, userId, !singleton))
                        != null) {
                    throw new SecurityException(msg);
                }
                checkTime(startTime, "getContentProviderImpl: after checkContentProviderPermission");
                if (!mProcessesReady
                        && !cpi.processName.equals("system")) {
                    throw new IllegalArgumentException(
                            "Attempt to launch content provider before system ready");
                }
                //如果provider的提供用户没有在运行则直接返回。
                if (!mUserController.isUserRunningLocked(userId, 0)) {
                    Slog.w(TAG, "Unable to launch app "
                            + cpi.applicationInfo.packageName + "/"
                            + cpi.applicationInfo.uid + " for provider "
                            + name + ": user " + userId + " is stopped");
                    return null;
                }
                ComponentName comp = new ComponentName(cpi.packageName, cpi.name);
                checkTime(startTime, "getContentProviderImpl: before getProviderByClass");
                //尝试从通过包名从缓存中获取ContentProvider
                cpr = mProviderMap.getProviderByClass(comp, userId);
                checkTime(startTime, "getContentProviderImpl: after getProviderByClass");
                final boolean firstClass = cpr == null;
                //没有缓存在进入处理，尝试获取新的ContentProviderRecord.
                if (firstClass) {
                    final long ident = Binder.clearCallingIdentity();
                    if (mPermissionReviewRequired) {
                        if (!requestTargetProviderPermissionsReviewIfNeededLocked(cpi, r, userId)) {
                            return null;
                        }
                    }
                    try {
                        checkTime(startTime, "getContentProviderImpl: before getApplicationInfo");
                        //根据包名和userId尝试获取新的ApplicationInfo.
                        ApplicationInfo ai =
                            AppGlobals.getPackageManager().
                                getApplicationInfo(
                                        cpi.applicationInfo.packageName,
                                        STOCK_PM_FLAGS, userId);
                        checkTime(startTime, "getContentProviderImpl: after getApplicationInfo");
                        //安装包中获取不到对应的信息，直接返回
                        if (ai == null) {
                            Slog.w(TAG, "No package info for content provider "
                                    + cpi.name);
                            return null;
                        }
                        ai = getAppInfoForUser(ai, userId);
                        //封装在新的ContentProviderRecord
                        cpr = new ContentProviderRecord(this, cpi, ai, comp, singleton);
                    } catch (RemoteException ex) {
                        // pm is in same process, this will never happen.
                    } finally {
                        Binder.restoreCallingIdentity(ident);
                    }
                }
                checkTime(startTime, "getContentProviderImpl: now have ContentProviderRecord");
                if (r != null && cpr.canRunHere(r)) {
                    //如果ContentProvider声明了多进程运行或者处于同一个进程或者拥有同样的uid, 则直接返回。
                    return cpr.newHolder(null);
                }
                if (DEBUG_PROVIDER) Slog.w(TAG_PROVIDER, "LAUNCHING REMOTE PROVIDER (myuid "
                            + (r != null ? r.uid : null) + " pruid " + cpr.appInfo.uid + "): "
                            + cpr.info.name + " callers=" + Debug.getCallers(6));
                //如果该ContentProvider已经正在启动中，则i < 正在启动的ContentProvider数。
                final int N = mLaunchingProviders.size();
                int i;
                for (i = 0; i < N; i++) {
                    if (mLaunchingProviders.get(i) == cpr) {
                        break;
                    }
                }
                //没有在启动中，启动它。
                if (i >= N) {
                    final long origId = Binder.clearCallingIdentity();
                    try {
                        //设置包状态
                        try {
                            checkTime(startTime, "getContentProviderImpl: before set stopped state");
                            AppGlobals.getPackageManager().setPackageStoppedState(
                                    cpr.appInfo.packageName, false, userId);
                            checkTime(startTime, "getContentProviderImpl: after set stopped state");
                        } catch (RemoteException e) {
                        } catch (IllegalArgumentException e) {
                            Slog.w(TAG, "Failed trying to unstop package "
                                    + cpr.appInfo.packageName + ": " + e);
                        }
                        //获取对应的进程信息
                        checkTime(startTime, "getContentProviderImpl: looking for process record");
                        ProcessRecord proc = getProcessRecordLocked(
                                cpi.processName, cpr.appInfo.uid, false);

                        if (proc != null && proc.thread != null && !proc.killed) {
                            if (DEBUG_PROVIDER) Slog.d(TAG_PROVIDER,
                                    "Installing in existing process " + proc);
                            if (!proc.pubProviders.containsKey(cpi.name)) {
                                checkTime(startTime, "getContentProviderImpl: scheduling install");
                                //provider对应的进程已经启动，但是该provider还没有被publish
                                //因此发送消息触发publish provider的操作。
                                proc.pubProviders.put(cpi.name, cpr);
                                try {
                                    proc.thread.scheduleInstallProvider(cpi);
                                } catch (RemoteException e) {
                                }
                            }
                        } else {
                            //provider所在的进程没有启动，或者已经被杀
                            //启动provider对应的进程
                            checkTime(startTime, "getContentProviderImpl: before start process");
                            proc = startProcessLocked(cpi.processName,
                                    cpr.appInfo, false, 0, "content provider",
                                    new ComponentName(cpi.applicationInfo.packageName,
                                            cpi.name), false, false, false);
                            checkTime(startTime, "getContentProviderImpl: after start process");
                            if (proc == null) {
                                //无法启动，直接返回空
                                Slog.w(TAG, "Unable to launch app "
                                        + cpi.applicationInfo.packageName + "/"
                                        + cpi.applicationInfo.uid + " for provider "
                                        + name + ": process is bad");
                                return null;
                            }
                        }
                        //将ContentProviderRecord中保存的launchingApp设置为所在的进程，并将provider
                        //保存到正在启动的ContentProvider列表
                        cpr.launchingApp = proc;
                        mLaunchingProviders.add(cpr);
                    } finally {
                        Binder.restoreCallingIdentity(origId);
                    }
                }
                checkTime(startTime, "getContentProviderImpl: updating data structures");
                //保存新的ContentProviderRecord到mProviderMap中。
                if (firstClass) {
                    mProviderMap.putProviderByClass(comp, cpr);
                }
                mProviderMap.putProviderByName(name, cpr);
                //增加Client与ContentProvider之间的引用计数。
                conn = incProviderCountLocked(r, cpr, token, stable);
                if (conn != null) {
                    conn.waiting = true;
                }
            }
```
关键步骤在文中有注释，主要流程是:

+ 权限检查
+ 通过PM获取对应provider信息
+ 检查provider是否能够正常运行
+ 启动对应的进程，然后publish provider
+ 更新包状态、缓存、引用计数。

**3. 轮询确保provider被publish完成**

```
        synchronized (cpr) {
            while (cpr.provider == null) {
                //provider所在进程已经不存在则直接返回
                if (cpr.launchingApp == null) {
                    Slog.w(TAG, "Unable to launch app "
                            + cpi.applicationInfo.packageName + "/"
                            + cpi.applicationInfo.uid + " for provider "
                            + name + ": launching app became null");
                    EventLog.writeEvent(EventLogTags.AM_PROVIDER_LOST_PROCESS,
                            UserHandle.getUserId(cpi.applicationInfo.uid),
                            cpi.applicationInfo.packageName,
                            cpi.applicationInfo.uid, name);
                    return null;
                }
                try {
                    if (DEBUG_MU) Slog.v(TAG_MU,
                            "Waiting to start provider " + cpr
                            + " launchingApp=" + cpr.launchingApp);
                    //Client与ContentProvider之间有引用时才执行
                    if (conn != null) {
                        conn.waiting = true;
                    }
                    cpr.wait();
                } catch (InterruptedException ex) {
                } finally {
                    if (conn != null) {
                        conn.waiting = false;
                    }
                }
            }
        }
        return cpr != null ? cpr.newHolder(conn) : null;
```

