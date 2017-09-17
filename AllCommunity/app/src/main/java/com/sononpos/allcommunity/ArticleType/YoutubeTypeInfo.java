package com.sononpos.allcommunity.ArticleType;

/**
 * Created by nnnyy on 2017-09-16.
 */

public class YoutubeTypeInfo extends ArticleTypeInfo {
    protected String sName;
    protected String query;

    YoutubeTypeInfo(){}

    @Override
    protected void MakeHash() {
        mHashKey = (query + mType).hashCode();
    }

    @Override
    public String GetName() {
        return sName;
    }
}
