
package com.unorganised.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.UnOrganisedApplication;

import org.json.JSONObject;

public class SeekerLocationUpdateService extends Service implements HttpRequestResponse,LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{


    private SeekerLocationUpdateService service;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        DebugLog.e("onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        service =  this;

        try {
            // show error dialog if GoolglePlayServices not available
            createLocationRequest();
            googleApiClient = new GoogleApiClient.Builder(service)
                    .addApi(LocationServices.API).addConnectionCallbacks(service)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            startLocationUpdates();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void onDestroy()
    {
        DebugLog.d("onDestroy");
        super.onDestroy();
        stopLocationUpdates();

    }



    @Override
    public void onSuccess(JSONObject data, Constants.HttpReqRespActionItems dataType) {
        DebugLog.d("onSuccess:" + data);
    }

    @Override
    public void onFailure(JSONObject data, Constants.HttpReqRespActionItems dataType) {
        DebugLog.d("onFailure:" + data);
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(Constants.SEEKER_LOCATION_UPDATE_TIME_INTERVAL);
        locationRequest.setFastestInterval(Constants.SEEKER_LOCATION_UPDATE_TIME_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        DebugLog.d("Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        DebugLog.d("****onLocationChanged****:"+location);
        UnOrgSharedPreferences sp = UnOrgSharedPreferences.getInstance(UnOrganisedApplication.getContext());
        long userId = sp.getLong(Constants.SP_USER_ID_KEY);
        HttpReqRespLayer.getInstance().updateSeekerLocation(this, UnOrganisedApplication.getContext(), userId, location.getLatitude(), location.getLongitude());

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Start location updates
     */
    private void startLocationUpdates() {

        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        }
    }

    /**
     * Stop location updates
     */
    public void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}