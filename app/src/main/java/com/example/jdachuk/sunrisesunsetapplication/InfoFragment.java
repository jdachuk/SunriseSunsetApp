package com.example.jdachuk.sunrisesunsetapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jdachuk.sunrisesunsetapplication.models.Results;

public class InfoFragment extends Fragment {

    private TextView mDayLength, mSunrise, mSunset, mTwilightStart, mTwilightEnd;

    public InfoFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDayLength = view.findViewById(R.id.day_length_info);
        mSunrise = view.findViewById(R.id.sunrise_info);
        mSunset = view.findViewById(R.id.sunset_info);
        mTwilightStart = view.findViewById(R.id.twilight_start_info);
        mTwilightEnd = view.findViewById(R.id.twilight_end_info);
    }

    public void setUpInfo(Results info) {
        mDayLength.setText(info.getDay_length());
        mSunrise.setText(info.getSunrise());
        mSunset.setText(info.getSunset());
        mTwilightStart.setText(info.getCivil_twilight_begin());
        mTwilightEnd.setText(info.getCivil_twilight_end());
    }
}
