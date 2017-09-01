package com.madfactory.madyoutubefilter.AlertManager;

import android.app.ActionBar;
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
    public static void ShowOk(Context context , String sTitle, String sMsg, final DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView tv = new TextView(context);
        tv.setText(sMsg);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        builder.setTitle(sTitle);
        builder.setPositiveButton("OK", listener);
        builder.setView(tv);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
