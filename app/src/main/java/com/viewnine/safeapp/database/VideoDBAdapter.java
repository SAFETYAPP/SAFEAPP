package com.viewnine.safeapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.viewnine.safeapp.model.VideoObject;
import com.viewnine.safeapp.ulti.Constants;
import com.viewnine.safeapp.ulti.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 4/21/15.
 */
public class VideoDBAdapter extends BaseAdapter{

    private String TAG = VideoDBAdapter.class.getName();

    public VideoDBAdapter(Context context) {
        super(context);
    }

    public void openDatabase() {
        if (mDb == null || !mDb.isOpen()) {
            this.open();
        }
    }

    public boolean insertVideo(VideoObject videoObject){
        int result = -1;
        ContentValues value = mapContentValues(videoObject);

        // execute query
        openDatabase();
        try {
            result = (int) mDb.insert(DbDefines.TABLE_VIDEOS, null, value);
        } catch (SQLiteException e) {
            LogUtils.logI(TAG, e.toString());
            // FlurryHelper.logException("insertChatMEssage()", TAG, e);
            result = -1;
        } finally {
            this.close();
        }
        return result > 0 ? true : false;
    }

    public boolean deleteVideoById(String videoId) {
        String where = DbDefines.VIDEO_ID + " = '" + videoId + "'";
        boolean result = false;
        openDatabase();
        try {
            result = (mDb.delete(DbDefines.TABLE_VIDEOS, where, null) != -1);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally{
            this.close();
        }


        return result;
    }

    public List<VideoObject> getAllVideos() {
        List<VideoObject> msgs = new ArrayList<VideoObject>();
        // Open database
        openDatabase();
        Cursor results = null;
        try {
            // Query
            results = mDb.rawQuery("SELECT * FROM " + DbDefines.TABLE_VIDEOS, null);
            if (results.getCount() > 0) {
                for (int counter = 0; counter < results.getCount(); counter++) {
                    results.moveToPosition(counter);
                    VideoObject tempMsg = mapMessageData(results);
                    msgs.add(tempMsg);
                }
            }
        } catch (SQLiteException e) {
            LogUtils.logI(TAG, e.toString());
        } finally {
            if (results != null) {
                results.close();
            }
            // close the cursor and the database
            this.close();
        }
        // value return
        return msgs;
    }

    public List<VideoObject> getListActivitiesLocalBaseOnListId(List<VideoObject> listNewsObject){
        List<VideoObject> msgs = new ArrayList<VideoObject>();
        String whereClause = Constants.EMPTY_STRING;
        int sizeOfListNewsObject = listNewsObject.size();
        for (int i = 0; i < sizeOfListNewsObject; i++) {
            VideoObject newsObject = listNewsObject.get(i);
            if(i == 0){
                whereClause = whereClause +  " " + DbDefines.VIDEO_ID + " = '" + newsObject.getId() + "'";
            }else {
                whereClause = whereClause +  " OR " + DbDefines.VIDEO_ID + " = '" + newsObject.getId() + "'";
            }

        }

        String queryString = "SELECT * FROM " + DbDefines.TABLE_VIDEOS + " WHERE " + whereClause;
        LogUtils.logI(TAG, "QueryString: " + queryString);
        // Open database
        openDatabase();
        Cursor results = null;
        try {
            // Query
            results = mDb.rawQuery(queryString, null);
            if (results.getCount() > 0) {
                for (int counter = 0; counter < results.getCount(); counter++) {
                    results.moveToPosition(counter);
                    VideoObject tempMsg = mapMessageData(results);
                    msgs.add(tempMsg);
                    LogUtils.logI(TAG, "Post ID: " + tempMsg.getId());
                }
            }


        } catch (Exception e) {
            LogUtils.logI(TAG, e.toString());
        } finally {
            if (results != null) {
                results.close();
            }
            // close the cursor and the database
            this.close();
        }
        // value return
        return msgs;
    }

    private ContentValues mapContentValues(VideoObject videoObject) {
        ContentValues value = new ContentValues();
        value.put(DbDefines.Video_URL, videoObject.getVideoUrl());
        value.put(DbDefines.Image_Link, videoObject.getImageLink());
        value.put(DbDefines.Time, videoObject.getTime());
        value.put(DbDefines.VIDEO_ID, videoObject.getId());
        return value;
    }

    private VideoObject mapMessageData(Cursor results) {
        VideoObject msg = new VideoObject();

        msg.setId(results.getString(results.getColumnIndex(DbDefines.VIDEO_ID)));
        msg.setVideoUrl(results.getString(results.getColumnIndex(DbDefines.Video_URL)));
        msg.setImageLink(results.getString(results.getColumnIndex(DbDefines.Image_Link)));
        msg.setTime((results.getLong(results.getColumnIndex(DbDefines.Time))));

        return msg;
    }


}
