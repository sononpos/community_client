package com.sononpos.communityviwerex;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sononpos.communityviwerex.databinding.FragmentRecentBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment extends Fragment implements  SwipeRefreshLayout.OnRefreshListener{
    FragmentRecentBinding mBind;

    public RecentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBind = DataBindingUtil.inflate(inflater, R.layout.fragment_recent, container, false);
        return mBind.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBind.srlayout.setOnRefreshListener(this);
        mBind.rvList.setHasFixedSize(true);
        mBind.rvList.setLayoutManager(new LinearLayoutManager(mBind.getRoot().getContext()));
        mBind.rvList.setAdapter(new RecentListRecyclerAdapter());
//        mBind.rvList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
//        mBind.rvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if(loadInfo.isLoading()) return;
//
//                int lastVisibleItemPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
//                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
//                if (lastVisibleItemPosition == itemTotalCount) {
//                    LoadContents();
//                }
//            }
//        });
        loadRecent();
    }

    private void loadRecent() {
        RecentListRecyclerAdapter adapter = (RecentListRecyclerAdapter)mBind.rvList.getAdapter();
        adapter.addItemList(Global.obj().getArticleListManager().getList());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        Log.e("EEEEE", "EEEEE");
        mBind.srlayout.setRefreshing(false);
        RecentListRecyclerAdapter adapter = (RecentListRecyclerAdapter)mBind.rvList.getAdapter();
        adapter.clear();
        loadRecent();
    }
}
