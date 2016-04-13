package com.unorganised.interfaces;

import com.google.android.gms.maps.model.Marker;

public interface MapsOperations {
	
	public void onMarkerClick(Marker marker);
	public void onSearch(double lat,double lng);
	public void onMapsReady();

}
