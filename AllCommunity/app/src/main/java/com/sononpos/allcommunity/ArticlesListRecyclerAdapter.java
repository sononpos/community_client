package com.sononpos.allcommunity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

    public ArticlesListRecyclerAdapter() {
        setHasStableIds(true);
    }

    @Override
    public ArticlesListRecyclerAdapter.ArticleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ArticleItemBinding bind = ArticleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ArticleItemViewHolder(bind.getRoot());
    }

    @Override
    public void onBindViewHolder(ArticlesListRecyclerAdapter.ArticleItemViewHolder holder, int position) {
        final ArticleItemViewHolder holderInner = holder;
        ArticleItem item =  aItemList.get(position);
        if(TextUtils.isEmpty(item.m_sViewCnt)) {
            item.m_sViewCnt = "-";
        }
        if(TextUtils.isEmpty(item.m_sCommentCnt)) {
            item.m_sCommentCnt = "-";
        }

        if(TextUtils.isEmpty(item.m_sName)) {
            item.m_sName = "noname";
        }

        if(TextUtils.isEmpty(item.m_sRegDate)) {
            item.m_sRegDate = "nodate";
        }

        holder.mBind.setItem(aItemList.get(position));

        holder.mBind.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ClickedItem", holderInner.mBind.getItem().m_sJsonString);
                Intent intent = new Intent(holderInner.mBind.getRoot().getContext(), CommunityArticle.class);
                intent.putExtra("URL", holderInner.mBind.getItem().m_sLink);
                intent.putExtra("TITLE", holderInner.mBind.getItem().m_sTitle);
                holderInner.mBind.getRoot().getContext().startActivity(intent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return aItemList.get(position).m_sTitle.toString().hashCode();
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
