package com.sononpos.allcommunity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sononpos.allcommunity.Funtional.ThemeManager;

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

        ThemeManager.ThemeColorObject theme = ThemeManager.GetTheme();
        ThemeManager.ThemeFontObject themeFont = ThemeManager.GetFont();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listviewitem, parent, false);
        }

        convertView.setBackgroundColor(Color.parseColor(theme.BgList));

        TextView titleTextView = (TextView) convertView.findViewById(R.id.tvTitle) ;
        titleTextView.setTextColor(Color.parseColor(theme.BasicFont));
        TextView nameTextView = (TextView) convertView.findViewById(R.id.tvName) ;
        TextView regDateTextView = (TextView) convertView.findViewById(R.id.tvRegDate) ;
        TextView countTextView = (TextView) convertView.findViewById(R.id.tvCount) ;
        nameTextView.setTextColor(Color.parseColor(theme.SubFont));
        regDateTextView.setTextColor(Color.parseColor(theme.SubFont));
        countTextView.setTextColor(Color.parseColor(theme.SubFont));

        /*
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {0xFF616261,0xFF131313});
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            convertView.setBackground(gd);
        }
        */

        ListViewItem item = listViewItemList.get(position);
        String sTitleRet = item.m_sTitle;

        if(G.isReaded(item.m_sTitle.hashCode())){
            titleTextView.setTextColor(Color.parseColor(theme.SubFont));
        }

        if(item.m_sCommentCnt.isEmpty()) {
            item.m_sCommentCnt = "0";
        }

        String sComment = " <font color='#f95959'>[" + item.m_sCommentCnt + "]</font>";
        String sCommentForCalc = " [" + item.m_sCommentCnt + "]";

        if(item.m_sName.isEmpty()) {
            item.m_sName = "noname";
        }

        Rect bounds = new Rect();
        Rect boundsComment = new Rect();
        titleTextView.setTextSize(themeFont.TitleFont);
        Paint textPaint = titleTextView.getPaint();

        int rootViewWidth = parent.getRootView().getWidth();

        textPaint.getTextBounds(item.m_sTitle, 0, item.m_sTitle.length(), bounds);
        textPaint.getTextBounds(sCommentForCalc, 0, sCommentForCalc.length(), boundsComment);

        SharedPreferences setRefer = PreferenceManager
                .getDefaultSharedPreferences(parent.getContext());
        boolean bShort = setRefer.getBoolean("list_short", true);

        if( bShort && bounds.width() > (rootViewWidth - 200 - boundsComment.width()) ) {
            float f = 1.0f - ((float)bounds.width() - (rootViewWidth - 200 - boundsComment.width())) / (float)bounds.width();
            int nAdjustLen = (int)((float)item.m_sTitle.length() * f);
            sTitleRet = item.m_sTitle.substring(0, nAdjustLen) + "...";
        }

        sTitleRet += sComment;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            titleTextView.setText(Html.fromHtml(sTitleRet, Html.FROM_HTML_MODE_LEGACY));
        }
        else{
            titleTextView.setText(Html.fromHtml(sTitleRet));
        }
        nameTextView.setText(item.m_sName); nameTextView.setTextSize(themeFont.SubFont);
        regDateTextView.setText(item.m_sRegDate); regDateTextView.setTextSize(themeFont.SubFont);
        countTextView.setText("조회수 : " + item.m_sViewCnt); countTextView.setTextSize(themeFont.SubFont);

        return convertView;
    }

    public void AddItem(String _sTitle, String _sName, String _sRegDate, String _sViewCnt, String _sCommentCnt, String _sLink, String _sJsonString){
        ListViewItem item = new ListViewItem();
        item.m_sTitle = _sTitle;
        item.m_sName = _sName;
        item.m_sLink = _sLink;
        item.m_sRegDate = _sRegDate;
        item.m_sViewCnt = _sViewCnt;
        item.m_sCommentCnt =_sCommentCnt;
        item.m_sJsonString = _sJsonString;
        listViewItemList.add(item);
    }

    public void RemoveAll() {
        listViewItemList.clear();
    }
}