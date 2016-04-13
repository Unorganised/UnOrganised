/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.unorganised;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.unorganised.activities.JobDetailsActivity;
import com.unorganised.activities.JobProviderDashboardActivity;
import com.unorganised.activities.JobSeekerDashbordActivity;
import com.unorganised.activities.LoadingActivity;
import com.unorganised.activities.LoginActivity;
import com.unorganised.activities.ShowLocationActivity;
import com.unorganised.activities.SplashActivity;
import com.unorganised.receivers.GcmBroadcastReceiver;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.UnOrgSharedPreferences;
import com.unorganised.util.Utility;
import com.unorganised.views.AssignedJobFragment;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService implements Constants {

    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "GCM Demo";
    private NotificationManager mNotificationManager;
    private UnOrgSharedPreferences sharedPreferences;
    public GcmIntentService() {
        super("GcmIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        sharedPreferences = UnOrgSharedPreferences.getInstance(new LoginActivity());
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {

                        Thread.sleep(5000);

                    } catch (InterruptedException e) {

                    }
                }
                Log.i(TAG, "Received: " + extras.toString());

                String message;
                // Message from PHP server
                message = intent.getStringExtra(GCM_MSG);

                int type = Integer.parseInt(intent.getStringExtra(GCM_NOTIFICATION_TYPE));
                DebugLog.d("type:" + type);
                DebugLog.d("message:" + message);
                long jobid;
                switch (type) {
                    case -1:
                        sendNotification(message);
                        break;
                    case 1:
                        // Provider posted job
                        jobid = Long.parseLong(intent.getStringExtra(GCM_JOB_ID));
                        DebugLog.d("Posted job:" + jobid);
                        if (jobid != -1) {
                            sendJobPostedNotification(message, jobid);
                        } else {
                            sendNotification(message);
                        }
                        break;
                    case 2:
                        //Seeker job appiled
                        jobid = Long.parseLong(intent.getStringExtra(GCM_JOB_ID));
                        DebugLog.d("Applied job:" + jobid);
                        if (jobid != -1) {

                            sendJobAppliedNotification(message, jobid);
                        } else {
                            sendNotification(message);
                        }
                        break;

                    case 3:
                        //Provider job assigned
                        jobid = Long.parseLong(intent.getStringExtra(GCM_JOB_ID));
                        DebugLog.d("Assigned Job:" + jobid);
                        if (jobid != -1) {
                            sendJobAssignedNotification(message, jobid);
                        } else {
                            sendNotification(message);
                        }
                        break;
                    case 4:
                        //Seeker navigation started
                        long seekerId = Long.parseLong(intent.getStringExtra(GCM_SEEKER_ID));
                        DebugLog.d("seekerId:" + seekerId);
                        sendNavigationStartedNotification(message, seekerId);
                        break;
                    case 5:
                        //Provider start job
                        break;
                    case 7:
                        //Provider started job
                        jobid = Long.parseLong(intent.getStringExtra(GCM_JOB_ID));
                        DebugLog.d("Provider started job:" + jobid);
                        if (jobid != -1) {
                            sendProviderStartedJobNotification(message, jobid);
                        } else {
                            sendNotification(message);
                        }
                        break;
                    case 8:
                        //Seeker started job
                        jobid = Long.parseLong(intent.getStringExtra(GCM_JOB_ID));
                        DebugLog.d("Seeker started job:" + jobid);
                        if (jobid != -1) {
                            sendSeekerStartedJobNotification(message, jobid);
                        } else {
                            sendNotification(message);
                        }
                        break;
                    case 9:
                        //Provider finish job
                        jobid = Long.parseLong(intent.getStringExtra(GCM_JOB_ID));
                        DebugLog.d("Provider finish job:" + jobid);
                        if (jobid != -1) {
                            sendProviderFinishedJobNotification(message, jobid);
                        } else {
                            sendNotification(message);
                        }
                        break;
                    case 10:
                        //Seeker finish job
                        jobid = Long.parseLong(intent.getStringExtra(GCM_JOB_ID));
                        DebugLog.d("Seeker finish job:" + jobid);
                        if (jobid != -1) {
                            sendSeekerFinishedJobNotification(message, jobid);
                        } else {
                            sendNotification(message);
                        }
                        break;
                }

            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendSeekerStartedJobNotification(String message, long jobId) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent;
        if (sharedPreferences.getBoolean(SP_LOGIN_STATUS)) {
            intent = new Intent(this, JobProviderDashboardActivity.class);
            intent.putExtra(DRAWER_ITEM_POSITION, 2);
            intent.putExtra(JOB_ID, jobId);
        } else {
            intent = new Intent(this, LoginActivity.class);
            intent.putExtra(SCREEN, NF_START_JOB_SEEKER);
            intent.putExtra(JOB_ID, jobId);
            intent.putExtra(IS_NOTIFICATION, true);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.app_icon_72x72)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setAutoCancel(true).setContentText(message)
                .setDefaults(Notification.DEFAULT_SOUND).setNumber(1);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendProviderStartedJobNotification(String message, long jobId) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent;
        if (sharedPreferences.getBoolean(SP_LOGIN_STATUS)) {
            DebugLog.d("sendProviderStartedJobNotification");
            intent = new Intent(this, JobSeekerDashbordActivity.class);
            intent.putExtra(DRAWER_ITEM_POSITION, 2);
            intent.putExtra(JOB_ID, jobId);
        } else {
            intent = new Intent(this, LoginActivity.class);
            intent.putExtra(SCREEN, NF_STOP_JOB_PROVIDER);
            intent.putExtra(JOB_ID, jobId);
            intent.putExtra(IS_NOTIFICATION, true);
        }
        //PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.app_icon_72x72)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setAutoCancel(true).setContentText(message)
                .setDefaults(Notification.DEFAULT_SOUND).setNumber(1);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendProviderFinishedJobNotification(String message, long jobId) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent;
        if (sharedPreferences.getBoolean(SP_LOGIN_STATUS)) {
            intent = new Intent(this, JobSeekerDashbordActivity.class);
            intent.putExtra(DRAWER_ITEM_POSITION, 1);
            intent.putExtra(JOB_ID, jobId);
        } else {
            intent = new Intent(this, LoginActivity.class);
            intent.putExtra(SCREEN, NF_STOP_JOB_PROVIDER);
            intent.putExtra(JOB_ID, jobId);
            intent.putExtra(IS_NOTIFICATION, true);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.app_icon_72x72)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setAutoCancel(true).setContentText(message)
                .setDefaults(Notification.DEFAULT_SOUND).setNumber(1);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendSeekerFinishedJobNotification(String message, long jobId) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent;
        if (sharedPreferences.getBoolean(SP_LOGIN_STATUS)) {
            intent = new Intent(this, JobProviderDashboardActivity.class);
            intent.putExtra(DRAWER_ITEM_POSITION, 1);
            intent.putExtra(JOB_ID, jobId);
        } else {
            intent = new Intent(this, LoginActivity.class);
            intent.putExtra(SCREEN, NF_STOP_JOB_SEEKER);
            intent.putExtra(JOB_ID, jobId);
            intent.putExtra(IS_NOTIFICATION, true);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.app_icon_72x72)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setAutoCancel(true).setContentText(message)
                .setDefaults(Notification.DEFAULT_SOUND).setNumber(1);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {


        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                RegisterActivity.class, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.app_icon_72x72)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setAutoCancel(true).setContentText(msg)
                .setDefaults(Notification.DEFAULT_SOUND).setNumber(1);

        //mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//
    }

    private void sendJobPostedNotification(String msg, long jobId) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent;
        if (sharedPreferences.getBoolean(SP_LOGIN_STATUS)) {
//            intent = new Intent(this, JobDetailsActivity.class);
//            intent.putExtra(JOB_ID, jobId);
            intent = new Intent(this, JobSeekerDashbordActivity.class);
            intent.putExtra(DRAWER_ITEM_POSITION, 0);
        } else {
            intent = new Intent(this, LoginActivity.class);
            intent.putExtra(SCREEN, NF_POST_JOB);
            intent.putExtra(JOB_ID, jobId);
            intent.putExtra(IS_NOTIFICATION, true);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.app_icon_72x72)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setAutoCancel(true).setContentText(msg)
                .setDefaults(Notification.DEFAULT_SOUND).setNumber(1);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendJobAppliedNotification(String msg, long jobId) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        //TODO:Launch job details screen of the provider.By using job id get the data
        Intent intent;
        if (sharedPreferences.getBoolean(SP_LOGIN_STATUS)) {
            intent = new Intent(this, JobProviderDashboardActivity.class);
            intent.putExtra(DRAWER_ITEM_POSITION, 0);
            intent.putExtra(JOB_ID, jobId);
        } else {
            intent = new Intent(this, LoginActivity.class);
            intent.putExtra(SCREEN, NF_ACCEPT_JOB);
            intent.putExtra(JOB_ID, jobId);
            intent.putExtra(IS_NOTIFICATION, true);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.app_icon_72x72)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setAutoCancel(true).setContentText(msg)
                .setDefaults(Notification.DEFAULT_SOUND).setNumber(1);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendJobAssignedNotification(String msg, long jobId) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent;
        if (sharedPreferences.getBoolean(SP_LOGIN_STATUS)) {
            DebugLog.d("sendJobAssignedNotification");
            intent = new Intent(this, JobSeekerDashbordActivity.class);
            intent.putExtra(DRAWER_ITEM_POSITION, 2);
            intent.putExtra(JOB_ID, jobId);
        } else {
            intent = new Intent(this, LoginActivity.class);
            intent.putExtra(SCREEN, NF_ASSIGN_JOB);
            intent.putExtra(JOB_ID, jobId);
            intent.putExtra(IS_NOTIFICATION, true);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.app_icon_72x72)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setAutoCancel(true).setContentText(msg)
                .setDefaults(Notification.DEFAULT_SOUND).setNumber(1);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendNavigationStartedNotification(String msg, long seekerId) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent;
        if (sharedPreferences.getBoolean(SP_LOGIN_STATUS)) {
            intent = new Intent(this, ShowLocationActivity.class);
            intent.putExtra(KEY_LOCATION_DISPLAY_LAUNCHER_TYPE, LOCATION_DISPLAY_LAUNCHER_TYPE.SEEKER);
            intent.putExtra(SEEKER_ID_TAG, seekerId);
        } else {
            intent = new Intent(this, LoginActivity.class);
            intent.putExtra(SCREEN, NF_SHOW_LOCATION);
            intent.putExtra(IS_NOTIFICATION, true);
            intent.putExtra(SEEKER_ID_TAG, seekerId);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.app_icon_72x72)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setAutoCancel(true).setContentText(msg)
                .setDefaults(Notification.DEFAULT_SOUND).setNumber(1);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
