package com.sononpos.allcommunity.youtube;

import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.sononpos.allcommunity.Global;
import com.sononpos.allcommunity.HttpHelper.HttpHelper;
import com.sononpos.allcommunity.HttpHelper.HttpHelperListener;
import com.sononpos.allcommunity.R;
import com.sononpos.allcommunity.databinding.ActivityVideoPlayerBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VideoPlayerActivity extends YouTubeFailureRecoveryActivity implements HttpHelperListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
            ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

    ActivityVideoPlayerBinding mBind;
    private String videoID = "";
    private YouTubePlayerView playerView;
    private YouTubePlayer player;

    private boolean bLoadingNext = false;
    private HttpHelper httpHelper = new HttpHelper();
    private String nextToken = "";
    ResultHandler descRetHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_video_player);
        mBind.setActivity(this);

        descRetHandler = new ResultHandler();

//        videoID = getIntent().getStringExtra("videoID");
        videoID = "62EOiH9gjc8";
        String sTitle = getIntent().getStringExtra("titleString");
        mBind.youtubeView.initialize(Global.ANDROID_KEY, this);
        httpHelper.SetListener(this);
        LoadComments();
    }

    @Override
    public void onRefresh() {
        nextToken = "";
        bLoadingNext = false;
        LoadComments();
    }

    @Override
    public void onResponse(int nType, int nErrorCode, String sResponse) {
        if(nErrorCode != 0) {
            // Error
            Message msg = descRetHandler.obtainMessage(nErrorCode);
            msg.sendToTarget();
            return;
        }
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(sResponse);
            int nRet = jsonObj.getInt("ret");
            if(nRet != 0) {
                Message msg = descRetHandler.obtainMessage(-1);
                msg.sendToTarget();
                return;
            }
            if(!jsonObj.isNull("nextToken"))
                nextToken = jsonObj.getString("nextToken");

            JSONArray arrContents = jsonObj.getJSONArray("contents");
            int len = arrContents.length();
            if(len <= 0) {
                Message msg = descRetHandler.obtainMessage(-1);
                msg.sendToTarget();
                return;
            }
            for(int i = 0 ; i < len ; ++i) {
//                CommentInfo ci = new CommentInfo();
//                JSONObject content = arrContents.getJSONObject(i);
//                ci.sComment = content.getString("comment");
//                ci.sAuthName = content.getString("authname");
//                ci.sLikeCnt = content.getString("likecnt");
//                ci.sPDate = content.getString("pdate");
//                ci.sUDate = content.getString("udate");
//                adapter.addItem(ci);
            }

            Message msg = descRetHandler.obtainMessage(0);
            msg.sendToTarget();
        } catch (JSONException e) {
            e.printStackTrace();
            Message msg = descRetHandler.obtainMessage(-1);
            msg.sendToTarget();
        }
    }

    class ResultHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what != 0) {
                //Error 처리
//                srl_youtubeList.setRefreshing(false);
                return;
            }

//            adapter.notifyDataSetChanged();
            bLoadingNext = false;
//            srl_youtubeList.setRefreshing(false);
        }
    }



    private void LoadComments() {
//        if(bLoadingNext) return;
//        bLoadingNext = true;
//
//        String urlRet = GVal.URL_Comments + videoID;
//        if(!nextToken.isEmpty()) {
//            urlRet += "&pageToken=" + nextToken;
//        }
//        else {
//            adapter.removeAll();
//        }
//        urlRet += "&regionCode=" + GVal.regionCode;
//        httpHelper.Request(0, urlRet);
        //Message msg = uiHandler.obtainMessage();
        //msg.sendToTarget();
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return null;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        player = youTubePlayer;
        int controlFlags = player.getFullscreenControlFlags();
        setRequestedOrientation(PORTRAIT_ORIENTATION);
        controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
        player.setFullscreenControlFlags(controlFlags);

        if (!wasRestored) {
            youTubePlayer.loadVideo(videoID);
            youTubePlayer.play();
        }
    }

    @Override
    protected void onResume() {

        if(player != null) {
            player.loadVideo(videoID);
            player.play();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        if(player != null) {
            player.release();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        if( player != null ) {
            player.release();
        }

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if( player != null ) {
            player.release();
        }
        player = null;
        super.onStop();
    }
}
