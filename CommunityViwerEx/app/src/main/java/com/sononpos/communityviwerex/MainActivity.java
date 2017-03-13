package com.sononpos.communityviwerex;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.ads.MobileAds;
import com.sononpos.communityviwerex.Funtional.ThemeManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private CommunityTypePagerAdapter adapter;
    private LeftMenuItemAdapter adapterLeftMenu;
    Toolbar toolbar;
    private DrawerLayout dl;
    private View dlv;
    private AdView mAdView;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("VLog","OnStart!");
    }

    @Override
    protected void onResume() {
        Log.d("VLog","OnResume!");
        if( G.GetCommunityList().size() <= 0 ) {
            G.ReloadCommunityListFromSharedPref(getApplicationContext());
            adapter.liData = G.GetCommunityList();
            adapter.notifyDataSetChanged();
            tabs.notifyDataSetChanged();
        }

        //  테스트
        SharedPreferences setRefer = PreferenceManager
                .getDefaultSharedPreferences(this);
        int themeType = Integer.parseInt(setRefer.getString("theme_type", "0"));
        ThemeManager.SetTheme(themeType);

        RefreshTheme();

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
        setContentView(R.layout.activity_main);

        //  실제 서비스가 시작되면 주석 제거
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3598320494828213~8676238288");

        // Load an ad into the AdMob banner view.
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextSize(40);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new CommunityTypePagerAdapter(getSupportFragmentManager());
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });
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

                ThemeManager.ThemeColorObject theme = ThemeManager.GetTheme();

                int nPrevSize = G.GetCommunityList().size();

                if( G.liFiltered.contains(item.sKey) ) {
                    G.liFiltered.remove(item.sKey);
                    tvName.setTextColor(Color.parseColor(theme.LeftEnable));
                }
                else {
                    if(nPrevSize <= 1 ){
                        Toast.makeText(view.getContext(), "하나 이상은 남겨두어야 합니다.", Toast.LENGTH_SHORT);
                        return;
                    }

                    G.liFiltered.add(item.sKey);
                    tvName.setTextColor(Color.parseColor(theme.LeftDisable));
                    if( (nPrevSize-1) <= pager.getCurrentItem() ){
                        pager.setCurrentItem(pager.getCurrentItem()-1);
                    }
                }

                G.setStringArrayPref(getApplicationContext(), G.FILTERED_COMM, new ArrayList<String>(G.liFiltered));
                G.RefreshFilteredInfo();

                adapter.liData = G.GetCommunityList();
                adapter.notifyDataSetChanged();
                tabs.notifyDataSetChanged();

            }
        });


        //  Setup ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        ImageButton ibtn = (ImageButton)toolbar.findViewById(R.id.btn_leftmenu);
        ibtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dl.openDrawer(dlv);

            }
        });
        Button btnSettings = (Button)dlv.findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
            }
        });

        RefreshTheme();

        mAdView = (AdView) findViewById(R.id.adViewMain);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("AEA1198981C8725DFB7C153E9D1F2CFE")
                .build();  // An example device ID
        mAdView.loadAd(adRequest);
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
            if( nPageNum >= G.GetCommunityList().size()) {
                return POSITION_NONE;
            }

            fragment.Reload();

            if (nPageNum >= 0) {
                return nPageNum;
            } else {
                return POSITION_NONE;
            }
        }
    }

    public void RefreshTheme() {
        ThemeManager.ThemeColorObject theme = ThemeManager.GetTheme();
        toolbar.setBackgroundColor(Color.parseColor(theme.BgTitle));
        dl.setBackgroundColor(Color.parseColor(theme.BgList));
        toolbar.getRootView().setBackgroundColor(Color.parseColor(theme.BgList));

        tabs.setTextColor(Color.parseColor(theme.BasicFont));
        tabs.setBackgroundColor(Color.parseColor(theme.BgList));
        tabs.setIndicatorColor(Color.parseColor(theme.BasicFont));

        Button btnSettings = (Button)dlv.findViewById(R.id.btn_settings);
        btnSettings.setBackgroundColor(Color.parseColor(theme.BgList));
        btnSettings.setTextColor(Color.parseColor(theme.BasicFont));
    }

}
