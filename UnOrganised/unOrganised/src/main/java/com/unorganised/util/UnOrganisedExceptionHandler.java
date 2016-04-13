/* Copyright */

package com.unorganised.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class UnOrganisedExceptionHandler implements Thread.UncaughtExceptionHandler {
	private Thread.UncaughtExceptionHandler defaultHandler = null;

	private Context context;

	public UnOrganisedExceptionHandler(Context ctx,
			Thread.UncaughtExceptionHandler defaultHandler) {
		context = ctx;
		this.defaultHandler = defaultHandler;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable e) {
		try {
			StringBuilder report = new StringBuilder();
			Date curDate = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"dd-MMM-yyyy HH:mm:SS");

			report.append("Error Report collected on : ");
			report.append(dateFormat.format(curDate)).append('\n');
			report.append('\n');
			report.append("Device Information:").append('\n');
			addInformation(report);

			report.append('\n');
			report.append('\n');
			report.append("Stack:\n");

			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			report.append(result.toString());
			printWriter.close();
			report.append('\n');
			report.append("**** End Report ***");

			if (Utility.isEmailClientAvailable(context)) {
				sendCrashReport(report, thread, e);
			} else {
				defaultHandler.uncaughtException(thread, e);
			}
		} catch (Throwable ignore) {
			ignore.printStackTrace();
			DebugLog.e("Error in uncaughtException()", ignore);
		}
	}
	/**
	 * Method to get the required mail id's to which crash report has to be
	 * delivered.
	 * 
	 * @return
	 */
	private String[] getDeveloperMailIds() {
		return new String[] { "unorganised.dev@gmail.com"};
	}

	/**
	 * Method to collect the device information and appends to the crash report.
	 * 
	 * @param message
	 *            which contains the crash report information.
	 */
	private void addInformation(StringBuilder message) {
		message.append("Locale: ").append(Locale.getDefault()).append('\n');

		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);

			message.append("Version: ");
			message.append(pi.versionName);
			message.append('\n');
			message.append("Package: ");
			message.append(pi.packageName);
			message.append('\n');
		} catch (Exception e) {
			e.printStackTrace();
			DebugLog.e(e.getMessage());
			message.append("Could not get Version information for ");
			message.append(context.getPackageName());
		}

		/* Phone Model */
		message.append("Phone Model: ");
		message.append(android.os.Build.MODEL);
		message.append('\n');

		/* Android Version */
		message.append("Android Version: ");
		message.append(android.os.Build.VERSION.RELEASE);
		message.append('\n');

		/* Board */
		message.append("Board: ");
		message.append(android.os.Build.BOARD);
		message.append('\n');

		/* Brand */
		message.append("Brand: ");
		message.append(android.os.Build.BRAND);
		message.append('\n');

		/* Device */
		message.append("Device: ");
		message.append(android.os.Build.DEVICE);
		message.append('\n');

		/* Host */
		message.append("Host: ");
		message.append(android.os.Build.HOST);
		message.append('\n');

		/* ID */
		message.append("ID: ");
		message.append(android.os.Build.ID);
		message.append('\n');

		/* Model */
		message.append("Model: ");
		message.append(android.os.Build.MODEL);
		message.append('\n');

		/* Product */
		message.append("Product: ");
		message.append(android.os.Build.PRODUCT);
		message.append('\n');

		/* Type */
		message.append("Type: ");
		message.append(android.os.Build.TYPE);
		message.append('\n');

	}

	/**
	 * Method to send crash report
	 * 
	 * @param report
	 * @param thread
	 * @param e
	 */
	private void sendCrashReport(final StringBuilder report,
			final Thread thread, final Throwable e) {
		final String app = "";//context.getString(R.string.app_name);

		new Thread() {
			@Override
			public void run() {
				try {
					/* Get the Developer Mail id's */
					getDeveloperMailIds();
					Intent sendIntent = new Intent(Intent.ACTION_SEND);

					/* Mail Subject */
					String subject = "[" + app
							+ " Mobile application crash report]";

					/* Mail Body */
					StringBuilder body = new StringBuilder(app);
					body.append('\n');
					body.append('\n');
					body.append(report);
					body.append('\n');
					body.append('\n');

					sendIntent.setType("message/rfc822");
					sendIntent.putExtra(Intent.EXTRA_EMAIL,
							getDeveloperMailIds());
					sendIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
					sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
					sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					context.startActivity(sendIntent);
					defaultHandler.uncaughtException(thread, e);
				} catch (Throwable ignore) {
					ignore.printStackTrace();
					DebugLog.e("Error while sending error e-mail", ignore);
				}
			}
		}.start();
	}
}
