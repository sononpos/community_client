package com.sononpos.communityviwerex;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.ads.MobileAds;
import com.sononpos.communityviwerex.Funtional.ThemeManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private CommunityTypePagerAdapter adapter;
    private LeftMenuItemAdapter adapterLeftMenu;
    private CommunityTypeInfo recent;
    private Toolbar toolbar;
    private DrawerLayout dl;
    private View dlv;
    private AdView mAdView;
    ImageButton btnListComm;
    LinearLayout dropdownListLayout;

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
            ResetArticleList();
            adapter.notifyDataSetChanged();
            tabs.notifyDataSetChanged();
        }

        //  테스트
        SharedPreferences setRefer = PreferenceManager
                .getDefaultSharedPreferences(this);
        int themeType = Integer.parseInt(setRefer.getString("theme_type", "0"));
        ThemeManager.SetTheme(themeType);
        int themeFontType = Integer.parseInt(setRefer.getString("theme_font_type", "1"));
        ThemeManager.SetThemeFont(themeFontType);

        RefreshTheme();
        ResetDropDownList();

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
        btnListComm = (ImageButton)findViewById(R.id.btn_list_comm);
        dropdownListLayout = (LinearLayout)findViewById(R.id.grid_list_comm);
        adapter = new CommunityTypePagerAdapter(getSupportFragmentManager());
        recent = new CommunityTypeInfo("recent", "최근 본 글", -1);

        ResetArticleList();
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
                        Log.e("Error", "onPageSelected" + ((CommunityListFragment) f).getPageNum());
                        CommunityListFragment cf = ((CommunityListFragment) f);
                        if (G.IsShowRecent(getApplicationContext()) && cf.getPageNum() == 0) {
                            cf.Reload();
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //  Setup ActionBar
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        InitLeftMenu();
        RefreshTheme();
        ResetDropDownList();

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
        ThemeManager.ThemeFontObject themeFont = ThemeManager.GetFont();
        //toolbar.setBackgroundColor(Color.parseColor(theme.BgTitle));
        dl.setBackgroundColor(Color.parseColor(theme.BgList));
        toolbar.getRootView().setBackgroundColor(Color.parseColor(theme.BgList));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tabs.setTextColor(Color.parseColor(theme.BasicFont));
        tabs.setBackgroundColor(Color.parseColor(theme.BgList));
        tabs.setIndicatorColor(Color.parseColor(theme.BasicFont));
        tabs.setIndicatorHeight(8);
        int fontSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,themeFont.TabFont,getApplicationContext().getResources().getDisplayMetrics());
        tabs.setTextSize(fontSize);
        tabs.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);

        tabs.setUnderlineColor(Color.TRANSPARENT);
        tabs.setDividerColor(Color.parseColor(theme.BasicFont));

        Button btnSettings = (Button)dlv.findViewById(R.id.btn_settings);
        btnSettings.setBackgroundColor(Color.parseColor(theme.BgList));
        btnSettings.setTextColor(Color.parseColor(theme.BasicFont));
    }

    private void ResetArticleList() {
        adapter.liData.clear();
        ArrayList<CommunityTypeInfo> renew = new ArrayList<>(G.GetCommunityList());
        if(G.IsShowRecent(getApplicationContext())){
            renew.add(0,recent);
        }
        adapter.liData.addAll(renew);
    }

    private void InitLeftMenu() {
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
                        Toast.makeText(view.getContext(), R.string.at_least_one_community, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    G.liFiltered.add(item.sKey);
                    tvName.setTextColor(Color.parseColor(theme.LeftDisable));
                    if( (nPrevSize-1) <= pager.getCurrentItem() ){
                        pager.setCurrentItem(pager.getCurrentItem()-1);
                    }
                }

                G.setStringArrayPref(getApplicationContext(), G.KEY_FILTERED_COMM, new ArrayList<String>(G.liFiltered));
                G.RefreshFilteredInfo();

                try{
                    ResetArticleList();
                    adapter.notifyDataSetChanged();
                    tabs.notifyDataSetChanged();
                }
                catch(NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        ImageButton ibtn = (ImageButton)toolbar.findViewById(R.id.btn_leftmenu);
        ibtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dl.openDrawer(dlv);
                closeDropdownList();

            }
        });
        Button btnSettings = (Button)dlv.findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        dl.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                ResetDropDownList();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void ResetDropDownList() {
        dropdownListLayout.setBackgroundColor(Color.parseColor(ThemeManager.GetTheme().BgList));
        if(dropdownListLayout != null) {
            dropdownListLayout.removeAllViewsInLayout();
            closeDropdownList();

            int listCnt = adapter.liData.size();
            LinearLayout newLinear = null;
            for(int i = 0 ; i < listCnt ; ++i) {
                if(i % 4 == 0) {
                    newLinear = new LinearLayout(getApplicationContext());
                    dropdownListLayout.addView(newLinear);
                }

                final CommunityTypeInfo info = adapter.liData.get(i);

                Button btn = new Button(getApplicationContext());
                final int btn_height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, btn_height, 1.0f);
                p.setMargins(2,2,2,2);
                btn.setText(info.sName);
                btn.setLayoutParams(p);
                btn.setTag(i);
                btn.setBackgroundColor(Color.parseColor(ThemeManager.GetTheme().BgList));
                btn.setTextColor(Color.parseColor(ThemeManager.GetTheme().BasicFont));
                int fontSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4,getApplicationContext().getResources().getDisplayMetrics());
                btn.setTextSize(fontSize);
                btn.setSingleLine(true);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int idx = (int)(((Button)v).getTag());
                        pager.setCurrentItem(idx);
                        closeDropdownList();
                    }
                });

                newLinear.addView(btn);
            }

            btnListComm.setImageResource(R.drawable.arrow_down);
            btnListComm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dropdownListLayout.isShown()) {
                        closeDropdownList();
                    }
                    else {
                        openDropdownList();
                    }
                }
            });
        }
    }

    private void openDropdownList() {
        dropdownListLayout.setVisibility(View.VISIBLE);
        dropdownListLayout.setClickable(true);
        btnListComm.setImageResource(R.drawable.arrow_up);
    }

    private void closeDropdownList() {
        dropdownListLayout.setVisibility(View.GONE);
        dropdownListLayout.setClickable(false);
        btnListComm.setImageResource(R.drawable.arrow_down);
    }

}
