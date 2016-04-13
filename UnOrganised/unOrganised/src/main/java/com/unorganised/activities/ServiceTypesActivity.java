package com.unorganised.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.unorganised.R;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.ServiceTypeJSONParser;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

public class ServiceTypesActivity extends Activity implements HttpRequestResponse, Constants, OnClickListener {
    private boolean roleSetting;
    private int nSelected = 0;
    private int nTotalServices;
    private int[] nServiceIconId = {R.drawable.icon_homecare,
            R.drawable.icn_repair, R.drawable.ic_facility_manager,
            R.drawable.icon_eventplaning, R.drawable.icon_house_worker,
            R.drawable.icn_hobbies, R.drawable.icon_houseparty,
            R.drawable.icon_eventplaning, R.drawable.icon_photogrpaher,
            R.drawable.icn_dailyneeds};
    private int[] nSelServiceIconId = {R.drawable.icon_homecare_onselect,
            R.drawable.icn_repair_select, R.drawable.ic_facility_manager_selected,
            R.drawable.icon_eventplaning_onselct, R.drawable.icon_house_worker_selected,
            R.drawable.icn_hobbies_select, R.drawable.icon_houseparty_onselect,
            R.drawable.icon_eventplaning_onselct, R.drawable.icon_photogrpaher_onselect,
            R.drawable.icn_dailyneeds_onselect};
    /*
     * These values will be taking from server
     */
    private String[] strServices;
    private Bitmap serviceIcon;
    private ImageView[] imgViewIcon;
    private TableLayout table_layout;
    private TextView registrationStepTitle;
    private TextView txtFullname;
    private Button nextBtn;
    private LinearLayout dotlayout;
    private ScrollView serviceList;
    private LinearLayout bottomLayout;
    private UnOrgSharedPreferences unOrgSharedPref;
    private HashMap<String, String> serviceData;
    ArrayList<String> selected;
    ArrayList<Integer> selectedList;
    private boolean serivesel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_types_activity);
        unOrgSharedPref = UnOrgSharedPreferences.getInstance(this);
        Utility.stlUserID = unOrgSharedPref.getLong(SP_USER_ID_KEY);
        registrationStepTitle = (TextView) findViewById(R.id.title_view_id);
        registrationStepTitle.setText(getResources().getString(R.string.job_seeker_text));
        txtFullname = (TextView) findViewById(R.id.tile_view_header_id);
        txtFullname.append(unOrgSharedPref.getString(USER_NAME));
        table_layout = (TableLayout) findViewById(R.id.service_type_table);
        nextBtn = (Button) findViewById(R.id.service_next_btn_id);
        dotlayout = (LinearLayout) findViewById(R.id.dotLayout);
        serviceList = (ScrollView) findViewById(R.id.service_list);
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        nextBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startSubServicesActivity();
            }
        });
        //Getting services
        HttpReqRespLayer.getInstance().getServices(this, this);
        Intent i = getIntent();
        roleSetting = i.getBooleanExtra(ROLE_SETTING, false);


    }

    private void getTableData(HashMap<String, String> serviceData) {
        int index = 1;
        nTotalServices = serviceData.size();
        imgViewIcon = new ImageView[nTotalServices];
        strServices = new String[nTotalServices];
        for (int i = 0; i < nTotalServices; i++) {
            imgViewIcon[i] = new ImageView(this);
            if (nServiceIconId.length > i) {
                serviceIcon = BitmapFactory.decodeResource(getResources(), nServiceIconId[i]);
            } else {
                serviceIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_default_service);
            }
            imgViewIcon[i].setImageBitmap(serviceIcon);
            strServices[i] = serviceData.get("" + index);
            index++;
        }
    }

    private void buildTable() {
        int colId = 0;
        int colCount = 2;
        int rowCount = 0;
        int leftMargin = 5;
        int topMargin = 5;
        int rightMargin = 5;
        int bottomMargin = 5;
        int textMargin = 10;
        LayoutParams linearLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LayoutParams textViewLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LayoutParams iconLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        linearLayoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        textViewLayoutParam.setMargins(textMargin, textMargin, 0, textMargin);
        iconLayoutParam.setMargins(textMargin, textMargin, 0, textMargin);

        rowCount = (nTotalServices % 2 == 0) ? nTotalServices / 2 : (nTotalServices / 2) + 1;

        for (int i = 0; i < rowCount; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            tableRow.setLayoutParams(linearLayoutParams);

            if (rowCount - 1 == i) {
                colCount = (nTotalServices % 2 == 0) ? 2 : 1;
            }

            for (int j = 0; j < colCount; j++) {
                LinearLayout layoutColumn = new LinearLayout(this);
                layoutColumn.setId(colId);
                ImageView imgView = (ImageView) imgViewIcon[colId];
                imgView.setId(200 + colId);
                layoutColumn.addView(imgView, iconLayoutParam);
                layoutColumn.setBackgroundColor(getResources().getColor(R.color.bg_gray));
                layoutColumn.setOnClickListener(this);
                TextView textView = new TextView(this);
                textView.setId(300 + colId);
                textView.setTextColor(Color.BLACK);
                textView.setText(strServices[colId]);
                layoutColumn.addView(textView, textViewLayoutParam);
                tableRow.addView(layoutColumn, linearLayoutParams);
                colId++;
            }
            table_layout.addView(tableRow);
        }
    }

    private void buildTableselected(ArrayList<String> selected) {
        int colId = 0;
        int colCount = 2;
        int rowCount = 0;
        int leftMargin = 5;
        int topMargin = 5;
        int rightMargin = 5;
        int bottomMargin = 5;
        int textMargin = 10;
        LayoutParams linearLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LayoutParams textViewLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        LayoutParams iconLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        linearLayoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        textViewLayoutParam.setMargins(textMargin, textMargin, 0, textMargin);
        iconLayoutParam.setMargins(textMargin, textMargin, 0, textMargin);

        rowCount = (nTotalServices % 2 == 0) ? nTotalServices / 2 : (nTotalServices / 2) + 1;

        for (int i = 0; i < rowCount; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            tableRow.setLayoutParams(linearLayoutParams);

            if (rowCount - 1 == i) {
                colCount = (nTotalServices % 2 == 0) ? 2 : 1;
            }

            for (int j = 0; j < colCount; j++) {
                LinearLayout layoutColumn = new LinearLayout(this);
                layoutColumn.setId(colId);
                ImageView imgView = (ImageView) imgViewIcon[colId];
                imgView.setId(200 + colId);
                layoutColumn.addView(imgView, iconLayoutParam);
                layoutColumn.setBackgroundColor(getResources().getColor(R.color.bg_gray));
                layoutColumn.setOnClickListener(this);
                TextView textView = new TextView(this);
                textView.setId(300 + colId);
                textView.setTextColor(Color.BLACK);
                textView.setText(strServices[colId]);
                layoutColumn.addView(textView, textViewLayoutParam);
                tableRow.addView(layoutColumn, linearLayoutParams);
                for (int p = 0; p < selected.size(); p++) {
                    if (strServices[colId].equals(selected.get(p))) {
                        layoutColumn.setBackgroundDrawable(getResources().getDrawable(R.drawable.service_selector));
                        selectedList.add(colId);
                    }
                }
                colId++;
            }
            table_layout.addView(tableRow);
            DebugLog.d("selectedList  "+selectedList.toString());
            serivesel = true;
        }
    }


    private void startSubServicesActivity() {
        Intent intent = new Intent(ServiceTypesActivity.this, SubServicesActivity.class);
        intent.putExtra(SEL_SERVICE, strServices[nSelected]);
        int selId = nSelected + 1;
        intent.putExtra(SEL_SERVICE_ID, selId);
        if (roleSetting) {
            intent.putExtra("FROM_ROLE", true);
        }
        startActivity(intent);
    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        switch (dataType) {
            case GET_SERVICES:
                ServiceTypeJSONParser jsonParser = new ServiceTypeJSONParser();
                serviceData = jsonParser.parse(data);
                if (serviceData != null) {
                    getTableData(serviceData);
                    if (roleSetting) {
                        Utility.isAddRole = true;
                        HttpReqRespLayer.getInstance().getUserServices(this, this, Utility.stlUserID);
                        dotlayout.setVisibility(View.GONE);
                        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 100, 0, 0);
                        nextBtn.setLayoutParams(layoutParams);
                    } else {
                        buildTable();
                    }
                }
                break;
            case REQUEST_USER_SERVICE:
                jsonParser = new ServiceTypeJSONParser();
                LinkedHashMap<String, List<String>> serviceData1 = jsonParser.parseUserService(data);
                selected = getuserselectedServices(serviceData, serviceData1);
                selectedList = new ArrayList<Integer>();
                buildTableselected(selected);
                break;
        }
    }

    public ArrayList<String> getuserselectedServices(HashMap<String, String> map1, LinkedHashMap<String, List<String>> map2) {
        ArrayList<String> sel = new ArrayList<String>();
        for (String ch : map2.keySet()) {
            for (String ch1 : map1.keySet()) {
                if (ch.equals(ch1)) {
                    sel.add(map1.get(ch1));
                    Log.d("key", map1.get(ch1));
                }
            }
        }
        return sel;
    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        //TODO:Need to parse json and get error message

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.back_btn_id:
                handleBack();
                break;
            case R.id.back_btn_layout_id:
                handleBack();
                break;
            default:
                if (strServices != null) {
                    for (int i = 0; i < strServices.length; i++) {
                        if (id == i) {
                            nSelected = i;
                            (findViewById(i)).setBackgroundColor(getResources().getColor(R.color.green));

                            if (nSelServiceIconId.length > i) {
                                ((ImageView) findViewById(200 + i)).setImageResource(nSelServiceIconId[i]);
                            } else {
                                ((ImageView) findViewById(200 + i)).setImageResource(R.drawable.icon_default_service_selected);
                            }
                            ((TextView) findViewById(300 + i)).setTextColor(Color.WHITE);
                        } else {
                            (findViewById(i)).setBackgroundColor(getResources().getColor(R.color.bg_gray));
                            if (nServiceIconId.length > i) {
                                ((ImageView) findViewById(200 + i)).setImageResource(nServiceIconId[i]);
                            } else {
                                ((ImageView) findViewById(200 + i)).setImageResource(R.drawable.icon_default_service);
                            }
                            ((TextView) findViewById(300 + i)).setTextColor(Color.BLACK);
                        }
                        if (serivesel) {
                            for (int c = 0; c < selectedList.size(); c++) {
                                if (id == selectedList.get(c)){
                                    (findViewById(selectedList.get(c))).setSelected(true);
                                }else {
                                    (findViewById(selectedList.get(c))).setSelected(false);
                                }
                                (findViewById(selectedList.get(c))).setBackgroundDrawable(getResources().getDrawable(R.drawable.service_selector));
                            }
                        }
                    }
                }
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
