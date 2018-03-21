# Android应用优化：兼容适配实践指南

**关于作者**

>郭孝星，程序员，吉他手，主要从事Android平台基础架构方面的工作，欢迎交流技术方面的问题，可以去我的[Github](https://github.com/guoxiaoxing)提issue或者发邮件至guoxiaoxingse@163.com与我交流。

第一次阅览本系列文章，请参见[导读](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/导读.md)，更多文章请参见[文章目录](https://github.com/BeesAndroid/BeesAndroid/blob/master/README.md)。

## 一 Android Studio Profiler

Android Studio 3.0 提供了全新的Profiler工具来分析应用的CPU、内存和网络的使用情况，可以跟踪函数来记录代码的执行时间，采集堆栈数据，查看内存分配以及查看网络状态等，功能
十分强大。

Android Studio Profiler 官方文档：https://developer.android.com/studio/profile/android-profiler.html

它的界面构造图如下所示：

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/practice/performance/android_studio_profile_structure.png"/>

CPU、内存和网络数据的展示都是通过Event时间线实时展示的，如果你想查看某个指标的详情，只需点击当前图表即可，如下所示：

CPU分析器

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/practice/performance/android_studio_profile_cpu.png"/>

内存分析器

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/practice/performance/android_studio_profile_memory.png"/>

网络分析器

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/practice/performance/android_studio_profile_network.png"/>

### 1.1 CPU分析器

> CPU分析器可以帮助我们实时的检查应用的CPU使用率，可以跟踪记录函数，帮助我们调试和优化应用代码，降低CPU使用率可以获得更加流畅的用户体验，延长电池续航，还可以
让我们的应用在一些旧设备上依然保持良好的性能。

CPU分析器界面如下所示：

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/practice/performance/android_studio_cpu_prefiler.png"/>

我们来讲一下上述小红圈数字代表的含义：

- 红圈1：显示应用中在其生命周期不同状态间转换的Activity，而且包含了用户与设备交互的各种Event，例如：屏幕旋转Event。
- 红圈2：CPU时间线，实时显示当前应用的CPU的使用率和总线程数，以及其他进程的CPU使用率。
- 红圈3：线程时间线，不同的颜色代表不同的含义，绿色代表当前线程处于活动状态或者准备使用CPU（运行中，可运行），黄色代表线程处于活动
状态，但它正在等待一个IO操作，然后才能完成它的工作，灰色代表线程正在休眠状态或者没有消耗任何CPU时间，当线程需要访问可用资源的时候会
发生这种情况。
- 红圈4：函数跟踪配置，默认有两种配置，Sampled在应用执行期间捕获调用栈，这种配置下如果在捕获调用栈的时候进入了一个函数，在结束之前
退出了该函数，则跟踪器不会记录该函数。Instrumented会在应用执行期间给每个函数打上开始和结束的时间戳，记录每个函数的时间信息和CPU使用率。除此之外，我们
还可以自定义配置。
- 红圈5：点击开始跟踪函数调用，再次点击结束函数调用。

我们来看看如何去跟踪函数调用栈，当点击跟踪按钮就可以开始跟踪，再次点击结束跟踪，跳出以下界面：

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/practice/performance/android_studio_cpu_prefiler.png"/>

- 红圈1：时间范围，用以选择跟踪的时间范围。
- 红圈2：时间戳，记录开始跟踪和结束跟踪的时间戳。
- 红圈3：跟踪窗口，显示具体的跟踪信息。
- 红圈4：以图表或者调用链的的形式显示跟踪信息，有Call Chart、Flame Chart、Top Down和Bottom Up四种。
- 红圈5：函数消耗的时间，有两种，Wall clock time表示实际经过的时间，Thread time表示Wall clock time减去线程没有消耗CPU的部分时间，即得出的是真正占用CPU的时间。

根据数据可以用图表或者调用链来表示，如下所示：

Call Chart：提供函数跟踪的图表表示形式，水平轴表示函数调用的时间段和时间，并妍垂直轴显示其被调用者，橙色表示系统API，绿色表示应用API，蓝色表示第三方API（包括Java API）。

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/practice/performance/call_chart.png"/>

Flame Chart：提供了一个倒置的Call Chart，功能和Call Chart相同。

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/practice/performance/flame_chart.png"/>

Top Down：展示了一个函数调用列表，它是一个树型结构。

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/practice/performance/bottom_up.png"/>

Bottom Up：展示了一个函数调用列表，它按照CPU消耗时间的最多（或者最少）来排序函数。

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/practice/performance/android_studio_cpu_prefiler.png"/>

除此之外，我们也可以通过 Recording Configurations 自定义跟踪配置。

### 1.2 内存分析器

内存分析器可以用来实时展示各种内存使用的情况以及GC的情况等。

内存分析器界面如下所示：

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/practice/performance/memory_profiler.png"/>

- 红圈1：强制执行GC。
- 红圈2：捕获堆转储备。
- 红圈3：跟踪内存的分配情况。
- 红圈4：放大/缩小时间线
- 红圈5：开启实时内存预览。
- 红圈6：Event时间线
- 红圈7：内存使用时间线
- 红圈1：

整个界面实时显示各种内存的使用情况：

- Java：从 Java 或 Kotlin 代码分配的对象内存。
- Native：从 C 或 C++ 代码分配的对象内存。
- Graphics：图形缓冲区队列向屏幕显示像素（包括 GL 表面、GL 纹理等等）所使用的内存。 （请注意，这是与 CPU 共享的内存，不是 GPU 专用内存。）
- Stack： 应用中的原生堆栈和 Java 堆栈使用的内存。 这通常与您的应用运行多少线程有关。
- Code：应用用于处理代码和资源（如 dex 字节码、已优化或已编译的 dex 码、.so 库和字体）的内存。
- Other：应用使用的系统不确定如何分类的内存。
- Allocated：应用分配的 Java/Kotlin 对象数。 它没有计入 C 或 C++ 中分配的对象。

内存分析器也可以针对函数对内存的使用情况进行跟踪，如下所示：

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/practice/performance/memory_profiler_record.png"/>                 

我们还可以点击上面的dunp java heap按钮来捕获堆转储，来帮助我们分析内存分配和内存泄漏相关信息，如下所示：

在类列表中，我们可以查看以下信息：

- Heap Count：堆中的实例数。
- Shallow Size：此堆中所有实例的总大小（以字节为单位）。
- Retained Size：为此类的所有实例而保留的内存总大小（以字节为单位）。

在类列表顶部，我们可以使用左侧下拉列表在以下堆转储之间进行切换：

- Default heap：系统未指定堆时。
- App heap：您的应用在其中分配内存的主堆。
- Image heap：系统启动映像，包含启动期间预加载的类。 此处的分配保证绝不会移动或消失。
- Zygote heap：写时复制堆，其中的应用进程是从 Android 系统中派生的。

默认情况下，此堆中的对象列表按类名称排列。 我们可以使用其他下拉列表在以下排列方式之间进行切换：

- Arrange by class：基于类名称对所有分配进行分组。
- Arrange by package：基于软件包名称对所有分配进行分组。
- Arrange by callstack：将所有分配分组到其对应的调用堆栈。 此选项仅在记录分配期间捕获堆转储时才有效。 即使如此，堆中的对象也很可能是在您开始记录之前分配的，因此这些分配会首先显示，且只按类名称列出。

默认情况下，此列表按 Retained Size 列排序。 您可以点击任意列标题以更改列表的排序方式。

在 Instance View 中，每个实例都包含以下信息：

Depth：从任意 GC 根到所选实例的最短 hop 数。
Shallow Size：此实例的大小。
Retained Size：此实例支配的内存大小（根据 dominator 树）。

另外，堆转储信息还可以被到处成文件，点击Export heap dump as HPROF file按钮可以将堆转储信息导出成HPROF文件，但是如果我们想要用其他工具（例如：MAT）分析HPROF文件，还要将其
转换成Java SE的HPROF文件，如下所示：

```java
hprof-conv heap-original.hprof heap-converted.hprof
```

除此之外我们还可以调用以下方法在代码里创建堆转储信息，如下所示：

```java
Debug.dumpHprofData() 
```

### 1.3 网络分析器

网络分析器就比较简单了，用来实时显示网络请求的情况，网络的速度，接收和发出的数据量等信息，如下所示：

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/practice/performance/network_profiler.png"/>    

- 红圈1：无线功耗状态（低/高）
- 红圈2：时间线
- 红圈3：指定时间段段内收发的文件名称、大小、类型、状态和时间。
- 红圈4：文件详细信息


## 二  Systrace

Systrace

Systrace 官方文档：https://developer.android.com/studio/command-line/systrace.html

## 三 TraceView

TraceView可以用图形的形式来展示Trace Log，展示代码的执行时间、次数以及调用栈，便于我们分析。

TraceView 官方文档：https://developer.android.com/studio/profile/traceview.html

如何为应用生成跟踪日志呢，也很简单，如下所示：

```java
// 在开始跟踪的地方调用该方法
Debug.startMethodTracing();

// 在结束跟踪的地方调用该方法
Debug.startMethodTracing();
```

Trace文件一般放在sdcard/Android/data/包名目录下，如下所示：

双击即可打开，如下所示：

