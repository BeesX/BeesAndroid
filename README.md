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

👉注：文章整理中，如果想查阅过往文章可以参见[android-open-source-project-analysis](https://github.com/guoxiaoxing/android-open-source-project-analysis)。

在正式阅读本系列文章之前，请先阅读导读相关内容，这会帮助你更加快捷的理解文章内容。

- [导读](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/导读.md)

你也可以先阅读关于Android系统的概述，让你有Android系统的设计有个整体的把握。

- [Android系统设计概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/Android系统设计概述.md)
- [Android系统设计原则与设计模式](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/Android系统设计原则与设计模式.md)

欢迎提交Issue与PR，为帮助我们更好的维护此项目，请先阅读wiki里的相关规范。

- [Wiki](https://github.com/BeesAndroid/BeesAndroid/wiki)

👉 注：每篇文章有三种状态：编辑中、校对中和已完成。请阅读已完成状态的文章。

## 原理篇

### 应用框架层

**Android视图系统**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|[Android视图系统：Android视图系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android视图系统概述.md)| 编辑中|  |  |
|[Android视图系统：Android应用窗口Window](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android应用窗口Window.md)| 编辑中|  |  |
|[Android视图系统：Android应用视图View](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android应用视图View.md)| 编辑中|  |  |
|[Android视图系统：Android应用视图组ViewGroup](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android应用视图组ViewGroup.md)| 编辑中|  |  |
|[Android视图系统：Android帧布局控件FrameLayout](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android帧布局控件FrameLayout.md)| 编辑中|  |  |
|[Android视图系统：Android线性布局控件LinearLayout](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android线性布局控件LinearLayout.md)| 编辑中|  |  |
|[Android视图系统：Android相对布局控件RelativeLayout](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android相对布局控件RelativeLayout.md)| 编辑中|  |  |
|[Android视图系统：Android协调布局控件CoordinatorLayout](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android协调布局控件CoordinatorLayout.md)| 编辑中|  |  |
|[Android视图系统：Android下拉刷新控件SwipeRefreshLayout](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android下拉刷新控件SwipeRefreshLayout.md)| 编辑中|  |  |
|[Android视图系统：Android滑动控件ViewPager](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android滑动控件ViewPager.md)| 编辑中|  |  |
|[Android视图系统：Android滚动控件ScrollView](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android滚动控件ScrollView.md)| 编辑中|  |  |
|[Android视图系统：Android列表控件RecyclerView](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android列表控件RecyclerView.md)| 编辑中|  |  |
|[Android视图系统：Android进度条控件ProgressBar](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android进度条控件ProgressBar.md)| 编辑中|  |  |
|[Android视图系统：Android进度条控件ProgressBar](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android进度条控件ProgressBar.md)| 编辑中|  |  |
|[Android视图系统：Android文字控件TextView](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android文字控件TextView.md)| 编辑中|  |  |
|[Android视图系统：Android编辑框控件EditText](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android编辑框控件EditText.md)| 编辑中|  |  |
|[Android视图系统：Android图片控件ImageView](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android图片控件ImageView.md)| 编辑中|  |  |
|[Android视图系统：Android对话框控件Dialog](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android对话框控件Dialog.md)| 编辑中|  |  |
|[Android视图系统：Android弹出窗口控件PopupWindow](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android视图系统/Android视图系统：Android弹出窗口控件PopupWindow.md)| 编辑中|  |  |


**Android组件系统**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|[Android组件系统：Android组件系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android组件系统/Android组件系统：Android组件系统概述.md)| 编辑中|  |  |
|[Android组件系统：Android组件注册表AndroidManifest](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android组件系统/Android组件系统：Android组件注册表AndroidManifest.md)| 编辑中|  |  |
|[Android组件系统：Android应用上下文Context](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android组件系统/Android组件系统：Android应用上下文Context.md)| 编辑中|  |  |
|[Android组件系统：Android视图容器Activity](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android组件系统/Android组件系统：Android视图容器Activity.md)| 编辑中|  |  |
|[Android组件系统：Android视图片段Fragment](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android组件系统/Android组件系统：Android视图片段Fragment.md)| 编辑中|  |  |
|[Android组件系统：Android后台服务Service](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android组件系统/Android组件系统：Android后台服务Service.md)| 编辑中|  |  |
|[Android组件系统：Android数据提供者ContentProvider](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android组件系统/Android组件系统：Android数据提供者ContentProvider.md)| 校对中| [wusp](https://github.com/wusp) | [郭孝星](https://github.com/guoxiaoxing) |
|[Android组件系统：Android广播接收者BroadcastReceiver](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android组件系统/Android组件系统：Android广播接收者BroadcastReceiver.md)| 编辑中|  |  |

**Android资源系统**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|[Android资源系统：Android资源系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android资源系统/Android包系统：Android资源系统概述.md)| 编辑中|  |  |
|[Android资源系统：应用资源Resources](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android资源系统/Android包系统：应用资源Resources.md)| 编辑中|  |  |
|[Android资源系统：资源管理器AssetManager](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android资源系统/Android包系统：资源管理器AssetManager.md)| 编辑中|  |  |

**Android包系统**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|[Android包系统：Android包系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android包系统/Android包系统：Android资源系统概述.md)| 编辑中|  |  |
|[Android包系统：APK解析器PackageParser](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android包系统/Android包系统：APK解析器PackageParser.md)| 编辑中|  |  |
|[Android包系统：APK的打包流程](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android包系统/Android包系统：APK的打包流程.md)| 编辑中|  |  |
|[Android包系统：APK的安装流程](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android包系统/Android包系统：APK的安装流程.md)| 编辑中|  |  |
|[Android包系统：APK的加载流程](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android包系统/Android包系统：APK的加载流程.md)| 编辑中|  |  |

**Android应用通信系统**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|[Android应用通信系统：Android应用通信系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android应用通信系统/Android应用通信系统：Android应用通信系统概述.md)| 编辑中|  |  |
|[Android应用通信系统：线程通信的桥梁Handler](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android应用通信系统/Android应用通信系统：线程通信的桥梁Handler.md)| 编辑中|  |  |
|[Android应用通信系统：局域广播LocalBroadcastReceiver](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android应用通信系统/Android应用通信系统：局域广播LocalBroadcastReceiver.md)| 编辑中|  |  |

**Android动画系统**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android动画系统：Android动画系统概述| | |

**Android多媒体系统**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android多媒体系统：Android多媒体系统概述| | |

**Android数据库系统**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|[Android数据库系统：Android数据库系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/应用框架层/Android数据库系统/Android数据库系统：Android数据库系统概述.md)| 编辑中|  |  |

### 进程通信层

**Binder进程通信系统**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Binder进程通信系统：Binder进程通信系统概述| | | |
|Binder进程通信系统：服务管理器ServiceManager| | |
|Binder进程通信系统：BpBinder与BBinder| | |
|Binder进程通信系统：Binder驱动| | |

**Socket进程通信系统**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Socket进程通信系统：Socket进程通信系统概述| | | |

### 系统服务层

**Android组件管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android组件管理服务：Android组件管理服务概述| | | |
|Android组件管理服务：ActivityServiceManager| | |

**Android窗口管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android窗口管理服务：Android窗口管理服务概述| | | |
|Android窗口管理服务：WindowServiceManager| | |

**Android图形绘制服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android图形绘制服务：Android图形绘制服务概述| | | |
|Android图形绘制服务：SurfaceFlinger| | |
|Android图形绘制服务：Android布局解析器LayoutInflater| | |

**Android包管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android视图系统：Android视图系统概述| | | |
|Android窗口管理服务：PackageServiceManager| | |

**Android反馈管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android反馈管理服务：Android反馈管理服务概述| | | |
|Android反馈管理服务：AccessibilityManagerService| | |

**Android壁纸管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android壁纸管理服务：Android壁纸管理服务概述| | | |
|Android壁纸管理服务：WallpaperManagerService| | |

**Android搜索管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android搜索管理服务：Android搜索管理服务概述| | | |
|Android搜索管理服务：SearchManagerService| | |

**Android通知管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android搜索管理服务：Android搜索管理服务概述| | | |
|Android通知管理服务：NotificationManagerService| | |

**Android定位管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android定位管理服务：Android定位管理服务概述| | | |
|Android定位管理服务：LocationManagerService| | |

**Android输入法管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android视图系统：Android视图系统概述| | | |
|Android输入法管理服务：InputMethodManagerService| | |

**Android电源管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android电源管理服务：Android电源管理服务概述| | | |
|Android电源管理服务：PowerManagerService| | |

**Android闹钟管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android电源管理服务：Android电源管理服务概述| | | |
|Android闹钟管理服务：AlarmManagerService| | |

**Android网络管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android网络管理服务：Android网络管理服务概述| | | |
|Android网络管理服务：ConnectivityService| | |

**Android账户管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android账户管理服务：Android账户管理服务概述| | | |
|Android账户管理服务：AccountManagerService| | |

**Android设备存储管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android设备存储管理服务：Android设备存储管理服务概述| | | |
|Android设备存储管理服务：DeviceStorageMonitorService| | |

**Android备份管理服务**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android设备存储管理服务：Android设备存储管理服务概述| | | |
|Android备份管理服务：BackupManagerService| | |

### 运行时层

**JVM虚拟机**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|JVM虚拟机：JVM虚拟机概述| | | |
|JVM虚拟机：Java虚拟机概述| | |
|JVM虚拟机：Java类加载机制| | |

**ART虚拟机**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|ART虚拟机：ART虚拟机概述| | | |
|ART虚拟机：Android ART虚拟机概述| | |

### 硬件抽象层

### 内核层

**Android进程系统**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|[Android进程系统：Android进程系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/内核层/Android进程系统/Android进程系统：Android进程系统概述.md)| 编辑中|  |  |
|[Android进程系统：进程的创建、启动与调度流程](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/内核层/Android进程系统/Android进程系统：进程的创建、启动与调度流程.md)| 编辑中|  |  |
|[Android进程系统：线程与线程池](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/内核层/Android进程系统/Android进程系统：线程与线程池.md)| 编辑中|  |  |
|[Android进程系统：AIDL](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/原理篇/内核层/Android进程系统/Android进程系统：AIDL.md)| 编辑中|  |  |

**Android内存系统**

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android内存系统：Android内存系统| | | |
|Android内存系统：内存管理系统| | |
|Android内存系统：Ashmem匿名共享内存系统| | |

## 实践篇

### Android工程化实践

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|[Android工程化实践：Android工程化实践概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/工程化实践/Android工程化实践：Android工程化实践概述.md)| 编辑中|  |  |
|[Android工程化实践：项目架构](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/工程化实践/Android工程化实践：项目架构.md)| 编辑中|  |  |
|[Android工程化实践：编译系统](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/工程化实践/Android工程化实践：编译系统.md)| 编辑中|  |  |
|[Android工程化实践：插件化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/工程化实践/Android工程化实践：插件化.md)| 编辑中|  |  |
|[Android工程化实践：热修复](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/工程化实践/Android工程化实践：热修复.md)| 编辑中|  |  |
|[Android工程化实践：模块化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/工程化实践/Android工程化实践：模块化.md)| 已完成| [郭孝星](https://github.com/guoxiaoxing)  | [郭孝星](https://github.com/guoxiaoxing)  |
|[Android工程化实践：VCS工作流](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/工程化实践/Android工程化实践：VCS工作流.md)| 编辑中|  |  |
|[Android工程化实践：持续集成](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/工程化实践/Android工程化实践：持续集成.md)| 编辑中|  |  |
|[Android工程化实践：编码规范](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/工程化实践/Android工程化实践：编码规范.md)| 编辑中|  |  |
|[Android工程化实践：自动化测试](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/工程化实践/Android工程化实践：自动化测试.md)| 编辑中|  |  |
|[Android工程化实践：日志系统](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/工程化实践/Android工程化实践：日志系统.md)| 编辑中|  |  |
|[Android工程化实践：自动化埋点与数据采集](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/工程化实践/Android工程化实践：自动化埋点与数据采集.md)| 编辑中|  |  |

### Android应用优化

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|[Android应用优化：优化概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/应用优化/Android应用优化：优化概述.md)| 编辑中|  |  |
|[Android应用优化：启动优化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/应用优化/Android应用优化：启动优化.md)| 编辑中|  |  |
|[Android应用优化：界面优化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/应用优化/Android应用优化：界面优化.md)| 编辑中|  |  |
|[Android应用优化：内存优化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/应用优化/Android应用优化：内存优化.md)| 编辑中|  |  |
|[Android应用优化：图像优化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/应用优化/Android应用优化：图像优化.md)| 编辑中|  |  |
|[Android应用优化：网络优化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/应用优化/Android应用优化：网络优化.md)| 编辑中|  |  |
|[Android应用优化：并发优化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/应用优化/Android应用优化：并发优化.md)| 编辑中|  |  |
|[Android应用优化：优化工具](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/应用优化/Android应用优化：优化工具.md)| 编辑中|  |  |

### Android界面开发

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|[Android界面开发：View自定义概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/界面开发/Android界面开发：View自定义概述.md)| 编辑中|  |  |
|[Android界面开发：View自定义布局篇](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/界面开发/Android界面开发：View自定义布局篇.md)| 编辑中|  |  |
|[Android界面开发：View自定义绘制篇](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/界面开发/Android界面开发：View自定义绘制篇.md)| 编辑中|  |  |
|[Android界面开发：View自定义交互篇](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/界面开发/Android界面开发：View自定义交互篇.md)| 编辑中|  |  |

### Android多媒体开发

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|[Android多媒体开发：Bitmap实践指南](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/多媒体开发/Android多媒体开发：Bitmap实践指南.md)| 编辑中|  |  |
|[Android多媒体开发：Camera实践指南](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/实践篇/多媒体开发/Android多媒体开发：Camera实践指南.md)| 编辑中|  |  |

### Android网络编程

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android网络编程：网络编程实践| | |

### Android混合编程

|文章                                                      |文章状态                            |作者                               |校对                              |
|:--------------------------------------------------------|:----------------------------------|:----------------------------------|:--------------------------------|
|Android混合编程：WebView实践| | |

> 欢迎关注我们的微信公众号，新文章会第一时间发布到掘金博客与微信公众平台，我们也有自己的交流群，下方是QQ交流群，微信群已满，可以加我微信 allenwells 邀请入群。

微信公众平台

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/wechat.png" width="300"/>

QQ交流群

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/qq_2.png" width="300"/>
