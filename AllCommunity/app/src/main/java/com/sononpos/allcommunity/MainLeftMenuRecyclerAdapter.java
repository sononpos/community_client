package com.sononpos.allcommunity;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sononpos.allcommunity.databinding.LeftMenuItemBinding;

/**
 * Created by nnnyyy on 2017-09-08.
 */

public class MainLeftMenuRecyclerAdapter extends RecyclerView.Adapter<MainLeftMenuRecyclerAdapter.LeftMenuItemViewHolder> {

    @Override
    public LeftMenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LeftMenuItemBinding bind = LeftMenuItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LeftMenuItemViewHolder(bind.getRoot());
    }

    @Override
    public void onBindViewHolder(LeftMenuItemViewHolder holder, int position) {
        ArticleItem item = new ArticleItem();
        item.m_sTitle = "Test";
        holder.mBind.setItem(item);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class LeftMenuItemViewHolder extends RecyclerView.ViewHolder {
        final LeftMenuItemBinding mBind;

        public LeftMenuItemViewHolder(View itemView) {
            super(itemView);
            mBind = DataBindingUtil.bind(itemView);
        }
    }
}
