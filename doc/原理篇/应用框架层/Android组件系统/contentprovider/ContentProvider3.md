# ContentProvider系列3-使用ContentProvider接口
> 本篇基于Android Framework 7.1.1 源码

至此我们已经知道Server端的ContentProvider是如何发布到系统中，同时也知道Client是如何向系统拿到这些ContentProvider接口的。现在来看看在Client APP中使用这些ContentProvider的细节。
根据在Client APP中是否进行特殊的处理，可以将ContentProvider的接口使用分成两部分：

**1. 直接调用provider Binder IPC接口的**

+ getType
+ insert
+ bulkinsert
+ delete
+ update
+ call
+ applyBatch
+ canonicalize
+ uncanonicalize
+ getStreamTypes

对于这部分，我们只以**call**方法为例，看看流程上请求是如何从Client APP中去到ContentProvider中具体执行的。

**2. 在ContentResolver中有自己逻辑的**

+ query
+ openFile
+ openAssetFile
+ openTypedAssetFile

这一部分，我们会以**openAssetFile**为例子来进行解析，看看平时调用ContextResolver来打开asset文件时，到底做了哪些事情。

在下面阅读使用provider接口的源码中，你会经常看到 **unstable provider** 和 **stable provider** 两个字眼。最后我们会介绍一下这两者到底是什么，两者之间有什么区别。



### 1.直接调用provider Binder IPC接口

ContextResolver.java

```
    public final @Nullable Bundle call(@NonNull Uri uri, @NonNull String method,
            @Nullable String arg, @Nullable Bundle extras) {
        Preconditions.checkNotNull(uri, "uri");
        Preconditions.checkNotNull(method, "method");
        // 获取provider的Binder IPC接口
        IContentProvider provider = acquireProvider(uri);
        if (provider == null) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            // Binder IPC
            final Bundle res = provider.call(mPackageName, method, arg, extras);
            Bundle.setDefusable(res, true);
            return res;
        } catch (RemoteException e) {
            // Arbitrary and not worth documenting, as Activity
            // Manager will kill this process shortly anyway.
            return null;
        } finally {
            // acquire的时候会增加引用计数，现在要减少引用计数
            releaseProvider(provider);
        }
    }
```

主要步骤:

+ 获取provider接口，从第一篇文章中我们分析过，会先从Client APP的缓存中找，如果找不到则向AMS要。
+ 执行对应的Binder IPC接口。
+ 释放完成之后减少provider的引用计数。

### 2.拥有自己逻辑的调用

###### query
```
    public final @Nullable Cursor query(final @RequiresPermission.Read @NonNull Uri uri,
            @Nullable String[] projection, @Nullable String selection,
            @Nullable String[] selectionArgs, @Nullable String sortOrder,
            @Nullable CancellationSignal cancellationSignal) {
        Preconditions.checkNotNull(uri, "uri");
        // 1. 获取unstable provider
        IContentProvider unstableProvider = acquireUnstableProvider(uri);
        if (unstableProvider == null) {
            return null;
        }
        IContentProvider stableProvider = null;
        Cursor qCursor = null;
        try {
            long startTime = SystemClock.uptimeMillis();

            ICancellationSignal remoteCancellationSignal = null;
            if (cancellationSignal != null) {
                cancellationSignal.throwIfCanceled();
                remoteCancellationSignal = unstableProvider.createCancellationSignal();
                cancellationSignal.setRemote(remoteCancellationSignal);
            }
            // 2. 先使用 unstable provider 进行查询
            try {
                qCursor = unstableProvider.query(mPackageName, uri, projection,
                        selection, selectionArgs, sortOrder, remoteCancellationSignal);
            } catch (DeadObjectException e) {
                // The remote process has died...  but we only hold an unstable
                // reference though, so we might recover!!!  Let's try!!!!
                // This is exciting!!1!!1!!!!1
                unstableProviderDied(unstableProvider);
                // 3. unstable provider 查询出错，则获取 stable provider
                stableProvider = acquireProvider(uri);
                if (stableProvider == null) {
                    return null;
                }
                // 4. 使用 stable provider 再次尝试查询
                qCursor = stableProvider.query(mPackageName, uri, projection,
                        selection, selectionArgs, sortOrder, remoteCancellationSignal);
            }
            if (qCursor == null) {
                return null;
            }

            // Force query execution.  Might fail and throw a runtime exception here.
            // Cursor中有个CursorWindow，那里才是数据的真正所在，我们先访问看看能不能正常使用。
            qCursor.getCount();
            long durationMillis = SystemClock.uptimeMillis() - startTime;
            maybeLogQueryToEventLog(durationMillis, uri, projection, selection, sortOrder);

            // 将拿到的Cursor包装在装饰者类中返回。
            final IContentProvider provider = (stableProvider != null) ? stableProvider
                    : acquireProvider(uri);
            final CursorWrapperInner wrapper = new CursorWrapperInner(qCursor, provider);
            stableProvider = null;
            qCursor = null;
            return wrapper;
        } catch (RemoteException e) {
            // Arbitrary and not worth documenting, as Activity
            // Manager will kill this process shortly anyway.
            return null;
        } finally {
            // 异常时将CursorWindow中的资源释放掉。
            if (qCursor != null) {
                qCursor.close();
            }
            if (cancellationSignal != null) {
                cancellationSignal.setRemote(null);
            }
            // 释放provider的引用计数
            if (unstableProvider != null) {
                releaseUnstableProvider(unstableProvider);
            }
            if (stableProvider != null) {
                releaseProvider(stableProvider);
            }
        }
    }
```
核心步骤：

