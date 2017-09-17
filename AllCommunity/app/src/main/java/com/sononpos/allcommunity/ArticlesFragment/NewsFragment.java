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

package com.sononpos.allcommunity.ArticlesFragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sononpos.allcommunity.ArticleItem;
import com.sononpos.allcommunity.Funtional.LogHelper;
import com.sononpos.allcommunity.Funtional.ParsingHelper;
import com.sononpos.allcommunity.HttpHelper.HttpHelper;
import com.sononpos.allcommunity.HttpHelper.HttpHelperListener;
import com.sononpos.allcommunity.R;
import com.sononpos.allcommunity.RecyclerAdapter.NewsListRecyclerAdapter;
import com.sononpos.allcommunity.SimpleDividerItemDecoration;
import com.sononpos.allcommunity.databinding.FragmentNewsListBinding;

import java.util.ArrayList;

public class NewsFragment extends Fragment implements HttpHelperListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String NEWS_SERV = "http://52.79.205.198:7888/most";
    public static final String NEWS_DETAIL = "http://52.79.205.198:7888/news/";
    FragmentNewsListBinding mBind;
    HttpHelper httpHelper = new HttpHelper();
    NotifyHandler handler = new NotifyHandler();
    ArrayList<String> items = new ArrayList<>();
    boolean bLoading = false;

    public NewsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_news_list, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        httpHelper.SetListener(this);

        mBind.swipeRefreshLayout.setOnRefreshListener(this);

        mBind.articlesList.setHasFixedSize(true);
        mBind.articlesList.setLayoutManager(new LinearLayoutManager(mBind.getRoot().getContext()));
        mBind.articlesList.setAdapter(new NewsListRecyclerAdapter());
        mBind.articlesList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        mBind.articlesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /*
                if(loadInfo.isLoading()) return;

                int lastVisibleItemPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount) {
                    LoadContents();
                }
                */
            }
        });

        LoadMost();
    }

    @Override
    public void onResponse(int nType, int nErrorCode, String sResponse) {
        switch(nType) {
            case 0: ProcLoadMost(nErrorCode, sResponse); break;
            case 1: ProcLoadNews(nErrorCode, sResponse); break;
        }
    }

    @Override
    public void onRefresh() {
        if(bLoading) {
            mBind.swipeRefreshLayout.setRefreshing(false);
            return;
        }
        ((NewsListRecyclerAdapter)mBind.articlesList.getAdapter()).ClearList();
        LoadMost();
    }

    protected void ProcLoadMost(int nErrorCode, String sRes) {
        ArrayList<ArticleItem> list = new ArrayList<>();
        if(ParsingHelper.News_Most.parse(sRes, list)) {
            items.clear();
            for(ArticleItem item : list ) {
                items.add(item.m_sTitle);
            }
            NewsListRecyclerAdapter adapter = (NewsListRecyclerAdapter)mBind.articlesList.getAdapter();
            adapter.AddList(list);

            Message msg = handler.obtainMessage(0);
            msg.arg1 = nErrorCode;
            handler.sendMessage(msg);
        }
    }

    protected void ProcLoadNews(int nErrorCode, String sRes) {
        ArrayList<ArticleItem> list = new ArrayList<>();
        LogHelper.di(sRes);
        if(ParsingHelper.News_Detail.parse(sRes, list)) {

        }
    }

    /*
    protected void SetSpinnerData(ArrayList<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, list);
        mBind.mostSpinner.setAdapter(adapter);

        mBind.mostSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(), (String)mBind.mostSpinner.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                LoadNews((String)mBind.mostSpinner.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    */

    protected void LoadMost() {
        if(bLoading) return;
        bLoading = true;
        httpHelper.Request(0, NEWS_SERV);
    }

    protected void LoadNews(String sWord) {
        httpHelper.Request(1, NEWS_DETAIL + sWord);
    }

    class NotifyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            bLoading = false;

            switch(msg.what) {
                case 0:
                {
                    if(msg.arg1 != 0) {
                        return;
                    }

                    mBind.articlesList.getAdapter().notifyDataSetChanged();
                    mBind.swipeRefreshLayout.setRefreshing(false);

                    //SetSpinnerData(items);
                }
                break;
            }
        }
    }
}