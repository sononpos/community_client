package com.sononpos.allcommunity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sononpos.allcommunity.AlertManager.AlertManager;
import com.sononpos.allcommunity.Funtional.KBONetworkInfo;
import com.sononpos.allcommunity.HttpHelper.HttpHelper;
import com.sononpos.allcommunity.HttpHelper.HttpHelperListener;
import com.sononpos.allcommunity.databinding.ActivityLoadingBinding;

import java.util.Collections;
import java.util.Comparator;

public class LoadingActivity extends AppCompatActivity {
    ActivityLoadingBinding mBind;
    HttpHelper httpHelper = new HttpHelper();
    MyHandler handlerPager;

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

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 첫번째 실행이면 FirstSetting으로
            if(G.IsFirstUse(getApplicationContext())){
                //G.SetFirstUse();    //  이후부터는 첫번째 실행이 아니게 된다
            }
            else {
                G.RefreshFilteredInfo();

                Intent intent = new Intent(mainActivity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        }
    }

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
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_loading);
        getSupportActionBar().hide();
        FirebaseInstanceId.getInstance().getToken();
        CheckUpdate();
    }

    private void CheckUpdate() {
        handlerUpdate = new CheckUpdateHandler();

        /*
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
        */

        Message completeMsg = handlerUpdate.obtainMessage(0);
        completeMsg.sendToTarget();
    }

    private void LoadCommunityList() {
        G.liCommTypeInfo.clear();
        handlerPager = new MyHandler(this);
        httpHelper.SetListener(new HttpHelperListener() {
            @Override
            public void onResponse(int nType, int nErrorCode, String sResponse) {
                if(nErrorCode != 0) {
                    Message msg = handlerPager.obtainMessage(-1);
                    handlerPager.sendMessage(msg);
                    return;
                }
                G.LoadCommunityList(sResponse);
                G.LoadFiltered(getApplicationContext());
                //.G.LoadRecentArticle(getApplicationContext());
                G.LoadReadedArticle(getApplicationContext());

                Collections.sort(G.liCommTypeInfo, new Comparator<CommunityTypeInfo>() {
                    @Override
                    public int compare(CommunityTypeInfo o1, CommunityTypeInfo o2) {
                        return o1.index < o2.index ? -1 : 1;
                    }
                });

                SharedPreferences setRefer = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = setRefer.edit();
                editor.putString("list_backup", sResponse);
                editor.apply();

                Message msg = handlerPager.obtainMessage(0);
                handlerPager.sendMessage(msg);
            }
        });

        httpHelper.Request(0, "http://52.79.205.198:3000/list");
    }

    public void finishApp() {
        AlertManager.ShowOk(LoadingActivity.this, "앱 종료", "네트워크를 확인 해 주세요", "네..", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

                dialog.dismiss();
            }
        });
    }
}
