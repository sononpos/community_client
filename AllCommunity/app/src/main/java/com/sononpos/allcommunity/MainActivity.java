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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sononpos.allcommunity.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mBind;
    private AdView mAdView;
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getSupportActionBar().hide();
        setupAd();      // 광고
        setupTabs();    // 상단 탭
        setupFAB();     // 플로팅 버튼 설정
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        if( G.GetCommunityList().size() <= 0 ) {
            G.ReloadCommunityListFromSharedPref(getApplicationContext());
        }

        if(mAdView != null) {
            mAdView.resume();
            mAdView.refreshDrawableState();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        if(mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mAdView != null) {
            mAdView.destroy();
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
                .addTestDevice("AEA1198981C8725DFB7C153E9D1F2CFE")
                .build();  // An example device ID
        mBind.adViewMain.loadAd(adRequest);
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
                mAdView.destroy();
                mAdView.setVisibility(View.GONE);
                mBind.fabItemHideAdmob.hideButtonInMenu(true);
            }
        });
    }
}

class CommListPagerAdapter extends FragmentPagerAdapter {
    public CommListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return G.GetCommunityList().size();
    }

    @Override
    public Fragment getItem(int position) {
        return new ArticlesListFragment(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return G.GetCommunityList().get(position).sName;
    }
}