# Android资源系统：资源管理器AssetManager

作者：[郭孝星](https://github.com/guoxiaoxing)

校对：[郭孝星](https://github.com/guoxiaoxing)

文章状态：编辑中

**关于项目**

> [BeesAndroid](https://github.com/BeesAndroid/BeesAndroid)项目旨在通过提供一系列的工具与方法，降低阅读Android系统源码的门槛，让更多的Android工程师理解Android系统，掌握Android系统。

**文章目录**

- 一 AssetManager的创建流程

## 一 AssetManager的创建流程

我们知道每个启动的应用都需要先创建一个应用上下文Context，Context的实际实现类是ContextImpl，ContextImpl在创建的时候创建了Resources对象和AssetManager对象。

AssetManager对象创建序列图如下所示：

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/principle/app/package/asset_manager_create_sequence.png"/>

我们可以发现在整个流程AssetManager在Java和C++层都有一个实现，那么它们俩有什么关系呢？🤔

>事实上实际的功能都是由C++层的AssetManag来完成的。每个Java层的AssetManager对象都一个long型的成员变量mObject，用来保存C++层
AssetManager对象的地址，通过这个变量将Java层的AssetManager对象与C++层的AssetManager对象关联起来。

```java
public final class AssetManager implements AutoCloseable {
    // 通过这个变量将Java层的AssetManager对象与C++层的AssetManager对象关联起来。
    private long mObject;
}
```

从上述序列图中我们可以看出，最终调用Asset的构造函数来创建Asset对象，如下所示：

```java
public final class AssetManager implements AutoCloseable {
      public AssetManager() {
          synchronized (this) {
              if (DEBUG_REFS) {
                  mNumRefs = 0;
                  incRefsLocked(this.hashCode());
              }
              init(false);
              if (localLOGV) Log.v(TAG, "New asset manager: " + this);
              //创建系统的AssetManager
              ensureSystemAssets();
          }
      }
  
      private static void ensureSystemAssets() {
          synchronized (sSync) {
              if (sSystem == null) {
                  AssetManager system = new AssetManager(true);
                  system.makeStringBlocks(null);
                  sSystem = system;
              }
          }
      }
      
      private AssetManager(boolean isSystem) {
          if (DEBUG_REFS) {
              synchronized (this) {
                  mNumRefs = 0;
                  incRefsLocked(this.hashCode());
              }
          }
          init(true);
          if (localLOGV) Log.v(TAG, "New asset manager: " + this);
      }
      
    private native final void init(boolean isSystem);
}
```

可以看到构造函数会先调用native方法init()去构造初始化AssetManager对象，可以发现它还调用了ensureSystemAssets()方法去创建系统AssetManager，为什么还会有个系统AssetManager呢？🤔

>这是因为Android应用程序不仅要访问自己的资源，还需要访问系统的资源，系统的资源放在/system/framework/framework-res.apk文件中，它在应用进程中是通过一个单独的Resources对象（Resources.sSystem）
和一个单独的AssetManger（AssetManager.sSystem）对象来管理的。

我们接着来看native方法init()的实现，它实际上是调用android_util_AssetManager.cpp类的android_content_AssetManager_init()方法，如下所示；

👉 [android_util_AssetManager.cpp](https://android.googlesource.com/platform/frameworks/base.git/+/android-4.3_r2.1/core/jni/android_util_AssetManager.cpp)

```java
static void android_content_AssetManager_init(JNIEnv* env, jobject clazz, jboolean isSystem)
{
    if (isSystem) {
        verifySystemIdmaps();
    }
    //构建AssetManager对象
    AssetManager* am = new AssetManager();
    if (am == NULL) {
        jniThrowException(env, "java/lang/OutOfMemoryError", "");
        return;
    }

    //添加默认的资源路径，也就是系统资源的路径
    am->addDefaultAssets();

    ALOGV("Created AssetManager %p for Java object %p\n", am, clazz);
    env->SetLongField(clazz, gAssetManagerOffsets.mObject, reinterpret_cast<jlong>(am));
}
```
我们接着来看看AssetManger.cpp的ddDefaultAssets()方法。

👉 [AssetManager.cpp](https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/AssetManager.cpp)

```java
static const char* kSystemAssets = "framework/framework-res.apk";

bool AssetManager::addDefaultAssets()
{
    const char* root = getenv("ANDROID_ROOT");
    LOG_ALWAYS_FATAL_IF(root == NULL, "ANDROID_ROOT not set");

    String8 path(root);
    path.appendPath(kSystemAssets);

    return addAssetPath(path, NULL, false /* appAsLib */, true /* isSystemAsset */);
}
```
ANDROID_ROOT指的就是/sysetm目录，全局变量kSystemAssets指向的是"framework/framework-res.apk"，所以拼接以后就是我们前面说的系统资源的存放目录"/system/framework/framework-res.apk"

拼接好path后作为参数传入addAssetPath()方法，注意Java层的addAssetPath()方法实际调用的也是底层的此方法，如下所示：

```java

static const char* kAppZipName = NULL; //"classes.jar";

bool AssetManager::addAssetPath(
        const String8& path, int32_t* cookie, bool appAsLib, bool isSystemAsset)
{
    AutoMutex _l(mLock);

    asset_path ap;

    String8 realPath(path);
    //kAppZipName如果不为NULL，一般将会被设置为classes.jar
    if (kAppZipName) {
        realPath.appendPath(kAppZipName);
    }
    
    //检查传入的path是一个文件还是一个目录，两者都不是的时候直接返回
    ap.type = ::getFileType(realPath.string());
    if (ap.type == kFileTypeRegular) {
        ap.path = realPath;
    } else {
        ap.path = path;
        ap.type = ::getFileType(path.string());
        if (ap.type != kFileTypeDirectory && ap.type != kFileTypeRegular) {
            ALOGW("Asset path %s is neither a directory nor file (type=%d).",
                 path.string(), (int)ap.type);
            return false;
        }
    }

    //资源路径mAssetPaths是否已经添加过参数path描述的一个APK的文件路径，如果
    //已经添加过，则不再往下处理。直接将path保存在输出参数cookie中
    for (size_t i=0; i<mAssetPaths.size(); i++) {
        if (mAssetPaths[i].path == ap.path) {
            if (cookie) {
                *cookie = static_cast<int32_t>(i+1);
            }
            return true;
        }
    }

    ALOGV("In %p Asset %s path: %s", this,
         ap.type == kFileTypeDirectory ? "dir" : "zip", ap.path.string());

    ap.isSystemAsset = isSystemAsset;
    //path所描述的APK资源路径没有被添加过，则添加到mAssetPaths中。
    mAssetPaths.add(ap);

    //...

    return true;
```

该方法的实现也很简单，就是把path描述的APK资源路径加入到资源目录数组mAssetsPath中去，mAssetsPath是AssetManger.cpp的成员变量，AssetManger.cpp有三个
比较重要的成员变量：

- mAssetsPath：资源存放目录。
- mResources：资源索引表。
- mConfig：设备的本地配置信息，包括设备大小，国家地区、语音等配置信息。

有了这些变量AssetManger就可以正常的工作了。AssetManger对象也就创建完成了。

ResroucesManager的createResroucesImpl()方法会先调用createAssetManager()方法创建AssetManger对象，然后再调用ResourcesImpl的构造方法创建ResourcesImpl对象。


> 本篇文章到这里就结束了，欢迎关注我们的BeesAndroid微信公众平台，BeesAndroid致力于分享Android系统源码的设计与实现相关文章，也欢迎开源爱好者参与到BeesAndroid项目中来。

微信公众平台

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/wechat.png" width="300"/>