package com.sononpos.communityviwerex;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sononpos.communityviwerex.Funtional.ThemeManager;

import im.delight.android.webview.AdvancedWebView;

public class CommunityArticle extends AppCompatActivity implements AdvancedWebView.Listener {

    private SwipeWebView mWebView;
    private String url;
    private String title;
    private AdView adView;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_article);

        ImageButton ibtn = (ImageButton)findViewById(R.id.btn_share);
        ibtn.setOnClickListener(new View.OnClickListener() {

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

        mWebView = (SwipeWebView)findViewById(R.id.webview);
        mWebView.getRootView().setBackgroundColor(Color.parseColor(ThemeManager.GetTheme().BgList));
        mWebView.setListener(this, this);
        mWebView.setWebChromeClient(new WebChromeClient(){
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.tutorial_desc);
            builder.setMessage("좌에서 우 슬라이드 : 닫기");
            builder.setCancelable(false);
            builder.setPositiveButton("다신 안 봄", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences pref_inner = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref_inner.edit();
                    editor.putBoolean(G.KEY_TUTORIAL_COMPLETE, true);
                    editor.apply();
                    dialog.dismiss();
                }

            });

            AlertDialog alert = builder.create();
            alert.show();
        }

        boolean bAction = pref.getBoolean("webview_slide_action" , true);
        if( bAction ) {
            mWebView.setCallback(new SwipeWebView.SwipeCallback() {
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
        mWebView.loadUrl(url);

        // Load an ad into the AdMob banner view.
        adView = (AdView) findViewById(R.id.adViewWeb);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("AEA1198981C8725DFB7C153E9D1F2CFE")
                .build();
        adView.loadAd(adRequest);
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
