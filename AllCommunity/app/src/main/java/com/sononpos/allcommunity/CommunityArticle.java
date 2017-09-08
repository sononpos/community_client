package com.sononpos.allcommunity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.google.android.gms.ads.AdRequest;
import com.sononpos.allcommunity.AlertManager.AlertManager;
import com.sononpos.allcommunity.databinding.ActivityCommunityArticleBinding;

import im.delight.android.webview.AdvancedWebView;

public class CommunityArticle extends AppCompatActivity implements AdvancedWebView.Listener {

    ActivityCommunityArticleBinding mBind;
    private String url;
    private String title;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_community_article);
        getSupportActionBar().hide();

        mBind.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent msg = new Intent(Intent.ACTION_SEND);
                msg.addCategory(Intent.CATEGORY_DEFAULT);
                msg.putExtra(Intent.EXTRA_SUBJECT, title);
                msg.putExtra(Intent.EXTRA_TEXT, url);
                msg.putExtra(Intent.EXTRA_TITLE, "핫 커뮤니티 - " + title);
                msg.setType("text/plain");
                startActivity(Intent.createChooser(msg, "공유하기"));
            }
        });

        mBind.webview.setListener(this, this);
        mBind.webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                if(builder != null) {
                    result.cancel();
                    return true;
                }

                builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(R.string.tutorial_desc);
                builder.setMessage("삭제 되거나 잘못 된 글");
                builder.setCancelable(false);
                builder.setPositiveButton("뒤로가기", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }

                });

                AlertDialog alert = builder.create();
                alert.show();
                result.cancel();
                return true;
            }
        });

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean bTutorial = pref.getBoolean(G.KEY_TUTORIAL_COMPLETE , false);

        if(!bTutorial) {
            AlertManager.ShowOk(CommunityArticle.this, "튜토리얼 설명", "좌에서 우 슬라이드 : 닫기", "닫기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences pref_inner = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref_inner.edit();
                    editor.putBoolean(G.KEY_TUTORIAL_COMPLETE, true);
                    editor.apply();
                    dialog.dismiss();
                }
            });
        }

        boolean bAction = pref.getBoolean("webview_slide_action" , true);
        if( bAction ) {
            mBind.webview.setCallback(new SwipeWebView.SwipeCallback() {
                @Override
                public void OnRightToLeft() {
                    finish();
                }

                @Override
                public void OnLeftToRight() {
                    //
                }

                @Override
                public void closeCallback() {
                    finish();
                }
            });
        }
        Intent intent = getIntent();
        url = intent.getStringExtra("URL");
        title = intent.getStringExtra("TITLE");
        mBind.webview.loadUrl(url);

        // Load an ad into the AdMob banner view.
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("AEA1198981C8725DFB7C153E9D1F2CFE")
                .build();
        mBind.adViewWeb.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBind.webview.onResume();
        if(mBind.adViewWeb != null) {
            mBind.adViewWeb.resume();
        }
    }

    @Override
    protected void onPause() {
        mBind.webview.onPause();
        super.onPause();
        if(mBind.adViewWeb != null) {
            mBind.adViewWeb.pause();
        }
    }

    @Override
    protected void onDestroy() {
        mBind.webview.onDestroy();
        super.onDestroy();
        if(mBind.adViewWeb != null) {
            mBind.adViewWeb.destroy();
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        Log.e("WEB_ERROR", "onPageStarted : " + url);
    }

    @Override
    public void onPageFinished(String url) {
        Log.e("WEB_ERROR", "onPageFinished : " + url);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        Log.e("WEB_ERROR", "onPageError");
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {
        Log.e("WEB_ERROR", "onExternalPageRequest");
    }
}
