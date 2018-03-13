package com.example.jdachuk.sunrisesunsetapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.jdachuk.sunrisesunsetapplication.async.SunriseSunsetAsync;
import com.example.jdachuk.sunrisesunsetapplication.callbacks.DownloadCallback;
import com.example.jdachuk.sunrisesunsetapplication.models.Results;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends FragmentActivity
    implements GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, DownloadCallback {

    private GoogleApiClient mGoogleApiClient;
    private PendingResult<PlaceLikelihoodBuffer> mResult;
    private PlaceAutocompleteFragment autocompleteFragment;
    private InfoFragment mInfoFragment;

    private CardView mInfoContainer;
    private ImageView mCollapseBtn;

    private GoogleMap mMap;
    private Place mSelectedPlace;

    private int mInfoContainerState = STATE_COLLAPSED;

    private final static String TAG = "Tag";
    private final static int PERMISSION_REQUEST = 1023;
    private final static int STATE_EXPANDED = 1;
    private final static int STATE_COLLAPSED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInfoFragment = (InfoFragment) getSupportFragmentManager().findFragmentById(R.id.info_container);
        mInfoContainer = findViewById(R.id.container);

        mCollapseBtn = findViewById(R.id.expand_collapse_btn);

        mCollapseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mInfoContainerState) {
                    case STATE_COLLAPSED:
                        mInfoContainer.setVisibility(View.VISIBLE);
                        mCollapseBtn.setImageResource(R.drawable.ic_expand_less_black_24dp);
                        mInfoContainerState = STATE_EXPANDED;
                        break;
                    case STATE_EXPANDED:
                        mInfoContainer.setVisibility(View.GONE);
                        mCollapseBtn.setImageResource(R.drawable.ic_expand_more_black_24dp);
                        mInfoContainerState = STATE_COLLAPSED;
                        break;
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        findViewById(R.id.fix_gps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
                    }
                    return;
                }

                mResult = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
                mResult.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                        PlaceLikelihood place_Likelihood = likelyPlaces.get(0);
                        mSelectedPlace = place_Likelihood.getPlace();
                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            if(placeLikelihood.getLikelihood() < place_Likelihood.getLikelihood()) {
                                mSelectedPlace = placeLikelihood.getPlace();
                            }
                        }
                        autocompleteFragment.setText(mSelectedPlace.getName());
                        getSunriseSunset(mSelectedPlace.getLatLng());
                        likelyPlaces.release();
                    }
                });
            }
        });


        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint("Search");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                mSelectedPlace = place;
                getSunriseSunset(mSelectedPlace.getLatLng());
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
            }
            return;
        }

        mResult = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);

        mResult.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                PlaceLikelihood place_Likelihood = likelyPlaces.get(0);
                mSelectedPlace = place_Likelihood.getPlace();
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    if(placeLikelihood.getLikelihood() < place_Likelihood.getLikelihood()) {
                        mSelectedPlace = placeLikelihood.getPlace();
                    }
                }
                autocompleteFragment.setText(mSelectedPlace.getName());
                getSunriseSunset(mSelectedPlace.getLatLng());
                likelyPlaces.release();
            }
        });
    }

    public void setUpInfo(Results info) {
        mInfoFragment.setUpInfo(info);
    }

    private void getSunriseSunset(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title(mSelectedPlace.getName().toString()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        try {
            URL jsonUrl = new URL("https://api.sunrise-sunset.org/json?lat=" + latLng.latitude
                    + "&lng=" + latLng.longitude);

            new SunriseSunsetAsync()
                    .setCallbackListener(this)
                    .execute(jsonUrl);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST);
                }
                return;
            }
            mResult = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onTaskCompleted(JSONObject object) {
        try {
            Results results = new Results(object.getJSONObject("results"));
            setUpInfo(results);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
