package com.sononpos.allcommunity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sononpos.allcommunity.AlertManager.AlertManager;
import com.sononpos.allcommunity.ArticleType.ArticleTypeInfo;
import com.sononpos.allcommunity.ArticleType.CommunityTypeInfo;
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

            G.RefreshFilteredInfo();

            Intent intent = new Intent(mainActivity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                Bundle bndlanimation =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.anim1,R.anim.anim2).toBundle();
                startActivity(intent, bndlanimation);
            }
            else {
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
                            marketLaunch.setData(Uri.parse("market://details?id=com.sononpos.allcommunity"));
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
        setupStatusBar();
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
                    G.deviceVer = device_version;
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
        G.liArticleTypeInfo.clear();
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
                G.LoadReadedArticle(getApplicationContext());
                Collections.sort(G.liArticleTypeInfo, new Comparator<ArticleTypeInfo>() {
                    @Override
                    public int compare(ArticleTypeInfo o1, ArticleTypeInfo o2) {
                        if(o1.GetType() != ArticleTypeInfo.TYPE_COMMUNITY) return -1;
                        if(o2.GetType() != ArticleTypeInfo.TYPE_COMMUNITY) return 1;

                        return ((CommunityTypeInfo)o1).index < ((CommunityTypeInfo)o2).index ? -1 : 1;
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

        httpHelper.Request(0, "https://hotcommunity-163106.appspot.com/community/list");
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

    protected void setupStatusBar() {
        Window window = getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.mainColor));
        }
    }
}
