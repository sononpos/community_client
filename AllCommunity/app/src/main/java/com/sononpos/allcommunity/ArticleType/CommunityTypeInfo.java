package com.sononpos.allcommunity.ArticleType;

/**
 * Created by nnnyy on 2017-09-16.
 */

public class CommunityTypeInfo extends ArticleTypeInfo {
    public String sKey;
    public String sName;
    public int index;

    CommunityTypeInfo(){}

    public CommunityTypeInfo(String sKey, String sName, int idx) {
        this.sKey = sKey;
        this.sName = sName;
        this.index = idx;
        mType = ArticleTypeInfo.TYPE_COMMUNITY;
        MakeHash();
    }

    @Override
    protected void MakeHash() {
        mHashKey = (sKey + mType).hashCode();
    }

    @Override
    public String GetName() {
        return sName;
    }
}
