package com.sononpos.communityviwerex;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sononpos.communityviwerex.Funtional.ThemeManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

public class LoadingActivity extends AppCompatActivity {

    class MyHandler extends Handler {
        private LoadingActivity mainActivity;

        public MyHandler(LoadingActivity act){
            mainActivity = act;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.arg1 == -1) {
                finishApp();
                return;
            }

            Intent intent = new Intent(mainActivity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            SharedPreferences setRefer = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            int themeType = Integer.parseInt(setRefer.getString("theme_type", "0"));
            ThemeManager.SetTheme(themeType);
            startActivity(intent);
        }
    }
    MyHandler handlerPager;

    class CheckUpdateHandler extends Handler {
        public CheckUpdateHandler(){}

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.arg1 == -1) {
                finishApp();
                return;
            }

            if( msg.arg1 == 0 ) {
                LoadCommunityList();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoadingActivity.this);

                builder.setTitle("업데이트 확인");
                builder.setMessage("마켓에 새 버전이 있습니다. 업데이트 하시겠습니까?");


                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                        marketLaunch.setData(Uri.parse("market://details?id=com.sononpos.communityviwerex"));
                        startActivity(marketLaunch);
                        finish();
                        dialog.dismiss();
                    }

                });

                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoadCommunityList();
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    CheckUpdateHandler handlerUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        CheckUpdate();
    }

    private void CheckUpdate() {
        handlerUpdate = new CheckUpdateHandler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = handlerUpdate.obtainMessage();
                try {
                    String device_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    int nState = MarketVersionChecker.getVersionState(device_version);
                    msg.arg1 = nState;
                    handlerUpdate.sendMessage(msg);
                } catch (PackageManager.NameNotFoundException e) {
                    msg.arg1 = -1;
                    handlerUpdate.sendMessage(msg);
                    return;
                }
            }
        }).start();
    }

    private void LoadCommunityList() {
        G.liCommTypeInfo.clear();
        ArrayList<String> aFiltered = G.getStringArrayPref(this, G.FILTERED_COMM);
        if(aFiltered != null) {
            G.liFiltered = new HashSet<String>(aFiltered);
        }
        else {
            G.liFiltered.clear();
        }
        handlerPager = new MyHandler(this);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                            int nIndex = element.getInt("index");
                            G.liCommTypeInfo.add(new CommunityTypeInfo(key, sName, nIndex));
                        }

                        Collections.sort(G.liCommTypeInfo, new Comparator<CommunityTypeInfo>() {
                            @Override
                            public int compare(CommunityTypeInfo o1, CommunityTypeInfo o2) {
                                return o1.index < o2.index ? -1 : 1;
                            }
                        });

                        G.RefreshFilteredInfo();

                        Message msg = handlerPager.obtainMessage();
                        handlerPager.sendMessage(msg);
                    }
                    else {
                        Message msg = handlerPager.obtainMessage();
                        msg.arg1 = -1;
                        handlerPager.sendMessage(msg);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                    Message msg = handlerPager.obtainMessage();
                    msg.arg1 = -1;
                    handlerPager.sendMessage(msg);
                }catch(JSONException e) {
                    e.printStackTrace();
                    Message msg = handlerPager.obtainMessage();
                    msg.arg1 = -1;
                    handlerPager.sendMessage(msg);
                }finally {
                }
            }
        }).start();
    }

    public void finishApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("앱 종료");
        builder.setMessage("네트워크 연결을 확인해 주십시오.");
        builder.setCancelable(false);


        builder.setPositiveButton("그..그럴게요", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

                dialog.dismiss();
            }

        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
