package com.sononpos.communityviwerex;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sononpos.communityviwerex.CommunityTypeInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by nnnyy on 2017-03-06.
 */
public class G {
    public static ArrayList<CommunityTypeInfo> liCommTypeInfo = new ArrayList<>();
    public static ArrayList<CommunityTypeInfo> liFilteredInfo = new ArrayList<>();
    public static HashSet<String> liFiltered = new HashSet<>();
    public static final String SERV_ROOT = "http://52.79.205.198:3000/";
    public static final String FILTERED_COMM = "FilteredCommunity";

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
        if(IsFiltered()) return liFilteredInfo;

        return liCommTypeInfo;
    }
}
