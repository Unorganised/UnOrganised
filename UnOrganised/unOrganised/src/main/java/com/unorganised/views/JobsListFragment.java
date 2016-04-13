package com.unorganised.views;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.adapters.JobsListAdapter;
import com.unorganised.interfaces.JobOperations;
import com.unorganised.util.Job;

@SuppressLint("ValidFragment")
public class JobsListFragment extends Fragment implements JobOperations {

    private ListView jobsListView;
    private JobsListAdapter jobsListAdapter;
    private ArrayList<Job> jobsList;
    private TextView numOfJobsCount;

    public JobsListFragment(ArrayList<Job> jobsList) {
        this.jobsList = jobsList;
    }

    public JobsListFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.jobs_list_fragment, container, false);
        jobsListView = (ListView) view.findViewById(R.id.jobs_list_view);
        numOfJobsCount = (TextView) view.findViewById(R.id.jobs_count_id);
        numOfJobsCount.setText(jobsList.size() + " Jobs Available");
        jobsListAdapter = new JobsListAdapter(this, getActivity(), R.layout.search_job_list_row, jobsList);
        jobsListView.setAdapter(jobsListAdapter);
        jobsListAdapter.notifyDataSetChanged();
        return view;
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

    ;

}
