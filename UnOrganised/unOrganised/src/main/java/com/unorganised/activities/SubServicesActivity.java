package com.unorganised.activities;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.adapters.CustomListAdapter;
import com.unorganised.adapters.SubService;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.ServiceTypeJSONParser;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

public class SubServicesActivity extends Activity implements HttpRequestResponse, Constants, OnClickListener {

    private int sel_service_id;
    private int userType;
    private String TAG = SubServicesActivity.class.getSimpleName();
    private CustomListAdapter subServiceAdapter;
    private TextView noDataText;
    /**
     * Declaring an ArrayAdapter to set items to ListView
     */
    private UnOrgSharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_services_activity);
        String sel_service = getIntent().getStringExtra(Constants.SEL_SERVICE);
        sel_service_id = getIntent().getIntExtra(Constants.SEL_SERVICE_ID, 0);
        sharedPref = UnOrgSharedPreferences.getInstance(this);
        Utility.stlUserID = sharedPref.getLong(SP_USER_ID_KEY);
        TextView titlebarHeader = (TextView) findViewById(R.id.title_view_id);
        titlebarHeader.setText(sel_service);
        noDataText = (TextView) findViewById(R.id.no_data);
        Button nextButtonId = (Button) findViewById(R.id.sub_service_next_btn_id);
        nextButtonId.setOnClickListener(this);
        Button addServiceBtn = (Button) findViewById(R.id.add_service_btn_id);
        addServiceBtn.setOnClickListener(this);
        HttpReqRespLayer.getInstance().getSubServices(this, this, sel_service_id);
        userType = sharedPref.getInt(SP_USER_TYPE);
    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        DebugLog.d("onSuccess "+data.toString());
        switch (dataType) {
            case GET_SUB_TYPE_SERVICES:
                ServiceTypeJSONParser jsonParser = new ServiceTypeJSONParser();
                HashMap<String, String> subServiceData = jsonParser.parseSubActivity(data);

                if (subServiceData != null) {
                    setListData(subServiceData);
                }

                if (subServiceData.size() == 0) {
                    noDataText.setText(getString(R.string.no_service_avilable));
                }

                break;
            case UPDATE_SERVICES_SUB_SERVICES:
                Utility.resetServiceData();

                if (Utility.isUpdateUser) {
                    HttpReqRespLayer.getInstance().updateUserType(this, this, Utility.stlUserID, 3, "");
                } else if (Utility.isAddRole) {
                    Intent user;
                    if (userType == 1) {
                        //Utility.showToast(this, getResources().getString(R.string.edit_successfully));
                        user = new Intent(this, JobSeekerDashbordActivity.class);
                        user.putExtra(DRAWER_ITEM_POSITION, 5);
                        startActivity(user);
                        break;
                    } else if (userType == 3) {
                        if (Utility.typeId == 1) {
                            //Utility.showToast(this, getResources().getString(R.string.edit_successfully));
                            user = new Intent(this, JobSeekerDashbordActivity.class);
                            user.putExtra(DRAWER_ITEM_POSITION, 5);
                            startActivity(user);
                            break;
                        } else {
                            //Utility.showToast(this, getResources().getString(R.string.edit_successfully));
                            user = new Intent(this, JobProviderDashboardActivity.class);
                            user.putExtra(DRAWER_ITEM_POSITION, 4);
                            startActivity(user);
                            break;
                        }
                    }
                    Utility.isAddRole = false;
                } else {
                    Intent intent = new Intent(this, UserLocationActivity.class);
                    startActivity(intent);
                }
                break;
            case REQUEST_USER_SERVICE:
                jsonParser = new ServiceTypeJSONParser();
                LinkedHashMap<String, List<String>> serviceData = jsonParser.parseUserService(data);
                HttpReqRespLayer.getInstance().updateSevicesAndSubServices(this, this, Utility.stlUserID, Utility.getServices(), serviceData);
                break;
            case UPDATE_USER_TYPE:
                Utility.isUpdateUser = false;
                Utility.stnUserType = 3;
                Intent intent = new Intent(this, UserLocationActivity.class);
                sharedPref.put(SP_USER_TYPE, Utility.stnUserType);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }


    private void setListData(HashMap<String, String> subServiceData) {
        Log.d(TAG, subServiceData.toString());
        ArrayList<SubService> subServiceArrayList = new ArrayList<SubService>();
        SubService subService;

        for (String key : subServiceData.keySet()) {
            subService = new SubService(key, subServiceData.get(key), false);
            subServiceArrayList.add(subService);
        }
        subServiceAdapter = new CustomListAdapter(this, R.layout.list_single, subServiceArrayList);
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(subServiceAdapter);
    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        try {
            Utility.showToast(this, data.getString(Constants.EXCEPTION_MSG));
        } catch (Exception e) {
        }
    }


    @Override
    public void onClick(View v) {
        if (subServiceAdapter != null) {
            ArrayList<String> selSubServices = subServiceAdapter.getSelectedSubServices();
            ArrayList<Integer> selSubServicesIds = new ArrayList<Integer>();

            if (selSubServices.size() == 0) {
                selSubServicesIds.add(0);
            }
            for (int i = 0; i < selSubServices.size(); i++) {
                selSubServicesIds.add(Integer.parseInt(selSubServices.get(i)));
            }
            Utility.addService(sel_service_id, selSubServicesIds);
        }

        switch (v.getId()) {
            case R.id.sub_service_next_btn_id:
                if (Utility.isAddRole) {
                    HttpReqRespLayer.getInstance().getUserServices(this, this, Utility.stlUserID);
                } else {
                    HttpReqRespLayer.getInstance().updateSevicesAndSubServices(this, this, Utility.stlUserID, Utility.getServices(), null);
                }
                break;
            case R.id.add_service_btn_id:
                Intent intent = new Intent(this, ServiceTypesActivity.class);
                Intent i = getIntent();
                boolean role = i.getBooleanExtra("FROM_ROLE", false);
                if (role) {
                    intent.putExtra(ROLE_SETTING, true);
                }
                startActivity(intent);
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

    private void handleBack() {
        finish();

    }

    @Override
    public void onBackPressed() {
        handleBack();
    }


}
