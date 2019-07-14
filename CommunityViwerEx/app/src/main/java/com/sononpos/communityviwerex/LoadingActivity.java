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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.sononpos.communityviwerex.FirstSettings.FirstSetting_ThemeActivity;
import com.sononpos.communityviwerex.Funtional.KBONetworkInfo;
import com.sononpos.communityviwerex.Funtional.RFData;
import com.sononpos.communityviwerex.Funtional.RetrofitExService;
import com.sononpos.communityviwerex.Funtional.ThemeManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

            String response = (String)msg.obj;
            G.LoadCommunityList(response);
            G.LoadRecentArticle(getApplicationContext());
            G.LoadReadedArticle(getApplicationContext());

            Collections.sort(G.liCommTypeInfo, new Comparator<CommunityTypeInfo>() {
                @Override
                public int compare(CommunityTypeInfo o1, CommunityTypeInfo o2) {
                    return o1.index < o2.index ? -1 : 1;
                }
            });

            // 첫번째 실행이면 FirstSetting으로
            if(G.IsFirstUse(getApplicationContext())){
                //G.SetFirstUse();    //  이후부터는 첫번째 실행이 아니게 된다
                Intent intent = new Intent(mainActivity, FirstSetting_ThemeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                SharedPreferences setRefer = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = setRefer.edit();
                editor.putString("list_backup", response);
                editor.apply();
                startActivity(intent);
            }
            else {
                G.RefreshFilteredInfo();

                Intent intent = new Intent(mainActivity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                SharedPreferences setRefer = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                int themeType = Integer.parseInt(setRefer.getString("theme_type", "0"));
                int themeFontType = Integer.parseInt(setRefer.getString("theme_font_type", "1"));
                ThemeManager.SetTheme(themeType);
                ThemeManager.SetThemeFont(themeFontType);
                SharedPreferences.Editor editor = setRefer.edit();
                editor.putString("list_backup", response);
                editor.apply();
                startActivity(intent);
            }
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
                if( KBONetworkInfo.IsWifiAvailable(getApplicationContext()) ) {
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
                else {
                    LoadCommunityList();
                }
            }
        }
    }

    CheckUpdateHandler handlerUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ThemeManager.Init();

        FirebaseInstanceId.getInstance().getToken();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(RetrofitExService.URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService retrofitExService = retrofit.create(RetrofitExService.class);
        retrofitExService.getData("1").enqueue(new Callback<RFData>() {
            @Override
            public void onResponse(Call<RFData> call, Response<RFData> response) {
                if( response.isSuccessful() ) {
                    RFData body = response.body();
                    if( body != null ) {
                        Log.d("nnnyyy", body.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<RFData> call, Throwable t) {

            }
        });

        CheckUpdate();
    }

    private void CheckUpdate() {
        handlerUpdate = new CheckUpdateHandler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Message msg = handlerUpdate.obtainMessage();
                try {
                    final String device_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    final FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
                    FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                            .setDeveloperModeEnabled(BuildConfig.DEBUG)
                            .setMinimumFetchIntervalInSeconds(3600)
                            .build();
                    config.setConfigSettings(configSettings);
                    config.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {
                            String sVer = config.getString("last_ver");
                            msg.arg1 = 0;
                            handlerUpdate.sendMessage(msg);
                        }
                    });



                    //int nState = MarketVersionChecker.getVersionState(device_version);
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
        ArrayList<String> aFiltered = G.getStringArrayPref(this, G.KEY_FILTERED_COMM);
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
                    Thread.sleep(750);
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

                        Message msg = handlerPager.obtainMessage();
                        msg.arg1 = 0;
                        msg.obj = response;
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
