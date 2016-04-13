package com.unorganised.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unorganised.R;
import com.unorganised.activities.ExitActivity;
import com.unorganised.activities.JobSeekerDashbordActivity;
import com.unorganised.activities.UserLocationActivity;
import com.unorganised.interfaces.MapsOperations;
import com.unorganised.util.AutoPlaceDetailsJSONParser;
import com.unorganised.util.AutoPlaceJSONParser;
import com.unorganised.util.Constants;
import com.unorganised.util.Constants.MAPS_LAUNCHER_TYPE;
import com.unorganised.util.ExitConfirmDialogListener;
import com.unorganised.util.Utility;

public class MapsFragment extends Fragment implements OnMarkerClickListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, ExitConfirmDialogListener {

    private static String TAG = MapsFragment.class.getSimpleName();
    private AutoCompleteTextView placesAutoCompleteText;
    private DownloadTask placesDownloadTask;
    private DownloadTask placeDetailsDownloadTask;
    private ParserTask placesParserTask;
    private ParserTask placeDetailsParserTask;
    private GoogleMap googleMap;
    private MarkerOptions markerOptions;
    private Marker marker;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Activity activity;

    private final int PLACES = 0;
    private final int PLACES_DETAILS = 1;
    private final long INTERVAL = 1000 * 10 * 1; // 10 sec
    private final long FASTEST_INTERVAL = 1000 * 10 * 1; // 10 sec
    // Holds the launch type of maps screen
    private int launcherType = MAPS_LAUNCHER_TYPE.REGISTER.ordinal();
    // TODO:WE can remove below and use current location
    // User location details
    private double latitude;
    private double longitude;
    private FragmentManager fragmentManager;
    private MapsOperations mapsOperationsListener;
    private SupportMapFragment mMapFragment;
    private ProgressDialog progressDialog;
    private boolean isLocationAccessDisable = false;
    private ArrayList<Marker> markersList = new ArrayList<Marker>();
private String jobSearch;
    @SuppressLint("ValidFragment")
    public MapsFragment(MapsOperations mapsOperationsListener) {
        this.mapsOperationsListener = mapsOperationsListener;
    }

    public MapsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (!isGooglePlayServicesAvailable()) {
            Utility.removeFragment(getActivity(), this);
            return;
        } else if (!Utility.isLocationAccessEnabled(getActivity())) {
            isLocationAccessDisable = true;
            //Utility.removeFragment(getActivity(), this);
            return;
        }
        try {
            // show error dialog if GoolglePlayServices not available
            createLocationRequest();
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = null;
        if (savedInstanceState != null) {
            Utility.removeFragment(getActivity(), this);
            return view;
        }
        if (view == null) {
            view = inflater.inflate(R.layout.maps_fragment, container, false);
            // Getting a reference to the AutoCompleteTextView
            placesAutoCompleteText = (AutoCompleteTextView) view
                    .findViewById(R.id.atv_places);
            placesAutoCompleteText.setThreshold(1);
            mMapFragment = SupportMapFragment.newInstance();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.location_map_container, mMapFragment);
            fragmentTransaction.commit();
            mMapFragment.getMapAsync(this);
            jobSearch=getArguments().getString("JOB_SEARCH_FRAGMENT");

                view.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            if(jobSearch.equals("EXIT_CONFIRM"))
                            {Log.d("back","clicked=======================");
                                ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(getActivity(), MapsFragment.this);
                                exitConfirm.show();
                                return true;
                            }

                        }
                        return false;
                    }
                });
               // return view;
            //}

        }
        // Adding text change listener
        placesAutoCompleteText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                Log.d(TAG, "onTextChanged");
                // Creating a DownloadTask to download Google Places matching
                // "s"
                placesDownloadTask = new DownloadTask(PLACES);

                // Getting url to the Google Places Auto complete api
                String url = getAutoCompleteUrl(s.toString());

                // Start downloading Google Places
                // This causes to execute doInBackground() of DownloadTask class
                placesDownloadTask.execute(url);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        // Setting an item click listener for the AutoCompleteTextView dropdown
        // list
        placesAutoCompleteText
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int index, long id) {
                        stopLocationUpdates();
                        SimpleAdapter adapter = (SimpleAdapter) arg0
                                .getAdapter();
                        HashMap<String, String> hm = (HashMap<String, String>) adapter
                                .getItem(index);
                        // Creating a DownloadTask to download Places details of
                        // the selected place
                        placeDetailsDownloadTask = new DownloadTask(
                                PLACES_DETAILS);
                        // Getting url to the Google Places details api
                        String url = getPlaceDetailsUrl(hm.get("reference"));

                        // Start downloading Google Place Details
                        // This causes to execute doInBackground() of
                        // DownloadTask class
                        placeDetailsDownloadTask.execute(url);
                        showProgressDialog();

                    }
                });

        launcherType = getArguments().getInt(Constants.KEY_MAPS_LAUNCHER_TYPE);
        if (launcherType == MAPS_LAUNCHER_TYPE.SEARCH.ordinal()) {
            // Change hint message
            placesAutoCompleteText
                    .setHint(getString(R.string.search_job_hint_msg));
            //googleMap.setOnMarkerClickListener(this);

        }
