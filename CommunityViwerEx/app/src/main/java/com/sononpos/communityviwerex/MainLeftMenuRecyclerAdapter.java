package com.sononpos.communityviwerex;

import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.sononpos.communityviwerex.databinding.ActivityMainBinding;
import com.sononpos.communityviwerex.databinding.LeftMenuItemBinding;

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
        final TabItemManager timan = Global.obj().getTabItemManager();
        final TabItem info = timan.getListAll().get(position);
        holder.mBind.setItem(info);
        SetItemColor(holder);
        holder.mBind.cbEnabled.setChecked(!timan.isFiltered(info.getKey()));
        holder.mBind.cbEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(timan.isFiltered(info.getKey()) && isChecked) {
                    timan.removeFilter(info.getKey());
                }
                else if(!timan.isFiltered(info.getKey()) && !isChecked) {
                    timan.addFilter(info.getKey());
                }
                timan.refreshList();
                mBind.tabs.notifyDataSetChanged();
                mBind.pager.getAdapter().notifyDataSetChanged();
                SetItemColor(holder);
            }
        });
//
//        holder.mBind.tvName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int nFilteredPos = listman.getFilteredIndex(position);
//                if(nFilteredPos == -1) return;
//                mBind.pager.setCurrentItem(nFilteredPos);
//                mBind.drawer.closeDrawer(mBind.navView);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        final TabItemManager timan = Global.obj().getTabItemManager();
        return timan.getListAll().size();
    }

    protected void SetItemColor(final LeftMenuItemViewHolder holder) {
        final TabItemManager timan = Global.obj().getTabItemManager();

        if(timan.isFiltered(holder.mBind.getItem().getKey())) {
            holder.mBind.tvName.setTextColor(ContextCompat.getColor(holder.mBind.getRoot().getContext(), R.color.common_google_signin_btn_text_light_disabled));
        }
        else {
            holder.mBind.tvName.setTextColor(ContextCompat.getColor(holder.mBind.getRoot().getContext(), R.color.colorPrimaryDark));
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
