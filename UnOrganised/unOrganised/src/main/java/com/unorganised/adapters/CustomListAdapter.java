package com.unorganised.adapters;

import java.util.ArrayList;

import com.unorganised.R;
import com.unorganised.util.DebugLog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<SubService> {

    private final Activity context;
    private final ArrayList<String> selectedIdList;
    private ArrayList<SubService> subServiceList;


    public class ViewHolder {
        public TextView txtTitle;
        public ImageView imageView;
        public CheckBox checkBox;

        public ViewHolder(CheckBox checkBox, TextView txtTitle, ImageView imageView) {
            this.checkBox = checkBox;
            this.txtTitle = txtTitle;
            this.imageView = imageView;
        }

        public TextView getTxtTitle() {
            return txtTitle;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

    }

    public CustomListAdapter(Activity context, int resourceId, ArrayList<SubService> subServiceList) {
        super(context, resourceId, subServiceList);
        this.context = context;
        this.subServiceList = new ArrayList<SubService>();
        this.subServiceList.addAll(subServiceList);
        selectedIdList = new ArrayList<String>();

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View rowView = view;
        final SubService subService = this.getItem(position);
        TextView txtTitle;
        ImageView imageView;
        CheckBox checkBox;
        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_single, null);
            txtTitle = (TextView) rowView.findViewById(R.id.txt);
            imageView = (ImageView) rowView.findViewById(R.id.img);
            checkBox = (CheckBox) rowView.findViewById(R.id.checkBox_id);
            rowView.setTag(new ViewHolder(checkBox, txtTitle, imageView));
            checkBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox check = (CheckBox) v;
                    SubService service = (SubService) check.getTag();
                    service.toggleChecked();
                    if (service.isSelected()) {
                        if (!selectedIdList.contains(service.getSubServiceId())) {
                            selectedIdList.add(service.getSubServiceId());
                        }
                    } else {
                        if (selectedIdList.contains(service.getSubServiceId())) {
                            selectedIdList.remove(service.getSubServiceId());
                        }
                    }
                }
            });
        } else {
            ViewHolder viewHolder = (ViewHolder) rowView.getTag();
            checkBox = viewHolder.getCheckBox();
            txtTitle = viewHolder.getTxtTitle();
            imageView = viewHolder.getImageView();
        }

        checkBox.setTag(subService);
        checkBox.setChecked(subService.isSelected());


        txtTitle.setText(subService.getSubService());
        imageView.setImageResource(R.drawable.icon_default_service);
        return rowView;
    }

    public ArrayList<String> getSelectedSubServices() {
        DebugLog.d("Selected ids  "+selectedIdList.toString());
        return selectedIdList;
    }
}