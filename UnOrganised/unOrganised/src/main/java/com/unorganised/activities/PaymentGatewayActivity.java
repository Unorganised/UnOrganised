package com.unorganised.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Extras.PayUSdkDetails;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.PayuConstants;
import com.payu.india.Payu.PayuErrors;
import com.payu.payuui.PayUBaseActivity;
import com.unorganised.R;
import com.unorganised.network.HttpRequestResponse;
import com.unorganised.util.Constants;
import com.unorganised.util.Utility;
import com.unorganised.views.ProviderOngoingJobFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;

import static com.unorganised.network.HttpReqRespLayer.getInstance;

public class PaymentGatewayActivity extends Activity implements View.OnClickListener, Constants, HttpRequestResponse{
    private int nJobId;
    private String strDuration;
    private int nBiddingAmt;
    private float fRating;
    private String strCompletedDate;
    private String strProviderName;
    private String strProviderMobile;
    private String strProviderMailId;

    private int merchantIndex = 0;
    //    int env = PayuConstants.MOBILE_STAGING_ENV;
    // in case of production make sure that merchantIndex is fixed as 0 (0MQaQP) for other key's payu server cant generate hash
    int env = PayuConstants.PRODUCTION_ENV;

    String merchantTestKeys[] = {"gtKFFx", "gtKFFx"};
    String merchantTestSalts[] = {"eCwWELxi", "eCwWELxi"};

    String merchantProductionKeys[] = {"0MQaQP", "smsplus"};
    String merchantProductionSalts[] = {"13p0PXZk", "1b1b0",};

    String offerKeys[] = {"test123@6622", "offer_test@ffer_t5172", "offerfranklin@6636"};

