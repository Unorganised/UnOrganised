package com.unorganised.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.unorganised.R;

public class Utility  {
	public static boolean otpcheck;
	public static boolean Isusertypescreen;
	public static boolean Isbankscreen;
	public static boolean bPendingJobs;
	public static boolean isCompletedJob;
	public static boolean isUpdateUser;
	public static boolean isAddRole;
//	public static boolean isLoggedIn;
	public static int stnUserType;
	public static int completedjobCount;
//	public static int stnNotificationStatus = 1;
	public static int typeId = 1;
	public static long stlUserID;
	private static Map <Integer, List<Integer>> serviceSet = new HashMap<Integer, List<Integer>>();
	private static Pattern pattern;
	public static String strCompleteJobId;
	public static String stStrUserName;
    private static Matcher matcher;


    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static Bitmap profileImg;


	/**
     * Validate Email with regular expression
     * 
     * @param email
     * @return true for Valid Email and false for Invalid Email
     */
    public static boolean validate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

	public static boolean isNetworkOnline(Context context) {
	    boolean status=false;
	    try{
	        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo netInfo = cm.getNetworkInfo(0);
	        if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
	            status= true;
	        }else {
	            netInfo = cm.getNetworkInfo(1);
	            if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
	                status= true;
	        }
	    }catch(Exception e){
	        e.printStackTrace();  
	        return false;
	    }
	    return status;

	    }

	public static int[] getCurrentDate(){
		int[] date = new int[3];
		Calendar cal = Calendar.getInstance();
		date[0] = cal.get(Calendar.YEAR);
		date[1] = cal.get(Calendar.MONTH);
		date[2] = cal.get(Calendar.DAY_OF_MONTH);
		return date;
	}

	public static int[] getCurrentTime() {
		int[] time = new int[2];
		Calendar cal = Calendar.getInstance();
		time[0] = cal.get(Calendar.HOUR_OF_DAY);
		time[1] = cal.get(Calendar.MINUTE);
		return time;
	}

	public static String getFormattedTime() {
//        SimpleDateFormat formatedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        formatedDate.setTimeZone(TimeZone.getDefault());
//        return formatedDate.format(Calendar.getInstance().getTime());

		int[] date = getCurrentDate();
		int[] time = getCurrentTime();
		int month = date[1] + 1;

		/*if (time[0] > 12) {
			time[0] -= 12;
		} else if (time[0] == 0) {
			time[0] += 12;
		}*/
		return String.valueOf(date[0]) + "-" + month + "-" + date[2] + " " + time[0] + ":" + time[1];
	}

	public static String getFormattedDate(int year, int month, int day){
		String monthVal = "" + month;
		String dayValue = "" + day;

		if (month < 10){
			monthVal = "0" + monthVal;
		}
		if (day < 10){
			dayValue = "0" + dayValue;
		}
		return (String.format("%d-%s-%s", year, monthVal, dayValue));
	}

	// Used to convert 24hr format to 12hr format with AM/PM values
	public static String updateTime(int hours, int mins) {

		String timeSet = "";
		if (hours > 12) {
			hours -= 12;
			timeSet = "PM";
		} else if (hours == 0) {
			hours += 12;
			timeSet = "AM";
		} else if (hours == 12)
			timeSet = "PM";
		else
			timeSet = "AM";


		String minutes = "";
		if (mins < 10)
			minutes = "0" + mins;
		else
			minutes = String.valueOf(mins);

		// Append in a StringBuilder
		String aTime = new StringBuilder().append(hours).append(':')
				.append(minutes).append(" ").append(timeSet).toString();

		return aTime;
	}

	public static String getAddressFromLocation(Context mContext, double latitude, double longitude){
		Geocoder geocoder = new Geocoder(mContext);
		List<Address> addresses = null;
		String addressText = "";
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		} catch (IOException ioException) {
			//Utility.showToast(mContext,"Service not available");
		} catch (IllegalArgumentException illegalArgumentException) {
			//Utility.showToast(mContext,"Invalid lng and lat");
		}

		// Handle case where no address was found.
		if (addresses == null || addresses.size()  == 0) {
//			Utility.showToast(mContext,"No address found");
		} else {
			Address address = addresses.get(0);
			ArrayList<String> addressFragments = new ArrayList<String>();
			// Fetch the address lines using getAddressLine,
			// join them, and send them to the thread.
			for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
				addressFragments.add(address.getAddressLine(i));
			}
			addressText = TextUtils.join(System.getProperty("line.separator"),
					addressFragments);
		}
		return addressText;


	}

	public static String getPath(Context context, Uri uri) throws URISyntaxException {
	    if ("content".equalsIgnoreCase(uri.getScheme())) {
	        String[] projection = { "_data" };
	        Cursor cursor = null;

	        try {
	            cursor = context.getContentResolver().query(uri, projection, null, null, null);
	            int column_index = cursor.getColumnIndexOrThrow("_data");
	            if (cursor.moveToFirst()) {
	                return cursor.getString(column_index);
	            }
	        } catch (Exception e) {
	            // Eat it
	        }
	    }
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	} 
	
	public static void addService(int serviceId, ArrayList<Integer> selSubServicesIds){
		serviceSet.put(serviceId, selSubServicesIds);
		Log.d("Service", serviceSet.toString());
		
	}
	
	public static Map<Integer, List<Integer>> getServices(){
		return serviceSet;
	}
	
	// convert from bitmap to byte array
	public static byte[] getBytesFromBitmap(Bitmap bitmap) {
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    bitmap.compress(CompressFormat.JPEG, 100, stream);

		if (stream != null){
			return stream.toByteArray();
		}else {
			return null;
		}
	}
	
	/**
	 * Method to find the fragment by id.
	 * 
	 * @param id
	 * @return Fragment object.
	 */
	public static Fragment findFragmentById(Activity activity,int id) {
		return activity.getFragmentManager().findFragmentById(id);
	}



	/**
	 * Method to find the fragment by tag.
	 * 
	 * @param tag
	 * @return Fragment object.
	 */
	public Fragment findFragmentByTag(Activity activity,String tag) {
		return activity.getFragmentManager().findFragmentByTag(tag);
	}

	/**
	 * Method to hide the fragment by id.
	 * 
	 * @param id
	 */
	public void hideFragment(Activity activity,int id) {
		FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
		Fragment fragment = findFragmentById(activity,id);
		ft.hide(fragment).commitAllowingStateLoss();

	}

	/**
	 * Method to show the fragment by id.
	 * 
	 * @param id
	 */
	public void showFragment(Activity activity,int id) {
		FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
		Fragment fragment = findFragmentById(activity, id);
		ft.show(fragment).commitAllowingStateLoss();

	}

	/**
	 * Method to add the fragment to the container by tag.
	 * 
	 */
	public void addFragment(Activity activity,int containerId, Fragment fragment, String tag) {
		FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
		ft.add(containerId, fragment, tag).commitAllowingStateLoss();

	}

	/**
	 * Method to remove the fragment by tag.
	 * 
	 * @param tag
	 */
	public void removeFragment(Activity activity,String tag) {
		FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
		Fragment fragment = findFragmentByTag(activity,tag);
		if (fragment != null) {
			ft.remove(fragment).commitAllowingStateLoss();

		}

	}
	protected boolean isFragmentVisible(Activity activity,String tag) {
		Fragment fragment = findFragmentByTag(activity, tag);
		if (fragment != null && fragment.isVisible()) {
			
			return true;
		}
		else
		{
			return false;
		}

	}

	/**
	 * Method to remove the fragment by id.
	 * 
	 * @param id
	 */
	public static void removeFragment(Activity activity, int id) {
		FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
		Fragment fragment = findFragmentById(activity, id);
		if (fragment != null) {
			ft.addToBackStack(null);
			ft.remove(fragment).commitAllowingStateLoss();
		}

	}
	
	/**
	 * Method to remove the fragment by id.
	 * 
	 */
	public static void removeFragment(Activity activity,Fragment fragment) {
		FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
		if (fragment != null) {
			ft.remove(fragment).commitAllowingStateLoss();
		}

	}
	
	public static void showToast(Activity activity,String msg)
	{
		Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
	}

	public static void showToast(Context context,String msg){
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	
	public static boolean isLocationAccessEnabled(final Activity activity) {
		LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		boolean gps_enabled = false;
		boolean network_enabled = false;

		try {
			gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
		}

		try {
			network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
		}

		if (!gps_enabled && !network_enabled) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
			dialog.setMessage(activity.getString(R.string.enable_location_msg));
			dialog.setPositiveButton(activity.getString(R.string.settings),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							// TODO Auto-generated method stub
							activity.startActivityForResult(
									new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 100);
						}
					});
			dialog.setNegativeButton(activity.getString(R.string.cancel),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(
								DialogInterface paramDialogInterface, int paramInt) {	}
					});
			dialog.show();
			// no network provider is enabled
			Log.e("Current Location", "Current Lat Lng is Null");
			return false;
		}
		return true;

	}
	
	public static boolean isEmailClientAvailable(Context context) {
		boolean yes = false;

		Intent i = new Intent().setAction(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		PackageManager pm = context.getPackageManager();

		List<ResolveInfo> list = pm.queryIntentActivities(i, 0);
		yes = (0 != list.size());

		return yes;
	}

	public static void resetServiceData() {
		serviceSet = null;
		serviceSet = new HashMap<Integer, List<Integer>>();
	}

	public static String getJobDuration(String strStartDate) {
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		sourceFormat.setTimeZone(TimeZone.getDefault());
		SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm");
		newFormat.setTimeZone(TimeZone.getDefault());
		long diff = 0;
		try {
			Date startDate = sourceFormat.parse(strStartDate);
			Date stopDate = sourceFormat.parse(Utility.getFormattedTime());
			diff = stopDate.getTime() - startDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date dateDiff = new Date(diff);
		return newFormat.format(dateDiff);
	}

//	public static ArrayList<Job> getAcceptedJobs() {
//		ArrayList<Job> jobsList = new ArrayList<Job>();
//		Job job = new Job("Abhi", "Need housekeeping", "CyberTowers",
//				17.449760845233513, 78.37653491646051,400,"25thDec 2015(sunday),9AM-9PM");
//		jobsList.add(job);
//		return jobsList;
//	}
//
//	public static Job getJobDetails(int jobId) {
//
//		Job job = new Job("Abhi", "Need housekeeping", "CyberTowers",
//				17.449760845233513, 78.37653491646051,400,"25thDec 2015(sunday),9AM-9PM");
//		return job;
//	}


//	public static ArrayList<Job> getTempJobs()
//	{
//		ArrayList<com.unorganised.util.Job> jobsList = new ArrayList<com.unorganised.util.Job>();
//		com.unorganised.util.Job job1 = new com.unorganised.util.Job("Ashok", "Need plumber", "bharathnagar",
//				17.464381735488654, 78.42509731650352,200,"25thNob 2015,9PM-9AM");
//		com.unorganised.util.Job job2 = new com.unorganised.util.Job("Abhi", "Need housekeeping","CyberTowers",
//				17.449760845233513, 78.37653491646051, 300,"25thNob 2015,9PM-9AM");
//		com.unorganised.util.Job job3 = new com.unorganised.util.Job("Anusha", "Need doctor "
//				,"Kukatpalli", 17.483385475061446,
//				78.3870904147625,1000,"25thNob 2015,9PM-9AM");
//		jobsList.add(job1);
//		jobsList.add(job2);
//		jobsList.add(job3);
//		return jobsList;
//	}
//
//	public static String getAddress(double lat,double lng)
//	{
//		//TODO:Need to get address using lat,lng
//		return  "Hyderabad";
//	}





}

