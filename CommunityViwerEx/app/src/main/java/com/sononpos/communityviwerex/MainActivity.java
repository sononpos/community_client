package com.sononpos.communityviwerex;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

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

public class MainActivity extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private CommunityTypePagerAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("OnStart!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("OnResume!");
    }

    class MyHandler extends Handler {
        private MainActivity mainActivity;

        public MyHandler(MainActivity act){
            mainActivity = act;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // Load an ad into the AdMob banner view.
            AdView adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                    .setRequestAgent("android_studio:ad_template").build();
            adView.loadAd(adRequest);

            tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            pager = (ViewPager) findViewById(R.id.pager);
            adapter = new CommunityTypePagerAdapter(getSupportFragmentManager());
            adapter.liData = G.liCommTypeInfo;
            pager.setAdapter(adapter);
            tabs.setViewPager(pager);
            tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    List<Fragment> fragments = getSupportFragmentManager().getFragments();
                    for(Fragment f: fragments) {
                        if (f instanceof CommunityListFragment) {
                            if ( ((CommunityListFragment)f).getPageNum() == (position+1) ) {
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

            //  Setup ActionBar
            ActionBar ab = getSupportActionBar();
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setDisplayShowTitleEnabled(false);
            LayoutInflater li = LayoutInflater.from(mainActivity);
            View vCustomAB = li.inflate(R.layout.toolbar, null);
            ab.setCustomView(vCustomAB);
            ImageButton ibtn = (ImageButton)vCustomAB.findViewById(R.id.btn_leftmenu);
            ibtn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    dl.openDrawer(dlv);
                }
            });

            adapter.liData = G.liCommTypeInfo;


        }
    }

    MyHandler handlerPager;

    //  DrawerLayout
    private DrawerLayout dl;
    private View dlv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        handlerPager = new MyHandler(this);
        G.liCommTypeInfo.clear();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL("http://52.79.205.198:3000/list");

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);

                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if( responseCode == HttpURLConnection.HTTP_OK){
                        InputStream is   = null;
                        ByteArrayOutputStream baos = null;
                        String response;
                        is = conn.getInputStream();
                        baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int nLength = 0;

                        while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, nLength);
                        }
                        byteData = baos.toByteArray();
                        response = new String(byteData);
                        JSONObject jobj = new JSONObject(response);
                        Iterator<String> iter = jobj.keys();
                        while(iter.hasNext()){
                            String key = iter.next();
                            JSONObject element = jobj.getJSONObject(key);
                            String sName = element.getString("name");
                            G.liCommTypeInfo.add(new CommunityTypeInfo(key, sName));
                        }

                        Message msg = handlerPager.obtainMessage();
                        handlerPager.sendMessage(msg);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }catch(JSONException e) {
                    e.printStackTrace();
                }finally {
                }
            }
        }).start();
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

    public class CommunityTypePagerAdapter extends FragmentPagerAdapter {

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
    }

}
