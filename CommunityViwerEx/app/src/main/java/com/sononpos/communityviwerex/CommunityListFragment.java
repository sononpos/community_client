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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sononpos.communityviwerex.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommunityListFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;
    private int nLoadOffset = 0;
    private boolean bLoading = false;
    private boolean loadingMore = false;
    SwipeRefreshLayout fl;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            nLoadOffset++;
            bLoading = false;
            loadingMore = false;
            fl.setRefreshing(false);
            lvAdapter.notifyDataSetChanged();
        }
    };

    ListView listView;
    ListViewAdapter lvAdapter;

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
                nLoadOffset = 0;
                lvAdapter.RemoveAll();
                LoadList();
            }
        });

        if(lvAdapter == null) lvAdapter = new ListViewAdapter();
        listView = new ListView(getContext());
        listView.setAdapter(lvAdapter);
        View footerView;
        footerView = ((LayoutInflater)getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_footer, null, false);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(1000);
                view.startAnimation(animation1);

                ListViewItem item = (ListViewItem)parent.getItemAtPosition(position);
                System.out.println("Item Clicked : " + item.m_sLink);
                Intent intent = new Intent(getActivity(), CommunityArticle.class);
                intent.putExtra("URL", item.m_sLink);
                startActivity(intent);
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
            System.out.println("##########################" + position + "###########################");
        }
    }

    void LoadMore() {
        if(loadingMore) return;

        if(nLoadOffset==0)  {
            LoadList();
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
        if( G.liCommTypeInfo.size() == 0 ) return;
        loadingMore = true;
        bLoading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(G.SERV_ROOT + G.liCommTypeInfo.get(position).sKey + "/" + (nLoadOffset+1));

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

                        System.out.println(response);
                        JSONArray jobj = new JSONArray(response);
                        JSONArray jlist = jobj.getJSONObject(0).getJSONArray("list");
                        int len = jlist.length();

                        for(int i = 0 ; i < len ; ++i) {
                            JSONObject obj = jlist.getJSONObject(i);
                            String sTitle = obj.getString("title");
                            String sUserName = obj.getString("username");
                            String sLink = obj.getString("link");
                            String sRegDate = obj.getString("regdate");
                            String sViewCnt = obj.getString("viewcnt");
                            String sCommentCnt = obj.getString("commentcnt");
                            lvAdapter.AddItem(sTitle,sUserName,sRegDate, sViewCnt, sCommentCnt, sLink);
                        }

                        Message msg = handler.obtainMessage();
                        handler.sendMessage(msg);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }catch(JSONException e) {
                    e.printStackTrace();
                }finally {
                }
            }
        }).start();
    }
    int getPageNum() {
        return position;
    }
}