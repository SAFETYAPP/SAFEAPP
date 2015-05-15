package com.viewnine.nuttysnap.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 4/21/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper{
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getHelper(Context context){
        if(instance == null){
            instance = new DatabaseHelper(context);
        }
        return instance;

    }


    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DbDefines.DB_NAME, null, DbDefines.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create Table: video
        createVideoTable(db);
    }

    private void createVideoTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DbDefines.TABLE_VIDEOS + " ("
                        + DbDefines.ID + " INTEGER PRIMARY KEY, "
                        + DbDefines.VIDEO_ID + " TEXT, "
                        + DbDefines.Video_URL + " TEXT, "
                        + DbDefines.Image_Link + " TEXT, "
                        + DbDefines.Time + " LONG )"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
