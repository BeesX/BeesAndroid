# Android混合编程：原生通信

作者：[郭孝星](https://github.com/guoxiaoxing)

校对：[郭孝星](https://github.com/guoxiaoxing)

文章状态：编辑中

**关于项目**

> [BeesAndroid](https://github.com/BeesAndroid/BeesAndroid)项目旨在通过提供一系列的工具与方法，降低阅读Android系统源码的门槛，让更多的Android工程师理解Android系统，掌握Android系统。

**文章目录**

### Android原生方案

关于WebView中Java代码和JS代码的交互实现, Android给了一套原生的方案, 我们先来看看原生的用法。后面我们还会讲到其他的开源方法。

JavaScript代码和Android代码是通过addJavascriptInterface()来建立连接的, 我们来看下具体的用法。

1 设置WebView支持JavaScript

```java
webView.getSettings().setJavaScriptEnabled(true);
```

2 在Android工程里定义一个接口

```java
public class WebAppInterface {
    Context mContext;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }
}
```

**注意**: API >= 17时, 必须在被JavaScript调用的Android方法前添加@JavascriptInterface注解, 否则将无法识别。

3 在Android代码中将该接口添加到WebView

```java
WebView webView = (WebView) findViewById(R.id.webview);
webView.addJavascriptInterface(new WebAppInterface(this), "Android");
```

这个"Android"就是我们为这个接口取的别名, 在JavaScript就可以通过Android.showToast(toast)这种方式来调用此方法。

4 在JavaScript中调用Android方法

```js
<input type="button" value="Say hello" onClick="showAndroidToast('Hello Android!')" />

<script type="text/javascript">
    function showAndroidToast(toast) {
        Android.showToast(toast);
    }
</script>
```

在JavaScript中我们不用再去实例化WebAppInterface接口, WebView会自动帮我们完成这一工作, 使它能够为WebPage所用。

**注意**:

由于addJavascriptInterface()给予了JS代码控制应用的能力, 这是一项非常有用的特性, 但同时也带来了安全上的隐患, 

> Using addJavascriptInterface() allows JavaScript to control your Android application. This can be a very useful feature or a dangerous 
security issue. When the HTML in the WebView is untrustworthy (for example, part or all of the HTML is provided by an unknown person or 
process), then an attacker can include HTML that executes your client-side code and possibly any code of the attacker's choosing. As such, 
you should not use addJavascriptInterface() unless you wrote all of the HTML and JavaScript that appears in your WebView. You should also 
not allow the user to navigate to other web pages that are not your own, within your WebView (instead, allow the user's default browser 
application to open foreign links—by default, the user's web browser opens all URL links, so be careful only if you handle page navigation
as described in the following section).
 
下面正式引入我们在项目中常用的两套开源的替代方案

### jockeyjs开源方案

[jockeyjs](https://github.com/tcoulter/jockeyjs)是一套IOS/Android双平台的Native和JS交互方法, 比较适合用在项目中。

>Library to facilitate communication between iOS apps and JS apps running inside a UIWebView

jockeyjs对Native和JS的交互做了优美的封装, 事件的发送与接收都可以通过send()和on()来完成。我们先简单的看一下Event的发送与接收。

Sending events from app to JavaScript

```java
// Send an event to JavaScript, passing a payload
jockey.send("event-name", webView, payload);

//With a callback to execute after all listeners have finished
jockey.send("event-name", webView, payload, new JockeyCallback() {
    @Override
    public void call() {
        //Your execution code
    }
});
```

Receiving events from app in JavaScript

```java
// Listen for an event from iOS, but don't notify iOS we've completed processing
// until an asynchronous function has finished (in this case a timeout).
Jockey.on("event-name", function(payload, complete) {
  // Example of event'ed handler.
  setTimeout(function() {
    alert("Timeout over!");
    complete();
  }, 1000);
});
```

Sending events from JavaScript to app

```java
// Send an event to iOS.
Jockey.send("event-name");

// Send an event to iOS, passing an optional payload.
Jockey.send("event-name", {
  key: "value"
});

// Send an event to iOS, pass an optional payload, and catch the callback when all the
// iOS listeners have finished processing.
Jockey.send("event-name", {
  key: "value"
}, function() {
  alert("iOS has finished processing!");
});
```

Receiving events from JavaScript in app

```java
//Listen for an event from JavaScript and log a message when we have receied it.
jockey.on("event-name", new JockeyHandler() {
    @Override
    protected void doPerform(Map<Object, Object> payload) {
        Log.d("jockey", "Things are happening");
    }
});

//Listen for an event from JavaScript, but don't notify the JavaScript that the listener has completed
//until an asynchronous function has finished
//Note: Because this method is executed in the background, if you want the method to interact with the UI thread
//it will need to use something like a android.os.Handler to post to the UI thread.
jockey.on("event-name", new JockeyAsyncHandler() {
    @Override
    protected void doPerform(Map<Object, Object> payload) {
        //Do something asynchronously
        //No need to called completed(), Jockey will take care of that for you!
    }
});


//We can even chain together several handlers so that they get processed in sequence.
//Here we also see an example of the NativeOS interface which allows us to chain some common
//system handlers to simulate native UI interactions.
jockey.on("event-name", nativeOS(this)
            .toast("Event occurred!")
            .vibrate(100), //Don't forget to grant permission
            new JockeyHandler() {
                @Override
                protected void doPerform(Map<Object, Object> payload) {
                }
            }
);

//...More Handlers


//If you would like to stop listening for a specific event
jockey.off("event-name");

//If you would like to stop listening to ALL events
jockey.clear();
```

通过上面的代码, 我们对jockeyjs的使用有了大致的理解, 下面我们具体来看一下在项目中的使用。

1 依赖配置

下载代码: https://github.com/tcoulter/jockeyjs, 将JockeyJS.Android导入到工程中。

2 jockeyjs配置

jockeyjs有两种使用方式

方式一:

只在一个Activity中使用jockey或者多Activity共享一个jockey实例

```java
//Declare an instance of Jockey
Jockey jockey;

//The WebView that we will be using, assumed to be instantiated either through findViewById or some method of injection.
WebView webView;

WebViewClient myWebViewClient;

@Override
protected void onStart() {
    super.onStart();

    //Get the default JockeyImpl
    jockey = JockeyImpl.getDefault();

    //Configure your webView to be used with Jockey
    jockey.configure(webView);

    //Pass Jockey your custom WebViewClient
    //Notice we can do this even after our webView has been configured.
    jockey.setWebViewClient(myWebViewClient)

    //Set some event handlers
    setJockeyEvents();

    //Load your webPage
    webView.loadUrl("file:///your.url.com");
}
```

方式二:

另一种就是把jockey当成一种全局的Service来用, 这种方式下我们可以在多个Activity之间甚至整个应用内共享handler. 当然我们同样需要
把jockey的生命周期和应用的生命周期绑定在一起。

```java
//First we declare the members involved in using Jockey

//A WebView to interact with
private WebView webView;

//Our instance of the Jockey interface
private Jockey jockey;

//A helper for binding services
private boolean _bound;

//A service connection for making use of the JockeyService
private ServiceConnection _connection = new ServiceConnection() {
    @Override
    public void onServiceDisconnected(ComponentName name) {
        _bound = false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        JockeyBinder binder = (JockeyBinder) service;

        //Retrieves the instance of the JockeyService from the binder
        jockey = binder.getService();

        //This will setup the WebView to enable JavaScript execution and provide a custom JockeyWebViewClient
        jockey.configure(webView);

        //Make Jockey start listening for events
        setJockeyEvents();

        _bound = true;

        //Redirect the WebView to your webpage.
        webView.loadUrl("file:///android_assets/index.html");
    }

}

///....Other member variables....////


//Then we bind the JockeyService to our activity through a helper function in our onStart method
@Override
protected void onStart() {
    super.onStart();
    JockeyService.bind(this, _connection);
}

//In order to bind this with the Android lifecycle we need to make sure that the service also shuts down at the appropriate time.
@Override
protected void onStop() {
    super.onStop();
    if (_bound) {
        JockeyService.unbind(this, _connection);
    }
}
```


以上便是jockeyjs的大致用法.


> 本篇文章到这里就结束了，欢迎关注我们的BeesAndroid微信公众平台，BeesAndroid致力于分享Android系统源码的设计与实现相关文章，也欢迎开源爱好者参与到BeesAndroid项目中来。

微信公众平台

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/wechat.png" width="300"/>