package com.viewnine.safeapp.ulti;

import android.media.CamcorderProfile;
import android.os.Environment;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * Created by user on 4/17/15.
 */
public class Constants {
    public static final boolean isStagingBuild = true;
    public static final boolean enableNavigationBar = true;
    public static final boolean ENABLE_SEND_EMAIL_FEATURE = false;
    public static final boolean ENABLE_CHECK_VIDEO_EXPIRED = true;
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

    public static final int DEFAULT_TIME_TO_RECORDING = 60 * 1000;
    public static final int TIME_TO_PENDING = 2000;
    public static final int TIME_DELAY = 500;
    public static final int VIDEO_QUALITY = 1 * 1000 * 1000;
    public static final String MAIL_SUBJECT = "[SafeApp] video";
    public static final String MAIL_CONTENT = "MAIL CONTENT";

    public static final int OK = 0;
    public static final int ERROR = 1;
    public static final int NUMBER_LOAD_VIDEO = 15;
    public static final String VIDEO_LINK = "VIDEO_LINK";
    public static final int MIN_NODE_OF_PATTER = 3;
    public static final int POSITIVE_90_DEGREE = 90;
    public static final int DEGREE_270 = 270;
    public static final int CAMERA_QUALITY = CamcorderProfile.QUALITY_480P;
    public static final String SLASH_CHARACTER = "/";
    public static final String ACTION_BROADCAST_RECIVER_VIDEO = "ACTION_BROADCAST_RECIVER_VIDEO";
    public static final LinkedHashMap<String, Integer> TIME_INTERVAL_LIST = new LinkedHashMap<String, Integer>() {{put("1 min", DEFAULT_TIME_TO_RECORDING);
                                                                                                        put("45 sec", 45 * 1000);
                                                                                                        put("30 sec", 30 * 1000);}};

    public static final int CHILD_ACTIVITY = 100;
    public static final int LOCK_SCREEN_ACTIVITY = 1;
    public static final int RECORD_FOREGROUND_ACTIVITY = 2;
    public static final int SETUP_ACTIVITY = 3;
    public static final int HISTORY_ACTIVITY = 4;
    public static final int SETTINGS_ACTIVITY = 5;
    public static final int SECURITY_ACTIVITY = 6;
    public static final int NOTIFICATIONS_ACTIVITY = 7;
    public static final int BACKUP_ACTIVITY = 8;
    public static final int UNLOCK_PATTERN_ACTIIVTY = 9;

    public static final int VIDEO_EXPIRED_DAY = 7;
    public static final String CONTACT_EMAIL = "gro4tech@gmail.com";
    public static final int DELETE_VIDEO_SIGNAL = 1;

}
