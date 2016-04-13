package com.unorganised.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.unorganised.R;
import com.unorganised.adapters.ExpandableListAdapter;
import com.unorganised.adapters.JobSeekerDrawerAdapter;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.DrawerItem;
import com.unorganised.util.ExitConfirmDialogListener;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;
import com.unorganised.views.AcceptedJobsFragment;
import com.unorganised.views.ExitConfirmationDialog;
import com.unorganised.views.HelpFragment;
import com.unorganised.views.JobSearchFragment;
import com.unorganised.views.ReferAFriendFragment;
import com.unorganised.views.SeekerCompletedJobsFragment;
import com.unorganised.views.SeekerOnGoingJobFragment;
import com.unorganised.views.SubscribedFragment;
import com.unorganised.views.UserSettingsFragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

public class JobSeekerDashbordActivity extends FragmentActivity implements Constants, ExitConfirmDialogListener, HttpRequestResponse {

    private boolean bDrawerOpened;
    private int userType;
    private String drawerTitle;
    private String actionBarTitle;
    private final String TAG = JobSeekerDashbordActivity.class.getSimpleName();
    private DrawerLayout drawerLayout;
    //private ListView drawerListView;
    private ArrayList<DrawerItem> seekerDrawerItemsList;
    private JobSeekerDrawerAdapter adapter;
    private ActionBarDrawerToggle drawerToggle;
    public View actionBarView;
    public TextView tileView;
    private UnOrgSharedPreferences sp;
    public static FragmentManager fragmentManager;
    private static ImageView deleteAllJobImg;
    private TextView userTypeView;
    private List<String> listDataHeader;
    private HashMap<DrawerItem, List<DrawerItem>> listDataChild;
    private ArrayList<DrawerItem> moreList;
    private ExpandableListView drawerListView;
    private ExpandableListAdapter listAdapter;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.seeker_dashboard_activity);
        sp = UnOrgSharedPreferences.getInstance(this);
        userType = sp.getInt(SP_USER_TYPE);
        boolean loginStatus = sp.getBoolean(SP_LOGIN_STATUS);
        Log.d("Login status", " " + loginStatus);
        if (!loginStatus) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        fragmentManager = getSupportFragmentManager();
        drawerTitle = actionBarTitle = getString(R.string.quick_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ExpandableListView) findViewById(R.id.left_drawer);
        adddrawerlistData();
        listAdapter = new ExpandableListAdapter(this, seekerDrawerItemsList, listDataChild);
        drawerListView.setGroupIndicator(null);
        // setting list adapter
        drawerListView.setAdapter(listAdapter);
        drawerListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                handleDrawerItemClick(groupPosition);
                return false;
            }
        });
        drawerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            Fragment fragment = null;
            Bundle args = new Bundle();

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (childPosition == 0) {

                    fragment = new ReferAFriendFragment();
                } else if (childPosition == 1) {
                    fragment = new HelpFragment();
                }
                if (fragment != null) {
                    fragmentManager = getSupportFragmentManager();
                    fragment.setArguments(args);
                    android.app.FragmentManager frgManager = getFragmentManager();
                    Utility.removeFragment(JobSeekerDashbordActivity.this, R.id.content_frame);
                    frgManager.beginTransaction().add(R.id.content_frame, fragment)
                            .commitAllowingStateLoss();
                    drawerListView.setItemChecked(childPosition, true);
                    actionBarTitle = moreList.get(childPosition).getTitle();
                    tileView.setText(actionBarTitle);
                    drawerLayout.closeDrawer(drawerListView);
                }
                return false;
            }
        });
        /*seekerDrawerItemsList = new ArrayList<DrawerItem>();
        seekerDrawerItemsList.add(new DrawerItem(
				getString(R.string.search_job), R.drawable.serach_job_app_drawer_selected, false));
		seekerDrawerItemsList
				.add(new DrawerItem(getString(R.string.on_going_job),
						R.drawable.ongoing_job_app_drawer_selected, false));
		seekerDrawerItemsList
				.add(new DrawerItem(getString(R.string.accepted_jobs),
						R.drawable.ongoing_job_app_drawer_selected, false));
		seekerDrawerItemsList
				.add(new DrawerItem(
						getString(R.string.completed_jobs),
						R.drawable.completed_job_app_drawer_selected, false));
		seekerDrawerItemsList
				.add(new DrawerItem(
						getString(R.string.subscribed),
						R.drawable.completed_job_app_drawer_selected, false));
		seekerDrawerItemsList.add(new DrawerItem(
				getString(R.string.acc_settings), R.drawable.account_setting_app_drawer_selected,
				false));
		seekerDrawerItemsList.add(new DrawerItem(
				getString(R.string.notification), R.drawable.recent_notification_app_drawer_selected, true));
		adapter = new JobSeekerDrawerAdapter(this,R.layout.seeker_drawer_list_row, seekerDrawerItemsList);

		if(userType == 3) {
			if(Utility.typeId==1)
			{
			seekerDrawerItemsList.add(new DrawerItem(
					getString(R.string.switch_to_provider), R.drawable.icon_switch_user,
					false));}
		}
		seekerDrawerItemsList.add(new DrawerItem(
				getString(R.string.help), R.drawable.icon_payment_info_selected,
				false));
		seekerDrawerItemsList.add(new DrawerItem(
				getString(R.string.logout), R.drawable.icon_logout_selected,
				false));

		drawerListView.setAdapter(adapter);
		drawerListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				handleDrawerItemClick(position);
			}
		});*/


        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        final int position = getIntent().getIntExtra(DRAWER_ITEM_POSITION, 0);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.quick_menu_icon, R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                bDrawerOpened = false;
                // getActionBar().setTitle(mTitle);
                tileView.setText(actionBarTitle);
                invalidateOptionsMenu();
                userTypeView.setVisibility(View.GONE);

                if (actionBarTitle.equals(getString(R.string.search_job))) {
                    actionBarView.findViewById(R.id.actionbar_right_side_options_layout).setVisibility(View.VISIBLE);
                }

                if (Utility.isCompletedJob && actionBarTitle.equals(getString(R.string.completed_jobs)) && Utility.completedjobCount > 1) {
                    deleteAllJobImg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                tileView.setText(drawerTitle);
                bDrawerOpened = true;
                // getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
                userTypeView.setVisibility(View.VISIBLE);
                actionBarView.findViewById(R.id.actionbar_right_side_options_layout).setVisibility(View.GONE);

                if (Utility.isCompletedJob) {
                    deleteAllJobImg.setVisibility(View.GONE);
                }
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        setupActionBar();
        handleDrawerItemClick(position);
    }

    public void adddrawerlistData() {
        listDataChild = new HashMap<DrawerItem, List<DrawerItem>>();
        seekerDrawerItemsList = new ArrayList<DrawerItem>();
        seekerDrawerItemsList.add(new DrawerItem(
                getString(R.string.search_job), R.drawable.serach_job_app_drawer_selected, false));
        seekerDrawerItemsList
                .add(new DrawerItem(getString(R.string.on_going_job),
                        R.drawable.ongoing_job_app_drawer_selected, false));
        seekerDrawerItemsList
                .add(new DrawerItem(getString(R.string.accepted_jobs),
                        R.drawable.icn_accepted_jobs_selected, false));
        seekerDrawerItemsList
                .add(new DrawerItem(
                        getString(R.string.completed_jobs),
                        R.drawable.completed_job_app_drawer_selected, false));
        seekerDrawerItemsList
                .add(new DrawerItem(
                        getString(R.string.subscribed),
                        R.drawable.icn_subscribed_selected, false));
        seekerDrawerItemsList.add(new DrawerItem(
                getString(R.string.acc_settings), R.drawable.account_setting_app_drawer_selected,
                false));
        seekerDrawerItemsList.add(new DrawerItem(
                getString(R.string.notification), R.drawable.recent_notification_app_drawer_selected, true));
        //  adapter = new JobSeekerDrawerAdapter(this, R.layout.seeker_drawer_list_row, seekerDrawerItemsList);
        moreList = new ArrayList<DrawerItem>();
        if (userType == 3) {
           // if (Utility.typeId == 1) {
                seekerDrawerItemsList.add(new DrawerItem(
                        getString(R.string.switch_user), R.drawable.icon_switch_user,
                        false));
                seekerDrawerItemsList.add(new DrawerItem(
                        "More......", R.drawable.icon_payment_info_selected,
                        false));
                moreList.add(new DrawerItem(
                        "Refer A Friend", R.drawable.icon_logout_selected,
                        false));
                moreList.add(new DrawerItem(
                        getString(R.string.help), R.drawable.icn_help_selected,
                        false));
          //  }
        } else {
            seekerDrawerItemsList.add(new DrawerItem(
                    "Refer A Friend", R.drawable.icon_logout_selected,
                    false));
            seekerDrawerItemsList.add(new DrawerItem(
                    getString(R.string.help), R.drawable.icn_help_selected,
                    false));
        }
        seekerDrawerItemsList.add(new DrawerItem(
                getString(R.string.terms_of_use), R.drawable.icn_help_selected,
                false));
        seekerDrawerItemsList.add(new DrawerItem(
                getString(R.string.logout), R.drawable.icon_logout_selected,
                false));
        ArrayList<DrawerItem> drawerItem = new ArrayList<DrawerItem>();
        if (userType == 3) {
            listDataChild.put(seekerDrawerItemsList.get(8), moreList);
        } else {
            listDataChild.put(seekerDrawerItemsList.get(8), drawerItem);
        }
        listDataChild.put(seekerDrawerItemsList.get(0), drawerItem);
        listDataChild.put(seekerDrawerItemsList.get(1), drawerItem);
        listDataChild.put(seekerDrawerItemsList.get(2), drawerItem);
        listDataChild.put(seekerDrawerItemsList.get(3), drawerItem);
        listDataChild.put(seekerDrawerItemsList.get(4), drawerItem);
        listDataChild.put(seekerDrawerItemsList.get(5), drawerItem);
        listDataChild.put(seekerDrawerItemsList.get(6), drawerItem);
        listDataChild.put(seekerDrawerItemsList.get(7), drawerItem);
        listDataChild.put(seekerDrawerItemsList.get(9), drawerItem);
        listDataChild.put(seekerDrawerItemsList.get(10), drawerItem);

    }


    private void handleDrawerItemClick(int position) {
        addDeleteButton(position);

        if (userType == 3) {
            selectItemWithSwitchOption(position);
        } else {
            selectItemWithoutSwitchOption(position);
        }
    }

    public void selectItemWithSwitchOption(int position) {
        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                fragment = new JobSearchFragment();
                break;
            case 1:
                fragment = new SeekerOnGoingJobFragment();
                break;
            case 2:
                fragment = new AcceptedJobsFragment();
                break;
            case 3:
                fragment = new SeekerCompletedJobsFragment();
                break;
            case 4:
                fragment = new SubscribedFragment();
                break;
            case 5:
                fragment = new UserSettingsFragment();
                break;
            case 7:
                startActivity(new Intent(this, SwitchUserActivity.class));
                break;
            case 8:
                //more
                break;
            case 9:
                //terms of service
                break;
            case 10:
                sp.put(SP_LOGIN_STATUS, false);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;

        }

        if (fragment != null) {
            fragmentManager = getSupportFragmentManager();
            fragment.setArguments(args);
            android.app.FragmentManager frgManager = getFragmentManager();
            Utility.removeFragment(this, R.id.content_frame);
            frgManager.beginTransaction().add(R.id.content_frame, fragment)
                    .commitAllowingStateLoss();
            drawerListView.setItemChecked(position, true);
            actionBarTitle = seekerDrawerItemsList.get(position).getTitle();
            tileView.setText(actionBarTitle);
            drawerLayout.closeDrawer(drawerListView);

            if (position == 0) {
                actionBarView.findViewById(
                        R.id.actionbar_right_side_options_layout)
                        .setVisibility(View.VISIBLE);
            } else {
                actionBarView.findViewById(
                        R.id.actionbar_right_side_options_layout)
                        .setVisibility(View.INVISIBLE);
            }
        }

    }

    public void selectItemWithoutSwitchOption(int position) {

        Fragment fragment = null;
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                fragment = new JobSearchFragment();
                break;
            case 1:
                fragment = new SeekerOnGoingJobFragment();
                break;
            case 2:
                fragment = new AcceptedJobsFragment();
                break;
            case 3:
                fragment = new SeekerCompletedJobsFragment();
                break;
            case 4:
                fragment = new SubscribedFragment();
                break;
            case 5:
                fragment = new UserSettingsFragment();

                //args.putString("type", "SEEKER");
                //Utility.showToast(this, args.getString("type"));
                break;
            case 7:
                //help
                fragment = new ReferAFriendFragment();
                break;
            case 8:
                fragment = new HelpFragment();
                break;
            case 9:
               //terms of service
                break;
            case 10:
                sp.put(SP_LOGIN_STATUS, false);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            default:
                break;

        }

        if (fragment != null) {
            fragmentManager = getSupportFragmentManager();
            fragment.setArguments(args);
            android.app.FragmentManager frgManager = getFragmentManager();
            Utility.removeFragment(this, R.id.content_frame);
            frgManager.beginTransaction().add(R.id.content_frame, fragment)
                    .commitAllowingStateLoss();
            drawerListView.setItemChecked(position, true);
            actionBarTitle = seekerDrawerItemsList.get(position).getTitle();
            tileView.setText(actionBarTitle);
            drawerLayout.closeDrawer(drawerListView);

            if (position == 0) {
                actionBarView.findViewById(
                        R.id.actionbar_right_side_options_layout)
                        .setVisibility(View.VISIBLE);
            } else {
                actionBarView.findViewById(
                        R.id.actionbar_right_side_options_layout)
                        .setVisibility(View.INVISIBLE);
            }
        }

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Used to setup the actionbar
     */
    @SuppressLint("NewApi")
    public void setupActionBar() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setIcon(android.R.color.transparent);
        // Customize the actionBar view to get our font and icons
        LayoutInflater inflater = LayoutInflater.from(this);
        actionBarView = inflater.inflate(R.layout.action_bar_custom, null);
        actionBar.setCustomView(actionBarView);// , layout);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.green)));
        tileView = (TextView) actionBarView
                .findViewById(R.id.dashboard_title_id);
        tileView.setText(drawerTitle);
        userTypeView = (TextView) actionBarView.findViewById(R.id.type_of_user_title_id);

        deleteAllJobImg = (ImageView) actionBarView.findViewById(R.id.delete_all_id);
        deleteAllJobImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Delete all completed jobs
                createDeleteDialog();
            }
        });
    }

    private void createDeleteDialog() {
        final Dialog deleteAlert = new Dialog(this);
        deleteAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deleteAlert.setContentView(R.layout.delete_dialog);
        TextView deleteTxt = (TextView) deleteAlert.findViewById(R.id.delete_text_id);
        deleteTxt.setText(getResources().getString(R.string.delete_all_completed_jobs));
        deleteAlert.show();
        Button delete = (Button) deleteAlert.findViewById(R.id.delete_btn_id);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInstance().deleteSeekerallCompletedJob(JobSeekerDashbordActivity.this, JobSeekerDashbordActivity.this, Utility.stlUserID, Utility.strCompleteJobId);
                deleteAlert.dismiss();
            }
        });
        Button cancel = (Button) deleteAlert.findViewById(R.id.cancel_btn_id);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public static void addDeleteButton(int position) {
        if (Utility.isCompletedJob && position == 3 && Utility.completedjobCount > 1) {
            deleteAllJobImg.setVisibility(View.VISIBLE);
        } else {
            deleteAllJobImg.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (bDrawerOpened) {
            drawerLayout.closeDrawer(drawerListView);
        } else if (getFragmentManager().getBackStackEntryCount() == 0) {
            ExitConfirmationDialog dialog = new ExitConfirmationDialog(this, this);
            dialog.show();
        }
    }

    @Override
    public void onExitConfirmed() {
        Intent intent = new Intent(this,
                ExitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NO_ANIMATION
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        startActivity(intent);
        finish();
//        moveTaskToBack(true);
//        finish();
    }

    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        switch (dataType) {
            case DELETE_SEEKER_ALL_COMPLETED_JOB:
                deleteAllJobImg.setVisibility(View.GONE);
                handleDrawerItemClick(3);

                break;
        }
    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {

    }
}
