package com.sononpos.allcommunity.RecyclerAdapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.sononpos.allcommunity.ArticleItem;
import com.sononpos.allcommunity.CommunityArticleActivity;
import com.sononpos.allcommunity.G;
import com.sononpos.allcommunity.R;
import com.sononpos.allcommunity.databinding.ArticleItemBinding;

import java.util.ArrayList;

/**
 * Created by nnnyyy on 2017-09-05.
 */

public class ArticlesListRecyclerAdapter extends RecyclerView.Adapter<ArticlesListRecyclerAdapter.ArticleItemViewHolder> {

    ArrayList<ArticleItem> aItemList = new ArrayList<>();
    private int mDuration = 500;
    private Interpolator mInterpolator = new LinearInterpolator();
    int mLastPosition = -1;
    boolean isFirstOnly = false;

    public ArticlesListRecyclerAdapter() {
        setHasStableIds(true);
    }

    @Override
    public ArticlesListRecyclerAdapter.ArticleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ArticleItemBinding bind = ArticleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ArticleItemViewHolder(bind.getRoot());
    }



    @Override
    public void onBindViewHolder(ArticlesListRecyclerAdapter.ArticleItemViewHolder holder, int position) {
        final ArticleItemViewHolder holderInner = holder;
        final ArticleItem item =  aItemList.get(position);
        if(TextUtils.isEmpty(item.m_sViewCnt)) {
            item.m_sViewCnt = "-";
        }
        if(TextUtils.isEmpty(item.m_sCommentCnt)) {
            item.m_sCommentCnt = "-";
        }

        if(TextUtils.isEmpty(item.m_sName)) {
            item.m_sName = "noname";
        }

        if(TextUtils.isEmpty(item.m_sRegDate)) {
            item.m_sRegDate = "nodate";
        }
        holder.mBind.setItem(item);
        String sTextModified = GetTitleAndComment(holderInner, item);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            holderInner.mBind.artTitle.setText(Html.fromHtml(sTextModified, Html.FROM_HTML_MODE_LEGACY));
        }
        else{
            holderInner.mBind.artTitle.setText(Html.fromHtml(sTextModified));
        }

        final TextView tv = holder.mBind.artTitle;
        int nHashcode = item.m_sTitle.hashCode();
        if(G.IsReaded(nHashcode)) {
            tv.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.disabledTextColor));
        }
        else {
            tv.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.mainTextColor));
        }

        holder.mBind.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holderInner.mBind.getRoot().getContext();
                holderInner.mBind.artTitle.setTextColor(ContextCompat.getColor(context, R.color.disabledTextColor));
                Log.e("ClickedItem", holderInner.mBind.getItem().m_sJsonString);
                Intent intent = new Intent(holderInner.mBind.getRoot().getContext(), CommunityArticleActivity.class);
                intent.putExtra("URL", holderInner.mBind.getItem().m_sLink);
                intent.putExtra("TITLE", holderInner.mBind.getItem().m_sTitle);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    Bundle bndlanimation =
                            ActivityOptions.makeCustomAnimation(holderInner.mBind.getRoot().getContext(), R.anim.anim1,R.anim.anim2).toBundle();
                    holderInner.mBind.getRoot().getContext().startActivity(intent, bndlanimation);
                }
                else {
                    holderInner.mBind.getRoot().getContext().startActivity(intent);
                }
                G.SetRead(context, holderInner.mBind.getItem().m_sTitle.hashCode());
            }
        });

        int adapterPosition = holder.getAdapterPosition();
        if (!isFirstOnly || adapterPosition > mLastPosition) {
            Animator anim = ObjectAnimator.ofFloat(holder.itemView, "alpha", 0.5f, 1f);
            anim.setDuration(mDuration).start();
            anim.setInterpolator(mInterpolator);
            mLastPosition = adapterPosition;
        }

    }

    @Override
    public long getItemId(int position) {
        int hashcode = aItemList.get(position).m_sTitle.toString().hashCode();
        return hashcode;
    }

    @Override
    public int getItemCount() {
        return aItemList.size();
    }

    public void AddList(ArrayList<ArticleItem> _list) {
        aItemList.addAll(_list);
    }

    public void ClearList() {
        mLastPosition = -1;
        aItemList.clear();
    }

    protected String GetTitleAndComment(ArticlesListRecyclerAdapter.ArticleItemViewHolder holder, ArticleItem item) {
        String sTitleRet = "";
        String sComment = " <font color='#f95959'>[" + item.m_sCommentCnt + "]</font>";
        String sCommentForCalc = " [" + item.m_sCommentCnt + "]";
        ArticleItemBinding bind = holder.mBind;
        Rect bounds = new Rect();
        Rect boundsComment = new Rect();
        Paint textPaint = bind.artTitle.getPaint();
        //int left = bind.getRoot().getPaddingLeft();
        //int right = bind.getRoot().getWidth() - bind.getRoot().getPaddingRight();
        textPaint.getTextBounds(item.m_sTitle, 0, item.m_sTitle.length(), bounds);
        textPaint.getTextBounds(sCommentForCalc, 0, sCommentForCalc.length(), boundsComment);

        try {
            sTitleRet = item.m_sTitle;
            /*
            if( bounds.width() > (rootViewWidth - 50 - boundsComment.width()) ) {
                float f = 1.0f - ((float)bounds.width() - (rootViewWidth - 50 - boundsComment.width())) / (float)bounds.width();
                int nAdjustLen = (int)((float)item.m_sTitle.length() * f);
                sTitleRet = item.m_sTitle.substring(0, nAdjustLen) + "...";
            }
            else {
                sTitleRet = item.m_sTitle;
            }
            */
        }catch(StringIndexOutOfBoundsException e) {
            Log.e("GetTitleAndComment", "Exception!!!!! " + item.m_sTitle + ", " + item.m_sCommentCnt + ", " + bounds.width() + ", " + boundsComment.width());
            sTitleRet = item.m_sTitle;
        }

        sTitleRet += sComment;

        return sTitleRet;
    }

    public class ArticleItemViewHolder extends RecyclerView.ViewHolder {
        final ArticleItemBinding mBind;

        public ArticleItemViewHolder(View itemView) {
            super(itemView);
            mBind = DataBindingUtil.bind(itemView);
        }
    }
}
