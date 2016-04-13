package com.unorganised.views;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import com.unorganised.R;
import com.unorganised.util.ExitConfirmDialogListener;

public class ExitConfirmationDialog extends Dialog {

    private ExitConfirmDialogListener listener;
    private Activity activity;
    private View view;

    public ExitConfirmationDialog(Activity activity, ExitConfirmDialogListener listener) {
        super(activity);
        this.listener = listener;
        this.activity = activity;
        view = activity.getLayoutInflater().inflate(R.layout.exit_confirm, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(view);
        view.findViewById(R.id.ok_button_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onExitConfirmed();
                dismiss();
            }
        });
        view.findViewById(R.id.cancel_button_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
