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

        TextView titleTextView = (TextView) convertView.findViewById(R.id.tvTitle) ;
        titleTextView.setTextColor(Color.parseColor("#fe6ea4"));
        TextView nameTextView = (TextView) convertView.findViewById(R.id.tvName) ;
        TextView regDateTextView = (TextView) convertView.findViewById(R.id.tvRegDate) ;
        TextView countTextView = (TextView) convertView.findViewById(R.id.tvCount) ;

        ListViewItem item = listViewItemList.get(position);
        String sTitleRet = item.m_sTitle;
        if( item.m_sTitle.length() > 30 ) {
            sTitleRet = item.m_sTitle.substring(0, 30) + "...";
        }

        if(item.m_sCommentCnt.isEmpty()) {
            item.m_sCommentCnt = "0";
        }
        sTitleRet += "[" + item.m_sCommentCnt + "]";
        titleTextView.setText(sTitleRet);
        nameTextView.setText(item.m_sName);
        regDateTextView.setText(item.m_sRegDate);
        countTextView.setText("조회수 : " + item.m_sViewCnt);

        return convertView;
    }

    public void AddItem(String _sTitle, String _sName, String _sRegDate, String _sViewCnt, String _sCommentCnt, String _sLink){
        ListViewItem item = new ListViewItem();
        item.m_sTitle = _sTitle;
        item.m_sName = _sName;
        item.m_sLink = _sLink;
        item.m_sRegDate = _sRegDate;
        item.m_sViewCnt = _sViewCnt;
        item.m_sCommentCnt =_sCommentCnt;
        listViewItemList.add(item);
    }

    public void RemoveAll() {
        listViewItemList.clear();
    }
}
