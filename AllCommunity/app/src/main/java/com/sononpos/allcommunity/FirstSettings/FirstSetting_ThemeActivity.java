package com.sononpos.allcommunity.FirstSettings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sononpos.allcommunity.Funtional.ThemeManager;
import com.sononpos.allcommunity.G;
import com.sononpos.allcommunity.MainActivity;
import com.sononpos.allcommunity.R;

public class FirstSetting_ThemeActivity extends AppCompatActivity {

    LinearLayout llBack;
    TextView tvFirstTimeTitle;
    Button btTheme0;
    Button btTheme1;
    Button btTheme2;
    Button btTheme3;
    Button btTheme4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_setting__theme);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                G.SetFirstUse(getApplicationContext());    //  이후부터는 첫번째 실행이 아니게 된다

                AlertDialog.Builder builder = new AlertDialog.Builder(FirstSetting_ThemeActivity.this);

                builder.setTitle("안내");
                builder.setMessage("테마 변경은 [설정] 메뉴에서 바꿀 수 있습니다");
                builder.setCancelable(false);

                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(FirstSetting_ThemeActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();


            }
        });

        llBack = (LinearLayout)findViewById(R.id.content_first_setting__theme);
        tvFirstTimeTitle = (TextView)findViewById(R.id.tv_first_theme);

        btTheme0 = (Button)findViewById(R.id.btn_theme0);
        btTheme0.setBackgroundColor(Color.parseColor(ThemeManager.GetTheme(0).BgList));
        btTheme0.setTextColor(Color.parseColor(ThemeManager.GetTheme(0).BasicFont));
        btTheme0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetTheme(0);
            }
        });
        btTheme1 = (Button)findViewById(R.id.btn_theme1);
        btTheme1.setBackgroundColor(Color.parseColor(ThemeManager.GetTheme(1).BgList));
        btTheme1.setTextColor(Color.parseColor(ThemeManager.GetTheme(1).BasicFont));
        btTheme1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetTheme(1);
            }
        });
        btTheme2 = (Button)findViewById(R.id.btn_theme2);
        btTheme2.setBackgroundColor(Color.parseColor(ThemeManager.GetTheme(2).BgList));
        btTheme2.setTextColor(Color.parseColor(ThemeManager.GetTheme(2).BasicFont));
        btTheme2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetTheme(2);
            }
        });
        btTheme3 = (Button)findViewById(R.id.btn_theme3);
        btTheme3.setBackgroundColor(Color.parseColor(ThemeManager.GetTheme(3).BgList));
        btTheme3.setTextColor(Color.parseColor(ThemeManager.GetTheme(3).BasicFont));
        btTheme3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetTheme(3);
            }
        });
        btTheme4 = (Button)findViewById(R.id.btn_theme4);
        btTheme4.setBackgroundColor(Color.parseColor(ThemeManager.GetTheme(4).BgList));
        btTheme4.setTextColor(Color.parseColor(ThemeManager.GetTheme(4).BasicFont));
        btTheme4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetTheme(4);
            }
        });
    }

    void SetTheme(int themeNum) {
        llBack.setBackgroundColor(Color.parseColor(ThemeManager.GetTheme(themeNum).BgList));
        tvFirstTimeTitle.setBackgroundColor(Color.parseColor(ThemeManager.GetTheme(themeNum).BgList));
        tvFirstTimeTitle.setTextColor(Color.parseColor(ThemeManager.GetTheme(themeNum).BasicFont));
        ThemeManager.SetTheme(themeNum);
        SharedPreferences setRefer = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = setRefer.edit();
        editor.putString("theme_type", String.valueOf(themeNum));
        editor.putString("theme_font_type", String.valueOf(1));
        editor.apply();

    }
}
