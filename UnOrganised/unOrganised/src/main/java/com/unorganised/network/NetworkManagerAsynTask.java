package com.unorganised.network;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.unorganised.R;
import com.unorganised.util.Constants;
import com.unorganised.util.DebugLog;
import com.unorganised.util.Utility;
import com.unorganised.util.Constants.HttpReqRespActionItems;

public class NetworkManagerAsynTask extends AsyncTask<Void, Void, Void> {
    private boolean isExceptionRaised;
    private String TAG = NetworkManagerAsynTask.class.getSimpleName();
    private String mainUri = "http://dev-unorganise.rhcloud.com/Service/api/utility";
    private JSONObject data = null;
    private String subUri;
    private HttpReqRespActionItems dataType;
    private HttpRequestResponse callBack;
    private Context activity;
    private ProgressDialog progressDialog;
    private String errorMsg;
    private String URL;

    public NetworkManagerAsynTask(JSONObject data, HttpReqRespActionItems dataType,
                                  HttpRequestResponse callBack, Context activity) {
        this.data = data;
        this.dataType = dataType;
        this.callBack = callBack;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (activity instanceof Activity) {
            showProgress();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            switch (dataType) {
                case CREATE_USER:
                    subUri = "/createuser";
                    break;
                case VALIDATE_OTP:
                    subUri = "/validateotp";
                    break;
                case RE_SEND_OTP:
                    subUri = "/resetotp";
                    break;
                case UPDATE_USER_STATUS:
                    subUri = "/updateuserstatus";
                    break;
                case GET_USER_STATUS:
                    subUri = "/getuserstatus";
                    break;
                case GET_NOTIFICATION_STATUS:
                    subUri = "/getnotificationstatus";
                    break;
                case GET_BANKS:
                    subUri = "/banks";
                    break;
                case GET_CERTIFICATES:
                    subUri = "/certificates";
                    break;
                case UPDATE_PROFILE:
                    subUri = "/updateuserprofile";
                    break;
                case GET_SERVICES:
                    subUri = "/service";
                    break;
                case GET_SUB_TYPE_SERVICES:
                    subUri = "/subservice";
                    break;
                case REQUEST_USER_SERVICE:
                    subUri = "/userservice";
                    break;
                case GET_JOBS:
                    subUri = "/seekersearchjobslocation";
                    break;
                case UPDATE_GEO_LOCATION:
                    subUri = "/updateusergeodetails";
                    break;
                case UPDATE_SERVICES_SUB_SERVICES:
                    subUri = "/updateuserservices";
                    break;
                case UPDATE_USER_TYPE:
                    subUri = "/updateusertype";
                    break;
                case RESET_MOB_NUM:
                    subUri = "/resetusermobilenumber";
                    break;
                case VALIDATE_USER:
                    subUri = "/validateuser";
                    break;
                case POST_JOB:
                    subUri = "/postjob";
                    break;
                case EDIT_JOB:
                    subUri = "/editjob";
                    break;
                case APPLY_JOB:
                    subUri = "/jobseekerapplyjob";
                    break;
                case PENDING_JOBS:
                    subUri = "/jobproviderpendingjobs";
                    break;
                case JOB_DETAIL:
                    subUri = "/jobdetails";
                    break;
                case ASSIGN_JOB:
                    subUri = "/jobproviderassignjob";
                    break;
                case ASSIGNED_JOB:
                    subUri = "/jobproviderassignedjobs";
                    break;
                case ACCEPTED_JOBS:
                    subUri = "/jobseekerassignedjobs";
                    break;
                case UPDATE_SEEKER_LOCATION:
                    subUri = "/updateusercurrentlocation";
                    break;
                case GET_SEEKER_LOCATION:
                    subUri = "/getusercurrentlocation";
                    break;
                case DELETE_JOB:
                    subUri = "/deletejob";
                    break;
                case SEEKER_START_JOB:
                    subUri = "/jobseekerstartjob";
                    break;
                case PROVIDER_START_JOB:
                    subUri = "/jobproviderstartjob";
                    break;
                case PROVIDER_STOP_JOB:
                    subUri = "/jobproviderstopjob";
                    break;
                case PROVIDER_CANCEL_JOB:
                    subUri = "/jobprovidercancelstartjob";
                    break;
                case PROVIDER_CANCEL_ASSIGN_JOB:
                    subUri = "/jobprovidercancelassignjob";
                    break;
                case PROVIDER_COMPLETED_JOB:
                    subUri = "/jobprovidercompletedjobs";
                    break;
                case PROVIDER_ONGOING_JOB:
                    subUri = "/jobproviderongoingjobs";
                    break;
                case PROVIDER_FINISH_JOB:
                    subUri = "/jobproviderfinishjob";
                    break;
                case SEEKER_ON_GOING_JOBS:
                    subUri = "/jobseekerongoingjobs";
                    break;
                case FYP:
                    subUri = "/forgotpassword";
                    break;
                case SEEKER_FINISH_JOB:
                    subUri = "/jobseekerfinishjob";
                    break;
                case SEEKER_COMPLETED_JOBS:
                    subUri = "/jobseekercompletedjobs";
                    break;
                case SEEKER_CANCEL_APPLY_JOB:
                    subUri = "/jobseekercancelapplyjob";
                    break;
                case SEEKER_CANCEL_ACCEPT_JOB:
                    subUri = "/jobseekercancelassignedjob";
                    break;
                case SEEKER_CANCEL_START_JOB:
                    subUri = "/jobseekercancelstartjob";
                    break;
                case EDIT_BIDDING:
                    subUri = "/updatebiddingamount";
                    break;
                case JOB_SUBSCRIBE:
                    subUri = "/jobsubscribe";
                    break;
                case JOB_UNSUBSCRIBE:
                    subUri = "/jobunsubscribe";
                    break;
                case PERSONAL_INFO:
                    subUri = "/userdetails";
                    break;
                case CHANGE_PASSWORD:
                    subUri = "/changepassword";
                    break;
                case UPDATE_SEEKER_BANK_DETAILS:
                    subUri = "/updateseekerbankdetails";
                    break;
                case UPDATE_PROVIDER_PERSONAL_DETAILS:
                    subUri = "/updateproviderpersonalinformation";
                    break;
                case UPDATE_SEEKER_PERSONAL_DETAILS:
                    subUri = "/updateseekerpersonalinformation";
                    break;
                case SEEKER_SUBSCRIBED_DETAILS:
                    subUri = "/jobseekersubscribedjobs";
                    break;
                case NOTIFICATION_STATUS:
                    subUri = "/updatenotificationstatus";
                    break;
                case ADD_INFORMATION:
                    subUri = "/updateuseradditionalinformation";
                    break;

                case SEEKER_RETRIVE_ADD_DETAILS:
                    subUri = "/useradditionalinformation";
                    break;
                case DELETE_COMPLETED_JOB:
                    subUri = "/updatejobproviderarchivejobs";

                    break;
                case DELETE_SEEKER_COMPLETED_JOB:
                    subUri = "/updatejobseekerarchivejobs";
                    break;
                case DELETE_ALL_JOBS:
                    subUri = "/updatejobproviderarchivejobs";
                    break;
                case DELETE_SEEKER_ALL_COMPLETED_JOB:
                    subUri = "/updatejobseekerarchivejobs";
                    break;
                case REQUEST_REFERRAL_CODE:
                    subUri = "/generatereferancecode";
                    break;
                case UPDATE_REG_ID:
                    subUri = "/updateregistrationid";
                    break;
                default:
                    break;
            }
            URL = mainUri + subUri;

            Log.d(TAG, "Request URL:" + URL);

            if (Utility.isNetworkOnline(activity)) {
                data = post(URL, data);
            } else {
                errorMsg = "No internet.Please check your connection and try again";
                isExceptionRaised = true;
            }
        } catch (Exception ex) {
            errorMsg = ex.getMessage();
            isExceptionRaised = true;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        dismissProgress();
        if (isExceptionRaised) {
            // if exception is raised in doInBackground method call the
            // failure method.
            data = new JSONObject();
            try {
                data.put(Constants.EXCEPTION_MSG, errorMsg);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            callBack.onFailure(data, dataType);
        } else {
            // if exception is not raised in doInBackground method call the
            // success method.
            if (data != null) {
                try {
                    int respCode = (Integer) data.get("Message_Id");
                    if (respCode == 200) {
                        DebugLog.d("success");
                        //Success
                        callBack.onSuccess(data, dataType);
                    } else {
                        DebugLog.d("fail");
                        //Failure
                        callBack.onFailure(data, dataType);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(activity, "Un-able to process request.", Toast.LENGTH_SHORT).show();
            }


        }

    }



    /**
     * Used when no request parameters need to send
     *
     * @param url
     * @return
     */
    private JSONObject post(String url, JSONObject jsonObj) {
        InputStream inputStream = null;
        String result = "";
        JSONObject response = null;
        try {

            // 1. create HttpClient
            HttpClient httpclient = getNewHttpClient();
            //HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            if (jsonObj != null) {
                // 3. convert JSONObject to JSON to String
                json = jsonObj.toString();
                Log.d(TAG, "json:----------------->" + json);

                // 4. set json to StringEntity
                StringEntity se = new StringEntity(json);

                // 5. set httpPost Entity
                httpPost.setEntity(se);
            }

            // 6. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 7. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 8. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();


            // 9. convert input-stream to string
            if (inputStream != null) {
                result = convertInputStreamToString(inputStream);
                //Log.d(TAG, "Resp:"+result);
                response = new JSONObject(result);
            } else {
                result = "Did not work!";
            }

        } catch (Exception e) {
            Log.d(TAG, "Exception:" + e.getLocalizedMessage());
        }

        // 10. return result
        return response;
    }

    /**
     * Send get request
     *
     * @param url
     * @return
     */
    public static String get(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    /*=============================================
        Optimized methods we might use in future
     ==============================================
     */
    private HttpClient getNewHttpClient() {
        try {

            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 1 * 60 * 1000);
            HttpConnectionParams.setSoTimeout(params, 1 * 60 * 1000);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultHttpClient();
        }
    }

    private JSONObject sendRequest(String Url, String reqKey, JSONObject json) throws Exception {
        HttpClient httpClient = getNewHttpClient();
        HttpPost httpPost = new HttpPost();
        HttpResponse httpResponse = null;
        InputStream is = null;
        if (!TextUtils.isEmpty(reqKey) && json != null && !TextUtils.isEmpty(json.toString())) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair(reqKey, json.toString()));
            StringEntity se = new StringEntity(json.toString());
            //UrlEncodedFormEntity se = new UrlEncodedFormEntity(nameValuePairs);
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));
            httpPost.setEntity(se);
            Log.e(TAG, "NameValuePairs" + nameValuePairs.toString());
            String content = EntityUtils.toString(se);
            Log.d(TAG, "content:" + content);
        }

