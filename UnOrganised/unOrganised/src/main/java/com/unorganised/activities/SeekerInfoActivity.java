package com.unorganised.activities;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import static com.unorganised.network.HttpReqRespLayer.getInstance;

import com.unorganised.R;
import com.unorganised.util.Constants;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Utility;

import org.json.JSONObject;

public class SeekerInfoActivity extends Activity implements View.OnClickListener, Constants, HttpRequestResponse {

    private Long userId;
    private int userType;
    private String addInfo;
    private EditText addeditTxt;
    private EditText setinfoTxt;
    private Button addBtn;
    private Button cancelBtn;
    private Button backBtn;
    private UnOrgSharedPreferences sharedPref;
    private RelativeLayout txtcountLayout;
    private TextView charTxt;
    private ImageView editImage;
    private FrameLayout addinfoLayout;
    private RelativeLayout titleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker__info);
        sharedPref = UnOrgSharedPreferences.getInstance(this);
        userType = sharedPref.getInt(SP_USER_TYPE);
        txtcountLayout = (RelativeLayout) findViewById(R.id.count_layout);
        addinfoLayout = (FrameLayout) findViewById(R.id.add_detailslayout);
        //editImage = (ImageView) findViewById(R.id.editImage);
        titleLayout = (RelativeLayout) findViewById(R.id.title);
        charTxt = (TextView) findViewById(R.id.char_txt);
        addeditTxt = (EditText) findViewById(R.id.add_info_seeker);
        addeditTxt.addTextChangedListener(EditorWatcher);
        addBtn = (Button) findViewById(R.id.add);
        cancelBtn = (Button) findViewById(R.id.cancel);
        backBtn = (Button) findViewById(R.id.back);
        addBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        //editImage.setOnClickListener(this);
        userId = sharedPref.getLong(SP_USER_ID_KEY);
        Intent i = getIntent();

        if (i.hasExtra(DETAILS)) {

            if (i.getExtras().getString(DETAILS) != null) {

                addBtn.setVisibility(View.VISIBLE);
                addBtn.setText(getResources().getString(R.string.edit));
                cancelBtn.setVisibility(View.GONE);
                backBtn.setVisibility(View.VISIBLE);
                txtcountLayout.setVisibility(View.GONE);
                titleLayout.setVisibility(View.GONE);
                addeditTxt.setText(i.getExtras().getString(DETAILS));
                addinfoLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.grayborder));


            } else {
                addinfoLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_layout));
                addBtn.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.GONE);
                txtcountLayout.setVisibility(View.VISIBLE);
                addinfoLayout.setVisibility(View.VISIBLE);
                titleLayout.setVisibility(View.VISIBLE);
                addeditTxt.setVisibility(View.VISIBLE);
            }
        }
        addeditTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                titleLayout.setVisibility(View.VISIBLE);
                txtcountLayout.setVisibility(View.VISIBLE);
                addBtn.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    private final TextWatcher EditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int currentPos = 100 - (s.length());
            charTxt.setText(String.valueOf(currentPos));
        }

        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                addInfo = addeditTxt.getText().toString();
                if (validatetxt(addInfo)) {
                    getInstance().addSeekerDetails(this, this, userId, addInfo);

                }

                break;
            case R.id.back:
                finish();
                break;
            case R.id.cancel:
                finish();
                break;

        }

    }


    public boolean validatetxt(String add_info) {
        if (add_info.equals(" ")) {
            Utility.showToast(this, getString(R.string.add_detail_error));
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        switch (dataType) {
            case ADD_INFORMATION:
                Utility.showToast(this, getResources().getString(R.string.add_info));
                addinfoLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.grayborder));
                addBtn.setVisibility(View.VISIBLE);
                addBtn.setText(getResources().getString(R.string.edit));
                cancelBtn.setVisibility(View.GONE);
                backBtn.setVisibility(View.VISIBLE);
                txtcountLayout.setVisibility(View.GONE);
                addinfoLayout.setVisibility(View.VISIBLE);
                titleLayout.setVisibility(View.GONE);
                addeditTxt.setText(addInfo);
                break;

        }
    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        try {
            Utility.showToast(this, data.getString(Constants.MESSAGE));
        } catch (Exception e) {

        }
    }
}
