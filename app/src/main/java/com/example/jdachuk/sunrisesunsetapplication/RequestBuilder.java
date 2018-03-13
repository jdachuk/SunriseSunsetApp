package com.example.jdachuk.sunrisesunsetapplication;

import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;

public class RequestBuilder {

    private double lat;
    private double lng;
    private String date;

    private static final int[] COMMON_YEAR = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static final int[] LEAP_YEAR = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private enum YEAR{
        LEAP,
        COMMON
    }

    public RequestBuilder setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public RequestBuilder setLng(double lng) {
        this.lng = lng;
        return this;
    }

    public RequestBuilder setLatLng(LatLng latLng) {
        this.lng = latLng.longitude;
        this.lat = latLng.latitude;
        return this;
    }

    public RequestBuilder setDate(String date) {
        if(dateIsCorrect(date)) {
            this.date = date;
        }
        return this;
    }

    public SunriseSunsetAsync load() {
        SunriseSunsetAsync async = null;

        try {
            URL url;
            if(date == null) {
                url = new URL("https://api.sunrise-sunset.org/json?lat=" + this.lat
                            + "&lng=" + this.lng);
            } else {
                url = new URL("https://api.sunrise-sunset.org/json?lat=" + this.lat
                            + "&lng=" + this.lng + "&date=" + this.date);
            }

            async = new SunriseSunsetAsync(url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        return async;
    }

    // Date should be presented as YYYY-MM-DD
    private boolean dateIsCorrect(String date) {
        String[] ymd = date.split("-");
        int month = Integer.parseInt(ymd[1]);
        int day = Integer.parseInt(ymd[2]);

        if (ymd.length != 3) {
            return false;
        }

        if(month > 12 || month == 0) {
            return false;
        }

        if(day > 31) {
            return false;
        }

        switch (Integer.parseInt(ymd[0]) % 4) {
            // Leap year
            case 0: return checkDay(month, day, YEAR.LEAP);
            //Common year
            default: return checkDay(month, day, YEAR.COMMON);
        }
    }

    private boolean checkDay(int month, int day, YEAR year) {

        switch (year) {
            case LEAP: return LEAP_YEAR[month - 1] >= day;

            case COMMON: return COMMON_YEAR[month - 1] >= day;
        }

        return false;
    }

}
