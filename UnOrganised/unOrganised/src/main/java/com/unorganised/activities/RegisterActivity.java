package com.unorganised.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.unorganised.R;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.ExitConfirmDialogListener;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;
import com.unorganised.views.ExitConfirmationDialog;

public class RegisterActivity extends FragmentActivity implements OnClickListener, Constants, HttpRequestResponse, ExitConfirmDialogListener {

    /*
         * User ID received along with OTP
         */
    private byte[] bytPhoto;
    private byte[] bytIDProof;
    private byte[] bytCheque;
    private byte[] bytDocument;
    private ViewFlipper viewFlipper;
    /*
     * Register screen 1/5
     */
    private EditText mobileNoEditText;
    private EditText fullnameEditText;
    private EditText emailEditText;
    private EditText adharNumberEditText;
    private EditText passwordEditText;
    private EditText reTypePasswordEditText;
    /*
     * Register screen 2/5
     */
    private EditText txtMobile;
    private TextView txtHelloName;
    private EditText OTPEditText;
    /*
     * Register screen 3/5
     */
    private boolean bProviderSelect;
    private boolean bSeekerSelect;
    private LinearLayout seekerLayout;
    private LinearLayout providerLayout;
    private ImageView seekerIcon;
    private ImageView providerIcon;
    private TextView seekerText;
    private TextView providerText;
    private View seekerDiv;
    private View providerDiv;
    private EditText refCodeEditText;
    /*
     * Register screen 4/5
     */
    private int[] nDate = new int[3];
    private int[] nDocListID;
    private String[] strBankList;
    private String[] stStrDocList;
    private Spinner selectBankSpinner;
    private Spinner selectDocSpinner;
    private Button uploadPhotoLayout;
    Button uploadIDProofLayout;
    Button uploadChequeLayout;
    private Button uploadDocLayout;
    private EditText accntNumberEditText;
    private EditText reEnterAccntNumberText;
    private EditText ISFCCodeEditText;
    private EditText ageText;
    private ImageView datePickerImg;

    private TextView registrationStepTitle;
    private final int PHOTO_CHOOSER = 1;
    private final int ID_PROOF_CHOOSING = 2;
    private final int CANCELLED_CHEQUE = 3;
    private final int DOCUMENT_CHOOSER = 4;
    private int nUserStatus;
    private long nUserID;
    private RegisterActivity activity;
    private UnOrgSharedPreferences unOrgSharedPref;
    private GoogleCloudMessaging gcm;
    private String regid = null;
    private AsyncTask getRegIdAsynTask;
    private ProgressDialog progressDialog;
    private int userType;
    private boolean userSettings;

    private enum RegStep {
        STEP0, STEP1, STEP2, STEP3, STEP4, STEP5
    }

    private RegStep currentRegStep = RegStep.STEP0;

    private enum ImageUploadType {
        REQUEST_CAMERA,
        SELECT_FILE
    }