+ 先尝试使用 unstable provider 进行查询。provider 的获取跟其他方法一样，先从Client APP的缓存中获取，获取不到再从AMS中获取。
+ 如果 unstable provider 查询出错，则尝试使用 stable provider 进行查询。
+ 获取到的Cursor对象是包装在一个装饰者类中之后再返回给调用者。
+ 发生异常时需要将Cursor中的资源（一片内存区域）释放掉，还需要减少provider的引用计数。

###### openFile & openAssetFile & openTypedAssetFile
将三者放在一起讲的原因是 openFile 实际的执行逻辑在 openAssetFile 中，同时 openTypedAssetFile 的逻辑跟 openAssetFile 的逻辑类型。

```
    public final @Nullable AssetFileDescriptor openAssetFileDescriptor(@NonNull Uri uri,
            @NonNull String mode, @Nullable CancellationSignal cancellationSignal)
                    throws FileNotFoundException {
        Preconditions.checkNotNull(uri, "uri");
        Preconditions.checkNotNull(mode, "mode");

        String scheme = uri.getScheme();
        if (SCHEME_ANDROID_RESOURCE.equals(scheme)) {
            // android.source:....
            if (!"r".equals(mode)) {
                throw new FileNotFoundException("Can't write resources: " + uri);
            }
            OpenResourceIdResult r = getResourceId(uri);
            try {
                // 打开asset file, 并将操作符返回。
                return r.r.openRawResourceFd(r.id);
            } catch (Resources.NotFoundException ex) {
                throw new FileNotFoundException("Resource does not exist: " + uri);
            }
        } else if (SCHEME_FILE.equals(scheme)) {
            // 打开一个由Parcel包装的File, 然后包装在AssetFileDescriptor中返回。
            ParcelFileDescriptor pfd = ParcelFileDescriptor.open(
                    new File(uri.getPath()), ParcelFileDescriptor.parseMode(mode));
            return new AssetFileDescriptor(pfd, 0, -1);
        } else {
            // e.g. content://
            if ("r".equals(mode)) {
                // 只读的方式则直接打开
                return openTypedAssetFileDescriptor(uri, "*/*", null, cancellationSignal);
            } else {
                // 1. 获取 unstable provider
                IContentProvider unstableProvider = acquireUnstableProvider(uri);
                if (unstableProvider == null) {
                    throw new FileNotFoundException("No content provider: " + uri);
                }
                IContentProvider stableProvider = null;
                AssetFileDescriptor fd = null;

                try {
                    ICancellationSignal remoteCancellationSignal = null;
                    if (cancellationSignal != null) {
                        cancellationSignal.throwIfCanceled();
                        remoteCancellationSignal = unstableProvider.createCancellationSignal();
                        cancellationSignal.setRemote(remoteCancellationSignal);
                    }

                    try {
                        // 由ContentProvider打开asset file并将操作符返回
                        fd = unstableProvider.openAssetFile(
                                mPackageName, uri, mode, remoteCancellationSignal);
                        if (fd == null) {
                            // The provider will be released by the finally{} clause
                            return null;
                        }
                    } catch (DeadObjectException e) {
                        // The remote process has died...  but we only hold an unstable
                        // reference though, so we might recover!!!  Let's try!!!!
                        // This is exciting!!1!!1!!!!1
                        unstableProviderDied(unstableProvider);
                        stableProvider = acquireProvider(uri);
                        if (stableProvider == null) {
                            throw new FileNotFoundException("No content provider: " + uri);
                        }
                        // unstable provider 执行出错，则用 stable provider进行重试。
                        fd = stableProvider.openAssetFile(
                                mPackageName, uri, mode, remoteCancellationSignal);
                        if (fd == null) {
                            // The provider will be released by the finally{} clause
                            return null;
                        }
                    }

                    if (stableProvider == null) {
                        stableProvider = acquireProvider(uri);
                    }
                    // 释放 unstable provider 引用计数
                    releaseUnstableProvider(unstableProvider);
                    unstableProvider = null;
                    // 在Parcel 文件操作符中保存的provider接口，应该是stable的接口实例。
                    ParcelFileDescriptor pfd = new ParcelFileDescriptorInner(
                            fd.getParcelFileDescriptor(), stableProvider);

                    // Success!  Don't release the provider when exiting, let
                    // ParcelFileDescriptorInner do that when it is closed.
                    stableProvider = null;

                    return new AssetFileDescriptor(pfd, fd.getStartOffset(),
                            fd.getDeclaredLength());

                } catch (RemoteException e) {
                    // Whatever, whatever, we'll go away.
                    throw new FileNotFoundException(
                            "Failed opening content provider: " + uri);
                } catch (FileNotFoundException e) {
                    throw e;
                } finally {
                    if (cancellationSignal != null) {
                        cancellationSignal.setRemote(null);
                    }
                    if (stableProvider != null) {
                        releaseProvider(stableProvider);
                    }
                    if (unstableProvider != null) {
                        releaseUnstableProvider(unstableProvider);
                    }
                }
            }
        }
    }
```

