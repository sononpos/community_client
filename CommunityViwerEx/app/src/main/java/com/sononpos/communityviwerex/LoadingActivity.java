package com.sononpos.communityviwerex;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class LoadingActivity extends AppCompatActivity {

    class MyHandler extends Handler {
        private LoadingActivity mainActivity;

        public MyHandler(LoadingActivity act){
            mainActivity = act;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Intent intent = new Intent(mainActivity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }

    MyHandler handlerPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        G.liCommTypeInfo.clear();
        ArrayList<String> aFiltered = G.getStringArrayPref(this, G.FILTERED_COMM);
        if(aFiltered != null) {
            G.liFiltered = new HashSet<String>(aFiltered);
        }
        else {
            G.liFiltered.clear();
        }
        handlerPager = new MyHandler(this);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try{
                    URL url = new URL("http://52.79.205.198:3000/list");

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
                        JSONObject jobj = new JSONObject(response);
                        Iterator<String> iter = jobj.keys();
                        while(iter.hasNext()){
                            String key = iter.next();
                            JSONObject element = jobj.getJSONObject(key);
                            String sName = element.getString("name");
                            G.liCommTypeInfo.add(new CommunityTypeInfo(key, sName));
                        }

                        G.RefreshFilteredInfo();

                        Message msg = handlerPager.obtainMessage();
                        handlerPager.sendMessage(msg);
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }catch(JSONException e) {
                    e.printStackTrace();
                }finally {
                }
            }
        }).start();

    }
}
