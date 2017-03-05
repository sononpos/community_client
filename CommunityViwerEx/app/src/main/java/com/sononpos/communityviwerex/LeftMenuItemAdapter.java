package com.sononpos.communityviwerex;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by nnnyy on 2017-03-05.
 */
public class LeftMenuItemAdapter extends ArrayAdapter<LeftMenuItem> {
    Context mContext;
    int layoutResourceId;
    LeftMenuItem menudata[];

    public LeftMenuItemAdapter(Context mContext, int layoutResourceId, LeftMenuItem[] _data){
        super(mContext, layoutResourceId, _data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.menudata = _data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vListItem = convertView;

        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
        vListItem = inflater.inflate(layoutResourceId, parent, false);

        TextView tvName = (TextView)vListItem.findViewById(R.id.textViewName);
        tvName.setText(menudata[position].name);

        return vListItem;
    }
}
