package com.example.piotn.pmob_td;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class HandlerRunnable implements Runnable {

    private WeakReference<CinemaAdapter> adapter;
    private Film f;
    private  WeakReference<Activity> a;

    public HandlerRunnable(CinemaAdapter adapter, Film f, Activity a) {
        this.adapter = new WeakReference<CinemaAdapter>(adapter);
        this.f = f;
        this.a = new WeakReference<Activity>(a);
    }

    @Override
    public void run() {


        Bitmap bm = null;
        try {
            URL aURL = new URL("http://lorempixel.com/100/100");
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
        }

        f.setImage(new SerialBitmap(bm));

        Activity activity = a.get();
        final CinemaAdapter adap = adapter.get();

        if(activity!=null && adapter!=null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adap.notifyDataSetChanged();
                }
            });
        }



    }
}
