package com.sononpos.allcommunity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.sononpos.allcommunity.AlertManager.AlertManager;
import com.sononpos.allcommunity.databinding.ActivityCommunityArticleBinding;

import im.delight.android.webview.AdvancedWebView;

public class CommunityArticleActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    ActivityCommunityArticleBinding mBind;
    private String url;
    private String title;
    AlertDialog.Builder builder;
    private RewardedVideoAd mRewardAd;
    private boolean bAdRemoved = false;
    private RewardedVideoAdListener RewardListener = new RewardedVideoAdListener() {
        @Override
        public void onRewardedVideoAdLoaded() {
            Log.i("RewardAds", "onRewardedVideoAdLoaded");
        }

        @Override
        public void onRewardedVideoAdOpened() {
            Log.i("RewardAds", "onRewardedVideoAdOpened");
        }

        @Override
        public void onRewardedVideoStarted() {
            Log.i("RewardAds", "onRewardedVideoStarted");
            mBind.faMenu.close(true);
        }

        @Override
        public void onRewardedVideoAdClosed() {
            Log.i("RewardAds", "onRewardedVideoAdClosed");
            LoadRewardedVideoAd();
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {
            Log.i("RewardAds", "onRewarded");
            Global.getInstance().getAdsTimeChecker().SaveNow(getApplicationContext());
            DestroyAds();
            mBind.faMenu.close(true);
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {
            Log.i("RewardAds", "onRewardedVideoAdLeftApplication");
            mBind.faMenu.close(true);
            ReloadAds();
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            Log.i("RewardAds", "onRewardedVideoAdFailedToLoad : " + i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_community_article);
        getSupportActionBar().hide();
        setupWebView();
        setupOptions();
        setupFAB();
        setupStatusBar();
        setupAds();
        loadURL();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBind.webview.onResume();
        if(mBind.adViewWeb != null) {
            mBind.adViewWeb.resume();
        }
        mRewardAd.resume(this);

        if(bAdRemoved && Global.getInstance().getAdsTimeChecker().IsTimeout(this)) {
            ReloadAds();
            bAdRemoved = false;
        }else if(!Global.getInstance().getAdsTimeChecker().IsTimeout(this)) {
            DestroyAds();
        }
    }

    @Override
    protected void onPause() {
        mBind.webview.onPause();
        super.onPause();
        if(mBind.adViewWeb != null) {
            mBind.adViewWeb.pause();
        }
        mRewardAd.pause(this);
    }

    @Override
    protected void onDestroy() {
        mBind.webview.onDestroy();
        super.onDestroy();
        if(mBind.adViewWeb != null) {
            mBind.adViewWeb.destroy();
        }
        mRewardAd.destroy(this);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        Log.i("CAAWebView", "onPageStarted : " + url);
    }

    @Override
    public void onPageFinished(String url) {
        Log.i("CAAWebView", "onPageFinished : " + url);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        Log.i("CAAWebView", "onPageError" + url);
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
        Log.i("CAAWebView", "onDownloadRequested : " + url);
    }

    @Override
    public void onExternalPageRequest(String url) {
        Log.i("CAAWebView", "onExternalPageRequest : " + url);
    }



    protected void setupWebView() {
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
    }

    protected void setupOptions() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean bTutorial = pref.getBoolean(Global.KEY_TUTORIAL_COMPLETE , false);

        if(!bTutorial) {
            AlertManager.ShowOk(CommunityArticleActivity.this, "튜토리얼 설명", "좌에서 우 슬라이드 : 닫기", "닫기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences pref_inner = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = pref_inner.edit();
                    editor.putBoolean(Global.KEY_TUTORIAL_COMPLETE, true);
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
    }

    protected void setupAds() {
        // Load an ad into the AdMob banner view.
        if(BuildConfig.DEBUG) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(getResources().getString(R.string.admob_test_device_id))
                    .build();
            mBind.adViewWeb.loadAd(adRequest);
        }
        else {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mBind.adViewWeb.loadAd(adRequest);
        }

        mRewardAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardAd.setRewardedVideoAdListener(RewardListener);
    }

    protected void setupFAB() {
        mBind.fabItemShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent msg = new Intent(Intent.ACTION_SEND);
                msg.addCategory(Intent.CATEGORY_DEFAULT);
                msg.putExtra(Intent.EXTRA_SUBJECT, title);
                msg.putExtra(Intent.EXTRA_TEXT, url);
                msg.putExtra(Intent.EXTRA_TITLE, "핫 커뮤니티 - " + title);
                msg.setType("text/plain");
                startActivity(Intent.createChooser(msg, "공유하기"));
                mBind.faMenu.close(false);
            }
        });
    }

    protected void loadURL() {
        Intent intent = getIntent();
        url = intent.getStringExtra("URL");
        title = intent.getStringExtra("TITLE");
        mBind.webview.loadUrl(url);
    }

    protected void DestroyAds() {
        mBind.adViewWeb.destroy();
        mBind.adViewWeb.setVisibility(View.GONE);
        bAdRemoved = true;
    }

    protected void ReloadAds() {
        mBind.adViewWeb.destroy();
        mBind.adViewWeb.setVisibility(View.VISIBLE);
        mRewardAd.destroy(this);

        if(BuildConfig.DEBUG) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("3776568EFE655D6E6A2B7FA4F2B8F521")
                    .build();  // An example device ID
            mBind.adViewWeb.loadAd(adRequest);
        }
        else {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mBind.adViewWeb.loadAd(adRequest);
        }

        mRewardAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardAd.setRewardedVideoAdListener(RewardListener);

        LoadRewardedVideoAd();
    }

    protected void LoadRewardedVideoAd() {
//        if(!mRewardAd.isLoaded()) {
//            if(BuildConfig.DEBUG) {
//                mRewardAd.loadAd(getString(R.string.reward_ad_unit_id), new AdRequest.Builder().addTestDevice(getResources().getString(R.string.admob_test_device_id)).build());
//            }
//            else {
//                mRewardAd.loadAd(getString(R.string.reward_ad_unit_id), new AdRequest.Builder().build());
//            }
//        }
    }

    protected void setupStatusBar() {
        Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.mainColor));
        }
    }
}
