package com.sononpos.allcommunity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.sononpos.allcommunity.AlertManager.AlertManager;
import com.sononpos.allcommunity.ArticleType.ArticleTypeInfo;
import com.sononpos.allcommunity.ArticlesFragment.ArticlesListFragment;
import com.sononpos.allcommunity.ArticlesFragment.NewsFragment;
import com.sononpos.allcommunity.RecyclerAdapter.MainLeftMenuRecyclerAdapter;
import com.sononpos.allcommunity.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mBind;
    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardAd;
    private boolean bAdRemoved = false;
    private boolean exit = false;
    private RewardedVideoAdListener RewardListener = new RewardedVideoAdListener() {
        @Override
        public void onRewardedVideoAdLoaded() {
            Log.i("RewardAds", "onRewardedVideoAdLoaded");
            mBind.fabItemHideAdmob.setEnabled(true);
            mBind.fabItemHideAdmob.setVisibility(View.VISIBLE);
            mBind.faMenu.close(true);
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
            G.adsTimeChecker.SaveNow(getApplicationContext());
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
            mBind.fabItemHideAdmob.setEnabled(false);
            mBind.fabItemHideAdmob.refreshDrawableState();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBind.setActivity(this);
            getSupportActionBar().hide();

        setupTabs();        // 상단 탭
        setupFAB();         // 플로팅 버튼 설정
        setupLeftMenu();    // 왼쪽 메뉴
        setupStatusBar();   // 최상단 상태바
        setupAd();          // 광고
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i("MainActivity", "onResume");

        ArrayList<ArticleTypeInfo> tablist = G.GetCommunityList(true);
        if( tablist == null || tablist.size() == 0 ) {
            Intent intent = new Intent(this, LoadingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            return;
        }

        if(mBind.adViewMain != null) {
            mBind.adViewMain.resume();
            mBind.adViewMain.refreshDrawableState();
        }

        mRewardAd.resume(this);

        if(bAdRemoved && G.adsTimeChecker.IsTimeout(this)) {
            ReloadAds();
            bAdRemoved = false;
        }
        else if(!G.adsTimeChecker.IsTimeout(this)) {
            DestroyAds();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        if(mBind.adViewMain != null) {
            mBind.adViewMain.pause();
        }

        mRewardAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mBind.adViewMain != null) {
            mBind.adViewMain.destroy();
        }

        mRewardAd.destroy(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "한번 더 누르면 종료 됩니다",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }

    protected void setupAd() {
        //  실제 서비스가 시작되면 주석 제거
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.admob_app_id));
        if(BuildConfig.DEBUG) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(getResources().getString(R.string.admob_test_device_id))
                    .build();  // An example device ID
            mBind.adViewMain.loadAd(adRequest);
        }
        else {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mBind.adViewMain.loadAd(adRequest);
        }

        //  전면광고 초기화
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.full_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mRewardAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardAd.setRewardedVideoAdListener(RewardListener);

        LoadRewardedVideoAd();

        if(!G.adsTimeChecker.IsTimeout(this)) {
            DestroyAds();
        }
    }

    protected void setupTabs() {
        mBind.pager.setAdapter(new CommListPagerAdapter(getSupportFragmentManager()));
        mBind.tabs.setViewPager(mBind.pager);
    }

    protected  void setupFAB() {
        mBind.fabItemSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this,
                        Pair.create((View)mBind.pager, "otherImage")
                );

                Intent settings = new Intent(MainActivity.this, SettingsRenewActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    startActivity(settings, options.toBundle());
                }
                else {
                    startActivity(settings);
                }
            }
        });

        mBind.fabItemHideAdmob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertManager.ShowYesNo(MainActivity.this, "알림", "영상 광고를 다 보시면 6시간 동안 배너광고가 제거됩니다.", "본다", "됐네요",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                    {
                                        if(mRewardAd.isLoaded()) {
                                            mRewardAd.show();
                                        }
                                    }
                                    break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                    {

                                    }
                                    break;
                                }
                            }
                        });
            }
        });

        mBind.faMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBind.drawer.closeDrawer(mBind.navView);
                if(mBind.faMenu.isOpened()) {
                    mBind.faMenu.close(true);
                }
                else
                    mBind.faMenu.open(true);
            }
        });
    }

    protected void setupLeftMenu() {
        mBind.leftlistview.setHasFixedSize(true);
        mBind.leftlistview.setLayoutManager(new LinearLayoutManager(mBind.getRoot().getContext()));
        mBind.leftlistview.setAdapter(new MainLeftMenuRecyclerAdapter(mBind));
        mBind.leftlistview.addItemDecoration(new SimpleDividerItemDecoration(mBind.getRoot().getContext()));
        if(!G.options.IsTutorialEnd(getApplicationContext())) {
            mBind.tutorial.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mBind.tutorial.setVisibility(View.GONE);
                    G.options.SetTutorialEnd(getApplicationContext());
                    return true;
                }
            });
        }
        else {
            mBind.tutorial.setVisibility(View.GONE);
        }

        mBind.drawer.addDrawerListener(new ActionBarDrawerToggle(this, mBind.drawer,R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mBind.faMenu.close(false);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                G.SaveFiltered(getApplicationContext());
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }

        });
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

    protected void DestroyAds() {
        mBind.fabItemHideAdmob.setEnabled(false);
        mBind.fabItemHideAdmob.setVisibility(View.GONE);
        mBind.fabItemHideAdmob.refreshDrawableState();
        mBind.adViewMain.destroy();
        mBind.adViewMain.setVisibility(View.GONE);
        bAdRemoved = true;
    }

    protected void ReloadAds() {
        mBind.adViewMain.destroy();
        mBind.adViewMain.setVisibility(View.VISIBLE);
        mRewardAd.destroy(this);
        mBind.fabItemHideAdmob.setEnabled(false);

        if(BuildConfig.DEBUG) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(getResources().getString(R.string.admob_test_device_id))
                    .build();  // An example device ID
            mBind.adViewMain.loadAd(adRequest);
        }
        else {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mBind.adViewMain.loadAd(adRequest);
        }

        mRewardAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardAd.setRewardedVideoAdListener(RewardListener);

        LoadRewardedVideoAd();
    }

    protected void LoadRewardedVideoAd() {
        if(!mRewardAd.isLoaded()) {
            if(BuildConfig.DEBUG) {
                mRewardAd.loadAd(getString(R.string.reward_ad_unit_id), new AdRequest.Builder().addTestDevice(getResources().getString(R.string.admob_test_device_id)).build());
            }
            else {
                mRewardAd.loadAd(getString(R.string.reward_ad_unit_id), new AdRequest.Builder().build());
            }
        }
    }

    public void onBtnLeftMenuOpen(View view) {
        mBind.drawer.openDrawer(mBind.navView);
    }
}

class CommListPagerAdapter extends FragmentPagerAdapter {
    public CommListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return G.GetCommunityList(false).size();
    }

    @Override
    public Fragment getItem(int position) {
        ArticleTypeInfo info = G.GetCommunityList(false).get(position);
        switch(info.GetType()) {
            case ArticleTypeInfo.TYPE_COMMUNITY:
            {
                Fragment f = new ArticlesListFragment();
                Bundle b = new Bundle();
                b.putInt("POS", position);
                f.setArguments(b);
                return f;
            }

            case ArticleTypeInfo.TYPE_MOST_NEWS:
            {
                Fragment f = new NewsFragment();
                Bundle b = new Bundle();
                b.putInt("POS", position);
                f.setArguments(b);
                return f;
            }
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return G.GetCommunityList(false).get(position).GetName();
    }

    @Override
    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }
}