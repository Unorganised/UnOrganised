package com.unorganised.views;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.activities.ExitActivity;
import com.unorganised.activities.JobProviderDashboardActivity;
import com.unorganised.activities.JobSeekerDashbordActivity;
import com.unorganised.util.Constants;
import com.unorganised.util.ExitConfirmDialogListener;

import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;


/**
 * Created by Dell on 2/15/2016.
 */
public class HelpFragment extends Fragment implements Constants, ExitConfirmDialogListener {
    private int userType;
    private Button backBtn;
    UnOrgSharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.help_fragment, container, false);
        sharedPref = UnOrgSharedPreferences.getInstance(getActivity());
        userType = sharedPref.getInt(USER_TYPE_KEY);
        backBtn = (Button) view.findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(getActivity(), HelpFragment.this);
                exitConfirm.show();
            }
        });
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(getActivity(), HelpFragment.this);
                    exitConfirm.show();
                    return true;
                }
                return false;
            }
        });
        return view;
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
