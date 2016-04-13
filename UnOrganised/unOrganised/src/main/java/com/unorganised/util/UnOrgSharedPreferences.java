/* Copyright */

package com.unorganised.util;

import android.content.Context;
import android.content.SharedPreferences;

public class UnOrgSharedPreferences {

	private SharedPreferences.Editor editor;
	private SharedPreferences sharedPrefs;

	private static UnOrgSharedPreferences instance;

	private UnOrgSharedPreferences(Context context) {
		sharedPrefs = context.getSharedPreferences(
				Constants.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
		editor = sharedPrefs.edit();
	}

	public static UnOrgSharedPreferences getInstance(Context context) {
		if (null == instance) {
			instance = new UnOrgSharedPreferences(context);
		}

		return instance;
	}

	public void put(String key, String value) {

		editor.putString(key, value);
		editor.commit();
	}

	public void put(String key, long value) {
		editor.putLong(key, value);
		editor.commit();
	}
	
	public void put(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public void put(String key, double value) {
		editor.putLong(key, Double.doubleToLongBits(value));
		editor.commit();
	}

	public String getString(String key) {
		String value = sharedPrefs.getString(key, "");

		return value;
	}
	public double getDouble(String key) {
		return Double.longBitsToDouble(sharedPrefs.getLong(key, 0));
	}

	public long getLong(String key) {
		return sharedPrefs.getLong(key, -1);
	}
	
	public int getInt(String key) {
		return sharedPrefs.getInt(key, -1);
	}
	
	public int getIntForDuplicate(String key) {
		return sharedPrefs.getInt(key, -1);
	}

	public void put(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getBoolean(String key) {
		return sharedPrefs.getBoolean(key, false);
	}

	public void remove(String key) {
		editor.remove(key);
		editor.commit();
	}
}
