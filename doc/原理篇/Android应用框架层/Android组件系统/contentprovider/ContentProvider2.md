# ContentProvider系列2-发布ContentProvider到Framework

> 本篇基于Android Framework 7.1.1 源码

由第一篇文章 **ContentProvider系列1-获取ContentProvider**，我们可以知道，可能有两种方式触发ContentProvider被发布到系统中:

+ **ContentProvider 所在进程未启动：** 先启动ContentProvider所在的进程，当系统通过一系列操作回调该进程的ActivityThread中handleBindApplication方法时，调用到ActivityThread的installContentProviders方法将该App内部注册的ContentProvider发布到系统。
+ **ContentProvider 所在进程已启动，但是ContentProvider未发布：** AMS调用该进程的ActivityThread中的scheduleInstallProvider方法来schedule触发到ActivityThread的installContentProviders方法将该App内部注册的ContentProvider发布到系统。

**无论哪种方式，最后都是通过installContentProviders方法将ContentProvider发布到系统中。因此，介绍ContentProvider发布流程的起点便是installContentProviders方法。**

从开始发布到将对应的记录添加进AMS，根据前后顺序可以分为以下重要的三步：

**1. installContentProviders** ActivityThread.java中，开始执行发布。
**2. installProvider** ActivityThread.java中，针对每一个provider进行在APP中的实例化、添加缓存等操作。
**3. publishContentProviders** ActivityManagerService.java中，完成最后的provider发布在系统中的具体操作。

#### 1. installContentProviders

```
    private void installContentProviders(
            Context context, List<ProviderInfo> providers) {
        final ArrayList<IActivityManager.ContentProviderHolder> results =
            new ArrayList<IActivityManager.ContentProviderHolder>();

        for (ProviderInfo cpi : providers) {
            if (DEBUG_PROVIDER) {
                StringBuilder buf = new StringBuilder(128);
                buf.append("Pub ");
                buf.append(cpi.authority);
                buf.append(": ");
                buf.append(cpi.name);
                Log.i(TAG, buf.toString());
            }
            // 创建CPH，installProvider在发布provider前或者获取provider后都会被调用。
            // 创建provider对应的实例和Binder接口，并根据诸如authority之类的key为缓存接口实例。
            // 对于外部的provider则主要是增加引用计数。
            // NOTE: 这里容易混淆的地方是对于发布provider的App来说，该provider就是'localProvider'。
            IActivityManager.ContentProviderHolder cph = installProvider(context, null, cpi,
                    false /*noisy*/, true /*noReleaseNeeded*/, true /*stable*/);
            if (cph != null) {
                cph.noReleaseNeeded = true;
                results.add(cph);
            }
        }

        try {
            // 通过ActivityManagerProxy将provider发布到AMS中.
            ActivityManagerNative.getDefault().publishContentProviders(
                getApplicationThread(), results);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }
```
核心步骤如下：

+ 创建ContentProvider的实例对象、Binder IPC接口、增加引用计数；
+ 创建ContentProvider的关键信息实体类ContentProviderHolder，里面包含了可以让系统找到具体的ContentProvider连接的信息。
+ 调用AMS的代理接口，通过Binder IPC将ContentProviderHolder发布到AMS中。

