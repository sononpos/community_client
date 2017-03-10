package com.sononpos.communityviwerex;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.sononpos.communityviwerex.Funtional.ThemeManager;
import com.sononpos.communityviwerex.util.*;

import org.json.JSONObject;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;

    IInAppBillingService mService;
    IabHelper mHelper;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

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

        //  인앱 결제 초기화
        bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"), mServiceConn, Context.BIND_AUTO_CREATE);

        // 구글에서 발급받은 바이너리키를 입력해줍니다
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAy23CcPAVmLIIw+sc2sqKoStYINIr6oIVsEj+7G1mBg0W0mZy/pUy+VGrUxmnB3O3yJbXOX1GMdfGJzF2Dt19A+R71nTqlWyXM+d/DTxAZGwEX3Bt1uretPVpOyX/7gnq9W5qfJVdFFp82vjozHKJ/zyNZe/QZZxM7Gzag8bsGdczrp0GLf6ueEznLvUemaDelMc2YSDMkO4dPOtQD+X7FYo0xfZBPg1zzQstiYIDo/AZ0dLRSj/VqBaZWKmRCz1WjtsSDprGPfBc5ywmEgD+v7quBgshMV47yOcweOvgKFg8QpS32IyS5CKVwrbMirG6MsGtI8kYPuG49zvXRzyUjQIDAQAB";

        mHelper = new IabHelper(this, null);
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // 구매오류처리 ( 토스트하나 띄우고 결제팝업 종료시키면 되겠습니다 )
                    Toast.makeText(getApplicationContext(), "결제 초기화 실패", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 구매목록을 초기화하는 메서드입니다.
                // v3으로 넘어오면서 구매기록이 모두 남게 되는데 재구매 가능한 상품( 게임에서는 코인같은아이템은 ) 구매후 삭제해주어야 합니다.
                // 이 메서드는 상품 구매전 혹은 후에 반드시 호출해야합니다. ( 재구매가 불가능한 1회성 아이템의경우 호출하면 안됩니다 )
                AlreadyPurchaseItems();
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
                    SettingsActivity context = (SettingsActivity) preference.getContext();
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

            Preference pfDonateCoffee = (Preference)findPreference("donate_coffee");
            pfDonateCoffee.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SettingsActivity context = (SettingsActivity) preference.getContext();
                    context.Buy("nodab_americano");
                    return false;
                }
            });
            return v;
        }
    }

    public void AlreadyPurchaseItems() {
        try {
            ArrayList<String> skuList = new ArrayList<String> ();
            skuList.add("nodab_americano");
            skuList.add("nodab_rice");
            Bundle querySkus = new Bundle();
            querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

            Bundle ownedItems = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList purchaseDataList = ownedItems.getStringArrayList("DETAILS_LIST");
                String[] tokens = new String[purchaseDataList.size()];
                for (int i = 0; i < purchaseDataList.size(); ++i) {
                    String purchaseData = (String) purchaseDataList.get(i);
                    JSONObject jo = new JSONObject(purchaseData);
                    tokens[i] = jo.getString("purchaseToken");
                    // 여기서 tokens를 모두 컨슘 해주기
                    mService.consumePurchase(3, getPackageName(), tokens[i]);
                }
            }
            else {
            }
            // 토큰을 모두 컨슘했으니 구매 메서드 처리
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 구매
    public void Buy(String id_item) {
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),	id_item, "inapp", "test");
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

            if (pendingIntent != null) {
                mHelper.launchPurchaseFlow(this, getPackageName(), 1001,  mPurchaseFinishedListener, "test");

            } else {
                // 결제가 막혔다면
                Toast.makeText(getApplicationContext(), "구매 실패", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener  = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            // 여기서 아이템 추가 해주시면 됩니다.
            // 만약 서버로 영수증 체크후에 아이템 추가한다면, 서버로 purchase.getOriginalJson() , purchase.getSignature() 2개 보내시면 됩니다.
            Toast.makeText(getApplicationContext(), "도움 감사합니다", Toast.LENGTH_SHORT);
        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mServiceConn != null){
            unbindService(mServiceConn);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mHelper ==null) return; //iabHelper가 null값인경우 리턴

        if(!mHelper.handleActivityResult(requestCode, resultCode, data)){ //iabHelper가 데이터를 핸들링하도록 데이터 전달
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
