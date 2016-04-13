package com.unorganised.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

import com.unorganised.R;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.unorganised.R;

public class UpdateBankInfoActivity extends Activity implements Constants, HttpRequestResponse, View.OnClickListener {

    private byte[] bytCheque;
    private final int CANCELLED_CHEQUE = 3;
    private int userType;
    private long userId;
    private String[] strBankList;
    private Button saveButton;
    private Button cancelButton;
    private EditText accountNo;
    private EditText retypeaccuntNo;
    private EditText ifscCode;
    private Spinner selectBankSpinner;
    UnOrgSharedPreferences sharedPref;
    private Button uploadCheque;
    private enum ImageUploadType {
        REQUEST_CAMERA,
        SELECT_FILE
    }

    private ImageUploadType imageUploadType = ImageUploadType.REQUEST_CAMERA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bank_info);
        HttpReqRespLayer.getInstance().getBanks(this, this);
        accountNo = (EditText) findViewById(R.id.acnt_no_id);
        retypeaccuntNo = (EditText) findViewById(R.id.re_enter_acnt_id);
        ifscCode = (EditText) findViewById(R.id.sel_isfc_code_id);
        selectBankSpinner = (Spinner) findViewById(R.id.sel_bank_id);
        uploadCheque = (Button) findViewById(R.id.cancelled_cheque_id);
        uploadCheque.setOnClickListener(this);
        saveButton = (Button) findViewById(R.id.edit);
        cancelButton = (Button) findViewById(R.id.cancel);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        sharedPref = UnOrgSharedPreferences.getInstance(this);
        userId = sharedPref.getLong(SP_USER_ID_KEY);
        userType = sharedPref.getInt(SP_USER_TYPE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit:
                String accNo = accountNo.getText().toString();
                String reaccNo = retypeaccuntNo.getText().toString();
                String bName = selectBankSpinner.getSelectedItem().toString();
                String ifsc = ifscCode.getText().toString();
                String chequeFormat = "";

                if (validateBankEntries(accNo, reaccNo, ifsc)) {
                    String ImgCheque = Base64.encodeToString(bytCheque, Base64.NO_WRAP);
                    getInstance().updateseekerbankDetails(this, this, userId, accNo, bName, ifsc, ImgCheque, chequeFormat);
                }

                break;
            case R.id.cancelled_cheque_id:
                selectImage(CANCELLED_CHEQUE);
                break;
            case R.id.cancel:
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

    private boolean validateBankEntries(String accntNumber, String retypeaccNo, String ifsc) {
        if (accntNumber.equals("") || retypeaccNo.equals("") || ifsc.equals("")) {
            Utility.showToast(this, getResources().getString(R.string.fill_details_error));
            return false;
        } else if (selectBankSpinner.getSelectedItem().equals(getResources().getString(R.string.select_bank_prompt))) {
            Utility.showToast(this, getResources().getString(R.string.select_your_bank_error));
            return false;
        } else if (bytCheque == null || (bytCheque.length == 0)) {
            Utility.showToast(this, getResources().getString(R.string.choose_cancelled_check_error));
            return false;
        } else if (!accntNumber.equals(retypeaccNo)) {
            Utility.showToast(this, getString(R.string.account_no_mismatch_error));
        }

        return true;
    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        switch (dataType) {
            case GET_BANKS:
                fillBankList(data);
                addBankList();
                break;
            case UPDATE_SEEKER_BANK_DETAILS:
                Utility.showToast(this, getResources().getString(R.string.edit_successfully));
                Intent user;
                if (userType == 1) {
                    user = new Intent(this, JobSeekerDashbordActivity.class);
                    user.putExtra(DRAWER_ITEM_POSITION, 5);
                    startActivity(user);

                } else if (userType == 2) {
                    user = new Intent(this, JobProviderDashboardActivity.class);
                    user.putExtra(DRAWER_ITEM_POSITION, 4);
                    startActivity(user);
                } else {
                    if (userType == 3) {
                        if (Utility.typeId == 1) {
                            Utility.showToast(this, getResources().getString(R.string.edit_successfully));
                            user = new Intent(this, JobSeekerDashbordActivity.class);
                            user.putExtra(DRAWER_ITEM_POSITION, 5);

                            startActivity(user);


                        } else {
                            Utility.showToast(this, getResources().getString(R.string.edit_successfully));
                            user = new Intent(this, JobProviderDashboardActivity.class);
                            user.putExtra(DRAWER_ITEM_POSITION, 4);

                            startActivity(user);

                        }
                    }
                    finish();
                }

        }


    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        try {
            Utility.showToast(this, data.getString(Constants.MESSAGE));
        } catch (Exception e) {

        }
    }

    private void addBankList() {
        List<String> list = new ArrayList<String>();
        int length = strBankList.length;

        if (length > 0) {
            strBankList[length - 1] = getResources().getString(R.string.select_bank_prompt);
        }

        Collections.addAll(list, strBankList);

        CustomizedSpinnerAdapter adapter1 = new CustomizedSpinnerAdapter(
                this, android.R.layout.simple_spinner_item,
                list);
        selectBankSpinner.setAdapter(adapter1);
        selectBankSpinner.setSelection(length - 1);
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
    private void selectImage(final int type) {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_from_library), getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateBankInfoActivity.this);
        builder.setTitle(getString(R.string.add_photo));
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
                                Utility.showToast(UpdateBankInfoActivity.this, getString(R.string.unable_open_camera));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == RESULT_OK && data != null) {
                Bitmap bitmap = null;
                if (imageUploadType == ImageUploadType.REQUEST_CAMERA) {
                    bitmap = (Bitmap) data.getExtras().get("data");
//					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//					bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//					//TODO:We need to decide where to store the photo which is capctured
//					File destination = new File(Environment.getExternalStorageDirectory(),
//							System.currentTimeMillis() + ".jpg");
//					FileOutputStream fo;
//					try
//					{
//						destination.createNewFile();
//						fo = new FileOutputStream(destination);
//						fo.write(bytes.toByteArray());
//						fo.close();
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}

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


                    // Set the Image in ImageView after decoding the String
//	            Bitmap bitmap = BitmapFactory.decodeFile(imgDecodableString);
//	            int scaleValue = 500;
//				//            TODO: Scaling image logic.
//	            if (bitmap.getWidth() > scaleValue || bitmap.getHeight() > scaleValue){
//	                int newHeight = (int) ( bitmap.getHeight() * (500.0 / bitmap.getWidth()) );
//	                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, scaleValue, newHeight, true);
//	                bitmap = scaled;
////	                imgView.setImageBitmap(scaled);
//	            } else {
//	            	ImageView imgView = (ImageView) findViewById(R.id.imgView);
//	            	imgView.setImageBitmap(bitmap);
//	            }

                    File imagefile = new File(imagePath);
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(imagefile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    bitmap = BitmapFactory.decodeStream(fis);
                }


                bytCheque = Utility.getBytesFromBitmap(bitmap);
                Utility.showToast(this, getResources().getString(R.string.cancel_cheque_uploaded_error));
                ImageView cancelledChequeImg = (ImageView) findViewById(R.id.cancelled_cheque_img_id);
                cancelledChequeImg.setVisibility(View.VISIBLE);
                uploadCheque.setText(getString(R.string.cheque_uploaded));
                cancelledChequeImg.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 100, 100, false));


            } else {
                Utility.showToast(this, getString(R.string.u_have_nt_pic_the_image));
            }
        } catch (Exception e) {
            Utility.showToast(this, getString(R.string.went_wrong));
        }
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

    @Override
    public void onBackPressed() {
        finish();
    }

}
