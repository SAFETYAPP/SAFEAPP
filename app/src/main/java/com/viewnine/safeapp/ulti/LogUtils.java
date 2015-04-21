package com.viewnine.safeapp.ulti;

import android.util.Log;

public class LogUtils {
	public static void logV(String TAG, String Message) {
		if (Constants.isStagingBuild && Message!=null) {
			Log.v(TAG, Message);

		}

	}

	public static void logD(String TAG, String Message) {
		if (Constants.isStagingBuild && Message!=null) {
			Log.d(TAG, Message);

		}

	}

	public static void logE(String TAG, String Message) {
		if (Constants.isStagingBuild && Message!=null) {
			Log.e(TAG, Message);

		}

	}

	public static void logI(String TAG, String Message) {
		if (Constants.isStagingBuild && Message!=null) {
			Log.i(TAG, Message);

		}

	}

	public static void logW(String TAG, String Message) {
		if (Constants.isStagingBuild && Message!=null) {
			Log.w(TAG, Message);

		}

	}

	public static void logW(String TAG, String Message, Exception e) {
		if (Constants.isStagingBuild && Message!=null) {
			Log.i(TAG, Message, e);
		}
	}

}
