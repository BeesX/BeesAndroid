# Android混合编程：性能优化

作者：[郭孝星](https://github.com/guoxiaoxing)

校对：[郭孝星](https://github.com/guoxiaoxing)

文章状态：编辑中

**关于项目**

> [BeesAndroid](https://github.com/BeesAndroid/BeesAndroid)项目旨在通过提供一系列的工具与方法，降低阅读Android系统源码的门槛，让更多的Android工程师理解Android系统，掌握Android系统。

**文章目录**

## 三 性能优化

### 优化网页加载速度

默认情况html代码下载到WebView后，webkit开始解析网页各个节点，发现有外部样式文件或者外部脚本文件时，会异步发起网络请求下载文件，但如果
在这之前也有解析到image节点，那势必也会发起网络请求下载相应的图片。在网络情况较差的情况下，过多的网络请求就会造成带宽紧张，影响到css或
js文件加载完成的时间，造成页面空白loading过久。解决的方法就是告诉WebView先不要自动加载图片，等页面finish后再发起图片加载。

设置WebView, 先禁止加载图片

```java
WebSettings webSettings = mWebView.getSettings();

//图片加载
if(Build.VERSION.SDK_INT >= 19){
    webSettings.setLoadsImagesAutomatically(true);
}else {
    webSettings.setLoadsImagesAutomatically(false);
}
```

覆写WebViewClient的onPageFinished()方法, 页面加载结束后再加载图片

```java
@Override
public void onPageFinished(WebView view, String url) {
    super.onPageFinished(view, url);
    if (!view.getSettings().getLoadsImagesAutomatically()) {
        view.getSettings().setLoadsImagesAutomatically(true);
    }
}
```

**注意**: 4.4以上系统在onPageFinished时再恢复图片加载时,如果存在多张图片引用的是相同的src时，会只有一个image标签得到加载，因而对于这样的系统我们就先直接加载。

### 硬件加速页面闪烁问题

4.0以上的系统我们开启硬件加速后，WebView渲染页面更加快速，拖动也更加顺滑。但有个副作用就是，当WebView视图被整体遮住一块，然后突然恢复时（比如使用SlideMenu将WebView从侧边
滑出来时），这个过渡期会出现白块同时界面闪烁。解决这个问题的方法是在过渡期前将WebView的硬件加速临时关闭，过渡期后再开启，如下所示:

过度前关闭硬件加速

```java
if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB){
    mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
}
```

过度前开启硬件加速

```java
if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB){
    mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
}
```

以上就是本篇文章的全部内容, 大致就说这么多,  在实际的项目中我们通常会自己去封装一个H5Activity用来统一显示H5页面, 下面就提供了完整的H5Activity, 封装了WebView各种特性与jockeyjs代码交互。
               
该H5Activity提供WebView常用设置、H5页面解析、标题解析、进度条显示、错误页面展示、重新加载等功能。可以拿去稍作改造, 用于自己的项目中。
               
```java
package com.guoxiaoxing.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.jockeyjs.Jockey;
import com.jockeyjs.JockeyImpl;

public class H5Activity extends AppCompatActivity {

    public static final String H5_URL = "H5_URL";
    private static final String JOCKEY_EVENT_NAME = "JOCKEY_EVENT_NAME";
    private static final String TAG = H5Activity.class.getSimpleName();

    private Toolbar mToolbar;
    private ProgressBar mProgressBar;

    private Jockey mJockey;
    private WebView mWebView;
    private WebViewClient mWebViewClient;
    private WebChromeClient mWebChromeClient;

    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_h5);
        setupView();
        setupSettings();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupJockey();
        setupData();
    }

    private void setupView() {
        mToolbar = (Toolbar) findViewById(R.id.h5_toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.h5_progressbar);
        mWebView = (WebView) findViewById(R.id.h5_webview);
    }

    private void setupSettings() {

        mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);

        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setSupportZoom(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setLoadsImagesAutomatically(true);

        //JS
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setDomStorageEnabled(true);


        //缓存
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            String wvcc = info.getTypeName();
            Log.d(TAG, "current network: " + wvcc);
            mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            Log.d(TAG, "No network is connected, use cache");
            mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        if (Build.VERSION.SDK_INT >= 16) {
            mWebSettings.setAllowFileAccessFromFileURLs(true);
            mWebSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        if (Build.VERSION.SDK_INT >= 12) {
            mWebSettings.setAllowContentAccess(true);
        }

        setupWebViewClient();
        setupWebChromeClient();
    }

    private void setupJockey() {
        mJockey = JockeyImpl.getDefault();
        mJockey.configure(mWebView);
        mJockey.setWebViewClient(mWebViewClient);
        mJockey.setOnValidateListener(new Jockey.OnValidateListener() {
            @Override
            public boolean validate(String host) {
                return "yourdomain.com".equals(host);
            }
        });

        //TODO set your event handler
        mJockey.on(JOCKEY_EVENT_NAME, new EventHandler());
    }

    private void setupData() {
        mUrl = getIntent().getStringExtra(H5_URL);
        if (TextUtils.isEmpty(mUrl)) {
            //TODO show error page
        } else {
            mWebView.loadUrl(mUrl);
        }
    }

    private void setupWebViewClient() {
        mWebViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //TODO 处理URL, 例如对指定的URL做不同的处理等
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        };
        mWebView.setWebViewClient(mWebViewClient);
    }

    private void setupWebChromeClient() {
        mWebChromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mToolbar.setTitle(title);

            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        };
        mWebView.setWebChromeClient(mWebChromeClient);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
```

> 本篇文章到这里就结束了，欢迎关注我们的BeesAndroid微信公众平台，BeesAndroid致力于分享Android系统源码的设计与实现相关文章，也欢迎开源爱好者参与到BeesAndroid项目中来。

微信公众平台

<img src="https://github.com/BeesAndroid/BeesAndroid/raw/master/art/wechat.png" width="300"/>