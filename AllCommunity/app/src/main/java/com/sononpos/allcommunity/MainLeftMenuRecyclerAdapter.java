package com.sononpos.allcommunity;

import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        final CommunityTypeInfo info = G.GetCommunityList(true).get(position);
        holder.mBind.setItem(info);
        SetItemColor(holder);
        holder.mBind.btnDirectGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nFilteredPos = G.GetFilteredIndex(position);
                if(nFilteredPos == -1) return;
                mBind.pager.setCurrentItem(nFilteredPos);
                mBind.drawer.closeDrawer(mBind.navView);
            }
        });

        holder.mBind.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.ToggleFilter(info.sKey);
                G.RefreshFilteredInfo();
                mBind.tabs.notifyDataSetChanged();
                mBind.pager.getAdapter().notifyDataSetChanged();
                SetItemColor(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return G.GetCommunityList(true).size();
    }

    protected void SetItemColor(final LeftMenuItemViewHolder holder) {
        if(G.IsFilteredKey(holder.mBind.getItem().sKey)) {
            holder.mBind.tvName.setTextColor(ContextCompat.getColor(holder.mBind.getRoot().getContext(), R.color.disabledTextColor));
            holder.mBind.btnDirectGo.setEnabled(false);
        }
        else {
            holder.mBind.tvName.setTextColor(ContextCompat.getColor(holder.mBind.getRoot().getContext(), R.color.mainTextColor));
            holder.mBind.btnDirectGo.setEnabled(true);
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
