package com.sononpos.allcommunity.ArticleType;

/**
 * Created by nnnyy on 2017-03-06.
 */
public abstract class ArticleTypeInfo {
    public final static int TYPE_COMMUNITY = 0;
    public final static int TYPE_MOST_NEWS = 1;
    public final static int TYPE_YOUTUBE = 2;
    public int mType = 0;
    public int mHashKey = 0;

    public ArticleTypeInfo() {}
    protected abstract void MakeHash();
    public abstract String GetName();

    public int GetType() { return mType; }
}
