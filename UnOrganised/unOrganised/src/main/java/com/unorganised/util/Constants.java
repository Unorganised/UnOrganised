package com.unorganised.util;

public interface Constants {

    int JOB_NOT_ACCEPTED = 1;
    int JOB_ACCEPTED = 2;
    int JOB_ACCEPTED_BY = 3;
    int JOB_STATUS_NOT_APPLIED = 0;
    int JOB_STATUS_APPLIED = 1;
    int JOB_STATUS_ASSIGNED = 2;
    int JOB_STATUS_ACCEPTED = 3;
    int SEEKER_LOCATION_UPDATE_TIME_INTERVAL = 2 * 60 * 1000;
    int MAX_SIZE = 500000;
    int MAX_OVER_SIZE = 1000000;
    int US_NOT_CREATED = 1;
    int US_CREATED = 2;
    int US_USER_TYPE_SELECTED = 3;
    int US_REGISTERED = 4;
    int NF_POST_JOB = 1;
    int NF_ACCEPT_JOB = 2;
    int NF_ASSIGN_JOB = 3;
    int NF_START_JOB_SEEKER = 4;
    int NF_START_JOB_PROVIDER = 5;
    int NF_STOP_JOB_SEEKER = 6;
    int NF_STOP_JOB_PROVIDER = 7;
    int NF_SHOW_LOCATION = 8;
    float SEEKER_LOCATION_UPDATE_DIST_INTERVAL = 0f;
    long SPLASH_WAIT_TIME_IN_MILLIS = 4000;
    String SERVICE_PROVIDER = "ServiceProvider";
    String SERVICE_SEEKER = "Serviceseeker";
    String USER_NAME = "user_name";
    String CALL_CENTER_NUMBER = "+919986014709";

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "921144154085";
    int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
//    04/04/16- To publish
//    String MAPS_BROWSER_KEY = "AIzaSyAFKBHW_p3JuURLlAuknuQCAMqB5knhhVY";
//    21/03/16
//    String MAPS_BROWSER_KEY = "AIzaSyALli1DZyBENmzbPp576DNbFt8FEyzsvaQ";
//    For Registration release
//    String MAPS_BROWSER_KEY = "AIzaSyBSjBMzgW3pB_8Z-jsMZX8IokAv3dFnIWk";
//    7/03/16
//    String MAPS_BROWSER_KEY = "AIzaSyBFctaOsI70mOaLw2alZ9JDOwGpiU6R8Cc";
//    Created for Release
    String MAPS_BROWSER_KEY = "AIzaSyBKr2j1Y5RNqJhdD6JLbgp2BPEEqMPljcc";
//
//    String MAPS_BROWSER_KEY = "AIzaSyDmWPdqJAspIs-VCllWTyDduiSNcC9gSMI";
//    String MAPS_BROWSER_KEY = "AIzaSyCSL0IlzD1V5vp4mvCwJ8A6EBZZ9G5AWok";
//    String MAPS_BROWSER_KEY = "AIzaSyDBIzV-Yq2ty1DgHoGd1BlG65fJ3ZfS5Xc";
    String KEY_MAPS_LAUNCHER_TYPE = "maps_launcher_type";
    String SEL_SERVICE = "selected_service";
    String DESCRIPTION_TAG = "Description";
    String SERVICE_ID_TAG = "Service_Id";
    String SUB_SERVICE_COUNT_TAG = "Sub_Services_Cnt";
    String SUB_SERVICE_DES_TAG = "Sub_Service_" + DESCRIPTION_TAG;
    String SUB_SERVICE_ID_TAG = "Sub_" + SERVICE_ID_TAG;
    String RESULT_TAG = "Result";
    String REF_COD_TAG = "referencecode";
    String BANK_NAME_TAG = "Bank_Name";
    String CERTIFICATE_NAME_TAG = "Certificate_Name";
    String CERTIFICATE_ID_TAG = "Certificate_Id";
    String SEL_SERVICE_ID = SEL_SERVICE + "_id";
    String USER_ID = "userid";
    String FULL_NAME_TAG = "Full_Name";
    String EMAIL_ID_TAG = "Email_Address";
    String MOBILE_NUMBER_TAG = "Mobile_Number";
    String EDIT_JOB = "EditJob";
    String SERVICES = "services";
    String EXCEPTION_MSG = "Message";
    String USER_TYPE_KEY = "usertype";
    String USER_SETTINGS = "User_Settings";
    String MESSAGE = "Message";
    String SHARED_PREFERENCES_FILE = "unorganises_shared_preferences";
    String KEY_LOCATION_ACTIVITY_LAUNCHER_TYPE = "location_activity_launcher_type";
    String KEY_LOCATION_NAME = "LocationName";
    String KEY_ADDRESS = "Address";
    String KEY_LNG = "Longitude";
    String KEY_LAT = "Latitude";
    String SP_USER_ID_KEY = "user_id";
    String SP_LOGIN_STATUS = "login_status";
    String SP_MOB_NUMBER = "mobile_num";
    String SP_USER_TYPE = "user_type";
    String SP_USER_LAT = "user_lat";
    String SP_USER_LNG = "user_lng";
    String SP_GCM_REG_ID = "gcmId";
    String SP_PROPERTY_APP_VERSION = "app_version";
    String COMPLETED_DATE = "CompletedDate";
    String DURATION = "Duration";
    String START_DATE_TAG = "Started_On";
    String JOB_ID = "Job_Id";
    String JOB_DES = "Description";
    String JOB_LAT = "Latitude";
    String JOB_LNG = "Longitude";
    String JOB_CREATED_DATE = "Created_On";
    String JOB_SCHEDULED_DATE = "Scheduled_On";
    String JOB_SERVICE_ID = "Service_Id";
    String JOB_SUB_SERVICE_ID = "Sub_Service_Id";
    String JOB_BIDDING_AMT = "Bidding_Amont";
    String JOB_PROVIDER_ID = "Job_Provider_User_Id";
    String JOB_PROVIDER_NAME = "Job_Provider_Full_Name";
    String JOB_PROVIDER_IMAGE = "Job_Provider_Photo";
    String JOB_SEEKER_NAME = "Job_Seeker_Full_Name";
    String JOB_PROVIDER_NUM = "Job_Provider_Mobile_Number";
    String JOB_SERVICE_DESC = "Service_Description";
    String JOB_SUB_SERVICE_DESC = "Sub_Service_Description";
    String JOB_RESPONSE_COUNT = "Responses_Count";
    String JOB_PRICE_TAG = "Bidding_Amont";
    String JOB_PAYMENT_TYPE = "Payment_Type";
    String SEEKER_TAG = "Seekers";
    String SEEKER_ID = "Seeker_Id";
    String SEEKER_ID_TAG = "Job_Seeker_User_Id";
    String STATUS_TAG = "Status";
    String SEEKER_FULL_NAME_TAG = "Job_Seeker_Full_Name";
    String SEEKER_MOBILE_TAG = "Job_Seeker_Mobile_Number";
    String SEEKER_RATING_TAG = "Seeker_Rating";
    String JOB_SEEKER_RATING_TAG = "Job_Seeker_Rating";
    String SEEKER_RATING_COUNT_TAG = "Seeker_Rating_Count";
    String SEEKER_TOTAL_RATING_TAG = "Seeker_Rating_On_Job";
    String SEEKER_IMG_TAG = "Job_Seeker_Photo";
    String SEEKER_ADDRESS_TAG = "Job_Seeker_Address";
    String JOB_STATUS = "Seeker_Status";
    String JOB_ADDRESS = "Address";
    String SEEKER_LAT = "Current_Location_Latitude";
    String SEEKER_LNG = "Current_Location_Longitude";
    String JOB_SEEKER_LAT = "Job_Seeker_Latitude";
    String JOB_SEEKER_LNG = "Job_Seeker_Longitude";
    String KEY_LOCATION_DISPLAY_LAUNCHER_TYPE = "location_display_launcher_typ";
    String DRAWER_ITEM_POSITION = "drawer_item_position";
    String GCM_MSG = "message";
    String GCM_NOTIFICATION_TYPE = "notification_type";
    String GCM_JOB_ID = "job_id";
    String GCM_SEEKER_ID = "seeker_id";
    String SERVICE_DES_TAG = "Service_Description";
    String PENDING_JOB_OBJ = "Job";
    String PROVIDER_RATING = "Provider_Rating";
    String SEEKER_RATING = "Provider_Rating";
    String PROVIDER_RATING_COUNT = "Provider_Rating_Count";
    String SEEKER_RATING_COUNT = "Seeker_Rating_Count";
    String JOB_SEEKER_RATING_COUNT = "Job_Seeker_Rating_Count";
    String PROVIDER_RATING_ON_JOB = "Provider_Rating_On_Job";
    String PROVIDER_MOBILE_NUM = "Job_Provider_Mobile_Number";
    String PROVIDER_MAIL_ID = "Job_Provider_Email_Address";
    String PROVIDER_FULL_NAME = "Job_Provider_Full_Name";
    String SUBSCRIPTION_FROM = "Subscription_From";
    String SUBSCRIPTION_TO = "Subscription_To";
    String SUBSCRIPTION_STATUS = "Subscription_Status";
    String SUBSCRIBED_COUNT_TAG = "Seeker_Subscribed_Jobs_Count";
    String FREQUENCY = "Frequency";
    String SP_NOTIFICATION_STATUS = "Notification_status";
    String USER_PHOTO = "User_Photo";
    String ADDITIONAL_INFORMATION = "Additional_Information";
    String DETAILS = "Details";
    String PERSONAL_INFORMATION = "Personal Information";
    String EDIT_BANK_DETAILS = "Edit Bank Details";
    String USER_ADDITIONAL_INFORMATION = "User Additional Information";
    String REGISTER_AS = "Register as";
    String INVITE_FRIENDS = "Refer A Friend";
    String CHANGE_PASSWORD = "Change Password";
    String ADD_YOUR_ROLE = "Add Your Skills";
    String ROLE_SETTING = "Role Setting";
    String SEEKER_EMAIL_ADDRESS = "Job_Seeker_Email_Address";
    String USER_STATUS_TAG = "User_Status";
    String USER_NOTIFICATION_STATUS_TAG = "Notification_Status";
    String SCREEN = "";
    String IS_NOTIFICATION = "Is notification";

