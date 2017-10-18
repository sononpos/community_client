package com.sononpos.communityviwerex;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sononpos.communityviwerex.databinding.ArticleListItemBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nnnyyy on 2017-10-18.
 */

public class RecentListRecyclerAdapter extends RecyclerView.Adapter<RecentListRecyclerAdapter.ArticleListViewHolder> {
    ArrayList<ListViewItem> aRecents = new ArrayList<>();
    @Override
    public ArticleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ArticleListItemBinding bind = ArticleListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ArticleListViewHolder(bind.getRoot());
    }

    @Override
    public void onBindViewHolder(ArticleListViewHolder holder, int position) {
        holder.mBind.setItem(aRecents.get(position));
    }

    @Override
    public int getItemCount() {
        return aRecents.size();
    }

    public void addItem(ListViewItem item) {
        aRecents.add(item);
    }

    public void addItemList(ArrayList<ListViewItem> items) {
        aRecents.addAll(items);
    }

    public void clear() {
        aRecents.clear();
    }

    public class ArticleListViewHolder extends RecyclerView.ViewHolder {
        ArticleListItemBinding mBind;
        public ArticleListViewHolder(View itemView) {
            super(itemView);
            mBind = DataBindingUtil.bind(itemView);
        }
    }
}
