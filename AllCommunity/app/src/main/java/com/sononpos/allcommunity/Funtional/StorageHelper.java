package com.sononpos.allcommunity.Funtional;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by nnnyy on 2017-09-08.
 */

public class StorageHelper {
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
}
