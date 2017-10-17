package com.sononpos.allcommunity.RecyclerAdapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sononpos.allcommunity.databinding.YoutubeCommentItemBinding;
import com.sononpos.allcommunity.youtube.CommentInfo;

import java.util.ArrayList;

/**
 * Created by nnnyyy on 2017-09-05.
 */

public class YoutubeCommentsRecyclerAdapter extends RecyclerView.Adapter<YoutubeCommentsRecyclerAdapter.ArticleItemViewHolder> {

    ArrayList<CommentInfo> aItemList = new ArrayList<>();
    int mLastPosition = -1;

    public YoutubeCommentsRecyclerAdapter() {
        setHasStableIds(true);
    }

    @Override
    public YoutubeCommentsRecyclerAdapter.ArticleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        YoutubeCommentItemBinding bind = YoutubeCommentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ArticleItemViewHolder(bind.getRoot());
    }



    @Override
    public void onBindViewHolder(YoutubeCommentsRecyclerAdapter.ArticleItemViewHolder holder, int position) {
        final CommentInfo item =  aItemList.get(position);
        holder.mBind.setItem(item);
    }

    @Override
    public long getItemId(int position) {
        if(aItemList.size() <= position ) return 0;
        int hashcode = aItemList.get(position).sComment.toString().hashCode();
        return hashcode;
    }

    @Override
    public int getItemCount() {
        return aItemList.size();
    }
    public void AddList(ArrayList<CommentInfo> _list) {
        aItemList.addAll(_list);
    }
    public void ClearList() {
        mLastPosition = -1;
        aItemList.clear();
    }

    public class ArticleItemViewHolder extends RecyclerView.ViewHolder {
        final YoutubeCommentItemBinding mBind;

        public ArticleItemViewHolder(View itemView) {
            super(itemView);
            mBind = DataBindingUtil.bind(itemView);
        }
    }
}
