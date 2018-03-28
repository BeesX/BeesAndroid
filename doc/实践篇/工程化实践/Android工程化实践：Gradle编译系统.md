# 大型Android项目的工程化实践：Gradle编译系统

**关于作者**

>郭孝星，程序员，吉他手，主要从事Android平台基础架构方面的工作，欢迎交流技术方面的问题，可以去我的[Github](https://github.com/guoxiaoxing)提issue或者发邮件至guoxiaoxingse@163.com与我交流。

**文章目录**

- 一 Groovy语言基础
- 二 Gradle脚本构建
    - 2.1 root build.gradle
    - 2.2 module build.gradle
    - 2.1 gradle wrapper
- 三 Gradle混淆与优化
    - 2.1 代码压缩
    - 2.2 资源压缩
- 四 Gradle多项目构建
- 五 Gradle多渠道打包
- 附录
    - Gradle常用命令
    - Gradle小技巧

关于文章封面，道理我都懂，你放个妹纸在文章封面上有什么意义吗？🙄

情况是这样的，昨天有个bug困扰了我一天，晚饭时分听到了T-ara的歌[《我怎么办》](http://music.163.com/#/m/song?id=28059209)，伴随着欢快的节奏，忽然思绪大开，解决了那个
bug，说到T-ara，当然要放在她们的主唱朴素妍的照片辣~🤓

闲话不多说，正文时间到。本篇文章是《大型Android项目的工程化之路》的开篇之作，这个系列的文章主要用来讨论伴随着Android项目越来越大时，如何处理编译与构建、VCS工作流、模块化、持续集成等问题，以及
一些应用黑科技插件化、热更新的实现方案。

首先让我们进入第一个主题，基于Gradle的项目的编译与构建。

>[Gradle](https://zh.wikipedia.org/wiki/Gradle)是一个基于Apache Ant和Apache Maven概念的项目自动化建构工具。它使用一种基于Groovy的特定领域语言来声明项目设置，大部分功能都通过
插件的方式实现。

<img src="https://github.com/guoxiaoxing/software-engineering/blob/master/art/gradle/gradle_org_hero.png"/>

官方网站：https://gradle.org/

官方介绍：From mobile apps to microservices, from small startups to big enterprises, Gradle helps teams build, automate and deliver better software, faster.

在正式介绍Gradle之前，我们先了解下Groovy语言的基础只是，方便我们后面的理解。

## 一 Groovy语言基础

[Groovy](http://groovy-lang.org/)是基于JVM的一种动态语言，语法与Java相似，也完全兼容Java。

这里我们简单的说一些我们平时用的到的Groovy语言的一些特性，方便大家理解和编写Gradle脚本，事实上如果你熟悉Kotlin、JavaScript这些语言，那么
Groovy对你来说会有种很相似的感觉。

注：Groovy是完全兼容Java的，也就意味着如果你对Groovy不熟悉，也可以用Java来写Gradle脚本。

- 单引号表示纯字符串，双引号表示对字符串求值，例如$取值。

```java
def version = '26.0.0'

dependencies {
    compile "com.android.support:appcompat-v7:$version"
}

```

- Groovy完全兼容Java的集合，并且进行了扩展。

```java
task printList {
    def list = [1, 2, 3, 4, 5]
    println(list)
    println(list[1])//访问第二个元素
    println(list[-1])//访问最后一个元素
    println(list[1..3])//访问第二个到第四个元素
}

task printMap {
    def map = ['width':720, 'height':1080]
    println(map)
    println(map.width)//访问width
    println(map.height)//访问height
    map.each {//遍历map
        println("Key:${it.key}, Value:${it.value}")
    }
}
```

- Groovy方法的定义方式和Java类似，调用方式比Java灵活，有返回值的函数也可以不写return语句，这个时候会把最后一行代码的值作为返回值返回。

```java
def method(int a, int b){
    if(a > b){
        a
    }else {
        b
    }
}

def  callMethod(){
    method 1, 2
}
```

可以看到，和Kotlin这些现代编程语言一样，有很多语法糖。了解了Groovy，我们再来看看Gradle工程相关知识。

## 二 Gradle脚本构建

一个标准的Android Gradle工程如下所示，我们分别来看看里面每个文件的作用。

<img src="https://github.com/guoxiaoxing/software-engineering/blob/master/art/gradle/gradle_project.png"/>

### 2.1 root build.gradle

>root build.gradle是根目录的build.gradle文件，它主要用来对整体工程以及各个Module进行一些通用的配置。

```java
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        //远程仓库
        google()
        jcenter()
    }
    dependencies {
        //Android Studio Gradle插件
        classpath 'com.android.tools.build:gradle:3.0.0'

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

//对所有工程进行遍历和配置
allprojects {
    repositories {
        //远程仓库
        jcenter()
        google()
    }
}

//对单个工程进行遍历和配置
subprojects{

}

task clean(type: Delete) {
    delete rootProject.buildDir
}


ext{
    //定义module通用的版本号，这样module里就可以通过$rootProject.ext.supportLibraryVersion
    //的方式访问
    supportLibraryVersion = '26.0.0'
}
```

### 2.2 module build.gradle

>module build.gradle用于module的配置与编译。

这里有很多常用的配置选项，你并不需要都把它们记住，有个大致的印象就行，等到用的时候再回来查一查。

```java
apply plugin: 'com.android.application'

android {
    compileSdkVersion 26
    buildToolsVersion '26.0.2'


    defaultConfig {
        //应用包名
        applicationId "com.guoxiaoxing.software.engineering.demo"
        //最低支持的Android SDK 版本
        minSdkVersion 15
        //基于开发的Android SDK版本
        targetSdkVersion 26
        //应用版本号
        versionCode 1
        //应用版本名称
        versionName "1.0"

        //单元测试时使用的Runner
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }

    signingConfigs{

        debug{
            storeFile file("debugKey.keystore")
            storePassword '123456'
            keyAlias 'debugkeyAlias'
            keyPassword '123456'
        }

        release{
            storeFile file("releaseKey.keystore")
            storePassword '123456'
            keyAlias 'releasekeyAlias'
            keyPassword '123456'
        }
    }

    //Java编译选项
    compileOptions{

        //编码
        encoding = 'utf-8'

        //Java编译级别
        sourceCompatibility = JavaVersion.VERSION_1_6

        //生成的Java字节码版本
        targetCompatibility = JavaVersion.VERSION_1_6
    }

    //ADE配置选项
    adbOptions{

        //ADB命令执行的超时时间，超时时会返回CommandRectException异常。
        timeOutInMs = 5 * 1000//5秒

        //ADB安装选项，例如-r代表替换安装
        installOptions '-r', '-s'
    }

    //DEX配置选项
    dexOptions{

        //是否启动DEX增量模式，可以加快速度，但是目前这个特性不是很稳定
        incremental false

        //执行DX命令是为其分配的最大堆内存，主要用来解决执行DX命令是内存不足的情况
        javaMaxHeapSize '4g'

        //执行DX开启的线程数，适当的线程数量可以提高编译速度
        threadCount 2

        //是否开启jumbo模式，有时方法数超过了65525，需要开启次模式才能编译成功
        jumboMode true
    }

    lintOptions{

        //lint发现错误时是否退出Gradle构建
        abortOnError false
    }

    //构建的应用类型。用于指定生成的APK相关属性
    buildTypes {

        debug{

            //是否可调试
            debuggable true

            //是否可调试jni
            jniDebuggable true

            //是否启动自动拆分多个DEx
            multiDexEnabled true

            //是否开启APK优化，zipAlign是Android提供的一个整理优化APK文件的
            //工具，它可以提高系统和应用的运行效率，更快的读写APK里面的资源，降低
            //内存的优化
            zipAlignEnabled true

            //签名信息
            signingConfig signingConfigs.debug

            //是否自动清理未使用的资源
            shrinkResources true

            //是否启用混淆
            minifyEnabled true

            //指定多个混淆文件
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }

        release {
            //签名信息
            signingConfig signingConfigs.release

            //是否启用混淆
            minifyEnabled true

            //指定多个混淆文件
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

//依赖
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })
    testCompile 'junit:junit:4.12'
    implementation 'com.android.support.constraint:constraint-layout:1.0.2'
}
```

### 2.3 Gradle Wrapper

Gradle Wrapper是对Gradle的一层包装，目的在于团队开发中统一Gradle版本，一般可以通过gradle wrapper命令构建，会生成以下文件：

- gradle-wrapper.jar
- gradle-wrapper.properties

文件用来进行Gradle Wrapper进行相关配置。如下所示：

```java
#Fri Nov 24 17:39:29 CST 2017
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-4.1-all.zip
```

我们通常关心的是distributionUrl，它用来配置Gradle的版本，它会去该路径下载相应的Gradle包。

注：如果官方的gradle地址下载比较慢，可以去[国内的镜像地址](http://mirrors.flysnow.org)下载。

## 三 Gradle混淆与优化

### 3.1 代码压缩

>代码压缩通过 [ProGuard](https://www.guardsquare.com/en/proguard) 提供，ProGuard 会检测和移除封装应用中未使用的类、字段、方法和属性，包括自带代码库中的未使用项（这使其成为以变通方式解决 64k 引用限制的有用工具）。
ProGuard 还可优化字节码，移除未使用的代码指令，以及用短名称混淆其余的类、字段和方法。混淆过的代码可令您的 APK 难以被逆向工程，这在应用使用许可验证等安全敏感性功能时特别
有用。

```java
android {
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt'),
                    'proguard-rules.pro'
        }
    }
    ...
}
```

除了 minifyEnabled 属性外，还有用于定义 ProGuard 规则的 proguardFiles 属性：

- getDefaultProguardFile('proguard-android.txt') 方法可从 Android SDK tools/proguard/ 文件夹获取默认的 ProGuard 设置。
提示：要想做进一步的代码压缩，请尝试使用位于同一位置的 proguard-android-optimize.txt 文件。它包括相同的 ProGuard 规则，但还包括其他在字节码一级（方法内和方法间）执行分析的优化，以进一步减小 APK 大小和帮助提高其运行速度。
- proguard-rules.pro 文件用于添加自定义 ProGuard 规则。默认情况下，该文件位于模块根目录（build.gradle 文件旁）。

我们可以在项目里的proguard-rules.pro定义我们的混淆规则，

常用的混淆命令如下所示：

proguard 参数

- -include {filename}    从给定的文件中读取配置参数 

- -basedirectory {directoryname}    指定基础目录为以后相对的档案名称 

- -injars {class_path}    指定要处理的应用程序jar,war,ear和目录 

- -outjars {class_path}    指定处理完后要输出的jar,war,ear和目录的名称 

- -libraryjars {classpath}    指定要处理的应用程序jar,war,ear和目录所需要的程序库文件 

- -dontskipnonpubliclibraryclasses    指定不去忽略非公共的库类。 

- -dontskipnonpubliclibraryclassmembers    指定不去忽略包可见的库类的成员。 

保留选项 

- -keep {Modifier} {class_specification}    保护指定的类文件和类的成员 

- -keepclassmembers {modifier} {class_specification}    保护指定类的成员，如果此类受到保护他们会保护的更好

- -keepclasseswithmembers {class_specification}    保护指定的类和类的成员，但条件是所有指定的类和类成员是要存在。 

- -keepnames {class_specification}    保护指定的类和类的成员的名称（如果他们不会压缩步骤中删除） 

- -keepclassmembernames {class_specification}    保护指定的类的成员的名称（如果他们不会压缩步骤中删除） 

- -keepclasseswithmembernames {class_specification}    保护指定的类和类的成员的名称，如果所有指定的类成员出席（在压缩步骤之后） 

- -printseeds {filename}    列出类和类的成员- -keep选项的清单，标准输出到给定的文件 

压缩 

- -dontshrink    不压缩输入的类文件 

- -printusage {filename} 

- -whyareyoukeeping {class_specification}    

优化 

- -dontoptimize    不优化输入的类文件 

- -assumenosideeffects {class_specification}    优化时假设指定的方法，没有任何副作用 

- -allowaccessmodification    优化时允许访问并修改有修饰符的类和类的成员 

混淆 

- -dontobfuscate    不混淆输入的类文件 

- -printmapping {filename} 

- -applymapping {filename}    重用映射增加混淆 

- -obfuscationdictionary {filename}    使用给定文件中的关键字作为要混淆方法的名称 

- -overloadaggressively    混淆时应用侵入式重载 

- -useuniqueclassmembernames    确定统一的混淆类的成员名称来增加混淆 

- -flattenpackagehierarchy {package_name}    重新包装所有重命名的包并放在给定的单一包中 

- -repackageclass {package_name}    重新包装所有重命名的类文件中放在给定的单一包中 

- -dontusemixedcaseclassnames    混淆时不会产生形形色色的类名 

- -keepattributes {attribute_name,...}    保护给定的可选属性，例如LineNumberTable, LocalVariableTable, SourceFile, Deprecated, Synthetic, Signature, and InnerClasses. 

- -renamesourcefileattribute {string}    设置源文件中给定的字符串常量

另外关于具体的混淆规则，可以使用Android Stduio插件[AndroidProguardPlugin](https://github.com/zhonghanwen/AndroidProguardPlugin)，它帮我们收集了主要第三方库的混淆规则，可以
参考下。

混淆完成后都会输出下列文件：

- dump.txt：说明 APK 中所有类文件的内部结构。
- mapping.txt：提供原始与混淆过的类、方法和字段名称之间的转换。
- seeds.txt：列出未进行混淆的类和成员。
- usage.txt：列出从 APK 移除的代码。
t
这些文件保存在 <module-name>/build/outputs/mapping/release/ 中，这些文件是很有用的，我们还可以利用在SDK的安装目录下\tools\proguard\lib的proguardgui程序再结合
mapping.txt对APK进行反混淆，以及利用etrace 脚本解码混淆过后的应用程序堆栈信息，这通常是用来来分析混淆后的线上应用的bug。

retrace 脚本（在 Windows 上为 retrace.bat；在 Mac/Linux 上为 retrace.sh）。它位于 <sdk-root>/tools/proguard/ 目录中。该脚本利用 mapping.txt 文件来生成应用程序堆栈信息。

具体做法：

```java
retrace.sh -verbose mapping.txt obfuscated_trace.txt
```

另外，还要提一点，如果想要混淆支持Instant Run，可以使用Android内置的代码压缩器，Android内置的代码压缩器也可以使用 与 ProGuard 相同的配置文件来配置 Android 插件压缩器。
但是，Android 插件压缩器不会对您的代码进行混淆处理或优化，它只会删除未使用的代码。因此，它应该仅将其用于调试构建，并为发布构建启用 ProGuard，以便对发布 APK 的代码进行混淆
处理和优化。
                                                  
要启用 Android 插件压缩器，只需在 "debug" 构建类型中将 useProguard 设置为 false（并保留 minifyEnabled 设置 true），如下所示：

```java
android {
    buildTypes {
        debug {
            minifyEnabled true
            useProguard false
            proguardFiles getDefaultProguardFile('proguard-android.txt'),
                    'proguard-rules.pro'
        }
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt'),
                    'proguard-rules.pro'
        }
    }
}
```
### 3.2 资源压缩

>资源压缩通过适用于 Gradle 的 Android 插件提供，该插件会移除封装应用中未使用的资源，包括代码库中未使用的资源。它可与代码压缩发挥协同效应，使得在移除未使
用的代码后，任何不再被引用的资源也能安全地移除。

```java
android {
    ...
    buildTypes {
        release {
            shrinkResources true
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt'),
                    'proguard-rules.pro'
        }
    }
}
```
同样地，我们也可以自定义保留的资源，我们可以在项目中创建一个包含 <resources> 标记的 XML 文件，并在 tools:keep 属性中指定每个要保留的资源，在 tools:discard 属性中指
定每个要舍弃的资源。这两个属性都接受逗号分隔的资源名称列表。当然我们也可以使用星号字符作为通配符。
                  
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources xmlns:tools="http://schemas.android.com/tools"
    tools:keep="@layout/l_used*_c,@layout/l_used_a,@layout/l_used_b*"
    tools:shrinkMode="strict"
    tools:discard="@layout/unused2" />
```
然后将该文件保存在项目资源中，例如，保存在 res/raw/keep.xml。构建不会将该文件打包到 APK 之中。上面提到可以用discard指定需要删除的资源，

这里有人可能会疑惑，直接删了不就完了，还要指定删除🤔。这个其实通常用在多构建应用变体之中，同一个应用可能包打包成不同的变体，不同变体需要的资源文件是不一样的，这样
可以通过为不同变体定义不同的keep.xml来解决这个问题。

另外，上面还有个tools:shrinkMode="strict"，即启用严格模式进行资源压缩。正常情况下，资源压缩器可准确判定系统是否使用了资源，但有些动态引用资源的情况，例如：

```java
String name = String.format("img_%1d", angle + 1);
res = getResources().getIdentifier(name, "drawable", getPackageName());
```

这种情况下，资源压缩器就会将img_开头的资源都标记为已使用，不会被移除。这是一种默认情况下的防御行为，要停用这种行为只需要加上tools:shrinkMode="strict"即可。

最后，我们还可以通过resConfigs指定我们的应用只支持哪些语言的资源。

例如将语言资源限定为仅支持英语和法语：

```xml
android {
    defaultConfig {
        ...
        resConfigs "en", "fr"
    }
}
```

## 四 Gradle多项目构建

Android的项目一般分为应用项目、库项目和测试项目，它们对应的Gradle插件类型分别为：

- com.android.application
- com.android.library
- com.android.test

我们一般只有一个应用项目，但是会有多个库项目，通过添加依赖的方式引用库项目。

例如：

```java
compile ('commons-httpclient:commons-httpclient:3.1'){
    exclude group:'commons-codec',module:'commons-codec'//排除该group的依赖，group是必选项，module可选
}

//选择1以上任意一个版本
compile 'commons-httpclient:commons-httpclient:1.+'

//选择最新的版本，避免直接指定版本号 
compile 'commons-httpclient:commons-httpclient:latest.integration'
```

依赖类型主要分为五种：

- compile：源代码（src/main/java）编译时的依赖，最常用
- runtime：源代码（src/main/java）执行时依赖
- testCompile：测试代码（src/main/test）编译时的依赖
- testRuntime：测试代码（src/main/java）执行时的依赖
- archives：项目打包（e.g.jar）时的依赖

注：Gradle 3.0已经废弃了compile，并新增了implementation与api两个命令，它们的区别如下：

- api：完全等同于compile指令，没区别，你将所有的compile改成api，完全没有错。
- implementation：这个指令的特点就是，对于使用了该命令编译的依赖，对该项目有依赖的项目将无法访问到使用该命令编译的依赖中的任何程序，也就是将该依赖隐藏在内部，而不对外部公开。

在编译库的时候，我们通常选择的远程库是jcenter（包含maven），google也推出了自己的远程仓库google()(新的gradle插件需要从这个远程仓库上下载)，这些国外的远程仓库在编译的时候
有时候会非常慢，这个时候可以换成国内的阿里云镜像。

修改项目根目录下的文件 build.gradle ：

```java
buildscript {
    repositories {
        maven{ url 'http://maven.aliyun.com/nexus/content/groups/public/'}
    }
}

allprojects {
    repositories {
        maven{ url 'http://maven.aliyun.com/nexus/content/groups/public/'}
    }
}
```

另外，如果我们想把自己的项目提交到jcenter上，可以使用[bintray-release](https://github.com/novoda/bintray-release)，具体使用方式很简单，项目文档上说的也很清楚，这里就
不再赘述。

## 五 Gradle多渠道打包

根据发布的渠道或者客户群的不同，同一个应用可能会有很多变体，不同变体的应用名字、渠道等很多信息都会不一样，这个时候就要使用Gradle多渠道打包。
多渠道打包主要是通过productFlavor进行定制。

例如下面针对google、baidu批量配置了UMENG_CHANNEL。

```java
apply plugin: 'com.android.application'

android {

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            zipAlignEnabled true
        }
    }
    
    productFlavors {
        xiaomi {
            //manifestPlaceholders定义了AndroidManifest里的占位符，
            //AndroidManifest可以通过$UMENG_CHANNEL_VALUE来获取
            manifestPlaceholders = [UMENG_CHANNEL_VALUE: "xiaomi"]
        }
        _360 {
            manifestPlaceholders = [UMENG_CHANNEL_VALUE: "_360"]
        }
        baidu {
            manifestPlaceholders = [UMENG_CHANNEL_VALUE: "baidu"]
        }
        wandoujia {
            manifestPlaceholders = [UMENG_CHANNEL_VALUE: "wandoujia"]
        }
    }
}
```

当然我们也可以批量修改：

```java
productFlavors {
    xiaomi {}
    _360 {}
    baidu {}
    wandoujia {}
}  

//通过all函数遍历每一个productFlavors然后把它作为UMENG_CHANNEL的名字，这种做法
//适合渠道名称非常多的情况
productFlavors.all { 
    flavor -> flavor.manifestPlaceholders = [UMENG_CHANNEL_VALUE: name] 
}
```

productFlavors里还可以自定义变量，自定义的定了可以在BuildConfig里获取。自定义变量通过以下方法完成：

```java
buildConfigField 'String','WEB_URL','"http://www.baidu.com"'
```
渠道productFlavors和编译类型里都可以自定义变量。

```java
apply plugin: 'com.android.application'

android {

    buildTypes {
        
        debug{
            buildConfigField 'String','WEB_URL','"http://www.baidu.com"'
        }
        
        release {
            buildConfigField 'String','WEB_URL','"http://www.google.com"'
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            zipAlignEnabled true
        }
    }
    
    productFlavors {
        google {
            buildConfigField 'String','WEB_URL','"http://www.google.com"'
        }
        baidu {
            buildConfigField 'String','WEB_URL','"http://www.baidu.com"'
        }
    }
}
```

## 附录

### Gradle常用命令

强制刷新依赖

```java
gradle --refresh-dependencies assemble
```

查看app所有依赖库

```java
gradle dependencies :app

```

查看编译时依赖

```java
gradle dependencies -configuration compile
```

查看运行时依赖

```java
gradle dependencies -configuration runtime
```

### Gradle小技巧

#### 批量修改生成的APK文件名

>有些时候想改变输入APK的文件名。

```java
apply plugin: 'com.android.application'

android {
    ...
    applicationVariants.all { variant ->
        variant.outputs.each { output ->
            if (output.outputFile != null && output.outputFile.name.endsWith('.apk')
                    &&'release'.equals(variant.buildType.name)) {
                def flavorName = variant.flavorName.startsWith("_") ? variant.flavorName.substring(1) : variant.flavorName
                def apkFile = new File(
                        output.outputFile.getParent(),
                        "Example92_${flavorName}_v${variant.versionName}_${buildTime()}.apk")
                output.outputFile = apkFile
            }
        }
    }
}

def buildTime() {
    def date = new Date()
    def formattedDate = date.format('yyyyMMdd')
    return formattedDate
}
```

#### 动态获取应用版本号和版本名称

>一般来说在打包的时候都会从git选择一个tag来打包发布，以tag来作为应用的名称。

git获取tag的命令

```
git describe --abbrev=0 --tags
```

这个时候就需要利用Gradle执行shell命令，它为我们提供了exec这样简便的方式来执行shell命令。

```java

apply plugin: 'com.android.application'

android {
    defaultConfig {
        applicationId "com.guoxiaoxing.software.demo"
        minSdkVersion 14
        targetSdkVersion 23
        versionCode getAppVersionCode()
        versionName getAppVersionName()
    }
}
/**
 * 以git tag的数量作为其版本号
 * @return tag的数量
 */
def getAppVersionCode(){
    def stdout = new ByteArrayOutputStream()
    exec {
        commandLine 'git','tag','--list'
        standardOutput = stdout
    }
    return stdout.toString().split("\n").size()
}

/**
 * 从git tag中获取应用的版本名称
 * @return git tag的名称
 */
def getAppVersionName(){
    def stdout = new ByteArrayOutputStream()
    exec {
        commandLine 'git','describe','--abbrev=0','--tags'
        standardOutput = stdout
    }
    return stdout.toString().replaceAll("\n","")
}
```

#### 隐藏签名文件信息

>很多团队在开发初期都是直接把签名文件放在git上（我司现在还是这么干的T_T)，这样的做法在开发团队越来越大的时候会有安全问题，解决方式是将签名文件放在打包服务器中，然后动态获取。

```java
apply plugin: 'com.android.application'

android {
    compileSdkVersion 23
    buildToolsVersion "23.0.1"

    signingConfigs {
        def appStoreFile = System.getenv("STORE_FILE")
        def appStorePassword = System.getenv("STORE_PASSWORD")
        def appKeyAlias = System.getenv("KEY_ALIAS")
        def appKeyPassword = System.getenv("KEY_PASSWORD")

        //当不能从环境变量里获取到签名信息的时候，则使用本地的debug.keystore，这一般是
        //针对研发自己打包测试的情况
        if(!appStoreFile||!appStorePassword||!appKeyAlias||!appKeyPassword){
            appStoreFile = "debug.keystore"
            appStorePassword = "android"
            appKeyAlias = "androiddebugkey"
            appKeyPassword = "android"
        }
        release {
            storeFile file(appStoreFile)
            storePassword appStorePassword
            keyAlias appKeyAlias
            keyPassword appKeyPassword
        }
    }

    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            zipAlignEnabled true
        }
    }
}
```