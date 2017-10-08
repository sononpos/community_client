package com.sononpos.allcommunity;

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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.fsn.cauly.CaulyAdInfo;
import com.fsn.cauly.CaulyAdInfoBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.sononpos.allcommunity.Ads.CaulyAdsManager;
import com.sononpos.allcommunity.ArticleType.ArticleTypeInfo;
import com.sononpos.allcommunity.ArticlesFragment.ArticlesListFragment;
import com.sononpos.allcommunity.ArticlesFragment.NewsFragment;
import com.sononpos.allcommunity.RecyclerAdapter.MainLeftMenuRecyclerAdapter;
import com.sononpos.allcommunity.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mBind;
    CaulyAdsManager caulyAdsMan;
    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardAd;
    private boolean bAdRemoved = false;
    private boolean exit = false;
    private RewardedVideoAdListener RewardListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBind.setActivity(this);
        getSupportActionBar().hide();

        setupTabs();        // 상단 탭
        //setupFAB();         // 플로팅 버튼 설정
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

        if(     Global.getInstance() == null ||
                Global.getInstance().getListMan() == null ) {
            Intent intent = new Intent(this, LoadingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
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
            //mBind.adViewMain.loadAd(adRequest);
        }
        else {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            //mBind.adViewMain.loadAd(adRequest);
        }

        //  전면광고 초기화
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.full_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        RewardListener = new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                Log.i("RewardAds", "onRewardedVideoAdLoaded");
                //mBind.btnNomoreAds.setEnabled(true);
            }

            @Override
            public void onRewardedVideoAdOpened() {
                Log.i("RewardAds", "onRewardedVideoAdOpened");
            }

            @Override
            public void onRewardedVideoStarted() {
                Log.i("RewardAds", "onRewardedVideoStarted");
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
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                Log.i("RewardAds", "onRewardedVideoAdLeftApplication");
                ReloadAds();
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Log.i("RewardAds", "onRewardedVideoAdFailedToLoad : " + i);
            }
        };

//        mRewardAd = MobileAds.getRewardedVideoAdInstance(this);
//        mRewardAd.setRewardedVideoAdListener(RewardListener);
//
//        LoadRewardedVideoAd();

//        if(!Global.getInstance().getAdsTimeChecker().IsTimeout(this)) {
//            DestroyAds();
//        }

        CaulyAdInfo adInfo = new CaulyAdInfoBuilder(getString(R.string.cauly_app_id)).
                effect("RightSlide").
                bannerHeight("Fixed_50").
                build();

        caulyAdsMan = new CaulyAdsManager();
        mBind.xmladview.setAdInfo(adInfo);
        mBind.xmladview.setAdViewListener(caulyAdsMan);
    }

    protected void setupTabs() {
        mBind.pager.setAdapter(new CommListPagerAdapter(getSupportFragmentManager()));
        mBind.tabs.setViewPager(mBind.pager);
    }

    protected void setupLeftMenu() {
        mBind.leftlistview.setHasFixedSize(true);
        mBind.leftlistview.setLayoutManager(new LinearLayoutManager(mBind.getRoot().getContext()));
        mBind.leftlistview.setAdapter(new MainLeftMenuRecyclerAdapter(mBind));
        mBind.leftlistview.addItemDecoration(new SimpleDividerItemDecoration(mBind.getRoot().getContext()));
        mBind.tutorial.setVisibility(View.GONE);

        mBind.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mBind.drawer.addDrawerListener(new ActionBarDrawerToggle(this, mBind.drawer,R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if(Global.getInstance().getListMan() != null) {
                    Global.getInstance().getListMan().saveFiltered(getApplicationContext());
                }
                else {
                    Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }

        });

        mBind.btnSettings.setOnClickListener(new View.OnClickListener() {
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

        //mBind.btnNomoreAds.setEnabled(false);
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
        //mBind.adViewMain.destroy();
        //mBind.adViewMain.setVisibility(View.GONE);
        //bAdRemoved = true;
    }

    protected void ReloadAds() {
        //mBind.adViewMain.destroy();
        //mBind.adViewMain.setVisibility(View.VISIBLE);
        //mRewardAd.destroy(this);

//        if(BuildConfig.DEBUG) {
//            AdRequest adRequest = new AdRequest.Builder()
//                    .addTestDevice(getResources().getString(R.string.admob_test_device_id))
//                    .build();  // An example device ID
//            //mBind.adViewMain.loadAd(adRequest);
//        }
//        else {
//            AdRequest adRequest = new AdRequest.Builder()
//                    .build();
//            //mBind.adViewMain.loadAd(adRequest);
//        }
//
//        mRewardAd = MobileAds.getRewardedVideoAdInstance(this);
//        mRewardAd.setRewardedVideoAdListener(RewardListener);

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
    Global.ArticleListManager listman;
    public CommListPagerAdapter(FragmentManager fm) {
        super(fm);
        listman = Global.getInstance().getListMan();
    }

    @Override
    public int getCount() {
        if( listman == null ) return 0;
        return listman.getCommunityList(false).size();
    }

    @Override
    public Fragment getItem(int position) {
        if( listman == null ) return null;
        ArticleTypeInfo info = listman.getCommunityList(false).get(position);
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
        if(listman == null ) return "";
        return listman.getCommunityList(false).get(position).GetName();
    }

    @Override
    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }
}