package com.sononpos.allcommunity;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.fsn.cauly.CaulyAdInfo;
import com.fsn.cauly.CaulyAdInfoBuilder;
import com.sononpos.allcommunity.Ads.CaulyAdsManager;
import com.sononpos.allcommunity.AlertManager.AlertManager;
import com.sononpos.allcommunity.Funtional.LogHelper;
import com.sononpos.allcommunity.Funtional.ParsingHelper;
import com.sononpos.allcommunity.HttpHelper.HttpHelper;
import com.sononpos.allcommunity.HttpHelper.HttpHelperListener;
import com.sononpos.allcommunity.RecyclerAdapter.YoutubeListRecyclerAdapter;
import com.sononpos.allcommunity.databinding.ActivityYoutubeListBinding;
import com.sononpos.allcommunity.youtube.YoutubeArticleItem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class YoutubeListActivity extends AppCompatActivity implements HttpHelperListener {
    ActivityYoutubeListBinding mBind;
    HttpHelper httpHelper;
    CaulyAdsManager caulyAdsMan;
    String sWord;
    Handler loadHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_youtube_list);
        sWord = getIntent().getStringExtra("Word");
        LogHelper.dd(sWord);
        setupRView();
        setupAds();
        loadList();
    }

    private void setupRView() {
        mBind.rvList.setHasFixedSize(true);
        mBind.rvList.setLayoutManager(new LinearLayoutManager(mBind.getRoot().getContext()));
        mBind.rvList.setAdapter(new YoutubeListRecyclerAdapter());
        mBind.rvList.addItemDecoration(new SimpleDividerItemDecoration(YoutubeListActivity.this));

        httpHelper = new HttpHelper();
        httpHelper.SetListener(this);

        loadHandler = new Handler();
    }

    private void setupAds() {
        CaulyAdInfo adInfo = new CaulyAdInfoBuilder(getString(R.string.cauly_app_id)).
                effect("RightSlide").
                bannerHeight("Fixed_50").
                build();

        caulyAdsMan = new CaulyAdsManager();
        mBind.xmladview.setAdInfo(adInfo);
        mBind.xmladview.setAdViewListener(caulyAdsMan);
    }

    private void loadList() {
        try {
            String url = Global.YOUTUBE_ROOT + URLEncoder.encode(sWord, "UTF-8");
            LogHelper.di(url);
            httpHelper.Request(0, url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(int nType, int nErrorCode, String sResponse) {
        if(nErrorCode != 0) {
            loadHandler.post(new Runnable() {
                @Override
                public void run() {
                    AlertManager.ShowOk(YoutubeListActivity.this, "Error", getString(R.string.alert_unknown_problem), "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    });
                }
            });
            return;
        }

        LogHelper.di(sResponse);

        ArrayList<YoutubeArticleItem> aList = new ArrayList<>();
        if(!ParsingHelper.Youtube.parse(sResponse, aList)) {
            loadHandler.post(new Runnable() {
                @Override
                public void run() {
                    AlertManager.ShowOk(YoutubeListActivity.this, "Error", getString(R.string.alert_unknown_problem), "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    });
                }
            });
            return;
        }

        final YoutubeListRecyclerAdapter adapter = (YoutubeListRecyclerAdapter)mBind.rvList.getAdapter();
        adapter.AddList(aList);
        loadHandler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
