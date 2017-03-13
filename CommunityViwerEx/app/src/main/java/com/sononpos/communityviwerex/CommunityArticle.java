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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sononpos.communityviwerex.Funtional.ThemeManager;

import im.delight.android.webview.AdvancedWebView;

public class CommunityArticle extends Activity implements AdvancedWebView.Listener {

    private SwipeWebView mWebView;
    private String url;
    private String title;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_article);

        mWebView = (SwipeWebView)findViewById(R.id.webview);
        mWebView.getRootView().setBackgroundColor(Color.parseColor(ThemeManager.GetTheme().BgList));
        mWebView.setListener(this, this);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean bTutorial = pref.getBoolean("tutorial_complete" , false);

        if(!bTutorial) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("튜토리얼 설명");
            builder.setMessage("-> 슬라이드 : 닫기\r\n<- 슬라이드 : 공유하기");
            builder.setCancelable(false);
            builder.setPositiveButton("다신 안 봄", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences pref_inner = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref_inner.edit();
                    editor.putBoolean("tutorial_complete", true);
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
                    View rootView = mWebView.getRootView();
                    ClipboardManager clipboardManager = (ClipboardManager)rootView.getContext().getSystemService(rootView.getContext().CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("label", url);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(rootView.getContext(), "클립보드에 복사 되었습니다", Toast.LENGTH_SHORT).show();

                    Intent msg = new Intent(Intent.ACTION_SEND);
                    msg.addCategory(Intent.CATEGORY_DEFAULT);
                    msg.putExtra(Intent.EXTRA_SUBJECT, title);
                    msg.putExtra(Intent.EXTRA_TEXT, url);
                    msg.putExtra(Intent.EXTRA_TITLE, title);
                    msg.setType("text/plain");
                    startActivity(Intent.createChooser(msg, "공유하기"));
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
