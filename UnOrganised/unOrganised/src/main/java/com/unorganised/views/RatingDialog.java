package com.unorganised.views;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.unorganised.R;
import com.unorganised.util.RatingDialogListener;

/**
 * Created by anarra on 02/01/16.
 */
public class RatingDialog extends Dialog{


    private RatingDialogListener listener;
    private Activity activity;
    private View view;
    RatingBar ratingBar;
    Button okBtn;
    private float selectedRating ;
    public RatingDialog(Activity activity,RatingDialogListener listener)
    {
        super(activity);
        this.listener = listener;
        this.activity = activity;
        view =  activity.getLayoutInflater().inflate(R.layout.rate_dialog,
                null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(view);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                selectedRating = rating;
            }
        });
        okBtn = (Button)view.findViewById(R.id.ok_button_id);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRatingConfirmned(selectedRating);
                dismiss();
            }
        });
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(activity.getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);

    }
}
