package com.sononpos.allcommunity.Funtional;

import com.sononpos.allcommunity.ArticleItem;
import com.sononpos.allcommunity.ArticlesListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nnnyy on 2017-09-06.
 */

public class ArticleParsingHelper {
    public static boolean parse(String sResponse, ArticlesListFragment.ArticleLoadInfo info, ArrayList<ArticleItem> aList) {
        if(aList == null) return false;
        try {
            JSONArray arrRoot = new JSONArray(sResponse);
            JSONObject oRoot = arrRoot.getJSONObject(0);
            int nURL = oRoot.getInt("next_url");
            info.sNext = String.valueOf(nURL);
            JSONArray arrList = oRoot.getJSONArray("list");
            int nListLen = arrList.length();
            for(int i = 0 ; i < nListLen ; ++i) {
                JSONObject oItem = arrList.getJSONObject(i);
                ArticleItem newItem = new ArticleItem();
                newItem.m_sTitle = oItem.getString("title");
                newItem.m_sLink = oItem.getString("link");
                newItem.m_sName = oItem.getString("username");
                newItem.m_sRegDate = oItem.getString("regdate");
                newItem.m_sViewCnt = oItem.getString("viewcnt");
                newItem.m_sCommentCnt = oItem.getString("commentcnt");
                newItem.m_sJsonString = oItem.getString("linkencoding");
                aList.add(newItem);
            }
        }catch(JSONException e) {
            return false;
        }

        return true;
    }
}
