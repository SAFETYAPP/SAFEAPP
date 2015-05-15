package com.viewnine.nuttysnap.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.viewnine.nuttysnap.application.SafeAppApplication;

/**
 * Created by user on 4/21/15.
 */
public class BaseAdapter {
    protected static DatabaseHelper mDbHelper;
    protected SQLiteDatabase mDb;
    protected Context mCtx;

    public BaseAdapter(Context context) {
        if(context != null){
            mCtx = context.getApplicationContext();
        }else {
            mCtx = SafeAppApplication.getInstance().getApplicationContext();
        }
    }

    public BaseAdapter open() {
        mDbHelper = DatabaseHelper.getHelper(mCtx);

        // Handle open writable database. On exception occurs, try to open a
        // readable instance
        mDb = mDbHelper.getWritableDatabase();

        return this;
    }

    // close db
    public void close() {
        mDb.close();
    }
}
