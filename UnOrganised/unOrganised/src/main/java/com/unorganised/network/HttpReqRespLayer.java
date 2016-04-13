package com.unorganised.network;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.unorganised.activities.LoginActivity;
import com.unorganised.activities.RegisterActivity;
import com.unorganised.activities.SubServicesActivity;
import com.unorganised.adapters.AcceptedJobsListAdapter;
import com.unorganised.adapters.JobSeekerDrawerAdapter;
import com.unorganised.adapters.JobsListAdapter;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.views.AssignedJobFragment;
import com.unorganised.views.CompletedJobFragment;
import com.unorganised.views.UserSettingsFragment;

public class HttpReqRespLayer {

    private static HttpReqRespLayer httpReqRespLayer;

    private HttpReqRespLayer() {

    }

    public static HttpReqRespLayer getInstance() {
        if (httpReqRespLayer == null) {
            httpReqRespLayer = new HttpReqRespLayer();
        }
        return httpReqRespLayer;
    }

    /**
     * @param reqResp
     * @param activity
     * @param userName
     * @param mailId
     * @param pwd
     * @param phoneNumber
     */
    public void createUser(HttpRequestResponse reqResp, Activity activity, String userName, String mailId, String pwd, long phoneNumber, String regId) {
        JSONObject json = new JSONObject();
        try {
            json.put("fullname", userName);
            json.put("emailaddress", mailId);
            json.put("mobilenumber", phoneNumber);
            json.put("password", pwd);
            json.put("registrationid", regId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.CREATE_USER, reqResp, activity);
        asynTask.execute();
    }

    /**
     * @param reqResp
     * @param activity
     */
    public void getServices(HttpRequestResponse reqResp, Activity activity) {
        JSONObject json = new JSONObject();
        NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.GET_SERVICES, reqResp, activity);
        asynTask.execute();
    }

