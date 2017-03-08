package com.sononpos.communityviwerex;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.sononpos.communityviwerex.G.liFiltered;

public class MainActivity extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private CommunityTypePagerAdapter adapter;
    private LeftMenuItemAdapter adapterLeftMenu;

    public SharedPreferences shortcutSharedPref;
    public boolean isInstalled;

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("OnStart!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        adView.refreshDrawableState();
        System.out.println("OnResume!");
    }

    //  DrawerLayout
    private DrawerLayout dl;
    private View dlv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  실제 서비스가 시작되면 주석 제거
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3598320494828213~8676238288");

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();  // An example device ID
        adView.loadAd(adRequest);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextColor(Color.parseColor("#cccccc"));
        tabs.setTextSize(40);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new CommunityTypePagerAdapter(getSupportFragmentManager());
        adapter.liData = G.GetCommunityList();
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                for (Fragment f : fragments) {
                    if (f instanceof CommunityListFragment) {
                        if (((CommunityListFragment) f).getPageNum() == (position + 1)) {
                            //((CommunityListFragment)f).reload();
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //  Init DrawerLayout
        dl = (DrawerLayout)findViewById(R.id.drawer_layout);
        dlv = (View)findViewById(R.id.drawer);

        ArrayList<LeftMenuItem> leftMenuList = new ArrayList<LeftMenuItem>();
        Iterator iter = G.liCommTypeInfo.iterator();
        while(iter.hasNext()) {
            CommunityTypeInfo info = (CommunityTypeInfo)iter.next();
            LeftMenuItem item = new LeftMenuItem(info.sName, info.sKey);
            leftMenuList.add(item);
        }
        adapterLeftMenu = new LeftMenuItemAdapter(this, R.layout.leftmenuitem, leftMenuList);
        ListView listLeft = (ListView)findViewById(R.id.list_left);
        listLeft.setAdapter(adapterLeftMenu);
        listLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //  메뉴 아이템 클릭
                LeftMenuItem item =  (LeftMenuItem)parent.getItemAtPosition(position);
                TextView tvName = (TextView)view.findViewById(R.id.textViewName);

                System.out.println("Menu Click : " + item.name);
                if( G.liFiltered.contains(item.sKey) ) {
                    G.liFiltered.remove(item.sKey);
                    tvName.setTextColor(Color.parseColor("#eeeeee"));
                }
                else {
                    G.liFiltered.add(item.sKey);
                    tvName.setTextColor(Color.parseColor("#555555"));
                }

                pager.setCurrentItem(0);

                G.setStringArrayPref(getApplicationContext(), G.FILTERED_COMM, new ArrayList<String>(G.liFiltered));
                G.RefreshFilteredInfo();

                adapter.liData = G.GetCommunityList();
                adapter.notifyDataSetChanged();
                tabs.notifyDataSetChanged();

            }
        });


        //  Setup ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        ImageButton ibtn = (ImageButton)toolbar.findViewById(R.id.btn_leftmenu);
        ibtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dl.openDrawer(dlv);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("앱 종료");
        builder.setMessage("종료 하시겠습니까?");


        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

                dialog.dismiss();
            }

        });


        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Code that is <a href="http://www.numotgaming.com/series/eternal/"><span class="eternal-hover-card-container">execute</span></a>d when clicking NO
                dialog.dismiss();
            }

        });


        AlertDialog alert = builder.create();
        alert.show();
    }

    public class CommunityTypePagerAdapter extends FragmentStatePagerAdapter {

        private static final String ARG_POSITION = "position";
        ArrayList<CommunityTypeInfo> liData = new ArrayList<>();
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

            }
        };

        public CommunityTypePagerAdapter(FragmentManager fm) {
            super(fm);


        }

        @Override
        public CharSequence getPageTitle(int position) {
            return liData.get(position).sName;
        }

        @Override
        public int getCount() {
            return liData.size();
        }

        @Override
        public Fragment getItem(int position) {
            System.out.println("Fragment Adapter : getItem - " + position);
            CommunityListFragment f = new CommunityListFragment();
            Bundle b = new Bundle();
            b.putInt(ARG_POSITION, position);
            f.setArguments(b);
            return f;
        }

        @Override
        public int getItemPosition(Object object) {
            CommunityListFragment fragment = (CommunityListFragment)object;
            int nPageNum = fragment.getPageNum();

            if(nPageNum == 0 ) fragment.Reload();

            if (nPageNum >= 0) {
                return nPageNum;
            } else {
                return POSITION_NONE;
            }
        }
    }

}
