package com.sononpos.communityviwerex;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nnnyyy on 2017-10-18.
 */

class ArticleListManager {
    public final static String KEY_RECENT_ARTICLE = "hc_recent";
    ArrayList<ListViewItem> aRecentItems = new ArrayList<>();

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

    public void add(ListViewItem item) {
        aRecentItems.add(0, item);
        if(aRecentItems.size() > 60) {
            aRecentItems.remove(aRecentItems.size()-1);
        }
    }

    public ArrayList<ListViewItem> getList() {
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
}
