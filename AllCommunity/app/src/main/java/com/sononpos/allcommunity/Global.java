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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by nnnyy on 2017-10-05.
 */

public class Global {
    public static final String SERV_ROOT = "https://hotcommunity-163106.appspot.com/community/";
    public static final String YOUTUBE_ROOT = "https://hotcommunity-163106.appspot.com/youtube/";
    public static final String YOUTUBE_COMMENT_ROOT = "https://hotcommunity-163106.appspot.com/youtube/comments/";
    public static final String KEY_RECENT_ARTICLES = "RecentArticles";
    public static final String KEY_TUTORIAL_COMPLETE = "tutorial_complete";
    public static final String ANDROID_KEY = "AIzaSyAgOtMxWNk2NmaCsiBynf8O7kBty9SXPrk";
    public static String deviceVer = "1.0";

    private static Global       instance;
    private ArticleListManager  artListMan;
    private AdsTimeChecker      adsTimeChecker;
    private Options             options;

    private Global(){}
    public static Global getInstance() {
        if(instance == null) {
            synchronized (Global.class) {
                if( instance == null ) {
                    instance = new Global();
                }
            }
        }

        return instance;
    }

    public void setDeviceVer(String ver) { deviceVer = ver; }
    public String getDeviceVer() { return deviceVer; }
    public ArticleListManager getListMan() { return artListMan; }
    public AdsTimeChecker getAdsTimeChecker() { return adsTimeChecker; }
    public Options getOptions() { return options; }

    public void Init() {
        adsTimeChecker = new AdsTimeChecker();
        options = new Options();
        artListMan = new ArticleListManager();
        artListMan.Init();
    }

    public class ArticleListManager {
        public static final String KEY_FILTERED_COMM = "FilteredCommunity";
        public static final String KEY_READED_ARTICLES = "ReadedArticles";

        private ArrayList<ArticleTypeInfo> liArticleTypeInfo;   //  항목 정보
        private ArrayList<ArticleTypeInfo> liFilteredInfo;      //  필터링 후 항목 정보
        private HashSet<Integer> liFiltered;                    //  필터링 된 해시 목록
        private HashSet<Integer> liReadArticleCheck;

        public void Init() {
            liArticleTypeInfo = new ArrayList<>();
            liFilteredInfo = new ArrayList<>();
            liFiltered = new HashSet<>();
            liReadArticleCheck = new HashSet<>();
        }

        public boolean parseListResponse(String res) {
            if(liArticleTypeInfo == null) return false;

            JSONObject jobj;
            try {
                jobj = new JSONObject(res);
                Iterator<String> iter = jobj.keys();
                while(iter.hasNext()){
                    String key = iter.next();
                    JSONObject element = jobj.getJSONObject(key);
                    String sName = element.getString("name");
                    int nIndex = element.getInt("index");
                    liArticleTypeInfo.add(new CommunityTypeInfo(key, sName, nIndex));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                liArticleTypeInfo.clear();
                return false;
            }

            liArticleTypeInfo.add(new NewsTypeInfo("news","실시간 인기 검색어",0));
            return true;
        }

        public void loadFilteredList(Context context) {
            if(liFiltered == null) return;
            liFiltered.clear();
            ArrayList<Integer> aFiltered = StorageHelper.getIntArrayPref(context, KEY_FILTERED_COMM);
            if(aFiltered != null)
                liFiltered = new HashSet<>(aFiltered);
            refreshListWithFiltered();
        }

        public void loadReadList(Context context) {
            liReadArticleCheck = new HashSet<>(StorageHelper.getIntArrayPref(context, KEY_READED_ARTICLES));
        }

        public void sort() {
            Collections.sort(liArticleTypeInfo, new Comparator<ArticleTypeInfo>() {
                @Override
                public int compare(ArticleTypeInfo o1, ArticleTypeInfo o2) {
                    if(o1.GetType() != ArticleTypeInfo.TYPE_COMMUNITY) return -1;
                    if(o2.GetType() != ArticleTypeInfo.TYPE_COMMUNITY) return 1;

                    return ((CommunityTypeInfo)o1).index < ((CommunityTypeInfo)o2).index ? -1 : 1;
                }
            });

            Collections.sort(liFilteredInfo, new Comparator<ArticleTypeInfo>() {
                @Override
                public int compare(ArticleTypeInfo o1, ArticleTypeInfo o2) {
                    if(o1.GetType() != ArticleTypeInfo.TYPE_COMMUNITY) return -1;
                    if(o2.GetType() != ArticleTypeInfo.TYPE_COMMUNITY) return 1;

                    return ((CommunityTypeInfo)o1).index < ((CommunityTypeInfo)o2).index ? -1 : 1;
                }
            });
        }

        public void refreshListWithFiltered() {
            liFilteredInfo.clear();
            if(liFiltered.size() == 0 ) return;

            for (ArticleTypeInfo articleTypeInfo : liArticleTypeInfo) {
                if(!liFiltered.contains(articleTypeInfo.mHashKey)) {
                    liFilteredInfo.add(articleTypeInfo);
                }
            }
        }

        public String getListURL() {
            return "https://hotcommunity-163106.appspot.com/community/list";
        }

        public boolean isFiltered() {
            return (liFiltered.size() != 0);
        }
        public boolean isFilteredKey(int nKey) {
            return liFiltered.contains(nKey);
        }
        public void toggleFilter(int key) {
            if(liFiltered.contains(key)){
                liFiltered.remove(key);
            }
            else {
                liFiltered.add(key);
            }
        }
        public void saveFiltered(Context context) {
            StorageHelper.setArrayPref(context, KEY_FILTERED_COMM, new ArrayList<>(liFiltered));
        }

        public ArrayList<ArticleTypeInfo> getCommunityList(boolean bForceAll) {
            if(!bForceAll && isFiltered()) {
                return liFilteredInfo;
            }

            return liArticleTypeInfo;
        }

        public int getFilteredIndex(int _nPosNotFiltered) {
            ArticleTypeInfo info = liArticleTypeInfo.get(_nPosNotFiltered);
            Iterator<ArticleTypeInfo> iter = isFiltered() ? liFilteredInfo.iterator() : liArticleTypeInfo.iterator();
            int idx = 0;
            while(iter.hasNext()){
                if(iter.next().mHashKey == info.mHashKey) {
                    return idx;
                }
                idx++;
            }

            return -1;
        }

        public String makePageURL(String key, String page) {
            return SERV_ROOT + key + page;
        }

        public boolean isReaded(int hash) {
            return liReadArticleCheck.contains(hash);
        }
        public void setRead(Context context, int hash) {
            liReadArticleCheck.add(hash);
            ArrayList<Integer> a = new ArrayList<>(liReadArticleCheck);
            StorageHelper.setArrayPref(context, KEY_READED_ARTICLES, a);
        }

    }


    public class Options {
        public static final String TUTORIAL_END = "TutorialEnd";

        public boolean IsTutorialEnd(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean bEnd = prefs.getBoolean(TUTORIAL_END, false);
            return bEnd;
        }

        public void SetTutorialEnd(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(TUTORIAL_END, true);
            editor.apply();
        }
    }

    public class AdsTimeChecker {
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
