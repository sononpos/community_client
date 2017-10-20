package com.sononpos.communityviwerex;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by nnnyyy on 2017-03-03.
 */

public class ListViewItem implements Serializable {
    public ListViewItem() {

    }

    public ListViewItem(String sTitle, String sName, String sDate, String sViewCnt, String sCommentCnt, String sLink, String sJson) {
        m_sTitle = sTitle;
        m_sName = sName;
        m_sRegDate = sDate;
        m_sViewCnt = sViewCnt;
        m_sCommentCnt = sCommentCnt;
        m_sLink = sLink;
        m_sJsonString = sJson;
    }

    public String m_sTitle;
    public String m_sName;
    public String m_sRegDate;
    public String m_sViewCnt;
    public String m_sCommentCnt;
    public String m_sLink;
    public String m_sJsonString;

    public JSONObject makeJsonObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("title", m_sTitle);
            obj.put("name", m_sName);
            obj.put("date", m_sRegDate);
            obj.put("link", m_sLink);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
