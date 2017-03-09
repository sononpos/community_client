package com.sononpos.communityviwerex;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import im.delight.android.webview.AdvancedWebView;

/**
 * Created by nnnyyy on 2017-03-09.
 */

public class SwipeWebView extends AdvancedWebView {

    GestureDetector gestureDetector;

    public interface SwipeCallback {
        public void OnRightToLeft();
    }

    private SwipeCallback _callback;

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1 == null || e2 == null) return false;
            if(e1.getPointerCount() > 1 || e2.getPointerCount() > 1) return false;
            else {
                try { // right to left swipe .. go to next page
                    if(e1.getX() - e2.getX() > 100 && Math.abs(velocityX) > 800) {
                        //do your stuff
                        System.out.println("Test : " + (e2.getY() - e1.getY()));
                        return false;
                    } //left to right swipe .. go to prev page
                    else if (e2.getX() - e1.getX() > 100 && Math.abs(velocityX) > 800) {
                        //do your stuff
                        System.out.println("Test : " + (e2.getY() - e1.getY()));
                        if( Math.abs(e2.getY() - e1.getY()) > 200 ) return false;
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

    public SwipeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gestureDetector = new GestureDetector(getContext(), new CustomGestureDetector());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    public void setCallback(SwipeCallback callback) {
        this._callback = callback;
    }
}
