package com.sononpos.communityviwerex;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by nnnyy on 2017-10-17.
 */

public class TabItemManager {
    public static final String KEY_FILTERED = "hc_filter";

    ArrayList<TabItem>  items;
    ArrayList<TabItem>  items_without_filtered;
    HashSet<String>    filteredKey;
    public TabItemManager(){
    }

    public void init() {
        items = new ArrayList<>();
        items_without_filtered = new ArrayList<>();
        filteredKey = new HashSet<>();
    }

    public void addItem(TabItem newItem) {
        items.add(newItem);
    }

    public void refreshList(Context context) {
        int nListCnt = items.size();
        JSONArray aSequance = new JSONArray();
        for(int i = 0 ; i < nListCnt ; ++i) {
            JSONObject obj = new JSONObject();
            TabItem item = items.get(i);
            try {
                item.index = i;
                obj.put("key", item.getKey());
                obj.put("idx", item.index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            aSequance.put(obj);
        }

        Iterator iter = items.iterator();
        items_without_filtered.clear();
        while(iter.hasNext()) {
            TabItem item = (TabItem) iter.next();
            if(isFiltered(item.getKey())){
                continue;
            }
            items_without_filtered.add(item);
        }

        Storage.save(context, "TabItemListSeq", aSequance.toString());
    }

    public void setSeq(String sSeqString) {
        HashMap<String, Integer> map = new HashMap<>();
        try {
            JSONArray aSeq = new JSONArray(sSeqString);
            int len = aSeq.length();
            for(int i = 0 ; i < len ; ++i) {
                JSONObject obj = aSeq.getJSONObject(i);
                String sKey = obj.getString("key");
                int nIdx = obj.getInt("idx");
                map.put(sKey, nIdx);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Iterator<TabItem> iter = items.iterator();
        while(iter.hasNext()) {
            TabItem item = iter.next();
            if(map.containsKey(item.getKey())) {
                item.index = map.get(item.getKey());
            }
        }
    }

    public void addFilter(String key) {
        filteredKey.add(key);
    }

    public void removeFilter(String key) {
        filteredKey.remove(key);
    }

    public boolean isFiltered(String key) {
        return filteredKey.contains(key);
    }

    public void setFilteredList(String sJson) {
        try {
            JSONArray aList = new JSONArray(sJson);
            for(int i = 0 ; i < aList.length() ; ++i) {
                filteredKey.add(aList.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String makeFilteredList() {
        JSONArray aList = new JSONArray(filteredKey);
        return aList.toString();
    }

    public void sort() {
        Collections.sort(items, new Comparator<TabItem>() {
            @Override
            public int compare(TabItem o1, TabItem o2) {
                return o1.index < o2.index ? -1 : 1;
            }
        });

        Collections.sort(items_without_filtered, new Comparator<TabItem>() {
            @Override
            public int compare(TabItem o1, TabItem o2) {
                return o1.index < o2.index ? -1 : 1;
            }
        });
    }

    public ArrayList<TabItem> getListWithoutFiltered() { return items_without_filtered; }
    public ArrayList<TabItem> getListAll() { return items; }
    public ArrayList<TabItem> getList() {
        if(filteredKey.size() > 0) {
            return items_without_filtered;
        }
        return items;
    }
}
