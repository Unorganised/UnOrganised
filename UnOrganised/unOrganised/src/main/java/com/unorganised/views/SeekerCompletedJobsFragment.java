package com.unorganised.views;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.activities.ExitActivity;
import com.unorganised.activities.JobSeekerDashbordActivity;
import com.unorganised.adapters.SeekerCompletedJobsListAdapter;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.ExitConfirmDialogListener;
import com.unorganised.util.JobsParser;
import com.unorganised.util.Provider;
import com.unorganised.util.SeekerData;
import com.unorganised.util.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

public class SeekerCompletedJobsFragment extends Fragment implements Constants, HttpRequestResponse, ExitConfirmDialogListener {

    private ListView completedJobList;
    private LinearLayout completeJobDetailLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.seeker_completed_jobs_fragment, container, false);
        completedJobList = (ListView) view.findViewById(R.id.completed_job_list);
        getInstance().getSeekerCompletedJobs(SeekerCompletedJobsFragment.this, getActivity(), Utility.stlUserID);
        completeJobDetailLayout = (LinearLayout) view.findViewById(R.id.complete_jod_detail_layout);
        completeJobDetailLayout.setVisibility(View.GONE);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(getActivity(), SeekerCompletedJobsFragment.this);
                    exitConfirm.show();
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        switch (dataType) {
            case SEEKER_COMPLETED_JOBS:
                DebugLog.d("data:" + data);
                JobsParser parser = new JobsParser();
                ArrayList<Provider> providerList = parser.parseSeekerCompletedJobList(data);
                if (providerList.size() > 0) {
                    Utility.isCompletedJob = true;
                    Utility.completedjobCount=providerList.size();
                    SeekerCompletedJobsListAdapter adapter = new SeekerCompletedJobsListAdapter(getActivity(), providerList);
                    completedJobList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } else {
                    Utility.showToast(getActivity(), getString(R.string.no_jobs_to_show));
                    Utility.isCompletedJob = false;
                }
                if (providerList.size() > 1) {
                    Log.d("sizeS=",Integer.toString(providerList.size()));
                    JobSeekerDashbordActivity.addDeleteButton(3);
                }
                break;
        }
    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        try {
            Utility.showToast(getActivity(), data.getString(Constants.EXCEPTION_MSG));
        } catch (Exception e) {
            e.printStackTrace();
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
