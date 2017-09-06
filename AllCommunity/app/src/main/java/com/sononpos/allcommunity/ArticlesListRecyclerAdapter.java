package com.sononpos.allcommunity;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sononpos.allcommunity.databinding.ArticleItemBinding;

import java.util.ArrayList;

/**
 * Created by nnnyyy on 2017-09-05.
 */

public class ArticlesListRecyclerAdapter extends RecyclerView.Adapter<ArticlesListRecyclerAdapter.ArticleItemViewHolder> {

    ArrayList<ArticleItem> aItemList = new ArrayList<>();

    @Override
    public ArticlesListRecyclerAdapter.ArticleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ArticleItemBinding bind = ArticleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ArticleItemViewHolder(bind.getRoot());
    }

    @Override
    public void onBindViewHolder(ArticlesListRecyclerAdapter.ArticleItemViewHolder holder, int position) {
        final ArticleItemViewHolder holderInner = holder;
        holder.mBind.setItem(aItemList.get(position));
        holder.mBind.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ClickedItem", holderInner.mBind.getItem().m_sJsonString);
            }
        });
    }

    @Override
    public int getItemCount() {
        return aItemList.size();
    }

    public void AddList(ArrayList<ArticleItem> _list) {
        aItemList.addAll(_list);
    }

    public void ClearList() {
        aItemList.clear();
    }

    public class ArticleItemViewHolder extends RecyclerView.ViewHolder {
        final ArticleItemBinding mBind;

        public ArticleItemViewHolder(View itemView) {
            super(itemView);
            mBind = DataBindingUtil.bind(itemView);
        }
    }
}
