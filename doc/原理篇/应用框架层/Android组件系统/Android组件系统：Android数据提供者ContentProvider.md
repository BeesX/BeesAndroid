# Android组件系统：Android后台服务Service

作者：[郭孝星](https://github.com/guoxiaoxing)

校对：[郭孝星](https://github.com/guoxiaoxing)

文章状态：编辑中

**关于项目**

> [BeesAndroid](https://github.com/BeesAndroid/BeesAndroid)项目旨在通过提供一系列的工具与方法，降低阅读Android系统源码的门槛，让更多的Android工程师理解
Android系统，掌握Android系统。

**文章目录**

Content Provider用于提供数据统一访问的格式，封装底层的具体实现，对于调用者而言，无需失效数据的来源，例如：数据库、文件或者
网络，只需要使用Content Provider提供的接口就可以进行数据的增删改查操作。

Content Provider作为Android四大组件之一，没有复杂的生命周期，只有简单的onCreate()过程。底层基于Binder实现，可以用来实现
跨进程数据交互与共享。

