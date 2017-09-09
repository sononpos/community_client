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
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
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
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("3776568EFE655D6E6A2B7FA4F2B8F521")
                .build();  // An example device ID
        mBind.adViewMain.loadAd(adRequest);

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
                mRewardAd.loadAd(getString(R.string.reward_ad_unit_id_test), new AdRequest.Builder().addTestDevice("3776568EFE655D6E6A2B7FA4F2B8F521").build());
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Log.i("RewardAds", "onRewarded");
                mBind.fabItemHideAdmob.setEnabled(false);
                mBind.adViewMain.destroy();
                mBind.adViewMain.setVisibility(View.GONE);
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
        mRewardAd.loadAd(getString(R.string.reward_ad_unit_id_test), new AdRequest.Builder().addTestDevice("3776568EFE655D6E6A2B7FA4F2B8F521").build());
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
                if(mRewardAd.isLoaded()) {
                    mRewardAd.show();
                }
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
        return new ArticlesListFragment(position);
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