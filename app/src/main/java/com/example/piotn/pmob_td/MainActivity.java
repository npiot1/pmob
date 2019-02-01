package com.example.piotn.pmob_td;

import android.app.Activity;
import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_view);
        recycler.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager);

        Button b = (Button) findViewById(R.id.button);
        Button b2 = (Button) findViewById(R.id.button2);
        Button b3 = (Button) findViewById(R.id.button3);

        final Activity activity = this;

        //WeakReference<View> wrw = new WeakReference(myView);

        Bitmap icon = BitmapFactory.decodeResource(getBaseContext().getResources(),
                R.drawable.ic_launcher_background);

        final List<Film> films = new ArrayList<Film>();
        //films.add(new Film("Shutter Island", "20/09/2010", "Jean"));
        //films.add(new Film("Avatar", "20/09/2009", "Cameron"));
        //films.add(new Film("La cité de la peur", "20/09/1994", "Marc"));
        for (int i = 0; i<50; i++) {
            Film f = new Film("La cité de la peur", "20/09/1994", "Marc", icon);
            films.add(f);
        }

        final CinemaAdapter adapter = new CinemaAdapter(films);
        recycler.setAdapter(adapter);


        //pool thread

        ImageThreadFactory sThreadFactory =  new ImageThreadFactory();

        final Executor pool
                = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (final Film f : films) {
                    pool.execute(new HandlerRunnable(adapter, f, activity));
                }

            }
        });

        //handler
        HandlerThread handlerThread = new HandlerThread("handler");
        handlerThread.start();

        Looper looper = handlerThread.getLooper();

        //final Handler handler = new Handler(Looper.getMainLooper());
        final Handler handler = new Handler(looper);


        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (final Film f : films) {
                    handler.post(new HandlerRunnable(adapter, f, activity));
                }

            }
        });


        //async
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Film f : films) {
                    new DownloadImagesTask(f, adapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR/*ou l'autre méthode*/, "http://lorempixel.com/100/100");
                    //new DownloadImagesTask(f, adapter).execute("http://lorempixel.com/100/100");
                }
            }
        });





    }
}
