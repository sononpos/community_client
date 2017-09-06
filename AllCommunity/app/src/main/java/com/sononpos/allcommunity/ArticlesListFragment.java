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

package com.sononpos.allcommunity;

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
import android.widget.Toast;

import com.sononpos.allcommunity.Funtional.ArticleParsingHelper;
import com.sononpos.allcommunity.HttpHelper.HttpHelper;
import com.sononpos.allcommunity.HttpHelper.HttpHelperListener;
import com.sononpos.allcommunity.databinding.FragmentCommlistBinding;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ArticlesListFragment extends Fragment implements HttpHelperListener, SwipeRefreshLayout.OnRefreshListener {
    FragmentCommlistBinding mBind;
    HttpHelper httpHelper;
    NotifyHandler notifyHandler;
    ArticleLoadInfo loadInfo = new ArticleLoadInfo();
    int m_nPosition;

    public ArticlesListFragment(int _nPosition) {
        m_nPosition = _nPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_commlist, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notifyHandler = new NotifyHandler(new WeakReference<>(this));
        mBind.swipeRefreshLayout.setOnRefreshListener(this);

        mBind.articlesList.setHasFixedSize(true);
        mBind.articlesList.setLayoutManager(new LinearLayoutManager(mBind.getRoot().getContext()));
        mBind.articlesList.setAdapter(new ArticlesListRecyclerAdapter());
        mBind.articlesList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        mBind.articlesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(loadInfo.isLoading()) return;

                int lastVisibleItemPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount) {
                    LoadContents();
                }
            }
        });

        httpHelper = new HttpHelper();
        httpHelper.SetListener(this);
        loadInfo.Reset();
        LoadContents();
    }

    @Override
    public void onResponse(int nType, int nErrorCode, String sResponse) {
        if(nErrorCode != 0) {
            Message msg = notifyHandler.obtainMessage(-1);
            notifyHandler.sendMessage(msg);
            return;
        }
        ArrayList<ArticleItem> list = new ArrayList<>();
        if(ArticleParsingHelper.parse(sResponse, loadInfo, list)) {
            //  파싱 성공
            loadInfo.bLoading = false;
            loadInfo.bFirstLoad = false;
            AddListAndNotify(list);
        }
    }

    @Override
    public void onRefresh() {
        if(loadInfo.isLoading()) {
            mBind.swipeRefreshLayout.setRefreshing(false);
            return;
        }
        loadInfo.Reset();
        ArticlesListRecyclerAdapter adapter = (ArticlesListRecyclerAdapter)mBind.articlesList.getAdapter();
        adapter.ClearList();
        LoadContents();
    }

    protected void AddListAndNotify(ArrayList<ArticleItem> list) {
        ArticlesListRecyclerAdapter adapter = (ArticlesListRecyclerAdapter)mBind.articlesList.getAdapter();
        adapter.AddList(list);
        Message msg = notifyHandler.obtainMessage(0);
        notifyHandler.sendMessage(msg);
    }

    protected void LoadContents() {
        if(loadInfo.isLoading()) return;
        CommunityTypeInfo info = G.GetCommunityList().get(m_nPosition);
        if(info != null) {
            httpHelper.Request(0, G.SERV_ROOT + info.sKey + loadInfo.getPage());
        }
    }

    class NotifyHandler extends Handler {
        WeakReference<ArticlesListFragment> root;

        public NotifyHandler(WeakReference<ArticlesListFragment> _root) {
            root = _root;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what != 0) {
                Toast.makeText(getContext(), R.string.alert_please_check_network_state, Toast.LENGTH_SHORT);
            }
            mBind.swipeRefreshLayout.setRefreshing(false);
            mBind.articlesList.getAdapter().notifyDataSetChanged();
        }
    }

    public class ArticleLoadInfo {
        public boolean bFirstLoad = true;
        public boolean bLoading = false;
        public String sNext = "";

        public boolean isLoading() {
            return bLoading;
        }

        public String getPage() {
            if(bFirstLoad) {
                return "/1";
            }
            else {
                return "/" + sNext;
            }
        }

        public void Reset() {
            bFirstLoad = true;
            bLoading = false;
            sNext = "";
        }
    }
}