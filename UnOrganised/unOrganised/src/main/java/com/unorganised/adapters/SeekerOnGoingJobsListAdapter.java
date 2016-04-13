package com.unorganised.adapters;

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
import android.widget.RatingBar;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.activities.JobSeekerDashbordActivity;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.services.SeekerLocationUpdateService;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.Job;
import com.unorganised.util.RatingDialogListener;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;
import com.unorganised.views.RatingDialog;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

public class SeekerOnGoingJobsListAdapter extends ArrayAdapter<Job> implements OnClickListener, HttpRequestResponse, Constants ,RatingDialogListener {

    private ArrayList<Job> jobsList;
    private Activity context;
    private SeekerOnGoingJobsListAdapter adaper;
    private UnOrgSharedPreferences sp;
    private Job selectedJob;

    public static class ViewHolder {
        public TextView name;
        public TextView address;
        public TextView type;
        public TextView description;
        public TextView amount;
        public TextView dateTime;
        public Button finishBtn;
        public RatingBar providerRating;
        public TextView ratingCountView;
        public Button cancelBtn;
        private ImageView profileIcon;
    }

    public ViewHolder viewHolder;

    public SeekerOnGoingJobsListAdapter(Activity context, int resource, ArrayList<Job> jobsList) {
        super(context, resource, jobsList);
        this.context = context;
        adaper = this;
        this.jobsList = jobsList;
        sp = UnOrgSharedPreferences.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.seeker_on_going_jobs_list_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) row.findViewById(R.id.provier_name_text_id);
            viewHolder.address = (TextView) row.findViewById(R.id.address_text_id);
            viewHolder.type = (TextView) row.findViewById(R.id.service_type_text_id);
            viewHolder.description = (TextView) row.findViewById(R.id.description_text_id);
            viewHolder.amount = (TextView) row.findViewById(R.id.amount_text_id);
            viewHolder.dateTime = (TextView) row.findViewById(R.id.date_time_text_id);
            viewHolder.finishBtn = (Button) row.findViewById(R.id.finish_btn_id);
            viewHolder.cancelBtn = (Button) row.findViewById(R.id.cancel_btn_id);
            viewHolder.providerRating = (RatingBar)row.findViewById(R.id.provider_ratingbar_id);
            viewHolder.ratingCountView = (TextView)row.findViewById(R.id.prvider_rating_count_id);
            viewHolder.profileIcon = (ImageView)row.findViewById(R.id.provider_profile_id);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }
        final Job job = getItem(position);
        viewHolder.name.setText(job.getProviderName());
        viewHolder.address.setText(job.getAddress());
        viewHolder.type.setText(job.getServiceDesc());
        viewHolder.description.setText(job.getDescription());
        viewHolder.amount.setText(String.format("%s%s", context.getResources().getString(R.string.rs_text), String.valueOf(job.getBiddingPrice())));
        viewHolder.dateTime.setText(job.getCreatedDate());
        if (job.getProfileImage() != null){
            viewHolder.profileIcon.setImageBitmap(job.getProfileImage());
        }
        viewHolder.finishBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Utility.showToast(context, "Finish job");
                RatingDialog ratingDialog = new RatingDialog(context, SeekerOnGoingJobsListAdapter.this);
                ratingDialog.show();
                selectedJob = job;
            }
        });
        viewHolder.cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Utility.showToast(context,"Cancel job");
                selectedJob = job;
                getInstance().seekerCancelStartJob(SeekerOnGoingJobsListAdapter.this, context, job.getId(), Utility.stlUserID);
            }
        });
        return row;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        int itemPosition = 0;
        switch (dataType) {
            case SEEKER_FINISH_JOB:
                Utility.showToast(context, "Job finished successfully.");
                jobsList.remove(selectedJob);
                notifyDataSetChanged();
                itemPosition = 3;
                break;
            case SEEKER_CANCEL_START_JOB:
                Utility.showToast(context, "Job is Canceled");
                selectedJob.setStatus(0);
                itemPosition = 2;
                break;
            default:
                break;
        }
        DebugLog.d("SEEKER_FINISH_JOB");
        Intent intent = new Intent(context, JobSeekerDashbordActivity.class);
        intent.putExtra(DRAWER_ITEM_POSITION, itemPosition);
        context.startActivity(intent);

    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        Log.d("Failed", data.toString());
        try {
            Utility.showToast(context, data.getString(EXCEPTION_MSG));
        } catch (Exception e) {

        }

    }
    @Override
    public void onRatingConfirmned(float rating) {
        getInstance().seeketFinishJob(this, context,selectedJob.getId(),
                UnOrgSharedPreferences.getInstance(context).getLong(Constants.SP_USER_ID_KEY),rating);
    }


}
