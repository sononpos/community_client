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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.sononpos.communityviwerex.FirstSettings.FirstSetting_ThemeActivity;
import com.sononpos.communityviwerex.Funtional.KBONetworkInfo;
import com.sononpos.communityviwerex.Funtional.ThemeManager;
import com.sononpos.communityviwerex.HttpHelper.HttpHelper;
import com.sononpos.communityviwerex.HttpHelper.HttpHelperListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
            ArticleListManager aiman = Global.obj().getArticleListManager();
            timan.init();
            if(Storage.have(getApplicationContext(), TabItemManager.KEY_FILTERED))
            {
                String sJsonList = Storage.load(getApplicationContext(), TabItemManager.KEY_FILTERED);
                timan.setFilteredList(sJsonList);
            }


            if(!Parcer.communityList(response, timan)) {
                finish();
            }
            timan.addItem(new TIRecent("hc_recent", "최근 본 글", -1));
            timan.addItem(new TIFavorate("hc_scrap", "스크랩", -1));
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
            timan.refreshList(getApplicationContext(), true);

            if(Storage.have(getApplicationContext(), ArticleListManager.KEY_RECENT_ARTICLE)) {
                String sRecentJsonList = Storage.load(getApplicationContext(), ArticleListManager.KEY_RECENT_ARTICLE);
                aiman.loadRecent(sRecentJsonList);
            }
            if(Storage.have(getApplicationContext(), ArticleListManager.KEY_FAVORITE_ARTICLE)) {
                String sRecentJsonList = Storage.load(getApplicationContext(), ArticleListManager.KEY_FAVORITE_ARTICLE);
                aiman.loadFavorate(sRecentJsonList);
            }

            //G.LoadCommunityList(response);
            G.LoadRecentArticle(getApplicationContext());
            G.LoadReadedArticle(getApplicationContext());

            if( !G.IsReadPP(getApplicationContext())) {
                Intent intent = new Intent(mainActivity, PrivatePolicyActivity.class);
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
    }
    MyHandler handlerPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ThemeManager.Init();

        FirebaseInstanceId.getInstance().getToken();

        CheckUpdate();
    }

    //  버전 체크
    private void CheckUpdate() {
        Observable.<Integer>create(e-> {
            FirebaseRemoteConfig frc = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings frcsettings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG).build();
            frc.setConfigSettings(frcsettings);

            frc.fetch(0).addOnCompleteListener(task-> {
                if( task.isSuccessful() ) {
                    try {

                        frc.activateFetched();
                        String sCurVer = BuildConfig.VERSION_NAME;
                        int nCurMajor = Integer.parseInt( sCurVer.split("\\.")[0] );
                        String s = frc.getString("last_ver");
                        int nServerMajor = Integer.parseInt( s.split("\\.")[0] );

                        if( nServerMajor > nCurMajor ) {
                            e.onNext(-1);
                            return;
                        }

                    }catch(Exception ex) {
                        finishApp();
                    }
                }

                e.onNext(0);
            });
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(ret-> {
            if( ret == 0 ) {
                LoadCommunityList();
            }
            else if( ret == -1 ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoadingActivity.this);

                builder.setTitle("업데이트 확인");
                builder.setMessage("마켓에 새 버전이 있습니다. 업데이트 하시겠습니까?");
                builder.setPositiveButton("네", (dlg,which)-> {
                    Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                    marketLaunch.setData(Uri.parse("market://details?id=com.sononpos.communityviwerex"));
                    startActivity(marketLaunch);
                    finish();
                    dlg.dismiss();
                });

                builder.setNegativeButton("아니오", (dlg,which)-> {
                    LoadCommunityList();
                    dlg.dismiss();
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
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
