package com.sononpos.allcommunity.Ads;

import com.fsn.cauly.CaulyAdView;
import com.fsn.cauly.CaulyAdViewListener;
import com.sononpos.allcommunity.Funtional.LogHelper;

/**
 * Created by nnnyy on 2017-10-06.
 */

public class CaulyAdsManager implements CaulyAdViewListener {
    @Override
    public void onReceiveAd(CaulyAdView caulyAdView, boolean b) {
        LogHelper.di("onReceiveAd");
    }

    @Override
    public void onFailedToReceiveAd(CaulyAdView caulyAdView, int i, String s) {
        LogHelper.di("onFailedToReceiveAd");
    }

    @Override
    public void onShowLandingScreen(CaulyAdView caulyAdView) {
        LogHelper.di("onShowLandingScreen");
    }

    @Override
    public void onCloseLandingScreen(CaulyAdView caulyAdView) {
        LogHelper.di("onCloseLandingScreen");
    }
}
