package com.sononpos.allcommunity.Funtional;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by nnnyy on 2017-03-19.
 */
public class KBONetworkInfo {

    public static boolean IsWifiAvailable(Context context)
    {
        ConnectivityManager m_NetConnectMgr= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean bConnect = false;
        try
        {
            if( m_NetConnectMgr == null ) return false;

            NetworkInfo info = m_NetConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            bConnect = (info.isAvailable() && info.isConnected());

        }
        catch(Exception e)
        {
            return false;
        }

        return bConnect;
    }

    public static boolean Is3GAvailable(Context context)
    {
        ConnectivityManager m_NetConnectMgr= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean bConnect = false;
        try
        {
            if( m_NetConnectMgr == null ) return false;
            NetworkInfo info = m_NetConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            bConnect = (info.isAvailable() && info.isConnected());
        }
        catch(Exception e)
        {
            return false;
        }

        return bConnect;
    }
}