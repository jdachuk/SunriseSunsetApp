package com.example.jdachuk.sunrisesunsetapplication;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Results {

    private String sunrise;
    private String sunset;
    private String solar_noon;
    private String day_length;
    private String civil_twilight_begin;
    private String civil_twilight_end;
    private String astronomical_twilight_begin;
    private String astronomical_twilight_end;

    public Results(JSONObject jsonObject) {
        Log.d("Test", jsonObject.toString());
        try {
            this.astronomical_twilight_begin = jsonObject.getString("astronomical_twilight_begin");
            this.astronomical_twilight_end = jsonObject.getString("astronomical_twilight_end");
            this.civil_twilight_begin = jsonObject.getString("civil_twilight_begin");
            this.civil_twilight_end = jsonObject.getString("civil_twilight_end");
            this.day_length = jsonObject.getString("day_length");
            this.solar_noon = jsonObject.getString("solar_noon");
            this.sunrise = jsonObject.getString("sunrise");
            this.sunset = jsonObject.getString("sunset");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public String getDay_length() {
        return day_length;
    }

    public String getCivil_twilight_begin() {
        return civil_twilight_begin;
    }

    public String getCivil_twilight_end() {
        return civil_twilight_end;
    }
}
