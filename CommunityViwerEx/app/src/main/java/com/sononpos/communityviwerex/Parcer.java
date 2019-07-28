package com.sononpos.communityviwerex;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by nnnyy on 2017-10-17.
 */

public class Parcer {
    static boolean communityList(String sResponse, TabItemManager tim) {
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(sResponse);
            Iterator<String> iter = jobj.keys();
            while(iter.hasNext()){
                String key = iter.next();
                JSONObject element = jobj.getJSONObject(key);
                String sName = element.getString("name");
                int nIndex = element.getInt("index");
                TabItem newItem = new TICommunity(key, sName, nIndex);
                tim.addItem(newItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
