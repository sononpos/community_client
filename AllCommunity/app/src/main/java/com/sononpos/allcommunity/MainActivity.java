package com.sononpos.allcommunity;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sononpos.allcommunity.Funtional.ThemeManager;
import com.sononpos.allcommunity.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mBind;
    private AdView mAdView;
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupAd();      // 광고
        setupTabs();    // 상단 탭
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

        //  테스트
        SharedPreferences setRefer = PreferenceManager
                .getDefaultSharedPreferences(this);
        int themeType = Integer.parseInt(setRefer.getString("theme_type", "0"));
        ThemeManager.SetTheme(themeType);
        int themeFontType = Integer.parseInt(setRefer.getString("theme_font_type", "1"));
        ThemeManager.SetThemeFont(themeFontType);

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

        mAdView = (AdView) findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("AEA1198981C8725DFB7C153E9D1F2CFE")
                .build();  // An example device ID
        mAdView.loadAd(adRequest);
    }

    protected void setupTabs() {
        mBind.pager.setAdapter(new CommListPagerAdapter(getSupportFragmentManager()));
        mBind.tabs.setViewPager(mBind.pager);
    }
}

class CommListPagerAdapter extends FragmentPagerAdapter {
    String[] aTitles = { "A", "B", "C" };
    public CommListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return aTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        return new ArticlesListFragment();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return aTitles[position];
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}