package com.unorganised.views;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.activities.ExitActivity;
import com.unorganised.activities.JobProviderDashboardActivity;
import com.unorganised.activities.PostJobActivity;
import com.unorganised.adapters.CustomJobListAdapter;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.ExitConfirmDialogListener;
import com.unorganised.util.JobsParser;
import com.unorganised.util.PendingJobs;
import com.unorganised.util.PendingJobsParser;
import com.unorganised.util.SeekerData;
import com.unorganised.util.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.unorganised.network.HttpReqRespLayer.*;


public class PendingJobsFragment extends Fragment implements Constants, HttpRequestResponse, ExitConfirmDialogListener {

    private boolean bPendingJobList;
    //Pending job list
    private int nJobListLength;
    private String[] jobTitle;
    private RelativeLayout layoutPendingJobs;
    private ListView jobList;
    private CustomJobListAdapter adapter;
    //Post job Button
    private RelativeLayout layoutBtn;
    private Button postJob;

    //Pending jobs selected
    private int newPrice;
    private LinearLayout layoutTotalApps;
    private RelativeLayout optionItemsLayout;
    private PendingJobs selectedPendingJob;
    private ArrayList<PendingJobs> pendingJobs;
    private ArrayList<SeekerData> seekerJobList;
    private TextView priceText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pending_job_fragment, container, false);
        layoutBtn = (RelativeLayout) view.findViewById(R.id.post_btn_layout);
        postJob = (Button) view.findViewById(R.id.post_job_btn_id);
        layoutPendingJobs = (RelativeLayout) view.findViewById(R.id.pending_jobs_layout);
        getInstance().pendingJobs(this, getActivity(), Utility.stlUserID);
        layoutTotalApps = (LinearLayout) view.findViewById(R.id.selected_total_job_layout);
        optionItemsLayout = (RelativeLayout) view.findViewById(R.id.provider_option_layout);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (bPendingJobList){
                        ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(getActivity(), PendingJobsFragment.this);
                        exitConfirm.show();
                    } else {
                        showPendingJobList();
                    }
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    private void fillPendingJobData() {
        //Create list
        PendingJobs[] pendingJobsObj = new PendingJobs[nJobListLength];
        jobTitle = new String[nJobListLength];

        if (nJobListLength > 0) {
            for (int i = 0; i < nJobListLength; i++) {
                pendingJobsObj[i] = pendingJobs.get(i);
                jobTitle[i] = pendingJobsObj[i].getJobTitle();
            }
            adapter = new CustomJobListAdapter(getActivity(), pendingJobsObj, jobTitle, optionItemsLayout);
            jobList.setAdapter(adapter);
            layoutPendingJobs.setVisibility(View.VISIBLE);
            layoutBtn.setVisibility(View.GONE);
            layoutTotalApps.setVisibility(View.GONE);
            Utility.bPendingJobs = true;
        }
    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        Log.d("OnSuccess, Json", data.toString());
        switch (dataType) {
            case PENDING_JOBS:
                PendingJobsParser jsonParser = new PendingJobsParser();
                pendingJobs = jsonParser.parsePendingJobs(data);
                showPendingJobList();
                break;
            case JOB_DETAIL:
                bPendingJobList = false;
                JobsParser jobsParser = new JobsParser();
                seekerJobList = jobsParser.parseSeekerJobList(data);
                showTotalApplications();
                break;
            case ASSIGN_JOB:
                Utility.showToast(getActivity(),getString(R.string.job_assignd_successfully));
                Intent intent = new Intent(getActivity(), JobProviderDashboardActivity.class);
                intent.putExtra(DRAWER_ITEM_POSITION, 2);
                startActivity(intent);
                break;
            case EDIT_BIDDING:
                priceText.setText(String.format("Price: Rs.%s.00", newPrice));
                break;
        }
    }

    private void showTotalApplications() {
        if (seekerJobList.size() > 0) {
            ListView listSeekers = (ListView) layoutTotalApps.findViewById(R.id.total_app_list_id);
            listSeekers.setAdapter(new CustomSeekerListAdapter(getActivity(), seekerJobList, jobTitle));
            layoutTotalApps.setVisibility(View.VISIBLE);
        } else {
            Utility.showToast(getActivity(), getString(R.string.no_response_to_show));
        }
    }

    private void showPendingJobList() {
        if (pendingJobs!= null && pendingJobs.size() > 0) {
            bPendingJobList = true;

            if (pendingJobs != null) {
                nJobListLength = pendingJobs.size();
                jobList = (ListView) layoutPendingJobs.findViewById(R.id.pending_jobs_list_id);
                jobList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (pendingJobs.get(position).getResponseCount() > 0) {
                            getInstance().getJobDetails(PendingJobsFragment.this, getActivity(), pendingJobs.get(position).getJobId());
                            parent.removeViewInLayout(view);
                            adapter.notifyDataSetChanged();
                            setSelectedJobData(view, pendingJobs.get(position));
                        }
                    }
                });
                fillPendingJobData();
            }else {
                Utility.showToast(getActivity(), getString(R.string.no_jobs_to_show));
            }
        } else {
            layoutTotalApps.setVisibility(View.GONE);
            layoutPendingJobs.setVisibility(View.GONE);
            postJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PostJobActivity.class);
                    intent.putExtra(EDIT_JOB, false);
                    intent.putExtra(JOB_ID, 0);
                    getActivity().startActivityForResult(intent, 2);
                }
            });
            layoutBtn.setVisibility(View.VISIBLE);
            Utility.bPendingJobs = false;
            bPendingJobList = true;
        }
        JobProviderDashboardActivity.addPostButton(0);
    }

    private void setSelectedJobData(View view, PendingJobs pendingJobs) {
        selectedPendingJob = pendingJobs;
        layoutPendingJobs.setVisibility(View.GONE);
        layoutBtn.setVisibility(View.GONE);
        LinearLayout layout = (LinearLayout) layoutTotalApps.findViewById(R.id.selected_job_layout);
        TextView totalApp = (TextView) layoutTotalApps.findViewById(R.id.total_app_id);
        totalApp.setText(String.format("Total Applications (%d)", pendingJobs.getResponseCount()));
        priceText = (TextView) view.findViewById(R.id.price_title_id);
        Button increaseBid = (Button) view.findViewById(R.id.increase_bid_btn_id);
        increaseBid.setVisibility(View.VISIBLE);
        increaseBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBiddingAmtDialog(selectedPendingJob.getJobId());
            }
        });
        view.findViewById(R.id.no_response_title_id).setVisibility(View.GONE);
        layout.removeAllViews();
        layout.addView(view);
    }

    private void createBiddingAmtDialog(final int jobId){
        final Dialog biddingDialog = new Dialog(getActivity());
        biddingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        biddingDialog.setContentView(R.layout.bidding_dialog);
        biddingDialog.show();
        final String priceval = priceText.getText().toString();
        final String price = priceval.substring(priceval.indexOf(".") + 1, priceval.indexOf(".0"));
        final EditText priceTextVal = (EditText) biddingDialog.findViewById(R.id.price_text_id);
        priceTextVal.setText(price);
        (biddingDialog.findViewById(R.id.ok_button_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPrice = Integer.parseInt(priceTextVal.getText().toString());
                int oldPrice = Integer.parseInt(price);

                if (newPrice < oldPrice){
                    Utility.showToast(getActivity(), getString(R.string.increase_bid_amount_error));
                } else {
                    getInstance().increaseBidding(PendingJobsFragment.this, getActivity(), jobId, Utility.stlUserID, newPrice);
                    biddingDialog.dismiss();
                }
            }
        });
    }


    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        try {
            Utility.showToast(getActivity(), data.getString(Constants.EXCEPTION_MSG));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onExitConfirmed() {
        Intent intent = new Intent(getActivity(),
                ExitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NO_ANIMATION
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        startActivity(intent);
        getActivity().finish();
//        getActivity().moveTaskToBack(true);
//        getActivity().finish();
    }

    public class CustomSeekerListAdapter extends ArrayAdapter<SeekerData> {
        ArrayList<SeekerData> seekerDataList;

        public class ViewHolder {
            public ImageView seekerIcon;
            public TextView jobHeader;
            public TextView address;
            public TextView ratingText;
            public RatingBar ratingBar;
            public Button assigntBtn;
            public ImageView callerImg;

        }

        private ViewHolder viewHolder;

        public CustomSeekerListAdapter(Context context, ArrayList<SeekerData> seekerDatas, String[] jobTitle) {
            super(context, R.layout.provider_total_app_list_row, seekerDatas);
            seekerDataList = seekerDatas;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                rowView = inflater.inflate(R.layout.provider_total_app_list_row, null, true);
                viewHolder = new ViewHolder();
                viewHolder.seekerIcon = (ImageView) rowView.findViewById(R.id.seeker_icon_id);
                viewHolder.jobHeader = (TextView) rowView.findViewById(R.id.job_header_id);
                viewHolder.address = (TextView) rowView.findViewById(R.id.address_text_id);
                viewHolder.ratingText = (TextView) rowView.findViewById(R.id.rating_text_id);
                viewHolder.ratingBar = (RatingBar) rowView.findViewById(R.id.ratingBar_id);
                viewHolder.assigntBtn = (Button) rowView.findViewById(R.id.accept_btn_id);
                viewHolder.callerImg = (ImageView) rowView.findViewById(R.id.call_btn_id);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }
            LayerDrawable stars = (LayerDrawable) viewHolder.ratingBar.getProgressDrawable();
            final SeekerData seekerData = getItem(position);
            stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
            if (seekerData.getImageData() != null){
                viewHolder.seekerIcon.setImageBitmap(seekerData.getImageData());
            }
            viewHolder.jobHeader.setText(seekerData.getFullName());
            viewHolder.address.setText(Utility.getAddressFromLocation(getActivity(), seekerData.getSeekerlat(), seekerData.getSeekerlng()));
            viewHolder.ratingText.setText(String.format("( %d Reviews)", seekerData.getTotalRating()));
            viewHolder.ratingBar.setRating(seekerDataList.get(position).getRating());
            //Assign Button
            viewHolder.assigntBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.bPendingJobs = false;
                    getInstance().assignSeeker(PendingJobsFragment.this, getActivity(), seekerDataList.get(position).getJobId(), seekerDataList.get(position).getSeekerId());
                }
            });
            //Call button
            viewHolder.callerImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String numberToDial = "tel:+91" + seekerDataList.get(position).getMobileNumber();
                    Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse(numberToDial));
                    startActivity(intentCall);
                }
            });
            return rowView;
        }
    }


    private void createPaymentDialog() {
        Dialog paymentAlert = new Dialog(getActivity());
        paymentAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        paymentAlert.setContentView(R.layout.payment_dialog);
        paymentAlert.show();
    }

}

