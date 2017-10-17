package com.sononpos.communityviwerex;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by nnnyy on 2017-10-17.
 */

public class TabItemManager {
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

    public void refreshList() {
        Iterator iter = items.iterator();
        items_without_filtered.clear();
        while(iter.hasNext()) {
            TabItem item = (TabItem) iter.next();
            if(isFiltered(item.getKey())){
                continue;
            }
            items_without_filtered.add(item);
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
                if(o1 instanceof TICommunity &&
                        o2 instanceof TICommunity) {
                    return ((TICommunity)o1).index < ((TICommunity)o2).index ? -1 : 1;
                }
                return -1;
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
