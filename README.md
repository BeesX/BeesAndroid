# <img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/logo.png" alt="BeesAndroid" width="80" height="80" align="bottom"/> BeesAndroid

## 功能介绍

[![License](https://img.shields.io/github/issues/BeesAndroid/BeesAndroid.svg)](https://jitpack.io/#BeesAndroid/BeesAndroid) 
[![Stars](https://img.shields.io/github/stars/BeesAndroid/BeesAndroid.svg)](https://jitpack.io/#BeesAndroid/BeesAndroid) 
[![Stars](https://img.shields.io/github/forks/BeesAndroid/BeesAndroid.svg)](https://jitpack.io/#BeesAndroid/BeesAndroid) 
[![Forks](https://img.shields.io/github/issues/BeesAndroid/BeesAndroid.svg)](https://jitpack.io/#BeesAndroid/BeesAndroid) 

BeesAndroid开源技术小组正式成立啦，Bees，即蜜蜂，取义分享、合作与奉献的意思，这也是BeesAndroid小组的宗旨，我们第一个团体项目BeesAndroid也于2018年3月6日同步上线，该项目的前
身是[android-open-source-project-analysis](https://github.com/guoxiaoxing/android-open-source-project-analysis)，这个项目提供了一系列的Android系统源码分析文章，收
到了良好的反馈。

但是一个人的力量是有限的，因此将其推成团体项目BeesAndroid，项目采用GPL协议，在保护作者知识产权的基础上，最大化的做到开源与开放，如果有什么问题和意见欢迎提交issue，也欢迎大家参与
到本项目中来。

> BeesAndroid项目旨在通过提供一系列的工具与方法，降低阅读系统源码的门槛，帮助更多的Android工程师理解Andriod系统，掌握Android系统。

- [Blog](https://juejin.im/user/5a9e5b95518825558b3d6e22/posts)
- [GitBook](https://www.gitbook.com/book/beesandroid/beesandroid/welcome)

**代码版本**

- 细分版本：N6F26U	
- 分支：android-7.1.1_r28	
- 版本：Nougat	
- 支持设备：Nexus 6

**分析思路**

Android是一个庞大的系统，Android Framework只是对系统的一个封装，里面还牵扯到JNI、C++、Java虚拟机、Linux系统内核、指令集等。面对如此庞大的系统，我们得有一定的
章法去阅读源码，否则就会只见树木不见森林，陷入卷帙浩繁的细节与琐碎之中。

- 不要去记录那些API调用链，绘制一个序列图理清思路即可，Android Framework中有很多复杂的API调用链，你去关注这些东西，用处不大。你需要学会的是跟踪调用链和梳理流程的
技巧，思考一下作者是怎么找到关键入口的，核心的实现在什么地方。
- 要善于思考，要多问为什么，面对一个模块，你要去思考这个模块解决了什么问题，这个问题的本质是什么，为什么这么解决，如果让我来写，我会怎么设计。事实上不管是是计算机还是
手机，从CPU、到内存、到操作系统、到应用层，看似纷繁复杂，但问题的本质无非就是这么几种：时间片怎么分配？线程/进程怎么调度？通信的机制是什么？只是在不同的场景下加了具体
的优化，但问题的本质没有改变，我们要善于抓住本质。
- 要善于去粗存精，Android Framework也是人写的，有精华也有糟粕，并不是每行代码你都需要问个为什么，很多时候没有那么多为什么，只是当时那种情况下就那样设计了。但是
对于关键函数我们要去深究它的实现细节。

**写作风格**

和大家一样，笔者也是在前人的书籍和博客的基础上开始学习Android的底层实现的，站在前人的肩膀上会看的更远。但是这些书籍和博客有个问题在于，文章中罗列了大量的代码，这样
很容易把初学者带入到琐碎的细节之中，所以本系列文章在行文中更多的会以图文并茂和提纲总结的方式来分析问题，关键的地方才会去解析源码，力求让大家从宏观上理解Android的底
层实现。另外，基本上一个主题对应一篇文章，所以文章会比较长，但是文章会有详细的标题划分和提纲总结，可以有的放矢，阅读自己需要的内容。

好了，让我们开始我们的寻宝之旅吧~😆

**Android系统架构图**

Android系统架构图

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/android_system_structure.png"/>

从上到下依次分为六层：

- 应用框架层
- 进程通信层
- 系统服务层
- Android运行时层
- 硬件抽象层
- Linux内核层

在正式阅读本系列文章之前，请先阅读导读相关内容，这会帮助你更加快捷的理解文章内容。

- [导读](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/导读.md)

欢迎提交Issue与PR，为帮助我们更好的维护此项目，请先阅读wiki里的相关规范。

- [Wiki](https://github.com/BeesAndroid/BeesAndroid/wiki)

## 原理篇

### 应用框架层

**Android视图系统**

- Android视图系统：Android视图系统概述
- Android视图系统：Android应用视图管理者Window
- Android视图系统：Android应用视图载体View
- Android视图系统：Android应用视图容器ViewGroup
- Android视图系统：Android帧布局控件FrameLayout
- Android视图系统：Android线性布局控件LinearLayout
- Android视图系统：Android相对布局控件RelativeLayout
- Android视图系统：Android协调布局控件CoordinatorLayout
- Android视图系统：Android下拉刷新控件SwipeRefreshLayout
- Android视图系统：Android滑动控件ViewPager
- Android视图系统：Android滚动控件ScrollView
- Android视图系统：Android列表控件RecyclerView
- Android视图系统：Android进度条控件ProgressBar
- Android视图系统：Android图片控件ImageView
- Android视图系统：Android文字控件TextView
- Android视图系统：Android编辑框控件EditView
- Android视图系统：Android图片控件Dialog

**Android组件系统**

- Android组件系统：Android组件系统概述
- Android组件系统：Android视图容器Activity
- Android组件系统：Android视图片段Fragment
- Android组件系统：Android后台服务Service
- Android组件系统：Android内容提供者ContentProvider
- Android组件系统：Android广播接收者BroadcastReceiver
- Android组件系统：Android应用上下文Context

**Android资源系统**

- Android资源系统：Android资源系统概述
- Android资源系统：资源管理器AssetManager

**Android包系统**

- Android包系统：Android包系统概述
- Android包系统：APK的打包流程
- Android包系统：APK的安装流程
- Android包系统：APK的加载流程

**Android应用通信系统**

- Android应用通信系统：Android应用通信系统概述
- Android应用通信系统：Android线程通信桥梁Handler

### 进程通信层

**Binder进程通信框架**

- Binder进程通信框架：服务管理器ServiceManager
- Binder进程通信框架：BpBinder与BBinder
- Binder进程通信框架：Binder驱动

**Socket进程通信框架**

- Socket进程通信框架：Socket

### 系统服务层

**Android组件管理服务**

- Android组件管理服务：ActivityServiceManager

**Android窗口管理服务**

- Android窗口管理服务：WindowServiceManager

**Android图形绘制服务**

- Android图形绘制服务：SurfaceFlinger
- Android图形绘制服务：Android布局解析器LayoutInflater

**Android包管理服务**

- Android窗口管理服务：PackageServiceManager

**Android反馈管理服务**

- Android反馈管理服务：AccessibilityManagerService

**Android壁纸管理服务**

- Android壁纸管理服务：WallpaperManagerService

**Android搜索管理服务**

- Android搜索管理服务：SearchManagerService

**Android通知管理服务**

- Android通知管理服务：NotificationManagerService

**Android定位管理服务**

- Android定位管理服务：LocationManagerService

**Android输入法管理服务**

- Android输入法管理服务：InputMethodManagerService

**Android电源管理服务**

- Android电源管理服务：PowerManagerService

**Android闹钟管理服务**

- Android闹钟管理服务：AlarmManagerService

**Android网络管理服务**

- Android闹钟管理服务：NetworkManagementService

**Android账户管理服务**

- Android账户管理服务：AccountManagerService

**Android设备存储管理服务**

- Android设备存储管理服务：DeviceStorageMonitorService

**Android备份管理服务**

- Android备份管理服务：BackupManagerService

### Android运行时层

**JVM虚拟机**

- JVM虚拟机：Java类加载机制

**Android ART虚拟机**

- Android ART虚拟机：Android ART虚拟机概述

### 硬件抽象层

### Linux内核层

**Android进程框架**

- Android进程框架：进程的创建、启动与调度流程
- Android进程框架：线程与线程池

**Android内存框架**

- Android内存框架：内存管理系统
- Android内存框架：Ashmem匿名共享内存系统

## 实践篇

**Android界面开发**

- Android界面开发：View自定义实践概览
- Android界面开发：View自定义实践布局篇
- Android界面开发：View自定义实践绘制篇
- Android界面开发：View自定义实践交互篇

**Android应用优化**

- Android应用优化：优化概述
- Android应用优化：启动优化
- Android应用优化：界面优化
- Android应用优化：内存优化
- Android应用优化：图像优化
- Android应用优化：网络优化
- Android应用优化：并发优化
- Android应用优化：优化工具

**Android媒体开发**

- Android媒体开发：Bitmap实践指南
- Android媒体开发：Camera实践指南

**其他**

- Android混合编程：WebView实践
- Android网络编程：网络编程实践
- Android系统设计：软件设计原则
- Android系统设计：设计模式

> 欢迎关注我们的微信公众号，新文章会第一时间发布到掘金博客与微信公众平台，我们也有自己的交流群，下方是QQ交流群，微信群已满，可以加我微信 allenwells 邀请入群。

微信公众平台

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/wechat.png" width="300"/>

QQ交流群

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/qq.png" width="300"/>