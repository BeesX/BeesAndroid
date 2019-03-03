# Android源码阅读指南

作者：[郭孝星](https://github.com/guoxiaoxing)<br />校对：[郭孝星](https://github.com/guoxiaoxing)<br />文章状态：编辑中<br />**关于项目**
> [BeesAndroid](https://github.com/BeesAndroid/BeesAndroid)项目旨在通过提供一系列的工具与方法，降低阅读Android系统源码的门槛，让更多的Android工程师理解Android系统，掌握Android系统。

**文章目录**
* 一 基础篇
* 二 工具篇
* 三 书籍篇
* 二 源码篇

第一次阅览本系列文章，请参见[导读](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md)，更多文章请参见[文章目录](https://github.com/BeesAndroid/BeesAndroid/blob/master/README.md)。<br />本篇文章是本系列文章的导读文章，强烈建议第一次阅读本系列本章的同学先看一下导读的内容，它会告诉你文章中都用到了哪些工具，以及牵扯到哪些理论知识这些内 容会帮助你更快更好地去理解文章内容。<br />在线源码
* [Git repositories on android](https://android.googlesource.com/)
* [Android Open Source Project](https://source.android.com/)
<a name="37d355bc"></a>
## [](https://github.com/BeesX/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md#%E4%B8%80-%E5%9F%BA%E7%A1%80%E7%AF%87)一 基础篇
在阅读 AOSP（Android Open Source Projec）之前你需要掌握的以下理论知识。
* Java：AOSP的主要语言，当然是应该掌握的。
* Linux：Android是基于Linux内核开发的，如何你需要涉及到内核或者驱动的开发，你需要掌握Linux相关技术。
* Make：AOSP是用Make来编译的，因此要了解常用的Make语法。
* Git：程序员必备技能。
* C++：AOSP一些性能敏感的模块底层都是由C++完成的，当然如果你如果不需要关注底层实现，也可以跳过这一段，可以更多的去关注框架层。
* 设计模式：AOSP里大量的框架都是用了设计模式，比方说观察者模式、工厂模式、复合模式等，如果对设计模式不够了解的会看的云里雾里。
* 熟练的Android App开发技能
<a name="2619dc69"></a>
## [](https://github.com/BeesX/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md#%E4%BA%8C-%E5%B7%A5%E5%85%B7%E7%AF%87)二 工具篇
本系列的文章基于的环境是MacOS，但是所使用的工具软件多数都是跨平台的，所以对其他平台的小伙伴也没有影响。
<a name="7ed21038"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md#%E4%BB%A3%E7%A0%81%E9%98%85%E8%AF%BB)代码阅读
> 如果在Windows下直接上SourceInsight 就可以了，Mac下可以用Understand，功能和SourceInsight一样强大。

[Understand 4.0.849 代码阅读分析软件](http://xclient.info/s/understand.html)<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590281507-82361e42-cc92-433e-8e42-26ab19203058.png#align=left&display=inline&height=256&originHeight=256&originWidth=256&size=0&status=done&width=256)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/understand.png)
<a name="93090486"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md#%E6%BC%94%E7%A4%BA%E6%96%87%E7%A8%BF)演示文稿
> 演示文稿是用Keynote来做的。

[Keynote](http://xclient.info/s/keynote.html)<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590281077-1bf15717-a8f8-4da1-95ca-769fb0e7c8d0.png#align=left&display=inline&height=256&originHeight=256&originWidth=256&size=0&status=done&width=256)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/keynote.png)
<a name="74f5306f"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md#%E6%96%87%E7%AB%A0%E5%86%99%E4%BD%9C)文章写作
> 文章都是用Markdown来写的，工具用的是MWeb，一款很强大的Markdown编辑工具。

[MWeb for Mac](http://www.mweb.im/)<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590281957-42515352-8b7b-40c5-a956-8babf922e9b8.png#align=left&display=inline&height=256&originHeight=256&originWidth=256&size=0&status=done&width=256)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/nweb_for_mac.png)<br />👉 注：Markdown语法请参见[Markdown维基百科](https://zh.wikipedia.org/wiki/Markdown)。
<a name="b71d05e7"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md#%E5%9B%BE%E4%BE%8B%E7%BB%98%E5%88%B6)图例绘制
> 流程图、类图、时序图、系统架构图等各种图例采用OmniGraffle来绘制，Visio也比较好用，可惜Mac下没有。

[OmniGraffle](http://xclient.info/s/omnigraffle.html?t=c9ed67f2304886c055051d955e56f9c6919c1419)<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590280504-9d97ae52-b676-447e-8bc2-4cc75fa5865d.png#align=left&display=inline&height=256&originHeight=256&originWidth=256&size=0&status=done&width=256)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/OmniGraffle.png)<br />👉 注：图片可以以Github为图床（图片仓库）来存储图片，然后使用图片的链接来显示图片，注意要把图片链接中的blob改成raw，否则在其他网站无法识别图片。<br />例如<br /><img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/OmniGraffle.png"/>
<a name="2f313f9b"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md#%E5%9B%BE%E7%89%87%E5%A4%84%E7%90%86)图片处理
> Gif图的制作用的是VideoGIF。

[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590280235-9cd97739-d515-43af-8d72-ac5e2418e418.png#align=left&display=inline&height=256&originHeight=256&originWidth=256&size=0&status=done&width=256)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/VideoGIF.png)<br />[VideoGIF](http://xclient.info/s/videogif.html)
<a name="d709d17c"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md#%E5%9B%BE%E7%89%87%E9%98%85%E8%A7%88)图片阅览
> 本系列的文字会涉及各种时序图、UML图、系统结构图等，这些图片有时候会很大，为了读者能更加清晰的阅览，笔者不仅提供了png格式的图片，还 提供了压缩更好的svg（可缩放矢量图形）格式，该种格式用xml语言来描述图片信息，在mac下可用Gapplin打开。

[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590280673-ba3abec3-b937-4b17-8ce4-0a84481092d5.png#align=left&display=inline&height=175&originHeight=175&originWidth=175&size=0&status=done&width=175)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/Gapplin.png)<br />[Gapplin](https://itunes.apple.com/cn/app/gapplin/id768053424?mt=12&ign-mpt=uo%3D4)
<a name="6c3f83fc"></a>
## [](https://github.com/BeesX/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md#%E4%B8%89-%E4%B9%A6%E7%B1%8D%E7%AF%87)三 书籍篇
站在前辈的肩膀上，我们能看的更远，进步的更快。以下是学习框架与源码一些不错的书籍。<br />[Android 源码设计模式解析与实战](https://item.jd.com/11793928.html)：何红辉，关爱民 著<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590283532-81062898-b162-4f80-a891-bfb8f3eb300d.png#align=left&display=inline&height=374&originHeight=374&originWidth=369&size=0&status=done&width=369)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/android_source_code_design_pattern.png)<br />[Android系统源代码情景分析](https://item.jd.com/11838754.html)：罗升阳 著<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590281077-f1a83542-8ec8-4e4b-b741-af71e1fb9ea6.png#align=left&display=inline&height=375&originHeight=375&originWidth=365&size=0&status=done&width=365)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/android_source_code_scenario_analysis.png)<br />[Android开发艺术探索](https://item.jd.com/11760209.html)：任玉刚 著<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590280645-63d454bc-521a-4293-926d-02946b9e3467.png#align=left&display=inline&height=370&originHeight=370&originWidth=360&size=0&status=done&width=360)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/android_develop_art_explore.png)<br />本系列文章也提供了一些经典的书籍资源<br />[http://pan.baidu.com/share/link?shareid=1026404461&uk=3444638282](http://pan.baidu.com/share/link?shareid=1026404461&uk=3444638282)<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590280796-8b085805-3c2a-4225-bb53-2885081059cb.png#align=left&display=inline&height=478&originHeight=629&originWidth=982&size=0&status=done&width=746)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/android_book.png)<br />好了，以上就是全部的基础、工具与书籍，如果你还没有下载Android源码，现在我们就去下载吧。
<a name="07bf2d5e"></a>
## [](https://github.com/BeesX/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md#%E5%9B%9B-%E6%BA%90%E7%A0%81%E7%AF%87)四 源码篇
<a name="64a49db5"></a>
### [](https://github.com/BeesX/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md#41-%E6%BA%90%E7%A0%81%E4%B8%8B%E8%BD%BD)4.1 源码下载
官方地址：[https://source.android.com/index.html](https://source.android.com/index.html)<br />清华大学开源软件镜像站：[https://mirror.tuna.tsinghua.edu.cn/help/AOSP/](https://mirror.tuna.tsinghua.edu.cn/help/AOSP/)<br />**源码版本**<br />[android-7.1.1_r1](https://source.android.com/source/build-numbers.html#source-code-tags-and-builds)<br />**电脑环境**<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590280694-b2650ebc-bb48-496a-93b5-9255f838d958.png#align=left&display=inline&height=423&originHeight=354&originWidth=586&size=0&status=done&width=700)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/mac_os.png)
<a name="186c9653"></a>
#### [](https://github.com/BeesX/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md#1-%E5%88%9B%E5%BB%BA%E5%8C%BA%E5%88%86%E5%A4%A7%E5%B0%8F%E5%86%99%E7%A3%81%E7%9B%98)1 创建区分大小写磁盘
👉 注：Android源码考虑存储和编译至少需要200G以上空间，磁盘需要区别大小写，Mac一般空间有限，建议买一个移动硬盘存放源码。<br />打开磁盘工具<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590279963-35ee591b-c7fe-4a55-965a-d535418b13a3.png#align=left&display=inline&height=383&originHeight=815&originWidth=1491&size=0&status=done&width=700)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/disk_tool_01.png)<br />创建空白映像<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590280695-8dd7eee9-71bf-4270-bb14-4198432908a9.png#align=left&display=inline&height=252&originHeight=481&originWidth=1336&size=0&status=done&width=700)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/disk_tool_02.png)<br />设置区分大小写<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590280403-e310136e-4b20-4154-97e3-c58fdf68810b.png#align=left&display=inline&height=523&originHeight=354&originWidth=474&size=0&status=done&width=700)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/disk_tool_03.png)<br />注意！注意！创建区分大小写磁盘很重要，如果你不想下了一天的源码，结果编译的时候报这个错误。<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590282390-5d1c1188-7b2e-47b8-8c2f-fd5e5e7273da.png#align=left&display=inline&height=165&originHeight=164&originWidth=695&size=0&status=done&width=700)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/case_insensitive_error.png)<br />不要问我为什么有这个报错的图片，都是泪，我在Windows下划分的ExFat格式的移动硬盘，天真的我以为它是区分大小写的。😷<br />搞不清自己磁盘格式的同学，可以用diskutil info 命令看一下，只有有case-sensitive字样的才是区分大小写的磁盘，其他的都是耍流氓。😤<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590281870-70095a1b-5186-4f09-a034-bdc3df1f4f9e.png#align=left&display=inline&height=545&originHeight=979&originWidth=1258&size=0&status=done&width=700)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/case_sensitive.png)
<a name="e21dbc5e"></a>
#### [](https://github.com/BeesX/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md#2-%E4%B8%8B%E8%BD%BDrepo%E5%B7%A5%E5%85%B7)2 下载repo工具
```
mkdir ~/bin
PATH=~/bin:$PATH
curl https://storage.googleapis.com/git-repo-downloads/repo > ~/bin/repo
chmod a+x ~/bin/repo
```
如果你没有翻墙，可以使用清华大学的repo镜像：[https://mirrors.tuna.tsinghua.edu.cn/help/git-repo/](https://mirrors.tuna.tsinghua.edu.cn/help/git-repo/)<br />下载完成后将bin/repo打开，将里面的REPO_URL改成清华大学的镜像：
```
https://mirrors.tuna.tsinghua.edu.cn/git/git-repo/'
```
[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590280003-4da16928-a72e-4ac5-b375-a5107c4d77a5.png#align=left&display=inline&height=446&originHeight=777&originWidth=1220&size=0&status=done&width=700)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/repo_download_1.png)
<a name="f47f1f87"></a>
#### [](https://github.com/BeesX/BeesAndroid/blob/master/doc/%E5%AF%BC%E8%AF%BB.md#3-%E4%B8%8B%E8%BD%BD%E6%BA%90%E7%A0%81)3 下载源码
1 建立工作目录
```
mkdir WORKING_DIRECTORY
cd WORKING_DIRECTORY
```
```
$ git config --global user.text "Your Name"
$ git config --global user.email "you@example.com"
```
2 初始化仓库
```
repo init -u https://aosp.tuna.tsinghua.edu.cn/platform/manifest
```
如果需要某个特定的Android版本，可以在后面指定版本号。<br />Android系统各版本号：[https://source.android.com/source/build-numbers.html#source-code-tags-and-builds](https://source.android.com/source/build-numbers.html#source-code-tags-and-builds)
```
repo init -u https://aosp.tuna.tsinghua.edu.cn/platform/manifest -b android-7.1.1_r28
```
注：更多源码版本可以参见[https://source.android.com/source/build-numbers](https://source.android.com/source/build-numbers)<br />3 下载源码<br />同步源码树，开始下载源码，如果后续下载中断，也可以重复执行这个命令。
```
repo sync
```
源码的下载会经常中断，我们可以写一个脚本自动repo sync，保存成repo.sh，放到WORKING_DIRECTORY目录下
```
#!/bin/bash   
#FileName  jkYishon.sh  
PATH=~/bin:$PATH   
repo init -u https://aosp.tuna.tsinghua.edu.cn/platform/manifest -b android-7.1.1_r28
repo sync   
while [ $? = 1 ]; do   
echo "================sync failed, re-sync again ====="   
sleep 3   
repo sync   
done
```
配置可执行权限，运行即可。
```
chmod 777 repo.sh
./repo.sh
```
开始下载<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590280798-88de6c7a-23a6-4460-bf8e-1f5cbb484646.png#align=left&display=inline&height=635&originHeight=911&originWidth=1005&size=0&status=done&width=700)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/repo_download_2.png)<br />下载完成<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590280044-90ba5601-d7a4-4eba-ac85-9e5734fc01b7.png#align=left&display=inline&height=541&originHeight=452&originWidth=585&size=0&status=done&width=700)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/repo_download_3.png)<br />源码目录<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590281879-ac5f1db4-76f5-483e-a6f1-c8d946301ca3.png#align=left&display=inline&height=577&originHeight=1158&originWidth=1406&size=0&status=done&width=700)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/repo_download_4.png)<br />我下载的是android-7.1.1_r28版本的源码，一共160G左右，😤光源码都这么大，真是个浩大的工程。<br />3 将源码导入Android Studio<br />源码下载完成以后，你可以使用我上面推荐Understand来导入阅读源码，它是一种类似于Windows平台上的SourceInsight的工具。但是我们如果希望能够调试源码，则就 需要将源码导入到Android Studio中。<br />① 进入源码目录，运行命令
```
$ source build/envsetup.sh
```
[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590281695-b7eb33d4-d5d4-4f6f-89dd-76aa270ebc24.png#align=left&display=inline&height=281&originHeight=271&originWidth=675&size=0&status=done&width=700)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/import_to_as_1.png)<br />② 运行命令，生成android.ipr文件
```
$ make idegen && development/tools/idegen/idegen.sh
```
在运行这个命令的时候，可能会报错。<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590281335-2bb3f033-9f98-4456-bfc8-d4dea3a98227.png#align=left&display=inline&height=92&originHeight=158&originWidth=1200&size=0&status=done&width=700)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/import_to_as_2.png)<br />解决方案：打开build/core/combo/mac_version.mk
```
# mac_sdk_versions_supported :=  10.8 10.9 10.10 10.11
mac_sdk_versions_supported :=  10.12
```
注意：部分版本代码中指定支持的 mac 版本的文件位置已发生了改变，以 android-9.0.0_r3 版本为例，需要打开 /build/soong/cc/config/x86_darwin_host.go
```
darwinSupportedSdkVersions = []string{
	"10.10",
	"10.11",
	"10.12",
	"10.13",
	"10.14", //添加版本号
}
```
然后就可以运行成功了，成功以后会在源码目录下生成android.ipr文件。<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590281615-b3f52516-803a-459d-a9ec-ef7f7cac9b49.png#align=left&display=inline&height=403&originHeight=1308&originWidth=2270&size=0&status=done&width=700)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/import_to_as_4.png)<br />③ 导入Android Studio<br />接下来我们就要导入源码，但是源码的体积很大，如果全部导入会比较卡，我们可以排除一些代码。例如我只想研究framework里的代码，我们 可以在android.iml了添加以下内容。
```
<excludeFolder url="file://$MODULE_DIR$/.repo" />
<excludeFolder url="file://$MODULE_DIR$/abi" />
<excludeFolder url="file://$MODULE_DIR$/art" />
<excludeFolder url="file://$MODULE_DIR$/bionic" />
<excludeFolder url="file://$MODULE_DIR$/bootable" />
<excludeFolder url="file://$MODULE_DIR$/build" />
<excludeFolder url="file://$MODULE_DIR$/cts" />
<excludeFolder url="file://$MODULE_DIR$/dalvik" />
<excludeFolder url="file://$MODULE_DIR$/developers" />
<excludeFolder url="file://$MODULE_DIR$/development" />
<excludeFolder url="file://$MODULE_DIR$/device" />
<excludeFolder url="file://$MODULE_DIR$/docs" />
<excludeFolder url="file://$MODULE_DIR$/external" />
<excludeFolder url="file://$MODULE_DIR$/hardware" />
<excludeFolder url="file://$MODULE_DIR$/libcore" />
<excludeFolder url="file://$MODULE_DIR$/libnativehelper" />
<excludeFolder url="file://$MODULE_DIR$/ndk" />
<excludeFolder url="file://$MODULE_DIR$/out" />
<excludeFolder url="file://$MODULE_DIR$/packages" />
<excludeFolder url="file://$MODULE_DIR$/pdk" />
<excludeFolder url="file://$MODULE_DIR$/prebuilt" />
<excludeFolder url="file://$MODULE_DIR$/prebuilts" />
<excludeFolder url="file://$MODULE_DIR$/sdk" />
<excludeFolder url="file://$MODULE_DIR$/system" />
<excludeFolder url="file://$MODULE_DIR$/tools" />
```
这样几分钟就导入成功了。<br />[![](https://cdn.nlark.com/yuque/0/2019/png/279116/1551590280481-149e1d13-884e-4de2-b7ed-505ce41453ff.png#align=left&display=inline&height=679&originHeight=1094&originWidth=1128&size=0&status=done&width=700)](https://github.com/BeesAndroid/BeesAndroid/raw/master/art/import_to_as_5.png)<br />好了，以上便是我们开始分析Android源码所需的全部准备工作，这个系列的文章已经开始有几个月了，Android显示框架的相关内容已经完成，其他的也在进行中，具体可以 关注[BeesAndroid](https://github.com/BeesAndroid/BeesAndroid)项目。
