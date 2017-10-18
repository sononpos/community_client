package com.sononpos.communityviwerex;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sononpos.communityviwerex.FirstSettings.FirstSetting_ThemeActivity;
import com.sononpos.communityviwerex.Funtional.KBONetworkInfo;
import com.sononpos.communityviwerex.Funtional.ThemeManager;
import com.sononpos.communityviwerex.HttpHelper.HttpHelper;
import com.sononpos.communityviwerex.HttpHelper.HttpHelperListener;

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
            TabItemManager timan = Global.obj().getTabItemManager();
            String sJsonList = Storage.load(getApplicationContext(), G.KEY_FILTERED_COMM);
            timan.setFilteredList(sJsonList);

            timan.init();
            if(!Parcer.communityList(response, timan)) {
                finish();
            }
            //  저장 된 순서가 없다면 서버에서 받은 순서대로 일단 정렬
            //  저장 된 순서가 있다면 새로 세팅하고 정렬
            if(Storage.have(getApplicationContext(), "TabItemListSeq")) {
                String sSeqJsonString = Storage.load(getApplicationContext(), "TabItemListSeq");
                timan.setSeq(sSeqJsonString);
                timan.sort();
            }
            else {
                timan.sort();
            }
            timan.refreshList(getApplicationContext());

            //G.LoadCommunityList(response);
            G.LoadRecentArticle(getApplicationContext());
            G.LoadReadedArticle(getApplicationContext());

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
        handlerPager = new MyHandler(this);
        new HttpHelper().SetListener(new HttpHelperListener() {
            @Override
            public void onResponse(int nType, int nErrorCode, String sResponse) {
                if(nErrorCode == 0) {
                    Message msg = handlerPager.obtainMessage();
                    msg.arg1 = 0;
                    msg.obj = sResponse;
                    handlerPager.sendMessage(msg);
                }
                else {
                    Message msg = handlerPager.obtainMessage();
                    msg.arg1 = -1;
                    handlerPager.sendMessage(msg);
                }
            }
        }).Request(0, "http://52.79.205.198:3000/list");
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
