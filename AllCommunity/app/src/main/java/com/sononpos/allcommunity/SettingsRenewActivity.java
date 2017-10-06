package com.sononpos.allcommunity;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.sononpos.allcommunity.util.IabHelper;
import com.sononpos.allcommunity.util.IabResult;
import com.sononpos.allcommunity.util.Purchase;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nnnyyy on 2017-09-04.
 */

public class SettingsRenewActivity extends AppCompatActivity {

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
            if(BuildConfig.DEBUG) {
                Log.i("InApp", "onServiceConnected");
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsRenewFragment()).commit();
        getSupportActionBar().setTitle("설정");
        setupStatusBar();

        //  인앱 결제 초기화
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

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

    public static class SettingsRenewFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_prefer);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = super.onCreateView(inflater, container, savedInstanceState);

            Preference pfDonateCoffee = (Preference)findPreference("donate_coffee");
            if(pfDonateCoffee != null) {
                pfDonateCoffee.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        SettingsRenewActivity context = (SettingsRenewActivity) preference.getContext();
                        context.Buy("nodab_americano");
                        return false;
                    }
                });
            }

            Preference pfVer = (Preference)findPreference("ver");
            pfVer.setSummary(Global.getInstance().getDeviceVer());
            return v;
        }
    }

    public void AlreadyPurchaseItems() {
        try {
            ArrayList<String> skuList = new ArrayList<String> ();
            skuList.add("nodab_americano");
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
                //mHelper.launchPurchaseFlow(this, getPackageName(), 1001,  mPurchaseFinishedListener, "test");
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));

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
            Toast.makeText(getApplicationContext(), "도움 감사합니다", Toast.LENGTH_SHORT).show();
            try {
                mService.consumePurchase(3, getPackageName(), purchase.getToken());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

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
