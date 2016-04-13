package com.unorganised.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.unorganised.R;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

import java.io.IOException;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity implements HttpRequestResponse, OnClickListener, Constants {

    private String TAG = LoginActivity.class.getSimpleName();
    private int type;
    private EditText mobileNumEdt;
    private EditText pwdEdt;
    private Button loginBtn;
    private TextView fypTextView;
    private UnOrgSharedPreferences sharedPref;
    private GoogleCloudMessaging gcm;
    private String strRegisterId;
    private String regid;
    private AsyncTask getRegIdAsynTask;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mobileNumEdt = (EditText) findViewById(R.id.mobile_num_edt);
        fypTextView = (TextView) findViewById(R.id.fyp_view_id);
        pwdEdt = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.sign_in_button);
        loginBtn.setOnClickListener(this);
        sharedPref = UnOrgSharedPreferences.getInstance(this);
        //Utility.stnNotificationStatus = sharedPref.getInt(SP_NOTIFICATION_STATUS);
        fypTextView.setOnClickListener(this);

    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        switch (dataType) {
            case VALIDATE_USER:
                sharedPref.put(SP_LOGIN_STATUS, true);
                sharedPref.put(SP_MOB_NUMBER, mobileNumEdt.getText().toString().trim());
                try {
                    String result = data.getString(RESULT_TAG);
                    Log.d(TAG, "result:" + result);
                    JSONObject resultJson = new JSONObject(result);
                    type = resultJson.getInt("usertype");
                    long userId = resultJson.getLong("userid");
                    double lat = resultJson.getDouble("latitude");
                    double lng = resultJson.getDouble("longitude");
                    strRegisterId = resultJson.getString("registration_id");
                    sharedPref.put(SP_USER_ID_KEY, userId);
                    sharedPref.put(SP_USER_LAT, lat);
                    sharedPref.put(SP_USER_LNG, lng);
                    Utility.stlUserID = userId;

                    if (checkPlayServices()) {
                        gcm = GoogleCloudMessaging.getInstance(this);
                        regid = getRegistrationId(getApplicationContext());
                        Log.d(TAG, "RegID:" + regid);

                        if (regid.isEmpty()) {
                            registerInBackground();
                        } else {
                            HttpReqRespLayer.getInstance().getNotificationStatus(this, this, Utility.stlUserID);
                        }

                    } else {
                        Log.i(TAG, "No valid Google Play Services APK found.");
                    }

                } catch (Exception e) {
                    DebugLog.e(e.toString());
                }
                break;
            case UPDATE_REG_ID:
                HttpReqRespLayer.getInstance().getNotificationStatus(LoginActivity.this, LoginActivity.this, Utility.stlUserID);
                break;
            case FYP:
                Utility.showToast(this, getString(R.string.fyp_success_msg));
                break;
            case GET_NOTIFICATION_STATUS:
                String result = null;
                try {
                    result = data.getString(RESULT_TAG);
                    Log.d(TAG, "result:" + result);
                    JSONObject resultJson = new JSONObject(result);
                    int status = resultJson.getInt(USER_NOTIFICATION_STATUS_TAG);
                    sharedPref.put(SP_NOTIFICATION_STATUS, status);
                    //Utility.stnNotificationStatus = status;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpReqRespLayer.getInstance().getUserStatus(this, this, Utility.stlUserID);
                break;
            case GET_USER_STATUS:
                result = null;
                try {
                    result = data.getString(RESULT_TAG);
                    Log.d(TAG, "result:" + result);
                    JSONObject resultJson = new JSONObject(result);
                    int status = resultJson.getInt(USER_STATUS_TAG);
                    switch (status) {
                        case US_NOT_CREATED:
                        case US_CREATED:
                        case US_USER_TYPE_SELECTED:
                            sharedPref.put(SP_LOGIN_STATUS, false);
                            Utility.showToast(this, getString(R.string.complete_user_registration));
                            Intent intent = new Intent(this, RegisterActivity.class);
                            intent.putExtra(USER_SETTINGS, false);
                            intent.putExtra(USER_STATUS_TAG, status);
                            if (status == US_USER_TYPE_SELECTED) {
                                sharedPref.put(SP_USER_TYPE, type);
                                Utility.stnUserType = type;
                            }
                            startActivity(intent);
                            break;
                        case US_REGISTERED:
                            DebugLog.d("US_REGISTERED;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;");
                            Intent i = getIntent();
                            boolean isFromNotification = i.getBooleanExtra(IS_NOTIFICATION, false);
                            if (isFromNotification) {
                                int screen = i.getIntExtra(SCREEN, 0);
//                                long jobId = i.getLongExtra(JOB_ID, 0);
                                long seekerId = i.getLongExtra(SEEKER_ID_TAG, 0);
                                Intent notfIntent = null;
                                switch (screen) {
                                    case NF_POST_JOB:
                                        //job posted notification
//                                        notfIntent = new Intent(this, JobDetailsActivity.class);
//                                        notfIntent.putExtra(JOB_ID, jobId);
                                        notfIntent = new Intent(this, JobSeekerDashbordActivity.class);
                                        notfIntent.putExtra(DRAWER_ITEM_POSITION, 0);
                                        break;
                                    case NF_ACCEPT_JOB:
                                        //job applied
                                        notfIntent = new Intent(this, JobProviderDashboardActivity.class);
                                        notfIntent.putExtra(DRAWER_ITEM_POSITION, 0);
                                        break;
                                    case NF_ASSIGN_JOB:
                                        //job assigned
                                        DebugLog.d("NF_ASSIGN_JOB");
                                        notfIntent = new Intent(this, JobSeekerDashbordActivity.class);
                                        notfIntent.putExtra(DRAWER_ITEM_POSITION, 2);
                                        break;
                                    case NF_START_JOB_SEEKER:
                                        //seeker started
                                        notfIntent = new Intent(this, JobProviderDashboardActivity.class);
                                        notfIntent.putExtra(DRAWER_ITEM_POSITION, 2);
                                        break;
                                    case NF_START_JOB_PROVIDER:
                                        //provider started
                                        notfIntent = new Intent(this, JobSeekerDashbordActivity.class);
                                        notfIntent.putExtra(DRAWER_ITEM_POSITION, 2);
                                        break;
                                    case NF_STOP_JOB_PROVIDER:
                                        //provider finished
                                        notfIntent = new Intent(this, JobSeekerDashbordActivity.class);
                                        notfIntent.putExtra(DRAWER_ITEM_POSITION, 1);
                                        break;

                                    case NF_STOP_JOB_SEEKER:
                                        //seeker finished
                                        notfIntent = new Intent(this, JobProviderDashboardActivity.class);
                                        notfIntent.putExtra(DRAWER_ITEM_POSITION, 1);
                                        break;
                                    case NF_SHOW_LOCATION:
                                        //navigation started
                                        notfIntent = new Intent(this, ShowLocationActivity.class);
                                        notfIntent.putExtra(SEEKER_ID_TAG, seekerId);
                                        break;

                                }
                                startActivity(notfIntent);
                                finish();

                            } else {
                                if (type == 1) {
                                    launchJobSeekerDashboardActivity();
                                    sharedPref.put(SP_USER_TYPE, 1);
                                } else if (type == 3) {
                                    launchJobSeekerDashboardActivity();
                                    sharedPref.put(SP_USER_TYPE, 3);
                                } else {
                                    launchJobProviderDashboardActivity();
                                    sharedPref.put(SP_USER_TYPE, 2);
                                }
                            }

                            break;
                    }
                    break;

                } catch (JSONException e) {
                    e.printStackTrace();
                }


        }

    }


    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {

        final UnOrgSharedPreferences prefs = getGcmPreferences(context);

        String registrationId = prefs.getString(SP_GCM_REG_ID);
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(SP_PROPERTY_APP_VERSION);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {

        getRegIdAsynTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgress();
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(SENDER_ID);
                    Log.d(TAG, "RegId" + regid);
                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                   // Persist the regID - no need to register again.
                    storeRegistrationId(getApplicationContext(), regid);
                } catch (IOException ex) {
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                    return null;
                }
                return regid;
            }

            @Override
            protected void onPostExecute(String msg) {
                dismissProgress();

                if (regid == null) {
                    Utility.showToast(LoginActivity.this, getString(R.string.gcm_reg_un_successful));
                } else {
                    HttpReqRespLayer.getInstance().updateRegId(LoginActivity.this, LoginActivity.this, Utility.stlUserID, regid);
                }
            }
        }.execute(null, null, null);
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.registering_on_gcm));
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }

        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final UnOrgSharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        prefs.put(SP_GCM_REG_ID, regId);
        prefs.put(SP_PROPERTY_APP_VERSION, appVersion);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private UnOrgSharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return UnOrgSharedPreferences.getInstance(context);
    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        try {
            Utility.showToast(this, data.getString(Constants.MESSAGE));
        } catch (Exception e) {

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign_in_button:
                if (validateValues()) {
                    HttpReqRespLayer.getInstance().validateUser(this, this, Long.parseLong(mobileNumEdt.getText().toString().trim()), pwdEdt.getText().toString().trim());
                }
                break;
            case R.id.fyp_view_id:
                showFypDialog();
                break;
            default:
                break;
        }

    }

    private boolean validateValues() {
        if (mobileNumEdt.getText().toString().trim().length() < 10) {
            Utility.showToast(this, getString(R.string.enter_valid_mob_num));
            return false;
        } else if (pwdEdt.getText().toString().trim().length() == 0) {
            Utility.showToast(this, getString(R.string.enter_password_error));
            return false;
        }
        return true;
    }

    private void launchJobSeekerDashboardActivity() {
        DebugLog.d("launchJobSeekerDashboardActivity");
        Intent intent = new Intent(this, JobSeekerDashbordActivity.class);
        Utility.typeId = 1;
        startActivity(intent);
        finish();
    }

    private void launchJobProviderDashboardActivity() {
        Intent intent = new Intent(this, JobProviderDashboardActivity.class);
        Utility.typeId = 2;
        startActivity(intent);
        finish();
    }

    private void showFypDialog() {
        final Dialog fypDialog = new Dialog(LoginActivity.this);
        fypDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //fypDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        fypDialog.setContentView(R.layout.forgot_password_dialog);
        Button fyp = (Button) fypDialog.findViewById(R.id.fyp_confirm_btn_id);
        final EditText fypPhoneNum = (EditText) fypDialog.findViewById(R.id.fyp_phone_number);
        fyp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = fypPhoneNum.getText().toString().trim();
                if (TextUtils.isEmpty(phoneNum) || phoneNum.length() < 10) {
                    Utility.showToast(LoginActivity.this, getString(R.string.enter_valid_mob_num));
                } else {
                    fypDialog.dismiss();
                    HttpReqRespLayer.getInstance().forgotPassword(LoginActivity.this, LoginActivity.this, Long.parseLong(phoneNum));
                }
            }
        });

        fypDialog.show();

    }


    @Override
    public void onBackPressed() {
//        moveTaskToBack(true);
        Intent intent = new Intent(this,
                ExitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NO_ANIMATION
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        startActivity(intent);
        finish();
//        System.exit(0);
    }
}
