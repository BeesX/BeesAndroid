# Android组件管理服务：应用主线程ActivityThread

作者：[郭孝星](https://github.com/guoxiaoxing)

校对：[郭孝星](https://github.com/guoxiaoxing)

文章状态：编辑中

**关于项目**

> [BeesAndroid](https://github.com/BeesAndroid/BeesAndroid)项目旨在通过提供一系列的工具与方法，降低阅读Android系统源码的门槛，让更多的Android工程师理解Android系统，掌握Android系统。

**文章目录**

>[ActivityThread](https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/app/ActivityThread.java)管理着应用进程里的主线程，负责Activity、Service、BroadcastReceiver的启动、切换、
以及销毁等操作。

### 2.1 ActivityThread启动流程

先来聊聊ActivityThread，这个类也厉害了😎，它就是我们app的入口，写过Java程序的同学都知道，Java程序的入口类都会有一个main()方法，ActivityThread也是这样，它的main()方法在新的应用
进程被创建后就会被调用，我们来看看这个main()方法实现了什么东西。

````java
public final class ActivityThread {
    
     public static void main(String[] args) {
         Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "ActivityThreadMain");
         SamplingProfilerIntegration.start();
 
         // CloseGuard defaults to true and can be quite spammy.  We
         // disable it here, but selectively enable it later (via
         // StrictMode) on debug builds, but using DropBox, not logs.
         CloseGuard.setEnabled(false);
 
         Environment.initForCurrentUser();
 
         // Set the reporter for event logging in libcore
         EventLogger.setReporter(new EventLoggingReporter());
 
         // Make sure TrustedCertificateStore looks in the right place for CA certificates
         final File configDir = Environment.getUserConfigDirectory(UserHandle.myUserId());
         TrustedCertificateStore.setDefaultUserDirectory(configDir);
 
         Process.setArgV0("<pre-initialized>");
         //主线程的looper
         Looper.prepareMainLooper();
         //创建ActivityThread实例
         ActivityThread thread = new ActivityThread();
         //调用attach()方法将ApplicationThread对象关联给AMS，以便AMS调用ApplicationThread里的方法，这同样也是一个IPC的过程。
         thread.attach(false);
 
         //主线程的Handler
         if (sMainThreadHandler == null) {
             sMainThreadHandler = thread.getHandler();
         }
 
         if (false) {
             Looper.myLooper().setMessageLogging(new
                     LogPrinter(Log.DEBUG, "ActivityThread"));
         }
 
         // End of event ActivityThreadMain.
         Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
         //开始消息循环
         Looper.loop();
 
         throw new RuntimeException("Main thread loop unexpectedly exited");
     }   
}
````
这里面还有关键的attach()方法，我们来看一下。

```java
public final class ActivityThread {
    
   private void attach(boolean system) {
        sCurrentActivityThread = this;
        //判断是否为系统进程，上面传过来的为false，表明它不是一个系统进程
        mSystemThread = system;
        //应用进程的处理流程
        if (!system) {
            ViewRootImpl.addFirstDrawHandler(new Runnable() {
                @Override
                public void run() {
                    ensureJitEnabled();
                }
            });
            android.ddm.DdmHandleAppName.setAppName("<pre-initialized>",
                                                    UserHandle.myUserId());
            RuntimeInit.setApplicationObject(mAppThread.asBinder());
            final IActivityManager mgr = ActivityManagerNative.getDefault();
            try {
                //将ApplicationThread对象关联给AMS，以便AMS调用ApplicationThread里的方法，这
                //同样也是一个IPC的过程。
                mgr.attachApplication(mAppThread);
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
            // Watch for getting close to heap limit.
            BinderInternal.addGcWatcher(new Runnable() {
                @Override public void run() {
                    if (!mSomeActivitiesChanged) {
                        return;
                    }
                    Runtime runtime = Runtime.getRuntime();
                    long dalvikMax = runtime.maxMemory();
                    long dalvikUsed = runtime.totalMemory() - runtime.freeMemory();
                    if (dalvikUsed > ((3*dalvikMax)/4)) {
                        if (DEBUG_MEMORY_TRIM) Slog.d(TAG, "Dalvik max=" + (dalvikMax/1024)
                                + " total=" + (runtime.totalMemory()/1024)
                                + " used=" + (dalvikUsed/1024));
                        mSomeActivitiesChanged = false;
                        try {
                            mgr.releaseSomeActivities(mAppThread);
                        } catch (RemoteException e) {
                            throw e.rethrowFromSystemServer();
                        }
                    }
                }
            });
        } 
        //系统进程的处理流程
        else {
            //初始化系统组件，例如：Instrumentation、ContextImpl、Application
            //系统进程的名称为system_process
            android.ddm.DdmHandleAppName.setAppName("system_process",
                    UserHandle.myUserId());
            try {
                //创建Instrumentation对象
                mInstrumentation = new Instrumentation();
                //创建ContextImpl对象
                ContextImpl context = ContextImpl.createAppContext(
                        this, getSystemContext().mPackageInfo);
                //创建Application对象
                mInitialApplication = context.mPackageInfo.makeApplication(true, null);
                //调用Application.onCreate()方法，这个方法我们非常熟悉了，我们经常在这里做一些初始化库的工作。
                mInitialApplication.onCreate();
            } catch (Exception e) {
                throw new RuntimeException(
                        "Unable to instantiate Application():" + e.toString(), e);
            }
        }

        // add dropbox logging to libcore
        DropBox.setReporter(new DropBoxReporter());
        
        //注册Configuration变化后的回调通知，当系统配置发生变化时，例如：语言切换，触发该回调。
        ViewRootImpl.addConfigCallback(new ComponentCallbacks2() {
            //配置发生变化
            @Override
            public void onConfigurationChanged(Configuration newConfig) {
                synchronized (mResourcesManager) {
                    // We need to apply this change to the resources
                    // immediately, because upon returning the view
                    // hierarchy will be informed about it.
                    if (mResourcesManager.applyConfigurationToResourcesLocked(newConfig, null)) {
                        updateLocaleListFromAppContext(mInitialApplication.getApplicationContext(),
                                mResourcesManager.getConfiguration().getLocales());

                        // This actually changed the resources!  Tell
                        // everyone about it.
                        if (mPendingConfiguration == null ||
                                mPendingConfiguration.isOtherSeqNewer(newConfig)) {
                            mPendingConfiguration = newConfig;

                            sendMessage(H.CONFIGURATION_CHANGED, newConfig);
                        }
                    }
                }
            }
            //低内存
            @Override
            public void onLowMemory() {
            }
            @Override
            public void onTrimMemory(int level) {
            }
        });
    }
}
```

从上面这两个方法我们可以看出ActivityThread主要做了两件事情：

- 创建并开启主线程的消息循环。
- 将ApplicationThread对象（Binder对象）关联给AMS，以便AMS调用ApplicationThread里的方法，这同样也是一个IPC的过程。

### 2.2 ActivityThread工作流程

ActivityThread工作流程图如下所示：

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/app/component/activity_thread_structure.png" />

通过前面的分析，ActivityThread的整个工作流程就非常明朗了。ActivityThread内部有个Binder对象ApplicationThread，AMS可以调用ApplicationThread里的方法，而
ApplicationThread里的方法利用mH（Handler）发送消息给ActivityThread里的消息队列，ActivityThread再去处理这些消息，进而完成诸如Activity启动等各种操作。

到这里我们已经把ActivityManager家族的主要框架都梳理完了，本篇文章并没有大篇幅的去分析源码，我们的重点是梳理整体框架，让大家有整体上的认识，至于具体的细节，可以根据自己的需要有的
放矢的去研究。这也是我们提倡的阅读Android源码的方法：不要揪着细节不放，要有整体意识。

理解了AMS的内容，后续就接着来分析Activity、Service、BroadcastReceiver的启动、切换和销毁等流程，分析的过程中也会结合着日常开发中经常遇到的一些问题，带着这些问题，我们去看看源
码里怎么写的，为什么会出现这些问题。应该如何去解决。



> 本篇文章到这里就结束了，欢迎关注我们的BeesAndroid微信公众平台，BeesAndroid致力于分享Android系统源码的设计与实现相关文章，也欢迎开源爱好者参与到BeesAndroid项目中来。

微信公众平台

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/wechat.png" width="300"/>