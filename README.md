# BeesAndroid


<a name="a7d80080"></a>
## 功能介绍

[![License](https://img.shields.io/github/issues/BeesX/BeesAndroid.svg)](https://jitpack.io/#BeesX/BeesAndroid)
[![Stars](https://img.shields.io/github/stars/BeesX/BeesAndroid.svg)](https://jitpack.io/#BeesX/BeesAndroid)
[![Stars](https://img.shields.io/github/forks/BeesX/BeesAndroid.svg)](https://jitpack.io/#BeesX/BeesAndroid)
[![Forks](https://img.shields.io/github/issues/BeesX/BeesAndroid.svg)](https://jitpack.io/#BeesX/BeesAndroid)


关于BeesX开源技术小组


> Bees（蜜蜂），取义蜜蜂精神求实、合作与奉献，也即BeesX开源技术小组的宗旨，求真务实，合作奉献，本小组旨在通过提供一系列的工具、方法与文档，帮助更多的无线开发工程师理解和掌握无线开发技术。


我们第一个团体项目BeesAndroid也于2018年3月6日同步上线，该项目的前身是[android-open-source-project-analysis](https://github.com/guoxiaoxing/android-open-source-project-analysis)，这个项目提供了一系列的Android系统源码分析文章，收 到了良好的反馈。但是一个人的力量是有限的，因此将其推成团体项目BeesAndroid，项目采用GPL协议，在保护作者知识产权的基础上，最大化的做到开源与开放，如果有什么问题和意见欢迎提交issue，也欢迎大家参与 到本项目中来。


> BeesAndroid项目旨在通过提供一系列的工具与方法，降低阅读系统源码的门槛，帮助更多的Android工程师理解Andriod系统，掌握Android系统。


- [掘金博客](https://juejin.im/user/5886d699128fe1006c455fb6)
- [在线书籍](https://www.yuque.com/beesx/beesandroid)

**代码版本**

- 细分版本：N6F26U
- 分支：android-7.1.1_r28
- 版本：Nougat
- 支持设备：Nexus 6

**分析思路**

Android是一个庞大的系统，Android Framework只是对系统的一个封装，里面还牵扯到JNI、C++、Java虚拟机、Linux系统内核、指令集等。面对如此庞大的系统，我们得有一定的 章法去阅读源码，否则就会只见树木不见森林，陷入卷帙浩繁的细节与琐碎之中。

- 不要去记录那些API调用链，绘制一个序列图理清思路即可，Android Framework中有很多复杂的API调用链，你去关注这些东西，用处不大。你需要学会的是跟踪调用链和梳理流程的 技巧，思考一下作者是怎么找到关键入口的，核心的实现在什么地方。
- 要善于思考，要多问为什么，面对一个模块，你要去思考这个模块解决了什么问题，这个问题的本质是什么，为什么这么解决，如果让我来写，我会怎么设计。事实上不管是是计算机还是 手机，从CPU、到内存、到操作系统、到应用层，看似纷繁复杂，但问题的本质无非就是这么几种：时间片怎么分配？线程/进程怎么调度？通信的机制是什么？只是在不同的场景下加了具体 的优化，但问题的本质没有改变，我们要善于抓住本质。
- 要善于去粗存精，Android Framework也是人写的，有精华也有糟粕，并不是每行代码你都需要问个为什么，很多时候没有那么多为什么，只是当时那种情况下就那样设计了。但是 对于关键函数我们要去深究它的实现细节。

在正式阅读本系列文章之前，请先阅读导读相关内容，这会帮助你更加快捷的理解文章内容

- [导读](https://www.yuque.com/beesx/beesandroid/uufhxo)

你也可以先阅读关于Android系统的概述，让你有Android系统的设计有个整体的把握。

- [Android系统设计概述](https://www.yuque.com/beesx/beesandroid/lk9rug)
- [Android系统设计原则与设计模式](https://www.yuque.com/beesx/beesandroid/vxai8u)

欢迎提交Issue与PR，为帮助我们更好的维护此项目，请先阅读wiki里的相关规范。

- [Wiki](https://github.com/BeesAndroid/BeesAndroid/wiki)

👉 注：每篇文章有三种状态：编辑中、校对中和已完成。请阅读已完成状态的文章。变更日志会记录每次文章变动的内容。

<a name="25eaa2da"></a>
## 原理篇


<a name="1b4593e9"></a>
### Android应用框架层

**Android视图系统**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| [Android视图系统：Android视图系统概述](https://www.yuque.com/beesx/beesandroid/bx3aos) | 已完成 | [苏策](https://github.com/sucese) |  |
| [Android视图系统：Android应用窗口Window](https://www.yuque.com/beesx/beesandroid/zcqcbu) | 已完成 | [苏策](https://github.com/sucese) |  |
| [Android视图系统：Android应用视图View](https://www.yuque.com/beesx/beesandroid/tgmglv) | 已完成 | [苏策](https://github.com/sucese) |  |
| Android视图系统：Android应用视图组ViewGroup | 编辑中 |  |  |
| Android视图系统：Android帧布局控件FrameLayout | 编辑中 |  |  |
| Android视图系统：Android线性布局控件LinearLayout | 编辑中 |  |  |
| Android视图系统：Android相对布局控件RelativeLayout | 编辑中 |  |  |
| Android视图系统：Android协调布局控件CoordinatorLayout | 编辑中 |  |  |
| Android视图系统：Android下拉刷新控件SwipeRefreshLayout | 编辑中 |  |  |
| Android视图系统：Android滑动控件ViewPager | 编辑中 |  |  |
| Android视图系统：Android滚动控件ScrollView | 编辑中 |  |  |
| Android视图系统：Android列表控件RecyclerView | 编辑中 |  |  |
| Android视图系统：Android进度条控件ProgressBar | 编辑中 |  |  |
| Android视图系统：Android文字控件TextView | 编辑中 |  |  |
| Android视图系统：Android编辑框控件EditText | 编辑中 |  |  |
| Android视图系统：Android图片控件ImageView | 编辑中 |  |  |
| Android视图系统：Android对话框控件Dialog | 编辑中 |  |  |
| Android视图系统：Android弹出窗口控件PopupWindow | 编辑中 |  |  |
| Android视图系统：Android浏览器控件WebView | 编辑中 |  |  |


**Android组件系统**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android组件系统：Android组件系统概述 | 编辑中 |  |  |
| Android组件系统：Android组件注册表AndroidManifest | 编辑中 |  |  |
| [Android组件系统：Android应用上下文Context](https://www.yuque.com/beesx/beesandroid/sfzs75) | 已完成 | [苏策](https://github.com/sucese) |  |
| [Android组件系统：Android视图容器Activity](https://www.yuque.com/beesx/beesandroid/abtzmx) | 已完成 | [苏策](https://github.com/sucese) |  |
| [Android组件系统：Android视图片段Fragment](https://www.yuque.com/beesx/beesandroid/endz1b) | 已完成 | [苏策](https://github.com/sucese) |  |
| [Android组件系统：Android后台服务Service](https://www.yuque.com/beesx/beesandroid/gvd3l9) | 已完成 | [苏策](https://github.com/sucese) |  |
| [Android组件系统：Android数据提供者ContentProvider](https://www.yuque.com/beesx/beesandroid/vbped6) | 已完成 | [wusp](https://github.com/wusp) |  |
| [Android组件系统：Android广播接收者BroadcastReceiver](https://www.yuque.com/beesx/beesandroid/ahbetr) | 已完成 | [苏策](https://github.com/sucese) |  |


**Android资源系统**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android资源系统：Android资源系统概述 | 编辑中 |  |  |
| Android资源系统：应用资源Resources | 编辑中 |  |  |
| Android资源系统：资源管理器AssetManager | 编辑中 |  |  |


**Android包系统**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android包系统：Android包系统概述 | 编辑中 |  |  |
| Android包系统：APK解析器PackageParser | 编辑中 |  |  |
| [Android包系统：APK的打包流程](https://www.yuque.com/beesx/beesandroid/qqqbuh) | 已完成 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |
| [Android包系统：APK的安装流程](https://www.yuque.com/beesx/beesandroid/auelxk) | 编辑中 |  |  |
| [Android包系统：APK的加载流程](https://www.yuque.com/beesx/beesandroid/gd7w9o) | 已完成 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |


**Android应用通信系统**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android应用通信系统：Android应用通信系统概述 | 编辑中 |  |  |
| [Android应用通信系统：线程通信的桥梁Handler](https://www.yuque.com/beesx/beesandroid/ewlw6g) | 编辑中 |  |  |
| Android应用通信系统：局域广播LocalBroadcastReceiver | 编辑中 |  |  |


**Android动画系统**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android动画系统：Android动画系统概述 | 编辑中 |  |  |
| Android动画系统：Android补间动画 | 编辑中 |  |  |
| Android动画系统：Android帧动画 | 编辑中 |  |  |
| Android动画系统：Android属性动画 | 编辑中 |  |  |


**Android多媒体系统**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android多媒体系统：Android多媒体系统概述 | 编辑中 |  |  |
| Android多媒体系统：实时预览与播放SurfaceView、GLSurfaceVIew、TextureView与SurfaceTexture | 编辑中 |  |  |
| Android多媒体系统：视频播放VideoView | 编辑中 |  |  |
| Android多媒体系统：相机Camera | 编辑中 |  |  |
| Android多媒体系统：相机Camera2 | 编辑中 |  |  |
| Android多媒体系统：音视频解码MediaCodec | 编辑中 |  |  |


**Android数据库系统**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android数据库系统：Android数据库系统概述 | 编辑中 |  |  |
| Android数据库系统：数据库SQLiteDatabase | 编辑中 |  |  |
| Android数据库系统：数据库连接SQLiteConnection | 编辑中 |  |  |
| Android数据库系统：数据库会话SQLiteSession | 编辑中 |  |  |
| Android数据库系统：数据库语句转换SQLiteProgram | 编辑中 |  |  |
| Android数据库系统：数据库游标Cursor | 编辑中 |  |  |


<a name="0ec0b0a6"></a>
### Java系统框架层

**Android组件管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android组件管理服务：Android组件管理服务概述 | 编辑中 |  |  |
| [Android组件管理服务：组件管理服务ActivityManagerService](https://www.yuque.com/beesx/beesandroid/pi7qpx) | 编辑中 | [苏策](https://github.com/sucese) |  |
| Android组件管理服务：应用主线程ActivityThread | 编辑中 |  |  |


**Android窗口管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android窗口管理服务：Android窗口管理服务概述 | 编辑中 |  |  |
| [Android窗口管理服务：窗口管理服务WindowManagerService](https://www.yuque.com/beesx/beesandroid/lsokoe) | 已完成 | [苏策](https://github.com/sucese) |  |
| Android窗口管理服务：窗口启动、显示与切换 | 编辑中 |  |  |
| [Android窗口管理服务：窗口大小与位置计算](https://www.yuque.com/beesx/beesandroid/mix281) | 已完成 | [苏策](https://github.com/sucese) |  |
| Android窗口管理服务：窗口动画 | 编辑中 |  |  |


**Android图形绘制服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android图形绘制服务：Android图形绘制服务概述 |  |  |  |
| Android图形绘制服务：SurfaceFlinger |  |  |  |
| Android图形绘制服务：Android布局解析器LayoutInflater |  |  |  |


**Android包管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android视图系统：Android视图系统概述 |  |  |  |
| Android窗口管理服务：PackageServiceManager |  |  |  |


**Android反馈管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android反馈管理服务：Android反馈管理服务概述 |  |  |  |
| Android反馈管理服务：AccessibilityManagerService |  |  |  |


**Android壁纸管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android壁纸管理服务：Android壁纸管理服务概述 |  |  |  |
| Android壁纸管理服务：WallpaperManagerService |  |  |  |


**Android搜索管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android搜索管理服务：Android搜索管理服务概述 |  |  |  |
| Android搜索管理服务：SearchManagerService |  |  |  |


**Android通知管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android搜索管理服务：Android搜索管理服务概述 |  |  |  |
| Android通知管理服务：NotificationManagerService |  |  |  |


**Android定位管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android定位管理服务：Android定位管理服务概述 |  |  |  |
| Android定位管理服务：LocationManagerService |  |  |  |


**Android输入法管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android视图系统：Android视图系统概述 |  |  |  |
| Android输入法管理服务：InputMethodManagerService |  |  |  |


**Android电源管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android电源管理服务：Android电源管理服务概述 |  |  |  |
| Android电源管理服务：PowerManagerService |  |  |  |


**Android闹钟管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android电源管理服务：Android电源管理服务概述 |  |  |  |
| Android闹钟管理服务：AlarmManagerService |  |  |  |


**Android网络管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android网络管理服务：Android网络管理服务概述 |  |  |  |
| Android网络管理服务：ConnectivityService |  |  |  |


**Android账户管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android账户管理服务：Android账户管理服务概述 |  |  |  |
| Android账户管理服务：AccountManagerService |  |  |  |


**Android设备存储管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android设备存储管理服务：Android设备存储管理服务概述 |  |  |  |
| Android设备存储管理服务：DeviceStorageMonitorService |  |  |  |


**Android备份管理服务**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Android备份管理服务：Android备份管理服务概述 |  |  |  |
| Android备份管理服务：BackupManagerService |  |  |  |




<a name="4a47097d"></a>
### C++系统框架层

**Binder进程通信系统**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| [Binder进程通信系统：Binder进程通信系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E5%B1%82/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%EF%BC%9ABinder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |
| [Binder进程通信系统：服务管理ServiceManager](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E5%B1%82/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%EF%BC%9A%E6%9C%8D%E5%8A%A1%E7%AE%A1%E7%90%86ServiceManager.md) | 编辑中 |  |  |
| [Binder进程通信系统：Binder线程池](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E5%B1%82/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%EF%BC%9ABinder%E7%BA%BF%E7%A8%8B%E6%B1%A0.md) | 编辑中 |  |  |
| [Binder进程通信系统：Binder驱动](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E5%B1%82/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%EF%BC%9ABinder%E9%A9%B1%E5%8A%A8.md) | 编辑中 |  |  |


**Socket进程通信系统**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| [Socket进程通信系统：Socket进程通信系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E5%B1%82/Socket%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F/Socket%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%EF%BC%9ASocket%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |

**Java虚拟机**

| 文章 | 文章状态 | 作者 | 变更日志 |
| --- | --- | --- | --- |
| Java虚拟机：Java虚拟机概述 | 编辑中 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |
| [Java虚拟机：类加载机制](https://www.yuque.com/beesx/beesandroid/ru5dzx) | 已完成 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |
| Java虚拟机：自动内存管理机制 | 编辑中 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |
| Java虚拟机：垃圾收集器与内存分配策略 | 编辑中 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |

**ART虚拟机**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| ART虚拟机：ART虚拟机概述 | 编辑中 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |
| ART虚拟机：OAT文件的加载流程 | 编辑中 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |
| ART虚拟机：类与方法的查找流程 | 编辑中 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |
| ART虚拟机：机器指令的查找流程 | 编辑中 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |
| ART虚拟机：垃圾收集 | 编辑中 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |


<a name="fe8277ee"></a>
### Linux内核层

**Android进程系统**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android进程系统：Android进程系统概述 | 编辑中 |  |  |
| [Android进程系统：进程的创建、启动与调度流程](https://www.yuque.com/beesx/beesandroid/zp3gzr) | 已完成 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |
| [Android进程系统：线程与线程池](https://www.yuque.com/beesx/beesandroid/vtub45) | 已完成 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |
| [Android进程系统：AIDL](https://www.yuque.com/beesx/beesandroid/illwd4) | 已完成 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |

**Android内存系统**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android内存系统：Android内存系统 |  |  |  |
| [Android内存系统：内存管理系统](https://www.yuque.com/beesx/beesandroid/uocvmf) | 已完成 | [苏策](https://github.com/sucese) | [苏策](https://github.com/sucese) |
| Android内存系统：Ashmem匿名共享内存系统 |  |  |  |

欢迎关注我们

![image.png](https://cdn.nlark.com/yuque/0/2019/png/279116/1551701953269-7532e5fc-1d67-4659-ba67-fe92ef3b2d3f.png#align=left&display=inline&height=800&name=image.png&originHeight=800&originWidth=2800&size=452660&status=done&width=2800#align=left&display=inline&height=800&originHeight=800&originWidth=2800&status=done&width=2800)
