package com.unorganised.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.activities.JobSeekerDashbordActivity;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.services.SeekerLocationUpdateService;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.Job;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

public class AcceptedJobsListAdapter extends ArrayAdapter<Job> implements OnClickListener, HttpRequestResponse, Constants {

    private ArrayList<Job> jobsList;
    private Activity context;
    private AcceptedJobsListAdapter adaper;
    private UnOrgSharedPreferences sp;
    private Job selectedJob;

    public static class ViewHolder {
        public TextView name;
        public TextView address;
        public TextView type;
        public TextView description;
        public TextView amount;
        public TextView dateTime;
        public Button navigateBtn;
//        public Button cancelJobBtn;
        public Button startJobBtn;
        public LinearLayout callBtn;
        private ImageView profileIcon;
    }

    public ViewHolder viewHolder;

    public AcceptedJobsListAdapter(Activity context, int resource, ArrayList<Job> jobsList) {
        super(context, resource, jobsList);
        this.context = context;
        adaper = this;
        this.jobsList = jobsList;
        sp = UnOrgSharedPreferences
                .getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row =  convertView;
        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.accepted_jobs_list_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) row.findViewById(R.id.provier_name_text_id);
            viewHolder.address = (TextView) row.findViewById(R.id.address_text_id);
            viewHolder.type = (TextView) row.findViewById(R.id.service_type_text_id);
            viewHolder.description = (TextView) row.findViewById(R.id.description_text_id);
            viewHolder.amount = (TextView) row.findViewById(R.id.amount_text_id);
            viewHolder.dateTime = (TextView) row.findViewById(R.id.date_time_text_id);
            viewHolder.navigateBtn = (Button) row.findViewById(R.id.navigate_btn_id);
            viewHolder.profileIcon = (ImageView) row.findViewById(R.id.profile_icon_id);
//            viewHolder.cancelJobBtn = (Button) row.findViewById(R.id.cancel_button_id);
            viewHolder.callBtn = (LinearLayout) row.findViewById(R.id.call_job_provider_btn_id);
            viewHolder.startJobBtn = (Button)row.findViewById(R.id.start_job_button_id);
            row.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder)row.getTag();
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
        viewHolder.navigateBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Starting service to update the user location to server
                context.startService(new Intent(context, SeekerLocationUpdateService.class));
                Utility.showToast(context, "Handle navigate");
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr="
                                + job.getLatitude() + "," + job.getLongitude()));
                context.startActivity(intent);
            }
        });
        viewHolder.callBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + job.getProviderNum()));
                context.startActivity(intent);

            }
        });

//        viewHolder.cancelJobBtn.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
////                selectedJob = job;
////                getInstance().seekerCancelAcceptedJob(adaper, context, job.getId(), sp.getLong(SP_USER_ID_KEY));
//                Utility.showToast(context, "Need to cancel job");
//            }
//        });
        viewHolder.startJobBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                selectedJob = job;
                getInstance().seekerStartJob(adaper, context, job.getId(), sp.getLong(SP_USER_ID_KEY));

            }
        });
        return row;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        switch (dataType) {
            case SEEKER_START_JOB:
                //TODO:Do we need to change icon as "stop job" or finish job
                Utility.showToast(context, "Job started successfully.");
                jobsList.remove(selectedJob);
                notifyDataSetChanged();
                //TODO:Need to decide where to stop service.
                context.stopService(new Intent(context, SeekerLocationUpdateService.class));
                DebugLog.d("SEEKER_START_JOB");
                Intent intent = new Intent(context, JobSeekerDashbordActivity.class);
                intent.putExtra(DRAWER_ITEM_POSITION, 1);
                context.startActivity(intent);
                break;
            default:
                break;
        }

    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        Log.d("Failed", data.toString());
        try {
            Utility.showToast(context, data.getString(EXCEPTION_MSG));
        }
        catch (Exception e)
        {

        }

    }


}