    enum LOCATION_ACTIVITY_LAUNCHER_TYPE {
        USER_LOCATION, POST_JOB_LOCATION
    }

    enum PAYMENT_TYPE {
        CASH, ONLINE
    }

    enum JOB_TYPE {
        ONE_TIME, WEEKLY, MONTHLY
    }

    enum MAPS_LAUNCHER_TYPE {
        REGISTER, SEARCH
    }

    enum HttpReqRespActionItems {
        CREATE_USER, VALIDATE_OTP, RE_SEND_OTP, GET_BANKS, GET_CERTIFICATES, UPDATE_PROFILE, LOGIN, GET_SERVICES, GET_SUB_TYPE_SERVICES, GET_JOBS, GET_JPBS_IN_RADION,
        UPDATE_GEO_LOCATION, UPDATE_SERVICES_SUB_SERVICES, UPDATE_USER_TYPE, RESET_MOB_NUM, VALIDATE_USER, POST_JOB, APPLY_JOB, PENDING_JOBS, JOB_DETAIL,
        ASSIGN_JOB, ASSIGNED_JOB, ACCEPTED_JOBS, UPDATE_SEEKER_LOCATION, GET_SEEKER_LOCATION, DELETE_JOB, EDIT_JOB, SEEKER_START_JOB, SEEKER_ON_GOING_JOBS, FYP, PROVIDER_START_JOB,
        PROVIDER_COMPLETED_JOB, PROVIDER_ONGOING_JOB, PROVIDER_FINISH_JOB, PROVIDER_STOP_JOB, SEEKER_FINISH_JOB, EDIT_BIDDING, PROVIDER_CANCEL_JOB, PROVIDER_CANCEL_ASSIGN_JOB,
        SEEKER_COMPLETED_JOBS, SEEKER_CANCEL_ACCEPT_JOB, SEEKER_CANCEL_START_JOB, JOB_SUBSCRIBE, SEEKER_CANCEL_APPLY_JOB, PERSONAL_INFO, CHANGE_PASSWORD, UPDATE_SEEKER_BANK_DETAILS,
        UPDATE_PROVIDER_PERSONAL_DETAILS, JOB_UNSUBSCRIBE, UPDATE_SEEKER_PERSONAL_DETAILS, NOTIFICATION_STATUS, SEEKER_SUBSCRIBED_DETAILS, ADD_INFORMATION, SEEKER_RETRIVE_ADD_DETAILS,
        DELETE_COMPLETED_JOB, DELETE_ALL_JOBS, DELETE_SEEKER_COMPLETED_JOB, DELETE_SEEKER_ALL_COMPLETED_JOB, REQUEST_USER_SERVICE, UPDATE_USER_STATUS, GET_USER_STATUS, GET_NOTIFICATION_STATUS, UPDATE_REG_ID, REQUEST_REFERRAL_CODE
    }

    enum JOB_STATUS_TYPE {
        NOT_APPLIED, APPLIED, ASSIGNED, ACCEPTED
    }

    enum LOCATION_DISPLAY_LAUNCHER_TYPE {
        SEEKER, PROVIDER
    }

}
