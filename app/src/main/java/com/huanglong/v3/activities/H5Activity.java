package com.huanglong.v3.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.huanglong.v3.BaseActivity;
import com.huanglong.v3.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by bin on 2018/6/16.
 * H5页面
 */
@ContentView(R.layout.activity_invitation_h5)
public class H5Activity extends BaseActivity {

    @ViewInject(R.id.title_name)
    private TextView tv_title;
    @ViewInject(R.id.invitation_web)
    private WebView web_view;

    private String url;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void logic() {

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        tv_title.setText(title);
        initWebView(url);

    }

    @Event(value = {R.id.title_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

    /**
     * 初始化设置webView
     *
     * @param url
     */
    private void initWebView(String url) {

//        web_view.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.removeAllViews();
//                view.loadUrl(url);
//                return true;
//            }
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//            }
//        });
//
//        web_view.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int progress) {
//                super.onProgressChanged(view, progress);
//            }
//
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//
//            }
//
//        });
//
//        web_view.setDownloadListener(new DownloadListener() {
//            @Override
//            public void onDownloadStart(String url, String userAgent,
//                                        String contentDisposition, String mimetype,
//                                        long contentLength) {
//            }
//        });


        WebSettings webSettings = web_view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        web_view.loadUrl(url);
//        web_view.addJavascriptInterface(click, "AndroidWebView");
    }


}
