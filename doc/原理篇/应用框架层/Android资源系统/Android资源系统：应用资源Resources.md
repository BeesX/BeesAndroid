# Android资源系统：应用资源Resources

作者：[郭孝星](https://github.com/guoxiaoxing)

校对：[郭孝星](https://github.com/guoxiaoxing)

文章状态：编辑中

**关于项目**

> [BeesAndroid](https://github.com/BeesAndroid/BeesAndroid)项目旨在通过提供一系列的工具与方法，降低阅读Android系统源码的门槛，让更多的Android工程师理解
Android系统，掌握Android系统。

**文章目录**

- 一 Resources的创建流程

## 一 Resources的创建流程

Resources对象的创建序列图如下所示：

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/principle/app/resource/resource_create_sequence.png"/>

ResourcesImpl的构造方法如下所示：

```java
public class ResourcesImpl {
   public ResourcesImpl(@NonNull AssetManager assets, @Nullable DisplayMetrics metrics,
            @Nullable Configuration config, @NonNull DisplayAdjustments displayAdjustments) {
        mAssets = assets;
        mMetrics.setToDefaults();
        mDisplayAdjustments = displayAdjustments;
        updateConfiguration(config, metrics, displayAdjustments.getCompatibilityInfo());
        mAssets.ensureStringBlocks();
    }
}
```
在这个方法里有两个重要的函数：

- updateConfiguration(config, metrics, displayAdjustments.getCompatibilityInfo())：首先是根据参数config和metrics来更新设备的当前配置信息，例如，屏幕大小和密码、国家地区和语言、键盘
配置情况等，接着再调用成员变量mAssets所指向的一个Java层的AssetManager对象的成员函数setConfiguration来将这些配置信息设置到与之关联的C++层的AssetManger。
- ensureStringBlocks()：读取

我们重点来看看ensureStringBlocks()的实现。

```java
public final class AssetManager implements AutoCloseable {
    
    @NonNull
    final StringBlock[] ensureStringBlocks() {
        synchronized (this) {
            if (mStringBlocks == null) {
                //读取字符串资源池，sSystem.mStringBlocks表示系统资源索引表的字符串常量池
                //前面我们已经创建的了系统资源的AssetManger sSystem，所以系统资源字符串资源池已经读取完毕。
                makeStringBlocks(sSystem.mStringBlocks);
            }
            return mStringBlocks;
        }
    }

    //seed表示是否要将系统资源索引表里的字符串资源池也一起拷贝出来
    /*package*/ final void makeStringBlocks(StringBlock[] seed) {
        //系统资源索引表个数
        final int seedNum = (seed != null) ? seed.length : 0;
        //总的资源索引表个数
        final int num = getStringBlockCount();
        mStringBlocks = new StringBlock[num];
        if (localLOGV) Log.v(TAG, "Making string blocks for " + this
                + ": " + num);
        for (int i=0; i<num; i++) {
            if (i < seedNum) {
                //系统预加载资源的时候，已经解析过framework-res.apk中的resources.arsc
                mStringBlocks[i] = seed[i];
            } else {
                //调用getNativeStringBlock(i)方法读取字符串资源池
                mStringBlocks[i] = new StringBlock(getNativeStringBlock(i), true);
            }
        }
    }
    
    private native final int getStringBlockCount();
    private native final long getNativeStringBlock(int block);
}
```

首先解释一下什么是StringBlocks，StringBlocks描述的是一个字符串资源池，Android里每一个资源索引表resources.arsc都包含一个字符串资源池。 getStringBlockCount()
方法返回的也就是这种资源池的个数。

上面我们已经说了resources.arsc的文件格式，接下来就会调用native方法getNativeStringBlock()去解析resources.arsc文件的内容，获取字符串
常量池，getNativeStringBlock()方法实际上就是将每一个资源包里面的resources.arsc的数据项值的字符串资源池数据块读取出来，并封装在C++层的StringPool对象中，然后Java层的makeStringBlocks()方法
再将该StringPool对象封装成Java层的StringBlock中。

关于C++层的具体实现，可以参考罗哥的这两篇博客：

- [resources.arsc的数据格式](http://blog.csdn.net/luoshengyang/article/details/8744683)
- [resources.arsc的解析流程](http://blog.csdn.net/luoshengyang/article/details/8806798)

如此，AssetManager和Resources对象的创建流程便分析完了，这两个对象构成了Android应用程序资源管理器的核心基础，资源的加载就是借由这两个对象来完成的。


> 本篇文章到这里就结束了，欢迎关注我们的BeesAndroid微信公众平台，BeesAndroid致力于分享Android系统源码的设计与实现相关文章，也欢迎开源爱好者参与到BeesAndroid项目中来。

微信公众平台

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/wechat.png" width="300"/>