    private ImageUploadType imageUploadType = ImageUploadType.REQUEST_CAMERA;

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        activity = this;
        unOrgSharedPref = UnOrgSharedPreferences.getInstance(activity);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
        registrationStepTitle = (TextView) findViewById(R.id.title_view_id);
        Intent intent = getIntent();
        userSettings = intent.getBooleanExtra(USER_SETTINGS, false);
        nUserStatus = intent.getIntExtra(USER_STATUS_TAG, 0);
        Button nextBtn = (Button) findViewById(R.id.next_btn_id);
        nextBtn.setOnClickListener(this);
        mobileNoEditText = (EditText) findViewById(R.id.phone_number);
        fullnameEditText = (EditText) findViewById(R.id.full_name);
        emailEditText = (EditText) findViewById(R.id.email_id);
        passwordEditText = (EditText) findViewById(R.id.password_id);
        reTypePasswordEditText = (EditText) findViewById(R.id.retype_password_id);
        Button verifyOTPBtn = (Button) findViewById(R.id.btn_verifyOTP);
        verifyOTPBtn.setOnClickListener(this);
        OTPEditText = (EditText) findViewById(R.id.text_verifyOTP);
        TextView txtSendAgain = (TextView) findViewById(R.id.txtSend_again);
        txtSendAgain.setOnClickListener(this);
        txtMobile = (EditText) findViewById(R.id.txt_MobileNo);
        txtHelloName = (TextView) findViewById(R.id.txt_hello_id);
        Button userTypeNextBtn = (Button) findViewById(R.id.usertype_next_btn_id);
        userTypeNextBtn.setOnClickListener(this);
        providerLayout = (LinearLayout) findViewById(R.id.layout_provider);
        providerLayout.setOnClickListener(this);
        seekerLayout = (LinearLayout) findViewById(R.id.layout_seeker);
        seekerLayout.setOnClickListener(this);
        seekerIcon = (ImageView) findViewById(R.id.seeker_icon_id);
        seekerText = (TextView) findViewById(R.id.seeker_text_id);
        seekerDiv = findViewById(R.id.seeker_div_id);
        providerIcon = (ImageView) findViewById(R.id.provider_icon_id);
        providerText = (TextView) findViewById(R.id.provider_text_id);
        providerDiv = findViewById(R.id.provider_div_id);
        refCodeEditText = (EditText) findViewById(R.id.referral_code_id);
        selectBankSpinner = (Spinner) findViewById(R.id.sel_bank_id);
        selectDocSpinner = (Spinner) findViewById(R.id.sel_document_id);
        accntNumberEditText = (EditText) findViewById(R.id.acnt_no_id);
        reEnterAccntNumberText = (EditText) findViewById(R.id.re_enter_acnt_id);
        ISFCCodeEditText = (EditText) findViewById(R.id.sel_isfc_code_id);
        adharNumberEditText = (EditText) findViewById(R.id.adhar_no_id);
        ageText = (EditText) findViewById(R.id.age_text_id);
        datePickerImg = (ImageView) findViewById(R.id.date_picker_id);
        datePickerImg.setOnClickListener(this);
        uploadPhotoLayout = (Button) findViewById(R.id.upload_photo_id);
        uploadPhotoLayout.setOnClickListener(this);
        uploadIDProofLayout = (Button) findViewById(R.id.upload_idproof_id);
        uploadIDProofLayout.setOnClickListener(this);
        uploadChequeLayout = (Button) findViewById(R.id.cancelled_cheque_id);
        uploadChequeLayout.setOnClickListener(this);
        uploadDocLayout = (Button) findViewById(R.id.upload_doc_id);
        uploadDocLayout.setOnClickListener(this);
        uploadDocLayout.setVisibility(View.GONE);
        Button uploadBankDetailsBtn = (Button) findViewById(R.id.upload_user_details_btn);
        uploadBankDetailsBtn.setOnClickListener(this);
        //Button editMobileNoBtn = (Button)findViewById(R.id.btn_EditMobile);
        //editMobileNoBtn.setOnClickListener(this);
        // EditText password = (EditText) findViewById(R.id.register_password_text);
        passwordEditText.setTypeface(Typeface.DEFAULT);
        reTypePasswordEditText.setTypeface(Typeface.DEFAULT);

