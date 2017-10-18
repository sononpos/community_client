package com.sononpos.communityviwerex;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by nnnyy on 2017-03-06.
 */
public class G {
    private static ArrayList<String> liRecentArticle = new ArrayList<>();
    public static final String SERV_ROOT = "http://52.79.205.198:3000/";
    public static final String KEY_RECENT_ARTICLES = "RecentArticles";
    public static final String KEY_SHOW_RECENT= "list_show_recent";
    public static final String KEY_TUTORIAL_COMPLETE = "tutorial_complete";
    public static final String KEY_READED_ARTICLES = "ReadedArticles";
    public static final String FIRST_USE = "FirstUse";

    public static HashSet<Integer> liReadArticleCheck = new HashSet<>();

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

    public static ArrayList<Integer> getIntArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<Integer> a = new ArrayList<>();
        if( json != null) {
            try {
                JSONArray ja = new JSONArray(json);
                for(int i = 0 ; i < ja.length() ; ++i) {
                    Integer sData = ja.optInt(i);
                    a.add(sData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return a;
    }

    public static void SaveRecentArticle(Context context, ListViewItem item) {
        if(liRecentArticle.size() >= 30) {
            liRecentArticle.remove(liRecentArticle.size()-1);
        }
        liRecentArticle.add(0,item.m_sJsonString);
        setStringArrayPref(context, KEY_RECENT_ARTICLES, liRecentArticle);
    }

    public static void LoadRecentArticle(Context context) {
        liRecentArticle = getStringArrayPref(context, KEY_RECENT_ARTICLES);
    }

    public static void ClearRecentArticle(Context context) {
        liRecentArticle.clear();
        setStringArrayPref(context, KEY_RECENT_ARTICLES, liRecentArticle);
    }

    public static void readArticle(Context context, int hash) {

        if(liReadArticleCheck.size() >= 200) {
            liReadArticleCheck.remove(liReadArticleCheck.size()-1);
        }
        liReadArticleCheck.add(hash);
        ArrayList li = new ArrayList(liReadArticleCheck);
        setStringArrayPref(context, KEY_READED_ARTICLES, li);
    }

    public static void LoadReadedArticle(Context context) {
        liReadArticleCheck = new HashSet<>(getIntArrayPref(context, KEY_READED_ARTICLES));
    }

    public static boolean isReaded(int hash) {
        return liReadArticleCheck.contains(hash);
    }
}
