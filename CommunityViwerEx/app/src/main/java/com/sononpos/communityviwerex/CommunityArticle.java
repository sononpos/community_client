package com.sononpos.communityviwerex;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;

import im.delight.android.webview.AdvancedWebView;

public class CommunityArticle extends Activity implements AdvancedWebView.Listener {

    private AdvancedWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_article);

        mWebView = (AdvancedWebView)findViewById(R.id.webview);
        mWebView.setListener(this,this);

        Intent intent = getIntent();
        String sURL = intent.getStringExtra("URL");
        mWebView.loadUrl(sURL);
    }

    @Override
    public void onBackPressed() {
        if(!mWebView.onBackPressed()) { return; }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}
