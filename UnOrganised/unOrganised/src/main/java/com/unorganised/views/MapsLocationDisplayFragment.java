package com.unorganised.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unorganised.R;
import com.unorganised.activities.ShowLocationActivity;
import com.unorganised.util.Constants.MAPS_LAUNCHER_TYPE;
import com.unorganised.util.DebugLog;
import com.unorganised.util.Utility;

public class MapsLocationDisplayFragment extends Fragment implements OnMapReadyCallback {

    private static String TAG = MapsLocationDisplayFragment.class.getSimpleName();
    private GoogleMap googleMap;
    private Activity activity;
    // Holds the launch type of maps screen
    private int launcherType = MAPS_LAUNCHER_TYPE.REGISTER.ordinal();
    private FragmentManager fragmentManager;
    private SupportMapFragment mMapFragment;
    private ProgressDialog progressDialog;
    private double lat;
    private double lng;


//    public MapsLocationDisplayFragment(double lat,double lng) {
//        this.lat = lat;
//        this.lng = lng;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (!isGooglePlayServicesAvailable()) {
            Utility.removeFragment(getActivity(), this);
            return;
        }


        Log.d(TAG, "onCreate end");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = null;
        if (savedInstanceState != null) {
            Utility.removeFragment(getActivity(), this);
            return view;
        }
        if (view == null) {
            view = inflater.inflate(R.layout.location_display_fragment, container, false);

            mMapFragment = SupportMapFragment.newInstance();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.location_map_container, mMapFragment);
            fragmentTransaction.commit();
            mMapFragment.getMapAsync(this);


        }
        return view;

    }


    public void clearMap() {
        googleMap.clear();
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired");


    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, activity, 0).show();
            return false;
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        if (activity instanceof ShowLocationActivity) {
            fragmentManager = ShowLocationActivity.fragmentManager;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }


    private void showProgressDialog() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void showLocationOnMap(double lat,double lng)
    {
        this.lat = lat;
        this.lng = lng;
        DebugLog.d("showLocationOnMap:" + googleMap);
        DebugLog.d("Lat:" + lat + " Lng:"+lng);
        if(googleMap!=null) {
            googleMap.clear();
            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(lat, lng));
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.tracking_marker));
            Marker marker = googleMap.addMarker(options);
            googleMap
                    .moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 13));
        }
    }

    public double getDistance(double destLat,double destLng)
    {
        float[] resultArray = new float[0];
        Location.distanceBetween(lat, lng, destLat, destLng, resultArray);
        return resultArray[0]/1e6;
    }

    @Override
    public void onMapReady(GoogleMap arg0) {
        Log.d(TAG, "onMapReady");
        try {
            googleMap = mMapFragment.getMap();
            // Enabling MyLocation Layer of Google Map
            //googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(lat, lng));
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.tracking_marker));
            Marker marker = googleMap.addMarker(options);
            googleMap
                    .moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 13));
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        } catch (Exception e) {
            // TODO: handle exception
        }


    }


}