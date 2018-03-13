package com.example.jdachuk.sunrisesunsetapplication;

import org.json.JSONObject;

public interface DownloadCallback {

    void onTaskSuccessful(JSONObject object);
    void onTaskFailed(Exception e);

}
