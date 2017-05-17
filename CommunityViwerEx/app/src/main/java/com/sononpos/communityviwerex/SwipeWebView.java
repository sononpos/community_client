package com.sononpos.communityviwerex;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.Toast;

import im.delight.android.webview.AdvancedWebView;

/**
 * Created by nnnyyy on 2017-03-09.
 */

public class SwipeWebView extends AdvancedWebView {

    GestureDetector gestureDetector;

    public interface SwipeCallback {
        public void OnRightToLeft();
        public void OnLeftToRight();
        public void closeCallback();
    }

    private SwipeCallback _callback;

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1 == null || e2 == null) return false;
            if(e1.getPointerCount() > 1 || e2.getPointerCount() > 1) return false;
            else {
                try { // right to left swipe .. go to next page
                    if(e1.getX() - e2.getX() > 100 && Math.abs(velocityX) > 900) {
                        //do your stuff
                        if( Math.abs(e2.getY() - e1.getY()) > 180 ) return false;
                        if(_callback != null) {
                            _callback.OnLeftToRight();
                        }
                        return true;
                    } //left to right swipe .. go to prev page
                    else if (e2.getX() - e1.getX() > 200 && Math.abs(velocityX) > 900) {
                        //do your stuff
                        if( Math.abs(e2.getY() - e1.getY()) > 100 ) return false;
                        if(_callback != null) {
                            _callback.OnRightToLeft();
                        }
                        return true;
                    } //bottom to top, go to next document
                } catch (Exception e) { // nothing
                }
                return false;
            }
        }
    }

    public SwipeWebView(Context context) {
        super(context);
        gestureDetector = new GestureDetector(getContext(), new CustomGestureDetector());
    }

    public SwipeWebView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gestureDetector = new GestureDetector(getContext(), new CustomGestureDetector());

        setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimeType, final long contentLength) {
                final String suggestedFilename = URLUtil.guessFileName(url, contentDisposition, mimeType);

                if (mListener != null) {
                    mListener.onDownloadRequested(url, suggestedFilename, mimeType, contentLength, contentDisposition, userAgent);
                }
            }

        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    public void setCallback(SwipeCallback callback) {
        this._callback = callback;
    }
}
