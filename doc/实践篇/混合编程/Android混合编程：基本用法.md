# Android混合编程：基本用法

作者：[郭孝星](https://github.com/guoxiaoxing)

校对：[郭孝星](https://github.com/guoxiaoxing)

文章状态：编辑中

**关于项目**

> [BeesAndroid](https://github.com/BeesAndroid/BeesAndroid)项目旨在通过提供一系列的工具与方法，降低阅读Android系统源码的门槛，让更多的Android工程师理解Android系统，掌握Android系统。

**文章目录**

- 一 基本用法
- 二 代码交互
- 三 性能优化

第一次阅览本系列文章，请参见[导读](https://github.com/BeesAndroid/BeesAndroid/blob/master/doc/导读.md)，更多文章请参见[文章目录](https://github.com/BeesAndroid/BeesAndroid/blob/master/README.md)。

## 一 基本用法

WebView也是Android View的一种, 我们通常用它来在应用内部展示网页, 和以往一样, 我们先来简单看一下它的基本用法。

添加网络权限

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

在布局中添加WebView

```xml
<?xml version="1.0" encoding="utf-8"?>
<WebView  xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/webview"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
/>
```

使用WebView加载网页

```
WebView myWebView = (WebView) findViewById(R.id.webview);
myWebView.loadUrl("http://www.example.com");
```

以上就是WebView的简单用法, 相比大家已经十分熟悉, 下面我们就来逐一看看WebView的其他特性。

### WebView基本组件

了解了基本用法, 我们对WebView就有了大致的印象, 下面我们来看看构建Web应用的三个重要组件。

#### WebSettings

WebSettings用来对WebView做各种设置, 你可以这样获取WebSettings:

```java
WebSettings webSettings = mWebView .getSettings();
```

WebSettings的常见设置如下所示:

JS处理

- setJavaScriptEnabled(true);  //支持js
- setPluginsEnabled(true);  //支持插件 
- setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口 

缩放处理

- setUseWideViewPort(true);  //将图片调整到适合webview的大小 
- setLoadWithOverviewMode(true); // 缩放至屏幕的大小
- setSupportZoom(true);  //支持缩放，默认为true。是下面那个的前提。
- setBuiltInZoomControls(true); //设置内置的缩放控件。 这个取决于setSupportZoom(), 若setSupportZoom(false)，则该WebView不可缩放，这个不管设置什么都不能缩放。
- setDisplayZoomControls(false); //隐藏原生的缩放控件

内容布局

- setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
- supportMultipleWindows(); //多窗口 

文件缓存

- setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存 
- setAllowFileAccess(true);  //设置可以访问文件 

其他设置

- setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
- setLoadsImagesAutomatically(true); //支持自动加载图片
- setDefaultTextEncodingName("utf-8"); //设置编码格式
- setPluginState(PluginState.OFF); //设置是否支持flash插件
- setDefaultFontSize(20); //设置默认字体大小

#### WebViewClient

WebViewClient用来帮助WebView处理各种通知, 请求事件。我们通过继承WebViewClient并重载它的方法可以实现不同功能的定制。具体如下所示:

- shouldOverrideUrlLoading(WebView view, String url) //在网页上的所有加载都经过这个方法,这个函数我们可以做很多操作。比如获取url，查看url.contains(“add”)，进行添加操作
 
- shouldOverrideKeyEvent(WebView view, KeyEvent event) //处理在浏览器中的按键事件。 

- onPageStarted(WebView view, String url, Bitmap favicon) //开始载入页面时调用的，我们可以设定一个loading的页面，告诉用户程序在等待网络响应。 

- onPageFinished(WebView view, String url) //在页面加载结束时调用, 我们可以关闭loading 条，切换程序动作。 

- onLoadResource(WebView view, String url) //在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。 

- onReceivedError(WebView view, int errorCode, String description, String failingUrl) //报告错误信息 

- doUpdateVisitedHistory(WebView view, String url, boolean isReload) //更新历史记录 

- onFormResubmission(WebView view, Message dontResend, Message resend) //应用程序重新请求网页数据

- onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host,String realm) //获取返回信息授权请求 

- onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) //让webview处理https请求。

- onScaleChanged(WebView view, float oldScale, float newScale) //WebView发生改变时调用

- onUnhandledKeyEvent(WebView view, KeyEvent event) //Key事件未被加载时调用

#### WebChromeClient

WebChromeClient用来帮助WebView处理JS的对话框、网址图标、网址标题和加载进度等。同样地, 通过继承WebChromeClient并重载它的方法也可以实现不同功能的定制, 如下所示:

- public void onProgressChanged(WebView view, int newProgress); //获得网页的加载进度，显示在右上角的TextView控件中

- public void onReceivedTitle(WebView view, String title); //获取Web页中的title用来设置自己界面中的title, 当加载出错的时候，比如无网络，这时onReceiveTitle中获取的标题为"找不到该网页",

- public void onReceivedIcon(WebView view, Bitmap icon); //获取Web页中的icon

- public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg);

- public void onCloseWindow(WebView window);

- public boolean onJsAlert(WebView view, String url, String message, JsResult result); //处理alert弹出框，html 弹框的一种方式

- public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) //处理confirm弹出框

