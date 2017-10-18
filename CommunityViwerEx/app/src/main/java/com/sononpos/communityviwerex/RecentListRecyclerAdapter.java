package com.sononpos.communityviwerex;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sononpos.communityviwerex.Funtional.ThemeManager;
import com.sononpos.communityviwerex.databinding.ArticleListItemBinding;

import java.util.ArrayList;

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
    public void onBindViewHolder(final ArticleListViewHolder holder, int position) {
        ThemeManager.ThemeColorObject theme = ThemeManager.GetTheme();
        final ListViewItem item = aRecents.get(position);
        holder.mBind.setItem(item);
        holder.mBind.artTitle.setTextColor(Color.parseColor(theme.BasicFont));
        holder.mBind.artName.setTextColor(Color.parseColor(theme.SubFont));
        holder.mBind.artRegdate.setTextColor(Color.parseColor(theme.SubFont));
        holder.mBind.artTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.mBind.getRoot().getContext(), CommunityArticle.class);
                intent.putExtra("URL", item.m_sLink);
                intent.putExtra("TITLE", item.m_sTitle);
                holder.mBind.getRoot().getContext().startActivity(intent);
            }
        });
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
