
package com.unorganised.views;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.activities.ChangePasswordActivity;
import com.unorganised.activities.ExitActivity;
import com.unorganised.activities.LoginActivity;
import com.unorganised.activities.PersonalInfoActivity;
import com.unorganised.activities.RegisterActivity;
import com.unorganised.activities.SeekerInfoActivity;
import com.unorganised.activities.ServiceTypesActivity;
import com.unorganised.activities.SwitchUserActivity;
import com.unorganised.activities.UpdateBankInfoActivity;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.ExitConfirmDialogListener;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

import org.json.JSONObject;

import static com.unorganised.network.HttpReqRespLayer.getInstance;


public class UserSettingsFragment extends Fragment implements HttpRequestResponse, Constants, View.OnClickListener, ExitConfirmDialogListener {
    private int userType;
    private static final String URL_TO_SHARE = "https://play.google.com/store/apps/details?id=com.ideashower.readitlater.pro";
    private String[] InfoSeeker;
    private String[] InfoProvider;
    private String[] InfoBoth;
    private LinearLayout Logout;
    private UnOrgSharedPreferences sharedPref;
    Intent intent;
    // private String typeid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPref = UnOrgSharedPreferences.getInstance(getActivity());
        Utility.stlUserID = sharedPref.getLong(SP_USER_ID_KEY);
        userType = sharedPref.getInt(SP_USER_TYPE);
        View view = inflater.inflate(R.layout.user_settings_fragment, container, false);
        final ListView settingsList = (ListView) view.findViewById(R.id.setting_list);
        Logout = (LinearLayout) view.findViewById(R.id.setting_logout);
        //typeid=getArguments().getString("type");
        InfoBoth = getResources().getStringArray(R.array.settingBoth);
        InfoProvider = getResources().getStringArray(R.array.settingProvider);
        InfoSeeker = getResources().getStringArray(R.array.settingSeeker);
        CustomAdapter adapter;
        if (userType == 3) {
            adapter = new CustomAdapter(getActivity(), InfoBoth);
        } else if (userType == 2) {
            adapter = new CustomAdapter(getActivity(), InfoProvider);
        } else {
            adapter = new CustomAdapter(getActivity(), InfoSeeker);
        }
        settingsList.setAdapter(adapter);
        setListViewHeightBasedOnChildren(settingsList);
        Logout.setOnClickListener(this);
        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = settingsList.getItemAtPosition(position).toString();
                Log.d("item-", item);
                selectionItem(item);

            }
        });
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(getActivity(), UserSettingsFragment.this);
                    exitConfirm.show();
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    public void selectionItem(String item) {
        if (userType == 1 || userType == 2) {
            Log.d("item-", Integer.toString(userType));
            selectionIndividual(item);
        } else {
            Log.d("item-", Integer.toString(userType));

            selectionBoth(item);
        }
    }

    public void selectionBoth(String item) {

        if (item.equals(PERSONAL_INFORMATION)) {
            getInstance().personalInfo(this, getActivity(), Utility.stlUserID);
        } else if (item.equals(CHANGE_PASSWORD)) {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        } else if (item.equals(EDIT_BANK_DETAILS)) {
            Intent i = new Intent(getActivity(), UpdateBankInfoActivity.class);
            startActivity(i);
        } else if (item.equals(ADD_YOUR_ROLE)) {
            Intent i = new Intent(getActivity(), ServiceTypesActivity.class);
            i.putExtra(ROLE_SETTING, true);
            startActivity(i);
        } else if (item.equals(USER_ADDITIONAL_INFORMATION)) {
            //add info
            getInstance().retriveaddDetails(this, getActivity(), Utility.stlUserID);


        }
    }


    public void selectionIndividual(String item) {
        if (item.equals(PERSONAL_INFORMATION)) {
            getInstance().personalInfo(this, getActivity(), Utility.stlUserID);
        } else if (item.equals(CHANGE_PASSWORD)) {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        } else if (item.equals(ADD_YOUR_ROLE)) {
            Intent i = new Intent(getActivity(), ServiceTypesActivity.class);
            i.putExtra(ROLE_SETTING, true);
            startActivity(i);
        } else if (item.equals(EDIT_BANK_DETAILS)) {
            Intent i = new Intent(getActivity(), UpdateBankInfoActivity.class);
            startActivity(i);
        } else if (item.equals(REGISTER_AS)) {
           /* Utility.stnUserType = 3;
            HttpReqRespLayer.getInstance().updateUserType(this, getActivity(), Utility.stlUserID, Utility.stnUserType, "");
            sharedPref.put(SP_USER_TYPE, Utility.stnUserType);*/
            if (userType == 1) {
                Utility.stnUserType = 3;
                HttpReqRespLayer.getInstance().updateUserType(this, getActivity(), Utility.stlUserID, Utility.stnUserType, "");
                sharedPref.put(SP_USER_TYPE, Utility.stnUserType);
            } else if (userType == 2) {
                Intent intent2 = new Intent(getActivity(), RegisterActivity.class);
                intent2.putExtra(USER_SETTINGS, true);
                getActivity().startActivity(intent2);
            }
        } else if (item.equals(USER_ADDITIONAL_INFORMATION)) {
            getInstance().retriveaddDetails(this, getActivity(), Utility.stlUserID);

        }
    }


    class CustomAdapter extends ArrayAdapter<String> {
        public ViewHolder viewHolder;
        private Context context;
        private String[] singleItem;
        LayoutInflater inflater;

        public CustomAdapter(Context context, String[] singleItem) {
            super(context, R.layout.user_setting_view, singleItem);
            this.context = context;
            this.singleItem = singleItem;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            View row = view;
            if (row == null) {
                viewHolder = new ViewHolder();
                row = inflater.inflate(R.layout.user_setting_view, parent, false);
                viewHolder.accountTxt = (TextView) row.findViewById(R.id.single_list_setting);
                row.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) row.getTag();
            }
            viewHolder.accountTxt.setText(singleItem[position]);

            if (userType == 1) {
                if (position == 3) {
                    viewHolder.accountTxt.setText(String.format("%s %s", singleItem[position], getString(R.string.provider)));
                }
            } else if (userType == 2) {
                if (position == 2) {
                    viewHolder.accountTxt.setText(String.format("%s %s", singleItem[position], getString(R.string.Seeker)));
                }
            }
            return row;
        }

        public class ViewHolder {
            private TextView accountTxt;
        }
    }

    @Override
    public void onClick(View v) {
        sharedPref.put(SP_LOGIN_STATUS, false);
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        LinearLayout.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    @Override

    public void onSuccess(JSONObject data, Constants.HttpReqRespActionItems dataType) {
        Log.d("OnSuccess...", data.toString());

        switch (dataType) {
            case PERSONAL_INFO:
                try {

                    String result = data.getString(RESULT_TAG);
                    JSONObject resultJson = new JSONObject(result);
                    String username = resultJson.getString(FULL_NAME_TAG);
                    String email = resultJson.getString(EMAIL_ID_TAG);
                    String mob = resultJson.getString(MOBILE_NUMBER_TAG);
                    double lat = resultJson.getDouble(KEY_LAT);
                    double lng = resultJson.getDouble(KEY_LNG);
                    String addrs = Utility.getAddressFromLocation(getActivity(), lat, lng);
                    intent = new Intent(getActivity(), PersonalInfoActivity.class);
                    intent.putExtra(FULL_NAME_TAG, username);
                    intent.putExtra(MOBILE_NUMBER_TAG, mob);
                    intent.putExtra(EMAIL_ID_TAG, email);
                    intent.putExtra(KEY_ADDRESS, addrs);
                    intent.putExtra(KEY_LAT, lat);
                    intent.putExtra(KEY_LNG, lng);
                    //intent.putExtra("type",typeid);
                    //pass image
                    //intent.putExtra("image",img)
                    if (resultJson.has(USER_PHOTO)) {
                        String encodedImage = resultJson.optString(USER_PHOTO);
                        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                        //Utility.showToast(getActivity(), Integer.toString(decodedString.length));
                        intent.putExtra(USER_PHOTO, decodedString);
                    }
                    startActivity(intent);

                } catch (Exception e) {
                    DebugLog.e(e.toString());
                }
                break;
            case UPDATE_USER_TYPE:

                switch (userType) {
                    case 1:
                        Utility.showToast(getActivity(), getString(R.string.user_added_message));
                        getActivity().startActivity(new Intent(getActivity(), SwitchUserActivity.class));
                        break;
                   /* case 2:
                        Intent intent2 = new Intent(getActivity(), RegisterActivity.class);
                        intent2.putExtra(USER_SETTINGS, true);
                        getActivity().startActivity(intent2);
                        break;*/
                }
                break;

            case SEEKER_RETRIVE_ADD_DETAILS:
                try {
                    String results = data.getString(RESULT_TAG);
                    Intent add = new Intent(getActivity(), SeekerInfoActivity.class);
                    JSONObject resultJson = new JSONObject(results);
                    if (resultJson.has(ADDITIONAL_INFORMATION)) {
                        String details = resultJson.getString(ADDITIONAL_INFORMATION);
                        add.putExtra(DETAILS, details);
                        startActivity(add);
                    } else {
                        startActivity(add);
                    }

                } catch (Exception e) {
                    DebugLog.e(e.toString());
                }
                break;
        }
    }

    @Override
    public void onFailure(JSONObject data, Constants.HttpReqRespActionItems dataType) {

        try {
            Utility.showToast(getActivity(), data.getString(Constants.MESSAGE));
        } catch (Exception e) {

        }
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
}
