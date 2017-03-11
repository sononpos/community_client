package com.sononpos.communityviwerex;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sononpos.communityviwerex.Funtional.ThemeManager;

import im.delight.android.webview.AdvancedWebView;

public class CommunityArticle extends Activity implements AdvancedWebView.Listener {

    private SwipeWebView mWebView;
    private String url;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_article);

        mWebView = (SwipeWebView)findViewById(R.id.webview);
        mWebView.getRootView().setBackgroundColor(Color.parseColor(ThemeManager.GetTheme().BgList));
        mWebView.setListener(this, this);
        mWebView.setCallback(new SwipeWebView.SwipeCallback() {
            @Override
            public void OnRightToLeft() {
                finish();
            }

            @Override
            public void OnLeftToRight() {
                View rootView = mWebView.getRootView();
                ClipboardManager clipboardManager = (ClipboardManager)rootView.getContext().getSystemService(rootView.getContext().CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", url);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(rootView.getContext(), "클립보드에 복사 되었습니다", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        url = intent.getStringExtra("URL");
        mWebView.loadUrl(url);

        // Load an ad into the AdMob banner view.
        adView = (AdView) findViewById(R.id.adViewWeb);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("AEA1198981C8725DFB7C153E9D1F2CFE")
                .build();
        adView.loadAd(adRequest);
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
        if(adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
        if(adView != null) {
            adView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        super.onDestroy();
        if(adView != null) {
            adView.destroy();
        }
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
