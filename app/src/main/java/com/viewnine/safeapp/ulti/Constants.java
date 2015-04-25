package com.viewnine.safeapp.ulti;

import android.media.CamcorderProfile;
import android.os.Environment;

import java.io.File;
import java.util.HashMap;

/**
 * Created by user on 4/17/15.
 */
public class Constants {
    public static final boolean isStagingBuild = true;
    public static final boolean enableNavigationBar = true;
    public static final String ZERODAY = "0000-00-00";
    public static final String EMPTY_STRING = "";
    public static final int ZERO_NUMBER = 0;

    public final static File SDROOT = Environment.getExternalStorageDirectory().getAbsoluteFile();
    public final static String SAFEAPP_FOLDER = SDROOT + File.separator + "SafeApp";
    public final static String VIDEO_FOLDER = SAFEAPP_FOLDER + File.separator + "Videos" + File.separator;
    public final static String IMAGE_FOLDER = SAFEAPP_FOLDER + File.separator + "Images" + File.separator;
    public final static String PREFIX_VIDEO_NAME = "SafeApp_";
    public final static String VIDEO_TYPE = ".mp4";
    public final static String PREFIX_VIDEO_ID = "Video_";
    public final static String PREFIX_LOCAL_FILE_URL = "file://";

    public static final int TIME_TO_RECORDING = 10000;
    public static final int TIME_TO_PENDING = TIME_TO_RECORDING + 2000;
    public static final int TIME_DELAY = 500;
    public static final int OK = 0;
    public static final int ERROR = 1;
    public static final int NUMBER_LOAD_VIDEO = 15;
    public static final String VIDEO_LINK = "VIDEO_LINK";
    public static final int MIN_NODE_OF_PATTER = 3;
    public static final int POSITIVE_90_DEGREE = 90;
    public static final int DEGREE_270 = 270;
    public static final int CAMERA_QUALITY = CamcorderProfile.QUALITY_480P;
    public static final HashMap<String, Integer> TIME_INTERVAL_LIST = new HashMap<String, Integer>() {{put("1 min", 60 * 1000);
                                                                                                        put("45 sec", 45 * 1000);
                                                                                                        put("30 sec", 30 * 1000);}};
}
