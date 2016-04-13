package com.unorganised.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unorganised.R;

public class RegisterTypeActivity extends Activity implements View.OnClickListener{

    private boolean bIndividualSelect;
    private boolean bCorporateSelect;
    private LinearLayout individualLayout;
    private LinearLayout corporateLayout;
    private ImageView individualIcon;
    private ImageView corporateIcon;
    private TextView individualText;
    private TextView corporateText;
    private View individualDiv;
    private View corporateDiv;
    private TextView registrationStepTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_type_layout);
        registrationStepTitle =(TextView)findViewById(R.id.title_view_id);
        registrationStepTitle.setText(R.string.register);
        individualLayout = (LinearLayout)findViewById(R.id.layout_individual);
        individualLayout.setOnClickListener(this);
        corporateLayout = (LinearLayout)findViewById(R.id.layout_corporate);
        corporateLayout.setOnClickListener(this);
        findViewById(R.id.next_btn_id).setOnClickListener(this);
        individualIcon = (ImageView)findViewById(R.id.individual_icon);
        corporateIcon = (ImageView)findViewById(R.id.corporate_icon);
        individualText = (TextView)findViewById(R.id.individual_text_id);
        corporateText = (TextView)findViewById(R.id.corporate_text_id);
        individualDiv = findViewById(R.id.individual_div_id);
        corporateDiv = findViewById(R.id.corporate_div_id);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.layout_individual:
                individualLayout.setBackgroundColor(getResources().getColor(R.color.green));
                individualIcon.setImageResource(R.drawable.icon_individuals_selected);
                individualText.setTextColor(Color.WHITE);
                individualDiv.setBackgroundColor(Color.WHITE);
                corporateLayout.setBackgroundColor(getResources().getColor(R.color.white));
                corporateIcon.setImageResource(R.drawable.icon_corporate_normal);
                corporateText.setTextColor(Color.BLACK);
                corporateDiv.setBackgroundColor(Color.BLACK);
                break;
            case R.id.layout_corporate:
                corporateLayout.setBackgroundColor(getResources().getColor(R.color.green));
                corporateIcon.setImageResource(R.drawable.icon_corporate_selected);
                corporateText.setTextColor(Color.WHITE);
                corporateDiv.setBackgroundColor(Color.WHITE);
                individualLayout.setBackgroundColor(getResources().getColor(R.color.white));
                individualIcon.setImageResource(R.drawable.icon_individuals_normal);
                individualText.setTextColor(Color.BLACK);
                individualDiv.setBackgroundColor(Color.BLACK);
                break;
            case R.id.next_btn_id:
                    startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.back_btn_id:
                finish();
                break;
            case R.id.back_btn_layout_id:
                finish();
                break;
        }

    }
}
