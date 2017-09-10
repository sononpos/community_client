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
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.sononpos.allcommunity.AlertManager.AlertManager;
import com.sononpos.allcommunity.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mBind;
    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardAd;
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBind.setActivity(this);
            getSupportActionBar().hide();
        setupAd();          // 광고
        setupTabs();        // 상단 탭
        setupFAB();         // 플로팅 버튼 설정
        setupLeftMenu();    // 왼쪽 메뉴
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        if( G.GetCommunityList(false).size() <= 0 ) {
            G.ReloadCommunityListFromSharedPref(getApplicationContext());
        }

        if(mBind.adViewMain != null) {
            mBind.adViewMain.resume();
            mBind.adViewMain.refreshDrawableState();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        if(mBind.adViewMain != null) {
            mBind.adViewMain.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mBind.adViewMain != null) {
            mBind.adViewMain.destroy();
        }
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
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3598320494828213~8676238288");
        if(BuildConfig.DEBUG) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("3776568EFE655D6E6A2B7FA4F2B8F521")
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
        mRewardAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
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
                if(BuildConfig.DEBUG) {
                    mRewardAd.loadAd(getString(R.string.reward_ad_unit_id), new AdRequest.Builder().addTestDevice("3776568EFE655D6E6A2B7FA4F2B8F521").build());
                }
                else {
                    mRewardAd.loadAd(getString(R.string.reward_ad_unit_id), new AdRequest.Builder().build());
                }
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
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Log.i("RewardAds", "onRewardedVideoAdFailedToLoad : " + i);
            }
        });
        if(BuildConfig.DEBUG) {
            mRewardAd.loadAd(getString(R.string.reward_ad_unit_id_test), new AdRequest.Builder().addTestDevice("3776568EFE655D6E6A2B7FA4F2B8F521").build());
        }
        else {
            mRewardAd.loadAd(getString(R.string.reward_ad_unit_id_test), new AdRequest.Builder().build());
        }

        if(!G.adsTimeChecker.IsTimeout(getApplicationContext())) {
            DestroyAds();
        }
    }

    protected void setupTabs() {
        mBind.pager.setAdapter(new CommListPagerAdapter(getSupportFragmentManager()));
        mBind.tabs.setViewPager(mBind.pager);
        mBind.tabs.setIndicatorColorResource(R.color.mainColor);
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

    protected void DestroyAds() {
        mBind.fabItemHideAdmob.setEnabled(false);
        mBind.adViewMain.destroy();
        mBind.adViewMain.setVisibility(View.GONE);
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
        Fragment f = new ArticlesListFragment();
        Bundle b = new Bundle();
        b.putInt("POS", position);
        f.setArguments(b);
        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return G.GetCommunityList(false).get(position).sName;
    }

    @Override
    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }
}