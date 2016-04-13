package com.unorganised.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.Job;
import com.unorganised.util.JobsParser;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

import org.json.JSONObject;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

/**
 * Created by anarra on 19/12/15.
 */
public class JobDetailsActivity extends Activity implements HttpRequestResponse,View.OnClickListener, Constants{
    private TextView title;
    private TextView serviceName;
    private TextView subServiceTypeName;
    private TextView locationName;
    private TextView jobDesc;
    private TextView price;
    private TextView dateTime;
    private TextView ratingCountView;
    private RatingBar userReview;
    private Button applyBtn;
    private Button cancelBtn;
    private ImageView locationImagView;
    private ImageView callImage;
    private ImageView profileIcon;
    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long jobId = (int) getIntent().getLongExtra(Constants.JOB_ID, -1);

        if (jobId == -1) {
            Utility.showToast(this,getString(R.string.invalid_jobid));
            return;
        }
        setContentView(R.layout.job_detatils_activity);
        HttpReqRespLayer.getInstance().getJobDetails(this, this, jobId);
        //Job job = Utility.getJobDetails(1);
        //job.setDescription("need help");
        title = (TextView) findViewById(R.id.title_id);
        serviceName = (TextView) findViewById(R.id.service_name_id);
        subServiceTypeName = (TextView) findViewById(R.id.sub_service_name_id);
        locationName = (TextView) findViewById(R.id.location_id);
        jobDesc = (TextView) findViewById(R.id.description);
        price = (TextView) findViewById(R.id.price);
        dateTime = (TextView) findViewById(R.id.date_time);
        userReview = (RatingBar) findViewById(R.id.user_rating_bar_id);
        ratingCountView = (TextView)findViewById(R.id.number_of_ratings_id);
        applyBtn = (Button)findViewById(R.id.apply_btn_id);
        cancelBtn  = (Button)findViewById(R.id.cancel_button_id);
        locationImagView = (ImageView)findViewById(R.id.job_location_btn_id);
        callImage = (ImageView)findViewById(R.id.call_job_provider_btn_id);
        profileIcon = (ImageView)findViewById(R.id.profile_icon_id);
        applyBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        callImage.setOnClickListener(this);
    }

    @Override
    public void onSuccess(JSONObject data, Constants.HttpReqRespActionItems dataType) {
        DebugLog.d("****onSuccess****"+data);
        switch (dataType) {
            case JOB_DETAIL:
                JobsParser parser = new JobsParser();
                job = parser.parseJob(data);
                if (job != null) {
                    serviceName.setText(job.getServiceDesc());
                    if (job.getSubServiceDesc().length() > 0) {
                        subServiceTypeName.setVisibility(View.VISIBLE);
                        subServiceTypeName.setText(job.getSubServiceDesc());
                    }
                    locationName.setText(job.getAddress());
                    jobDesc.setText(job.getDescription());
                    price.setText(String.format("%s%s", getResources().getString(R.string.rs_text), job.getBiddingPrice()));
                    dateTime.setText(job.getCreatedDate());
                    title.setText(job.getProviderName());
                    userReview.setRating(job.getRating());
                    ratingCountView.setText(String.format(" %d(Reviews)", job.getTotalRating()));

                    if (job.getProfileImage() != null) {
                        profileIcon.setImageBitmap(job.getProfileImage());
                    }
                    int status = job.getStatus();

                    if (status == JOB_STATUS_APPLIED) {
                        applyBtn.setVisibility(View.GONE);
                        cancelBtn.setVisibility(View.VISIBLE);
                    } else if (status == JOB_STATUS_NOT_APPLIED) {
                        applyBtn.setVisibility(View.VISIBLE);
                        cancelBtn.setVisibility(View.GONE);
                    }
                } else {
                    Utility.showToast(this, getString(R.string.no_jobs_to_show));
                }
                break;
            case APPLY_JOB:
                applyBtn.setVisibility(View.GONE);
                cancelBtn.setVisibility(View.VISIBLE);
                Utility.showToast(this, getResources().getString(R.string.job_applied_success_msg));
                locationImagView.setVisibility(View.VISIBLE);
                break;
            case SEEKER_CANCEL_APPLY_JOB:
                applyBtn.setVisibility(View.VISIBLE);
                cancelBtn.setVisibility(View.GONE);
                Utility.showToast(this, getResources().getString(R.string.job_canceled_success));
                break;
        }
    }

    @Override
    public void onFailure(JSONObject data, Constants.HttpReqRespActionItems dataType) {
        DebugLog.d("****onFailure****"+data);
        try {
            Utility.showToast(this, data.getString(Constants.EXCEPTION_MSG));
        }
        catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apply_btn_id:
                UnOrgSharedPreferences sp = UnOrgSharedPreferences.getInstance(this);
                if (job != null)
                    HttpReqRespLayer.getInstance().applyJob(this, this, job.getId(), sp.getLong(Constants.SP_USER_ID_KEY));
                break;
            case R.id.cancel_button_id:
                if (job != null)
                    getInstance().cancelAppliedJob(this, this, job.getId(), Utility.stlUserID);
                break;
            case R.id.call_job_provider_btn_id:
                String numberToDial = String.format("tel:+91%s", job.getProviderNum());
                Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse(numberToDial));
                startActivity(intentCall);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
