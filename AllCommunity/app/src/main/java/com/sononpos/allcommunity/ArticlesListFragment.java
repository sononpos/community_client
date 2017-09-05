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
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sononpos.allcommunity.HttpHelper.HttpHelper;
import com.sononpos.allcommunity.HttpHelper.HttpHelperListener;
import com.sononpos.allcommunity.databinding.FragmentCommlistBinding;

public class ArticlesListFragment extends Fragment implements HttpHelperListener {
    FragmentCommlistBinding mBind;
    HttpHelper httpHelper;
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
        mBind.articlesList.setHasFixedSize(true);
        mBind.articlesList.setLayoutManager(new LinearLayoutManager(mBind.getRoot().getContext()));
        mBind.articlesList.setAdapter(new ArticlesListRecyclerAdapter());

        httpHelper = new HttpHelper();
        httpHelper.SetListener(this);
        httpHelper.Request(0, "http://52.79.205.198:3000/dogdrip/1");
    }

    @Override
    public void onResponse(int nType, int nErrorCode, String sResponse) {
        Log.e("onResponse", "type :" + nType + ", nErrorCode : " + nErrorCode + ", res : " + sResponse);
    }
}