        txtMobile.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) || actionId == EditorInfo.IME_ACTION_DONE) {
                    String phoneNum = txtMobile.getText().toString().trim();

                    if (phoneNum.length() < 10) {
                        Utility.showToast(activity, getString(R.string.enter_valid_mob_num));
                    } else {
                        HttpReqRespLayer.getInstance().resetMobileNum(activity, activity, Utility.stlUserID, Long.parseLong(phoneNum));
                    }
                }
                return false;
            }
        });

        if (userSettings) { //Add new user type from settings
            Utility.isUpdateUser = true;
            HttpReqRespLayer.getInstance().getBanks(this, this);
        } else if (nUserStatus == US_NOT_CREATED) {
            setRegTitle(1);
            currentRegStep = RegStep.STEP1;
            //Sets first register screen
            viewFlipper.setDisplayedChild(1);
        } else if (nUserStatus == US_CREATED) {
            setUserTypeScreen();
        } else if (nUserStatus == US_USER_TYPE_SELECTED) {
            if (Utility.stnUserType == 2) {
                startActivity(new Intent(this, UserLocationActivity.class));
            } else {
                HttpReqRespLayer.getInstance().getBanks(this, this);
            }
            unOrgSharedPref.put(SP_USER_TYPE, Utility.stnUserType);
        } else {
            Utility.otpcheck = false;
            Utility.Isusertypescreen = false;
            Utility.Isbankscreen = false;
            setRegTitle(0);
            currentRegStep = RegStep.STEP0;
            //Sets first register screen
            viewFlipper.setDisplayedChild(0);
        }
    }

    /**
     * Sets registration step title to the screen
     *
     * @param screenIndex Registration step index
     */
    private void setRegTitle(int screenIndex) {
        switch (screenIndex) {
            case 0:
                registrationStepTitle.setText(R.string.register1);
                TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                String number = tm.getLine1Number();
                mobileNoEditText.setText(number);
                break;
            case 1:
                registrationStepTitle.setText(R.string.register2);
                txtMobile.append(mobileNoEditText.getText().toString());
                break;
            case 2:
                DebugLog.d("Register 3");
                registrationStepTitle.setText(R.string.register3);
                String helloText = getString(R.string.hello);
                txtHelloName.setText(String.format("%s %s", helloText, fullnameEditText.getText().toString()));
                break;
            case 3:
                if (Utility.stnUserType == 1 || Utility.stnUserType == 3) {
                    registrationStepTitle.setText(getResources().getString(R.string.job_seeker_text));
                } else {
                    registrationStepTitle.setText(getResources().getString(R.string.job_provider_text));
                }
                //registrationStepTitle.setText(R.string.register4);
                addBankList();
                addDocumentList();
                break;
            case 4:
                if (Utility.stnUserType == 1 || Utility.stnUserType == 3) {
                    registrationStepTitle.setText(getResources().getString(R.string.job_seeker_text));
                } else {
                    registrationStepTitle.setText(getResources().getString(R.string.job_provider_text));
                }
                //registrationStepTitle.setText(R.string.register5);
                break;
            default:
                break;
        }
    }

    /**
     * Adds the given bank list to Spinner
     */
    private void addBankList() {
        List<String> list = new ArrayList<String>();
        int length = strBankList.length;

        if (length > 0) {
            strBankList[length - 1] = getString(R.string.select_bank_prompt);
        }

        Collections.addAll(list, strBankList);
       /* ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list) {
            @Override
            public int getCount() {
                int count = super.getCount();
                return count > 0 ? count - 1 : count;
            }
        };*/
        CustomizedSpinnerAdapter adapter1 = new CustomizedSpinnerAdapter(
                this, android.R.layout.simple_spinner_item,
                list);
        selectBankSpinner.setAdapter(adapter1);
        selectBankSpinner.setSelection(length - 1);
    }

    /**
     * Adds the given Document list to Spinner
     */
    private void addDocumentList() {
        List<String> list = new ArrayList<String>();
        int length = stStrDocList.length;

        if (length > 0) {
            stStrDocList[length - 1] = getString(R.string.select_doc_prompt);
            uploadDocLayout.setVisibility(View.VISIBLE);
        }

        list.addAll(Arrays.asList(stStrDocList).subList(0, length));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list) {
            @Override
            public int getCount() {
                int count = super.getCount();
                return count > 0 ? count - 1 : count;
            }
        };
        selectDocSpinner.setAdapter(dataAdapter);
        selectDocSpinner.setSelection(length - 1);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_btn_id:
                String userName = fullnameEditText.getText().toString();
                String mailId = emailEditText.getText().toString();
                String pwd = passwordEditText.getText().toString();
                String reTypePwd = reTypePasswordEditText.getText().toString();
                String phone = mobileNoEditText.getText().toString();

                if (validateScreen1(userName, mailId, pwd, reTypePwd, phone)) {
                    long phoneNumber = Long.parseLong(phone);
                    // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
                    if (checkPlayServices()) {

                        gcm = GoogleCloudMessaging.getInstance(this);
                        regid = getRegistrationId(getApplicationContext());
                        Log.d(TAG, "RegID:" + regid);

                        if (regid.isEmpty()) {
                            registerInBackground();
                        } else {
                            HttpReqRespLayer.getInstance().createUser(this, this, userName, mailId, pwd, phoneNumber, regid);
                        }

                    } else {
                        Log.i(TAG, "No valid Google Play Services APK found.");
                    }

                }
                break;
            case R.id.btn_verifyOTP:
                String OTP = OTPEditText.getText().toString().trim();

                Utility.otpcheck = false;
                if (TextUtils.isEmpty(OTP)) {
                    Utility.showToast(this, getString(R.string.enter_otp_error));
                } else {
                    long otp = Long.parseLong(OTP);
                    HttpReqRespLayer.getInstance().validateOTP(this, this, Utility.stlUserID, otp);
                }
                break;
            case R.id.usertype_next_btn_id:
                if ((!bProviderSelect) && (!bSeekerSelect)) {
                    Utility.showToast(this, getString(R.string.select_user_type_error));
                } else {
//				int userType = 0;
                    if (bSeekerSelect) {
                        //Seeker
                        Utility.stnUserType = 1;
                    }
                    if (bProviderSelect) {
                        //Provider
                        Utility.stnUserType = 2;
                    }
                    if (bSeekerSelect && bProviderSelect) {
                        //Both
                        Utility.stnUserType = 3;
                    }
                    //TODO: add refCodeEditText value to request
                    HttpReqRespLayer.getInstance().updateUserType(this, this, Utility.stlUserID, Utility.stnUserType, refCodeEditText.getText().toString());
                }
                break;
            case R.id.upload_user_details_btn:
                String accntNumber = accntNumberEditText.getText().toString();
                String reTypeAccntNumber = reEnterAccntNumberText.getText().toString();
                String ISFCCode = ISFCCodeEditText.getText().toString();
                String selectedBank = selectBankSpinner.getSelectedItem().toString();
                String adharNumber = adharNumberEditText.getText().toString();
                ageText.setText(ageText.getText().toString());
                String strAge = ageText.getText().toString();

                Log.d("age=", strAge);
                if (validateBankEntries(accntNumber, reTypeAccntNumber, ISFCCode, adharNumber, strAge)) {
                    String strImgPhoto = Base64.encodeToString(bytPhoto, Base64.NO_WRAP);
                    String strImgIdProof = Base64.encodeToString(bytIDProof, Base64.NO_WRAP);
                    String strImgCheque = Base64.encodeToString(bytCheque, Base64.NO_WRAP);
                    String strImgDocument = "";

                    if (bytDocument != null) {
                        strImgDocument = Base64.encodeToString(bytDocument, Base64.NO_WRAP);
                    }
                    HttpReqRespLayer.getInstance().updateUserProfile(this, this, Utility.stlUserID, accntNumber, selectedBank, ISFCCode, 1,
                            strImgPhoto, strImgIdProof, strImgCheque, adharNumber, nDocListID[selectDocSpinner.getSelectedItemPosition()],
                            strImgDocument, String.format("%s 00:00", strAge));
                }
                break;
            case R.id.txtSend_again:
                HttpReqRespLayer.getInstance().resendOTP(this, this, Utility.stlUserID);
                break;
            case R.id.upload_photo_id:
                selectImage(PHOTO_CHOOSER);
                break;
            case R.id.upload_idproof_id:
                selectImage(ID_PROOF_CHOOSING);
                break;
            case R.id.cancelled_cheque_id:
                selectImage(CANCELLED_CHEQUE);
                break;
            case R.id.upload_doc_id:
                selectImage(DOCUMENT_CHOOSER);
                break;
            case R.id.layout_provider:

                bProviderSelect = !bProviderSelect;

                if (bProviderSelect) {
                    providerLayout.setBackgroundColor(getResources().getColor(R.color.green));
                    providerIcon.setImageResource(R.drawable.icn_provider_onselect);
                    providerText.setTextColor(Color.WHITE);
                    providerDiv.setBackgroundColor(Color.WHITE);
                } else {
                    providerLayout.setBackgroundColor(getResources().getColor(R.color.bg_gray));
                    providerIcon.setImageResource(R.drawable.icn_provider);
                    providerText.setTextColor(Color.BLACK);
                    providerDiv.setBackgroundColor(Color.GRAY);
                }
                break;
            case R.id.layout_seeker:
                bSeekerSelect = !bSeekerSelect;

                if (bSeekerSelect) {
                    seekerLayout.setBackgroundColor(getResources().getColor(R.color.green));
                    seekerIcon.setImageResource(R.drawable.icn_seeker_onselect);
                    seekerText.setTextColor(Color.WHITE);
                    seekerDiv.setBackgroundColor(Color.WHITE);
                } else {
                    seekerLayout.setBackgroundColor(getResources().getColor(R.color.bg_gray));
                    seekerIcon.setImageResource(R.drawable.icn_seeker);
                    seekerText.setTextColor(Color.BLACK);
                    seekerDiv.setBackgroundColor(Color.GRAY);
                }
                break;
        /*case R.id.btn_EditMobile:
                txtMobile.setEnabled(true);
				txtMobile.setFocusable(true);
                break;8*/
            case R.id.back_btn_id:
                handleBack();
                break;
            case R.id.back_btn_layout_id:
                handleBack();
                break;
            case R.id.date_picker_id:
                handleDatePicker();
                break;
            default:
                break;
        }
    }


    private void handleDatePicker() {
        int[] date = Utility.getCurrentDate();
        DatePickerDialog datePicker = new DatePickerDialog(this, datePickerListener, date[0], date[1], date[2]);
        datePicker.show();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            nDate[0] = selectedYear;
            nDate[1] = selectedMonth + 1;
            nDate[2] = selectedDay;
            ageText.setText(Utility.getFormattedDate(selectedYear, selectedMonth + 1, selectedDay));

        }
    };

    private boolean validateBankEntries(String accntNumber, String reTypeAccntNumber, String iSFCCode, String adharNumber, String age) {
        if (accntNumber.equals("") || reTypeAccntNumber.equals("") || iSFCCode.equals("") || adharNumber.equals("")) {
            Utility.showToast(this, getString(R.string.fill_details_error));
            return false;
        }

        if (!accntNumber.equals(reTypeAccntNumber)) {
            Utility.showToast(this, getString(R.string.account_no_mismatch_error));
            accntNumberEditText.requestFocus();
            return false;
        }
        if (adharNumber.length() < 12) {
            Utility.showToast(this, getString(R.string.invalid_aadhar_error));
            adharNumberEditText.requestFocus();
            return false;
        }

        if (selectBankSpinner.getSelectedItem().equals(getString(R.string.select_bank_prompt))) {
            Utility.showToast(this, getString(R.string.select_your_bank_error));
            return false;
        }

        if (age.equals("")) {
            Utility.showToast(this, getString(R.string.enter_your_age_error));
            return false;
        }

        int[] cDate = Utility.getCurrentDate();
        String y = age.substring(0, age.indexOf("-"));
        int year = Integer.parseInt(y);

        if (year == 0 || cDate[0] < year) {
            Utility.showToast(this, getString(R.string.age_invalid_error));
            return false;
        }
        if (cDate[0] < year + 15) {// Age validation age > 15
            Utility.showToast(this, getString(R.string.age_validation_error));
            return false;
        }
          /*  if (nDate[0] == 0 || cDate[0] < nDate[0]) {
                Utility.showToast(this, getString(R.string.age_invalid_error));
                return false;
            }

            if (cDate[0] < nDate[0] + 15) {// Age validation age > 15
                Utility.showToast(this, getString(R.string.age_validation_error));
                return false;
            }*/


        if (bytPhoto == null || (bytPhoto.length == 0)) {
            Utility.showToast(this, getString(R.string.choose_profile_picture_error));
            return false;
        }

        if (bytIDProof == null || (bytIDProof.length == 0)) {
            Utility.showToast(this, getString(R.string.choose_id_proof_error));
            return false;
        }

        if (bytCheque == null || (bytCheque.length == 0)) {
            Utility.showToast(this, getString(R.string.choose_cancelled_check_error));
            return false;
        }

        if (bytDocument != null && selectDocSpinner.getSelectedItem().equals(getString(R.string.select_doc_prompt))) {
            Utility.showToast(this, getString(R.string.select_doc_type_error));
            return false;
        }
        return true;
    }


    /**
     * Validate the first registration screen
     *
     * @param username    Full name
     * @param emailId     User's mail id
     * @param password    Password entered
     * @param reTypePwd   Re typed password
     * @param phoneNumber Mobile number
     * @return true if all validations are success.
     */
    private boolean validateScreen1(String username, String emailId, String password, String reTypePwd, String phoneNumber) {
        if (username.equals("")) {
            Utility.showToast(this, getString(R.string.enter_full_name_error));
            fullnameEditText.requestFocus();
            return false;
        } else if (emailId.equals("") || !Utility.validate(emailId)) {
            Utility.showToast(this, getString(R.string.enter_valid_mail_id_error));
            emailEditText.requestFocus();
            return false;
        } else if (phoneNumber.equals("") || phoneNumber.length() < 10 || phoneNumber.contains(" ")) {
            Utility.showToast(this, getString(R.string.enter_valid_mob_num));
            mobileNoEditText.requestFocus();
            return false;
        } else if (phoneNumber.startsWith("91") || phoneNumber.startsWith("+91")) {
            Utility.showToast(this, getString(R.string.remove_country_code_error));
            mobileNoEditText.requestFocus();
            return false;
        } else if (password.equals("")) {
            Utility.showToast(this, getString(R.string.enter_password_error));
            passwordEditText.requestFocus();
            return false;
        } else if (!password.equals(reTypePwd)) {
            Utility.showToast(this, getString(R.string.password_missmatch_error));
            reTypePasswordEditText.requestFocus();
            return false;
        }
        return true;
    }

    private void uploadFile(int i) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, i);
    }

    /**
     * Calculates destination rectangle for scaling bitmap
     *
     * @param srcWidth     Width of source image
     * @param srcHeight    Height of source image
     * @param dstWidth     Width of destination area
     * @param dstHeight    Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal destination rectangle
     */
    public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                        ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return new Rect(0, 0, dstWidth, (int) (dstWidth / srcAspect));
            } else {
                return new Rect(0, 0, (int) (dstHeight * srcAspect), dstHeight);
            }
        } else {
            return new Rect(0, 0, dstWidth, dstHeight);
        }
    }

    /**
     * Calculates source rectangle for scaling bitmap
     *
     * @param srcWidth     Width of source image
     * @param srcHeight    Height of source image
     * @param dstWidth     Width of destination area
     * @param dstHeight    Height of destination area
     * @param scalingLogic Logic to use to avoid image stretching
     * @return Optimal source rectangle
     */
    public static Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                        ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.CROP) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                final int srcRectWidth = (int) (srcHeight * dstAspect);
                final int srcRectLeft = (srcWidth - srcRectWidth) / 2;
                return new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                final int srcRectHeight = (int) (srcWidth / dstAspect);
                final int scrRectTop = (int) (srcHeight - srcRectHeight) / 2;
                return new Rect(0, scrRectTop, srcWidth, scrRectTop + srcRectHeight);
            }
        } else {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
    }

    /**
     * ScalingLogic defines how scaling should be carried out if source and
     * destination image has different aspect ratio.
     * <p/>
     * CROP: Scales the image the minimum amount while making sure that at least
     * one of the two dimensions fit inside the requested destination area.
     * Parts of the source image will be cropped to realize this.
     * <p/>
     * FIT: Scales the image the minimum amount while making sure both
     * dimensions fit inside the requested destination area. The resulting
     * destination dimensions might be adjusted to a smaller size than
     * requested.
     */
    public static enum ScalingLogic {
        CROP, FIT
    }

    /**
     * Utility function for creating a scaled version of an existing bitmap
     *
     * @param unscaledBitmap Bitmap to scale
     * @param dstWidth       Wanted width of destination bitmap
     * @param dstHeight      Wanted height of destination bitmap
     * @param scalingLogic   Logic to use to avoid image stretching
     * @return New scaled bitmap object
     */
    public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight,
                                            ScalingLogic scalingLogic) {
        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(),
                dstWidth, dstHeight, scalingLogic);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == RESULT_OK && data != null) {
                Bitmap bitmap = null;
                if (imageUploadType == ImageUploadType.REQUEST_CAMERA) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                } else if (imageUploadType == ImageUploadType.SELECT_FILE) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                    if (cursor != null) {
                        cursor.moveToFirst();
                    }
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imagePath = cursor.getString(columnIndex);
                    cursor.close();
                    File imagefile = new File(imagePath);
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(imagefile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap = BitmapFactory.decodeStream(fis);
                    byte[] before = Utility.getBytesFromBitmap(bitmap);
                    Log.d("byte", Integer.toString(before.length));
                    if (before.length > MAX_SIZE) {
                        int width = bitmap.getWidth() / 2;
                        int height = bitmap.getHeight() / 2;

                        if (before.length > MAX_OVER_SIZE) {
                            width = width / 2;
                            height = height / 2;
                        }
                        bitmap = createScaledBitmap(bitmap, width, height, ScalingLogic.FIT);
                    }
                }


                switch (requestCode) {
                    case PHOTO_CHOOSER:
                        bytPhoto = Utility.getBytesFromBitmap(bitmap);
                        Utility.showToast(this, getString(R.string.profile_picture_uploaded_error));
                        ImageView profileImage = (ImageView) findViewById(R.id.profile_img_id);
                        profileImage.setVisibility(View.VISIBLE);
                        uploadPhotoLayout.setText(getString(R.string.profile_uploaded));
                        Bitmap profileBitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
                        Utility.profileImg = profileBitmap;
                        profileImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));
                        break;
                    case ID_PROOF_CHOOSING:
                        bytIDProof = Utility.getBytesFromBitmap(bitmap);
                        Utility.showToast(this, getString(R.string.id_proof_uploaded_error));
                        ImageView idProofImg = (ImageView) findViewById(R.id.id_proof_img_id);
                        idProofImg.setVisibility(View.VISIBLE);
                        uploadIDProofLayout.setText(getString(R.string.id_uploaded));
                        idProofImg.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));

                        break;
                    case CANCELLED_CHEQUE:
                        bytCheque = Utility.getBytesFromBitmap(bitmap);
                        Utility.showToast(this, getString(R.string.cancel_cheque_uploaded_error));
                        ImageView cancelledChequeImg = (ImageView) findViewById(R.id.cancelled_cheque_img_id);
                        cancelledChequeImg.setVisibility(View.VISIBLE);
                        uploadChequeLayout.setText(getString(R.string.cheque_uploaded));
                        cancelledChequeImg.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));
                        break;
                    case DOCUMENT_CHOOSER:
                        bytDocument = Utility.getBytesFromBitmap(bitmap);
                        Utility.showToast(this, getString(R.string.doc_uploaded_error));
                        ImageView documentImgId = (ImageView) findViewById(R.id.doc_img_id);
                        documentImgId.setVisibility(View.VISIBLE);
                        uploadDocLayout.setText(getString(R.string.doc_uploaded));
                        documentImgId.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));
                        break;
                }

            } else {
                Utility.showToast(this, getString(R.string.u_have_nt_pic_the_image));
            }
        } catch (Exception e) {
            Utility.showToast(this, getString(R.string.went_wrong));
            e.printStackTrace();
        }
    }

    private void startServiceActivity() {
        Intent intent = new Intent(this, ServiceTypesActivity.class);
        startActivity(intent);
    }

    public class CustomizedSpinnerAdapter extends ArrayAdapter<String> {

        List<String> data = new ArrayList<String>();

        public CustomizedSpinnerAdapter(Activity context, int resource, List<String> data2) {
            super(context, resource, data2);
            this.data = data2;
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
            View row = inflater.inflate(R.layout.spinner_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.category_id);
            label.setText(data.get(position));
            return row;
        }

    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        Log.d(TAG, "onSuccess:" + data.toString());
        switch (dataType) {
            case CREATE_USER:
                setRegTitle(1);
                viewFlipper.setDisplayedChild(1);
                HashMap<String, String> resp = parseGetOTP(data);
                nUserID = Long.parseLong(resp.get("userid"));
                Utility.stlUserID = nUserID;
//                String otp = resp.get("otp");
//                OTPEditText.setText(otp);
                currentRegStep = RegStep.STEP1;
                Utility.stStrUserName = fullnameEditText.getText().toString();
                unOrgSharedPref.put(USER_NAME, fullnameEditText.getText().toString());
                break;
            case VALIDATE_OTP:
                DebugLog.d("Register Validate OTP");
                HttpReqRespLayer.getInstance().updateUserStatus(this, this, Utility.stlUserID, US_CREATED);
                setUserTypeScreen();
                break;
            case RE_SEND_OTP: {
//				String newOtp = parseOTP(data);
//				OTPEditText.setText(newOtp);
                Utility.showToast(this, getString(R.string.resend_otp_text_error));
                break;
            }
            case RESET_MOB_NUM: {
//				String newOtp = parseOTP(data);
//				OTPEditText.setText(newOtp);
                Utility.showToast(this, getString(R.string.resend_otp_text_error));
                break;
            }

            case GET_BANKS:
                if (fillBankList(data)) {
                    HttpReqRespLayer.getInstance().getDocs(this, this);
                }
                break;
            case GET_CERTIFICATES:
                if (fillDocumentList(data)) {
                    setRegTitle(3);
                    viewFlipper.setDisplayedChild(3);
                    currentRegStep = RegStep.STEP3;
                }
                break;
            case UPDATE_PROFILE:
                Utility.Isbankscreen = false;
                startServiceActivity();
                break;
            case UPDATE_USER_TYPE:
                HttpReqRespLayer.getInstance().updateUserStatus(this, this, Utility.stlUserID, US_USER_TYPE_SELECTED);
                setBankDetailScreen();
                break;
            default:
                break;
        }
    }

    private void setBankDetailScreen() {
        Utility.Isusertypescreen = false;
        if ((bSeekerSelect && bProviderSelect)) {
            //Seeker or both types
            HttpReqRespLayer.getInstance().getBanks(this, this);
            unOrgSharedPref.put(SP_USER_TYPE, 3);
        } else if (bSeekerSelect) {
            //Seeker or both types
            HttpReqRespLayer.getInstance().getBanks(this, this);
            unOrgSharedPref.put(SP_USER_TYPE, 1);
        } else if (bProviderSelect) {
            //Provider
            Intent intent = new Intent(this, UserLocationActivity.class);
            startActivity(intent);
            unOrgSharedPref.put(SP_USER_TYPE, 2);
        }
    }

    private void setUserTypeScreen() {
        setRegTitle(2);
        viewFlipper.setDisplayedChild(2);
        unOrgSharedPref.put(SP_MOB_NUMBER, txtMobile.getText().toString());
        unOrgSharedPref.put(SP_USER_ID_KEY, Utility.stlUserID);
        currentRegStep = RegStep.STEP2;
    }

    private boolean fillBankList(JSONObject data) {
        try {
            JSONArray jsonArray = data.optJSONArray(RESULT_TAG);
            strBankList = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                strBankList[i] = jsonObject.getString(BANK_NAME_TAG);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean fillDocumentList(JSONObject data) {
        try {
            JSONArray jsonArray = data.optJSONArray(RESULT_TAG);
            stStrDocList = new String[jsonArray.length() + 1];
            nDocListID = new int[jsonArray.length() + 1];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                stStrDocList[i] = jsonObject.getString(CERTIFICATE_NAME_TAG);
                nDocListID[i] = jsonObject.getInt(CERTIFICATE_ID_TAG);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }


    private String parseOTP(JSONObject data) {
        String otp = null;
        try {
            JSONObject jsonObject = new JSONObject(data.getString(RESULT_TAG));
            otp = jsonObject.getString("otp");
            Log.d("OTP ", otp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return otp;
    }


    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        Log.d("Failed", data.toString());

        try {
            String text = data.getString(Constants.EXCEPTION_MSG);
            Utility.showToast(this, text);

            if (text.startsWith(getString(R.string.mobile_number))) {
                mobileNoEditText.requestFocus();
            } else if (text.startsWith(getString(R.string.email_id_text))) {
                emailEditText.requestFocus();
            } else if (text.endsWith(getString(R.string.adhar_no))) {
                adharNumberEditText.requestFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private HashMap<String, String> parseGetOTP(JSONObject json) {
        HashMap<String, String> resp = new HashMap<String, String>();
        try {
            String result = json.getString(RESULT_TAG);
            JSONObject resultjson = new JSONObject(result);
            resp.put("userid", resultjson.getString("userid"));
            resp.put("otp", resultjson.getString("otp"));

        } catch (Exception e) {
            Log.e(TAG, "Exception:" + e);
        }
        return resp;
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
                    sendRegistrationIdToBackend();
                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.
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
                    Utility.showToast(activity, getString(R.string.gcm_reg_un_successful));
                } else {
                    String userName = fullnameEditText.getText().toString();
                    String mailId = emailEditText.getText().toString();
                    String pwd = passwordEditText.getText().toString();
                    String phone = mobileNoEditText.getText().toString();
                    long phoneNumber = Long.parseLong(phone);
                    HttpReqRespLayer.getInstance().createUser(activity, activity, userName, mailId, pwd, phoneNumber, regid);
                }
            }
        }.execute(null, null, null);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getRegIdAsynTask != null && getRegIdAsynTask.getStatus() == AsyncTask.Status.RUNNING) {
            getRegIdAsynTask.cancel(true);
        }
        dismissProgress();
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

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.registering_on_gcm));
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void handleBack() {
        switch (currentRegStep) {
            case STEP0:
                finish();
                break;
            case STEP1:
                setRegTitle(0);
                viewFlipper.setDisplayedChild(0);
                currentRegStep = RegStep.STEP0;
                break;
            case STEP2:
                setRegTitle(1);
                viewFlipper.setDisplayedChild(1);
                currentRegStep = RegStep.STEP1;
                break;
            case STEP3:
                setRegTitle(2);
                viewFlipper.setDisplayedChild(2);
                currentRegStep = RegStep.STEP2;
                break;
            case STEP4:
                setRegTitle(3);
                viewFlipper.setDisplayedChild(3);
                currentRegStep = RegStep.STEP3;
                break;
            case STEP5:
                setRegTitle(4);
                viewFlipper.setDisplayedChild(4);
                break;
            default:
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (userSettings) {
            ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(this, RegisterActivity.this);
            exitConfirm.show();

        } else if (Utility.otpcheck) {
            currentRegStep = RegStep.STEP0;
            viewFlipper.setDisplayedChild(0);
            //checkOtp=null;
            //handleBack();
            Utility.otpcheck = false;
        } else if (Utility.Isusertypescreen) {
            currentRegStep = RegStep.STEP1;
            viewFlipper.setDisplayedChild(1);
            Utility.Isusertypescreen = false;
        } else if (Utility.Isbankscreen) {
            currentRegStep = RegStep.STEP2;
            viewFlipper.setDisplayedChild(2);
            Utility.Isbankscreen = false;
        } else {
            handleBack();
        }
    }


    @Override
    public void onExitConfirmed() {
//        moveTaskToBack(true);
        Intent intent = new Intent(this,
                ExitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NO_ANIMATION
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        startActivity(intent);
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        Utility.otpcheck = false;
        Utility.Isusertypescreen = false;
        Utility.Isbankscreen = false;
        if (viewFlipper.getDisplayedChild() == 1) {
            // unOrgSharedPref.put(getOtp,"otp");
            Utility.otpcheck = true;
        } else if (viewFlipper.getDisplayedChild() == 2) {
            Utility.Isusertypescreen = true;
        } else if (viewFlipper.getDisplayedChild() == 3) {
            Utility.Isbankscreen = true;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        // checkOtp=unOrgSharedPref.getString(getOtp);
        // if(checkOtp=="otp")
        if (Utility.otpcheck) {
            viewFlipper.setDisplayedChild(1);
            String OTP = OTPEditText.getText().toString().trim();
        } else if (Utility.Isusertypescreen) {
            viewFlipper.setDisplayedChild(2);
        } else if (Utility.Isbankscreen) {
            viewFlipper.setDisplayedChild(3);
        }
    }

    private void selectImage(final int type) {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_from_library), getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals(getString(R.string.take_photo))) {

                            try {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, type);
                                imageUploadType = ImageUploadType.REQUEST_CAMERA;
                            } catch (Exception e) {
                                DebugLog.e(e.toString());
                                //TODO:Need to check the existence of camera
                                Utility.showToast(RegisterActivity.this, getString(R.string.unable_open_camera));
                            }
                        } else if (items[item].equals(getString(R.string.choose_from_library))) {
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(
                                    Intent.createChooser(intent, getString(R.string.select_file)),
                                    type);
                            imageUploadType = ImageUploadType.SELECT_FILE;
                        } else if (items[item].equals(getString(R.string.cancel))) {
                            dialog.dismiss();
                        }
                    }
                }

        );
        builder.show();
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        // this device has a camera
        // no camera on this device
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private String TAG = RegisterActivity.class.getSimpleName();
}
