package com.unorganised.views;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.unorganised.R;
import com.unorganised.activities.ExitActivity;
import com.unorganised.activities.JobDetailsActivity;
import com.unorganised.activities.JobSeekerDashbordActivity;
import com.unorganised.interfaces.JobOperations;
import com.unorganised.interfaces.MapsOperations;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.ExitConfirmDialogListener;
import com.unorganised.util.Job;
import com.unorganised.util.JobsParser;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

public class JobSearchFragment extends Fragment implements Constants,
		MapsOperations, HttpRequestResponse, OnClickListener ,JobOperations, ExitConfirmDialogListener{

    private boolean bSearchBar;
    private String TAG = JobSearchFragment.class.getSimpleName();
	private Activity activity;
	private MapsFragment mapsFragment;
	private final String MAPS_TAG = "MapsTag";
	private ArrayList<Job> jobsList;
	private View selectedJobDetailsView;
	private TextView providerNameView;
	private TextView jobLocationView;
	private TextView jobTypeView;
	private TextView jobDescView;
	private TextView amountView;
	private TextView dateTimeView;
	private ImageView searchImg;
	private ImageView profileIcon;
	private ImageView mapsImg;
	private ImageView jobsListImg;
	//private ImageView filterImg;
	private FrameLayout mapsLayout;
	private FrameLayout jobsListLayout;


    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO:Need to optimize
		View view = inflater.inflate(R.layout.search_job_fragment, container, false);
		providerNameView = (TextView) view.findViewById(R.id.provier_name_text_id);
		jobLocationView = (TextView) view.findViewById(R.id.address_text_id);
		jobTypeView = (TextView) view.findViewById(R.id.service_type_text_id);
		amountView = (TextView) view.findViewById(R.id.amount_text_id);
		dateTimeView = (TextView) view.findViewById(R.id.date_time_text_id);
		selectedJobDetailsView = view.findViewById(R.id.job_details_view);
		jobDescView = (TextView)view.findViewById(R.id.description_text_id);
		profileIcon = (ImageView) view.findViewById(R.id.profile_icon_id);
		showLocationChooser();
		searchImg = (ImageView) ((JobSeekerDashbordActivity) activity).actionBarView.findViewById(R.id.search_img_id);
		mapsImg = (ImageView) ((JobSeekerDashbordActivity) activity).actionBarView.findViewById(R.id.maps_img_id);
		mapsImg.setVisibility(View.GONE);
		jobsListImg = (ImageView) ((JobSeekerDashbordActivity) activity).actionBarView.findViewById(R.id.jobs_list_img_id);
//		filterImg = (ImageView) ((JobSeekerDashbordActivity) activity).actionBarView.findViewById(R.id.filter_img_id);
		searchImg.setOnClickListener(this);
		mapsImg.setOnClickListener(this);
		jobsListImg.setOnClickListener(this);
		//filterImg.setOnClickListener(this);
		mapsLayout = (FrameLayout)view.findViewById(R.id.maps_layout);
		jobsListLayout = (FrameLayout)view.findViewById(R.id.jobs_list_layout);
		view.setFocusableInTouchMode(true);
		view.requestFocus();
		view.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
					ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(getActivity(), JobSearchFragment.this);
					exitConfirm.show();
					return true;
				}
				return false;
			}
		});
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	private void showLocationChooser() {
		mapsFragment = new MapsFragment(this);
		Bundle bundle = new Bundle();
		bundle.putInt(KEY_MAPS_LAUNCHER_TYPE,
				MAPS_LAUNCHER_TYPE.SEARCH.ordinal());
		bundle.putString("JOB_SEARCH_FRAGMENT","EXIT_CONFIRM");
		mapsFragment.setArguments(bundle);
		addFragment(R.id.maps_container_id, mapsFragment, MAPS_TAG);
	}

	@Override
	public void onMarkerClick(Marker marker) {
		mapsFragment.reSetLocationMarkers();
		marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_pin_selected));
		LatLng markerLatLng = marker.getPosition();
		for (int i = 0; i < jobsList.size(); i++) {
			Job job = jobsList.get(i);
			Log.d("Marker clicked"+ job.getLatitude(), "marker"+markerLatLng.latitude);
			Log.d("Marker clicked"+ job.getLongitude(), "marker"+markerLatLng.longitude);
			if (job.getLatitude() == markerLatLng.latitude
					&& job.getLongitude() == markerLatLng.longitude) {
				showJobDetails(job);
				break;
			}
		}

	}

	@Override
	public void onSearch(double lat, double lng) {
		Log.d(TAG, "Searched location lat:" + lat + " Long:" + lng);
		mapsFragment.clearMap();
		HttpReqRespLayer.getInstance().getJobs(this, activity, Utility.stlUserID, lat, lng);
	}

	/**
	 * Method to add the fragment to the container by tag.
	 *
	 * @param containerId
	 * @param fragment
	 * @param tag
	 */
	public void addFragment(int containerId, Fragment fragment, String tag) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(containerId, fragment, tag).commitAllowingStateLoss();

	}

	@Override
	public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
		Log.d(TAG, "onSuccess" + data);
		switch (dataType)
		{
			case  GET_JOBS:
				JobsParser searchJobsParser = new JobsParser();
				jobsList = searchJobsParser.parseSearchJobs(data);

				if (jobsList.size() > 0) {
					for (int i = 0; i < jobsList.size(); i++) {
						Job job = jobsList.get(i);
						mapsFragment.addMarker(job.getLatitude(), job.getLongitude());
					}
					mapsFragment.hideSearchBar();
                    bSearchBar = false;
                    searchImg.setVisibility(View.VISIBLE);
					jobsListImg.setVisibility(View.VISIBLE);
				} else {
					jobsListImg.setVisibility(View.GONE);
					Utility.showToast(getActivity(), getString(R.string.no_jobs_to_show));
				}

				break;
		}
	}

	@Override
	public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
		switch (dataType){
			case GET_JOBS:
				Utility.showToast(getActivity(),getString(R.string.unable_get_jobs));
				break;
		}

	}

	private void showJobDetails(final Job job) {
		selectedJobDetailsView.setVisibility(View.VISIBLE);
		selectedJobDetailsView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent =  new Intent(getActivity(), JobDetailsActivity.class);
				intent.putExtra(JOB_ID,job.getId());
				startActivity(intent);
			}
		});
		if (job.getProfileImage() != null){
			profileIcon.setImageBitmap(job.getProfileImage());
		}
		providerNameView.setText(job.getProviderName());
		jobTypeView.setText(job.getDescription());
		jobLocationView.setText(job.getAddress());
		jobTypeView.setText(job.getServiceDesc());
		jobDescView.setText(job.getDescription());
		amountView.setText(activity.getResources().getString(R.string.rs_text) + String.valueOf(job.getBiddingPrice()));
		dateTimeView.setText(job.getCreatedDate());
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		// mapsFragment.stopLocationUpdates();
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		// mapsFragment.startUpdates();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, "onDetach");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

	/**
	 * Method to remove the fragment by id.
	 *
	 * @param id
	 */
	public void removeFragment(int id) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment fragment = findFragmentById(id);
		if (fragment != null) {
			ft.remove(fragment).commitAllowingStateLoss();
		}

	}

	/**
	 * Method to find the fragment by id.
	 *
	 * @param id
	 * @return Fragment object.
	 */
	public Fragment findFragmentById(int id) {
		return getFragmentManager().findFragmentById(id);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.maps_img_id:
			enableMapSearch();
			//Toast.makeText(activity, "Maps clicked", Toast.LENGTH_SHORT).show();
			break;
//		case R.id.filter_img_id:
//			Toast.makeText(activity, "Filter clicked", Toast.LENGTH_SHORT).show();
//			break;
		case R.id.search_img_id:
//			Toast.makeText(activity, "Search clicked", Toast.LENGTH_SHORT).show();
			mapsFragment.showSearchBar();
            bSearchBar = true;
			searchImg.setVisibility(View.GONE);
			break;
		case R.id.jobs_list_img_id:

			if (jobsList != null && jobsList.size() > 0)
				enableJobsList();
			break;

		default:
			break;
		}
	}
	private void enableJobsList() {
		mapsLayout.setVisibility(View.GONE);
		jobsListImg.setVisibility(View.GONE);
        searchImg.setVisibility(View.GONE);
		jobsListLayout.setVisibility(View.VISIBLE);
		mapsImg.setVisibility(View.VISIBLE);
		JobsListFragment jobListFragment = new JobsListFragment(jobsList);

		if(!jobListFragment.isAdded())	{
			removeFragment(R.id.jobs_list_container_id);
			addFragment(R.id.jobs_list_container_id, jobListFragment, getString(R.string.jobs_list));
		}

	}
	private void enableMapSearch() {
		jobsListLayout.setVisibility(View.GONE);
		mapsImg.setVisibility(View.GONE);

        if(!bSearchBar){
            searchImg.setVisibility(View.VISIBLE);
        }
		mapsLayout.setVisibility(View.VISIBLE);
		jobsListImg.setVisibility(View.VISIBLE);
	}

	@Override
	public void onJobRequested(Job job) {


	}

	@Override
	public void onJobAccepted(Job job) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onJobSelected(Job job) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onJobDeleted(Job job) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapsReady() {
		UnOrgSharedPreferences sp = UnOrgSharedPreferences.getInstance(activity);
		double lat = sp.getDouble(Constants.SP_USER_LAT);
		double lng = sp.getDouble(Constants.SP_USER_LNG);
		DebugLog.d("Lat:"+lat+" Lng:"+lng);
		HttpReqRespLayer.getInstance().getJobs(this, activity, Utility.stlUserID, lat, lng);
	}

	@Override
	public void onExitConfirmed() {
//		getActivity().moveTaskToBack(true);
		Intent intent = new Intent(getActivity(),
				ExitActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NO_ANIMATION
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

		startActivity(intent);
		getActivity().finish();
	}
}
