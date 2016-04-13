package com.unorganised.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.unorganised.R;
import com.unorganised.interfaces.MapsOperations;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.Utility;
import com.unorganised.views.MapsLocationDisplayFragment;

import org.json.JSONException;
import org.json.JSONObject;


public class ShowLocationActivity extends FragmentActivity implements
        Constants, MapsOperations, HttpRequestResponse {

    private final String TAG = ShowLocationActivity.class.getSimpleName();
    private final String MAPS_TAG = "MapsTag";
    private double jobLat;
    private double jobLng;
    public static FragmentManager fragmentManager;
    private MapsLocationDisplayFragment mapsFragment;
    private TextView titleView;
    TextView timeView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seeker_location);
        fragmentManager = getSupportFragmentManager();
        titleView = (TextView) findViewById(R.id.location_title_id);
        timeView = (TextView)findViewById(R.id.time_comsumes_id);
        Intent intent = getIntent();
        LOCATION_DISPLAY_LAUNCHER_TYPE laucherType = (LOCATION_DISPLAY_LAUNCHER_TYPE)intent.getSerializableExtra(KEY_LOCATION_DISPLAY_LAUNCHER_TYPE);

        if(laucherType == LOCATION_DISPLAY_LAUNCHER_TYPE.PROVIDER)
        {
            //mapsFragment = new MapsLocationDisplayFragment(intent.getDoubleExtra(KEY_LAT, 0),intent.getDoubleExtra(KEY_LNG, 0));
            //addFragment(R.id.maps_container_id, mapsFragment, MAPS_TAG);
           // mapsFragment.showLocationOnMap(intent.getDoubleExtra(KEY_LAT, 0), intent.getDoubleExtra(KEY_LNG, 0));
        }
        else if(laucherType == LOCATION_DISPLAY_LAUNCHER_TYPE.SEEKER)
        {
            mapsFragment = new MapsLocationDisplayFragment();
            addFragment(R.id.maps_container_id, mapsFragment, MAPS_TAG);
            HttpReqRespLayer.getInstance().getSeekerLocation(this, this, intent.getIntExtra(SEEKER_ID_TAG, 1));
            jobLat = (double) intent.getDoubleExtra(JOB_LAT,0);
            jobLng = (double) intent.getDoubleExtra(JOB_LNG,0);
            DebugLog.d("Job Lat:"+ jobLat +" Lng:"+ jobLng);
            startRepeatingTask();
        }

    }

    private void hideLocationChooser() {
        removeFragment(R.id.maps_container_id);
    }

    /**
     * Method to find the fragment by id.
     *
     * @param id
     * @return Fragment object.
     */
    public Fragment findFragmentById(int id) {
        return getFragmentManager().findFragmentById(id);
    }

    /**
     * Method to find the fragment by tag.
     *
     * @param tag
     * @return Fragment object.
     */
    public Fragment findFragmentByTag(String tag) {
        return getFragmentManager().findFragmentByTag(tag);
    }

    /**
     * Method to hide the fragment by id.
     *
     * @param id
     */
    public void hideFragment(int id) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = findFragmentById(id);
        ft.hide(fragment).commitAllowingStateLoss();

    }

    /**
     * Method to show the fragment by id.
     *
     * @param id
     */
    public void showFragment(int id) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = findFragmentById(id);
        ft.show(fragment).commitAllowingStateLoss();

    }

    /**
     * Method to add the fragment to the container by tag.
     *
     */
    public void addFragment(int containerId, Fragment fragment, String tag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(containerId, fragment, tag).commitAllowingStateLoss();

    }

    /**
     * Method to remove the fragment by tag.
     *
     * @param tag
     */
    public void removeFragment(String tag) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = findFragmentByTag(tag);
        if (fragment != null) {
            ft.remove(fragment).commitAllowingStateLoss();

        }

    }

    protected boolean isFragmentVisible(String tag) {
        Fragment fragment = findFragmentByTag(tag);
        if (fragment != null && fragment.isVisible()) {

            return true;
        } else {
            return false;
        }

    }

    /**
     * Method to remove the fragment by id.
     *
     * @param id
     */
    public void removeFragment(int id) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment = findFragmentById(id);
        if (fragment != null) {
            ft.remove(fragment).commitAllowingStateLoss();
        }

    }

    @Override
    public void onMarkerClick(Marker marker) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSearch(double lat, double lng) {
    }



    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        DebugLog.d("****onSuccess*****");
            try{
                JSONObject result = null;
                try
                {
                    result = data.getJSONObject(RESULT_TAG);
                    double seekerLng = result.optDouble(SEEKER_LNG);
                    double seekerLat = result.optDouble(SEEKER_LAT);
                    DebugLog.d("seeker Lat:"+seekerLat+" Lng:"+seekerLng);
                    mapsFragment.showLocationOnMap(seekerLat, seekerLng);
//                    double time = mapsFragment.getTime(jobLat, jobLng);
//                    timeView.setText(time+" Min");

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
            catch (Exception e)
            {
                DebugLog.e("Exception while getting seeker location:"+e);
            }

    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        try {
            Utility.showToast(this, data.getString(Constants.EXCEPTION_MSG));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    Handler mHandler =  new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DebugLog.d("handleMessage");
        }
    };

    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {
            HttpReqRespLayer.getInstance().getSeekerLocation(ShowLocationActivity.this, ShowLocationActivity.this, getIntent().getIntExtra(SEEKER_ID_TAG, 1));
            mHandler.postDelayed(mHandlerTask, Constants.SEEKER_LOCATION_UPDATE_TIME_INTERVAL);
        }
    };

    void startRepeatingTask()
    {
        mHandlerTask.run();
    }

    void stopRepeatingTask()
    {
        mHandler.removeCallbacks(mHandlerTask);
    }

    @Override
    public void onMapsReady() {

    }
}
