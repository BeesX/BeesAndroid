# Android视图系统：Android视图系统概述

作者：[郭孝星](https://github.com/guoxiaoxing)

校对：[郭孝星](https://github.com/guoxiaoxing)

文章状态：编辑中

**关于项目**

> [BeesAndroid](https://github.com/BeesAndroid/BeesAndroid)项目旨在通过提供一系列的工具与方法，降低阅读Android系统源码的门槛，让更多的Android工程师理解Android系统，掌握Android系统。

**文章目录**


Android的显示系统分为三层：

- UI框架层：负责管理窗口中View组件的布局与绘制以及响应用户输入事件
- WindowManagerService层：负责管理窗口Surface的布局与次序
- SurfaceFlinger层：将WindowManagerService管理的窗口按照一定的次序显示在屏幕上

在Android显示框架里有这么几个角色：

- Activity：应用视图的容器。
- Window：应用窗口的抽象表示，它的实际表现是View。
- View：实际显示的应用视图。
- WindowManagerService：用来创建、管理和销毁Window。

后续的分析思路是这样的，我们先分析最上层的View，然后依次是Window、WindowManagerService。这样可以由浅入深，便于理解。至于Activity我们会放在Android组件框架里分析。

> 本篇文章到这里就结束了，欢迎关注我们的BeesAndroid微信公众平台，BeesAndroid致力于分享Android系统源码的设计与实现相关文章，也欢迎开源爱好者参与到BeesAndroid项目中来。

微信公众平台

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/wechat.png" width="300"/>