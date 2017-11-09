package com.warm.pullrefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.warm.library.PullToRefreshLayout;


public class WebActivity extends AppCompatActivity implements PullToRefreshLayout.OnRefreshListener {

    private PullToRefreshLayout pull;
    private WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        pull= (PullToRefreshLayout) this.findViewById(R.id.pull);
        pull.setOnRefreshListener(this);
        wv= (WebView) this.findViewById(R.id.wv);
        WebSettings webSettings = wv.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(false);


        wv.loadUrl("https://www.baidu.com/");
    }

    @Override
    public void refresh() {
        wv.loadUrl("https://www.baidu.com/");
        pull.refreshEnd();

    }
}
