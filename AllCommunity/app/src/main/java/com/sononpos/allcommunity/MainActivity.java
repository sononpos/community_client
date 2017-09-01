package com.sononpos.allcommunity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.ads.MobileAds;
import com.sononpos.allcommunity.Funtional.ThemeManager;
import com.sononpos.allcommunity.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding mBind;
    private LeftMenuItemAdapter adapterLeftMenu;
    private CommunityTypeInfo recent;
    private AdView mAdView;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //  실제 서비스가 시작되면 주석 제거
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3598320494828213~8676238288");

        mAdView = (AdView) findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("AEA1198981C8725DFB7C153E9D1F2CFE")
                .build();  // An example device ID
        mAdView.loadAd(adRequest);
    }

    private boolean exit = false;
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
}
