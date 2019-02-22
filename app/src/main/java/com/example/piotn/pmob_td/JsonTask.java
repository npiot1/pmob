package com.example.piotn.pmob_td;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

class JsonTask extends AsyncTask<Void, Void, JSONObject>
{

    CinemaAdapter adapter;
    List<Film> films;
    Bitmap icon;

    public JsonTask(CinemaAdapter adapter, List<Film> films, Bitmap icon) {
        this.adapter = adapter;
        this.films = films;
        this.icon = icon;
    }

    @Override
    protected JSONObject doInBackground(Void... params)
    {

        String str="http://www.omdbapi.com/?s=thriller&apikey=20de11f6";
        URLConnection urlConn = null;
        BufferedReader bufferedReader = null;
        try
        {
            URL url = new URL(str);
            urlConn = url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                stringBuffer.append(line);
            }

            return new JSONObject(stringBuffer.toString());
        }
        catch(Exception ex)
        {
            Log.e("App", "yourDataTask", ex);
            return null;
        }
        finally
        {
            if(bufferedReader != null)
            {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostExecute(JSONObject response)
    {
        if(response != null)
        {
            try {
                //Log.e("App", "Success: " + response.getString("yourJsonElement") );
                films.clear();
                JSONArray filmsJSON = response.getJSONArray("Search");
                //for(JSONObject jsonObj : filmsJSON.get)
                for(int i = 0; i<filmsJSON.length(); i++) {
                    JSONObject obj = filmsJSON.getJSONObject(i);
                    Film filmjson = new Film(obj.getString("Title"), obj.getString("Year"), /*response.getString("Director")*/"", new SerialBitmap(icon));
                    films.add(filmjson);
                }

                for(int i = 0; i<filmsJSON.length();i++) {
                    JSONObject obj = filmsJSON.getJSONObject(i);
                    new DownloadImagesTask(films.get(i), adapter).execute(obj.getString("Poster"));
                }



                //films.add(filmjson);
                //adapter.notifyDataSetChanged();
            } catch (JSONException ex) {
                Log.e("App", "Failure", ex);
            }
        }
    }
}