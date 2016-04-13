package com.unorganised.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.unorganised.R;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.PendingJobs;
import com.unorganised.util.ServiceTypeJSONParser;
import com.unorganised.util.Utility;


import org.json.JSONObject;

import java.util.HashMap;

public class PostJobActivity extends Activity implements OnClickListener, Constants, HttpRequestResponse {
    private String TAG = JobDetailsActivity.class.getSimpleName();
    private boolean bEdit;
    private int nJobId;
    private int nTime;
    private int selServicePos;
    private int nSelServiceId = 0;
    private int nSelSubServiceId = 0;
    private int[] totalSubServices;
    private int[] nServiceIconId = {
            R.drawable.icon_homecare,
            R.drawable.icn_repair,
            R.drawable.icn_beauty_makeup,
            R.drawable.icon_eventplaning,
            R.drawable.icn_wedding,
            R.drawable.icn_hobbies,
            R.drawable.icon_houseparty,
            R.drawable.icon_eventplaning,
            R.drawable.icon_photogrpaher,
            R.drawable.icn_dailyneeds};

    private int[] nSubServicesIconID = {
            R.drawable.icn_hobbies,
            R.drawable.icn_dailyneeds,
            R.drawable.icon_houseparty,
            R.drawable.icon_photogrpaher,
            R.drawable.icn_repair,
            R.drawable.icn_beauty_makeup,
            R.drawable.icon_homecare

    };

    private Integer[] nPaymentIconID = {
            R.drawable.cash,
            R.drawable.online
    };


