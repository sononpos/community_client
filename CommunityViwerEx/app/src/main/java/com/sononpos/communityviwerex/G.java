package com.sononpos.communityviwerex;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sononpos.communityviwerex.CommunityTypeInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by nnnyy on 2017-03-06.
 */
public class G {
    public static ArrayList<CommunityTypeInfo> liCommTypeInfo = new ArrayList<>();
    public static ArrayList<CommunityTypeInfo> liFilteredInfo = new ArrayList<>();
    public static HashSet<String> liFiltered = new HashSet<>();
    private static ArrayList<String> liRecentArticle = new ArrayList<>();
    public static final String SERV_ROOT = "http://52.79.205.198:3000/";
    public static final String KEY_FILTERED_COMM = "FilteredCommunity";
    public static final String KEY_RECENT_ARTICLES = "RecentArticles";
    public static final String KEY_SHOW_RECENT= "list_show_recent";
    public static final String FIRST_USE = "FirstUse";

    public static boolean IsShowRecent(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean bFirstUse = prefs.getBoolean(KEY_SHOW_RECENT, false);
        return bFirstUse;
    }

    public static boolean IsFirstUse(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean bFirstUse = prefs.getBoolean(FIRST_USE, true);
        return bFirstUse;
    }

    public static void SetFirstUse(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(FIRST_USE, false);
        editor.apply();
    }

    public static void setStringArrayPref(Context context , String sKey, ArrayList<String> values){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for(int i =  0 ; i < values.size() ; ++i) {
            a.put(values.get(i));
        }

        if(!values.isEmpty()) {
            editor.putString(sKey, a.toString());
        }
        else {
            editor.putString(sKey, null);
        }

        editor.apply();
    }

    public static ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> a = new ArrayList<>();
        if( json != null) {
            try {
                JSONArray ja = new JSONArray(json);
                for(int i = 0 ; i < ja.length() ; ++i) {
                    String sData = ja.optString(i);
                    a.add(sData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return a;
    }

    public static void LoadCommunityList(String response) {
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(response);
            Iterator<String> iter = jobj.keys();
            while(iter.hasNext()){
                String key = iter.next();
                JSONObject element = jobj.getJSONObject(key);
                String sName = element.getString("name");
                int nIndex = element.getInt("index");
                G.liCommTypeInfo.add(new CommunityTypeInfo(key, sName, nIndex));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void ReloadCommunityListFromSharedPref(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String list_backup = prefs.getString("list_backup", "");
        LoadCommunityList(list_backup);

        ArrayList<String> aFiltered = G.getStringArrayPref(context, G.KEY_FILTERED_COMM);
        if(aFiltered != null)
            G.liFiltered = new HashSet<String>(aFiltered);
        RefreshFilteredInfo();
    }

    public static void RefreshFilteredInfo() {
        liFilteredInfo.clear();
        if(liFiltered.size() == 0 ) return;

        for (CommunityTypeInfo communityTypeInfo : G.liCommTypeInfo) {
            boolean bFind = false;
            for (String s : liFiltered) {
                if( communityTypeInfo.sKey.compareTo(s) == 0 ) {
                    bFind = true;
                    break;
                }
            }
            if(!bFind) {
                liFilteredInfo.add(communityTypeInfo);
            }
        }
    }

    public static boolean IsFiltered() {
        return (liFiltered.size() != 0);
    }

    public static ArrayList<CommunityTypeInfo> GetCommunityList() {
        if(IsFiltered()) {
            return liFilteredInfo;
        }

        return liCommTypeInfo;
    }

    public static void SaveRecentArticle(Context context, ListViewItem item) {
        if(liRecentArticle.size() >= 10) {
            liRecentArticle.remove(liRecentArticle.size()-1);
        }
        liRecentArticle.add(0,item.m_sJsonString);
        setStringArrayPref(context, KEY_RECENT_ARTICLES, liRecentArticle);
    }

    public static void LoadRecentArticle(Context context) {
        liRecentArticle = getStringArrayPref(context, KEY_RECENT_ARTICLES);
    }
}
