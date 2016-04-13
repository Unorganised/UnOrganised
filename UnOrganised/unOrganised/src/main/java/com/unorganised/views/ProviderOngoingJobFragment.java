package com.unorganised.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.activities.ExitActivity;
import com.unorganised.activities.JobProviderDashboardActivity;
import com.unorganised.activities.PaymentGatewayActivity;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.AssignedJobs;
import com.unorganised.util.Constants;
import com.unorganised.util.ExitConfirmDialogListener;
import com.unorganised.util.PendingJobsParser;
import com.unorganised.util.Utility;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

public class ProviderOngoingJobFragment extends android.app.Fragment implements Constants, HttpRequestResponse, ExitConfirmDialogListener {

    private int nJobId;
    private String strDuration;
    private int nBiddingAmt;
    private float fRating;
    private String strCompletedDate;
    private String[] jobTitle;
    private ListView ongoingJobListView;
    private String seekerMailId;
    private String jobDescription;
    private String paidAmount;
    private String jobProviderName;
    private String jobProviderMobile;
    private String seeker;
    private ArrayList<AssignedJobs> ongoingJobList;
    private String jobProviderMail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.provider_ongoing_job_fragment, container,false);
        getInstance().providerOngoingJobs(this, getActivity(), Utility.stlUserID);
        ongoingJobListView = (ListView) view.findViewById(R.id.ongoing_job_list);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(getActivity(), ProviderOngoingJobFragment.this);
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
        Log.d("OnSuccess", data.toString());
        switch (dataType){
            case PROVIDER_ONGOING_JOB:
                PendingJobsParser parser = new PendingJobsParser();
                ongoingJobList = parser.parseAssignedJobs(data);
                showOngoingJobList();
                break;
            case PROVIDER_FINISH_JOB: {

                sendMail();
//                Intent intent = new Intent(getActivity(), JobProviderDashboardActivity.class);
//                intent.putExtra(DRAWER_ITEM_POSITION, 3);
//                getActivity().startActivity(intent);
            }
            break;
            case PROVIDER_CANCEL_JOB: {
                Intent intent = new Intent(getActivity(), JobProviderDashboardActivity.class);
                intent.putExtra(DRAWER_ITEM_POSITION, 2);
                getActivity().startActivity(intent);
            }
            break;
        }
    }

    private void sendMail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
       // i.putExtra(Intent.EXTRA_EMAIL, new String[]{"anusha.chat@gmail.com"});
        i.putExtra(Intent.EXTRA_EMAIL,new String[] { seekerMailId });

        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.payment_confirmation));

        i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(new StringBuilder().append("<p>Hi&nbsp;" + seeker + "</p><br>&nbsp;&nbsp;<b>Payment Description</b><br><br>&nbsp;&nbsp;Job Description : "
                + jobDescription + "<br>Provider Name : " + jobProviderName + "<br>Paid Amount : " + paidAmount ).toString()));
        try {
            startActivityForResult((Intent.createChooser(i, getString(R.string.choose_email))), 1);
        } catch (android.content.ActivityNotFoundException ex) {
            Utility.showToast(getActivity(), getString(R.string.no_email_client_installed));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 ){
            Intent intent = new Intent(getActivity(), JobProviderDashboardActivity.class);
            intent.putExtra(DRAWER_ITEM_POSITION, 3);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        try {
            Utility.showToast(getActivity(), data.getString(Constants.EXCEPTION_MSG));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showOngoingJobList() {
        if (ongoingJobList != null  && ongoingJobList.size() > 0) {
            int jobListLength = ongoingJobList.size();
            jobTitle = new String[jobListLength];
            //Create list
            AssignedJobs[] ongoingJobs = new AssignedJobs[jobListLength];

            for (int i = 0; i < jobListLength; i++) {
                ongoingJobs[i] = ongoingJobList.get(i);
                jobTitle[i] = ongoingJobList.get(i).getSeekerName();
            }
            CustomJobListAdapter adapter = new CustomJobListAdapter(getActivity(), ongoingJobs, jobTitle);
            ongoingJobListView.setAdapter(adapter);
        } else {
            Utility.showToast(getActivity(), getString(R.string.no_jobs_to_show));
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

    private class CustomJobListAdapter extends ArrayAdapter<String>{
        private class ViewHolder {
            private TextView addressTitleText;
            private TextView seekerName;
            private ImageView callSeeker;
            private ImageView seekerIcon;
//            private ImageView locateSeeker;
            private Button stopButton;
            private TextView verifiedText;
            private Button assignButton;
            private Button cancelButton;
        }
        private AssignedJobs[] ongoingJobs;
        private ViewHolder viewHolder;


        public CustomJobListAdapter(Context context, AssignedJobs[] ongoingJobs, String[] jobTitle) {
            super(context, R.layout.provider_total_app_list_row, jobTitle);
            this.ongoingJobs = ongoingJobs;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            if (rowView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                rowView = inflater.inflate(R.layout.provider_total_app_list_row, null, true);
                viewHolder = new ViewHolder();
                viewHolder.seekerName = (TextView) rowView.findViewById(R.id.job_header_id);
                viewHolder.addressTitleText = (TextView) rowView.findViewById(R.id.address_text_id);
                viewHolder.verifiedText = (TextView) rowView.findViewById(R.id.verified_id);
                viewHolder.callSeeker = (ImageView) rowView.findViewById(R.id.call_btn_id);
//                viewHolder.locateSeeker = (ImageView) rowView.findViewById(R.id.locate_seeker_id);
                viewHolder.seekerIcon = (ImageView) rowView.findViewById(R.id.seeker_icon_id);
                viewHolder.stopButton = (Button) rowView.findViewById(R.id.stop_btn_id);
                viewHolder.assignButton = (Button) rowView.findViewById(R.id.accept_btn_id);
                viewHolder.cancelButton = (Button) rowView.findViewById(R.id.cancel_btn_id);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }
            viewHolder.assignButton.setVisibility(View.GONE);
            viewHolder.stopButton.setVisibility(View.VISIBLE);
            viewHolder.cancelButton.setVisibility(View.VISIBLE);
            viewHolder.callSeeker.setVisibility(View.GONE);
//            viewHolder.locateSeeker.setVisibility(View.VISIBLE);
            viewHolder.seekerName.setText(ongoingJobs[position].getSeekerName());
            viewHolder.addressTitleText.setText(ongoingJobs[position].getSeekerAddress());

            if (ongoingJobs[position].getSeekerIcon() != null){
                viewHolder.seekerIcon.setImageBitmap(ongoingJobs[position].getSeekerIcon());
            }

            viewHolder.stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nJobId = ongoingJobs[position].getJobId();
                    seekerMailId = ongoingJobs[position].getSeekerEmailId();
                    jobDescription = ongoingJobs[position].getDescription();
                    paidAmount = ongoingJobs[position].getBiddingAmount();
                    jobProviderName = ongoingJobs[position].getProviderName();
                    jobProviderMobile = ongoingJobs[position].getProviderMobileNo();
                    jobProviderMail = ongoingJobs[position].getProviderMailId();
                    seeker = ongoingJobs[position].getSeekerName();
                    strCompletedDate = Utility.getFormattedTime();
                    String amt = ongoingJobs[position].getBiddingAmount();
                    nBiddingAmt = Integer.parseInt(amt.substring(0, amt.indexOf(".")));
                    strDuration = Utility.getJobDuration(ongoingJobs[position].getStartDate());
                    createRateDialog();
                }
            });
            viewHolder.cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.showToast(getActivity(), getString(R.string.cancel_start_job_error));
                    getInstance().cancelProviderJob(ProviderOngoingJobFragment.this, getActivity(), ongoingJobs[position].getJobId(), Utility.stlUserID);
                }
            });
            return rowView;
        }
    }

    private void createRateDialog() {
        final Dialog rateAlert = new Dialog(getActivity());
        rateAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        rateAlert.setContentView(R.layout.rate_dialog);
        rateAlert.show();
        final RatingBar ratingBar = (RatingBar) rateAlert.findViewById(R.id.ratingBar);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
        rateAlert.findViewById(R.id.ok_button_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Payment option
                fRating = ratingBar.getRating();
                rateAlert.dismiss();
//                Intent intent = new Intent(getActivity(), PaymentGatewayActivity.class);
//                intent.putExtra(JOB_ID, nJobId);
//                intent.putExtra(COMPLETED_DATE, strCompletedDate);
//                intent.putExtra(DURATION, strDuration);
//                intent.putExtra(SEEKER_RATING, fRating);
//                intent.putExtra(JOB_BIDDING_AMT, nBiddingAmt);
//                intent.putExtra(PROVIDER_FULL_NAME, jobProviderName);
//                intent.putExtra(PROVIDER_MOBILE_NUM, jobProviderMobile);
//                intent.putExtra(PROVIDER_MAIL_ID, jobProviderMail);
//                getActivity().startActivity(intent);
                getInstance().finishProviderJob(ProviderOngoingJobFragment.this, getActivity(), nJobId, strCompletedDate, strDuration, fRating);
            }
        });
    }
}