    String merchantKey = env == PayuConstants.PRODUCTION_ENV ? merchantProductionKeys[merchantIndex] : merchantTestKeys[merchantIndex];
    //String merchantSalt = env == PayuConstants.PRODUCTION_ENV ? merchantProductionSalts[merchantIndex] : merchantTestSalts[merchantIndex];
    String mandatoryKeys[] = {PayuConstants.KEY, PayuConstants.AMOUNT, PayuConstants.PRODUCT_INFO, PayuConstants.FIRST_NAME, PayuConstants.EMAIL, PayuConstants.TXNID, PayuConstants.SURL, PayuConstants.FURL, PayuConstants.USER_CREDENTIALS, PayuConstants.UDF1, PayuConstants.UDF2, PayuConstants.UDF3, PayuConstants.UDF4, PayuConstants.UDF5, PayuConstants.ENV};
    String mandatoryValues[] = {merchantKey, "10.0", "myproduct", "firstname", "me@itsmeonly.com", "" + System.currentTimeMillis(), "https://payu.herokuapp.com/success", "https://payu.herokuapp.com/failure", merchantKey + ":payutest@payu.in", "udf1", "udf2", "udf3", "udf4", "udf5", "" + env};
    private String key;
    private String salt;
    private String var1;
    private String cardBin;
    private String firstName;
//    private String lastName;
    private String email;
    private String mobileNumber;
    private String amount;
    private Intent intent;
    private PaymentParams mPaymentParams;
    private PayuConfig payuConfig;
    private PayUChecksum checksum;
    private PostData postData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_gateway_activity);
        // lets tell the people what version of sdk we are using
        PayUSdkDetails payUSdkDetails = new PayUSdkDetails();
        Toast.makeText(this, "Build No: " + payUSdkDetails.getSdkBuildNumber() + "\n Build Type: " + payUSdkDetails.getSdkBuildType() +
                " \n Build Flavor: " + payUSdkDetails.getSdkFlavor() + "\n Application Id: " + payUSdkDetails.getSdkApplicationId() +
                "\n Version Code: " + payUSdkDetails.getSdkVersionCode() + "\n Version Name: " + payUSdkDetails.getSdkVersionName(), Toast.LENGTH_LONG).show();
        findViewById(R.id.paynow_btn_id).setOnClickListener(this);
        Intent intent = getIntent();
        nJobId = intent.getIntExtra(JOB_ID, 0);
        strDuration = intent.getStringExtra(DURATION);
        nBiddingAmt = intent.getIntExtra(JOB_BIDDING_AMT, 0);
        fRating = intent.getFloatExtra(SEEKER_RATING, 0);
        strCompletedDate = intent.getStringExtra(COMPLETED_DATE);
        strProviderName = intent.getStringExtra(PROVIDER_FULL_NAME);
        strProviderMobile = intent.getStringExtra(PROVIDER_MOBILE_NUM);
        strProviderMailId = intent.getStringExtra(PROVIDER_MAIL_ID);
        ((EditText) findViewById(R.id.amount_id)).setText(nBiddingAmt);
        ((EditText) findViewById(R.id.firstname_id)).setText(strProviderName);
        ((EditText) findViewById(R.id.email_id)).setText(strProviderMailId);
        ((EditText) findViewById(R.id.phone_number_id)).setText(strProviderMobile);
    }

    private boolean validate() {
        if (firstName.equals("")){
            Utility.showToast(this, getString(R.string.enter_full_name_error));
            return false;
        } else if(email.equals("")){
            Utility.showToast(this, getString(R.string.enter_valid_mail_id_error));
            return false;
        } else if(mobileNumber.equals("")){
            Utility.showToast(this, getString(R.string.enter_mobile_num_text));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        firstName = ((EditText) findViewById(R.id.firstname_id)).getText().toString();
//        lastName = ((EditText) findViewById(R.id.lastname_id)).getText().toString();
        email = ((EditText) findViewById(R.id.email_id)).getText().toString();
        mobileNumber = ((EditText) findViewById(R.id.phone_number_id)).getText().toString();
        amount = "" + nBiddingAmt;

        if (validate()){
            Log.d("Validate true", "onclick");
            navigateToBaseActivity();
        }
    }

    private void navigateToBaseActivity() {
        intent = new Intent(this, PayUBaseActivity.class);
        mPaymentParams = new PaymentParams();
        payuConfig = new PayuConfig();
        mPaymentParams.setKey(mandatoryValues[0]);
        key = mandatoryValues[0];
        mPaymentParams.setAmount(amount);
        mPaymentParams.setProductInfo(mandatoryValues[2]);
        mPaymentParams.setFirstName(firstName);
        mPaymentParams.setEmail(email);
        mPaymentParams.setTxnId(mandatoryValues[5]);
        mPaymentParams.setSurl(mandatoryValues[6]);
        mPaymentParams.setFurl(mandatoryValues[7]);
        mPaymentParams.setUdf1(mandatoryValues[9]);
        mPaymentParams.setUdf2(mandatoryValues[10]);
        mPaymentParams.setUdf3(mandatoryValues[11]);
        mPaymentParams.setUdf4(mandatoryValues[12]);
        mPaymentParams.setUdf5(mandatoryValues[13]);
        // in case store user card
        mPaymentParams.setUserCredentials(mandatoryValues[8]);
        var1 = mandatoryValues[8];

        // for offer key
        mPaymentParams.setOfferKey(offerKeys[1]);

        // other params- should be inside bundle, so that we can get them in next page.
        intent.putExtra(PayuConstants.SALT, merchantProductionSalts[0]);
        salt = merchantProductionSalts[0];

        // stetting up the environment
        String env = mandatoryValues[14];
        payuConfig.setEnvironment(env.contentEquals("" + PayuConstants.PRODUCTION_ENV) ? PayuConstants.PRODUCTION_ENV : PayuConstants.MOBILE_STAGING_ENV);

        // is_Domestic
        cardBin = "is_Domestic";

        // generate hash from server
        // just a sample. Actually Merchant should generate from his server.
        if (null == salt) {
            generateHashFromServer(mPaymentParams);
        } else {
            generateHashFromSDK(mPaymentParams, intent.getStringExtra(PayuConstants.SALT));
        }
    }

    /******************************
     * Server hash generation
     ********************************/
    // lets generate hashes from server
    public void generateHashFromServer(PaymentParams mPaymentParams) {
        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayuConstants.KEY, mPaymentParams.getKey()));
        postParamsBuffer.append(concatParams(PayuConstants.AMOUNT, mPaymentParams.getAmount()));
        postParamsBuffer.append(concatParams(PayuConstants.TXNID, mPaymentParams.getTxnId()));
        postParamsBuffer.append(concatParams(PayuConstants.EMAIL, null == mPaymentParams.getEmail() ? "" : mPaymentParams.getEmail()));
        postParamsBuffer.append(concatParams(PayuConstants.PRODUCT_INFO, mPaymentParams.getProductInfo()));
        postParamsBuffer.append(concatParams(PayuConstants.FIRST_NAME, null == mPaymentParams.getFirstName() ? "" : mPaymentParams.getFirstName()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF1, mPaymentParams.getUdf1() == null ? "" : mPaymentParams.getUdf1()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF2, mPaymentParams.getUdf2() == null ? "" : mPaymentParams.getUdf2()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF3, mPaymentParams.getUdf3() == null ? "" : mPaymentParams.getUdf3()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF4, mPaymentParams.getUdf4() == null ? "" : mPaymentParams.getUdf4()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF5, mPaymentParams.getUdf5() == null ? "" : mPaymentParams.getUdf5()));
        postParamsBuffer.append(concatParams(PayuConstants.USER_CREDENTIALS, mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials()));

        // for offer_key
        if (null != mPaymentParams.getOfferKey())
            postParamsBuffer.append(concatParams(PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey()));
        // for check_isDomestic
        if (null != cardBin)
            postParamsBuffer.append(concatParams("card_bin", cardBin));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();
        // make api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }

    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    class GetHashesFromServerTask extends AsyncTask<String, String, PayuHashes> {

        @Override
        protected PayuHashes doInBackground(String... postParams) {
            PayuHashes payuHashes = new PayuHashes();
            try {
//                URL url = new URL(PayuConstants.MOBILE_TEST_FETCH_DATA_URL);
//                        URL url = new URL("http://10.100.81.49:80/merchant/postservice?form=2");;

                URL url = new URL("https://payu.herokuapp.com/get_hash");

                // get the payuConfig first
                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    if (key.equals("payment_hash")) {
                        payuHashes.setPaymentHash(response.getString(key));
                    } else if (key.equals("get_merchant_ibibo_codes_hash")) {
                        payuHashes.setMerchantIbiboCodesHash(response.getString(key));
                    } else if (key.equals("vas_for_mobile_sdk_hash")) {
                        payuHashes.setVasForMobileSdkHash(response.getString(key));
                    } else if (key.equals("payment_related_details_for_mobile_sdk_hash")) {
                        payuHashes.setPaymentRelatedDetailsForMobileSdkHash(response.getString(key));
                    } else if (key.equals("delete_user_card_hash")) {
                        payuHashes.setDeleteCardHash(response.getString(key));
                    } else if (key.equals("get_user_cards_hash")) {
                        payuHashes.setStoredCardsHash(response.getString(key));
                    } else if (key.equals("edit_user_card_hash")) {
                        payuHashes.setEditCardHash(response.getString(key));
                    } else if (key.equals("save_user_card_hash")) {
                        payuHashes.setSaveCardHash(response.getString(key));
                    } else if (key.equals("check_offer_status_hash")) {
                        payuHashes.setCheckOfferStatusHash(response.getString(key));
                    } else if (key.equals("check_isDomestic_hash")) {
                        payuHashes.setCheckIsDomesticHash(response.getString(key));
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return payuHashes;
        }

        @Override
        protected void onPostExecute(PayuHashes payuHashes) {
            super.onPostExecute(payuHashes);
//            nextButton.setEnabled(true);
            launchSdkUI(payuHashes);
        }
    }

    public void launchSdkUI(PayuHashes payuHashes) {
        // let me add the other params which i might use from other activity
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
//        intent.putExtra(PayuConstants.PAYMENT_DEFAULT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);


        /**
         *  just for testing, dont do this in production.
         *  i need to generate hash for {@link com.payu.india.Tasks.GetTransactionInfoTask} since it requires transaction id, i don't generate hash from my server
         *  merchant should generate the hash from his server.
         *
         */
        intent.putExtra(PayuConstants.SALT, salt);

        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);

    }

    /******************************
     * Client hash generation
     ***********************************/
    // Do not use this, you may use this only for testing.
    // lets generate hashes.
    // This should be done from server side..
    // Do not keep salt anywhere in app.
    public void generateHashFromSDK(PaymentParams mPaymentParams, String Salt) {
        PayuHashes payuHashes = new PayuHashes();
        postData = new PostData();

        // payment Hash;
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setPaymentHash(postData.getResult());
        }

        // checksum for payemnt related details
        // var1 should be either user credentials or default
        var1 = var1 == null ? PayuConstants.DEFAULT : var1;

        if ((postData = calculateHash(key, PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // Assign post data first then check for success
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        //vas
        if ((postData = calculateHash(key, PayuConstants.VAS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setVasForMobileSdkHash(postData.getResult());

        // getIbibocodes
        if ((postData = calculateHash(key, PayuConstants.GET_MERCHANT_IBIBO_CODES, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setMerchantIbiboCodesHash(postData.getResult());

        if (!var1.contentEquals(PayuConstants.DEFAULT)) {
            // get user card
            if ((postData = calculateHash(key, PayuConstants.GET_USER_CARDS, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // todo rename stored card
                payuHashes.setStoredCardsHash(postData.getResult());
            // save user card
            if ((postData = calculateHash(key, PayuConstants.SAVE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setSaveCardHash(postData.getResult());
            // delete user card
            if ((postData = calculateHash(key, PayuConstants.DELETE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setDeleteCardHash(postData.getResult());
            // edit user card
            if ((postData = calculateHash(key, PayuConstants.EDIT_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setEditCardHash(postData.getResult());
        }

        if (mPaymentParams.getOfferKey() != null) {
            postData = calculateHash(key, PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey(), salt);
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                payuHashes.setCheckOfferStatusHash(postData.getResult());
            }
        }

        if (mPaymentParams.getOfferKey() != null && (postData = calculateHash(key, PayuConstants.CHECK_OFFER_STATUS, mPaymentParams.getOfferKey(), salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setCheckOfferStatusHash(postData.getResult());
        }

        // we have generated all the hases now lest launch sdk's ui
        launchSdkUI(payuHashes);
    }

    // deprecated, should be used only for testing.
    private PostData calculateHash(String key, String command, String var1, String salt) {
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setKey(key);
        checksum.setCommand(command);
        checksum.setVar1(var1);
        checksum.setSalt(salt);
        return checksum.getHash();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
//            getInstance().finishProviderJob(this, this, nJobId, strCompletedDate, strDuration, fRating);
        }
    }


    @Override
    public void onSuccess(JSONObject data, HttpReqRespActionItems dataType) {
        Utility.showToast(this, getString(R.string.job_completed_message));
        Intent intent = new Intent(this, JobProviderDashboardActivity.class);
        intent.putExtra(DRAWER_ITEM_POSITION, 3);
        startActivity(intent);
    }

    @Override
    public void onFailure(JSONObject data, HttpReqRespActionItems dataType) {

    }

}