+ 首先，如果Uri是 "android.source:" 和 "file:" 格式开头的话，ContextResolver会自己尝试打开这些文件，而不是交由ContentProvider来执行。 
+ 如果需要ContentProvider来完成文件的访问，如果是只读的话，将调用IContentProvider的openTypedAssetFile接口来执行，如果是使用可写方式来打开文件的话，将调用的是openAssetFile的接口。
+ 同query一样，在调用provider的接口时，会先尝试使用 unstable provider，如果不能正常使用，则获取 stable provider，进而调用 stable provider的接口重新执行。
+ 操作完成后释放provider的引用计数是老生常谈了。

### 3.unstable provider & stable provider

**unstable和stable的不同在于对provider引用计数的处理。而unstable、stable引用计数在执行provider清理工作时有很大的不同。**
 
下面来看看不同之处是怎么处理的。

在前面的文章中说过，ContextResolver中不同的acquireProvider，最终都会去到ActivityThread中的acquireProvider方法，区别在于对于stable provider，其方法参数stable为true；对于unstable provider，其方法参数stable为false。

```
    public final IContentProvider acquireProvider(
            Context c, String auth, int userId, boolean stable) {
        final IContentProvider provider = acquireExistingProvider(c, auth, userId, stable);
        if (provider != null) {
            return provider;
        }

        IActivityManager.ContentProviderHolder holder = null;
        try {
            holder = ActivityManagerNative.getDefault().getContentProvider(
                    getApplicationThread(), auth, userId, stable);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
        if (holder == null) {
            Slog.e(TAG, "Failed to find provider info for " + auth);
            return null;
        }

        // Install provider will increment the reference count for us, and break
        // any ties in the race.
        holder = installProvider(c, holder, holder.info,
                true /*noisy*/, holder.noReleaseNeeded, stable);
        return holder.provider;
    }
```

往下看看第一步中，是怎么从APP的缓存中获取provider IPC接口的。

###### 1.acquireExistingProvider
```
    public final IContentProvider acquireExistingProvider(
            Context c, String auth, int userId, boolean stable) {
        synchronized (mProviderMap) {
            final ProviderKey key = new ProviderKey(auth, userId);
            final ProviderClientRecord pr = mProviderMap.get(key);
            if (pr == null) {
                return null;
            }

            IContentProvider provider = pr.mProvider;
            IBinder jBinder = provider.asBinder();
            if (!jBinder.isBinderAlive()) {
                // The hosting process of the provider has died; we can't
                // use this one.
                Log.i(TAG, "Acquiring provider " + auth + " for user " + userId
                        + ": existing object's process dead");
                handleUnstableProviderDiedLocked(jBinder, true);
                return null;
            }

            // Only increment the ref count if we have one.  If we don't then the
            // provider is not reference counted and never needs to be released.
            ProviderRefCount prc = mProviderRefCountMap.get(jBinder);
            if (prc != null) {
                incProviderRefLocked(prc, stable);
            }
            return provider;
        }
    }
```

