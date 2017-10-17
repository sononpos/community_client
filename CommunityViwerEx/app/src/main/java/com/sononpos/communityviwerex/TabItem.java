package com.sononpos.communityviwerex;

/**
 * Created by nnnyy on 2017-10-17.
 */

public class TabItem {
    public final static int IT_COMMUNITY = 0;
    public final static int IT_RECENT = 1;
    public final static int IT_FAVORATE = 2;
    protected String sKey;
    protected String sName;
    protected int nType;
    public int index;

    public String getName() { return sName; }
    public String getKey() { return sKey; }
}
