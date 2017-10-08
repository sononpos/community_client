package com.sononpos.allcommunity.RecyclerAdapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sononpos.allcommunity.ArticleItem;
import com.sononpos.allcommunity.CommunityArticleActivity;
import com.sononpos.allcommunity.R;
import com.sononpos.allcommunity.YoutubeListActivity;
import com.sononpos.allcommunity.databinding.ArticleNewsItemBinding;

import java.util.ArrayList;

/**
 * Created by nnnyyy on 2017-09-05.
 */

public class NewsListRecyclerAdapter extends RecyclerView.Adapter<NewsListRecyclerAdapter.ArticleItemViewHolder> {

    ArrayList<ArticleItem> aItemList = new ArrayList<>();
    int mLastPosition = -1;

    public NewsListRecyclerAdapter() {
        setHasStableIds(true);
    }

    @Override
    public NewsListRecyclerAdapter.ArticleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ArticleNewsItemBinding bind = ArticleNewsItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ArticleItemViewHolder(bind.getRoot());
    }



    @Override
    public void onBindViewHolder(NewsListRecyclerAdapter.ArticleItemViewHolder holder, int position) {
        final ArticleItem item =  aItemList.get(position);
        holder.mBind.setItem(item);

        holder.mBind.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, CommunityArticleActivity.class);
                intent.putExtra("URL", item.m_sLink);
                intent.putExtra("TITLE", item.m_sTitle);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(context, R.anim.anim1,R.anim.anim2).toBundle();
                    context.startActivity(intent, bndlanimation);
                }
                else {
                    context.startActivity(intent);
                }
            }
        });

        holder.mBind.ivYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, YoutubeListActivity.class);
                intent.putExtra("Word", item.m_sTitle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        int hashcode = aItemList.get(position).m_sTitle.toString().hashCode();
        return hashcode;
    }

    @Override
    public int getItemCount() {
        return aItemList.size();
    }

    public void AddList(ArrayList<ArticleItem> _list) {
        aItemList.addAll(_list);
    }

    public void ClearList() {
        mLastPosition = -1;
        aItemList.clear();
    }

    public class ArticleItemViewHolder extends RecyclerView.ViewHolder {
        final ArticleNewsItemBinding mBind;

        public ArticleItemViewHolder(View itemView) {
            super(itemView);
            mBind = DataBindingUtil.bind(itemView);
        }
    }
}
