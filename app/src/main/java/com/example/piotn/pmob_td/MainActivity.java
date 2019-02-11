package com.example.piotn.pmob_td;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.crypto.NoSuchPaddingException;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;
    private boolean GPS_PERMISSION = false;

    Button b;
    Button b2;
    Button b3;
    Button save;
    Button load;

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String[] permissions = new String[2];
        permissions[0] = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        permissions[1] = Manifest.permission.ACCESS_COARSE_LOCATION;

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_view);
        recycler.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recycler.setLayoutManager(manager);

        b = (Button) findViewById(R.id.button);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        save = (Button) findViewById(R.id.button_save);
        load = (Button) findViewById(R.id.button_load);



        final Activity activity = this;

        //WeakReference<View> wrw = new WeakReference(myView);

        Bitmap icon = BitmapFactory.decodeResource(getBaseContext().getResources(),
                R.drawable.ic_launcher_background);
        SerialBitmap serialIcon = new SerialBitmap(icon);

        final List<Film> films = new ArrayList<Film>();
        //films.add(new Film("Shutter Island", "20/09/2010", "Jean"));
        //films.add(new Film("Avatar", "20/09/2009", "Cameron"));
        //films.add(new Film("La cité de la peur", "20/09/1994", "Marc"));
        for (int i = 0; i<50; i++) {
            Film f = new Film("La cité de la peur", "20/09/1994", "Marc", serialIcon);
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

                if(askPermission(permissions)) {
                    for (final Film f : films) {
                        pool.execute(new HandlerRunnable(adapter, f, activity));
                    }
                }

            }
        });

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //def popup pour save et load
                // Inflate the popup_layout.xml
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_save, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
                Button close = (Button) popupView.findViewById(R.id.button_ok);

                close.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();

                        OutputStream os = null;
                        File path = getBaseContext().getFilesDir();
                        File file = new File(path, "sample.txt");

                        try {
                           os = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        try {
                            SaveFilms.encrypt((Serializable) films, os);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        });

        load.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //def popup pour save et load
                // Inflate the popup_layout.xml
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_save, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
                Button close = (Button) popupView.findViewById(R.id.button_ok);

                close.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();

                        InputStream is = null;
                        File path = getBaseContext().getFilesDir();
                        File file = new File(path, "sample.txt");

                        try {
                            is = new FileInputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        try {
                            List<Film> filmsSerial = (List<Film>) SaveFilms.decrypt(is);
                            adapter.setItems(filmsSerial);
                            adapter.notifyDataSetChanged();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }


                    }
                });
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
                if(askPermission(permissions)) {
                    for (final Film f : films) {
                        handler.post(new HandlerRunnable(adapter, f, activity));
                    }
                }

            }
        });


        //async
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(askPermission(permissions)) {
                    for (Film f : films) {
                        new DownloadImagesTask(f, adapter).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR/*ou l'autre méthode*/, "http://lorempixel.com/100/100");
                        //new DownloadImagesTask(f, adapter).execute("http://lorempixel.com/100/100");
                    }
                }
            }
        });





    }


    private boolean askPermission(String[] permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) || (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_DENIED)) {
                MainActivity.this.requestPermissions(permissions, 1);
                return GPS_PERMISSION;

            }
            else
                return true;

        }

return false;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_DENIED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            finish();
        }

        if(grantResults[1]== PackageManager.PERMISSION_DENIED){
            Log.v(TAG,"Permission: "+permissions[1]+ "was "+grantResults[1]);
            GPS_PERMISSION = false;
            /*b.setClickable(false);
            b2.setClickable(false);
            b3.setClickable(false);
            save.setClickable(false);
            load.setClickable(false);*/
        }else {
            Log.v(TAG,"Permission: "+permissions[1]+ "was "+grantResults[1]);
            GPS_PERMISSION = true;
        }
    }
}
