package com.sononpos.allcommunity.HttpHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nnnyyy on 2017-05-24.
 */

public class HttpHelper {
    public HttpHelperListener listener;
    public void SetListener(HttpHelperListener _listener) {
        listener = _listener;
    }
    public void Request(final int nType, final String sURL) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL(sURL);

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);

                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if( responseCode == HttpURLConnection.HTTP_OK){
                        InputStream is   = null;
                        ByteArrayOutputStream baos = null;
                        String response;
                        is = conn.getInputStream();
                        baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int nLength = 0;

                        while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, nLength);
                        }
                        byteData = baos.toByteArray();
                        response = new String(byteData);

                        listener.onResponse(nType, 0, response);
                    }
                    else {
                        listener.onResponse(nType, -1, "");
                    }
                }catch(IOException e){
                    e.printStackTrace();
                    listener.onResponse(nType, -1, "");
                }finally {
                }
            }
        }).start();
    }

    public Drawable LoadDrawableFromURL(Context context, final String sImageURL) throws IOException {
        URL url = new URL(sImageURL);
        Bitmap x = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        return new BitmapDrawable(context.getResources(), x);
    }
}