- public boolean onJsConfirm(WebView view, String url, String message, JsResult result); //处理prompt弹出框

### WebView生命周期

#### onResume()  

WebView为活跃状态时回调，可以正常执行网页的响应。

#### onPause() 

WebView被切换到后台时回调, 页面被失去焦点, 变成不可见状态，onPause动作通知内核暂停所有的动作，比如DOM的解析、plugin的执行、JavaScript执行。

#### pauseTimers() 

当应用程序被切换到后台时回调，该方法针对全应用程序的WebView，它会暂停所有webview的layout，parsing，javascripttimer。降低CPU功耗。

#### resumeTimers()

恢复pauseTimers时的动作。

#### destroy() 

关闭了Activity时回调, WebView调用destory时, WebView仍绑定在Activity上.这是由于自定义WebView构建时传入了该Activity的context对象, 因此需要先从父
容器中移除WebView, 然后再销毁webview。

```java
mRootLayout.removeView(webView);  
mWebView.destroy();
```

###  WebView页面导航

#### 页面跳转

当我们在WebView点击链接时, 默认的WebView会直接跳转到别的浏览器中, 如果想要实现在WebView内跳转就需要设置WebViewClient, 下面我们先来
说说WebView、WebViewClient、WebChromeClient三者的区别。

- WebView: 主要负责解析和渲染网页
- WebViewClient: 辅助WebView处理各种通知和请求事件
- WebChromeClient: 辅助WebView处理JavaScript中的对话框, 网址图标和标题等

如果我们想控制不同链接的跳转方式, 我们需要继承WebViewClient重写shouldOverrideUrlLoading()方法

```java
    static class CustomWebViewClient extends WebViewClient {

        private Context mContext;

        public CustomWebViewClient(Context context) {
            mContext = context;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("github.com/guoxiaoxing")) {
                //如果是自己站点的链接, 则用本地WebView跳转
                return false;
            }
            //如果不是自己的站点则launch别的Activity来处理
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mContext.startActivity(intent);
            return true;
        }
    }
```

关于shouldOverrideUrlLoading()方法的两点说明:

1 方法返回值

返回true: Android 系统会处理URL, 一般是唤起系统浏览器。
返回false: 当前 WebView 处理URL。

由于默认放回false, 如果我们只想在WebView内处理链接跳转只需要设置mWebView.setWebViewClient(new WebViewClient())即可

```java
/** 
     * Give the host application a chance to take over the control when a new 
     * url is about to be loaded in the current WebView. If WebViewClient is not 
     * provided, by default WebView will ask Activity Manager to choose the 
     * proper handler for the url. If WebViewClient is provided, return true 
     * means the host application handles the url, while return false means the 
     * current WebView handles the url. 
     * This method is not called for requests using the POST "method". 
     * 
     * @param view The WebView that is initiating the callback. 
     * @param url The url to be loaded. 
     * @return True if the host application wants to leave the current WebView 
     *         and handle the url itself, otherwise return false. 
     */  
    public boolean shouldOverrideUrlLoading(WebView view, String url) {  
        return false;  
    }  
```

2 方法deprecated问题

shouldOverrideUrlLoading()方法在API >= 24时被标记deprecated, 它的替代方法是

```
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.toString());
            return true;
        }
```

但是public boolean shouldOverrideUrlLoading(WebView view, String url)支持更广泛的API我们在使用的时候还是它, 
关于这两个方法的讨论可以参见:

http://stackoverflow.com/questions/36484074/is-shouldoverrideurlloading-really-deprecated-what-can-i-use-instead  
http://stackoverflow.com/questions/26651586/difference-between-shouldoverrideurlloading-and-shouldinterceptrequest

#### 页面回退

Android的返回键, 如果想要实现WebView内网页的回退, 可以重写onKeyEvent()方法。

