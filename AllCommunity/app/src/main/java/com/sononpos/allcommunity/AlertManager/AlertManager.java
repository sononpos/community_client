package com.sononpos.allcommunity.AlertManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by nnnyyy on 2017-05-25.
 */

public class AlertManager {
    public static void ShowOk(Context context , String sTitle, String sMsg, String sOkText, final DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView tv = new TextView(context);
        tv.setText(sMsg);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        builder.setTitle(sTitle);
        builder.setPositiveButton(sOkText, listener);
        builder.setCancelable(false);
        builder.setView(tv);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void ShowYesNo(Context context , String sTitle, String sMsg, String sOkText, String sNoText, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView tv = new TextView(context);
        tv.setText(sMsg);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        builder.setTitle(sTitle);
        builder.setPositiveButton(sOkText, listener);
        builder.setNegativeButton(sNoText, listener);
        builder.setCancelable(false);
        builder.setView(tv);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
