package com.unorganised.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.unorganised.R;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.DrawerItem;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;

import org.json.JSONObject;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

public class JobSeekerDrawerAdapter extends ArrayAdapter<DrawerItem> implements HttpRequestResponse, Constants{

	private int resource;
	private int notificationStatus;
	private Context context;
	private ArrayList<DrawerItem> seekerDrawerItemsList;
	private UnOrgSharedPreferences sharedPreferences;

	public JobSeekerDrawerAdapter(Context context, int resource, ArrayList<DrawerItem> seekerDrawerItemsList) {
		super(context, resource, seekerDrawerItemsList);
		this.resource = resource;
		this.context = context;
		this.seekerDrawerItemsList = seekerDrawerItemsList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final ViewHolder holder;
		if (view == null) {
			sharedPreferences =  UnOrgSharedPreferences.getInstance(context);
			view = ((Activity) context).getLayoutInflater().inflate(resource, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) view.findViewById(R.id.seeker_drawer_name);
			holder.icon = (ImageView) view.findViewById(R.id.seeker_drawer_icon);
			holder.toggleBtn = (ToggleButton) view.findViewById(R.id.seeker_notification_toggle_btn);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		 DrawerItem dItem = (DrawerItem) this.seekerDrawerItemsList.get(position);

		 if(dItem.getIsNotification())	 {
			 holder.toggleBtn.setVisibility(View.VISIBLE);
			 notificationStatus = sharedPreferences.getInt(SP_NOTIFICATION_STATUS);
			 boolean status = notificationStatus == 1;
			 DebugLog.d("Status:;;;;;;;:::: "+status);
			 if (status) {
				 holder.icon.setImageResource(R.drawable.recent_notification_app_drawer_selected);
			 } else {
				 holder.icon.setImageResource(R.drawable.notification_app_drawer_selected);
			 }
			 holder.toggleBtn.setChecked(status);
			 holder.toggleBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				 @Override
				 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					 notificationStatus = isChecked ? 1 : 0;
					 sharedPreferences.put(SP_NOTIFICATION_STATUS, notificationStatus);
					 getInstance().updateNotification(JobSeekerDrawerAdapter.this, (Activity) context, Utility.stlUserID, notificationStatus);
					 if (isChecked) {
						 holder.icon.setImageResource(R.drawable.recent_notification_app_drawer_selected);
					 } else {
						 holder.icon.setImageResource(R.drawable.notification_app_drawer_selected);
					 }
				 }
			 });
		 }else {
			 holder.icon.setImageResource(dItem.getImgId());
		 }
		 holder.name.setText(dItem.getTitle());

		return view;

	}

	@Override
	public void onSuccess(JSONObject data, Constants.HttpReqRespActionItems dataType) {

	}

	@Override
	public void onFailure(JSONObject data, Constants.HttpReqRespActionItems dataType) {

	}

	public class ViewHolder {
		public TextView name;
		public ImageView icon;
		public ToggleButton toggleBtn;

	}

}
