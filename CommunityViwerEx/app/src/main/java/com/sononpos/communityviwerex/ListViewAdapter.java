package com.sononpos.communityviwerex;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nnnyyy on 2017-03-03.
 */

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> listViewItemList = new ArrayList<>();

    public ListViewAdapter() {

    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listviewitem, parent, false);
        }

        convertView.setBackgroundColor(Color.parseColor("#fee0e8"));

        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1) ;
        titleTextView.setTextColor(Color.parseColor("#fe6ea4"));
        TextView descTextView = (TextView) convertView.findViewById(R.id.textView2) ;

        ListViewItem item = listViewItemList.get(position);

        titleTextView.setText(item.m_sTitle);
        descTextView.setText(item.m_sDesc);

        return convertView;
    }

    public void AddItem(String _sTitle, String _sDesc){
        ListViewItem item = new ListViewItem();
        item.m_sTitle = _sTitle;
        item.m_sDesc = _sDesc;

        listViewItemList.add(item);
    }

    public void RemoveAll() {
        listViewItemList.clear();
    }
}
