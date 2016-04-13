package com.unorganised.views;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.activities.ExitActivity;
import com.unorganised.activities.JobProviderDashboardActivity;
import com.unorganised.activities.ShowLocationActivity;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.AssignedJobs;
import com.unorganised.util.Constants;
import com.unorganised.util.ExitConfirmDialogListener;
import com.unorganised.util.PendingJobsParser;
import com.unorganised.util.Utility;
import org.json.JSONObject;
import java.util.ArrayList;
import static com.unorganised.network.HttpReqRespLayer.getInstance;

public class AssignedJobFragment extends Fragment implements Constants, HttpRequestResponse, ExitConfirmDialogListener {
    private boolean bAssignedJobList;
    private int nJobListLength;
    private ListView jobListView;
    private ArrayList<AssignedJobs> assignedJobList;
    private LinearLayout jobListLayout;
    private LinearLayout layoutAcceptedApp;
    private LinearLayout acceptedLayout;
    private CustomJobListAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assigned_job_fragment, container,false);
        jobListView = (ListView)view.findViewById(R.id.assigned_list_id);
        getInstance().assignedJobs(this, getActivity(), Utility.stlUserID);
        jobListLayout = (LinearLayout)view.findViewById(R.id.job_list_layout);
        layoutAcceptedApp = (LinearLayout) view.findViewById(R.id.accepted_job_main_layout);
        acceptedLayout = (LinearLayout)view.findViewById(R.id.accepted_job_layout);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (bAssignedJobList){
                        ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(getActivity(), AssignedJobFragment.this);
                        exitConfirm.show();
                    } else {
                        jobListLayout.setVisibility(View.VISIBLE);
                        layoutAcceptedApp.setVisibility(View.GONE);
                        showAssignedJobList();
                    }
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        Log.d("OnSuccess..", data.toString());
        switch (dataType){
            case ASSIGNED_JOB:
                PendingJobsParser parser = new PendingJobsParser();
                assignedJobList = parser.parseAssignedJobs(data);
                showAssignedJobList();
                break;
            case PROVIDER_START_JOB: {
                Intent intent = new Intent(getActivity(), JobProviderDashboardActivity.class);
                intent.putExtra(DRAWER_ITEM_POSITION, 1);
                getActivity().startActivity(intent);
            }
            break;
            case PROVIDER_CANCEL_ASSIGN_JOB: {
                Intent intent = new Intent(getActivity(), JobProviderDashboardActivity.class);
                intent.putExtra(DRAWER_ITEM_POSITION, 0);
                getActivity().startActivity(intent);
            }
            break;
        }
    }

    private void showAssignedJobList() {
        bAssignedJobList = true;

        if (assignedJobList != null && assignedJobList.size() > 0) {
            nJobListLength = assignedJobList.size();
            //Create list
            AssignedJobs[] assignedJobs = new AssignedJobs[nJobListLength];
            String[] jobTitle = new String[nJobListLength];

            for (int i = 0; i < nJobListLength; i++) {
                assignedJobs[i] = assignedJobList.get(i);
                jobTitle[i] = assignedJobs[i].getDescription();
            }
            adapter = new CustomJobListAdapter(getActivity(), assignedJobs, jobTitle);
            jobListView.setAdapter(adapter);
        } else {
                Utility.showToast(getActivity(),getString(R.string.no_job_assigned));
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

    public class CustomJobListAdapter extends ArrayAdapter<String>{
        public class ViewHolder {
            private ImageView pendingIconImg;
            private TextView jobTitleText;
            private TextView addressTitleText;
            private TextView jobDescriptionTitleText;
            private TextView priceTitleText;
            private TextView dateTitleText;
            private LinearLayout jobStatusLayoutChild;
            private LinearLayout jobStatusLayout;
            private TextView responseTitleText;
            private TextView seekerName;
            private ImageView callSeeker;
            private ImageView locateSeeker;
            private LinearLayout callSeekerLayout;
            private LinearLayout locteseekerLayout;
        }

        private AssignedJobs selAsgndJob;
        private AssignedJobs[] assignedJobs;
        public ViewHolder viewHolder;
        private View rowView;

        public CustomJobListAdapter(Context context, AssignedJobs[] assignedJobs, String[] jobTitle) {
            super(context, R.layout.provider_pending_jobs, jobTitle);
            this.assignedJobs = assignedJobs;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            rowView = convertView;

            if (rowView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                rowView = inflater.inflate(R.layout.provider_pending_jobs, null, true);
                viewHolder = new ViewHolder();
                viewHolder.pendingIconImg = (ImageView) rowView.findViewById(R.id.pending_icon_id);
                viewHolder.jobTitleText = (TextView) rowView.findViewById(R.id.job_header_id);
                viewHolder.addressTitleText = (TextView) rowView.findViewById(R.id.address_text_id);
                viewHolder.jobDescriptionTitleText = (TextView) rowView.findViewById(R.id.job_description_id);
                viewHolder.priceTitleText = (TextView) rowView.findViewById(R.id.price_title_id);
                viewHolder.dateTitleText = (TextView) rowView.findViewById(R.id.date_title_id);
                viewHolder.jobStatusLayout = (LinearLayout) rowView.findViewById(R.id.job_status_layout);
                viewHolder.responseTitleText = (TextView) viewHolder.jobStatusLayout.findViewById(R.id.no_response_title_id);
                viewHolder.jobStatusLayoutChild = (LinearLayout) rowView.findViewById(R.id.accepted_for_layout);
                viewHolder.seekerName = (TextView) rowView.findViewById(R.id.accepted_name_id);
                viewHolder.callSeeker = (ImageView) rowView.findViewById(R.id.call_btn_id);
                viewHolder.locateSeeker = (ImageView) rowView.findViewById(R.id.locate_seeker_id);
                viewHolder.callSeekerLayout=(LinearLayout)rowView.findViewById(R.id.call_btn_layout);
                viewHolder.locteseekerLayout=(LinearLayout)rowView.findViewById(R.id.locator_btn_layout);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }
            viewHolder.jobStatusLayoutChild.setVisibility(View.VISIBLE);
            viewHolder.responseTitleText.setVisibility(View.GONE);
            selAsgndJob = assignedJobs[position];
            viewHolder.jobTitleText.setText(selAsgndJob.getServiceDes());
            viewHolder.addressTitleText.setText(Utility.getAddressFromLocation(getContext(), selAsgndJob.getLatitude(), selAsgndJob.getLongitude()));
            viewHolder.jobDescriptionTitleText.setText(selAsgndJob.getDescription());
            viewHolder.priceTitleText.setText(String.format("Price: Rs.%s", selAsgndJob.getBiddingAmount()));
            viewHolder.dateTitleText.setText(String.format("Date: %s", selAsgndJob.getCreatedDate()));
            viewHolder.pendingIconImg.setImageResource(R.drawable.icon_jobseeker_selected);
            viewHolder.seekerName.setText(selAsgndJob.getSeekerName());
            ImageView imgIcon = (ImageView)rowView.findViewById(R.id.profile_icon_id);

            if(selAsgndJob.getSeekerIcon() != null){
                imgIcon.setImageBitmap(selAsgndJob.getSeekerIcon());
            }
            final int index = position;
            viewHolder.callSeekerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String numberToDial = String.format("tel:+91%s", selAsgndJob.getSeekerMobileNumber());
                    Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse(numberToDial));
                    startActivity(intentCall);
                }
            });
            viewHolder.locteseekerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent locationActivity = new Intent(getActivity(), ShowLocationActivity.class);
                        locationActivity.putExtra(KEY_LOCATION_DISPLAY_LAUNCHER_TYPE, LOCATION_DISPLAY_LAUNCHER_TYPE.SEEKER);
                        locationActivity.putExtra(SEEKER_ID_TAG, selAsgndJob.getSeekerId());
                        locationActivity.putExtra(JOB_LAT, selAsgndJob.getLatitude());
                        locationActivity.putExtra(JOB_LNG, selAsgndJob.getLongitude());
                        startActivity(locationActivity);
                }
            });

            viewHolder.jobStatusLayoutChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAssingedSeeker(position);
                }
            });
            return rowView;
        }

        private void showAssingedSeeker(int position) {
            bAssignedJobList = false;
            jobListLayout.setVisibility(View.GONE);
            layoutAcceptedApp.setVisibility(View.VISIBLE);
            LinearLayout acceptedJobLayout = (LinearLayout)layoutAcceptedApp.findViewById(R.id.sel_job_layout);
            View selView = jobListView.getChildAt(position);
            jobListView.removeViewInLayout(selView);
            acceptedJobLayout.removeAllViews();
            acceptedJobLayout.addView(selView);
            TextView totalApp = (TextView) layoutAcceptedApp.findViewById(R.id.total_app_id);
            totalApp.setText(getResources().getString(R.string.accepted_apps));
            View view = View.inflate(getContext(), R.layout.provider_total_app_list_row, null);
            ((TextView) view.findViewById(R.id.job_header_id)).setText(selAsgndJob.getSeekerName());
            ((TextView) view.findViewById(R.id.address_text_id)).setText(selAsgndJob.getSeekerAddress());
            ((TextView) view.findViewById(R.id.subscribed_count_id)).setText(selAsgndJob.getSubscribedCount() + " ");
            ((RatingBar) view.findViewById(R.id.ratingBar_id)).setRating(Float.parseFloat(selAsgndJob.getRating()));
            ((TextView) view.findViewById(R.id.rating_text_id)).setText(String.format("(%s Ratings)", selAsgndJob.getTotalRating()));

            if (selAsgndJob.getSeekerIcon() != null){
                ((ImageView) view.findViewById(R.id.seeker_icon_id)).setImageBitmap(selAsgndJob.getSeekerIcon());
            }
            Button startBtn = (Button) view.findViewById(R.id.start_btn_id);
            Button cancelBtn = (Button) view.findViewById(R.id.cancel_start_btn_id);
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.showToast(getActivity(), getString(R.string.job_started));
                    getInstance().providerStartJob(AssignedJobFragment.this, getActivity(), selAsgndJob.getJobId(), Utility.stlUserID, Utility.getFormattedTime());
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.showToast(getActivity(),getString(R.string.job_canceld));
                    getInstance().providerCancelAssignedJob(AssignedJobFragment.this, getActivity(), selAsgndJob.getJobId(), selAsgndJob.getSeekerId());
                }
            });
            selView.findViewById(R.id.accepted_for_layout).setVisibility(View.GONE);
            ImageView locateSeeker = (ImageView)view.findViewById(R.id.locate_seeker_id);
            locateSeeker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Locate seeker
                    Intent locationActivity = new Intent(getActivity(), ShowLocationActivity.class);
                    locationActivity.putExtra(KEY_LOCATION_DISPLAY_LAUNCHER_TYPE, LOCATION_DISPLAY_LAUNCHER_TYPE.SEEKER);
                    locationActivity.putExtra(SEEKER_ID_TAG, selAsgndJob.getSeekerId());
                    locationActivity.putExtra(JOB_LAT, selAsgndJob.getLatitude());
                    locationActivity.putExtra(JOB_LNG, selAsgndJob.getLongitude());
                    startActivity(locationActivity);
                }
            });
            ImageView callSeeker = (ImageView)view.findViewById(R.id.call_btn_id);
            callSeeker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String numberToDial = String.format("tel:+91%s", selAsgndJob.getSeekerMobileNumber());
                    Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse(numberToDial));
                    startActivity(intentCall);
                }
            });
            view.findViewById(R.id.accept_btn_id).setVisibility(View.GONE);
            startBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
            locateSeeker.setVisibility(View.VISIBLE);
            acceptedLayout.addView(view);
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
}
