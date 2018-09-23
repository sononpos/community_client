package com.sononpos.communityviwerex;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sononpos.communityviwerex.FirstSettings.FirstSetting_ThemeActivity;
import com.sononpos.communityviwerex.Funtional.ThemeManager;
import com.sononpos.communityviwerex.databinding.ActivityPrivatePolicyBinding;

public class PrivatePolicyActivity extends AppCompatActivity {
    ActivityPrivatePolicyBinding mBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBind = DataBindingUtil.setContentView(this, R.layout.activity_private_policy);
        mBind.webview.loadUrl("http://4seasonpension.com/pp.html");

        mBind.btnPrivatePolicyConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                G.SetReadPP(getApplicationContext());

                // 첫번째 실행이면 FirstSetting으로
                if(G.IsFirstUse(getApplicationContext())){
                    //G.SetFirstUse();    //  이후부터는 첫번째 실행이 아니게 된다
                    Intent intent = new Intent(PrivatePolicyActivity.this, FirstSetting_ThemeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    SharedPreferences setRefer = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = setRefer.edit();
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(PrivatePolicyActivity.this, MainActivity.class);
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
                    startActivity(intent);
                }
            }
        });
    }

}
