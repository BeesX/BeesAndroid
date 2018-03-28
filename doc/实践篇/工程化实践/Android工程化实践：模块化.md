# Android工程化实践：模块化

作者：[郭孝星](https://github.com/guoxiaoxing)

校对：[郭孝星](https://github.com/guoxiaoxing)

文章状态：已完成

**关于项目**

> [BeesAndroid](https://github.com/BeesAndroid/BeesAndroid)项目旨在通过提供一系列的工具与方法，降低阅读Android系统源码的门槛，让更多的Android工程师理解Android系统，掌握Android系统。

**文章目录**

- 一 发现问题
- 二 提出方案
    - 2.1 模块容器
    - 2.2 模块架构
    - 2.3 模块通信
    - 2.4 模块生命周期
    - 2.5 模块初始化
- 三 解决问题

模块化也是近两年经常被提及的一个技术点，究其原因，随着公司业务的逐渐壮大，主应用的工程体积也逐渐变大，管理和编译都变得十分困难。再加上随着公司业务的发展，主应用功能拆分和研发团队的拆分已成必然，这就要求
主应用里的各个模块能够独立编译、独立运行、不与主工程以及其他模块相互耦合。

模块化的过程其实是一个解决技术债的过程，每个公司的技术债也各不相同，因为模块化的过程是一个因地制宜的过程，没有放之四海而皆准的方案，一般说来，模块化分为以下三步：

1. 发现问题：发现问题就是理清公司现用的技术架构，清理技术债。
2. 提出方案：提出方案包含两个方面，一方面试新的工程架构，另一方面是做好新需求排期的安排（是否会阻塞新需求）。
3. 解决问题：新方案的推行也是逐步进行的，新的模块要做好灰度发布，应用回滚等工作。

而模块化实践起来并不是一件简单的事情，每家的应用都有自己的特殊情况，没有放之四海而皆准的技术方案，整体上来说，模块的拆分牵扯工程框架（MVP）、模块通信（进程内、跨进程）、Library多端复用、资源拆分等多种
情况，那么模块化最终要达到一个什么样的目标呢？🤔

- 主应用的其他模块可以快速移植到其他应用。
- 减少Build时间，各模块交由各团队独立负责，代码责任制。
- 主应用的各模块可以拆分成独立的应用，模块功能服务化。
- 模块可以独立开发、独立编译、独立运行，无需借助任何主工程环境，模块之间可以快速替换。
- 无侵入式的配置各种独立服务，例如：账户信息、设置信息、网络服务、图片加载服务、埋点服务、下拉刷新样式、错误状态等。
- Library可以快速便捷的在多端使用。库里功能尽量独立在View或者Fragment，在使用的时候可以直接添加到宿主Activity里，宿主Activity可以自己添加下载刷新样式、Action bar样式等。

理解了具体的模块化需求，我们接下来开始真正的开始进行模块化，光说不练假把式，空谈没有任何意义。下面的模块化都是围绕着我司主应用**大风车**而展开的。

广告时间到😎

大风车：http://dafengche.souche.com/

> 一款SaaS产品，提供建站系统、ERP、CRM、微信营销系统、财务系统等解决方案，旨在帮助车商及4S集团提升运营和管理水平。

<img src="https://github.com/BeesAndroid/BeesAndroid/blob/master/art/practice/project/module/dafengche_banner.png"/>

在分析方案之前，首先我们要知道我们的应用出了什么问题，针对大风车这个项目，我们来具体分析下。

## 一 发现问题

大风车与2015年上线，经过三年的发展，业务有了很大的增长，功能也逐渐完善，大风车里程碑如下所示：

<img src="https://github.com/BeesAndroid/BeesAndroid/blob/master/art/practice/project/module/dafengche_milestone.png"/>

我们和其他团队一样，在业务的发展中，主工程的架构也在不断的变化，我简单总结一下：

1. 微型项目：早期就是一个工程，几个人，那个时候也是业务跑量的时候，没有特别注意架构桑的问题。
2. 小型项目：随着业务的发展，业务种类也逐渐增多，这个时候我们就把一些业务模块拆分成了独立的Library，体抽了一个Base Library，提供了一些工具库和样式上的东西。
3. 中型项目：业务进一步增长，单纯搞Module Library已经不好用了，这个时间插件化框架很火，很强大，但是问题也很多，我们最终采用了Router的方式实现了一套**伪模块化方案**。
4. 大型项目：时间来到了现在，公司业务有了爆发式的增长，公司的应用也有原来的2个变成了5个，而且还有很多定制App、影子App，模块App等需求提交给我们，在上一套**伪模块化方案**的基础
上，我们要实现一套真正的模块化方案。

大风车工程层级结构如下图所示：

<img src="https://github.com/BeesAndroid/BeesAndroid/blob/master/art/practice/project/module/dafengche_structure.png"/>

可以看到整个大风车的主工程可以分为四层：

- 主工程业务层
- 模块业务层
- 公司框架层
- 第三方框架层

所以你可以看到这个工程与模块之间、模块与模块之间的依赖关系真的是美如画😅，相互引用导致扩展性和可维护性都很差，而且难以测试。我们来看看这种项目架构的问题在哪里：

- 模块边界被破坏，模块之间相互依赖，模块升级复杂，测试困难。
- 基础工程中心化，类库积累过重，难以维护。
- 模块依赖主工程，所有模块无法独立编译、独立发布，编译耗时，APK体积巨大，多团队无法并行开发。

## 二 提出方案

我们先来看一看重构后的架构，如下所示：

<img src="https://github.com/BeesAndroid/BeesAndroid/blob/master/art/practice/project/module/dafengche_new_structure.png"/>

重构后的大风车采用多容器架构，我们来看看这套架构是如何实现的。

### 2.1 模块容器

既然要把业务模块化，那就要有承载模块的容器，目前来说主要用以下三种容器：

- Native容器：Android/iOS原生的容器，承载使用原生实现的业务，例如Android就有Activity容器、Fragment容器以及更加细粒度的View容器。
- H5容器：传统WebView承载的页面。
- ReactNative/Weex/Flutter容器：这是自Facebook从15年推出RN方案开始后，流行起来的方案，这套方案的思想就是将JS组件转义成Native组件，从而实现一套界面，多端运行的效果。

👉 注：手淘提供了细粒度的View容器方案：[Virtualview-Android](https://github.com/alibaba/Virtualview-Android)，它可以通过下发XML配置文件，动态的渲染View。

从长远来看，这三套容器都不是用来相互取代对方，而是会长期并存，取长补短，相互助益。

<img src="https://github.com/BeesAndroid/BeesAndroid/blob/master/art/practice/project/module/three_container_structure.png" width="600"/>

- Native容器：Native容器适合用来编写应用的基础骨架页面，例如主页等，这在iOS上也用来避免审核上的问题。
- H5容器：H5容器适合用来编写经常需要变化的页面，例商家活动页等。
- ReactNative/Weex/Flutter容器：这一类容器就适合用来编写常规的页面界面，由于这一类容器也天然带有热更新能力，所以它也可以用来解决动态发布，热修复等方面的问题。

那如何实现这三套容器呢？🤔

- Native容器：插件化方案，插件化方案大体都比较相似，具体可以参见我这一篇文章的讨论[VirtualAPK](https://github.com/guoxiaoxing/android-open-framwork-analysis/blob/master/doc/Android开源框架源码鉴赏：VIrtualAPK.md)。
- H5容器：WebView封装，Jockey通信协议封装。
- ReactNative/Weex/Flutter容器：ReactNative/Weex/Flutter容器工程化体系搭建，事实上，用RN或者Weex写页面是十分简单的，它的复杂性在于工程化体系的搭建。

这三套容器的实现，我们后续都有详细的文章来讨论，我们接着来看看模块架构的实现。

### 2.2 模块架构

一个良好的系统设计纵向分层，横向模块化。我们来看看从纵向和横向的角度如何去设计一个模块。

#### 2.2.1 纵向架构

一般说来，从纵向角度，一个模块一般可以划分为三个部分：

<img src="https://github.com/BeesAndroid/BeesAndroid/blob/master/art/practice/project/module/level_structure.png" width="600"/>

- Api层：接口部分，提供对外的接口和数据结构。
- Implementation层：实现部分，提供对业务逻辑的实现，它往往和应用的状态、账户信息等息息相关，library为它提供具体的功能，它决定如何去加载、组织、以及展示这些功能。
- Library层：功能部分，为implementation提供一些具体的功能。

一个模块就这样可以被划分为三层，如果是更加复杂的模块，我们还有做好层与层间的解耦与通信，我们接着来看一下横向架构如何实现。

#### 2.2.2 横向架构

横向架构就是如何去处理视图、数据与业务逻辑的关系，关于这一块内容的实践，从最初的MVC、到MVP、MVVM，各种架构的目的都都是希望模块的耦合性更低、独立性更强，移植性更好。

Google自己也开了一个Repo来讨论这些框架的最佳实践，如下所示：

- [android-architecture](https://github.com/googlesamples/android-architecture)

<img src="https://github.com/BeesAndroid/BeesAndroid/blob/master/art/practice/project/module/mvp_structure.png"/>

- MVC：PC时代就有的架构方案，在Android上也是最早的方案，Activity/Fragment这些上帝角色既承担了V的角色，也承担了C的角色，小项目开发起来十分顺手，大项目就会遇到
耦合过重，Activity/Fragment类过大等问题。
- MVP：为了解决MVC耦合过重的问题，MVP的核心思想就是提供一个Presenter将视图逻辑I和业务逻辑相分离，达到解耦的目的。
- MVVM：使用ViewModel代替Presenter，实现数据与View的双向绑定，这套框架最早使用的data-binding将数据绑定到xml里，这么做在大规模应用的时候是不行的，不过数据绑定是
一个很有用的概念，后续Google又推出了ViewModel组件与LiveData组件。ViewModel组件规范了ViewModel所处的地位、生命周期、生产方式以及一个Activity下多个Fragment共享View
Model数据的问题。LiveData组件则提供了在Java层面View订阅ViewModel数据源的实现方案。

Google官方也提供了MVP的实现，这个MVP框架的核心思想如下所示：

- 使用Contract接口统一管理View接口和Presenter接口的定义，当然这个也不是一定非得这么写，并不是每个View接口和Presenter接口都可以成对出现，可能会出现一个VIew接口对应介个Presenter接口或者
一个Presenter接口对应几个View接口的情况。
- 采用Fragment实现View接口，我们知道Presenter接口主要定义的是业务逻辑，例如：加载下一页、下拉刷新、编辑、提交、删除等，这些都是在页面的生命周期方法或者setXXXListener里调用的，Fragment的生命
周期正好可以用的上，而且Fragment还可以独立的填充到其他Activity里。

官方的这套框架存在两个问题：

- 正如上面所说的View接口交由Fragment实现，但是如果一个页面由多个独立的子页面组合而成，那是不是要在这个页面添加几个Fragment，这显示是不合理的，鉴于这种情况，我们可以
退而求其次，采用自定义View的方式来实现View接口。
- 当页面增大到一定的量级的时候，就出出现大量的Presenter实现类，其实大风车现有的工程就有很多的Presenter实现类，Presenter实现类和View实现类需要相互set，以便View可以调用Presenter加载数据
，Presenter调用View刷新UI，管理这些Presenter类是个很大的问题，而且如果别人要继承你这个View，你还要告诉它在View的生命周期里如何去处理Presenter的创建和销毁，以及何时去加载数据等等。
如果出现跨部门甚至跨跨城市的合作时，沟通成本就非常的高。

总的说来，就是当业务量急剧膨胀的时候，就会需要写大量的View接口和Presenter类，而且这还牵扯到Presenter类与Activity生命周期同步的问题，在大型项目面前，这些操作都会变得十分复杂。

综上所述，一个理想的方案就是结合ViewModel组件与LiveData组件来实现MVVM框架。

- [todo-mvvm-live](https://github.com/googlesamples/android-architecture/tree/todo-mvvm-live/)
- [Lifecycle Component官方文档](https://developer.android.com/topic/libraries/architecture/guide.html)

这套框架有两个重要的原则：

- 任何不处理UI逻辑和用户交互的代码都不应该写到Activity或者Fragment中，因为Activity或者Fragment是十分脆弱的，低内存、配置发生变化、进入后台等等都可能导致它们的销毁，应该
最大限度的减低对Activity或者Fragment的依赖。
- 应该使用一个持久数据模型来驱动我们的UI，数据可以在该套模型里进行持久化，一旦Activity或者Fragment被销毁，用户数据不会丢失，这套模型专门用来处理数据逻辑，使应用的数据逻辑与视图逻辑
向分离，让应用变得更易维护。

👉 注：这里可能有人有疑问，非得用Lifecycle组件吗，利用View的onAttachToWindow()、onDetachToWindow()这些方法来模拟Activity或者Fragment的生命周期不可以吗，事实上View的生命周期在
一些特殊的场景下是不可靠的，例如：RecyclerView、ViewPager，所以我们还是需要利用Lifecycle组件来监听Activity或者Fragment的生命周期变化。

### 2.3 模块通信

解决了模块间的解耦问题，另一个就是模块间的通信问题。在一个大型的应用里很多模块都是可以独立运行甚至独立成一个App的，这就牵扯到模块间的数据交互和通信问题，例如：最常见的一种
场景就是子模块需要知道主应用里的登录信息等等，模块间的通信业可以分为两种情况：

- 进程内通信：模块都运行在同一个进程中。
- 跨进程通信：模块运行在不同的进程中。

#### 2.3.1 进程内通信

进程内通信的手段有很多种，最常见的就是EventBus，

- [EventBus](https://github.com/greenrobot/EventBus)

> EventBus 用来完成 Activities, Fragments, Threads, Services 之间的数据交互和通信。

EventBus是早期页面通信和模块通信常见的手段，它的好处是显而易见的，将事件的发布者与订阅者解耦，无需再定义一堆复杂的回调接口，但是随着工程的
膨胀，它的问题也凸显出来，具体说来：

- Event并非所有通信常见的最佳方式，它主要适合一对多的广播场景，如果业务中的通信需要一组接口时，就需要定义多个Event，代码复杂。
- 大量的Event的类，难以管理，如果应用越来越庞大，模块划分也越来越多，这个Event就变得难以维护。

但是即便这样，EventBus还是一个优秀的进程内通信的方式。

👉 注：当然除了EventBus以外，在简单的通信场景下，我们还可以选择LocalBroadcastReceiver。LocalBroadcastReceiver是一个应用内的局域广播，它也是利用一个Looper Handler维护一个
全局Map进行应用内部通信，与EventBus不同，它发送的是字符串。LocalBroadcastReceiver在面临业务膨胀的时候，也会遇到消息字符串的管理问题。

#### 2.3.2 进程间通信

跨进程通信可以借助Content Provider来完成，

- [Content Provider官方文档](https://developer.android.com/guide/topics/providers/content-providers.html)

> Content Provider 底层采用的是Binder机制，用来完成进程间的数据交互和通信。

模块通信采用Content Provider的方式来解决，一个比较常见的场景就是多模块共享登录信息，登录信息可以用Content Provider来保存，当登录状态发生变化时，可以通知到
各个模块。

通过上面的分析，我们已经完成了一个设计良好的模块，但是模块的接入仍然面临着诸多问题，例如：如何界定模块的生命周期，用户信息等如何同步，模块如何进行注册以及初始化
等问题。少量的模块，这些都不是问题，但是当模块增长到一定的数量级的时候，这个问题就会变得十分突出。

### 2.4 模块生命周期

模块生命周期的生命周期可以做如下划分：

1. 进程启动：执行模块的初始化。
2. Account初始化：执行模块用户信息同步，告知模块用户已经登录。
3. Account注销：执行模块用户信息同步，告知模块用户已经注销。
4. 进程退出：执行模块的退出。

### 2.5 模块初始化

模块的初始化一般在Application里进行，当然也有懒加载的模块，模块的初始化一般传递应用上下文信息，用户信息，配置参数等信息，这里可以考虑对模块进行自动初始化，具体
流程如下所示：

1. 添加依赖，依赖也分为两种：编译期依赖和运行期依赖，
2. 配置数据，注册服务。
3. 启动服务。

## 三 解决问题

模块化拆分不是一个简单的事情，没法一蹴而就，也不可能让团队全部停下来去做拆分重构，所以真正实施模块化需要按照以下几个步骤循序渐进的进行。

1. 心态调整

技术上的重构并不能带来短期上的收益，它是一个长时间才能显现好处的事情，你往往花费了很多时间来做这些事情，它也非常的有意义，但是老板看不到，业务上也不会带来明显的增长。所以
第一件事情，就是做好团队成员的思想工作。

事实上，大部分研发同学都还是非常有技术追求的，但是我们工程通常有很多历史遗留问题，也就是所谓的技术债，要去重构这些东西，成本是非常高的，面对这种情况在加上平时业务需求多，时间紧，大家
通常都会想：

> 重构难度这么大，出了问题怎么办，算了，别人怎么写，我也怎么写好了。

这是一个很普遍的现象，这种情况下就需要有一个有魄力的leader打响第一枪，有了第一个阶段的重构，大家看到了曙光，就会开始陆续吐槽原来的设计有多么烂，应该如何设计等等。

2. 模块拆分：对需要重构的模块进行拆分，包括代码，资源等等。
3. 灰度发布：对小部分用户推送重构版本。
4. 应用回滚：对git代码做好tag，遇到问题时随时准备回归。

## 附录

最后啰嗦几句：

1. 能用原生实现的不要用第三方库实现，如果实在需要第三方库实现，例如：图片库、网络库，也不要直接使用，要做好封装和接口隔离，方便以后做替换。
2. 页面间的继承关系一定要谨慎，除非是专门为继承而设计的页面，否则应该考虑使用组合或其他侵入性更低的方式来解决问题。
3. 项目中为某个需求提出了解决方案时，如果这种需求其他团队还可能会遇到，就要评估一下这个方案耦合性怎么样，以后能否直接给其他团队使用，较少团队间
的重复劳动。
4. 对外提供的功能尽量做好接口封装，不要直接暴露内部细节，这样日后也可以直接替换内部逻辑，而不至于影响业务方。

> 本篇文章到这里就结束了，欢迎关注我们的BeesAndroid微信公众平台，BeesAndroid致力于分享Android系统源码的设计与实现相关文章，也欢迎开源爱好者参与到BeesAndroid项目中来。

微信公众平台

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/wechat.png" width="300"/>
