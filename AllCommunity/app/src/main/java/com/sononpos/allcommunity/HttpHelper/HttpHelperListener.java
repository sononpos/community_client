package com.sononpos.allcommunity.HttpHelper;

/**
 * Created by nnnyyy on 2017-05-24.
 */

public interface HttpHelperListener {
    void onResponse(int nType, int nErrorCode, String sResponse);
}
