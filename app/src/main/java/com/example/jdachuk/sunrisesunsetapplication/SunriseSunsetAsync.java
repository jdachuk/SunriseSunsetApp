package com.example.jdachuk.sunrisesunsetapplication;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SunriseSunsetAsync extends AsyncTask<URL, Void, JSONObject> {

    private StringBuilder data = new StringBuilder();
    private DownloadCallback callback;
    private URL url;

    public SunriseSunsetAsync(URL url) {
        this.url = url;
    }

    @Override
    protected JSONObject doInBackground(URL... strings) {

        JSONObject jsonObject = null;

        try {
            URL url = strings[0];

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                data.append(line);
            }

            jsonObject = new JSONObject(data.toString());

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void withCallback(DownloadCallback callback) {
        this.callback = callback;
        this.execute(url);
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);

        if (jsonObject != null && callback != null) {
            try {
                callback.onTaskSuccessful(jsonObject.getJSONObject("results"));
            } catch (JSONException e) {
                callback.onTaskFailed(e);
            }
        }
    }

}
