package com.unorganised.views;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.activities.ExitActivity;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.ExitConfirmDialogListener;
import com.unorganised.util.Utility;

import org.json.JSONObject;

import static com.unorganised.network.HttpReqRespLayer.getInstance;


public class ReferAFriendFragment extends Fragment implements HttpRequestResponse, Constants, ExitConfirmDialogListener {
    private Button invitefriendBtn;
    private TextView invitecodeTxt;
    private static final String URL_TO_SHARE = "https://play.google.com/store/apps/details?id=com.unorganised";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.refer_a_friend_fragment, container, false);
        getInstance().getReferralCode(this, getActivity(), Utility.stlUserID);
        invitefriendBtn = (Button) view.findViewById(R.id.invite_friend_btn);
        invitecodeTxt = (TextView) view.findViewById(R.id.invite_code);
        invitefriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.setAction(Intent.ACTION_SEND);
                intent1.setType("text/plain");
                intent1.putExtra(Intent.EXTRA_TEXT, URL_TO_SHARE + "\n" + getString(R.string.refer_code) + invitecodeTxt.getText());
                startActivity(Intent.createChooser(intent1, "Share"));
            }
        });
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    ExitConfirmationDialog exitConfirm = new ExitConfirmationDialog(getActivity(), ReferAFriendFragment.this);
                    exitConfirm.show();
                    return true;
                }
                return false;
            }
        });
        return view;

    }

    @Override
    public void onSuccess(JSONObject data, Constants.HttpReqRespActionItems dataType) {
        String refCode = "";
        try {
            String result = data.getString(RESULT_TAG);
            JSONObject json = new JSONObject(result);
            if (json.has(REF_COD_TAG)) {
                refCode = json.getString(REF_COD_TAG);
                invitecodeTxt.setText(refCode);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onFailure(JSONObject data, Constants.HttpReqRespActionItems dataType) {
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
}
