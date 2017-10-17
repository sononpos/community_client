package com.sononpos.allcommunity.youtube;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by nnnyyy on 2017-09-05.
 */

public class YoutubeArticleItem implements Serializable {
    public String id;
    public String title;
    public String thumbnail;
    public String viewcnt;
    public String commentcnt;
    public String regdate;

    public YoutubeArticleItem() {
    }

    public void set(JSONObject obj) {
        try{
            id = obj.getString("id");
            title = obj.getString("title");
            thumbnail = obj.getString("thumnails");
            viewcnt = obj.getString("viewCnt");
            commentcnt = obj.getString("commentCnt");
            regdate = obj.getString("regdate");

        }catch(JSONException e) {
            e.printStackTrace();
        }
    }
}

