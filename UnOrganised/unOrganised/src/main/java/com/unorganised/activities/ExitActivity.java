package com.unorganised.activities;

import android.app.Activity;
import android.os.Bundle;

import com.unorganised.util.Utility;

/**
 * 
 * {@link ExitActivity} is used to remove the application from Recents apps tray
 * of the device.
 * 
 */
public class ExitActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utility.isAddRole = false;
		finish();
	}
}