        httpResponse = httpClient.execute(httpPost);
        /*Checking response */
        if (httpResponse != null) {
            is = httpResponse.getEntity().getContent(); //Get the data in the entity
        }

        // is = httpUrlConn.getInputStream();
        int len = (int) httpResponse.getEntity().getContentLength();
        byte[] byteBuffer;

        if (len > 0) {
            byteBuffer = new byte[len];
            int done = 0;

            while (done < len) {
                done += is.read(byteBuffer, done, len - done);
            }
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int count;
            while ((count = is.read(buffer)) >= 0) {
                bos.write(buffer, 0, count);
            }
            byteBuffer = bos.toByteArray();
        }
        String strOutput = new String(byteBuffer);
        Log.d(TAG, "Resp:" + strOutput);
        is.close();
        JSONObject response = new JSONObject(strOutput);
        return response;

    }

    private JSONObject postRequest(String url, JSONObject jsonObj) throws Exception {
        HttpClient httpClient = getNewHttpClient();
        HttpPost httpPost = new HttpPost();
        HttpResponse httpResponse = null;
        InputStream is = null;
        String json;

        if (jsonObj != null) {
            json = jsonObj.toString();
            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
        }
        httpResponse = httpClient.execute(httpPost);
        /*Checking response */
        if (httpResponse != null) {
            is = httpResponse.getEntity().getContent(); //Get the data in the entity
        }

        // is = httpUrlConn.getInputStream();
        int len = (int) httpResponse.getEntity().getContentLength();
        byte[] byteBuffer;

        if (len > 0) {
            byteBuffer = new byte[len];
            int done = 0;

            while (done < len) {
                done += is.read(byteBuffer, done, len - done);
            }
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int count;
            while ((count = is.read(buffer)) >= 0) {
                bos.write(buffer, 0, count);
            }
            byteBuffer = bos.toByteArray();
        }
        String strOutput = new String(byteBuffer);
        Log.d(TAG, "Resp:" + strOutput);
        is.close();
        JSONObject response = new JSONObject(strOutput);
        return response;
    }

    AnimationDrawable loadAnimation;
    Dialog loadDialog;

    private void showProgress() {
        loadDialog = new Dialog(activity);
        loadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        loadDialog.setContentView(R.layout.progress_bar);
        loadDialog.setCanceledOnTouchOutside(false);
        ImageView loadImage = (ImageView) loadDialog.findViewById(R.id.load_img);
        loadImage.setBackgroundResource(R.drawable.animate);
        loadAnimation = (AnimationDrawable) loadImage.getBackground();
        loadAnimation.start();
        loadDialog.show();
    }

    private void dismissProgress() {
        if (loadAnimation != null) {
            loadAnimation.stop();
        }
        if (loadDialog != null && loadDialog.isShowing()) {
            loadDialog.dismiss();
        }
    }
}
