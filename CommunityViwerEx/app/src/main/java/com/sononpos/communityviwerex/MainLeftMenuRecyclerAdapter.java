package com.sononpos.communityviwerex;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.sononpos.communityviwerex.Funtional.ThemeManager;
import com.sononpos.communityviwerex.databinding.ActivityMainBinding;
import com.sononpos.communityviwerex.databinding.LeftMenuItemBinding;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nnnyyy on 2017-09-08.
 */

public class MainLeftMenuRecyclerAdapter extends RecyclerView.Adapter<MainLeftMenuRecyclerAdapter.LeftMenuItemViewHolder>
implements ItemTouchHelperAdapter {
    ActivityMainBinding mBind;
    OnStartDragListener mDragListener;

    public MainLeftMenuRecyclerAdapter(ActivityMainBinding _mBind, OnStartDragListener listener) {
        mDragListener = listener;
        mBind = _mBind;
    }

    @Override
    public LeftMenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LeftMenuItemBinding bind = LeftMenuItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LeftMenuItemViewHolder(bind.getRoot());
    }

    @Override
    public void onBindViewHolder(final LeftMenuItemViewHolder holder, int position) {
        final TabItemManager timan = Global.obj().getTabItemManager();
        final TabItem info = timan.getListAll().get(position);
        holder.mBind.setItem(info);
        SetItemColor(holder);
        holder.mBind.cbEnabled.setOnCheckedChangeListener(null);
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
                //timan.refreshList(holder.mBind.getRoot().getContext(), false);
                SetItemColor(holder);
            }
        });

        holder.mBind.ivDragHandler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragListener.onStartDrag(holder);
                }
                return false;
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
        if(timan.getListAll() == null) return 0;
        return timan.getListAll().size();
    }

    protected void SetItemColor(final LeftMenuItemViewHolder holder) {
        ThemeManager.ThemeColorObject theme = ThemeManager.GetTheme();
        final TabItemManager timan = Global.obj().getTabItemManager();

        if(timan.isFiltered(holder.mBind.getItem().getKey())) {
            holder.mBind.tvName.setTextColor(Color.parseColor(theme.LeftDisable));
        }
        else {
            holder.mBind.tvName.setTextColor(Color.parseColor(theme.LeftEnable));
        }
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        final TabItemManager timan = Global.obj().getTabItemManager();
        ArrayList<TabItem> aList = timan.getListAll();
        if(aList == null) return;
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(aList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(aList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {

    }

    public class LeftMenuItemViewHolder extends RecyclerView.ViewHolder {
        final LeftMenuItemBinding mBind;

        public LeftMenuItemViewHolder(View itemView) {
            super(itemView);
            mBind = DataBindingUtil.bind(itemView);
        }
    }
}
