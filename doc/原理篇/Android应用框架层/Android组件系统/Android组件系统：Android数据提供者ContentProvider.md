# Android组件系统：Android数据提供者ContentProvider

作者：[wusp](https://github.com/wusp)

校对：[郭孝星](https://github.com/guoxiaoxing)

文章状态：校对中

**关于项目**

> [BeesAndroid](https://github.com/BeesAndroid/BeesAndroid)项目旨在通过提供一系列的工具与方法，降低阅读Android系统源码的门槛，让更多的Android工程师理解
Android系统，掌握Android系统。

**文章目录**

Content Provider用于提供数据统一访问的格式，封装底层的具体实现，对于调用者而言，无需失效数据的来源，例如：数据库、文件或者
网络，只需要使用Content Provider提供的接口就可以进行数据的增删改查操作。

Content Provider作为Android四大组件之一，没有复杂的生命周期，只有简单的onCreate()过程。底层基于Binder实现，可以用来实现
跨进程数据交互与共享。

跟使用其他系统服务类似，APP对ContentProvider的使用可以分成三部分:

+ [Client APP如何从系统服务中获取到ContentProvider的调用接口](https://github.com/wusp/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F/contentprovider/ContentProvider1.md)
+ [ContentProvider如何将自己的发布到Framework当中](https://github.com/wusp/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F/contentprovider/ContentProvider2.md)
+ [Client APP调用ContentProvider接口的具体流程](https://github.com/wusp/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F/contentprovider/ContentProvider3.md)

