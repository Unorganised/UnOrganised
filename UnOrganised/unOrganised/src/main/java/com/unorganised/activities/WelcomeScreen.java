package com.unorganised.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.util.Constants;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

public class WelcomeScreen extends Activity implements View.OnClickListener, Constants {

    private int userType;
    private String strImgData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen_activity);
        findViewById(R.id.butt_continue).setOnClickListener(this);
        ((TextView)findViewById(R.id.full_name)).setText(Utility.stStrUserName);
        LinearLayout seeker = (LinearLayout) findViewById(R.id.seeker);
        LinearLayout provider = (LinearLayout) findViewById(R.id.provider);
        UnOrgSharedPreferences sharedPref;
        sharedPref = UnOrgSharedPreferences.getInstance(this);
        userType = sharedPref.getInt(SP_USER_TYPE);

        if (userType == 1) {
            seeker.setVisibility(View.VISIBLE);
        } else if (userType == 2) {
            provider.setVisibility(View.VISIBLE);
        } else {
            provider.setVisibility(View.VISIBLE);
            seeker.setVisibility(View.VISIBLE);
        }
        if(Utility.profileImg != null){
            ((ImageView)findViewById(R.id.profile_icon_id)).setImageBitmap(Utility.profileImg);
        }
    }

    @Override
    public void onClick(View v) {

        switch (userType) {
            case 1:
                // seeker
                launchJobSeekerDashboardActivity();
                finish();
                break;
            case 3:
                // both
                launchJobSeekerDashboardActivity();
                finish();
                break;
            case 2:
                // provider
                launchJobProviderDashboardActivity();
                finish();
                break;
        }
    }

    private void launchJobSeekerDashboardActivity() {
        Intent intent = new Intent(this, JobSeekerDashbordActivity.class);
        Utility.typeId = 1;
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void launchJobProviderDashboardActivity() {
        Intent intent = new Intent(this, JobProviderDashboardActivity.class);
        Utility.typeId = 2;
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