//        view.setFocusableInTouchMode(true);
//        view.requestFocus();
//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                    ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(getActivity(), MapsFragment.this);
//                    exitConfirm.show();
//                    return true;
//                }
//                return false;
//            }
//        });
        return view;

    }

    /**
     * Get the formated url
     *
     * @param place entered place name
     * @return formated string
     */
    private String getAutoCompleteUrl(String place) {

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=" + Constants.MAPS_BROWSER_KEY;

        // place to be be searched
        String input = "input=" + place;
        try {
            input = "input=" + URLEncoder.encode(place, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // place type to be searched
        String types = "types=geocode";

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = input + "&" + types + "&" + sensor + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
                + output + "?" + parameters;

        return url;
    }

    /**
     * Get formated string to get area details
     *
     * @param ref
     * @return formated string
     */
    private String getPlaceDetailsUrl(String ref) {

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=" + Constants.MAPS_BROWSER_KEY;

        // reference of place
        String reference = "reference=" + ref;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = reference + "&" + sensor + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/details/"
                + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000);

            // Connecting to url
            urlConnection.connect();
            Log.d(TAG, "Resp:" + urlConnection.getResponseCode());
            // Log.d("Testing",
            // "is connection OK:"+(urlConnection.getResponseCode() ==
            // HttpURLConnection.HTTP_OK))));

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception:", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onExitConfirmed() {
//        getActivity().moveTaskToBack(true);
        Intent intent = new Intent(getActivity(),
                ExitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NO_ANIMATION
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        startActivity(intent);
        getActivity().finish();
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        private int downloadType = 0;

        // Constructor
        public DownloadTask(int type) {
            this.downloadType = type;
        }

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            switch (downloadType) {
                case PLACES:
                    // Creating ParserTask for parsing Google Places
                    placesParserTask = new ParserTask(PLACES);

                    // Start parsing google places json data
                    // This causes to execute doInBackground() of ParserTask class
                    placesParserTask.execute(result);

                    break;

                case PLACES_DETAILS:
                    // Creating ParserTask for parsing Google Places
                    placeDetailsParserTask = new ParserTask(PLACES_DETAILS);
                    // Starting Parsing the JSON string
                    // This causes to execute doInBackground() of ParserTask class
                    placeDetailsParserTask.execute(result);
                    break;
                default:
                    break;

            }
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends
            AsyncTask<String, Integer, List<HashMap<String, String>>> {

        int parserType = 0;

        public ParserTask(int type) {
            this.parserType = type;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<HashMap<String, String>> list = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                switch (parserType) {
                    case PLACES:
                        AutoPlaceJSONParser placeJsonParser = new AutoPlaceJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeJsonParser.parse(jObject);
                        break;
                    case PLACES_DETAILS:
                        AutoPlaceDetailsJSONParser placeDetailsJsonParser = new AutoPlaceDetailsJSONParser();
                        // Getting the parsed data as a List construct
                        list = placeDetailsJsonParser.parse(jObject);
                        break;
                }

            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            switch (parserType) {
                case PLACES:
                    String[] from = new String[]{"description"};
                    int[] to = new int[]{R.id.textViewItem};
                    if (result != null) {
                        // Creating a SimpleAdapter for the AutoCompleteTextView
                        SimpleAdapter adapter = new SimpleAdapter(activity, result,
                                R.layout.location_list_row, from, to);
                        // Setting the adapter
                        placesAutoCompleteText.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case PLACES_DETAILS:
                    HashMap<String, String> hm = result.get(0);

                    // Getting latitude from the parsed data
                    latitude = Double.parseDouble(hm.get("lat"));

                    // Getting longitude from the parsed data
                    longitude = Double.parseDouble(hm.get("lng"));
                    mapsOperationsListener.onSearch(latitude, longitude);

                    if (launcherType == MAPS_LAUNCHER_TYPE.REGISTER.ordinal()) {
                        googleMap.clear();
                        // Show the selected location in map
                        LatLng point = new LatLng(latitude, longitude);
                        markerOptions = new MarkerOptions();
                        markerOptions.position(point);
                        // Adding the marker in the Google Map
                        marker = googleMap.addMarker(markerOptions);
                        // Changing marker icon
//                        marker.setIcon(BitmapDescriptorFactory
//                                .fromResource(R.drawable.icon_pin));
                        googleMap
                                .moveCamera(CameraUpdateFactory.newLatLngZoom(point, 13));
                    } else if (launcherType == MAPS_LAUNCHER_TYPE.SEARCH.ordinal()) {

                    }
                    dismissProgress();
                    break;
            }
        }
    }

    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
        Context mContext;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

        // Finding address using reverse geocoding
        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(mContext);
            double latitude = params[0].latitude;
            double longitude = params[0].longitude;
            List<Address> addresses = null;
            String addressText = "";

//            try {
//                addresses = geocoder.getFromLocation(latitude, longitude, 1);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if (addresses != null && addresses.size() > 0) {
//                Address address = addresses.get(0);
//                addressText = String.format(
//                        "%s, %s, %s,%s",
//                        address.getMaxAddressLineIndex() > 0 ? address
//                                .getAddressLine(0) : "", address.getLocality(),
//                        address.getCountryName(), address.getPostalCode());
//            }

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);

            } catch (IOException ioException) {

//                Utility.showToast(getActivity(),getString(R.string.service_not_avilable));
            } catch (IllegalArgumentException illegalArgumentException) {

//                Utility.showToast(getActivity(),getString(R.string.invalid_lng_and_lat));
            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size()  == 0) {
               // Utility.showToast(getActivity(),getString(R.string.no_address_found));
            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    Log.d("Testing","address.getAddressLine:"+i+ " "+address.getAddressLine(i));
                    addressFragments.add(address.getAddressLine(i));
                }
                addressText = TextUtils.join(System.getProperty("line.separator"),
                        addressFragments);
            }

            return addressText;
        }

        @Override
        protected void onPostExecute(String addressText) {
            googleMap.clear();
            if (latitude != 0 && longitude != 0) {
                // Setting the title for the marker.
                markerOptions.position(new LatLng(latitude, longitude));
                markerOptions.title(addressText);
                // Adding the marker in the Google Map
                marker = googleMap.addMarker(markerOptions);
                //            marker.setIcon(BitmapDescriptorFactory
                //                    .fromResource(R.drawable.icon_pin));
                marker.showInfoWindow();
            }
        }
    }


    public void addMarker(double latitude, double longitude) {
        MarkerOptions options = new MarkerOptions();
        LatLng location = new LatLng(latitude, longitude);
        options.position(location);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_pin_normal));
        Marker marker = googleMap.addMarker(options);
        markersList.add(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
    }

    public void clearMap() {
        googleMap.clear();
        markersList.clear();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mapsOperationsListener.onMarkerClick(marker);
        return true;
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ");
        try {
            googleApiClient.connect();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired");
        try {
            googleApiClient.disconnect();
            Log.d(TAG, "isConnected: " + googleApiClient.isConnected());
        } catch (Exception e) {
            // TODO: handle exception
        }


    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, activity, 0).show();
            return false;
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "****onLocationChanged****");
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d(TAG, "longitude:" + longitude);
        Log.d(TAG, "latitude:" + latitude);
        LatLng latLong = new LatLng(latitude, longitude);
        if (launcherType == MAPS_LAUNCHER_TYPE.REGISTER.ordinal()) {

            googleMap.clear();
            markerOptions = new MarkerOptions();
            if (markerOptions != null) {
                // Setting the title for the marker.
                markerOptions.position(latLong);
                // Adding the marker in the Google Map
                marker = googleMap.addMarker(markerOptions);
                //            marker.setIcon(BitmapDescriptorFactory
                //                    .fromResource(R.drawable.icon_pin));
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 13));
        } else if (launcherType == MAPS_LAUNCHER_TYPE.SEARCH.ordinal()) {
            markerOptions = new MarkerOptions();
            if (markerOptions != null && latLong != null) {
                // Setting the title for the marker.
                markerOptions.position(latLong);
                // Adding the marker in the Google Map
                marker = googleMap.addMarker(markerOptions);
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 13));
        }
    }

    public void stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void startUpdates() {
        try {
            if (googleApiClient.isConnected()) {
                startLocationUpdates();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private void startLocationUpdates() {
        // PendingResult<Status> pendingResult =
        // LocationServices.FusedLocationApi.requestLocationUpdates(
        // mGoogleApiClient, mLocationRequest, this);
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        try {
            if (isLocationAccessDisable) {
                createLocationRequest();
                googleApiClient = new GoogleApiClient.Builder(activity)
                        .addApi(LocationServices.API).addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this).build();
                Log.d(TAG, "onStart fired ");
                try {
                    googleApiClient.connect();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
            if(launcherType == MAPS_LAUNCHER_TYPE.REGISTER.ordinal()) {
                //Clear the searched text
                placesAutoCompleteText.setText("");
                //Start updates
                //TODO:Need to decide the expected behavior,currently once user starts searching we are stopping location updates and showing searched location .
                startUpdates();
            }
            else if(launcherType == MAPS_LAUNCHER_TYPE.SEARCH.ordinal())
            {
                //TODO:Handle search case
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        if (activity instanceof UserLocationActivity) {
            fragmentManager = UserLocationActivity.fragmentManager;
        } else if (activity instanceof JobSeekerDashbordActivity) {
            fragmentManager = JobSeekerDashbordActivity.fragmentManager;
        }
    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }

    public void showSelectedInfoDialog() {
        new ReverseGeocodingTask(activity).execute(new LatLng(latitude,
                longitude));
    }

    public void hideSearchBar() {
        placesAutoCompleteText.setVisibility(View.GONE);
    }

    public void showSearchBar() {
        placesAutoCompleteText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        stopLocationUpdates();
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

    public void reSetLocationMarkers() {
        if (markersList.size() > 0) {
            for (int i = 0; i < markersList.size(); i++) {
                Marker marker = markersList.get(i);
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_not_selected));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(10,10)));
        Log.d(TAG, "onMapReady");
        try {
            googleMap = mMapFragment.getMap();
            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleApiClient.connect();
            if (launcherType == MAPS_LAUNCHER_TYPE.REGISTER.ordinal()) {
                startUpdates();
            } else if (launcherType == MAPS_LAUNCHER_TYPE.SEARCH.ordinal()) {
                googleMap.setOnMarkerClickListener(this);
                mapsOperationsListener.onMapsReady();
                stopLocationUpdates();

            }

        } catch (Exception e) {
            // TODO: handle exception
        }


    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
    }

    private void dismissProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void setAutoComplteTextHintmessage(String hintMsg) {
        placesAutoCompleteText.setHint(hintMsg);
    }

    public String getAutoComplteTextMsg() {
        return placesAutoCompleteText.getText().toString();
    }

}