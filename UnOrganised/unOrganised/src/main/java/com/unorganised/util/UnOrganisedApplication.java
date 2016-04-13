package com.unorganised.util;

import android.app.Application;
import android.content.Context;

public class UnOrganisedApplication extends Application {

	private static Context context;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.context = this;
		// set custom exception handler to monitor the crash.
		Thread.UncaughtExceptionHandler defaultHandler = Thread
				.getDefaultUncaughtExceptionHandler();
		UnOrganisedExceptionHandler handler = new UnOrganisedExceptionHandler(this,
				defaultHandler);
		Thread.setDefaultUncaughtExceptionHandler(handler);
	}
	
	public static Context getContext() {
		return context;
	}
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

}
