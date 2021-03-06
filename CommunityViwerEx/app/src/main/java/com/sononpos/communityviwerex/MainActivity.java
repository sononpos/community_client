package com.sononpos.communityviwerex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sononpos.communityviwerex.Funtional.ThemeManager;
import com.sononpos.communityviwerex.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements OnStartDragListener{
    ActivityMainBinding mBind;
    private CommunityTypePagerAdapter adapter;
    private CommunityTypeInfo recent;
    LinearLayout dropdownListLayout;
    ItemTouchHelper mTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //  실제 서비스가 시작되면 주석 제거
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3598320494828213~8676238288");

        // Load an ad into the AdMob banner view.
        mBind.tabs.setTextSize(40);
        dropdownListLayout = (LinearLayout)findViewById(R.id.grid_list_comm);
        adapter = new CommunityTypePagerAdapter(getSupportFragmentManager());
        recent = new CommunityTypeInfo("recent", "최근 본 글", -1);

        ResetArticleList();
        mBind.pager.setAdapter(adapter);
        mBind.tabs.setViewPager(mBind.pager);

        InitLeftMenu();
        RefreshTheme();
        ResetDropDownList();

        if( Global.obj().isKor ) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("AEA1198981C8725DFB7C153E9D1F2CFE")
                    .build();  // An example device ID
            getAdView().loadAd(adRequest);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  테스트
        SharedPreferences setRefer = PreferenceManager
                .getDefaultSharedPreferences(this);
        int themeType = Integer.parseInt(setRefer.getString("theme_type", "0"));
        ThemeManager.SetTheme(themeType);
        int themeFontType = Integer.parseInt(setRefer.getString("theme_font_type", "1"));
        ThemeManager.SetThemeFont(themeFontType);

        boolean bReload = true;
        do {
            if(Global.obj() == null) break;
            TabItemManager timan = Global.obj().getTabItemManager();
            if(timan == null) break;
            if(timan.getListWithoutFiltered() == null) break;

            bReload = false;

            //return Global.obj().getTabItemManager().getListWithoutFiltered().size();
        }while(false);

        if(bReload) {
            ReloadApp();
            return;
        }

        RefreshTheme();
        ResetDropDownList();

        if(getAdView() != null) {
            getAdView().resume();
            getAdView().refreshDrawableState();
        }
    }

    @Override
    protected void onPause() {
        if(getAdView() != null) {
            getAdView().pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(getAdView() != null) {
            getAdView().destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
        public CommunityTypePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Global.obj().getTabItemManager().getListWithoutFiltered().get(position).getName();
        }

        @Override
        public int getCount() {
            do {
                if(Global.obj() == null) break;
                TabItemManager timan = Global.obj().getTabItemManager();
                if(timan == null) break;
                if(timan.getListWithoutFiltered() == null) break;

                return Global.obj().getTabItemManager().getListWithoutFiltered().size();
            }while(false);

            ReloadApp();
            return 0;
        }

        @Override
        public Fragment getItem(int position) {
            final TabItemManager timan = Global.obj().getTabItemManager();
            final ArrayList<TabItem> aList = timan.getList();
            TabItem item = aList.get(position);

            Fragment f = null;
            switch(item.getType()) {
                case TabItem.IT_COMMUNITY:
                    f = new CommunityListFragment();
                    break;

                case TabItem.IT_RECENT:
                    f = new RecentFragment();
                    break;

                case TabItem.IT_FAVORATE:
                    f = new FavorateFragment();
                    break;
            }

            if(f == null) {
                finish();
            }

            Bundle b = new Bundle();
            b.putInt(ARG_POSITION, position);
            f.setArguments(b);
            return f;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public void RefreshTheme() {
        ThemeManager.ThemeColorObject theme = ThemeManager.GetTheme();
        ThemeManager.ThemeFontObject themeFont = ThemeManager.GetFont();
        //toolbar.setBackgroundColor(Color.parseColor(theme.BgTitle));
        mBind.ivSetting.setBackgroundColor(Color.parseColor(theme.BgList));
        mBind.drawerLayout.setBackgroundColor(Color.parseColor(theme.BgList));
        mBind.listLeft.setBackgroundColor(Color.parseColor(theme.BgList));
        mBind.tabs.setTextColor(Color.parseColor(theme.BasicFont));
        mBind.tabs.setBackgroundColor(Color.parseColor(theme.BgList));
        mBind.tabs.setIndicatorColor(Color.parseColor(theme.BasicFont));
        mBind.tabs.setIndicatorHeight(8);
        int fontSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,themeFont.TabFont,getApplicationContext().getResources().getDisplayMetrics());
        mBind.tabs.setTextSize(fontSize);
        mBind.tabs.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);

        mBind.tabs.setUnderlineColor(Color.TRANSPARENT);
        mBind.tabs.setDividerColor(Color.parseColor(theme.BasicFont));

        mBind.btnSettings.setBackgroundColor(Color.parseColor(theme.BgList));
        mBind.btnSettings.setTextColor(Color.parseColor(theme.BasicFont));
    }

    private void ResetArticleList() {
//        if(G.IsShowRecent(getApplicationContext())){
//            renew.add(0,recent);
//        }
    }

    private void InitLeftMenu() {
        //  Init DrawerLayout
        ArrayList<LeftMenuItem> leftMenuList = new ArrayList<LeftMenuItem>();
        final TabItemManager timan = Global.obj().getTabItemManager();
        final ArrayList<TabItem> aList = timan.getListAll();
        if(aList == null) {
            ReloadApp();
            return;
        }
        Iterator iter = aList.iterator();
        while(iter.hasNext()) {
            TabItem info = (TabItem)iter.next();
            LeftMenuItem item = new LeftMenuItem(info.getName(), info.getKey());
            leftMenuList.add(item);
        }
        mBind.listLeft.setHasFixedSize(true);
        mBind.listLeft.setLayoutManager(new LinearLayoutManager(mBind.getRoot().getContext()));
        mBind.listLeft.setAdapter(new MainLeftMenuRecyclerAdapter(mBind, this));
        mBind.listLeft.addItemDecoration(new SimpleDividerItemDecoration(mBind.getRoot().getContext()));
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((MainLeftMenuRecyclerAdapter)mBind.listLeft.getAdapter());
        mTouchHelper = new ItemTouchHelper(callback);
        mTouchHelper.attachToRecyclerView(mBind.listLeft);

        ImageButton ibtn = (ImageButton)findViewById(R.id.btn_leftmenu);
        ibtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mBind.drawerLayout.openDrawer(mBind.drawer);
                closeDropdownList();

            }
        });

        mBind.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        mBind.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                final TabItemManager timan = Global.obj().getTabItemManager();
                timan.refreshList(getApplicationContext(), true);
                ResetDropDownList();
                mBind.tabs.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                Storage.save(getApplicationContext(), TabItemManager.KEY_FILTERED, timan.makeFilteredList());
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

            ArrayList<TabItem> aList = Global.obj().getTabItemManager().getListWithoutFiltered();
            final int COLUMN_CNT_PER_ROW = 4;
            if(aList == null) {
                ReloadApp();
                return;
            }
            int listCnt = aList.size();
            LinearLayout newLinear = null;
            for(int i = 0 ; i < listCnt ; ++i) {
                if(i % COLUMN_CNT_PER_ROW == 0) {
                    newLinear = new LinearLayout(getApplicationContext());
                    newLinear.setBaselineAligned(false);
                    dropdownListLayout.addView(newLinear);
                }

                final TabItem info = aList.get(i);

                Button btn = new Button(getApplicationContext());
                final int btn_height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, btn_height, 1.0f);
                p.setMargins(2,2,2,2);
                btn.setText(info.getName());
                btn.setLayoutParams(p);
                btn.setPadding(5,5,5,5);
                btn.setTag(i);
                btn.setBackgroundColor(Color.parseColor(ThemeManager.GetTheme().SubFont));
                btn.setTextColor(Color.parseColor(ThemeManager.GetTheme().BgList));
                int fontSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,3,getApplicationContext().getResources().getDisplayMetrics());
                btn.setTextSize(fontSize);
                btn.setSingleLine(false);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int idx = (int)(((Button)v).getTag());
                        mBind.pager.setCurrentItem(idx);
                        closeDropdownList();
                    }
                });

                newLinear.addView(btn);
            }

            int remainCnt = listCnt % COLUMN_CNT_PER_ROW == 0 ? 0 : COLUMN_CNT_PER_ROW - listCnt % COLUMN_CNT_PER_ROW;
            for(int i = 0 ; i < remainCnt ; ++i) {
                Space s = new Space(getApplicationContext());
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                s.setLayoutParams(p);
                p.setMargins(2,2,2,2);
                newLinear.addView(s);
            }

            mBind.btnListComm.setImageResource(R.drawable.arrow_down);
            mBind.btnListComm.setOnClickListener(new View.OnClickListener() {
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
        mBind.btnListComm.setImageResource(R.drawable.arrow_up);
    }

    private void closeDropdownList() {
        dropdownListLayout.setVisibility(View.GONE);
        dropdownListLayout.setClickable(false);
        mBind.btnListComm.setImageResource(R.drawable.arrow_down);
    }

    private void ReloadApp() {
        Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        getApplicationContext().startActivity(intent);
    }
    private AdView getAdView() { return mBind.adViewMain; }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mTouchHelper.startDrag(viewHolder);
    }
}
