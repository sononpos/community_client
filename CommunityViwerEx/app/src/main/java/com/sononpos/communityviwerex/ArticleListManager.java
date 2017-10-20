package com.sononpos.communityviwerex;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

/**
 * Created by nnnyyy on 2017-10-18.
 */

class ArticleListManager {
    public final static String KEY_RECENT_ARTICLE = "hc_recent";
    public final static String KEY_FAVORITE_ARTICLE = "hc_favorate";
    ArrayList<ListViewItem> aRecentItems = new ArrayList<>();
    Vector<ListViewItem> aFavorateItems = new Vector<>();
    HashSet<Integer> aFavorateHash = new HashSet<>();

    public void loadRecent(String json) {
        aRecentItems.clear();
        try {
            JSONArray aList = new JSONArray(json);
            int nLen = aList.length();
            for(int i = 0 ; i < nLen ; ++i) {
                JSONObject obj = aList.getJSONObject(i);
                String sTitle = obj.getString("title");
                String sName = obj.getString("name");
                String sDate = obj.getString("date");
                String sLink = obj.getString("link");
                ListViewItem item = new ListViewItem(sTitle, sName, sDate, "", "", sLink, "");
                aRecentItems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addRecent(ListViewItem item) {
        aRecentItems.add(0, item);
        if(aRecentItems.size() > 60) {
            aRecentItems.remove(aRecentItems.size()-1);
        }
    }

    public ArrayList<ListViewItem> getRecentList() {
        return aRecentItems;
    }

    public String makeRecentJsonString() {
        JSONArray aList = new JSONArray();
        for(int i = 0 ; i < aRecentItems.size() ; ++i) {
            JSONObject obj = aRecentItems.get(i).makeJsonObject();
            aList.put(obj);
        }

        return aList.toString();
    }

    public void saveRecent(Context context) {
        Storage.save(context, KEY_RECENT_ARTICLE, makeRecentJsonString());
    }

    public void clearRecent(Context context) {
        aRecentItems.clear();
        saveRecent(context);
    }


    public void loadFavorate(String json) {
        aFavorateItems.clear();
        try {
            JSONArray aList = new JSONArray(json);
            int nLen = aList.length();
            for(int i = 0 ; i < nLen ; ++i) {
                JSONObject obj = aList.getJSONObject(i);
                String sTitle = obj.getString("title");
                String sName = obj.getString("name");
                String sDate = obj.getString("date");
                String sLink = obj.getString("link");
                ListViewItem item = new ListViewItem(sTitle, sName, sDate, "", "", sLink, "");
                aFavorateItems.add(item);
                aFavorateHash.add(item.m_sTitle.hashCode());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addFavorate(ListViewItem item) {
        aFavorateItems.add(item);
        aFavorateHash.add(item.m_sTitle.hashCode());
    }

    public Vector<ListViewItem> getFavorateList() {
        return aFavorateItems;
    }

    public String makeFavorateJsonString() {
        JSONArray aList = new JSONArray();
        for(int i = 0 ; i < aFavorateItems.size() ; ++i) {
            JSONObject obj = aFavorateItems.get(i).makeJsonObject();
            aList.put(obj);
        }

        return aList.toString();
    }

    public void saveFavorate(Context context) {
        Storage.save(context, KEY_FAVORITE_ARTICLE, makeFavorateJsonString());
    }

    public void clearFavorate(Context context) {
        aFavorateItems.clear();
        saveRecent(context);
    }

    public void removeFavorate(Context context, int idx) {
        ListViewItem item = aFavorateItems.get(idx);
        aFavorateHash.remove(item.m_sTitle.hashCode());
        aFavorateItems.remove(idx);
        saveRecent(context);
    }

    public boolean isFavorated(int _hash) {
        return aFavorateHash.contains(_hash);
    }

}
