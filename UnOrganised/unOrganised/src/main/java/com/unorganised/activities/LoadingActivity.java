package com.unorganised.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.unorganised.R;
import com.unorganised.util.Constants;

public class LoadingActivity extends Activity implements OnClickListener{
	private Button registerBtn;
	private Button loginBtn;
	private Button callBtn;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_activity);
		registerBtn = (Button) findViewById(R.id.register_btn_id);
		loginBtn = (Button) findViewById(R.id.login_btn_id);
		callBtn = (Button)findViewById(R.id.call_us_btn_id);
		//TODO:We need to add the single click listener
		registerBtn.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		callBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.register_btn_id: {
//			Intent intent = new Intent(this, RegisterActivity.class);
			Intent intent = new Intent(this, RegisterTypeActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.login_btn_id: {
			Intent intent = new Intent(this, LoginActivity.class);
//			Intent intent = new Intent(this, PostJobActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.call_us_btn_id:
			String numberToDial = "tel:" + Constants.CALL_CENTER_NUMBER;
			Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse(numberToDial));
			startActivity(intentCall);
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
