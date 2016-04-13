package com.unorganised.views;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.activities.ExitActivity;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.ExitConfirmDialogListener;
import com.unorganised.util.JobsParser;
import com.unorganised.util.SubscribedDetails;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.unorganised.network.HttpReqRespLayer.getInstance;


public class SubscribedFragment extends Fragment implements Constants, HttpRequestResponse, ExitConfirmDialogListener {
    private Long userId;
    private ListView subscribedListview;
    private UnOrgSharedPreferences sharedPref;
    private ArrayList<SubscribedDetails> subscribedJob;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subscribed_fragment, container, false);
        sharedPref = UnOrgSharedPreferences.getInstance(getActivity());
        userId = sharedPref.getLong(SP_USER_ID_KEY);
        subscribedListview = (ListView) view.findViewById(R.id.subscribed_list);
        getInstance().seekersubscribeddetails(this, getActivity(), userId);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(getActivity(), SubscribedFragment.this);
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
        switch (dataType) {
            case SEEKER_SUBSCRIBED_DETAILS:

                JobsParser subscribedParser = new JobsParser();
                subscribedJob = subscribedParser.subscribedJobs(data);
                if (subscribedJob.size() > 0) {


                    CustomSubscribeAdapter adapter = new CustomSubscribeAdapter(getActivity(), subscribedJob);
                    subscribedListview.setAdapter(adapter);
                } else {
                    Utility.showToast(getActivity(), getString(R.string.no_subscribed_jobs));
                }
        }
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

    class CustomSubscribeAdapter extends ArrayAdapter<SubscribedDetails> {
        ArrayList<SubscribedDetails> subscribeList;
        private Context context;

        public class ViewHolder {

            public TextView providernameTxt;
            public TextView serviceTxt;
            public TextView subscribefromTxt;
            public TextView subscribetoTxt;
            public TextView frequencyTxt;


        }

        private ViewHolder viewHolder;

        public CustomSubscribeAdapter(Context context, ArrayList<SubscribedDetails> subscribe) {
            super(context, R.layout.subscibed_list_row, subscribe);
            this.subscribeList = subscribe;
            this.context = context;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                rowView = inflater.inflate(R.layout.subscibed_list_row, null, true);
                viewHolder = new ViewHolder();

                viewHolder.providernameTxt = (TextView) rowView.findViewById(R.id.providernameTxt);
                viewHolder.serviceTxt = (TextView) rowView.findViewById(R.id.servicetxt);
                viewHolder.subscribefromTxt = (TextView) rowView.findViewById(R.id.sub_from);
                viewHolder.subscribetoTxt = (TextView) rowView.findViewById(R.id.sub_to);
                viewHolder.frequencyTxt = (TextView) rowView.findViewById(R.id.frequencytxt);

                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }

            final SubscribedDetails subscribeData = getItem(position);

            viewHolder.providernameTxt.setText(subscribeData.getProviderName());
            viewHolder.serviceTxt.setText(subscribeData.getService());
            viewHolder.subscribefromTxt.setText(subscribeData.getSubscribed_From());
            viewHolder.subscribetoTxt.setText(subscribeData.getSubscribed_To());
            if (subscribeData.getFrequency() == "1") {
                viewHolder.frequencyTxt.setText(getString(R.string.daily));
            } else if (subscribeData.getFrequency() == "2") {
                viewHolder.frequencyTxt.setText(getString(R.string.weekly));
            } else {
                viewHolder.frequencyTxt.setText(getString(R.string.monthly));
            }

            return rowView;
        }
    }
}
