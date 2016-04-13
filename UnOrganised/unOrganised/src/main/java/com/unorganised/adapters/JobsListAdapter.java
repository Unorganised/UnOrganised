package com.unorganised.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.unorganised.R;
import com.unorganised.activities.JobSeekerDashbordActivity;
import com.unorganised.interfaces.JobOperations;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.Job;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

import org.json.JSONObject;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

public class JobsListAdapter extends ArrayAdapter<Job> implements OnClickListener, HttpRequestResponse, Constants {

    private ArrayList<Job> jobsList;
    private JobOperations jobOperationsListener;
    private Activity context;
    private JobsListAdapter adapter;
    private Job selectedJob ;

    public static class ViewHolder {
        private TextView name;
        private TextView address;
        private TextView type;
        private TextView description;
        private TextView amount;
        private TextView dateTime;
        private Button requestJob;
        private Button cancelJobBtn;
        private ImageButton callBtn;
        private ImageButton locationBtn;
        private ImageView profileIcon;
    }

    public ViewHolder viewHolder;

    public JobsListAdapter(JobOperations jobOperationsListener, Activity context, int resource, ArrayList<Job> jobsList) {
        super(context, resource, jobsList);
        this.jobOperationsListener = jobOperationsListener;
        this.context = context;
        adapter = this;
        this.jobsList = jobsList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.search_job_list_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.provier_name_text_id);
            viewHolder.address = (TextView) view.findViewById(R.id.address_text_id);
            viewHolder.type = (TextView) view.findViewById(R.id.service_type_text_id);
            viewHolder.description = (TextView) view.findViewById(R.id.description_text_id);
            viewHolder.amount = (TextView) view.findViewById(R.id.amount_text_id);
            viewHolder.dateTime = (TextView) view.findViewById(R.id.date_time_text_id);
            viewHolder.profileIcon = (ImageView)view.findViewById(R.id.profile_icon_id);
            viewHolder.requestJob = (Button) view.findViewById(R.id.apply_job_btn_id);
            viewHolder.cancelJobBtn = (Button) view.findViewById(R.id.cancel_button_id);
            viewHolder.callBtn = (ImageButton) view.findViewById(R.id.call_job_provider_btn_id);
            viewHolder.locationBtn = (ImageButton) view.findViewById(R.id.job_location_btn_id);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();

        }
        final Job job = getItem(position);
        viewHolder.name.setText(job.getProviderName());
        viewHolder.address.setText(job.getAddress());
        viewHolder.type.setText(job.getServiceDesc());
        viewHolder.description.setText(job.getDescription());
        viewHolder.amount.setText(context.getResources().getString(R.string.rs_text) + String.valueOf(job.getBiddingPrice()));
        viewHolder.dateTime.setText(job.getCreatedDate());

        if(job.getProfileImage() != null){
            viewHolder.profileIcon.setImageBitmap(job.getProfileImage());
        }
        viewHolder.requestJob.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //jobOperationsListener.onJobRequested(job);
                UnOrgSharedPreferences sp = UnOrgSharedPreferences.getInstance(context);
                selectedJob = job;
                getInstance().applyJob(adapter, context, job.getId(), sp.getLong(SP_USER_ID_KEY));

            }
        });
        viewHolder.callBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + job.getProviderNum()));
                context.startActivity(intent);

            }
        });

        viewHolder.locationBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                selectedJob = job;
                Utility.showToast(context, "Need to implement location showing");
//                Intent intent =  new Intent(context, ShowLocationActivity.class);
//                intent.putExtra(KEY_LOCATION_DISPLAY_LAUNCHER_TYPE,LOCATION_DISPLAY_LAUNCHER_TYPE.PROVIDER);
//                intent.putExtra(KEY_LAT,job.getLatitude());
//                intent.putExtra(KEY_LNG,job.getLongitude());
//                context.startActivity(intent);

            }
        });
        viewHolder.cancelJobBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
            selectedJob = job;
            getInstance().cancelAppliedJob(adapter, context, job.getId(), Utility.stlUserID);

            }
        });
        int status = job.getStatus();
        if (status == 0) {
            viewHolder.requestJob.setVisibility(View.VISIBLE);
            viewHolder.cancelJobBtn.setVisibility(View.GONE);
        }
        else if (status == 1) {
            viewHolder.requestJob.setVisibility(View.GONE);
            viewHolder.cancelJobBtn.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onSuccess(JSONObject data, Constants.HttpReqRespActionItems dataType) {
        DebugLog.d("onSuccess:" + data);
        switch (dataType) {
            case APPLY_JOB:
                Utility.showToast(context, context.getResources().getString(R.string.job_applied_success_msg));
                selectedJob.setStatus(1);
                notifyDataSetChanged();
                break;
            case SEEKER_CANCEL_APPLY_JOB:
                Utility.showToast(context, "Job Canceled");
                selectedJob.setStatus(0);
                notifyDataSetChanged();
                break;
        }

    }

    @Override
    public void onFailure(JSONObject data, Constants.HttpReqRespActionItems dataType) {
        Log.d("Failed", data.toString());
        try
        {
            Toast.makeText(context, data.getString(Constants.EXCEPTION_MSG), Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
        }

    }


}
