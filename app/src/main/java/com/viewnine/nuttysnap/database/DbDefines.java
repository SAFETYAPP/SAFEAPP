package com.viewnine.nuttysnap.database;

/**
 * Created by user on 4/21/15.
 */
public class DbDefines {

    public static final String DB_NAME = "SafeApp.db";
    public static final int DATABASE_VERSION = 2;

    // Table Names
    public static final String TABLE_VIDEOS = "VIDEOS";

    //Table BACK_VIDEO's column names
    public static final String ID = "Id";
    public static final String VIDEO_ID = "VideoId";
    public static final String Video_URL = "VideoURL";
    public static final String Image_Link = "ImageLink";
    public static final String Time = "Time";
    public static final String IsAddedWatermark = "isAddedWatermark";
    public static final String CameraMode = "cameraMode";
}
