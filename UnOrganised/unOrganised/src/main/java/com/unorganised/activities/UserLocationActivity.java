package com.unorganised.activities;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.unorganised.R;
import com.unorganised.interfaces.MapsOperations;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;
import com.unorganised.views.MapsFragment;

public class UserLocationActivity extends FragmentActivity implements
        Constants, OnClickListener, MapsOperations, HttpRequestResponse {

    private final String TAG = UserLocationActivity.class.getSimpleName();
    private final String MAPS_TAG = "MapsTag";
    public static FragmentManager fragmentManager;
    private MapsFragment mapsFragment;
    private TextView titleView;
    private Button finishBtn;
    private Button confirmBtn;
    private LinearLayout layoutSteps;
    private LOCATION_ACTIVITY_LAUNCHER_TYPE locationLauncherType = LOCATION_ACTIVITY_LAUNCHER_TYPE.USER_LOCATION;
    private UnOrgSharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_location);
        fragmentManager = getSupportFragmentManager();
        sharedPreferences = UnOrgSharedPreferences.getInstance(this);
        titleView = (TextView) findViewById(R.id.title_view_id);
        int userType;
        userType = sharedPreferences.getInt(SP_USER_TYPE);
        if (userType == 1 || userType == 3) {
            titleView.setText(getResources().getString(R.string.job_seeker_text));
        } else {
            titleView.setText(getResources().getString(R.string.job_provider_text));
        }

        locationLauncherType = (LOCATION_ACTIVITY_LAUNCHER_TYPE) getIntent()
                .getSerializableExtra(KEY_LOCATION_ACTIVITY_LAUNCHER_TYPE);
        showLocationChooser();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish_reg_button_id: {
                LatLng currentLocation = mapsFragment.getLocation();
                // Call the api for registration
                mapsFragment.showSelectedInfoDialog();
                sharedPreferences.put(SP_USER_LAT, currentLocation.latitude);
                sharedPreferences.put(SP_USER_LNG, currentLocation.longitude);
                HttpReqRespLayer.getInstance().updateSevicesAndGeoLocation(this,
                        this, Utility.stlUserID,
                        currentLocation.latitude, currentLocation.longitude);
            }
            break;
            case R.id.confirm_button_id: {
                LatLng currentLocation = mapsFragment.getLocation();
                new ReverseGeocodingTask(this).execute(currentLocation);
            }
            break;
            case R.id.back_btn_id:
                handleBack();
                break;
            case R.id.back_btn_layout_id:
                handleBack();
                break;
            default:
                break;
        }

    }

    private void showLocationChooser() {
        mapsFragment = new MapsFragment(this);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MAPS_LAUNCHER_TYPE,
                MAPS_LAUNCHER_TYPE.REGISTER.ordinal());
        mapsFragment.setArguments(bundle);
        addFragment(R.id.maps_container_id, mapsFragment, MAPS_TAG);
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
        Log.d(TAG, "Searched location lat:" + lat + " Long:" + lng);
    }



    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        switch (dataType) {
            case UPDATE_GEO_LOCATION:
                Log.d(TAG, "onSuccess:" + data);
                startActivity(new Intent(this, TermsAndConditionActivity.class));
                break;

            default:
                break;
        }

    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        try {
            Utility.showToast(this, data.getString(Constants.EXCEPTION_MSG));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        layoutSteps = (LinearLayout)findViewById(R.id.steps_button_id);
        if (locationLauncherType == LOCATION_ACTIVITY_LAUNCHER_TYPE.POST_JOB_LOCATION) {
            mapsFragment.setAutoComplteTextHintmessage(getString(R.string.search_job_location));
            titleView.setText(getString(R.string.job_location));
            findViewById(R.id.finish_reg_button_id).setVisibility(View.GONE);
            confirmBtn = (Button) findViewById(R.id.confirm_button_id);
            confirmBtn.setOnClickListener(this);
            confirmBtn.setVisibility(View.VISIBLE);
            layoutSteps.setVisibility(View.GONE);
        } else {
            finishBtn = (Button) findViewById(R.id.finish_reg_button_id);
            finishBtn.setOnClickListener(this);


        }
    }

    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
        Context mContext;
        double latitude;
        double longitude;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
            Log.d(TAG, "ReverseGeocodingTask");
        }

        @Override
        protected String doInBackground(LatLng... params) {
            Log.d(TAG, "doInBackground");
            latitude = params[0].latitude;
            longitude = params[0].longitude;
            return Utility.getAddressFromLocation(mContext, latitude, longitude);
        }

        @Override
        protected void onPostExecute(String addressText) {
            Log.d(TAG, "onPostExecute done:");
            Intent intent = new Intent();
            intent.putExtra(KEY_LAT, latitude);
            intent.putExtra(KEY_LNG, longitude);
            intent.putExtra(KEY_LOCATION_NAME, addressText);
            setResult(2, intent);
            finish();
        }
    }

    @Override
    public void onMapsReady() {

    }

    private void handleBack() {
        finish();
    }
    @Override
    public void onBackPressed() {
        handleBack();
    }


}