    /**
     * @param reqResp
     * @param activity
     * @param serviceId
     */
    public void getSubServices(HttpRequestResponse reqResp, Activity activity, int serviceId) {
        try {
            JSONObject json = new JSONObject();
            json.put("serviceid", serviceId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.GET_SUB_TYPE_SERVICES, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
        }
    }

    /**
     * @param reqResp
     * @param activity
     * @param latitude
     * @param longitude
     */
    public void getJobs(HttpRequestResponse reqResp, Activity activity, long userid, double latitude, double longitude) {
        try {
            JSONObject json = new JSONObject();
            json.put("jobseekeruserid", userid);
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.GET_JOBS, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
        }
    }

    /**
     * @param reqResp
     * @param activity
     * @param nUserID
     * @param OTP
     */
    public void validateOTP(HttpRequestResponse reqResp, Activity activity, long nUserID, long OTP) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", nUserID);
            json.put("otp", OTP);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.VALIDATE_OTP, reqResp, activity);
        asynTask.execute();
    }

    /**
     *  @param reqResp
     * @param activity
     * @param nUserID
     * @param accntNumber
     * @param selectedBank
     * @param iSFCCode
     * @param photoidType
     * @param bytPhoto
     * @param bytIDProof
     * @param bytCheque
     * @param adharNo
     * @param docId
     * @param bytDocument
     * @param strAge
     */
    public void updateUserProfile(HttpRequestResponse reqResp, Activity activity, long nUserID,
                                  String accntNumber, String selectedBank, String iSFCCode, int photoidType,
                                  String bytPhoto, String bytIDProof, String bytCheque, String adharNo, int docId, String bytDocument, String strAge) {

        JSONObject json = new JSONObject();
        try {
            json.put("userid", nUserID);
            json.put("bankaccountnumber", accntNumber);
            json.put("bankname", selectedBank);
            json.put("isfccode", iSFCCode);
            json.put("photoidprooftype", photoidType);
            json.put("userphoto", bytPhoto);
            json.put("photoidproof", bytIDProof);
            json.put("cancellationchequeleaf", bytCheque);
            json.put("aadhaarnumber", adharNo);
            json.put("certificateid", docId);
            json.put("certificate", bytDocument);
            json.put("dob", strAge);
            Log.d("Update profile", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.UPDATE_PROFILE, reqResp, activity);
        asynTask.execute();
    }

    public void updateseekerbankDetails(HttpRequestResponse reqResp, Activity activity, long userId, String acNo,String bName,String ifsc,String cancelcheque,String chequeformat) {
        try {
            JSONObject json = new JSONObject();
            json.put("userid", userId);
            json.put("bankaccountnumber", acNo);
            json.put("bankname",bName);
            json.put("ifsccode",ifsc);
            json.put("cancellationchequeleaf",cancelcheque);
            json.put("canellationchequeleafformat",chequeformat);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.UPDATE_SEEKER_BANK_DETAILS, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void updateproviderpersonalInfo(HttpRequestResponse reqResp, Activity activity, long userId, String uName,String uEmail,Double lattitude ,Double longitude,String uAddress) {
        try {
            JSONObject json = new JSONObject();
            json.put("userid", userId);
            json.put("fullname", uName);
            json.put("emailaddress",uEmail);
            json.put("latitude",lattitude);
            json.put("longitude",longitude);
            json.put("address",uAddress);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.UPDATE_PROVIDER_PERSONAL_DETAILS, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
    public void updateseekerpersonalInfo(HttpRequestResponse reqResp, Activity activity, long userId, String uName,String uEmail,Double lattitude ,Double longitude,String uAddress,String uImage,String uImageFormat) {
        try {
            JSONObject json = new JSONObject();
            json.put("userid", userId);
            json.put("fullname", uName);
            json.put("emailaddress",uEmail);
            json.put("latitude",lattitude);
            json.put("longitude",longitude);
            json.put("address",uAddress);
            json.put("userphoto",uImage);
            json.put("userphotoformat",uImageFormat);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.UPDATE_SEEKER_PERSONAL_DETAILS, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void changepassword(HttpRequestResponse reqResp, Activity activity, long userid,String Newpwd) {
        try {
            JSONObject json = new JSONObject();
            json.put("userid",userid);
           // json.put("password",oldpwd);
            json.put("password",Newpwd);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.CHANGE_PASSWORD, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
    public void resendOTP(HttpRequestResponse reqResp, Activity activity, long nUserID) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", nUserID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.RE_SEND_OTP, reqResp, activity);
        asynTask.execute();
    }
    public void retriveaddDetails(HttpRequestResponse reqResp, Activity activity, long nUserID) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", nUserID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.SEEKER_RETRIVE_ADD_DETAILS, reqResp, activity);
        asynTask.execute();
    }
    public void addSeekerDetails(HttpRequestResponse reqResp, Activity activity, long nUserID,String information) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", nUserID);

            json.put("additionalinformation",information);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.ADD_INFORMATION, reqResp, activity);
        asynTask.execute();
    }
    public void seekersubscribeddetails(HttpRequestResponse reqResp, Activity activity, long nUserID) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobseekerid", nUserID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.SEEKER_SUBSCRIBED_DETAILS, reqResp, activity);
        asynTask.execute();
    }

    public void getBanks(HttpRequestResponse reqResp, Activity activity) {
        JSONObject json = new JSONObject();
        NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.GET_BANKS, reqResp, activity);
        asynTask.execute();
    }


    public void updateSevicesAndSubServices(HttpRequestResponse reqResp, Activity activity, long userId, Map<Integer, List<Integer>> selServices, LinkedHashMap<String, List<String>> serviceData) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", userId);
            JSONArray services = new JSONArray();

            for (Map.Entry<Integer, List<Integer>> entry : selServices.entrySet()) {
                if (entry.getValue().size() == 0) {
                    JSONObject item = new JSONObject();
                    item.put("serviceid", entry.getKey());
                    services.put(item);
                } else {
                    for (Iterator<Integer> iterator = entry.getValue().iterator(); iterator.hasNext(); ) {
                        JSONObject item = new JSONObject();
                        item.put("serviceid", entry.getKey());
                        Integer id = iterator.next();
                        item.put("subserviceid", id);
                        services.put(item);
                    }
                }
            }
            if (serviceData != null){
                for(Map.Entry<String, List<String>> entry : serviceData.entrySet()){
                    if (entry.getValue().size() == 0){
                        JSONObject item = new JSONObject();
                        item.put("serviceid", Integer.parseInt(entry.getKey()));
                        services.put(item);
                    } else {
                        for (Iterator<String> iterator = entry.getValue().iterator(); iterator.hasNext(); ) {
                            JSONObject item = new JSONObject();
                            item.put("serviceid", Integer.parseInt(entry.getKey()));
                            String id = iterator.next();
                            item.put("subserviceid", Integer.parseInt(id));
                            services.put(item);
                        }
                    }
                }
            }
            json.put("services", services);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.UPDATE_SERVICES_SUB_SERVICES, reqResp, activity);
            asynTask.execute();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void updateSevicesAndGeoLocation(HttpRequestResponse reqResp, Activity activity, long userId, double lat, double lng) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", userId);
            json.put("latitude", lat);
            json.put("longitude", lng);

            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.UPDATE_GEO_LOCATION, reqResp, activity);
            asynTask.execute();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void updateUserType(HttpRequestResponse reqResp, Activity activity, long userId, int userType, String refCode) {
        try {
            JSONObject json = new JSONObject();
            json.put("userid", userId);
            json.put("usertype", userType);
            json.put("referencecode", refCode);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.UPDATE_USER_TYPE, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void resetMobileNum(HttpRequestResponse reqResp, Activity activity, long userId, long mobileNum) {
        try {
            JSONObject json = new JSONObject();
            json.put("userid", userId);
            json.put("mobilenumber", mobileNum);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.RESET_MOB_NUM, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void validateUser(HttpRequestResponse reqResp, Activity activity, long mobileNum, String pwd) {
        try {
            JSONObject json = new JSONObject();
            json.put("mobilenumber", mobileNum);
            json.put("password", pwd);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.VALIDATE_USER, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }


    public void postJob(HttpRequestResponse reqResp, Activity activity, String jobDetails, double latitude,
                        double longitude, long stlUserID, int serviceId, int subServiceID, String biddingAmount,
                        int paymentType, int jobType, String scheduledFrom, String scheduledTo, String address, String createdOn) {
        JSONObject json = new JSONObject();
        try {
            json.put("description", jobDetails);
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            json.put("provideruserid", stlUserID);
            json.put("serviceid", serviceId);
            json.put("subserviceid", subServiceID);
            json.put("biddingamont", biddingAmount);
            json.put("paymenttype", paymentType);
            json.put("jobtype", jobType);
            json.put("scheduledfrom", scheduledFrom);
            json.put("scheduledto", scheduledTo);
            json.put("address", address);
            json.put("createdon", createdOn);
            Log.d("postJob", json.toString());
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.POST_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void applyJob(HttpRequestResponse reqResp, Activity activity, long jobId, long seekerId) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            json.put("jobseekerid", seekerId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.APPLY_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void getJobDetails(HttpRequestResponse reqResp, Activity activity, long jobId) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.JOB_DETAIL, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void personalInfo(HttpRequestResponse reqResp, Activity activity, long userid) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", userid);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.PERSONAL_INFO, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void pendingJobs(HttpRequestResponse reqResp, Activity activity, long providerID) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobprovideruserid", providerID);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.PENDING_JOBS, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void updateSeekerLocation(HttpRequestResponse reqResp, Context context, long seekerId, double lat, double lng) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", seekerId);
            json.put("latitude", lat);
            json.put("longitude", lng);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.UPDATE_SEEKER_LOCATION, reqResp, context);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
            DebugLog.d("Exception while updating seeker location:" + e);
        }
    }


    public void assignSeeker(HttpRequestResponse reqResp, Activity activity, long jobId, long seekerId) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            json.put("jobseekerid", seekerId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.ASSIGN_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    public void getSeekerLocation(HttpRequestResponse reqResp, Activity activity, long seekerId) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", seekerId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.GET_SEEKER_LOCATION, reqResp, activity);
            asynTask.execute();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void getAcceptedJobs(HttpRequestResponse reqResp, Activity activity, long seekerId) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobseekeruserid", seekerId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.ACCEPTED_JOBS, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void getSeekerOnGoingJobs(HttpRequestResponse reqResp, Activity activity, long seekerId) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobseekeruserid", seekerId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.SEEKER_ON_GOING_JOBS, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void deleteJob(HttpRequestResponse reqResp, Activity activity, int jobId) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.DELETE_JOB, reqResp, activity);
            asynTask.execute();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void editJob(HttpRequestResponse reqResp, Activity activity, int jobId, String jobDetails, double latitude,
                        double longitude, int serviceId, int subServiceID, String biddingAmount,
                        int paymentType, int jobType, String scheduledFrom, String scheduledTo) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            json.put("description", jobDetails);
            json.put("latitude", latitude);
            json.put("longitude", longitude);
            json.put("serviceid", serviceId);
            json.put("subserviceid", subServiceID);
            json.put("biddingamont", biddingAmount);
            json.put("paymenttype", paymentType);
            json.put("jobtype", jobType);
            json.put("scheduledfrom", scheduledFrom);
            json.put("scheduledto", scheduledTo);
            Log.d("editJob", json.toString());
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.EDIT_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void seekerStartJob(HttpRequestResponse reqResp, Activity activity, long jobId,long seekerId) {
        JSONObject json =  new JSONObject();
        try
        {
            json.put("jobid",jobId);
            json.put("jobseekerid", seekerId);
            NetworkManagerAsynTask  asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.SEEKER_START_JOB, reqResp, activity);
            asynTask.execute();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void forgotPassword(HttpRequestResponse reqResp, Activity activity, long mobileNumber) {
        JSONObject json =  new JSONObject();
        try
        {
            json.put("mobilenumber",mobileNumber);
            NetworkManagerAsynTask  asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.FYP, reqResp, activity);
            asynTask.execute();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }


	public void assignedJobs(HttpRequestResponse reqResp, Activity activity, long providerId) {
		JSONObject json = new JSONObject();
		try {
			json.put("jobprovideruserid", providerId);
			NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.ASSIGNED_JOB, reqResp, activity);
			asynTask.execute();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

    public void getDocs(HttpRequestResponse reqResp, Activity activity) {
        JSONObject json = new JSONObject();
        NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.GET_CERTIFICATES, reqResp, activity);
        asynTask.execute();
    }

    public void providerStartJob(HttpRequestResponse reqResp, Activity activity, int jobId, long providerId, String formattedTime) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            json.put("jobproviderid", providerId);
            json.put("startedon", formattedTime);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.PROVIDER_START_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void getCompletedJobs(HttpRequestResponse reqResp, Activity activity, long providerId) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobprovideruserid", providerId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.PROVIDER_COMPLETED_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void providerOngoingJobs(HttpRequestResponse reqResp, Activity activity, long providerId) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobprovideruserid", providerId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.PROVIDER_ONGOING_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void finishProviderJob(HttpRequestResponse reqResp, Activity activity, int jobId, String date, String duration, float rating) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            json.put("completedon", date);
            json.put("durationhours", duration);
            json.put("rating", rating);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.PROVIDER_FINISH_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void seeketFinishJob(HttpRequestResponse reqResp, Activity activity, long jobId,long seekerId, float rating) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            json.put("jobseekerid", seekerId);
            json.put("rating", rating);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.SEEKER_FINISH_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void getSeekerCompletedJobs(HttpRequestResponse reqResp, Activity activity, long seekerId) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobseekeruserid", seekerId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.SEEKER_COMPLETED_JOBS, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void increaseBidding(HttpRequestResponse reqResp, Activity activity, int jobId, long providerId, int biddingAmount) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            json.put("jobproviderid", providerId);
            json.put("biddingamont", biddingAmount);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.EDIT_BIDDING, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void cancelProviderJob(HttpRequestResponse reqResp, Activity activity, int jobId, long providerId) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            json.put("jobproviderid", providerId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.PROVIDER_CANCEL_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void providerCancelAssignedJob(HttpRequestResponse reqResp, Activity activity, int jobId, long providerId) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            json.put("jobseekerid", providerId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.PROVIDER_CANCEL_ASSIGN_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void cancelAppliedJob(HttpRequestResponse reqResp, Activity activity, long jobId, long seekerId) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            json.put("jobseekerid", seekerId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.SEEKER_CANCEL_APPLY_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void seekerCancelAcceptedJob(HttpRequestResponse reqResp, Activity activity, long jobId, long seekerId) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            json.put("jobseekerid", seekerId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.SEEKER_CANCEL_ACCEPT_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void seekerCancelStartJob(HttpRequestResponse reqResp, Activity activity, long jobId, long stlUserID) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            json.put("jobseekerid", stlUserID);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.SEEKER_CANCEL_START_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void subscribe(HttpRequestResponse reqResp, Activity activity, int jobId, long providerId, int seekerId, String frmDate, String toDate, int frequency) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            json.put("jobproviderid", providerId);
            json.put("jobseekerid", seekerId);
            json.put("scheduledfrom", frmDate);
            json.put("scheduledto", toDate);
            json.put("frequency", frequency);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.JOB_SUBSCRIBE, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void unSubscribe(HttpRequestResponse reqResp, Activity activity, int jobId, long providerId, int seekerId) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobid", jobId);
            json.put("jobproviderid", providerId);
            json.put("jobseekerid", seekerId);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.JOB_UNSUBSCRIBE, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void updateNotification(HttpRequestResponse reqResp, Activity activity, long stlUserID, int status) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", stlUserID);
            json.put("notificationstatus", status);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.NOTIFICATION_STATUS, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void deleteProviderCompletedJob(HttpRequestResponse reqResp, Activity activity, long stlUserID, String jobIds) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobids", jobIds);
            json.put("jobproviderid", stlUserID);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.DELETE_COMPLETED_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void deleteProviderallCompletedJob(HttpRequestResponse reqResp, Activity activity, long stlUserID, String jobIds) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobids", jobIds);
            json.put("jobproviderid", stlUserID);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.DELETE_ALL_JOBS, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void deleteSeekerCompletedJob(HttpRequestResponse reqResp, Activity activity, long stlUserID, String jobIds) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobids", jobIds);
            json.put("jobseekerid", stlUserID);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.DELETE_SEEKER_COMPLETED_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void deleteSeekerallCompletedJob(HttpRequestResponse reqResp, Activity activity, long stlUserID, String jobIds) {
        JSONObject json = new JSONObject();
        try {
            json.put("jobids", jobIds);
            json.put("jobseekerid", stlUserID);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.DELETE_SEEKER_ALL_COMPLETED_JOB, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void getReferralCode(HttpRequestResponse reqResp, Activity activity, long stlUserID) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", stlUserID);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.REQUEST_REFERRAL_CODE, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void getUserServices(HttpRequestResponse reqResp, Activity activity, long stlUserID) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", stlUserID);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.REQUEST_USER_SERVICE, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void updateUserStatus(HttpRequestResponse reqResp, Activity activity, long stlUserID, int status) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", stlUserID);
            json.put("userstatus", status);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.UPDATE_USER_STATUS, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void getUserStatus(HttpRequestResponse reqResp, Activity activity, long stlUserID) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", stlUserID);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.GET_USER_STATUS, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void getNotificationStatus(HttpRequestResponse reqResp, Activity activity, long stlUserID) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", stlUserID);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.GET_NOTIFICATION_STATUS, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    public void updateRegId(HttpRequestResponse reqResp, Activity activity, long stlUserID, String regid) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", stlUserID);
            json.put("registrationid", regid);
            NetworkManagerAsynTask asynTask = new NetworkManagerAsynTask(json, Constants.HttpReqRespActionItems.UPDATE_REG_ID, reqResp, activity);
            asynTask.execute();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
