package com.unorganised.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 22-12-2015.
 */
public class PendingJobsParser implements Constants{


    public ArrayList<PendingJobs> parsePendingJobs(JSONObject json){
        ArrayList<PendingJobs> pendingJobsList = new ArrayList<PendingJobs>();
        JSONArray result = null;
        try{
            result = json.getJSONArray(RESULT_TAG);

            for (int i = 0; i <result.length() ; i++) {
                JSONObject jsonObj = result.getJSONObject(i);
                int jobId = jsonObj.optInt(JOB_ID);
                int jobStatus = jsonObj.optInt(JOB_RESPONSE_COUNT);
                int responseStatus = 0;

                if (jobStatus == 0){
                    responseStatus = JOB_NOT_ACCEPTED;
                } else if (jobStatus > 0) {
                    responseStatus = JOB_ACCEPTED_BY;
                }
                String jobTitle = jsonObj.optString(JOB_SERVICE_DESC);
                double lat = jsonObj.optDouble(JOB_LAT);
                double lon =  jsonObj.optDouble(JOB_LNG);
                String jobDescription = jsonObj.optString(JOB_DES);
                String price = jsonObj.optString(JOB_PRICE_TAG);
                String date = jsonObj.optString(JOB_CREATED_DATE);
                int serviceID = Integer.parseInt(jsonObj.optString(SERVICE_ID_TAG));
                int subServiceID = Integer.parseInt(jsonObj.optString(SUB_SERVICE_ID_TAG));
                String responses = (jobStatus == 0) ? "No Responses" : (jobStatus == 1) ? "("+ jobStatus +") Response" : "("+ jobStatus +") Responses";
                PendingJobs pendingJobs = new PendingJobs(jobId, jobStatus, responseStatus,jobTitle, lat, lon, jobDescription, price, date, responses,
                        serviceID, subServiceID, jsonObj.optString(SERVICE_DES_TAG),jsonObj.optString(SUB_SERVICE_DES_TAG));
                pendingJobsList.add(pendingJobs);
            }
        } catch (Exception e){

        }
        return pendingJobsList;
    }

    public ArrayList<AssignedJobs> parseAssignedJobs(JSONObject json) {
        ArrayList<AssignedJobs> arrayList = new ArrayList<AssignedJobs>();
        try {
            JSONArray result = json.getJSONArray(RESULT_TAG);
            for (int i = 0; i < result.length(); i++) {
                JSONObject jsonObj = result.getJSONObject(i);
                String encodedImage = jsonObj.optString(SEEKER_IMG_TAG);
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                AssignedJobs assignedJobs = new AssignedJobs(jsonObj.optInt(JOB_ID), jsonObj.optInt(SEEKER_ID_TAG), jsonObj.optInt(SUBSCRIBED_COUNT_TAG), jsonObj.getString(DESCRIPTION_TAG), jsonObj.optDouble(JOB_SEEKER_LAT), jsonObj.optDouble(JOB_SEEKER_LNG),
                        jsonObj.optString(JOB_BIDDING_AMT), jsonObj.optString(JOB_PROVIDER_NAME),jsonObj.optString(PROVIDER_MOBILE_NUM),jsonObj.optString(PROVIDER_MAIL_ID), jsonObj.optString(JOB_SEEKER_NAME), jsonObj.optString(SEEKER_MOBILE_TAG),
                        jsonObj.optString(SEEKER_RATING_TAG),jsonObj.optString(SEEKER_RATING_COUNT_TAG), jsonObj.optString(SERVICE_DES_TAG), jsonObj.optString(SUB_SERVICE_DES_TAG),
                        jsonObj.optString(JOB_CREATED_DATE),jsonObj.optString(JOB_ADDRESS), decodedByte,jsonObj.optString(SEEKER_EMAIL_ADDRESS), jsonObj.optString(START_DATE_TAG));
                arrayList.add(assignedJobs);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public ArrayList<AssignedJobs> parseOngoingJobs(JSONObject json) {
        ArrayList<AssignedJobs> arrayList = new ArrayList<AssignedJobs>();
        try {
            JSONArray result = json.getJSONArray(RESULT_TAG);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jsonObj = result.getJSONObject(i);
                AssignedJobs assignedJobs = new AssignedJobs(jsonObj.optInt(JOB_ID), jsonObj.optInt(SEEKER_ID),jsonObj.getString(SEEKER_ADDRESS_TAG),
                        jsonObj.optString(JOB_SEEKER_NAME), jsonObj.optString(SEEKER_RATING_TAG),jsonObj.optString(SEEKER_RATING_COUNT_TAG),
                        jsonObj.optDouble(JOB_LAT), jsonObj.optDouble(JOB_LNG), jsonObj.optString(SEEKER_MOBILE_TAG));
                arrayList.add(assignedJobs);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }


}
