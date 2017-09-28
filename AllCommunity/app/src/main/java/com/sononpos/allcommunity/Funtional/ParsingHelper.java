package com.sononpos.allcommunity.Funtional;

import com.sononpos.allcommunity.ArticleItem;
import com.sononpos.allcommunity.ArticlesFragment.ArticlesListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by nnnyy on 2017-09-06.
 */

public class ParsingHelper {
    public static class Article {
        public static boolean parse(String sResponse, ArticlesListFragment.ArticleLoadInfo info, ArrayList<ArticleItem> aList) {
            if(aList == null) return false;
            try {
                JSONArray arrRoot = new JSONArray(sResponse);
                JSONObject oRoot = arrRoot.getJSONObject(0);
                int nURL = oRoot.getInt("next_page");
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

    public static class News_Most {
        public static boolean parse(String sRes, ArrayList<ArticleItem> aList) {
            if(aList == null) return false;

            try {
                JSONObject oRoot = new JSONObject(sRes);
                JSONArray aMost = oRoot.getJSONArray("data");
                for(int i = 0 ; i < aMost.length() ; ++i) {
                    JSONObject oItem = aMost.getJSONObject(i);
                    int nRank = oItem.getInt("rank");
                    ArticleItem ai = new ArticleItem();
                    ai.m_sTitle = oItem.getString("item");
                    ai.m_sName = String.valueOf(nRank);
                    try {
                        String sURLEncoded = URLEncoder.encode(ai.m_sTitle, "utf-8");
                        ai.m_sLink = "https://search.naver.com/search.naver?where=nexearch&query=" + sURLEncoded + "&sm=top_lve&ie=utf8";
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    aList.add(ai);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }

    public static class News_Detail {
        public static boolean parse(String sRes, ArrayList<ArticleItem> aList) {
            if(aList == null) return false;

            try {
                JSONObject oRoot = new JSONObject(sRes);
                JSONArray aMost = oRoot.getJSONArray("data");
                for(int i = 0 ; i < aMost.length() ; ++i) {
                    JSONObject oItem = aMost.getJSONObject(i);
                    int nRank = oItem.getInt("rank");
                    String sText = oItem.getString("item");
                    LogHelper.di("Rank " + nRank + ", Text" + sText );
                    ArticleItem ai = new ArticleItem();
                    ai.m_sTitle = oItem.getString("item");
                    aList.add(ai);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }
    }
}
