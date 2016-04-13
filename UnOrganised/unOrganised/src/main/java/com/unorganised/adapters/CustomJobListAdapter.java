package com.unorganised.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.unorganised.R;
import com.unorganised.activities.JobProviderDashboardActivity;
import com.unorganised.activities.PostJobActivity;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.PendingJobs;
import com.unorganised.util.Utility;

import org.json.JSONObject;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

public class CustomJobListAdapter extends ArrayAdapter<String> implements Constants, HttpRequestResponse {
    private  int nSelectedJobId;
    private int[] nPendingIconId = {R.drawable.icon_waiting_application,
            R.drawable.icon_jobseeker_selected,
            R.drawable.icon_job_pending};
    private RelativeLayout optionItemsLayout;
    private PendingJobs[] pendingJobs;
    private ViewHolder viewHolder;
    private Activity activity;
    private PendingJobs selPendingJob;
    private View rowView;

    public CustomJobListAdapter(Activity activity, PendingJobs[] pendingJobs, String[] jobTitle, RelativeLayout optionItemsLayout) {
        super(activity, R.layout.provider_pending_jobs, jobTitle);
        this.pendingJobs = pendingJobs;
        this.activity = activity;
        this.optionItemsLayout = optionItemsLayout;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        rowView = convertView;
        if (rowView == null) {
            rowView = View.inflate(activity,R.layout.provider_pending_jobs, null);
            viewHolder = new ViewHolder();
            viewHolder.pendingIconImg = (ImageView) rowView.findViewById(R.id.pending_icon_id);
            viewHolder.optionIconLayout = (LinearLayout) rowView.findViewById(R.id.option_icon_layout);
            viewHolder.jobTitleText = (TextView) rowView.findViewById(R.id.job_header_id);
            viewHolder.addressTitleText = (TextView) rowView.findViewById(R.id.address_text_id);
            viewHolder.jobDescriptionTitleText = (TextView) rowView.findViewById(R.id.job_description_id);
            viewHolder.priceTitleText = (TextView) rowView.findViewById(R.id.price_title_id);
            viewHolder.dateTitleText = (TextView) rowView.findViewById(R.id.date_title_id);
            viewHolder.responseTitleText = (TextView) rowView.findViewById(R.id.no_response_title_id);
            viewHolder.responseTxt=(TextView)rowView.findViewById(R.id.responseId);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        selPendingJob = pendingJobs[position];
        viewHolder.jobTitleText.setText(selPendingJob.getJobTitle());
        viewHolder.addressTitleText.setText(Utility.getAddressFromLocation(getContext(), selPendingJob.getLatitude(), selPendingJob.getLongitude()));
        viewHolder.jobDescriptionTitleText.setText(selPendingJob.getJobDescription());
        viewHolder.priceTitleText.setText(String.format("Price: Rs.%s", selPendingJob.getPrice()));
        viewHolder.dateTitleText.setText(String.format("Date: %s", selPendingJob.getDate()));
        viewHolder.responseTitleText.setText(selPendingJob.getResponses());

        if (selPendingJob.getResponseCount() == 0) {
            viewHolder.responseTitleText.setTextColor(activity.getResources().getColor(R.color.gray));
            viewHolder.pendingIconImg.setImageResource(nPendingIconId[0]);
            viewHolder.optionIconLayout.setVisibility(View.VISIBLE);
            //Delete and Edit job
            viewHolder.optionIconLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    optionItemsLayout.setVisibility(View.VISIBLE);
                    optionItemsLayout.findViewById(R.id.close_btn_id).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            optionItemsLayout.setVisibility(View.GONE);
                        }
                    });

                    //Edit Job
                    (optionItemsLayout.findViewById(R.id.edit_job_layout)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PendingJobs pendJob = pendingJobs[position];
                            Intent intent = new Intent(activity, PostJobActivity.class);
                            intent.putExtra(EDIT_JOB, true);
                            intent.putExtra(JOB_ID, pendJob.getJobId());
                            PendingJobs pendingJob = new PendingJobs(pendJob.getServiceDes(), pendJob.getSubServiceDes(),
                                    pendJob.getLatitude(), pendJob.getLongitude(), pendJob.getJobDescription(),
                                    pendJob.getPrice(), pendJob.getServiceId(), pendJob.getSubServiceId());
                            intent.putExtra(PENDING_JOB_OBJ, pendingJob);
                            activity.startActivity(intent);
                        }
                    });
                    //Delete Job
                    (optionItemsLayout.findViewById(R.id.delete_job_layout)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            createDeleteDialog();
                            nSelectedJobId = pendingJobs[position].getJobId();
                        }
                    });
                }
            });
        } else {
            viewHolder.responseTxt.setVisibility(View.VISIBLE);
            viewHolder.responseTxt.setText(Integer.toString(selPendingJob.getResponseCount()));
            viewHolder.pendingIconImg.setImageResource(nPendingIconId[2]);
            viewHolder.responseTitleText.setTextColor(activity.getResources().getColor(R.color.green));
            viewHolder.optionIconLayout.setVisibility(View.GONE);
        }
        return rowView;
    }

    private void createDeleteDialog() {
        final Dialog deleteAlert = new Dialog(activity);
        deleteAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deleteAlert.setContentView(R.layout.delete_dialog);
        deleteAlert.show();
        Button delete = (Button) deleteAlert.findViewById(R.id.delete_btn_id);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlert.dismiss();
                getInstance().deleteJob(CustomJobListAdapter.this, activity, nSelectedJobId);
            }
        });
        Button cancel = (Button) deleteAlert.findViewById(R.id.cancel_btn_id);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionItemsLayout.setVisibility(View.GONE);
                deleteAlert.dismiss();
            }
        });

        deleteAlert.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    deleteAlert.dismiss();
                }
                return true;
            }
        });
    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        switch (dataType) {
            case DELETE_JOB:
                activity.startActivity(new Intent(activity, JobProviderDashboardActivity.class));
                break;
        }
    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {

    }

    public class ViewHolder {
        private ImageView pendingIconImg;
        private TextView jobTitleText;
        private TextView addressTitleText;
        private TextView jobDescriptionTitleText;
        private TextView priceTitleText;
        private TextView dateTitleText;
        private TextView responseTitleText;
        private LinearLayout optionIconLayout;
        private ImageView closeImg;
        private TextView responseTxt;
    }

}
