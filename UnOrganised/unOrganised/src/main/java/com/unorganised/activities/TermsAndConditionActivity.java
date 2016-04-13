package com.unorganised.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.unorganised.R;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

import org.json.JSONObject;

public class TermsAndConditionActivity extends Activity implements View.OnClickListener,Constants, HttpRequestResponse {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_and_condition_activity);
        Button disAgreeBtn = (Button) findViewById(R.id.disagree_btn_id);
        disAgreeBtn.setOnClickListener(this);
        Button acceptsBtn = (Button) findViewById(R.id.accept_btn_id);
        acceptsBtn.setOnClickListener(this);
        setTitle();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.disagree_btn_id:
                showTandCDialog();
                break;
            case R.id.accept_btn_id:
                HttpReqRespLayer.getInstance().updateUserStatus(this, this, Utility.stlUserID, US_REGISTERED);
                Toast.makeText(this, getString(R.string.user_registered_successsfully), Toast.LENGTH_LONG).show();
                UnOrgSharedPreferences sp = UnOrgSharedPreferences.getInstance(this);
                sp.put(SP_LOGIN_STATUS, true);
                sp.put(SP_USER_ID_KEY, Utility.stlUserID);
                sp.put(SP_NOTIFICATION_STATUS, 1);
                startActivity(new Intent(this, WelcomeScreen.class));
                break;
            case R.id.back_btn_layout_id:
                handleBack();
                break;
            case R.id.back_btn_id:
                handleBack();
                break;
            default:
                break;

        }
    }

    private void showTandCDialog() {
        final Dialog disagreeDialog = new Dialog(this);
        disagreeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        disagreeDialog.setContentView(R.layout.disagree_confirm);
        disagreeDialog.show();
        disagreeDialog.findViewById(R.id.ok_button_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disagreeDialog.dismiss();
                startActivity(new Intent(TermsAndConditionActivity.this, LoadingActivity.class));

            }
        });
        disagreeDialog.findViewById(R.id.cancel_button_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disagreeDialog.dismiss();
            }
        });
    }

    private void setTitle() {
        TextView title = (TextView)findViewById(R.id.title_view_id);
        title.setText(R.string.t_and_c_title);
    }

    private void handleBack()
    {
        finish();
    }
    @Override
    public void onBackPressed() {
        handleBack();
    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {

    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {

    }
}
