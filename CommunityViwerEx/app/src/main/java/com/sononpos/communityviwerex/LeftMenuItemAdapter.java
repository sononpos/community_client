package com.sononpos.communityviwerex;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sononpos.communityviwerex.Funtional.ThemeManager;

import java.util.List;

/**
 * Created by nnnyy on 2017-03-05.
 */
public class LeftMenuItemAdapter extends ArrayAdapter<LeftMenuItem> {
    Context mContext;
    int layoutResourceId;
    List<LeftMenuItem> menudata;

    public LeftMenuItemAdapter(Context mContext, int layoutResourceId, List<LeftMenuItem> _data){
        super(mContext, layoutResourceId, _data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.menudata = _data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vListItem = convertView;

        ThemeManager.ThemeColorObject theme = ThemeManager.GetTheme();

        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        vListItem = inflater.inflate(layoutResourceId, parent, false);

        vListItem.setBackgroundColor(Color.parseColor(theme.BgList));

        TextView tvName = (TextView)vListItem.findViewById(R.id.textViewName);
        tvName.setText(menudata.get(position).name);

        TabItemManager timan = Global.obj().getTabItemManager();
        TabItem item = timan.getListAll().get(position);

        if( timan.isFiltered(item.getKey()) ) {
            tvName.setTextColor(Color.parseColor(theme.LeftDisable));
        }
        else {
            tvName.setTextColor(Color.parseColor(theme.LeftEnable));
        }

        return vListItem;
    }
}
