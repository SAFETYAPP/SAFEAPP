package com.viewnine.nuttysnap.ulti;

import android.media.CamcorderProfile;
import android.os.Environment;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by user on 4/17/15.
 */
public class Constants {
    public static final boolean isStagingBuild = true;
    public static final boolean enableNavigationBar = true;
    public static final boolean ENABLE_SEND_EMAIL_FEATURE = true;
    public static final boolean ENABLE_CHECK_VIDEO_EXPIRED = true;
    public static final boolean DISABLE_SYSTEM_LOCK_SCREEN = true;
    public static final boolean ENABLE_WATER_MARK = false;
    public static final boolean ENABLE_VIDEO_COORDINATE = true;
    public static final boolean ENABLE_NOTIFICATION_YOUTUBE = false;
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

    public static final int DEFAULT_TIME_TO_RECORDING = 3 * 60 * 1000;
    public static final int TIME_TO_PENDING = 2000;
    public static final int TIME_DELAY = 500;
    public static final int BACK_CAMERA_BIT_RATE = 1 * 1000 * 1000;
    public static final int FRONT_CAMERA_BIT_RATE = 1 * 1000 * 1000;
    public static final String MAIL_SUBJECT = "[NuttySnap] video";
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
    public static final LinkedHashMap<String, Integer> TIME_INTERVAL_LIST = new LinkedHashMap<String, Integer>() {{
                                                                                                        put("3 min", DEFAULT_TIME_TO_RECORDING);
                                                                                                        put("1 min", 60 * 1000);
                                                                                                        put("45 sec", 45 * 1000);
                                                                                                        put("30 sec", 30 * 1000);
                                                                                                        }};

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
    public static final int BROWSER_SCREEN = 10;
    public static final int INTRODUCE_ACTIVITY = 11;

    public static final int VIDEO_EXPIRED_DAY = 7;
    public static final String CONTACT_EMAIL = "gro4tech@gmail.com";
    public static final int DELETE_VIDEO_SIGNAL = 1;

    public static final long MINIMUM_STORAGE_SPACE = 500; //500Mb

    // Source - http://en.wikipedia.org/wiki/Samsung_Galaxy_S_III
    private static final String s3ModelNames[] = { "XXXXXXXXXXXXXXXX", // Place holder
            "SAMSUNG-SGH-I747", // AT&T
            "SAMSUNG-SGH-T999", // T-Mobile
            "SAMSUNG-SGH-N064", // Japan
            "SAMSUNG-SCH-R530", // US Cellular
            "SAMSUNG-SCH-I535", // Verizon
            "SAMSUNG-SPH-L710", // Sprint
            "SAMSUNG-GT-I9300", // International
            "SGH-I747", // AT&T
            "SGH-T999", // T-Mobile
            "SGH-N064", // Japan
            "SCH-R530", // US Cellular
            "SCH-I535", // Verizon
            "SPH-L710", // Sprint
            "GT-I9300"  // International
    };

    public static List<String> s3ModelList = Arrays.asList(s3ModelNames);

    public static final String CAMERA_ID = "CAMERA_ID";
    public static final String ENABLE_LOCK_SCREEN = "ENABLE_LOCK_SCREEN";
    public static final int DEFAULT_WIDTH_RESOLUTION = 1280;
    public static final int DEFAULT_HEIGH_RESOLUTION = 720;

    //Youtube value
    public static final int MAX_KEYWORD_LENGTH = 30;
    public static final String DEFAULT_KEYWORD = "ytdl";
    // A playlist ID is a string that begins with PL. You must replace this string with the correct
    // playlist ID for the app to work
    public static final String UPLOAD_PLAYLIST = "pl1";
    public static final String ACCOUNT_KEY = "accountName";
    public static final String MESSAGE_KEY = "message";
    public static final String YOUTUBE_ID = "youtubeId";
    public static final String YOUTUBE_WATCH_URL_PREFIX = "http://www.youtube.com/watch?v=";
    public static final String REQUEST_AUTHORIZATION_INTENT = "com.google.example.yt.RequestAuth";
    public static final String REQUEST_AUTHORIZATION_INTENT_PARAM = "com.google.example.yt.RequestAuth.param";
    public static final String WATERMARK_NAME_BACK_CAMERA = "watermark1.png";
    public static final String WATERMARK_NAME_FRONT_CAMERA = "watermark2.png";
    public static final String WATERMARK_BACK_CAMERA = "/sdcard/" + WATERMARK_NAME_BACK_CAMERA;
    public static final String WATERMARK_FRONT_CAMERA = "/sdcard/" + WATERMARK_NAME_FRONT_CAMERA;
}
