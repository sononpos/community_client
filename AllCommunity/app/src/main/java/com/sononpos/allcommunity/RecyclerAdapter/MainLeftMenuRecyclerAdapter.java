package com.sononpos.allcommunity.RecyclerAdapter;

import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.sononpos.allcommunity.ArticleType.ArticleTypeInfo;
import com.sononpos.allcommunity.Global;
import com.sononpos.allcommunity.R;
import com.sononpos.allcommunity.databinding.ActivityMainBinding;
import com.sononpos.allcommunity.databinding.LeftMenuItemBinding;

/**
 * Created by nnnyyy on 2017-09-08.
 */

public class MainLeftMenuRecyclerAdapter extends RecyclerView.Adapter<MainLeftMenuRecyclerAdapter.LeftMenuItemViewHolder> {
    ActivityMainBinding mBind;

    public MainLeftMenuRecyclerAdapter(ActivityMainBinding _mBind) {
        mBind = _mBind;
    }

    @Override
    public LeftMenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LeftMenuItemBinding bind = LeftMenuItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LeftMenuItemViewHolder(bind.getRoot());
    }

    @Override
    public void onBindViewHolder(final LeftMenuItemViewHolder holder, final int position) {
        final Global.ArticleListManager listman = Global.getInstance().getListMan();
        if(listman == null) return;

        final ArticleTypeInfo info = listman.getCommunityList(true).get(position);
        holder.mBind.setItem(info);
        SetItemColor(holder);
        holder.mBind.cbEnabled.setOnCheckedChangeListener(null);
        holder.mBind.cbEnabled.setChecked(!listman.isFilteredKey(info.mHashKey));
        holder.mBind.cbEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(listman.isFilteredKey(info.mHashKey) && isChecked) {
                    listman.toggleFilter(info.mHashKey);
                }
                else if(!listman.isFilteredKey(info.mHashKey) && !isChecked) {
                    listman.toggleFilter(info.mHashKey);
                }
                SetItemColor(holder);
            }
        });

        holder.mBind.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nFilteredPos = listman.getFilteredIndex(position);
                if(nFilteredPos == -1) return;
                mBind.pager.setCurrentItem(nFilteredPos);
                mBind.drawer.closeDrawer(mBind.navView);
            }
        });
    }

    @Override
    public int getItemCount() {
        final Global.ArticleListManager listman = Global.getInstance().getListMan();
        if(listman == null) return 0;
        return listman.getCommunityList(true).size();
    }

    protected void SetItemColor(final LeftMenuItemViewHolder holder) {
        final Global.ArticleListManager listman = Global.getInstance().getListMan();
        if(listman == null) return;

        if(listman.isFilteredKey(holder.mBind.getItem().mHashKey)) {
            holder.mBind.tvName.setTextColor(ContextCompat.getColor(holder.mBind.getRoot().getContext(), R.color.disabledTextColor));
        }
        else {
            holder.mBind.tvName.setTextColor(ContextCompat.getColor(holder.mBind.getRoot().getContext(), R.color.mainTextColor));
        }
    }

    public class LeftMenuItemViewHolder extends RecyclerView.ViewHolder {
        final LeftMenuItemBinding mBind;

        public LeftMenuItemViewHolder(View itemView) {
            super(itemView);
            mBind = DataBindingUtil.bind(itemView);
        }
    }
}