#### 2. installProvider
```
   private ContentProviderHolder installProvider(Context context,
            ContentProviderHolder holder, ProviderInfo info,
            boolean noisy, boolean noReleaseNeeded, boolean stable) {
        ContentProvider localProvider = null;
        IContentProvider provider;
        if (holder == null || holder.provider == null) {
            if (DEBUG_PROVIDER || noisy) {
                Slog.d(TAG, "Loading provider " + info.authority + ": "
                        + info.name);
            }
            Context c = null;
            ApplicationInfo ai = info.applicationInfo;
            //provider与caller在同一个包内，一般情况就是在本APP发布provider时
            if (context.getPackageName().equals(ai.packageName)) {
                c = context;
            } else if (mInitialApplication != null &&
                    mInitialApplication.getPackageName().equals(ai.packageName)) {
                c = mInitialApplication;
            } else {
                try {
                    c = context.createPackageContext(ai.packageName,
                            Context.CONTEXT_INCLUDE_CODE);
                } catch (PackageManager.NameNotFoundException e) {
                    // Ignore
                }
            }
            if (c == null) {
                Slog.w(TAG, "Unable to get context for package " +
                      ai.packageName +
                      " while loading content provider " +
                      info.name);
                return null;
            }
            if (info.splitName != null) {
                try {
                    c = c.createContextForSplit(info.splitName);
                } catch (NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            //创建provider实例对象
            try {
                final java.lang.ClassLoader cl = c.getClassLoader();
                localProvider = (ContentProvider)cl.
                    loadClass(info.name).newInstance();
                provider = localProvider.getIContentProvider();
                if (provider == null) {
                    Slog.e(TAG, "Failed to instantiate class " +
                          info.name + " from sourceDir " +
                          info.applicationInfo.sourceDir);
                    return null;
                }
                if (DEBUG_PROVIDER) Slog.v(
                    TAG, "Instantiating local provider " + info.name);
                // XXX Need to create the correct context for this provider.
                //创建ContentProvider的Context
                localProvider.attachInfo(c, info);
            } catch (java.lang.Exception e) {
                if (!mInstrumentation.onException(null, e)) {
                    throw new RuntimeException(
                            "Unable to get provider " + info.name
                            + ": " + e.toString(), e);
                }
                return null;
            }
        } else {
            //来自本App外的ContentProvider
            provider = holder.provider;
            if (DEBUG_PROVIDER) Slog.v(TAG, "Installing external provider " + info.authority + ": "
                    + info.name);
        }
        ContentProviderHolder retHolder;
        synchronized (mProviderMap) {
            if (DEBUG_PROVIDER) Slog.v(TAG, "Checking to add " + provider
                    + " / " + info.name);
            IBinder jBinder = provider.asBinder();
            if (localProvider != null) {
                //本地provider分支
                ComponentName cname = new ComponentName(info.packageName, info.name);
                ProviderClientRecord pr = mLocalProvidersByName.get(cname);
                if (pr != null) {
                    //缓存中已经存在本地provider实例。
                    if (DEBUG_PROVIDER) {
                        Slog.v(TAG, "installProvider: lost the race, "
                                + "using existing local provider");
                    }
                    provider = pr.mProvider;
                } else {
                    //不存在，创建新的Client provider record并install，
                    //然后保存到本地的缓存中
                    holder = new ContentProviderHolder(info);
                    holder.provider = provider;
                    holder.noReleaseNeeded = true;
                    pr = installProviderAuthoritiesLocked(provider, localProvider, holder);
                    mLocalProviders.put(jBinder, pr);
                    mLocalProvidersByName.put(cname, pr);
                }
                //返回值为install过的本地Content Provider Holder
                retHolder = pr.mHolder;
            } else {
              // 外部provider，从别的进程获取到的。
                ProviderRefCount prc = mProviderRefCountMap.get(jBinder);
                if (prc != null) {
                    //不为空，说明引用缓存中已经保存有该provider的引用计数。
                    if (DEBUG_PROVIDER) {
                        Slog.v(TAG, "installProvider: lost the race, updating ref count");
                    }
                    //外部provider则增加引用计数。
                    if (!noReleaseNeeded) {
                        incProviderRefLocked(prc, stable);
                        try {
                            ActivityManager.getService().removeContentProvider(
                                    holder.connection, stable);
                        } catch (RemoteException e) {
                            //do nothing content provider object is dead any way
                        }
                    }
                } else {
                  // 将provider根据authority保存到缓存当中，便于直接查询。
                  // 对于外部的provider, provider的Binder接口不为空
                    ProviderClientRecord client = installProviderAuthoritiesLocked(
                            provider, localProvider, holder);
                    if (noReleaseNeeded) {
                        //本地provider或者来自系统的provider
                        prc = new ProviderRefCount(holder, client, 1000, 1000);
                    } else {
                        //其他App的provider
                        prc = stable
                                ? new ProviderRefCount(holder, client, 1, 0)
                                : new ProviderRefCount(holder, client, 0, 1);
                    }
                    //在本App的缓存中保存provider的引用计数。
                    mProviderRefCountMap.put(jBinder, prc);
                }
                retHolder = prc.holder;
            }
        }
        return retHolder;
    }
```
核心步骤：

