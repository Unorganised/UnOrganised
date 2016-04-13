package com.unorganised.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.activities.JobProviderDashboardActivity;
import com.unorganised.activities.JobSeekerDashbordActivity;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.Provider;
import com.unorganised.util.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

/**
 * Created by anarra on 10/01/16.
 */
public class SeekerCompletedJobsListAdapter extends ArrayAdapter<Provider> implements HttpRequestResponse,Constants{

    ArrayList<Provider> completedJobs;
    private Activity activity;



    public class ViewHolder {
        public ImageView seekerIcon;
        public TextView fullName;
        public TextView address;
        public TextView jobDesc;
        public TextView date;
        public TextView price;
        private ImageView deleteJob;

    }

    private ViewHolder viewHolder;
    public SeekerCompletedJobsListAdapter(Activity activity,ArrayList<Provider> providersList)
    {
        super(activity, R.layout.seeker_completed_job_list_row,providersList);
        this.completedJobs = providersList;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.seeker_completed_job_list_row, null, true);
            viewHolder = new ViewHolder();
            viewHolder.seekerIcon = (ImageView) rowView.findViewById(R.id.profile_icon_id);
            viewHolder.jobDesc = (TextView) rowView.findViewById(R.id.job_description_id);
            viewHolder.address = (TextView) rowView.findViewById(R.id.job_address_id);
            viewHolder.fullName = (TextView) rowView.findViewById(R.id.provider_name_id);
            viewHolder.price = (TextView) rowView.findViewById(R.id.price_id);
            viewHolder.date = (TextView) rowView.findViewById(R.id.date_id);
            viewHolder.deleteJob=(ImageView)rowView.findViewById(R.id.delete_job);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        final Provider provider = completedJobs.get(position);
        if(provider.getProfileIcon() != null){
            viewHolder.seekerIcon.setImageBitmap(provider.getProfileIcon());
        }
        viewHolder.fullName.setText(provider.getName());
        viewHolder.address.setText(provider.getAddress());
        viewHolder.jobDesc.setText(provider.getJobDesc());
        viewHolder.date.setText(provider.getDate());
        viewHolder.price.setText("Rs." + provider.getBiddingAmt());
        //Utility.strCompleteJobId=""+provider.getJobId();
        viewHolder.deleteJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int jobid=provider.getJobId();
               createDeleteDialog(jobid);
               // getInstance().deleteSeekerCompletedJob(SeekerCompletedJobsListAdapter.this, activity, Utility.stlUserID, "" + provider.getJobId());
            }
        });
        return rowView;
    }
    private void createDeleteDialog(final int jobId) {
        final Dialog deleteAlert = new Dialog(activity);
        deleteAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deleteAlert.setContentView(R.layout.delete_dialog);
        deleteAlert.show();
        TextView deleteTxt=(TextView)deleteAlert.findViewById(R.id.delete_text_id);
        deleteTxt.setText("Are you sure want to delete completed job");
        Button delete = (Button) deleteAlert.findViewById(R.id.delete_btn_id);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInstance().deleteSeekerCompletedJob(SeekerCompletedJobsListAdapter.this, activity, Utility.stlUserID, "" + jobId);
            }
        });
        Button cancel = (Button) deleteAlert.findViewById(R.id.cancel_btn_id);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //optionItemsLayout.setVisibility(View.GONE);
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
    public void onSuccess(JSONObject data, Constants.HttpReqRespActionItems dataType) {
switch (dataType)
{
    case DELETE_SEEKER_COMPLETED_JOB:
//
        Intent intent = new Intent(activity, JobSeekerDashbordActivity.class);
        intent.putExtra(DRAWER_ITEM_POSITION, 3);
        JobSeekerDashbordActivity.addDeleteButton(3);
        activity.startActivity(intent);
        break;
}
    }

    @Override
    public void onFailure(JSONObject data, Constants.HttpReqRespActionItems dataType) {

    }
}
