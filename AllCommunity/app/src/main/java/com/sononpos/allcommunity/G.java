package com.sononpos.allcommunity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.sononpos.allcommunity.ArticleType.ArticleTypeInfo;
import com.sononpos.allcommunity.ArticleType.CommunityTypeInfo;
import com.sononpos.allcommunity.ArticleType.NewsTypeInfo;
import com.sononpos.allcommunity.Funtional.StorageHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by nnnyy on 2017-03-06.
 */
public class G {
    public static ArrayList<ArticleTypeInfo> liArticleTypeInfo = new ArrayList<>();
    public static ArrayList<ArticleTypeInfo> liFilteredInfo = new ArrayList<>();
    public static HashSet<Integer> liFiltered = new HashSet<>();
    private static ArrayList<String> liRecentArticle = new ArrayList<>();
    public static final String SERV_ROOT = "http://52.79.205.198:3000/";
    public static final String KEY_FILTERED_COMM = "FilteredCommunity";
    public static final String KEY_RECENT_ARTICLES = "RecentArticles";
    public static final String KEY_SHOW_RECENT= "list_show_recent";
    public static final String KEY_TUTORIAL_COMPLETE = "tutorial_complete";
    public static final String KEY_READED_ARTICLES = "ReadedArticles";
    public static String deviceVer = "1.0";

    public static HashSet<Integer> liReadArticleCheck = new HashSet<>();
    protected static AdsTimeChecker adsTimeChecker = new AdsTimeChecker();
    public static Options options = new Options();

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
                G.liArticleTypeInfo.add(new CommunityTypeInfo(key, sName, nIndex));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        G.liArticleTypeInfo.add(new NewsTypeInfo("news"," 실시간 인기 검색어",0));
    }

    public static void ReloadCommunityListFromSharedPref(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String list_backup = prefs.getString("list_backup", "");
        LoadCommunityList(list_backup);

        ArrayList<Integer> aFiltered = StorageHelper.getIntArrayPref(context, G.KEY_FILTERED_COMM);
        if(aFiltered != null)
            G.liFiltered = new HashSet<>(aFiltered);
        RefreshFilteredInfo();
    }

    public static void RefreshFilteredInfo() {
        liFilteredInfo.clear();
        if(liFiltered.size() == 0 ) return;

        for (ArticleTypeInfo articleTypeInfo : G.liArticleTypeInfo) {
            if(!liFiltered.contains(articleTypeInfo.mHashKey)) {
                liFilteredInfo.add(articleTypeInfo);
            }
        }
    }

    public static boolean IsFiltered() {
        return (liFiltered.size() != 0);
    }
    public static boolean IsFilteredKey(int nKey) {
        return liFiltered.contains(nKey);
    }
    public static void ToggleFilter(int key) {
        if(liFiltered.contains(key)){
            liFiltered.remove(key);
        }
        else {
            liFiltered.add(key);
        }
    }
    public static void SaveFiltered(Context context) {
        StorageHelper.setArrayPref(context, G.KEY_FILTERED_COMM, new ArrayList<>(G.liFiltered));
    }
    public static void LoadFiltered(Context context) {
        liFiltered.clear();
        ArrayList<Integer> aFiltered = StorageHelper.getIntArrayPref(context, G.KEY_FILTERED_COMM);
        if(aFiltered != null)
            liFiltered = new HashSet<>(aFiltered);
        RefreshFilteredInfo();
    }

    public static ArrayList<ArticleTypeInfo> GetCommunityList(boolean bForceAll) {
        if(!bForceAll && IsFiltered()) {
            return liFilteredInfo;
        }

        return liArticleTypeInfo;
    }

    public static int GetFilteredIndex(int _nPosNotFiltered) {
        ArticleTypeInfo info = liArticleTypeInfo.get(_nPosNotFiltered);
        Iterator<ArticleTypeInfo> iter = IsFiltered() ? liFilteredInfo.iterator() : liArticleTypeInfo.iterator();
        int idx = 0;
        while(iter.hasNext()){
            if(iter.next().mHashKey == info.mHashKey) {
                return idx;
            }
            idx++;
        }

        return -1;
    }

    public static void ClearRecentArticle(Context context) {
        liRecentArticle.clear();
        StorageHelper.setArrayPref(context, KEY_RECENT_ARTICLES, liRecentArticle);
    }

    public static void LoadReadedArticle(Context context) {
        liReadArticleCheck = new HashSet<>(StorageHelper.getIntArrayPref(context, KEY_READED_ARTICLES));
    }

    public static boolean IsReaded(int hash) {
        return liReadArticleCheck.contains(hash);
    }
    public static void SetRead(Context context, int hash) {
        liReadArticleCheck.add(hash);
        ArrayList<Integer> a = new ArrayList<>(liReadArticleCheck);
        StorageHelper.setArrayPref(context, KEY_READED_ARTICLES, a);
    }

    public static class Options {
        public static final String TUTORIAL_END = "TutorialEnd";

        public static boolean IsTutorialEnd(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean bEnd = prefs.getBoolean(TUTORIAL_END, false);
            return bEnd;
        }

        public static void SetTutorialEnd(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(TUTORIAL_END, true);
            editor.apply();
        }
    }

    public static class AdsTimeChecker {
        public static final String KEY_ADS_TIME_CHECKER = "AdsTimeCheck";
        public static final long DIFF_LIMIT_HOUR = 6;
        public void SaveNow(Context context) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            String strDate = dateFormat.format(date);
            StorageHelper.setPref(context, KEY_ADS_TIME_CHECKER, strDate);
        }

        public boolean IsTimeout(Context context) {
            String sDate = StorageHelper.getPref(context, KEY_ADS_TIME_CHECKER);
            if(TextUtils.isEmpty(sDate)) {
                return true;
            }

            SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
            try {
                Date dateSaved = dateFormat.parse(sDate);
                long now = System.currentTimeMillis();
                Date dateNow = new Date(now);
                long diff = dateNow.getTime() - dateSaved.getTime();
                long diffHour = diff / (60 * 60 * 1000);
                if( BuildConfig.DEBUG ) {
                    diffHour = diff / (60 * 1000);  // 디버그일 때는 분으로
                }
                if(DIFF_LIMIT_HOUR <= diffHour) {
                    return true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return true;
            }

            return false;
        }
    }
}
