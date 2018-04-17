# Android视图系统：Android视图系统概述

作者：[郭孝星](https://github.com/guoxiaoxing)

校对：[郭孝星](https://github.com/guoxiaoxing)

文章状态：已完成

**关于项目**

> [BeesAndroid](https://github.com/BeesAndroid/BeesAndroid)项目旨在通过提供一系列的工具与方法，降低阅读Android系统源码的门槛，让更多的Android工程师理解Android系统，掌握Android系统。

**文章目录**

Android的视图系统主要分为三层，即负责处理用户事件以及触发绘制的UI框架层，管理窗口的WindowManagerService层以及最终完成界面渲染的SurfaceFlinger层，如下所示：

- UI框架层：负责管理窗口中View组件的布局与绘制以及响应用户输入事件
- WindowManagerService层：负责管理窗口Surface的布局与次序
- SurfaceFlinger层：将WindowManagerService管理的窗口按照一定的次序显示在屏幕上

关于WindowManagerService层与SurfaceFlinger层我们后续会详细讲，今天我们先来看看与应用开发工程师关系密切的UI框架层。

一个与用户交互的窗口，它的标准结构如下所示：

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/principle/app/view/view_system_structure.png" width="300"/>

从上图可以看出UI框架层主要包含以下三个角色：

- Activity：应用视图的容器。
- Window：应用窗口的抽象表示，它的实际表现是View。Window是一个抽象类，它的实现类是PhoneWindow。
- ViewGroup/View：实际显示的应用视图。

> 本篇文章到这里就结束了，欢迎关注我们的BeesAndroid微信公众平台，BeesAndroid致力于分享Android系统源码的设计与实现相关文章，也欢迎开源爱好者参与到BeesAndroid项目中来。

微信公众平台

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/wechat.png" width="300"/>