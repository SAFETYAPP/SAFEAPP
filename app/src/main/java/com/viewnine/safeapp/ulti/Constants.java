package com.viewnine.safeapp.ulti;

import android.os.Environment;

import java.io.File;

/**
 * Created by user on 4/17/15.
 */
public class Constants {
    public static final boolean isStagingBuild = true;
    public static final boolean enableNavigationBar = true;
    public static final String ZERODAY = "0000-00-00";
    public static final String EMPTY_STRING = "";
    public static final int ZERO_NUMBER = 0;

    public final static File SDROOT = Environment.getExternalStorageDirectory();
    public final static String SAFEAPP_FOLDER = "/SafeApp";
    public final static String VIDEO_FOLDER = SDROOT + SAFEAPP_FOLDER + "/Videos/";
    public final static String PREFIX_VIDEO_NAME = "SafeApp_";
    public final static String VIDEO_TYPE = ".mp4";

    public static final int TIME_TO_RECORDING = 10000;
    public static final int TIME_DELAY = 500;
}