```java
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    // Check if the key event was the Back button and if there's history
    if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
        myWebView.goBack();
        return true;
    }
    // If it wasn't the Back key or there's no web page history, bubble up to the default
    // system behavior (probably exit the activity)
    return super.onKeyDown(keyCode, event);
}
```

#### 页面滑动

关于页面滑动, 我们在做下拉刷新等功能时, 经常会去判断WebView是否滚动到顶部或者滚动到底部。

我们先来看一看三个判断高度的方法

```java
getScrollY();
```

该方法返回的是当前可见区域的顶端距整个页面顶端的距离,也就是当前内容滚动的距离.

```java
getHeight();
getBottom();
```

该方法都返回当前WebView这个容器的高度

```
getContentHeight(); 
```

返回的是整个html的高度, 但并不等同于当前整个页面的高度, 因为WebView有缩放功能, 所以当前整个页面的高度实际上应该是原始html的高度
再乘上缩放比例. 因此, 判断方法是:

```java
if (webView.getContentHeight() * webView.getScale() == (webView.getHeight() + webView.getScrollY())) {
    //已经处于底端
}

if(webView.getScrollY() == 0){
    //处于顶端
}
```

以上这个方法也是我们常用的方法, 不过从API 17开始, mWebView.getScale()被标记为deprecated

>This method was deprecated in API level 17. This method is prone to inaccuracy due to race conditions 
between the web rendering and UI threads; prefer onScaleChanged(WebView, 

因为scale的获取可以用一下方式:

```java
public class CustomWebView extends WebView {

public CustomWebView(Context context) {
    super(context);
    setWebViewClient(new WebViewClient() {
        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
            mCurrentScale = newScale
        }
    });
}
```

关于mWebView.getScale()的讨论可以参见:

https://developer.android.com/reference/android/webkit/WebView.html

http://stackoverflow.com/questions/16079863/how-get-webview-scale-in-android-4

### WebView缓存实现

在项目中如果使用到WebView控件, 当加载html页面时, 会在/data/data/包名目录下生成database与cache两个文件夹。
请求的url记录是保存在WebViewCache.db, 而url的内容是保存在WebViewCache文件夹下。

控制缓存行为

```java
WebSettings webSettings = mWebView.getSettings();
//优先使用缓存
webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); 
//只在缓存中读取
webSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY);
/不使用缓存
WwebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
```

清除缓存

```java
clearCache(true); //清除网页访问留下的缓存，由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
clearHistory (); //清除当前webview访问的历史记录，只会webview访问历史记录里的所有记录除了当前访问记录.
clearFormData () //这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据。
```

### WebView Cookies

添加Cookies

```java
public void synCookies() {
    if (!CacheUtils.isLogin(this)) return;
    CookieSyncManager.createInstance(this);
    CookieManager cookieManager = CookieManager.getInstance();
    cookieManager.setAcceptCookie(true);
    cookieManager.removeSessionCookie();//移除
    String cookies = PreferenceHelper.readString(this, AppConfig.COOKIE_KEY, AppConfig.COOKIE_KEY);
    KJLoger.debug(cookies);
    cookieManager.setCookie(url, cookies);
    CookieSyncManager.getInstance().sync();
}
```

清除Cookies

```java
CookieManager.getInstance().removeSessionCookie();
```

### WebView本地资源访问

当我们在WebView中加载出从web服务器上拿取的内容时，是无法访问本地资源的，如assets目录下的图片资源，因为这样的行为属于跨域行为（Cross-Domain），而WebView是禁止
的。解决这个问题的方案是把html内容先下载到本地，然后使用loadDataWithBaseURL加载html。这样就可以在html中使用 file:///android_asset/xxx.png 的链接来引用包里
面assets下的资源了。

```java
private void loadWithAccessLocal(final String htmlUrl) {
    new Thread(new Runnable() {
        public void run() {
            try {
                final String htmlStr = NetService.fetchHtml(htmlUrl);
                if (htmlStr != null) {
                    TaskExecutor.runTaskOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadDataWithBaseURL(htmlUrl, htmlStr, "text/html", "UTF-8", "");
                        }
                    });
                    return;
                }
            } catch (Exception e) {
                Log.e("Exception:" + e.getMessage());
            }

            TaskExecutor.runTaskOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onPageLoadedError(-1, "fetch html failed");
                }
            });
        }
    }).start();
}
```

**注意**

- 从网络上下载html的过程应放在工作线程中
- html下载成功后渲染出html的步骤应放在UI主线程，不然WebView会报错
- html下载失败则可以使用我们前面讲述的方法来显示自定义错误界面