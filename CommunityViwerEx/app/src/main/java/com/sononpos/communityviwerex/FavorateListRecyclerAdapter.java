package com.sononpos.communityviwerex;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sononpos.communityviwerex.Funtional.ThemeManager;
import com.sononpos.communityviwerex.databinding.ArticleListItemBinding;
import com.sononpos.communityviwerex.databinding.ArticleListItemWithDelBinding;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by nnnyyy on 2017-10-18.
 */

public class FavorateListRecyclerAdapter extends RecyclerView.Adapter<FavorateListRecyclerAdapter.ArticleListViewHolder> {
    ArrayList<ListViewItem> aFavorates = new ArrayList<>();
    TextView tutorialView;
    public FavorateListRecyclerAdapter(TextView tvTuto) {
        tutorialView = tvTuto;
    }

    @Override
    public ArticleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ArticleListItemWithDelBinding bind = ArticleListItemWithDelBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ArticleListViewHolder(bind.getRoot());
    }

    @Override
    public void onBindViewHolder(final ArticleListViewHolder holder, final int position) {
        ThemeManager.ThemeColorObject theme = ThemeManager.GetTheme();
        final ListViewItem item = aFavorates.get(position);
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

        holder.mBind.tvDel.setTextColor(Color.parseColor(theme.BasicFont));
        holder.mBind.tvDel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(holder.getAdapterPosition() == -1) return false;
                Global.obj().getArticleListManager().removeFavorate(holder.mBind.getRoot().getContext(), holder.getAdapterPosition());
                aFavorates.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), aFavorates.size());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if(aFavorates.size() == 0) {
            tutorialView.setVisibility(View.VISIBLE);
        }
        else {
            tutorialView.setVisibility(View.GONE);
        }
        return aFavorates.size();
    }

    public void addItem(ListViewItem item) {
        aFavorates.add(item);
    }

    public void addItemList(Vector<ListViewItem> items) {
        aFavorates.addAll(items);
    }

    public void clear() {
        aFavorates.clear();
    }

    public class ArticleListViewHolder extends RecyclerView.ViewHolder {
        ArticleListItemWithDelBinding mBind;
        public ArticleListViewHolder(View itemView) {
            super(itemView);
            mBind = DataBindingUtil.bind(itemView);
        }
    }
}