步骤：

+ 从接口缓存mProviderMap中查询接口实例缓存。
+ 如果该IPC接口对应的provider已经死亡，则进行清理，然后返回错误（NULL）。
+ 该接口还可以用，则从引用计数缓存mProviderRefCountMap中获取引用计数的统计器。
+ 通过引用计数统计器增加该provider的引用计数。

###### 2.incProviderRefLocked
```
    private final void incProviderRefLocked(ProviderRefCount prc, boolean stable) {
        if (stable) {
            prc.stableCount += 1;
            if (prc.stableCount == 1) {
                ...
                    ActivityManagerNative.getDefault().refContentProvider(
                            prc.holder.connection, 1, unstableDelta);
                ...
            }
        } else {
            prc.unstableCount += 1;
            if (prc.unstableCount == 1) {
                ...
                        ActivityManagerNative.getDefault().refContentProvider(
                                prc.holder.connection, 0, 1);
                ...
                }
            }
        }
    }
```

无论是 unstable 还是 stable，过程都非常的相似，无非就是让计数器的引用计数+1，当本APP开始持有某个provider连接时，通知AMS。如果是stable类型的连接，则传进去的stableDelta为1；如果是unstable类型的连接，则传进去的unstableDelta为1。

回想一下我们之前总是看到的 **releaseProvider**，也提到过该方法主要作用就是减少引用计数。releaseProvider其实就是跟incProviderRefLocked配对使用的。

```
    public final boolean releaseProvider(IContentProvider provider, boolean stable) {
        ...
            boolean lastRef = false;
            if (stable) {
                if (prc.stableCount == 0) {
                    if (DEBUG_PROVIDER) Slog.v(TAG,
                            "releaseProvider: stable ref count already 0, how?");
                    return false;
                }
                prc.stableCount -= 1;
                if (prc.stableCount == 0) {
                    // 如果同provider之间连接无论stable还是unstable都没有了，则准备移除
                    lastRef = prc.unstableCount == 0;
                    ...
                        ActivityManagerNative.getDefault().refContentProvider(
                                prc.holder.connection, -1, lastRef ? 1 : 0);
                    ...
                }
            } else {
                if (prc.unstableCount == 0) {
                    if (DEBUG_PROVIDER) Slog.v(TAG,
                            "releaseProvider: unstable ref count already 0, how?");
                    return false;
                }
                prc.unstableCount -= 1;
                if (prc.unstableCount == 0) {
                    // 同理也是一样，都是在完全没有连接后再移除。
                    lastRef = prc.stableCount == 0;
                    if (!lastRef) {
                        ...
                        // 减少的是unstable连接的引用计数，但是还有stable连接的存在。
                            ActivityManagerNative.getDefault().refContentProvider(
                                    prc.holder.connection, 0, -1);
                        ...
                    }
                }
            }

            if (lastRef) {
                if (!prc.removePending) {
                    ...
                    // schedule 移除 引用计数器
                    prc.removePending = true;
                    Message msg = mH.obtainMessage(H.REMOVE_PROVIDER, prc);
                    mH.sendMessage(msg);
                } else {
                    Slog.w(TAG, "Duplicate remove pending of provider " + prc.holder.info.name);
                }
            }
            return true;
        }
    }
```

由上面两段重要的代码，在四种临界情况会调用AMS的refContentProvider方法，同时传进不同的unstableDelta、stableDelta参数。

| 场景 | stableDelta | unstableDelta |
|:--------------------------------------------------------|:----------------------------------|:--------------------------------|
| 建立第一个stable连接 | 1 | 引用计数器如果正在移除中又重新拿到provider连接则为-1，否则为0 |
| 建立第一个unstable连接 | 0 | 1 |
| 释放最后一个stable连接 | -1 | 1: 如果没持有unstable连接; 0: 如果还持有unstable连接|
| 释放最后一个unstable连接且还存在stable连接时 | 0 | -1 |

