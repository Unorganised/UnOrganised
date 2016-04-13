package com.unorganised.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by anarra on 18/12/15.
 */
public class JobsParser implements Constants {

    /*
         * Gets searched jobs as list
         */
    public ArrayList<Job> parseSearchJobs(JSONObject json) {
        ArrayList<Job> jobslist = new ArrayList<Job>();
        JSONArray result = null;

        try {
            result = json.getJSONArray(RESULT_TAG);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jsonObject = result.getJSONObject(i);
                double lng = jsonObject.optDouble(JOB_LNG);
                double lat = jsonObject.optDouble(JOB_LAT);
                int status = jsonObject.optInt(JOB_STATUS);
                String encodedImage = jsonObject.optString(JOB_PROVIDER_IMAGE);
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                String address = jsonObject.optString(JOB_ADDRESS);
                if (TextUtils.isEmpty(address)) {
                    address = "";
                }
                Job job = new Job(jsonObject.optString(JOB_PROVIDER_NAME), jsonObject.optString(JOB_DES), address, lat, lng, jsonObject.optDouble(JOB_BIDDING_AMT),
                        jsonObject.optString(JOB_CREATED_DATE), jsonObject.optString(JOB_SCHEDULED_DATE), jsonObject.optLong(JOB_ID), jsonObject.optLong(JOB_SERVICE_ID),
                        jsonObject.optLong(JOB_SUB_SERVICE_ID), jsonObject.optLong(JOB_PROVIDER_ID), jsonObject.optLong(JOB_PROVIDER_NUM), jsonObject.optString(JOB_SERVICE_DESC),
                        jsonObject.optString(JOB_SUB_SERVICE_DESC), status, decodedByte);
                jobslist.add(job);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;

        }
        return jobslist;
    }

