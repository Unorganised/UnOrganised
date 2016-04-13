package com.unorganised.network;

import org.json.JSONObject;

import com.unorganised.util.Constants.HttpReqRespActionItems;

public interface HttpRequestResponse {

	/**
	 * This method should implemented by ui which interested to receive the http request success results.
	 * 
	 */
	void onSuccess(JSONObject data, HttpReqRespActionItems dataType);

	void onFailure(JSONObject data,HttpReqRespActionItems dataType);
}
