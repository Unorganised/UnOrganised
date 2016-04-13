package com.unorganised.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

import com.unorganised.R;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;
import com.unorganised.views.UserSettingsFragment;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class PersonalInfoActivity extends Activity implements View.OnClickListener, Constants, HttpRequestResponse {
    private byte[] bytPhoto;
    private int typeId;
    private int userType;
    private final int PHOTO_CHOOSER = 1;
    private long userId;
    private String strImgPhoto = "";
    private Double latitude;
    private Double longitude;
    private Button uploadImageBtn;
    private Button editBtn;
    private Button cancelBtn;
    private ImageView userImage;
    private ImageView locationImage;
    private FrameLayout picLayout;
    private EditText userText;
    private EditText mobileNoText;
    private EditText addressText;
    private EditText mailText;
    private UnOrgSharedPreferences sharedPref;
    private FrameLayout PicLayout;

    private enum ImageUploadType {
        REQUEST_CAMERA,
        SELECT_FILE
    }

    private ImageUploadType imageUploadType = ImageUploadType.REQUEST_CAMERA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        sharedPref = UnOrgSharedPreferences.getInstance(this);
        Intent intent = getIntent();
        picLayout = (FrameLayout) findViewById(R.id.pic_layout);
        userImage = (ImageView) findViewById(R.id.user_icon);
        locationImage = (ImageView) findViewById(R.id.location_icon_id);
        picLayout = (FrameLayout) findViewById(R.id.pic_layout);
        editBtn = (Button) findViewById(R.id.edit);
        cancelBtn = (Button) findViewById(R.id.cancel);
        userText = (EditText) findViewById(R.id.Username);
        mobileNoText = (EditText) findViewById(R.id.mob);
        mailText = (EditText) findViewById(R.id.emailid);
        addressText = (EditText) findViewById(R.id.location_id);
        userImage.setOnClickListener(this);
        locationImage.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);
        //userImage.setImageResource(R.drawable.default_profile_pic);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_pic);
        userType = sharedPref.getInt(SP_USER_TYPE);
        userId = sharedPref.getLong(SP_USER_ID_KEY);
        userText.setText(intent.getExtras().getString(FULL_NAME_TAG));
        mailText.setText(intent.getExtras().getString(EMAIL_ID_TAG));
        addressText.setText(intent.getExtras().getString(KEY_ADDRESS));
        mobileNoText.setText(intent.getExtras().getString(MOBILE_NUMBER_TAG));
        latitude = intent.getDoubleExtra(KEY_LAT, 0.0);
        longitude = intent.getDoubleExtra(KEY_LNG, 0.0);
       // Utility.showToast(this,Integer.toString(typeId));
        byte[] userPic = intent.getExtras().getByteArray(USER_PHOTO);

        if (userType == 1 || userType == 3) {
            picLayout.setVisibility(View.VISIBLE);
            if ( userPic != null) {
                Bitmap decodedByte = BitmapFactory.decodeByteArray(userPic, 0, userPic.length);
                userImage.setImageBitmap(decodedByte);
                strImgPhoto = Base64.encodeToString(userPic, Base64.NO_WRAP);;
            } else {
                userImage.setImageBitmap(icon);
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.user_icon:
                selectimage(PHOTO_CHOOSER);
                break;
            case R.id.location_icon_id:
                Intent intent = new Intent(this, UserLocationActivity.class);
                intent.putExtra(Constants.KEY_LOCATION_ACTIVITY_LAUNCHER_TYPE, LOCATION_ACTIVITY_LAUNCHER_TYPE.POST_JOB_LOCATION);
                startActivityForResult(intent, 2);
                break;
            case R.id.edit:
                String uName = userText.getText().toString();
                String uEmail = mailText.getText().toString();
                String uMobile = mobileNoText.getText().toString();
                String uAddress = addressText.getText().toString();
                String photoFormat = "";

                if (userType == 1 || userType == 3) {
                    if (validateSeeker(uName, uEmail,uAddress)) {

                        if (bytPhoto != null) {
                            strImgPhoto = Base64.encodeToString(bytPhoto, Base64.NO_WRAP);
                        }
                        sharedPref.put(SP_USER_LAT, latitude);
                        sharedPref.put(SP_USER_LNG, longitude);
                        getInstance().updateseekerpersonalInfo(this, this, userId, uName, uEmail, latitude, longitude, uAddress, strImgPhoto, photoFormat);
                    }
                } else {
                    if (validateProvider(uName, uEmail, uAddress)) {
                        getInstance().updateproviderpersonalInfo(this, this, userId, uName, uEmail, latitude, longitude, uAddress);
                    }
                }
                break;
            case R.id.cancel:
                finish();
//                Intent user;
//                if (userType == 1 || userType == 3) {
//                    user = new Intent(this, JobSeekerDashbordActivity.class);
//                    user.putExtra(DRAWER_ITEM_POSITION, 5);
//                    finish();
//
//                } else {
//                    user = new Intent(this, JobProviderDashboardActivity.class);
//                    user.putExtra(DRAWER_ITEM_POSITION, 4);
//                    finish();
//
//                }
//                startActivity(user);
                break;
        }
    }


    private boolean validateSeeker(String name, String mail, String addrs) {
        if (name.equals("")) {
            Utility.showToast(this, getString(R.string.enter_full_name_error));
            userText.requestFocus();
            return false;
        } else if (addrs.equals("")) {
            Utility.showToast(this, getString(R.string.enter_address_error));
            addressText.requestFocus();
            return false;
        } else if (mail.equals("") || !Utility.validate(mail)) {
            Utility.showToast(this, getString(R.string.enter_valid_mail_id_error));
            mailText.requestFocus();
            return false;
        }


        return true;
    }

    private boolean validateProvider(String name, String mail, String addrs) {
        if (name.equals("")) {
            Utility.showToast(this, getString(R.string.enter_full_name_error));
            userText.requestFocus();
            return false;
        } else if (mail.equals("") || !Utility.validate(mail)) {
            Utility.showToast(this, getString(R.string.enter_valid_mail_id_error));
            mailText.requestFocus();
            return false;
        } else if (addrs.equals("")) {
            Utility.showToast(this, getString(R.string.enter_address_error));
            addressText.requestFocus();
            return false;
        }


        return true;
    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        Log.d("OnSuccess...", data.toString());
        Intent user;
        switch (dataType) {
            case UPDATE_PROVIDER_PERSONAL_DETAILS:
                Utility.showToast(this, getResources().getString(R.string.edit_successfully));
                user = new Intent(this, JobProviderDashboardActivity.class);
                user.putExtra(DRAWER_ITEM_POSITION, 4);
                finish();
                startActivity(user);
                break;
            case UPDATE_SEEKER_PERSONAL_DETAILS:
                if (userType == 1) {
                    Utility.showToast(this, getResources().getString(R.string.edit_successfully));
                    user = new Intent(this, JobSeekerDashbordActivity.class);
                    user.putExtra(DRAWER_ITEM_POSITION, 5);
                    startActivity(user);
                    break;
                } else if (userType == 3) {
                    if (Utility.typeId == 1) {
                        Utility.showToast(this, getResources().getString(R.string.edit_successfully));
                        user = new Intent(this, JobSeekerDashbordActivity.class);
                        user.putExtra(DRAWER_ITEM_POSITION, 5);
                        startActivity(user);
                        break;
                    } else {
                        Utility.showToast(this, getResources().getString(R.string.edit_successfully));
                        user = new Intent(this, JobProviderDashboardActivity.class);
                        user.putExtra(DRAWER_ITEM_POSITION, 4);
                        startActivity(user);
                        break;
                    }
                }
                finish();
        }
    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        try {
            Utility.showToast(this, data.getString(Constants.MESSAGE));
        } catch (Exception e) {

        }
    }


    private void selectimage(final int type) {
        final CharSequence[] items = {getString(R.string.take_photo),getString(R.string.choose_from_library), getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInfoActivity.this);
        builder.setTitle(getString(R.string.add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals(getString(R.string.take_photo))) {

                            try {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, type);
                                imageUploadType = ImageUploadType.REQUEST_CAMERA;
                            } catch (Exception e) {
                                DebugLog.e(e.toString());
                                //TODO:Need to check the existence of camera
                                Utility.showToast(PersonalInfoActivity.this, getString(R.string.unable_open_camera));
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
            if (resultCode == RESULT_OK && requestCode == PHOTO_CHOOSER) {
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
                if (bitmap.getHeight() > 0 && bitmap.getWidth() > 0) {
                    userImage.setImageBitmap(bitmap);
                    bytPhoto = Utility.getBytesFromBitmap(bitmap);
                } else {
                    Utility.showToast(this, getString(R.string.empty));
                }


            } else {
                if (data != null) {
                    String locationName = data.getStringExtra(KEY_LOCATION_NAME);
                    longitude = data.getDoubleExtra(KEY_LNG, 0);
                    latitude = data.getDoubleExtra(KEY_LAT, 0);

                    //TODO:We can set addressText also,but sometime maps is giving only house number,so better to display only lat,lng.
                    addressText.setText(locationName);
                }


            }
        } catch (Exception e) {
            Utility.showToast(this, getString(R.string.went_wrong));
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