+ 赋值或创建Context。
+ 如果是本地ContentProvider（即自己发布时），需要创建provider的实例。
+ 来自外部的ContentProvider要增加计数。
+ 将ProviderClientRecord (里面保存着IContentProvider的IPC接口和provider实例对象) 保存到缓存中以便后面使用。来自APP自己的本地provider保存到mLocalProviders当中；来自己外部的provider保存到mProviderRefCountMap当中。

#### 3. publishContentProviders
```
    public final void publishContentProviders(IApplicationThread caller,
            List<ContentProviderHolder> providers) {
        if (providers == null) {
            return;
        }

        enforceNotIsolatedCaller("publishContentProviders");
        // AMS的大锁
        synchronized (this) {
            final ProcessRecord r = getRecordForAppLocked(caller);
            if (DEBUG_MU) Slog.v(TAG_MU, "ProcessRecord uid = " + r.uid);
            // 确保对应的进程存在
            if (r == null) {
                throw new SecurityException(
                        "Unable to find app for caller " + caller
                      + " (pid=" + Binder.getCallingPid()
                      + ") when publishing content providers");
            }

            final long origId = Binder.clearCallingIdentity();

            final int N = providers.size();
            for (int i = 0; i < N; i++) {
                ContentProviderHolder src = providers.get(i);
                if (src == null || src.info == null || src.provider == null) {
                    continue;
                }
                ContentProviderRecord dst = r.pubProviders.get(src.info.name);
                if (DEBUG_MU) Slog.v(TAG_MU, "ContentProviderRecord uid = " + dst.uid);
                if (dst != null) {
                    ComponentName comp = new ComponentName(dst.info.packageName, dst.info.name);
                    // 根据不同的包名和provider名来区分，并将ContentProviderRecord记录添加进AMS中的ContentProvider记录缓存。
                    mProviderMap.putProviderByClass(comp, dst);
                    String names[] = dst.info.authority.split(";");
                    for (int j = 0; j < names.length; j++) {
                        // 根据authority来保存ContentProviderRecord
                        mProviderMap.putProviderByName(names[j], dst);
                    }

                    int launchingCount = mLaunchingProviders.size();
                    int j;
                    boolean wasInLaunchingProviders = false;
                    // 从正在等待发布的ContentProvider队列记录中移除记录。
                    // 从上一篇我们知道，在AMS尝试获取未发布的ContentProvider时会将ContentProviderRecord
                    // 添加进mLaunchingProviders.
                    for (j = 0; j < launchingCount; j++) {
                        if (mLaunchingProviders.get(j) == dst) {
                            mLaunchingProviders.remove(j);
                            wasInLaunchingProviders = true;
                            j--;
                            launchingCount--;
                        }
                    }
                    // 移除该进程对应的ContentProvider发布超时器，如果进程发布ContentProvider超过10s没有完成则触发ANR .
                    if (wasInLaunchingProviders) {
                        mHandler.removeMessages(CONTENT_PROVIDER_PUBLISH_TIMEOUT_MSG, r);
                    }
                    // 也许该ContentProvider正在被AMS中另一个Binder线程阻塞等待获取，我们notify一下。
                    synchronized (dst) {
                        dst.provider = src.provider;
                        dst.proc = r;
                        dst.notifyAll();
                    }
                    updateOomAdjLocked(r);
                    // 完成Component Usage的统计记录更新。
                    maybeUpdateProviderUsageStatsLocked(r, src.info.packageName,
                            src.info.authority);
                }
            }

            Binder.restoreCallingIdentity(origId);
        }
    }
```
核心步骤如下：

+ 将ContentProviderRecord添加进缓存中。
+ 将该ContentProvider对应的记录从 **正等待发布的ContentProvider队列mLaunchingProviders** 中移除。
+ 移除该App对应的ContentProvider发布超时器，避免触发该App的ANR。
+ notify唤醒正在阻塞等待该ContentProvider发布的system_server binder线程。


