/**
 */
package com.unorganised.util;

//~--- non-JDK imports --------------------------------------------------------

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;


public class DebugLog {

	private static final boolean DEBUG = true;

	/**
	 * Method to check whether the logs are enable or not.
	 * 
	 * @return boolean value.
	 */
	public static boolean isLoggable() {
		return DEBUG;
	}

	/**
	 * Method to print debug log message.
	 * 
	 * @param msg
	 *            string to print.
	 */
	public static void d(String msg) {
		if (DEBUG) {
			Log.d(getClassName(), getMethodName() + msg);
		}
	}

	/**
	 * Method to print debug log message and exception
	 * 
	 * @param msg
	 *            string to print.
	 * @param tr
	 *            throwable or exception to print.
	 */
	public static void d(String msg, Throwable tr) {
		if (DEBUG) {
			Log.d(getClassName(), getMethodName() + msg, tr);
		}
	}

	/**
	 * Method to print the contents of a Cursor to debug log.
	 * 
	 * @param cursor
	 *            the cursor object to print.
	 */
	public static void d(Cursor cursor) {
		if (DEBUG) {
			Log.d(getClassName(),
					getMethodName() + DatabaseUtils.dumpCursorToString(cursor));
		}
	}

	/**
	 * Method to print info log message.
	 * 
	 * @param msg
	 *            string to print.
	 */
	public static void i(String msg) {
		if (DEBUG) {
			Log.i(getClassName(), getMethodName() + msg);
		}
	}

	/**
	 * Method to print info log message and exception.
	 * 
	 * @param msg
	 *            string to print.
	 * @param tr
	 *            throwable or exception to print.
	 */
	public static void i(String msg, Throwable tr) {
		if (DEBUG) {
			Log.i(getClassName(), getMethodName() + msg, tr);
		}
	}

	/**
	 * Method to print error log message.
	 * 
	 * @param msg
	 *            string to print.
	 */
	public static void e(String msg) {
		if (DEBUG) {
			Log.e(getClassName(), getMethodName() + msg);
		}
	}

	/**
	 * Method to print error log message and exception.
	 * 
	 * @param msg
	 *            string to print.
	 * @param tr
	 *            throwable or exception to print.
	 */
	public static void e(String msg, Throwable tr) {
		if (DEBUG) {
			Log.e(getClassName(), getMethodName() + msg, tr);
		}
	}

	/**
	 * Method to print verbose log message.
	 * 
	 * @param msg
	 *            string to print.
	 */
	public static void v(String msg) {
		if (DEBUG) {
			Log.v(getClassName(), getMethodName() + msg);
		}
	}

	/**
	 * Method to print verbose log message and exception.
	 * 
	 * @param msg
	 *            string to print.
	 * @param tr
	 *            throwable or exception to print.
	 */
	public static void v(String msg, Throwable tr) {
		if (DEBUG) {
			Log.v(getClassName(), getMethodName() + msg, tr);
		}
	}

	/**
	 * Method to print warning log message.
	 * 
	 * @param msg
	 *            string to print.
	 */
	public static void w(String msg) {
		if (DEBUG) {
			Log.w(getClassName(), getMethodName() + msg);
		}
	}

	/**
	 * Method to print warning log message and exception.
	 * 
	 * @param msg
	 *            string to print.
	 * @param tr
	 *            throwable or exception to print.
	 */
	public static void w(String msg, Throwable tr) {
		if (DEBUG) {
			Log.w(getClassName(), getMethodName() + msg, tr);
		}
	}

	/**
	 * Method to get the current class name.
	 * 
	 * @return string represents a class name.
	 */
	private static String getClassName() {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
		String c = caller.getClassName();
		String className = c.substring(c.lastIndexOf(".") + 1, c.length());

		return className;
	}

	/**
	 * Method to get the current method name.
	 * 
	 * @return string represents a method name.
	 */
	private static String getMethodName() {
		StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
		StringBuilder sb = new StringBuilder(3);

		sb.append(caller.getMethodName());
		sb.append("()::");
		sb.append(caller.getLineNumber());
		sb.append("::");

		return sb.toString();
	}
}
