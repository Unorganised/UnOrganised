package com.unorganised.activities;

import android.app.Activity;

import android.graphics.Typeface;
import android.os.Bundle;


import android.content.Intent;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

import org.json.JSONObject;

import com.unorganised.R;

public class ChangePasswordActivity extends Activity implements View.OnClickListener, HttpRequestResponse, Constants {
    private int userType;
    private long userId;
    private Button saveButton;
    private Button cancelButton;
    private EditText newPassword;
    private EditText retypePassword;
    UnOrgSharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        saveButton = (Button) findViewById(R.id.savePassword);
        cancelButton = (Button) findViewById(R.id.cancel_changePassword);
        newPassword = (EditText) findViewById(R.id.new_Password);
        retypePassword = (EditText) findViewById(R.id.retype_password);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        sharedPref = UnOrgSharedPreferences.getInstance(this);
        userId = sharedPref.getLong(SP_USER_ID_KEY);
        userType = sharedPref.getInt(USER_TYPE_KEY);
        newPassword.setTypeface(Typeface.DEFAULT);
        retypePassword.setTypeface(Typeface.DEFAULT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.savePassword:
                String new_pwd = newPassword.getText().toString();
                String retype_pwd = retypePassword.getText().toString();

                if (validateValues(new_pwd, retype_pwd)) {
                    getInstance().changepassword(this, this, userId, new_pwd);
                }
                break;
            case R.id.cancel_changePassword:
                finish();
//                Intent user;
//                if (userType == 1 || userType == 3) {
//                    user = new Intent(this, JobSeekerDashbordActivity.class);
//                    user.putExtra(DRAWER_ITEM_POSITION, 5);
//                    finish();
//                } else {
//                    user = new Intent(this, JobProviderDashboardActivity.class);
//                    user.putExtra(DRAWER_ITEM_POSITION, 4);
//                    finish();
//                }
//                startActivity(user);
                break;
        }

    }

    private boolean validateValues(String newPwd, String retypePwd) {

        if (newPwd.length() == 0 || retypePwd.length() == 0) {
            Utility.showToast(this, getString(R.string.enter_password_error));
            return false;


        } else if (!newPwd.equals(retypePwd)) {
            Utility.showToast(this, getString(R.string.password_missmatch_error));
            retypePassword.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess(JSONObject data, Constants.HttpReqRespActionItems dataType) {
        switch (dataType)
        {
            case CHANGE_PASSWORD:
                Utility.showToast(this, getResources().getString(R.string.edit_successfully));
                Intent user;
                if (userType == 1 ) {
                    user = new Intent(this, JobSeekerDashbordActivity.class);
                    user.putExtra(DRAWER_ITEM_POSITION, 5);
                    startActivity(user);
                    finish();
                    break;
                } else if(userType==2) {
                    user = new Intent(this, JobProviderDashboardActivity.class);
                    user.putExtra(DRAWER_ITEM_POSITION, 4);
                    startActivity(user);
                    finish();
                    break;
                }
                else {
                    if(userType==3)
                    {
                        if(Utility.typeId==1)
                        {
                            Utility.showToast(this, getResources().getString(R.string.edit_successfully));
                            user = new Intent(this, JobSeekerDashbordActivity.class);
                            user.putExtra(DRAWER_ITEM_POSITION, 5);
                            startActivity(user);
                            finish();
                            break;

                        }
                        else
                        {
                            Utility.showToast(this, getResources().getString(R.string.edit_successfully));
                            user = new Intent(this, JobProviderDashboardActivity.class);
                            user.putExtra(DRAWER_ITEM_POSITION, 4);
                            startActivity(user);
                            finish();
                            break;
                        }
                    }
                    finish();
                }


        }


    }

    @Override
    public void onFailure(JSONObject data, Constants.HttpReqRespActionItems dataType) {

        try {
            Utility.showToast(this, data.getString(Constants.MESSAGE));
        } catch (Exception e) {

        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