    public ArrayList<SubscribedDetails> subscribedJobs(JSONObject json) {
        ArrayList<SubscribedDetails> Subscribedlist = new ArrayList<SubscribedDetails>();
        JSONArray result = null;

        try {
            result = json.getJSONArray(RESULT_TAG);
            Log.d("result==", Integer.toString(result.length()));
            for (int i = 0; i < result.length(); i++) {
                Log.d("looping", "1");
                JSONObject jsonObject = result.getJSONObject(i);


                SubscribedDetails details = new SubscribedDetails(jsonObject.optLong(JOB_ID), jsonObject.optString(JOB_PROVIDER_NAME), jsonObject.optString(JOB_SERVICE_DESC),
                        jsonObject.optString(SUBSCRIPTION_FROM), jsonObject.optString(SUBSCRIPTION_TO), jsonObject.optString(FREQUENCY));
                Subscribedlist.add(details);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;

        }
        return Subscribedlist;
    }


    /*
    * Gets searched jobs as list
    */
    public ArrayList<Job> parseAcceptedJobs(JSONObject json) {
        ArrayList<Job> jobslist = new ArrayList<Job>();
        JSONArray result = null;
        try {
            result = json.getJSONArray(RESULT_TAG);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jsonObject = result.getJSONObject(i);
                double lng = jsonObject.optDouble(JOB_LNG);
                double lat = jsonObject.optDouble(JOB_LAT);
                int status = jsonObject.optInt(JOB_STATUS);
                String encodedImage = jsonObject.optString(JOB_PROVIDER_IMAGE);
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                String address = jsonObject.optString(JOB_ADDRESS);
                if (TextUtils.isEmpty(address)) {
                    address = "";
                }
                Job job = new Job(jsonObject.optString(JOB_PROVIDER_NAME), jsonObject.optString(JOB_DES), address, lat, lng, jsonObject.optDouble(JOB_BIDDING_AMT),
                        jsonObject.optString(JOB_CREATED_DATE), jsonObject.optString(JOB_SCHEDULED_DATE), jsonObject.optLong(JOB_ID), jsonObject.optLong(JOB_SERVICE_ID),
                        jsonObject.optLong(JOB_SUB_SERVICE_ID), jsonObject.optLong(JOB_PROVIDER_ID), jsonObject.optLong(JOB_PROVIDER_NUM), jsonObject.optString(JOB_SERVICE_DESC),
                        jsonObject.optString(JOB_SUB_SERVICE_DESC), status, decodedByte);
                jobslist.add(job);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;

        }
        return jobslist;
    }

    /**
     * Parse a job
     *
     * @param jsonObject
     * @return
     */
    public Job parseJob(JSONObject jsonObject) {
        JSONObject result = null;
        Job job = null;
        try {
            result = jsonObject.getJSONObject(RESULT_TAG);
            double lng = result.optDouble(JOB_LNG);
            double lat = result.optDouble(JOB_LAT);
            int status = result.optInt(JOB_STATUS);
            String encodedImage = result.optString(JOB_PROVIDER_IMAGE);
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            String address = result.optString(JOB_ADDRESS);

            JSONArray seeker = result.getJSONArray(SEEKER_TAG);
            for (int j = 0; j < seeker.length(); j++) {
                JSONObject obj = seeker.getJSONObject(j);
                if (Utility.stlUserID == obj.getLong(SEEKER_ID_TAG)){
                    status = obj.getInt(STATUS_TAG);
                }
            }

            if (TextUtils.isEmpty(address)) {
                address = "";
            }
            job = new Job(result.optInt(PROVIDER_RATING_COUNT),(float)(result.optDouble(PROVIDER_RATING)), result.optString(JOB_PROVIDER_NAME), result.optString(JOB_DES),
                    address, lat, lng, result.optDouble(JOB_BIDDING_AMT), result.optString(JOB_CREATED_DATE), result.optString(JOB_SCHEDULED_DATE), result.optLong(JOB_ID),
                    result.optLong(JOB_SERVICE_ID),result.optLong(JOB_SUB_SERVICE_ID), result.optLong(JOB_PROVIDER_ID), result.optLong(JOB_PROVIDER_NUM),
                    result.optString(JOB_SERVICE_DESC),result.optString(JOB_SUB_SERVICE_DESC), status, decodedByte);
            return job;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public ArrayList<SeekerData> parseSeekerJobList(JSONObject data) {
        ArrayList<SeekerData> seekerDataList = new ArrayList<SeekerData>();
        JSONObject result = null;

        try {
            result = data.getJSONObject(RESULT_TAG);
            JSONArray seeker = result.getJSONArray(SEEKER_TAG);

            for (int j = 0; j < seeker.length(); j++) {
                JSONObject obj = seeker.getJSONObject(j);
                String encodedImage = obj.optString(SEEKER_IMG_TAG);
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                SeekerData seekerData = new SeekerData(obj.optInt(JOB_ID), obj.getInt(SEEKER_ID_TAG), obj.optInt(STATUS_TAG), obj.optInt(JOB_SEEKER_RATING_COUNT),
                        obj.optDouble(JOB_SEEKER_LAT), obj.optDouble(JOB_SEEKER_LNG), obj.optString(SEEKER_FULL_NAME_TAG), obj.optLong(SEEKER_MOBILE_TAG),
                        (float) obj.optDouble(JOB_SEEKER_RATING_TAG), decodedByte, obj.optString(SEEKER_ADDRESS_TAG));
                seekerDataList.add(seekerData);
            }
        } catch (Exception e) {

        }
        return seekerDataList;
    }

    public ArrayList<SeekerData> parseCompletedJobList(JSONObject data) {
        StringBuilder sb=new StringBuilder();
        ArrayList<SeekerData> arrayList = new ArrayList<SeekerData>();
        try {
            JSONArray result = data.getJSONArray(RESULT_TAG);
            for (int i = 0; i < result.length(); i++) {
                JSONObject obj = result.getJSONObject(i);
                double lat = obj.optDouble(JOB_SEEKER_LAT);
                double lng = obj.optDouble(JOB_SEEKER_LNG);
                String encodedImage = obj.optString(SEEKER_IMG_TAG);
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                int paymentType = Integer.parseInt(obj.optString(JOB_PAYMENT_TYPE));
                String paymentTypeVal = (paymentType == 0) ? "Paid Cash" : "Paid Online";
                sb.append(obj.optInt(JOB_ID)+",");
                //Log.d("complete",Utility.strCompleteJobId);
                SeekerData seekerData = new SeekerData(obj.optInt(JOB_ID), obj.optInt(SEEKER_ID_TAG), lat, lng, (float) obj.optDouble(PROVIDER_RATING_ON_JOB), decodedByte, obj.optString(SEEKER_FULL_NAME_TAG),
                        obj.optString(SERVICE_DES_TAG), obj.optString(JOB_CREATED_DATE), obj.optString(JOB_BIDDING_AMT), paymentTypeVal, obj.optInt(SUBSCRIPTION_STATUS));
                arrayList.add(seekerData);
            }
            String JobLength=sb.toString();
            JobLength=JobLength.substring(0,JobLength.length()-1);
            Log.d("Sb=",JobLength);
            Utility.strCompleteJobId=JobLength;
        } catch (Exception e) {

        }
        return arrayList;
    }

    public ArrayList<Provider> parseSeekerCompletedJobList(JSONObject data) {
        StringBuilder sb=new StringBuilder();
        ArrayList<Provider> providerList = new ArrayList<Provider>();
        try {
            JSONArray result = data.getJSONArray(RESULT_TAG);
            for (int i = 0; i < result.length(); i++) {
                JSONObject obj = result.getJSONObject(i);
                String encodedImage = obj.optString(JOB_PROVIDER_IMAGE);
                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                sb.append(obj.optInt(JOB_ID) + ",");
                //TODO:Need to parse and return the provider list.
                Provider provider = new Provider(obj.getInt(PROVIDER_RATING_ON_JOB), obj.getInt(PROVIDER_RATING), obj.getInt(PROVIDER_RATING_COUNT), null, 0d, 0d,
                        obj.optString(PROVIDER_FULL_NAME), obj.optString(JOB_ADDRESS), obj.getInt(JOB_ID), obj.optString(JOB_DES), obj.getInt(SERVICE_ID_TAG),
                        obj.getInt(SUB_SERVICE_ID_TAG), obj.optString(SERVICE_DES_TAG), obj.optString(SUB_SERVICE_DES_TAG), obj.getLong(PROVIDER_MOBILE_NUM),
                        obj.optDouble(JOB_BIDDING_AMT), obj.optString(JOB_CREATED_DATE), decodedByte);
                providerList.add(provider);
            }

            String JobLength=sb.toString();
            JobLength=JobLength.substring(0,JobLength.length()-1);
            Log.d("Sb=",Integer.toString(JobLength.length()));
            Utility.strCompleteJobId=JobLength;


        } catch (Exception e) {

        }
        return providerList;
    }

}
