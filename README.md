# BeesAndroid

<a name="a7d80080"></a>
## 功能介绍

[![](https://cdn.nlark.com/yuque/0/2019/svg/279116/1551591113511-42045739-0c92-4100-a450-87fcae03f7d3.svg#align=left&display=inline&height=20&originHeight=20&originWidth=134&size=0&status=done&width=134)](https://jitpack.io/#BeesAndroid/BeesAndroid) [![](https://cdn.nlark.com/yuque/0/2019/svg/279116/1551591112173-5f54de88-7384-46dc-bf16-900862ffd51f.svg#align=left&display=inline&height=20&originHeight=20&originWidth=60&size=0&status=done&width=60)](https://jitpack.io/#BeesAndroid/BeesAndroid) [![](https://cdn.nlark.com/yuque/0/2019/svg/279116/1551591112118-36ef7b09-590f-4d9a-b14c-3c5c11df63ba.svg#align=left&display=inline&height=20&originHeight=20&originWidth=68&size=0&status=done&width=68)](https://jitpack.io/#BeesAndroid/BeesAndroid) [![](https://cdn.nlark.com/yuque/0/2019/svg/279116/1551591111611-d3912ef7-1020-43ba-849c-41022a21a5c1.svg#align=left&display=inline&height=20&originHeight=20&originWidth=134&size=0&status=done&width=134)](https://jitpack.io/#BeesAndroid/BeesAndroid)<br />

关于BeesX开源技术小组

> Bees（蜜蜂），取义蜜蜂精神求实、合作与奉献，也即BeesX开源技术小组的宗旨，求真务实，合作奉献，本小组旨在通过提供一系列的工具、方法与文档，帮助更多的无线开发工程师理解和掌握无线开发技术。

我们第一个团体项目BeesAndroid也于2018年3月6日同步上线，该项目的前身是[android-open-source-project-analysis](https://github.com/guoxiaoxing/android-open-source-project-analysis)，这个项目提供了一系列的Android系统源码分析文章，收 到了良好的反馈。<br />但是一个人的力量是有限的，因此将其推成团体项目BeesAndroid，项目采用GPL协议，在保护作者知识产权的基础上，最大化的做到开源与开放，如果有什么问题和意见欢迎提交issue，也欢迎大家参与 到本项目中来。

> BeesAndroid项目旨在通过提供一系列的工具与方法，降低阅读系统源码的门槛，帮助更多的Android工程师理解Andriod系统，掌握Android系统。

* [掘金博客](https://juejin.im/user/5886d699128fe1006c455fb6)
* [在线书籍](https://www.yuque.com/beesx/beesandroid)

**代码版本**

* 细分版本：N6F26U
* 分支：android-7.1.1_r28
* 版本：Nougat
* 支持设备：Nexus 6

**分析思路**<br />Android是一个庞大的系统，Android Framework只是对系统的一个封装，里面还牵扯到JNI、C++、Java虚拟机、Linux系统内核、指令集等。面对如此庞大的系统，我们得有一定的 章法去阅读源码，否则就会只见树木不见森林，陷入卷帙浩繁的细节与琐碎之中。
* 不要去记录那些API调用链，绘制一个序列图理清思路即可，Android Framework中有很多复杂的API调用链，你去关注这些东西，用处不大。你需要学会的是跟踪调用链和梳理流程的 技巧，思考一下作者是怎么找到关键入口的，核心的实现在什么地方。
* 要善于思考，要多问为什么，面对一个模块，你要去思考这个模块解决了什么问题，这个问题的本质是什么，为什么这么解决，如果让我来写，我会怎么设计。事实上不管是是计算机还是 手机，从CPU、到内存、到操作系统、到应用层，看似纷繁复杂，但问题的本质无非就是这么几种：时间片怎么分配？线程/进程怎么调度？通信的机制是什么？只是在不同的场景下加了具体 的优化，但问题的本质没有改变，我们要善于抓住本质。
* 要善于去粗存精，Android Framework也是人写的，有精华也有糟粕，并不是每行代码你都需要问个为什么，很多时候没有那么多为什么，只是当时那种情况下就那样设计了。但是 对于关键函数我们要去深究它的实现细节。

在正式阅读本系列文章之前，请先阅读导读相关内容，这会帮助你更加快捷的理解文章内容。
* [导读](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md)

你也可以先阅读关于Android系统的概述，让你有Android系统的设计有个整体的把握。
* [Android系统设计概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/Android%E7%B3%BB%E7%BB%9F%E8%AE%BE%E8%AE%A1%E6%A6%82%E8%BF%B0.md)
* [Android系统设计原则与设计模式](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/Android%E7%B3%BB%E7%BB%9F%E8%AE%BE%E8%AE%A1%E5%8E%9F%E5%88%99%E4%B8%8E%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F.md)

欢迎提交Issue与PR，为帮助我们更好的维护此项目，请先阅读wiki里的相关规范。
* [Wiki](https://github.com/BeesAndroid/BeesAndroid/wiki)

👉 注：每篇文章有三种状态：编辑中、校对中和已完成。请阅读已完成状态的文章。
<a name="25eaa2da"></a>
## [](https://github.com/BeesX/BeesAndroid/blob/c853ac17efaf355064ded9f157ab9980bce6a086/README.md#%E5%8E%9F%E7%90%86%E7%AF%87)原理篇
<a name="1b4593e9"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/c853ac17efaf355064ded9f157ab9980bce6a086/README.md#android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82)Android应用框架层
**Android视图系统**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android视图系统：Android视图系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%E6%A6%82%E8%BF%B0.md) | 已完成 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Android视图系统：Android应用窗口Window](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%BA%94%E7%94%A8%E7%AA%97%E5%8F%A3Window.md) | 已完成 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Android视图系统：Android应用视图View](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%BA%94%E7%94%A8%E8%A7%86%E5%9B%BEView.md) | 已完成 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Android视图系统：Android应用视图组ViewGroup](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%BA%94%E7%94%A8%E8%A7%86%E5%9B%BE%E7%BB%84ViewGroup.md) | 编辑中 |  |  |
| [Android视图系统：Android帧布局控件FrameLayout](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%B8%A7%E5%B8%83%E5%B1%80%E6%8E%A7%E4%BB%B6FrameLayout.md) | 编辑中 |  |  |
| [Android视图系统：Android线性布局控件LinearLayout](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E7%BA%BF%E6%80%A7%E5%B8%83%E5%B1%80%E6%8E%A7%E4%BB%B6LinearLayout.md) | 编辑中 |  |  |
| [Android视图系统：Android相对布局控件RelativeLayout](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E7%9B%B8%E5%AF%B9%E5%B8%83%E5%B1%80%E6%8E%A7%E4%BB%B6RelativeLayout.md) | 编辑中 |  |  |
| [Android视图系统：Android协调布局控件CoordinatorLayout](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%8D%8F%E8%B0%83%E5%B8%83%E5%B1%80%E6%8E%A7%E4%BB%B6CoordinatorLayout.md) | 编辑中 |  |  |
| [Android视图系统：Android下拉刷新控件SwipeRefreshLayout](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E4%B8%8B%E6%8B%89%E5%88%B7%E6%96%B0%E6%8E%A7%E4%BB%B6SwipeRefreshLayout.md) | 编辑中 |  |  |
| [Android视图系统：Android滑动控件ViewPager](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E6%BB%91%E5%8A%A8%E6%8E%A7%E4%BB%B6ViewPager.md) | 编辑中 |  |  |
| [Android视图系统：Android滚动控件ScrollView](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E6%BB%9A%E5%8A%A8%E6%8E%A7%E4%BB%B6ScrollView.md) | 编辑中 |  |  |
| [Android视图系统：Android列表控件RecyclerView](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%88%97%E8%A1%A8%E6%8E%A7%E4%BB%B6RecyclerView.md) | 编辑中 |  |  |
| [Android视图系统：Android进度条控件ProgressBar](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E8%BF%9B%E5%BA%A6%E6%9D%A1%E6%8E%A7%E4%BB%B6ProgressBar.md) | 编辑中 |  |  |
| [Android视图系统：Android文字控件TextView](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E6%96%87%E5%AD%97%E6%8E%A7%E4%BB%B6TextView.md) | 编辑中 |  |  |
| [Android视图系统：Android编辑框控件EditText](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E7%BC%96%E8%BE%91%E6%A1%86%E6%8E%A7%E4%BB%B6EditText.md) | 编辑中 |  |  |
| [Android视图系统：Android图片控件ImageView](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%9B%BE%E7%89%87%E6%8E%A7%E4%BB%B6ImageView.md) | 编辑中 |  |  |
| [Android视图系统：Android对话框控件Dialog](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%AF%B9%E8%AF%9D%E6%A1%86%E6%8E%A7%E4%BB%B6Dialog.md) | 编辑中 |  |  |
| [Android视图系统：Android弹出窗口控件PopupWindow](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%BC%B9%E5%87%BA%E7%AA%97%E5%8F%A3%E6%8E%A7%E4%BB%B6PopupWindow.md) | 编辑中 |  |  |
| [Android视图系统：Android浏览器控件WebView](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F/Android%E8%A7%86%E5%9B%BE%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E6%B5%8F%E8%A7%88%E5%99%A8%E6%8E%A7%E4%BB%B6WebView.md) | 编辑中 |  |  |

**Android组件系统**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android组件系统：Android组件系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |
| [Android组件系统：Android组件注册表AndroidManifest](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E7%BB%84%E4%BB%B6%E6%B3%A8%E5%86%8C%E8%A1%A8AndroidManifest.md) | 编辑中 |  |  |
| [Android组件系统：Android应用上下文Context](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%BA%94%E7%94%A8%E4%B8%8A%E4%B8%8B%E6%96%87Context.md) | 编辑中 |  |  |
| [Android组件系统：Android视图容器Activity](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E8%A7%86%E5%9B%BE%E5%AE%B9%E5%99%A8Activity.md) | 编辑中 |  |  |
| [Android组件系统：Android视图片段Fragment](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E8%A7%86%E5%9B%BE%E7%89%87%E6%AE%B5Fragment.md) | 编辑中 |  |  |
| [Android组件系统：Android后台服务Service](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%90%8E%E5%8F%B0%E6%9C%8D%E5%8A%A1Service.md) | 编辑中 |  |  |
| [Android组件系统：Android数据提供者ContentProvider](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E6%95%B0%E6%8D%AE%E6%8F%90%E4%BE%9B%E8%80%85ContentProvider.md) | 校对中 | [wusp](https://github.com/wusp) | [郭孝星](https://github.com/guoxiaoxing) |
| [Android组件系统：Android广播接收者BroadcastReceiver](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F/Android%E7%BB%84%E4%BB%B6%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%B9%BF%E6%92%AD%E6%8E%A5%E6%94%B6%E8%80%85BroadcastReceiver.md) | 编辑中 |  |  |

**Android资源系统**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android资源系统：Android资源系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%B5%84%E6%BA%90%E7%B3%BB%E7%BB%9F/Android%E5%8C%85%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E8%B5%84%E6%BA%90%E7%B3%BB%E7%BB%9F%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |
| [Android资源系统：应用资源Resources](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%B5%84%E6%BA%90%E7%B3%BB%E7%BB%9F/Android%E5%8C%85%E7%B3%BB%E7%BB%9F%EF%BC%9A%E5%BA%94%E7%94%A8%E8%B5%84%E6%BA%90Resources.md) | 编辑中 |  |  |
| [Android资源系统：资源管理器AssetManager](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E8%B5%84%E6%BA%90%E7%B3%BB%E7%BB%9F/Android%E5%8C%85%E7%B3%BB%E7%BB%9F%EF%BC%9A%E8%B5%84%E6%BA%90%E7%AE%A1%E7%90%86%E5%99%A8AssetManager.md) | 编辑中 |  |  |

**Android包系统**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android包系统：Android包系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%8C%85%E7%B3%BB%E7%BB%9F/Android%E5%8C%85%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E8%B5%84%E6%BA%90%E7%B3%BB%E7%BB%9F%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |
| [Android包系统：APK解析器PackageParser](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%8C%85%E7%B3%BB%E7%BB%9F/Android%E5%8C%85%E7%B3%BB%E7%BB%9F%EF%BC%9AAPK%E8%A7%A3%E6%9E%90%E5%99%A8PackageParser.md) | 编辑中 |  |  |
| [Android包系统：APK的打包流程](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%8C%85%E7%B3%BB%E7%BB%9F/Android%E5%8C%85%E7%B3%BB%E7%BB%9F%EF%BC%9AAPK%E7%9A%84%E6%89%93%E5%8C%85%E6%B5%81%E7%A8%8B.md) | 编辑中 |  |  |
| [Android包系统：APK的安装流程](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%8C%85%E7%B3%BB%E7%BB%9F/Android%E5%8C%85%E7%B3%BB%E7%BB%9F%EF%BC%9AAPK%E7%9A%84%E5%AE%89%E8%A3%85%E6%B5%81%E7%A8%8B.md) | 编辑中 |  |  |
| [Android包系统：APK的加载流程](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%8C%85%E7%B3%BB%E7%BB%9F/Android%E5%8C%85%E7%B3%BB%E7%BB%9F%EF%BC%9AAPK%E7%9A%84%E5%8A%A0%E8%BD%BD%E6%B5%81%E7%A8%8B.md) | 编辑中 |  |  |

**Android应用通信系统**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android应用通信系统：Android应用通信系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%BA%94%E7%94%A8%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F/Android%E5%BA%94%E7%94%A8%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%BA%94%E7%94%A8%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |
| [Android应用通信系统：线程通信的桥梁Handler](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%BA%94%E7%94%A8%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F/Android%E5%BA%94%E7%94%A8%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%EF%BC%9A%E7%BA%BF%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%9A%84%E6%A1%A5%E6%A2%81Handler.md) | 编辑中 |  |  |
| [Android应用通信系统：局域广播LocalBroadcastReceiver](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%BA%94%E7%94%A8%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F/Android%E5%BA%94%E7%94%A8%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%EF%BC%9A%E5%B1%80%E5%9F%9F%E5%B9%BF%E6%92%ADLocalBroadcastReceiver.md) | 编辑中 |  |  |

**Android动画系统**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android动画系统：Android动画系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%8A%A8%E7%94%BB%E7%B3%BB%E7%BB%9F/Android%E5%8A%A8%E7%94%BB%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%8A%A8%E7%94%BB%E7%B3%BB%E7%BB%9F%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |
| [Android动画系统：Android补间动画](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%8A%A8%E7%94%BB%E7%B3%BB%E7%BB%9F/Android%E5%8A%A8%E7%94%BB%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E8%A1%A5%E9%97%B4%E5%8A%A8%E7%94%BB.md) | 编辑中 |  |  |
| [Android动画系统：Android帧动画](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%8A%A8%E7%94%BB%E7%B3%BB%E7%BB%9F/Android%E5%8A%A8%E7%94%BB%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%B8%A7%E5%8A%A8%E7%94%BB.md) | 编辑中 |  |  |
| [Android动画系统：Android属性动画](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%8A%A8%E7%94%BB%E7%B3%BB%E7%BB%9F/Android%E5%8A%A8%E7%94%BB%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%B1%9E%E6%80%A7%E5%8A%A8%E7%94%BB.md) | 编辑中 |  |  |

**Android多媒体系统**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android多媒体系统：Android多媒体系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%A4%9A%E5%AA%92%E4%BD%93%E7%B3%BB%E7%BB%9F/Android%E5%A4%9A%E5%AA%92%E4%BD%93%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E5%A4%9A%E5%AA%92%E4%BD%93%E7%B3%BB%E7%BB%9F%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |
| [Android多媒体系统：实时预览与播放SurfaceView、GLSurfaceVIew、TextureView与SurfaceTexture](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%A4%9A%E5%AA%92%E4%BD%93%E7%B3%BB%E7%BB%9F/Android%E5%A4%9A%E5%AA%92%E4%BD%93%E7%B3%BB%E7%BB%9F%EF%BC%9A%E5%AE%9E%E6%97%B6%E9%A2%84%E8%A7%88%E4%B8%8E%E6%92%AD%E6%94%BESurfaceView%E3%80%81GLSurfaceVIew%E3%80%81TextureView%E4%B8%8ESurfaceTexture.md) | 编辑中 |  |  |
| [Android多媒体系统：视频播放VideoView](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%A4%9A%E5%AA%92%E4%BD%93%E7%B3%BB%E7%BB%9F/Android%E5%A4%9A%E5%AA%92%E4%BD%93%E7%B3%BB%E7%BB%9F%EF%BC%9A%E8%A7%86%E9%A2%91%E6%92%AD%E6%94%BEVideoView.md) | 编辑中 |  |  |
| [Android多媒体系统：相机Camera](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%A4%9A%E5%AA%92%E4%BD%93%E7%B3%BB%E7%BB%9F/Android%E5%A4%9A%E5%AA%92%E4%BD%93%E7%B3%BB%E7%BB%9F%EF%BC%9A%E7%9B%B8%E6%9C%BACamera.md) | 编辑中 |  |  |
| [Android多媒体系统：相机Camera2](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%A4%9A%E5%AA%92%E4%BD%93%E7%B3%BB%E7%BB%9F/Android%E5%A4%9A%E5%AA%92%E4%BD%93%E7%B3%BB%E7%BB%9F%EF%BC%9A%E7%9B%B8%E6%9C%BACamera2.md) | 编辑中 |  |  |
| [Android多媒体系统：音视频解码MediaCodec](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E5%A4%9A%E5%AA%92%E4%BD%93%E7%B3%BB%E7%BB%9F/Android%E5%A4%9A%E5%AA%92%E4%BD%93%E7%B3%BB%E7%BB%9F%EF%BC%9A%E9%9F%B3%E8%A7%86%E9%A2%91%E8%A7%A3%E7%A0%81MediaCodec.md) | 编辑中 |  |  |

**Android数据库系统**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android数据库系统：Android数据库系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E6%95%B0%E6%8D%AE%E5%BA%93%E7%B3%BB%E7%BB%9F/Android%E6%95%B0%E6%8D%AE%E5%BA%93%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E6%95%B0%E6%8D%AE%E5%BA%93%E7%B3%BB%E7%BB%9F%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |
| [Android数据库系统：数据库SQLiteDatabase](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E6%95%B0%E6%8D%AE%E5%BA%93%E7%B3%BB%E7%BB%9F/Android%E6%95%B0%E6%8D%AE%E5%BA%93%E7%B3%BB%E7%BB%9F%EF%BC%9A%E6%95%B0%E6%8D%AE%E5%BA%93SQLiteDatabase.md) | 编辑中 |  |  |
| [Android数据库系统：数据库连接SQLiteConnection](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E6%95%B0%E6%8D%AE%E5%BA%93%E7%B3%BB%E7%BB%9F/Android%E6%95%B0%E6%8D%AE%E5%BA%93%E7%B3%BB%E7%BB%9F%EF%BC%9A%E6%95%B0%E6%8D%AE%E5%BA%93%E8%BF%9E%E6%8E%A5SQLiteConnection.md) | 编辑中 |  |  |
| [Android数据库系统：数据库会话SQLiteSession](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E6%95%B0%E6%8D%AE%E5%BA%93%E7%B3%BB%E7%BB%9F/Android%E6%95%B0%E6%8D%AE%E5%BA%93%E7%B3%BB%E7%BB%9F%EF%BC%9A%E6%95%B0%E6%8D%AE%E5%BA%93%E4%BC%9A%E8%AF%9DSQLiteSession.md) | 编辑中 |  |  |
| [Android数据库系统：数据库语句转换SQLiteProgram](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E6%95%B0%E6%8D%AE%E5%BA%93%E7%B3%BB%E7%BB%9F/Android%E6%95%B0%E6%8D%AE%E5%BA%93%E7%B3%BB%E7%BB%9F%EF%BC%9A%E6%95%B0%E6%8D%AE%E5%BA%93%E8%AF%AD%E5%8F%A5%E8%BD%AC%E6%8D%A2SQLiteProgram.md) | 编辑中 |  |  |
| [Android数据库系统：数据库游标Cursor](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Android%E5%BA%94%E7%94%A8%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E6%95%B0%E6%8D%AE%E5%BA%93%E7%B3%BB%E7%BB%9F/Android%E6%95%B0%E6%8D%AE%E5%BA%93%E7%B3%BB%E7%BB%9F%EF%BC%9A%E6%95%B0%E6%8D%AE%E5%BA%93%E6%B8%B8%E6%A0%87Cursor.md) | 编辑中 |  |  |

<a name="0ec0b0a6"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/c853ac17efaf355064ded9f157ab9980bce6a086/README.md#java%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82)Java系统框架层
**Android组件管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android组件管理服务：Android组件管理服务概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Java%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1/Android%E7%BB%84%E4%BB%B6%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1%EF%BC%9AAndroid%E7%BB%84%E4%BB%B6%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |
| [Android组件管理服务：组件管理服务ActivityManagerService](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Java%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1/Android%E7%BB%84%E4%BB%B6%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1%EF%BC%9A%E7%BB%84%E4%BB%B6%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1ActivityManagerService.md) | 编辑中 |  |  |
| [Android组件管理服务：应用主线程ActivityThread](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Java%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%BB%84%E4%BB%B6%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1/Android%E7%BB%84%E4%BB%B6%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1%EF%BC%9AAndroid%E7%BB%84%E4%BB%B6%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |

**Android窗口管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android窗口管理服务：Android窗口管理服务概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Java%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%AA%97%E5%8F%A3%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1/Android%E7%AA%97%E5%8F%A3%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1%EF%BC%9AAndroid%E7%AA%97%E5%8F%A3%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |
| [Android窗口管理服务：窗口管理服务WindowManagerService](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Java%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%AA%97%E5%8F%A3%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1/Android%E7%AA%97%E5%8F%A3%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1%EF%BC%9A%E7%AA%97%E5%8F%A3%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1WindowManagerService.md) | 编辑中 |  |  |
| [Android窗口管理服务：窗口启动、显示与切换](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Java%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%AA%97%E5%8F%A3%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1/Android%E7%AA%97%E5%8F%A3%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1%EF%BC%9A%E7%AA%97%E5%8F%A3%E5%90%AF%E5%8A%A8%E3%80%81%E6%98%BE%E7%A4%BA%E4%B8%8E%E5%88%87%E6%8D%A2.md) | 编辑中 |  |  |
| [Android窗口管理服务：窗口大小与位置计算](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Java%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%AA%97%E5%8F%A3%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1/Android%E7%AA%97%E5%8F%A3%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1%EF%BC%9A%E7%AA%97%E5%8F%A3%E5%A4%A7%E5%B0%8F%E4%B8%8E%E4%BD%8D%E7%BD%AE%E8%AE%A1%E7%AE%97.md) | 编辑中 |  |  |
| [Android窗口管理服务：窗口动画](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/Java%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/Android%E7%AA%97%E5%8F%A3%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1/Android%E7%AA%97%E5%8F%A3%E7%AE%A1%E7%90%86%E6%9C%8D%E5%8A%A1%EF%BC%9A%E7%AA%97%E5%8F%A3%E5%8A%A8%E7%94%BB.md) | 编辑中 |  |  |

**Android图形绘制服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android图形绘制服务：Android图形绘制服务概述 |  |  |  |
| Android图形绘制服务：SurfaceFlinger |  |  |  |
| Android图形绘制服务：Android布局解析器LayoutInflater |  |  |  |

**Android包管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android视图系统：Android视图系统概述 |  |  |  |
| Android窗口管理服务：PackageServiceManager |  |  |  |

**Android反馈管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android反馈管理服务：Android反馈管理服务概述 |  |  |  |
| Android反馈管理服务：AccessibilityManagerService |  |  |  |

**Android壁纸管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android壁纸管理服务：Android壁纸管理服务概述 |  |  |  |
| Android壁纸管理服务：WallpaperManagerService |  |  |  |

**Android搜索管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android搜索管理服务：Android搜索管理服务概述 |  |  |  |
| Android搜索管理服务：SearchManagerService |  |  |  |

**Android通知管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android搜索管理服务：Android搜索管理服务概述 |  |  |  |
| Android通知管理服务：NotificationManagerService |  |  |  |

**Android定位管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android定位管理服务：Android定位管理服务概述 |  |  |  |
| Android定位管理服务：LocationManagerService |  |  |  |

**Android输入法管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android视图系统：Android视图系统概述 |  |  |  |
| Android输入法管理服务：InputMethodManagerService |  |  |  |

**Android电源管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android电源管理服务：Android电源管理服务概述 |  |  |  |
| Android电源管理服务：PowerManagerService |  |  |  |

**Android闹钟管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android电源管理服务：Android电源管理服务概述 |  |  |  |
| Android闹钟管理服务：AlarmManagerService |  |  |  |

**Android网络管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android网络管理服务：Android网络管理服务概述 |  |  |  |
| Android网络管理服务：ConnectivityService |  |  |  |

**Android账户管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android账户管理服务：Android账户管理服务概述 |  |  |  |
| Android账户管理服务：AccountManagerService |  |  |  |

**Android设备存储管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android设备存储管理服务：Android设备存储管理服务概述 |  |  |  |
| Android设备存储管理服务：DeviceStorageMonitorService |  |  |  |

**Android备份管理服务**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android备份管理服务：Android备份管理服务概述 |  |  |  |
| Android备份管理服务：BackupManagerService |  |  |  |

<a name="4a47097d"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/c853ac17efaf355064ded9f157ab9980bce6a086/README.md#c%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82)C++系统框架层
**Binder进程通信系统**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Binder进程通信系统：Binder进程通信系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E5%B1%82/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%EF%BC%9ABinder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |
| [Binder进程通信系统：服务管理ServiceManager](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E5%B1%82/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%EF%BC%9A%E6%9C%8D%E5%8A%A1%E7%AE%A1%E7%90%86ServiceManager.md) | 编辑中 |  |  |
| [Binder进程通信系统：Binder线程池](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E5%B1%82/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%EF%BC%9ABinder%E7%BA%BF%E7%A8%8B%E6%B1%A0.md) | 编辑中 |  |  |
| [Binder进程通信系统：Binder驱动](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E5%B1%82/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F/Binder%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%EF%BC%9ABinder%E9%A9%B1%E5%8A%A8.md) | 编辑中 |  |  |

**Socket进程通信系统**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Socket进程通信系统：Socket进程通信系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E5%B1%82/Socket%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F/Socket%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%EF%BC%9ASocket%E8%BF%9B%E7%A8%8B%E9%80%9A%E4%BF%A1%E7%B3%BB%E7%BB%9F%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |

**Java虚拟机**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Java虚拟机：Java虚拟机概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%90%E8%A1%8C%E6%97%B6%E5%B1%82/Java%E8%99%9A%E6%8B%9F%E6%9C%BA/Java%E8%99%9A%E6%8B%9F%E6%9C%BA%EF%BC%9AJava%E8%99%9A%E6%8B%9F%E6%9C%BA%E6%A6%82%E8%BF%B0.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Java虚拟机：类加载机制](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%90%E8%A1%8C%E6%97%B6%E5%B1%82/Java%E8%99%9A%E6%8B%9F%E6%9C%BA/Java%E8%99%9A%E6%8B%9F%E6%9C%BA%EF%BC%9A%E7%B1%BB%E5%8A%A0%E8%BD%BD%E6%9C%BA%E5%88%B6.md) | 已完成 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Java虚拟机：自动内存管理机制](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%90%E8%A1%8C%E6%97%B6%E5%B1%82/Java%E8%99%9A%E6%8B%9F%E6%9C%BA/Java%E8%99%9A%E6%8B%9F%E6%9C%BA%EF%BC%9AJVM%E8%99%9A%E6%8B%9F%E6%9C%BA%E6%A6%82%E8%BF%B0%E6%A6%82%E8%BF%B0.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Java虚拟机：垃圾收集器与内存分配策略](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%90%E8%A1%8C%E6%97%B6%E5%B1%82/Java%E8%99%9A%E6%8B%9F%E6%9C%BA/Java%E8%99%9A%E6%8B%9F%E6%9C%BA%EF%BC%9A%E5%9E%83%E5%9C%BE%E6%94%B6%E9%9B%86%E5%99%A8%E4%B8%8E%E5%86%85%E5%AD%98%E5%88%86%E9%85%8D%E7%AD%96%E7%95%A5.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |

**ART虚拟机**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [ART虚拟机：ART虚拟机概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%90%E8%A1%8C%E6%97%B6%E5%B1%82/ART%E8%99%9A%E6%8B%9F%E6%9C%BA/ART%E8%99%9A%E6%8B%9F%E6%9C%BA%EF%BC%9AART%E8%99%9A%E6%8B%9F%E6%9C%BA%E6%A6%82%E8%BF%B0.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [ART虚拟机：OAT文件的加载流程](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%90%E8%A1%8C%E6%97%B6%E5%B1%82/ART%E8%99%9A%E6%8B%9F%E6%9C%BA/ART%E8%99%9A%E6%8B%9F%E6%9C%BA%EF%BC%9AOAT%E6%96%87%E4%BB%B6%E7%9A%84%E5%8A%A0%E8%BD%BD%E6%B5%81%E7%A8%8B.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [ART虚拟机：类与方法的查找流程](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%90%E8%A1%8C%E6%97%B6%E5%B1%82/ART%E8%99%9A%E6%8B%9F%E6%9C%BA/ART%E8%99%9A%E6%8B%9F%E6%9C%BA%EF%BC%9A%E7%B1%BB%E4%B8%8E%E6%96%B9%E6%B3%95%E7%9A%84%E6%9F%A5%E6%89%BE%E6%B5%81%E7%A8%8B.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [ART虚拟机：机器指令的查找流程](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%90%E8%A1%8C%E6%97%B6%E5%B1%82/ART%E8%99%9A%E6%8B%9F%E6%9C%BA/ART%E8%99%9A%E6%8B%9F%E6%9C%BA%EF%BC%9A%E6%9C%BA%E5%99%A8%E6%8C%87%E4%BB%A4%E7%9A%84%E6%9F%A5%E6%89%BE%E6%B5%81%E7%A8%8B.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [ART虚拟机：垃圾收集](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E8%BF%90%E8%A1%8C%E6%97%B6%E5%B1%82/ART%E8%99%9A%E6%8B%9F%E6%9C%BA/ART%E8%99%9A%E6%8B%9F%E6%9C%BA%EF%BC%9A%E5%9E%83%E5%9C%BE%E6%94%B6%E9%9B%86.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |

<a name="fe8277ee"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/c853ac17efaf355064ded9f157ab9980bce6a086/README.md#linux%E5%86%85%E6%A0%B8%E5%B1%82)Linux内核层
**Android进程系统**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android进程系统：Android进程系统概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E5%86%85%E6%A0%B8%E5%B1%82/Android%E8%BF%9B%E7%A8%8B%E7%B3%BB%E7%BB%9F/Android%E8%BF%9B%E7%A8%8B%E7%B3%BB%E7%BB%9F%EF%BC%9AAndroid%E8%BF%9B%E7%A8%8B%E7%B3%BB%E7%BB%9F%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |
| [Android进程系统：进程的创建、启动与调度流程](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E5%86%85%E6%A0%B8%E5%B1%82/Android%E8%BF%9B%E7%A8%8B%E7%B3%BB%E7%BB%9F/Android%E8%BF%9B%E7%A8%8B%E7%B3%BB%E7%BB%9F%EF%BC%9A%E8%BF%9B%E7%A8%8B%E7%9A%84%E5%88%9B%E5%BB%BA%E3%80%81%E5%90%AF%E5%8A%A8%E4%B8%8E%E8%B0%83%E5%BA%A6%E6%B5%81%E7%A8%8B.md) | 编辑中 |  |  |
| [Android进程系统：线程与线程池](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E5%86%85%E6%A0%B8%E5%B1%82/Android%E8%BF%9B%E7%A8%8B%E7%B3%BB%E7%BB%9F/Android%E8%BF%9B%E7%A8%8B%E7%B3%BB%E7%BB%9F%EF%BC%9A%E7%BA%BF%E7%A8%8B%E4%B8%8E%E7%BA%BF%E7%A8%8B%E6%B1%A0.md) | 编辑中 |  |  |
| [Android进程系统：AIDL](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%8E%9F%E7%90%86%E7%AF%87/C++%E7%B3%BB%E7%BB%9F%E6%A1%86%E6%9E%B6%E5%B1%82/%E5%86%85%E6%A0%B8%E5%B1%82/Android%E8%BF%9B%E7%A8%8B%E7%B3%BB%E7%BB%9F/Android%E8%BF%9B%E7%A8%8B%E7%B3%BB%E7%BB%9F%EF%BC%9AAIDL.md) | 编辑中 |  |  |

**Android内存系统**

| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| Android内存系统：Android内存系统 |  |  |  |
| Android内存系统：内存管理系统 |  |  |  |
| Android内存系统：Ashmem匿名共享内存系统 |  |  |  |

<a name="d49cb8f5"></a>
## [](https://github.com/BeesX/BeesAndroid/blob/c853ac17efaf355064ded9f157ab9980bce6a086/README.md#%E5%AE%9E%E8%B7%B5%E7%AF%87)实践篇
<a name="dadc7b53"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/c853ac17efaf355064ded9f157ab9980bce6a086/README.md#%E7%95%8C%E9%9D%A2%E5%BC%80%E5%8F%91)界面开发
| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android界面开发：View自定义概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E7%95%8C%E9%9D%A2%E5%BC%80%E5%8F%91/Android%E7%95%8C%E9%9D%A2%E5%BC%80%E5%8F%91%EF%BC%9AView%E8%87%AA%E5%AE%9A%E4%B9%89%E6%A6%82%E8%BF%B0.md) | 编辑中 |  |  |
| [Android界面开发：View自定义布局篇](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E7%95%8C%E9%9D%A2%E5%BC%80%E5%8F%91/Android%E7%95%8C%E9%9D%A2%E5%BC%80%E5%8F%91%EF%BC%9AView%E8%87%AA%E5%AE%9A%E4%B9%89%E5%B8%83%E5%B1%80%E7%AF%87.md) | 编辑中 |  |  |
| [Android界面开发：View自定义绘制篇](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E7%95%8C%E9%9D%A2%E5%BC%80%E5%8F%91/Android%E7%95%8C%E9%9D%A2%E5%BC%80%E5%8F%91%EF%BC%9AView%E8%87%AA%E5%AE%9A%E4%B9%89%E7%BB%98%E5%88%B6%E7%AF%87.md) | 编辑中 |  |  |
| [Android界面开发：View自定义交互篇](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E7%95%8C%E9%9D%A2%E5%BC%80%E5%8F%91/Android%E7%95%8C%E9%9D%A2%E5%BC%80%E5%8F%91%EF%BC%9AView%E8%87%AA%E5%AE%9A%E4%B9%89%E4%BA%A4%E4%BA%92%E7%AF%87.md) | 编辑中 |  |  |

<a name="927d6ee9"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/c853ac17efaf355064ded9f157ab9980bce6a086/README.md#%E5%A4%9A%E5%AA%92%E4%BD%93%E5%BC%80%E5%8F%91)多媒体开发
| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android多媒体开发：Bitmap实践指南](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E5%A4%9A%E5%AA%92%E4%BD%93%E5%BC%80%E5%8F%91/Android%E5%A4%9A%E5%AA%92%E4%BD%93%E5%BC%80%E5%8F%91%EF%BC%9ABitmap%E5%AE%9E%E8%B7%B5%E6%8C%87%E5%8D%97.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Android多媒体开发：Camera实践指南](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E5%A4%9A%E5%AA%92%E4%BD%93%E5%BC%80%E5%8F%91/Android%E5%A4%9A%E5%AA%92%E4%BD%93%E5%BC%80%E5%8F%91%EF%BC%9ACamera%E5%AE%9E%E8%B7%B5%E6%8C%87%E5%8D%97.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |

<a name="54f56c57"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/c853ac17efaf355064ded9f157ab9980bce6a086/README.md#%E7%BD%91%E7%BB%9C%E7%BC%96%E7%A8%8B)网络编程
| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android网络编程：基础理论](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E7%BD%91%E7%BB%9C%E7%BC%96%E7%A8%8B/Android%E7%BD%91%E7%BB%9C%E7%BC%96%E7%A8%8B%EF%BC%9A%E5%9F%BA%E7%A1%80%E7%90%86%E8%AE%BA.md) | 已完成 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |

<a name="e5c6c3f0"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/c853ac17efaf355064ded9f157ab9980bce6a086/README.md#%E6%B7%B7%E5%90%88%E7%BC%96%E7%A8%8B)混合编程
| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android混合编程：基本用法](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E6%B7%B7%E5%90%88%E7%BC%96%E7%A8%8B/Android%E6%B7%B7%E5%90%88%E7%BC%96%E7%A8%8B%EF%BC%9A%E5%9F%BA%E6%9C%AC%E7%94%A8%E6%B3%95.md) | 已完成 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Android混合编程：原生通信](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E6%B7%B7%E5%90%88%E7%BC%96%E7%A8%8B/Android%E6%B7%B7%E5%90%88%E7%BC%96%E7%A8%8B%EF%BC%9A%E5%8E%9F%E7%94%9F%E9%80%9A%E4%BF%A1.md) | 已完成 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Android混合编程：性能优化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E6%B7%B7%E5%90%88%E7%BC%96%E7%A8%8B/Android%E6%B7%B7%E5%90%88%E7%BC%96%E7%A8%8B%EF%BC%9A%E6%80%A7%E8%83%BD%E4%BC%98%E5%8C%96.md) | 已完成 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |

<a name="01344e39"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/c853ac17efaf355064ded9f157ab9980bce6a086/README.md#android%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96)Android应用优化
| 文章 | 文章状态 | 作者 | 校对 |
| --- | --- | --- | --- |
| [Android应用优化：优化概述](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96/Android%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96%EF%BC%9A%E4%BC%98%E5%8C%96%E6%A6%82%E8%BF%B0.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Android应用优化：启动优化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96/Android%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96%EF%BC%9A%E5%90%AF%E5%8A%A8%E4%BC%98%E5%8C%96.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Android应用优化：界面优化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96/Android%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96%EF%BC%9A%E7%95%8C%E9%9D%A2%E4%BC%98%E5%8C%96.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Android应用优化：内存优化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96/Android%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96%EF%BC%9A%E5%86%85%E5%AD%98%E4%BC%98%E5%8C%96.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Android应用优化：图像优化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96/Android%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96%EF%BC%9A%E5%9B%BE%E5%83%8F%E4%BC%98%E5%8C%96.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Android应用优化：网络优化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96/Android%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96%EF%BC%9A%E7%BD%91%E7%BB%9C%E4%BC%98%E5%8C%96.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Android应用优化：并发优化](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96/Android%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96%EF%BC%9A%E5%B9%B6%E5%8F%91%E4%BC%98%E5%8C%96.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |
| [Android应用优化：优化工具](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AE%9E%E8%B7%B5%E7%AF%87/%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96/Android%E5%BA%94%E7%94%A8%E4%BC%98%E5%8C%96%EF%BC%9A%E4%BC%98%E5%8C%96%E5%B7%A5%E5%85%B7.md) | 编辑中 | [郭孝星](https://github.com/guoxiaoxing) | [郭孝星](https://github.com/guoxiaoxing) |

> 欢迎关注我们的微信公众号，新文章会第一 时间发布到掘金博客与微信公众平台，我们也有自己的交流群，下方是QQ交流群，微信群已满，可以加我微信 allenwells 邀请入群。

微信公众平台 [![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551591115089-fbbd29b8-b48c-45e2-9b2c-bfbf713e115a.png#align=left&display=inline&height=300&originHeight=258&originWidth=258&size=0&status=done&width=300)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/wechat.png)<br />QQ交流群<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551591113479-ef171928-61b7-47cf-b77e-2a31dc6e18d1.png#align=left&display=inline&height=411&originHeight=740&originWidth=540&size=0&status=done&width=300)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/qq_2.png)
