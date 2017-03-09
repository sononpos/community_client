package com.sononpos.communityviwerex;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sononpos.communityviwerex.Funtional.ThemeManager;

public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new MyPreferenceFragment()).commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.getRootView().setBackgroundColor(Color.parseColor(ThemeManager.GetTheme().BgList));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_prefer);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = super.onCreateView(inflater, container, savedInstanceState);

            ListPreference themeTypePref = (ListPreference)findPreference("theme_type");
            int nTheme = Integer.parseInt(themeTypePref.getValue());
            themeTypePref.setSummary(ThemeManager.GetName(nTheme));
            themeTypePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int nTheme = Integer.parseInt((String) newValue);
                    preference.setSummary(ThemeManager.GetName(nTheme));

                    ThemeManager.ThemeColorObject theme = ThemeManager.GetTheme();

                    // return false; 로 리턴하면 변경을 취소합니다.
                    return true;
                }
            });

            Preference pfRecommand = (Preference)findPreference("app_recommand");
            pfRecommand.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                    marketLaunch.setData(Uri.parse("market://details?id=com.sononpos.communityviwerex"));
                    startActivity(marketLaunch);

                    return false;
                }
            });

            Preference pfVer = (Preference)findPreference("ver");
            try {

                PackageInfo i = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                String version = i.versionName;
                pfVer.setSummary(version);
            } catch(PackageManager.NameNotFoundException e) { }

            return v;
        }
    }
}
