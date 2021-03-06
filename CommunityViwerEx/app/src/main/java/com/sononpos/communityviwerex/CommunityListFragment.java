/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sononpos.communityviwerex;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;

import com.sononpos.communityviwerex.Funtional.ThemeManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CommunityListFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;
    private int nLoadOffset = 0;
    private String sNextURL = "";
    private boolean bLoading = false;
    private boolean loadingMore = false;

    private JSONArray jsBackup = new JSONArray();
    private String backupString = "";

    SwipeRefreshLayout fl;
    ListView listView;
    ListViewAdapter lvAdapter;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if(msg.arg1 == 0) {

                String response = (String)msg.obj;

                try {
                    jsBackup.put(nLoadOffset, response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(backupString.isEmpty() || backupString == "") {
                    backupString = "\"" + nLoadOffset + "\":" + response;
                }
                else {
                    backupString += ",\"" + nLoadOffset + "\":" + response;
                }

                try {
                    JSONArray jobj = new JSONArray(response);
                    sNextURL = jobj.getJSONObject(0).getString("next_url");
                    JSONArray jlist = jobj.getJSONObject(0).getJSONArray("list");
                    int len = jlist.length();

                    for(int i = 0 ; i < len ; ++i) {
                        JSONObject obj = jlist.getJSONObject(i);
                        String sJsonString = obj.toString();
                        String sTitle = obj.getString("title");
                        if(sTitle.isEmpty() || sTitle.compareTo("") == 0) continue;
                        String sUserName = obj.getString("username");
                        String sLink = obj.getString("link");
                        String sRegDate = obj.getString("regdate");
                        String sViewCnt = obj.getString("viewcnt");
                        String sCommentCnt = obj.getString("commentcnt");
                        lvAdapter.AddItem(sTitle,sUserName,sRegDate, sViewCnt, sCommentCnt, sLink, sJsonString);
                    }
                }catch(JSONException e) {

                }

                nLoadOffset++;
                lvAdapter.notifyDataSetChanged();
            }
            else if( msg.arg1 == 1) {
                //  최근 글 목록
                ArrayList<String> aJsonList = G.getStringArrayPref(getContext(), G.KEY_RECENT_ARTICLES);
                for (String s : aJsonList) {
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(s);
                        String sTitle = obj.getString("title");
                        if(sTitle.isEmpty() || sTitle.compareTo("") == 0) continue;
                        String sUserName = obj.getString("username");
                        String sLink = obj.getString("link");
                        String sRegDate = obj.getString("regdate");
                        String sViewCnt = obj.getString("viewcnt");
                        String sCommentCnt = obj.getString("commentcnt");
                        lvAdapter.AddItem(sTitle,sUserName,sRegDate, sViewCnt, sCommentCnt, sLink, "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                lvAdapter.notifyDataSetChanged();
            }
            else {
                if( msg.arg1 == -3 ) {
                    if( getContext() != null )
                        Toast.makeText(getContext(), R.string.alert_please_check_network_state, Toast.LENGTH_SHORT).show();
                }
                else {
                    if( getContext() != null )
                            Toast.makeText(getContext(), R.string.alert_unknown_problem, Toast.LENGTH_SHORT).show();
                }
            }

            bLoading = false;
            loadingMore = false;
            fl.setRefreshing(false);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        fl = new SwipeRefreshLayout(getActivity());
        fl.setLayoutParams(params);
        fl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Reload();
            }
        });

        ThemeManager.ThemeColorObject theme = ThemeManager.GetTheme();
        int fontColor = Color.parseColor(theme.BasicFont);
        if(lvAdapter == null) lvAdapter = new ListViewAdapter();
        listView = new ListView(getContext());
        listView.setAdapter(lvAdapter);
        listView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, new int[]{0, fontColor, fontColor, fontColor, 0}));
        listView.setDividerHeight(1);
        View footerView;
        footerView = ((LayoutInflater)getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_footer, null, false);
        if(G.IsShowRecent(getContext())){
            if(position != 0)
                listView.addFooterView(footerView);
        }
        else
            listView.addFooterView(footerView);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(loadingMore)) {
                    LoadMore();
                }
            }
        });

        fl.addView(listView);
        LoadList();

        return fl;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible) {
        }
    }

    void Reload() {
        backupString = "";
        jsBackup = new JSONArray();
        nLoadOffset = 0;
        sNextURL = "";
        lvAdapter.RemoveAll();
        LoadList();
    }

    void LoadMore() {
        if(loadingMore) return;

        if(nLoadOffset==0)  {
            return;
        }


        LoadInner();
    }

    void LoadList() {
        if(bLoading) return;
        if(nLoadOffset != 0) return;


        LoadInner();
    }

    private void LoadInner(){
        final ArrayList<TabItem> aList = Global.obj().getTabItemManager().getList();
        if(aList == null) return;
        if( aList.size() == 0 ) return;
        loadingMore = true;
        bLoading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<TabItem> liTemp = new ArrayList<TabItem>(aList);
                if(G.IsShowRecent(getContext())) {
                    liTemp.add(0,new TICommunity("recent", "최근 본 글", -1));
                }
                if( liTemp.get(position).index == -1 ) {
                    Message msg = handler.obtainMessage();
                    msg.arg1 = 1;
                    handler.sendMessage(msg);
                    return;
                }

                try {
                    URL url;
                    if( nLoadOffset == 0 ) {
                        url = new URL(G.SERV_ROOT + liTemp.get(position).sKey + "/" + (nLoadOffset+1));
                    }
                    else {
                        url = new URL(G.SERV_ROOT + liTemp.get(position).sKey + "/" + sNextURL);
                    }

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);

                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if( responseCode == HttpURLConnection.HTTP_OK){
                        InputStream is   = null;
                        ByteArrayOutputStream baos = null;
                        String response;
                        is = conn.getInputStream();
                        baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int nLength = 0;

                        while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, nLength);
                        }
                        byteData = baos.toByteArray();
                        response = new String(byteData);

                        Message msg = handler.obtainMessage();
                        msg.arg1 = 0;
                        msg.obj = (Object)response;
                        handler.sendMessage(msg);
                    }
                    else {
                        Message msg = handler.obtainMessage();
                        msg.arg1 = -3;
                        handler.sendMessage(msg);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                    Message msg = handler.obtainMessage();
                    msg.arg1 = -3;
                    handler.sendMessage(msg);
                }catch(IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Message msg = handler.obtainMessage();
                    msg.arg1 = -3;
                    handler.sendMessage(msg);
                }
                finally {
                }
            }
        }).start();
    }
    int getPageNum() {
        return position;
    }
}