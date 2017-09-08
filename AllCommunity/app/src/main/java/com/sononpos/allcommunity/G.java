package com.sononpos.allcommunity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sononpos.allcommunity.Funtional.StorageHelper;

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
    public static final String KEY_TUTORIAL_COMPLETE = "tutorial_complete";
    public static final String KEY_READED_ARTICLES = "ReadedArticles";
    public static final String FIRST_USE = "FirstUse";

    public static HashSet<Integer> liReadArticleCheck = new HashSet<>();

    public static boolean IsFirstUse(Context context) {        /*
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean bFirstUse = prefs.getBoolean(FIRST_USE, true);
        return bFirstUse;
        */
        return false;
    }

    public static void SetFirstUse(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(FIRST_USE, false);
        editor.apply();
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

        ArrayList<String> aFiltered = StorageHelper.getStringArrayPref(context, G.KEY_FILTERED_COMM);
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
    public static boolean IsFilteredKey(String sKey) {
        return liFiltered.contains(sKey);
    }
    public static void ToggleFilter(String key) {
        if(liFiltered.contains(key)){
            liFiltered.remove(key);
        }
        else {
            liFiltered.add(key);
        }
    }

    public static ArrayList<CommunityTypeInfo> GetCommunityList(boolean bForceAll) {
        if(!bForceAll && IsFiltered()) {
            return liFilteredInfo;
        }

        return liCommTypeInfo;
    }

    public static int GetFilteredIndex(int _nPosNotFiltered) {
        CommunityTypeInfo info = liCommTypeInfo.get(_nPosNotFiltered);
        Iterator<CommunityTypeInfo> iter = IsFiltered() ? liFilteredInfo.iterator() : liCommTypeInfo.iterator();
        int idx = 0;
        while(iter.hasNext()){
            if(iter.next().sKey == info.sKey) {
                return idx;
            }
            idx++;
        }

        return -1;
    }

    public static void ClearRecentArticle(Context context) {
        liRecentArticle.clear();
        StorageHelper.setStringArrayPref(context, KEY_RECENT_ARTICLES, liRecentArticle);
    }

    public static void LoadReadedArticle(Context context) {
        liReadArticleCheck = new HashSet<>(StorageHelper.getIntArrayPref(context, KEY_READED_ARTICLES));
    }

    public static boolean isReaded(int hash) {
        return liReadArticleCheck.contains(hash);
    }
}
