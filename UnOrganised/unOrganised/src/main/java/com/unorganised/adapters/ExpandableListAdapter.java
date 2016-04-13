package com.unorganised.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.unorganised.R;
import com.unorganised.activities.JobProviderDashboardActivity;
import com.unorganised.network.HttpReqRespLayer;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.DrawerItem;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;
import static com.unorganised.network.HttpReqRespLayer.getInstance;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 3/9/2016.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter implements Constants,HttpRequestResponse{

    private int notificationStatus;
    private Activity activity;
    private UnOrgSharedPreferences sharedPref;
    private List<DrawerItem> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<DrawerItem, List<DrawerItem>> _listDataChild;

    public ExpandableListAdapter(Activity activity, List<DrawerItem> listDataHeader,
                                 HashMap<DrawerItem, List<DrawerItem>> listChildData) {
        this.activity = activity;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        sharedPref = UnOrgSharedPreferences.getInstance(activity);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        //final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.single_list_item, null);
        }
        DrawerItem dItem = (DrawerItem) getChild(groupPosition, childPosition);
        TextView txt = (TextView) convertView.findViewById(R.id.lblListItem);
        txt.setText(dItem.getTitle());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        //String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.seeker_drawer_list_row, null);
        }

        DrawerItem dItem = (DrawerItem) this._listDataHeader.get(groupPosition);
        ToggleButton toggleBtn = (ToggleButton) convertView.findViewById(R.id.seeker_notification_toggle_btn);
        TextView txt = (TextView) convertView.findViewById(R.id.seeker_drawer_name);
        final ImageView img = (ImageView) convertView.findViewById(R.id.seeker_drawer_icon);

        if (dItem.getIsNotification()) {
            toggleBtn.setVisibility(View.VISIBLE);
            notificationStatus = sharedPref.getInt(SP_NOTIFICATION_STATUS);
            boolean status = notificationStatus == 1;
            DebugLog.d("Status:;;;;;;;:::: " + status);
            if (status) {
                img.setImageResource(R.drawable.recent_notification_app_drawer_selected);
            } else {
                img.setImageResource(R.drawable.notification_app_drawer_selected);
            }
            toggleBtn.setChecked(status);
            toggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    notificationStatus = isChecked ? 1 : 0;
                    sharedPref.put(SP_NOTIFICATION_STATUS, notificationStatus);
                    getInstance().updateNotification(ExpandableListAdapter.this,activity, Utility.stlUserID, notificationStatus);

                    if (isChecked) {
                        img.setImageResource(R.drawable.recent_notification_app_drawer_selected);
                    } else {
                        img.setImageResource(R.drawable.notification_app_drawer_selected);
                    }
                }
            });
        } else {
            img.setImageResource(dItem.getImgId());
        }
        txt.setText(dItem.getTitle());


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {

    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {

    }
}