    private String[] strServices;
    private String[] paymentTypes;
    private double latitude;
    private double longitude;
    private String selDate = "";
    private String selFromTime = "";
    private String selToTime = "";
    private Spinner serviceSpinner;
    private Spinner subServiceSpinner;
    private Spinner paymentTypeSpinner;
    private Button scheduleNow;
    private Button scheduleLaterBtn;
    private EditText jobDetailsEdit;
    private EditText biddingPriceEdit;
    private Dialog scheduleLaterAlert;
    private TextView selectDateText;
    private TextView fromTimeText;
    private TextView toTimeText;
    private TimePickerDialog timePickerDialog;
    private ImageView locationImage;
    private EditText locationEdt;
    private String[] strSubServices;
    private int[] nSubServicesId;
    private PendingJobs pendingJobToEdit;
    private int[] nServiceImageID;
    private int[] nSubServiceImageID;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_job_activity);
        bEdit = getIntent().getBooleanExtra(EDIT_JOB, false);
        //Category Spinner
        serviceSpinner = (Spinner) findViewById(R.id.sel_category_id);
        serviceSpinner.setDropDownWidth(450);
        //Sub Category Spinner
        subServiceSpinner = (Spinner) findViewById(R.id.sel_subcategory_id);
        subServiceSpinner.setDropDownWidth(450);
        //Select Payment spinner
        paymentTypeSpinner = (Spinner) findViewById(R.id.sel_payment_type_id);
        paymentTypeSpinner.setDropDownWidth(450);
        paymentTypes = getResources().getStringArray(R.array.payment_type);
        PaymentTypeAdapter paymentAdapter = new PaymentTypeAdapter(this, android.R.layout.simple_spinner_item, paymentTypes);
        paymentTypeSpinner.setAdapter(paymentAdapter);
        paymentTypeSpinner.setSelection(paymentAdapter.getCount());
        locationImage = (ImageView) findViewById(R.id.location_icon_id);
        locationImage.setOnClickListener(this);
        locationEdt = (EditText) findViewById(R.id.location_id);
        jobDetailsEdit = (EditText) findViewById(R.id.job_details_id);
        biddingPriceEdit = (EditText) findViewById(R.id.bidding_price_id);
        scheduleNow = (Button) findViewById(R.id.schedule_now_btn_id);
        scheduleNow.setOnClickListener(this);
        scheduleLaterBtn = (Button) findViewById(R.id.schedule_btn_id);
        scheduleLaterBtn.setOnClickListener(this);

        if (bEdit) {
            nJobId = getIntent().getIntExtra(JOB_ID, 0);
            pendingJobToEdit = getIntent().getParcelableExtra(PENDING_JOB_OBJ);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
            arrayAdapter.add(pendingJobToEdit.getServiceDes());
            serviceSpinner.setAdapter(arrayAdapter);
            serviceSpinner.setSelection(0);
            serviceSpinner.setEnabled(false);
            ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
            arrayAdapter1.add(pendingJobToEdit.getSubServiceDes());
            subServiceSpinner.setAdapter(arrayAdapter1);
            subServiceSpinner.setSelection(0);
            subServiceSpinner.setEnabled(false);
            jobDetailsEdit.setText(pendingJobToEdit.getJobDescription());
            biddingPriceEdit.setText(pendingJobToEdit.getPrice());
            scheduleNow.setText("Save");
        } else {
            //Getting services
            HttpReqRespLayer.getInstance().getServices(this, this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.schedule_now_btn_id:
                postJob();
                break;
            case R.id.schedule_btn_id:
                createScheduleDialog();
                break;
            case R.id.date_picker_id:
                int[] date = Utility.getCurrentDate();
                DatePickerDialog datePicker = new DatePickerDialog(this, datePickerListener, date[0], date[1], date[2]);
                datePicker.show();
                break;
            case R.id.ok_button_id:
                postJob();
                scheduleLaterAlert.dismiss();
                break;
            case R.id.cancel_button_id:
                scheduleLaterAlert.dismiss();
                break;
            case R.id.from_time_id:
                timePickerDialog.show();
                nTime = 0;
                break;
            case R.id.to_time_id:
                timePickerDialog.show();
                nTime = 1;
                break;
            case R.id.location_icon_id:
                Intent intent = new Intent(this, UserLocationActivity.class);
                intent.putExtra(Constants.KEY_LOCATION_ACTIVITY_LAUNCHER_TYPE, LOCATION_ACTIVITY_LAUNCHER_TYPE.POST_JOB_LOCATION);
                startActivityForResult(intent, 2);
                break;
        }
    }

    private void postJob() {
        String service = "";

        if (!bEdit) {
            selServicePos = serviceSpinner.getSelectedItemPosition();
            nSelServiceId = selServicePos + 1;
            service = strServices[selServicePos];

//            if (totalSubServices[selServicePos] != 0 && nSubServicesId.length > 0) {
//                nSelSubServiceId = nSubServicesId[subServiceSpinner.getSelectedItemPosition()];
//            }
        }

        String jobDetails = jobDetailsEdit.getText().toString();
        String location = locationEdt.getText().toString();
        String biddingAmount = biddingPriceEdit.getText().toString();
        //String scheduledOn = Utility.getFormattedTime();
        String payType = paymentTypeSpinner.getSelectedItem().toString();
        String scheduledFrom = (new StringBuilder().append(selDate).append(" ").append(selFromTime)).toString();
        String scheduledTo = (new StringBuilder().append(selDate).append(" ").append(selToTime)).toString();

        if (scheduledFrom.equals("") && scheduledTo.equals("")) {
            scheduledFrom = Utility.getFormattedTime();
            scheduledTo = scheduledFrom;
        }
        int paymentType = paymentTypeSpinner.getSelectedItemPosition();//(payType.equals(PAYMENT_TYPE.CASH)) ? PAYMENT_TYPE.CASH.ordinal() : PAYMENT_TYPE.ONLINE.ordinal();
        String address = Utility.getAddressFromLocation(this, latitude, longitude);


        //TODO: Change this later
        String jobtype = getString(R.string.one_time);
        int jobType = (jobtype.equals(getString(R.string.one_time))) ? JOB_TYPE.ONE_TIME.ordinal() : JOB_TYPE.WEEKLY.ordinal();

        if (validateFields(service, jobDetails, location, biddingAmount, payType)) {

            if (bEdit) {
                Utility.showToast(this, getResources().getString(R.string.job_edited_confirmation));
                HttpReqRespLayer.getInstance().editJob(this, this, nJobId, jobDetails, latitude, longitude, pendingJobToEdit.getServiceId(),
                        pendingJobToEdit.getSubServiceId(), biddingAmount, paymentType, jobType + 1, scheduledFrom, scheduledTo);
            } else {
                if (totalSubServices[selServicePos] != 0) {
                    nSelSubServiceId = nSubServicesId[subServiceSpinner.getSelectedItemPosition()];
                }
                HttpReqRespLayer.getInstance().postJob(this, this, jobDetails, latitude, longitude, Utility.stlUserID, nSelServiceId,
                        nSelSubServiceId, biddingAmount, paymentType, jobType + 1, scheduledFrom, scheduledTo, address, Utility.getFormattedTime());
                Utility.showToast(this, getResources().getString(R.string.job_posted_confirmation));

            }
        }
    }


    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            selDate = new StringBuilder().append(selectedDay).append("-").append(selectedMonth + 1).append("-").append(selectedYear).toString();
            selectDateText.setText(selDate);
        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            String time = Utility.updateTime(hourOfDay, minutes);
            if (nTime == 0) {
                selFromTime = hourOfDay + ":" + minutes;
                fromTimeText.setText(time);
            } else {
                selToTime = hourOfDay + ":" + minutes;
                toTimeText.setText(time);
            }
        }
    };


    private boolean validateFields(String service, String jobDetails, String location, String biddingAmount, String payType) {
        if ((strServices != null && service.equals(strServices[strServices.length - 1])) || jobDetails.equals("") ||
                location.equals("") || biddingAmount.equals("")) {
            Utility.showToast(this, getString(R.string.please_fill_all_data));
            return false;
        } else if (!bEdit && totalSubServices[selServicePos] != 0 && subServiceSpinner.getSelectedItem().toString().equals(getString(R.string.select_sub_category))) {
            Utility.showToast(this, getString(R.string.select_sub_category));
            return false;
        } else if (payType.equals(getResources().getString(R.string.select_payment_type))) {
            Utility.showToast(this, getString(R.string.please_select_the_payment_type));
            return false;
        }
        return true;
    }

    private void createScheduleDialog() {
        scheduleLaterAlert = new Dialog(this);
        scheduleLaterAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        scheduleLaterAlert.setContentView(R.layout.schedule_later_dialog);
        scheduleLaterAlert.show();
        ImageView date = (ImageView) scheduleLaterAlert.findViewById(R.id.date_picker_id);
        date.setOnClickListener(this);
        ImageView fromTime = (ImageView) scheduleLaterAlert.findViewById(R.id.from_time_id);
        fromTime.setOnClickListener(this);
        ImageView toTime = (ImageView) scheduleLaterAlert.findViewById(R.id.to_time_id);
        toTime.setOnClickListener(this);
        Button okButton = (Button) scheduleLaterAlert.findViewById(R.id.ok_button_id);
        okButton.setOnClickListener(this);
        Button cancelButton = (Button) scheduleLaterAlert.findViewById(R.id.cancel_button_id);
        cancelButton.setOnClickListener(this);
        selectDateText = (TextView) scheduleLaterAlert.findViewById(R.id.select_date_text_id);
        fromTimeText = (TextView) scheduleLaterAlert.findViewById(R.id.from_time_text_id);
        toTimeText = (TextView) scheduleLaterAlert.findViewById(R.id.to_time_text_id);
        int[] currentTime = Utility.getCurrentTime();
        timePickerDialog = new TimePickerDialog(this, timePickerListener, currentTime[0], currentTime[1], false);
    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        Log.d("Json response:::: ", data.toString());
        switch (dataType) {
            case GET_SERVICES:
                ServiceTypeJSONParser jsonParser = new ServiceTypeJSONParser();
                HashMap<String, String> serviceData = jsonParser.parse(data);
                totalSubServices = jsonParser.getTotalSubService();
                if (serviceData != null) {
                    addServiceToList(serviceData);
                }
                break;
            case GET_SUB_TYPE_SERVICES:
                ServiceTypeJSONParser jsonParser1 = new ServiceTypeJSONParser();
                HashMap<String, String> subServiceData = jsonParser1.parseSubActivity(data);

                if (subServiceData != null) {
                    addSubServiceToList(subServiceData);
                }

                break;
            case EDIT_JOB:
            case POST_JOB:
                startActivity(new Intent(this, JobProviderDashboardActivity.class));
                finish();
                break;
        }
    }


    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {

    }

    private void addServiceToList(HashMap<String, String> serviceData) {
        int index = 1;
        int totalServices = serviceData.size();
        nServiceImageID = new int[totalServices];
        strServices = new String[totalServices + 1];

        for (int i = 0; i < totalServices; i++) {
            if (nServiceIconId.length > i) {
                nServiceImageID[i] = nServiceIconId[i];
            } else {
                nServiceImageID[i] = R.drawable.icon_default_service;
            }
            strServices[i] = serviceData.get("" + index);
            index++;
        }
        strServices[totalServices] = getString(R.string.select_category);
        CategoryAdapter adapter = new CategoryAdapter(this, android.R.layout.simple_spinner_item, strServices);
        serviceSpinner.setAdapter(adapter);
        serviceSpinner.setSelection(adapter.getCount());
        serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (totalSubServices[position] != 0) {
                    HttpReqRespLayer.getInstance().getSubServices(PostJobActivity.this, PostJobActivity.this, position + 1);
                    subServiceSpinner.setVisibility(View.VISIBLE);
                } else {
                    subServiceSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addSubServiceToList(HashMap<String, String> subServiceData) {
        int totalData = subServiceData.size();
        int index = 0;
        Log.d(TAG, subServiceData.toString());
        strSubServices = new String[totalData + 1];
        nSubServicesId = new int[totalData];
        nSubServiceImageID = new int[totalData];

        for (String key : subServiceData.keySet()) {
            strSubServices[index] = subServiceData.get(key);
            nSubServicesId[index] = Integer.parseInt(key);

            if (nSubServicesIconID.length > index) {
                nSubServiceImageID[index] = nSubServicesIconID[index];
            } else {
                nSubServiceImageID[index] = R.drawable.icon_default_service;
            }
            index++;
        }
        strSubServices[totalData] = getString(R.string.select_sub_category);
        SubCategoryAdapter adapter1 = new SubCategoryAdapter(this, android.R.layout.simple_spinner_item, strSubServices);
        subServiceSpinner.setAdapter(adapter1);
        subServiceSpinner.setSelection(adapter1.getCount());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    public class CategoryAdapter extends ArrayAdapter<String> {

        public CategoryAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public int getCount() {
            // don't display last item. It is used as hint.
            int count = super.getCount();
            return count > 0 ? count - 1 : count;
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.job_category_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.category_id);
            label.setText(strServices[position]);
            ImageView icon = (ImageView) row.findViewById(R.id.icon);
            icon.setImageResource(nServiceImageID[position]);
            return row;
        }
    }

    public class SubCategoryAdapter extends ArrayAdapter<String> {

        public SubCategoryAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public int getCount() {
            // don't display last item. It is used as hint.
            int count = super.getCount();
            return count > 0 ? count - 1 : count;
        }


        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.job_category_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.category_id);
            label.setText(strSubServices[position]);
            ImageView icon = (ImageView) row.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.icon_default_service);
            return row;
        }
    }

    public class PaymentTypeAdapter extends ArrayAdapter<String> {

        public PaymentTypeAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public int getCount() {
            // don't display last item. It is used as hint.
            int count = super.getCount();
            return count > 0 ? count - 1 : count;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.job_category_row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.category_id);
            label.setText(paymentTypes[position]);
            ImageView icon = (ImageView) row.findViewById(R.id.icon);
            icon.setImageResource(nPaymentIconID[position]);
            return row;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == 2) {
                String locationName = data.getStringExtra(KEY_LOCATION_NAME);
                longitude = data.getDoubleExtra(KEY_LNG, 0);
                latitude = data.getDoubleExtra(KEY_LAT, 0);
                Log.d(TAG, "locationName:" + locationName);
                Log.d(TAG, "lng:" + longitude);
                Log.d(TAG, "lat:" + latitude);
                //TODO:We can set address also,but sometime maps is giving only house number,so better to display only lat,lng.
                locationEdt.setText(locationName);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}


