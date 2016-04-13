package com.unorganised.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServiceTypeJSONParser implements Constants{

	private int[] totalSubService;

	public int[] getTotalSubService() {
		return totalSubService;
	}

	/*
         * Gets data
         */
	public LinkedHashMap<String, String> parse(JSONObject json){
		LinkedHashMap<String, String> serviceTypes = new LinkedHashMap<String, String>();
		JSONArray result = null;
		
		try {
			result = json.getJSONArray(RESULT_TAG);
			totalSubService = new int[result.length() + 1];

			for (int i = 0; i < result.length(); i++) {
				JSONObject jsonObject = result.getJSONObject(i);
				String description = jsonObject.optString(DESCRIPTION_TAG);
				String serviceId = jsonObject.optString(SERVICE_ID_TAG);
				totalSubService[i] = Integer.parseInt(jsonObject.optString(SUB_SERVICE_COUNT_TAG));
				serviceTypes.put(serviceId, description);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return serviceTypes;
	}
	
	public LinkedHashMap<String, String> parseSubActivity(JSONObject json){
		LinkedHashMap<String, String> subServiceTypes = new LinkedHashMap<String, String>();
		JSONArray result = null;
		try {
			result = json.getJSONArray(RESULT_TAG);
			DebugLog.d("Length "+ result.length());
			for (int i = 0; i < result.length(); i++) {
				JSONObject jsonObject = result.getJSONObject(i);
				String description = jsonObject.optString(SUB_SERVICE_DES_TAG);
				String subServiceId = jsonObject.optString(SUB_SERVICE_ID_TAG);
				subServiceTypes.put(subServiceId, description);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return subServiceTypes;
		
	}


	public LinkedHashMap<String, List<String>> parseUserService(JSONObject json) {
		LinkedHashMap<String, List<String>> serviceData = new LinkedHashMap<String, List<String>>();
		List<String> subServiceList = new ArrayList<String>();
		String serviceID = "";
		JSONArray result;
		try {
			result = json.getJSONArray(RESULT_TAG);

			for (int i = 0; i < result.length(); i++){
				JSONObject jsonObject = result.getJSONObject(i);

				String subServiceID = jsonObject.optString(SUB_SERVICE_ID_TAG);
				if (!jsonObject.optString(SERVICE_ID_TAG).equals(serviceID)){
					subServiceList = new ArrayList<String>();
				}
				serviceID = jsonObject.optString(SERVICE_ID_TAG);
				serviceData.put(serviceID, subServiceList);
				subServiceList.add(subServiceID);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return serviceData;
	}
}
