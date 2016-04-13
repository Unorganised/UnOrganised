package com.unorganised.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.activities.ExitActivity;
import com.unorganised.activities.JobProviderDashboardActivity;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.ExitConfirmDialogListener;
import com.unorganised.util.JobsParser;
import com.unorganised.util.SeekerData;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

public class CompletedJobFragment extends Fragment implements Constants, HttpRequestResponse, View.OnClickListener, ExitConfirmDialogListener {

    private boolean bJobselected;
    private int nSelectedJobIndex;
    private int nSelDate;
    private String strSelDate;
    private String[] strSubTypes;
    private ListView completedJobList;
    private ArrayList<SeekerData> completedJobs;
    private LinearLayout completeJobDetailLayout;
    private Dialog subscribeDialog;
    private Button subscribeBtn;
    private DatePickerDialog datePicker;
    private TextView frmDateText;
    private TextView toDateText;
    private ImageView deleteJob;
    private Spinner subTypeList;
    private UnOrgSharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.provider_completed_job_fragment, container, false);
        completedJobList = (ListView) view.findViewById(R.id.completed_job_list);
        getInstance().getCompletedJobs(CompletedJobFragment.this, getActivity(), Utility.stlUserID);
        completeJobDetailLayout = (LinearLayout) view.findViewById(R.id.complete_jod_detail_layout);
        completeJobDetailLayout.setVisibility(View.GONE);
        strSubTypes = getResources().getStringArray(R.array.str_subTypes);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    if (bJobselected) {
                        showJobDetailsList();
                        completedJobList.setVisibility(View.VISIBLE);
                        completeJobDetailLayout.setVisibility(View.GONE);
                        bJobselected = false;
                        return true;
                    } else {
                        ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(getActivity(), CompletedJobFragment.this);
                        exitConfirm.show();
                        return true;
                    }
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        Log.d("OnSucess", data.toString());
        switch (dataType) {
            case PROVIDER_COMPLETED_JOB:
                JobsParser jobsParser = new JobsParser();
                completedJobs = jobsParser.parseCompletedJobList(data);
                showJobDetailsList();
                break;
            case JOB_SUBSCRIBE:
                Utility.showToast(getActivity(), getString(R.string.subscribe_success_message));
                completedJobs.get(nSelectedJobIndex).setSubscriptionStatus(1);
                subscribeDialog.dismiss();
                break;
            case JOB_UNSUBSCRIBE:
                Utility.showToast(getActivity(), getString(R.string.un_subscribe_success_message));
                completedJobs.get(nSelectedJobIndex).setSubscriptionStatus(0);
                break;
            case DELETE_COMPLETED_JOB:
//                getInstance().getCompletedJobs(CompletedJobFragment.this, getActivity(), Utility.stlUserID);
                Intent intent = new Intent(getActivity(), JobProviderDashboardActivity.class);
                intent.putExtra(DRAWER_ITEM_POSITION, 3);
                JobProviderDashboardActivity.addDeleteButton(3);
                startActivity(intent);
                break;
        }
    }

    private void showJobDetailsList() {
        if (completedJobs != null && completedJobs.size() > 0) {
            completedJobList.setAdapter(new CustomSeekerListAdapter(getActivity(), completedJobs));
            bJobselected = false;
            Utility.isCompletedJob = true;
            Utility.completedjobCount=completedJobs.size();
        } else {
            Utility.isCompletedJob = false;
            Utility.showToast(getActivity(), getString(R.string.no_jobs_to_show));
        }
        if (completedJobs.size() > 1) {
            Log.d("size=",Integer.toString(completedJobs.size()));
            JobProviderDashboardActivity.addDeleteButton(3);
        }
    }

    private void showJobDetails(int position) {
        completedJobList.setVisibility(View.GONE);
        completeJobDetailLayout.setVisibility(View.VISIBLE);
        View newView = View.inflate(getActivity(), R.layout.completed_job_details, null);
        ((TextView) newView.findViewById(R.id.seeker_name_id)).setText(completedJobs.get(position).getFullName());
        ((TextView) newView.findViewById(R.id.seeker_address_id)).setText(Utility.getAddressFromLocation(getActivity(), completedJobs.get(position).getLatitude(), completedJobs.get(position).getLongitude()));
        ((TextView) newView.findViewById(R.id.job_title_id)).setText(completedJobs.get(position).getJobTitle());
        ((TextView) newView.findViewById(R.id.date_id)).setText(completedJobs.get(position).getDate());
        ((TextView) newView.findViewById(R.id.price_id)).setText(String.format("₹%s", completedJobs.get(position).getBiddingPrice()));
        ((TextView) newView.findViewById(R.id.charged_text_id)).setText(completedJobs.get(position).getPaymentType());

        if (completedJobs.get(position).getImageData() != null) {
            ((ImageView) newView.findViewById(R.id.profile_icon_id)).setImageBitmap(completedJobs.get(position).getImageData());
        }
        subscribeBtn = (Button) newView.findViewById(R.id.subscribe_btn_id);
        if (completedJobs.get(position).getSubscriptionStatus() != 0) {
            subscribeBtn.setText(getString(R.string.unsubscribe_text));
            subscribeBtn.setBackgroundResource(R.drawable.btn_red);
        }
        subscribeBtn.setOnClickListener(this);
        ((TextView) newView.findViewById(R.id.charged_text_id)).setText(completedJobs.get(position).getPaymentType());
        ((RatingBar) newView.findViewById(R.id.rating_bar_id)).setRating(completedJobs.get(position).getRating());
        completeJobDetailLayout.removeAllViews();
        completeJobDetailLayout.addView(newView);
    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {
        try {
            Utility.showToast(getActivity(), data.getString(Constants.EXCEPTION_MSG));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.subscribe_btn_id:
                if (subscribeBtn.getText().equals(getString(R.string.subscribe))) {
                    createSubscriptionDialog();
                    subscribeBtn.setText(getString(R.string.unsubscribe_text));
                    subscribeBtn.setBackgroundResource(R.drawable.btn_red);
                } else {
                    getInstance().unSubscribe(this, getActivity(), completedJobs.get(nSelectedJobIndex).getJobId(), Utility.stlUserID, completedJobs.get(nSelectedJobIndex).getSeekerId());
                    subscribeBtn.setText(getString(R.string.subscribe));
                    subscribeBtn.setBackgroundResource(R.drawable.button_selector);
                }
                break;
            case R.id.ok_button_id:
                String frmDate = frmDateText.getText().toString();
                String toDate = toDateText.getText().toString();
                int frequency = subTypeList.getSelectedItemPosition() + 1;

                if (validate(frmDate, toDate)) {
                    getInstance().subscribe(this, getActivity(), completedJobs.get(nSelectedJobIndex).getJobId(), Utility.stlUserID,
                            completedJobs.get(nSelectedJobIndex).getSeekerId(), frmDate, toDate, frequency);
                }
                break;
            case R.id.cancel_button_id:
                subscribeDialog.dismiss();
                break;
            case R.id.from_date_id:
                datePicker.show();
                nSelDate = 1;
                break;
            case R.id.to_date_id:
                datePicker.show();
                nSelDate = 2;
                break;
        }
    }

    private boolean validate(String frmDate, String toDate) {
        Log.d("Selected item", subTypeList.getSelectedItem().toString());
        if (frmDate.equals("")) {
            Utility.showToast(getActivity(), getActivity().getString(R.string.enter_from_date_error));
            return false;
        } else if (toDate.equals("")) {
            Utility.showToast(getActivity(), getActivity().getString(R.string.enter_to_date_error));
            return false;
        } else if (subTypeList.getSelectedItem().toString().equals(getActivity().getString(R.string.select_sub_type))) {
            Utility.showToast(getActivity(), getActivity().getString(R.string.select_frequency_error));
            return false;
        } else {
            return true;
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

    private class CustomSeekerListAdapter extends ArrayAdapter<SeekerData> {
        ArrayList<SeekerData> completedJobs;

        public class ViewHolder {
            private ImageView seekerIcon;
            private TextView jobHeader;
            private TextView fullName;
            private TextView date;
            private TextView price;
            private TextView address;
            private Button detailBtn;
            private ImageView deleteJob;
        }

        private ViewHolder viewHolder;

        public CustomSeekerListAdapter(Context context, ArrayList<SeekerData> completedJobs) {
            super(context, R.layout.provider_completed_job_list_row, completedJobs);
            this.completedJobs = completedJobs;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                rowView = inflater.inflate(R.layout.provider_completed_job_list_row, null, true);
                viewHolder = new ViewHolder();
                viewHolder.seekerIcon = (ImageView) rowView.findViewById(R.id.profile_icon_id);
                viewHolder.address = (TextView) rowView.findViewById(R.id.seeker_address_id);
                viewHolder.jobHeader = (TextView) rowView.findViewById(R.id.job_title_id);
                viewHolder.fullName = (TextView) rowView.findViewById(R.id.seeker_name_id);
                viewHolder.price = (TextView) rowView.findViewById(R.id.price_id);
                viewHolder.date = (TextView) rowView.findViewById(R.id.date_id);
                viewHolder.detailBtn = (Button) rowView.findViewById(R.id.detail_btn_id);
                viewHolder.deleteJob = (ImageView) rowView.findViewById(R.id.delete_job);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }
            final SeekerData completedJob = completedJobs.get(position);

            if (completedJob.getImageData() != null) {
                viewHolder.seekerIcon.setImageBitmap(completedJob.getImageData());
            }
            viewHolder.fullName.setText(completedJob.getFullName());
            viewHolder.address.setText(Utility.getAddressFromLocation(getContext(), completedJob.getLatitude(), completedJob.getLongitude()));
            viewHolder.jobHeader.setText(completedJob.getJobTitle());
            viewHolder.date.setText(completedJob.getDate());
            viewHolder.price.setText(String.format("₹ %s", completedJob.getBiddingPrice()));
            viewHolder.detailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bJobselected = true;
                    nSelectedJobIndex = position;
                    showJobDetails(position);
                }
            });
            // Utility.strCompleteJobId=""+completedJob.getJobId();
            //sp.put(COMPLETED_JOB_ID_COUNT,Utility.strCompleteJobId);
            viewHolder.deleteJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int Jobid = completedJob.getJobId();
                    createDeleteDialog(Jobid);
                    // getInstance().deleteProviderCompletedJob(CompletedJobFragment.this, getActivity(), Utility.stlUserID, "" + completedJob.getJobId());
                }
            });
            return rowView;
        }
    }

    private void createDeleteDialog(final int jobId) {
        final Dialog deleteAlert = new Dialog(getActivity());
        deleteAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deleteAlert.setContentView(R.layout.delete_dialog);
        TextView deleteTxt = (TextView) deleteAlert.findViewById(R.id.delete_text_id);
        deleteTxt.setText(getResources().getString(R.string.delete_completed_job));
        deleteAlert.show();
        Button delete = (Button) deleteAlert.findViewById(R.id.delete_btn_id);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInstance().deleteProviderCompletedJob(CompletedJobFragment.this, getActivity(), Utility.stlUserID, "" + jobId);
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

    private void createSubscriptionDialog() {
        subscribeDialog = new Dialog(getActivity());
        subscribeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        subscribeDialog.setContentView(R.layout.subscribe_dialog);
        subscribeDialog.show();
        (subscribeDialog.findViewById(R.id.ok_button_id)).setOnClickListener(this);
        (subscribeDialog.findViewById(R.id.cancel_button_id)).setOnClickListener(this);
        (subscribeDialog.findViewById(R.id.from_date_id)).setOnClickListener(this);
        (subscribeDialog.findViewById(R.id.to_date_id)).setOnClickListener(this);
        frmDateText = (TextView) subscribeDialog.findViewById(R.id.from_date_text_id);
        toDateText = (TextView) subscribeDialog.findViewById(R.id.to_date_text_id);
        subTypeList = (Spinner) subscribeDialog.findViewById(R.id.sel_sub_type_id);
        List<String> list = new ArrayList<String>();
        Collections.addAll(list, strSubTypes);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list) {
            @Override
            public int getCount() {
                int count = super.getCount();
                return count > 0 ? count - 1 : count;
            }
        };
        subTypeList.setAdapter(dataAdapter);
        subTypeList.setSelection(strSubTypes.length - 1);
        int[] date = Utility.getCurrentDate();
        datePicker = new DatePickerDialog(getActivity(), datePickerListener, date[0], date[1], date[2]);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            strSelDate = new StringBuilder().append(selectedYear).append("-").append(selectedMonth + 1).append("-").append(selectedDay).append(" 00:00").toString();

            if (nSelDate == 1) {
                frmDateText.setText(strSelDate);
                ;
            } else {
                toDateText.setText(strSelDate);
            }
        }
    };

}


