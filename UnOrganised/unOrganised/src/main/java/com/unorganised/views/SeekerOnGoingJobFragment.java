package com.unorganised.views;

import com.unorganised.R;
import com.unorganised.activities.ExitActivity;
import com.unorganised.adapters.AcceptedJobsListAdapter;
import com.unorganised.adapters.SeekerOnGoingJobsListAdapter;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.ExitConfirmDialogListener;
import com.unorganised.util.Job;
import com.unorganised.util.JobsParser;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

public class SeekerOnGoingJobFragment extends Fragment implements HttpRequestResponse, ExitConfirmDialogListener {
    private ListView jobsListView;
    private SeekerOnGoingJobsListAdapter jobsListAdapter;
    private ArrayList<Job> jobsList;
    private TextView numOfJobsCount;

    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.jobs_list_fragment, container, false);
        jobsListView = (ListView) view.findViewById(R.id.jobs_list_view);
        numOfJobsCount = (TextView) view.findViewById(R.id.jobs_count_id);
        UnOrgSharedPreferences sp = UnOrgSharedPreferences.getInstance(getActivity());
        long userID = sp.getLong(Constants.SP_USER_ID_KEY);
        HttpReqRespLayer.getInstance().getSeekerOnGoingJobs(this, getActivity(), userID);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(getActivity(), SeekerOnGoingJobFragment.this);
                    exitConfirm.show();
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onSuccess(JSONObject data, Constants.HttpReqRespActionItems dataType) {

        DebugLog.d("onSuccess:" + data);
        JobsParser parser = new JobsParser();
        jobsList = parser.parseAcceptedJobs(data);
        if (jobsList != null && jobsList.size() > 0) {
            numOfJobsCount.setText(jobsList.size() + " Jobs is running currently.");
            jobsListAdapter = new SeekerOnGoingJobsListAdapter(getActivity(), R.layout.seeker_on_going_jobs_list_row, jobsList);
            jobsListView.setAdapter(jobsListAdapter);
        } else {
            //TODO:Need to add no jobs found text in layout
            Utility.showToast(getActivity(), getString(R.string.no_jobs_to_show));
        }
    }

    @Override
    public void onFailure(JSONObject data, Constants.HttpReqRespActionItems dataType) {
        DebugLog.d("onFailure:" + data);
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