在AMS中执行的refContentProvider也是进一步增加provider connection中的引用计数。
那记录的这些引用计数有什么用呢？请注意看下面这一小节，目标ContentProvider死亡后的清理。

###### 目标ContentProvider死亡

当ContentProvider所在的进程死亡后，会调用AMS中appDiedLocked方法，最近进入cleanUpApplicationRecordLocked中执行APP的清理工作。

只显示跟ContentProvider有关的部分

```
    private final boolean cleanUpApplicationRecordLocked(ProcessRecord app,
            boolean restarting, boolean allowRestart, int index, boolean replacingPid) {
        ...
        // Remove published content providers.
        for (int i = app.pubProviders.size() - 1; i >= 0; i--) {
            ContentProviderRecord cpr = app.pubProviders.valueAt(i);
            final boolean always = app.bad || !allowRestart;
            boolean inLaunching = removeDyingProviderLocked(app, cpr, always);
            if ((inLaunching || always) && cpr.hasConnectionOrHandle()) {
                // We left the provider in the launching list, need to
                // restart it.
                restart = true;
            }

            cpr.provider = null;
            cpr.proc = null;
        }
        app.pubProviders.clear();

        // Take care of any launching providers waiting for this process.
        if (cleanupAppInLaunchingProvidersLocked(app, false)) {
            restart = true;
        }

        // Unregister from connected content providers.
        if (!app.conProviders.isEmpty()) {
            for (int i = app.conProviders.size() - 1; i >= 0; i--) {
                ContentProviderConnection conn = app.conProviders.get(i);
                conn.provider.connections.remove(conn);
                stopAssociationLocked(app.uid, app.processName, conn.provider.uid,
                        conn.provider.name);
            }
            app.conProviders.clear();
        }

        ...    
    }
```

我们会发现系统使用removeDyingProviderLocked来执行已发布ContentProvider的清理工作

```
    private final boolean removeDyingProviderLocked(ProcessRecord proc,
            ContentProviderRecord cpr, boolean always) {
        final boolean inLaunching = mLaunchingProviders.contains(cpr);

        if (!inLaunching || always) {
            synchronized (cpr) {
                cpr.launchingApp = null;
                cpr.notifyAll();
            }
            // 1. 从provider缓存中移除
            mProviderMap.removeProviderByClass(cpr.name, UserHandle.getUserId(cpr.uid));
            String names[] = cpr.info.authority.split(";");
            for (int j = 0; j < names.length; j++) {
                mProviderMap.removeProviderByName(names[j], UserHandle.getUserId(cpr.uid));
            }
        }
        // 2.遍历该ContentProvider所有的connection
        for (int i = cpr.connections.size() - 1; i >= 0; i--) {
            ContentProviderConnection conn = cpr.connections.get(i);
            if (conn.waiting) {
                if (inLaunching && !always) {
                    continue;
                }
            }
            // 3.获取该connection上对应的Client进程
            ProcessRecord capp = conn.client;
            // 4.设置connection的标志位为已死亡
            conn.dead = true;
            if (conn.stableCount > 0) {
                // 5.该Client上还有stable连接，我们将其杀掉
                if (!capp.persistent && capp.thread != null
                        && capp.pid != 0
                        && capp.pid != MY_PID) {
                    capp.kill("depends on provider "
                            + cpr.name.flattenToShortString()
                            + " in dying proc " + (proc != null ? proc.processName : "??")
                            + " (adj " + (proc != null ? proc.setAdj : "??") + ")", true);
                }
            } else if (capp.thread != null && conn.provider.provider != null) {
                // 但是对于unstable连接，我们只是通知Client，unstable连接对应的provider已经死亡。
                try {
                    capp.thread.unstableProviderDied(conn.provider.provider.asBinder());
                } catch (RemoteException e) {
                }
                // In the protocol here, we don't expect the client to correctly
                // clean up this connection, we'll just remove it.
                cpr.connections.remove(i);
                if (conn.client.conProviders.remove(conn)) {
                    stopAssociationLocked(capp.uid, capp.processName, cpr.uid, cpr.name);
                }
            }
        }

        if (inLaunching && always) {
            mLaunchingProviders.remove(cpr);
        }
        return inLaunching;
    }
```
经过上面几个步骤，一定不难发现，**ContentProvider所在进程死亡时，如果系统发现还有其他的Client APP通过stable的方法同provider保持连接，那么就会将这些Client APP所在的进程杀掉！**


