package com.unorganised.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.unorganised.R;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

public class SplashActivity extends Activity implements Runnable, Constants {
    UnOrgSharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);
        Handler handler = new Handler();
        handler.postDelayed(this, 1000);
    }

    @Override
    public void run() {
        Intent intent = new Intent();
        sharedPref = UnOrgSharedPreferences.getInstance(this);
        boolean isLogged = sharedPref.getBoolean(SP_LOGIN_STATUS);
//        Utility.isLoggedIn = isLogged;
        Utility.stlUserID = sharedPref.getLong(SP_USER_ID_KEY);
        Log.d(TAG, "isLogged" + isLogged);
        if (isLogged) {
//            Utility.stnNotificationStatus = sharedPref.getInt(SP_NOTIFICATION_STATUS);
            int userType = sharedPref.getInt(SP_USER_TYPE);
            if (userType == 2) {
                intent.setClass(SplashActivity.this, JobProviderDashboardActivity.class);
            } else if (userType == 1 || userType == 3) {
                //Using maps as Fragments
                DebugLog.d("Splashactivity");
                intent.setClass(SplashActivity.this, JobSeekerDashbordActivity.class);
                Utility.typeId = 1;
                //TODO:Un-comment this to use maps as activity
//				intent.setClass(SplashActivity.this,MapsActivityWithLocationUpdate.class);
//				intent.putExtra(Constants.USER_ID, sharedPref.getLong(USER_ID));
//				intent.putExtra(KEY_MAPS_LAUNCHER_TYPE, MAPS_LAUNCHER_TYPE.SEARCH);
            }
        } else {
            intent.setClass(SplashActivity.this, LoadingActivity.class);

        }
        startActivity(intent);
        finish();
    }

    private String TAG = SplashActivity.class.getSimpleName();


}
