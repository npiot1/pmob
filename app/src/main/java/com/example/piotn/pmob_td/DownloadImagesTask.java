package com.example.piotn.pmob_td;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {

    private Film f;
    private CinemaAdapter adapter;

    public DownloadImagesTask(Film f, CinemaAdapter adapter) {

        this.f = f;
        this.adapter = adapter;

    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(strings[0]);
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

        return bm;
    }


    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        f.setImage(result);
        adapter.notifyDataSetChanged();
    }

}
