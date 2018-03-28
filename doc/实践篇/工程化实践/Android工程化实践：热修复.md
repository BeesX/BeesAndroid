# Android项目的工程化实践：热修复

**关于作者**

>郭孝星，程序员，吉他手，主要从事Android平台基础架构方面的工作，欢迎交流技术方面的问题，可以去我的[Github](https://github.com/guoxiaoxing)提issue或者发邮件至guoxiaoxingse@163.com与我交流。

**文章目录**


热修复原理图如下所示：

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/practice/project/hotfix/hotfix_principle_structure.png" width="500"/>

> 将补丁class文件打成dex文件，将该dex文件插入到App的Element数组的首部，App在遍历Element数组时会率先加载hotfix dex文件。从而达到热修复的目的。

QQ控件超级补丁

基于Android Dex分包的方案，利用插桩绕开预校验问题，只支持重启修复，不支持资源修复。

jar cvf patch.jar


dx --dex --p

Instant Run

Sophix

热插拔：无需重启，实时修复；插桩，性能开销大。
温插拔：可实现资源修复；下发全量资源包，开销大。
冷插拔：支持完整类替换；分包方案限制大。

