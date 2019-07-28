package com.sononpos.communityviwerex;


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sononpos.communityviwerex.Funtional.ThemeManager;
import com.sononpos.communityviwerex.databinding.FragmentFavorateBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavorateFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{
    FragmentFavorateBinding mBind;

    public FavorateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_favorate, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBind.srlayout.setOnRefreshListener(this);
        mBind.rvList.setHasFixedSize(true);
        mBind.rvList.setLayoutManager(new LinearLayoutManager(mBind.getRoot().getContext()));
        mBind.rvList.setAdapter(new FavorateListRecyclerAdapter(mBind.tvTutorial));
        mBind.rvList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        ThemeManager.ThemeColorObject theme = ThemeManager.GetTheme();
        mBind.tvTutorial.setTextColor(Color.parseColor(theme.BasicFont));
        load();
    }

    private void load() {
        FavorateListRecyclerAdapter adapter = (FavorateListRecyclerAdapter)mBind.rvList.getAdapter();
        adapter.addItemList(Global.obj().getArticleListManager().getFavorateList());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        mBind.srlayout.setRefreshing(false);
        FavorateListRecyclerAdapter adapter = (FavorateListRecyclerAdapter)mBind.rvList.getAdapter();
        adapter.clear();
        load();
    }
}
