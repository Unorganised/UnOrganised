package com.unorganised.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.unorganised.R;
import com.unorganised.util.Utility;

public class SwitchUserActivity extends Activity {

    private LinearLayout providerLayout;
    private LinearLayout seekerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_user_activity);
        providerLayout = (LinearLayout)findViewById(R.id.layout_provider);
        providerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SwitchUserActivity.this, JobProviderDashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Utility.typeId=2;
                startActivity(intent);

            }
        });
        seekerLayout = (LinearLayout)findViewById(R.id.layout_seeker);
        seekerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SwitchUserActivity.this, JobSeekerDashbordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Utility.typeId=1;
                startActivity(intent);
            }
        });

    }
}
