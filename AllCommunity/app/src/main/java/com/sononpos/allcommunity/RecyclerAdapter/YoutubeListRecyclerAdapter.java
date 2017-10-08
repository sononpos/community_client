package com.sononpos.allcommunity.RecyclerAdapter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sononpos.allcommunity.databinding.YoutubeArticleItemBinding;
import com.sononpos.allcommunity.youtube.VideoPlayerActivity;
import com.sononpos.allcommunity.youtube.YoutubeArticleItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by nnnyyy on 2017-09-05.
 */

public class YoutubeListRecyclerAdapter extends RecyclerView.Adapter<YoutubeListRecyclerAdapter.ArticleItemViewHolder> {

    ArrayList<YoutubeArticleItem> aItemList = new ArrayList<>();
    int mLastPosition = -1;

    public YoutubeListRecyclerAdapter() {
        setHasStableIds(true);
    }

    @Override
    public YoutubeListRecyclerAdapter.ArticleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        YoutubeArticleItemBinding bind = YoutubeArticleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ArticleItemViewHolder(bind.getRoot());
    }



    @Override
    public void onBindViewHolder(YoutubeListRecyclerAdapter.ArticleItemViewHolder holder, int position) {
        final YoutubeArticleItem item =  aItemList.get(position);
        holder.mBind.setItem(item);
        holder.mBind.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), VideoPlayerActivity.class);
                intent.putExtra("id", item.id);
                intent.putExtra("title", item.title);
                v.getContext().startActivity(intent);
            }
        });
        Picasso.with(holder.mBind.getRoot().getContext()).load(item.thumbnail).into(holder.mBind.ivThumbnail);
    }

    @Override
    public long getItemId(int position) {
        if(aItemList.size() <= position ) return 0;
        int hashcode = aItemList.get(position).title.toString().hashCode();
        return hashcode;
    }

    @Override
    public int getItemCount() {
        return aItemList.size();
    }
    public void AddList(ArrayList<YoutubeArticleItem> _list) {
        aItemList.addAll(_list);
    }
    public void ClearList() {
        mLastPosition = -1;
        aItemList.clear();
    }

    public class ArticleItemViewHolder extends RecyclerView.ViewHolder {
        final YoutubeArticleItemBinding mBind;

        public ArticleItemViewHolder(View itemView) {
            super(itemView);
            mBind = DataBindingUtil.bind(itemView);
        }
    }
}
