package com.sononpos.allcommunity.youtube;

import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.youtube.player.YouTubePlayer;
import com.sononpos.allcommunity.Funtional.LogHelper;
import com.sononpos.allcommunity.Global;
import com.sononpos.allcommunity.HttpHelper.HttpHelper;
import com.sononpos.allcommunity.HttpHelper.HttpHelperListener;
import com.sononpos.allcommunity.R;
import com.sononpos.allcommunity.RecyclerAdapter.YoutubeCommentsRecyclerAdapter;
import com.sononpos.allcommunity.SimpleDividerItemDecoration;
import com.sononpos.allcommunity.databinding.ActivityVideoPlayerBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoPlayerActivity extends YouTubeFailureRecoveryActivity implements HttpHelperListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
            ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

    ActivityVideoPlayerBinding mBind;
    private String videoID = "";
    private YouTubePlayer player;

    private boolean bLoadingNext = false;
    private boolean bLoadingFirst = true;
    private String nextToken = "";

    private HttpHelper httpHelper = new HttpHelper();
    Handler mHanlder = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBind = DataBindingUtil.setContentView(this, R.layout.activity_video_player);
        mBind.setActivity(this);

        videoID = getIntent().getStringExtra("id");
        mBind.tvVideoTitle.setText(getIntent().getStringExtra("title"));
        mBind.youtubeView.initialize(Global.ANDROID_KEY, this);
        httpHelper.SetListener(this);
        mBind.rvComments.setHasFixedSize(true);
        mBind.rvComments.setLayoutManager(new LinearLayoutManager(mBind.getRoot().getContext()));
        mBind.rvComments.addItemDecoration(new SimpleDividerItemDecoration(mBind.getRoot().getContext()));
        mBind.rvComments.setAdapter(new YoutubeCommentsRecyclerAdapter());
        mBind.rvComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(bLoadingNext) return;

                int lastVisibleItemPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount) {
                    LoadComments();
                }
            }
        });

        mBind.slRv.setOnRefreshListener(this);
        LoadComments();
    }

    @Override
    public void onRefresh() {
        nextToken = "";
        bLoadingNext = false;
        bLoadingFirst = true;
        LoadComments();
    }

    private void LoadComments() {
        if(bLoadingNext) return;
        bLoadingNext = true;

        if(!bLoadingFirst && nextToken.isEmpty()) {
            return;
        }

        bLoadingFirst = false;

        String urlRet = Global.YOUTUBE_COMMENT_ROOT + videoID;
        LogHelper.di("nextToken : " + nextToken);
        if(!nextToken.isEmpty()) {
            urlRet += "&pageToken=" + nextToken;
        }
        else {
            YoutubeCommentsRecyclerAdapter adapter = (YoutubeCommentsRecyclerAdapter)mBind.rvComments.getAdapter();
            adapter.ClearList();
        }
        //urlRet += "&regionCode=" + GVal.regionCode;
        httpHelper.Request(0, urlRet);
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

    @Override
    public void onResponse(int nType, int nErrorCode, String sResponse) {
        final YoutubeCommentsRecyclerAdapter adapter = (YoutubeCommentsRecyclerAdapter)mBind.rvComments.getAdapter();
        if(nErrorCode != 0) {
            mHanlder.post(new Runnable() {
                @Override
                public void run() {
                    //   Error Message
                    adapter.notifyDataSetChanged();
                    mBind.slRv.setRefreshing(false);
                }
            });
            return;
        }
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(sResponse);
            int nRet = jsonObj.getInt("ret");
            if(nRet != 0) {
                mHanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        //   Error Message
                        adapter.notifyDataSetChanged();
                        mBind.slRv.setRefreshing(false);
                    }
                });
                return;
            }
            if(!jsonObj.isNull("nextToken"))
                nextToken = jsonObj.getString("nextToken");

            JSONArray arrContents = jsonObj.getJSONArray("contents");
            int len = arrContents.length();
            if(len <= 0) {
                mHanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        //   Error Message
                        adapter.notifyDataSetChanged();
                        mBind.slRv.setRefreshing(false);
                    }
                });
                return;
            }
            ArrayList<CommentInfo> aList = new ArrayList<>();
            for(int i = 0 ; i < len ; ++i) {
                CommentInfo ci = new CommentInfo();
                JSONObject content = arrContents.getJSONObject(i);
                ci.sComment = content.getString("comment");
                ci.sAuthName = content.getString("authname");
                ci.sLikeCnt = content.getString("likecnt");
                ci.sPDate = content.getString("pdate");
                ci.sUDate = content.getString("udate");
                aList.add(ci);
            }

            adapter.AddList(aList);
            mHanlder.post(new Runnable() {
                @Override
                public void run() {
                    bLoadingNext = false;
                    adapter.notifyDataSetChanged();
                    mBind.slRv.setRefreshing(false);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            mHanlder.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    mBind.slRv.setRefreshing(false);
                }
            });
        }
    }
}
