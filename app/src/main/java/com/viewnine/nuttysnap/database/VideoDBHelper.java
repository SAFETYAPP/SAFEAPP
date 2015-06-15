package com.viewnine.nuttysnap.database;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.viewnine.nuttysnap.model.VideoObject;
import com.viewnine.nuttysnap.ulti.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 4/21/15.
 */
public class VideoDBHelper {

    private String TAG = VideoDBHelper.class.getName();

    public boolean insertVideo(VideoObject videoObject){
        long result = -1;
//        ContentValues value = mapContentValues(videoObject);
//
//        // execute query
//        openDatabase();
//        try {
//            result = (int) mDb.insert(DbDefines.TABLE_VIDEOS, null, value);
//        } catch (SQLiteException e) {
//            LogUtils.logI(TAG, e.toString());
//            // FlurryHelper.logException("insertChatMEssage()", TAG, e);
//            result = -1;
//        } finally {
//            this.close();
//        }
        result = videoObject.save();
        return result > 0 ? true : false;
    }

    public boolean updateVideo(VideoObject videoObject){
//        openDatabase();
//        int result = -1;
//        try {
//            ContentValues value = mapContentValues(videoObject);
//            result = mDb.update(
//                    DbDefines.TABLE_VIDEOS,
//                    value,
//                    DbDefines.VIDEO_ID + " = '" + videoObject.getId() + "'",
//                    null);
//        } catch (SQLiteException e) {
//            LogUtils.logI(TAG, e.toString());
//            result = -1;
//        } finally {
//            this.close();
//        }

        long result = -1;
        result = videoObject.save();

        return (result > 0);
    }

    public boolean deleteVideoById(String videoId) {
        String whereClause = DbDefines.VIDEO_ID + " = '" + videoId + "'";
//        boolean result = false;
//        openDatabase();
//        try {
//            result = (mDb.delete(DbDefines.TABLE_VIDEOS, whereClause, null) != -1);
//        } catch (Exception e) {
//            e.printStackTrace();
//            result = false;
//        } finally{
//            this.close();
//        }

        List<VideoObject> listDeleted = new Delete().from(VideoObject.class).where(whereClause).execute();

        if(listDeleted == null || listDeleted.size() == 0){
            return false;
        }else {
            return true;
        }


    }

    public int deleteListVideos(List<VideoObject> listVideosDelete){
//        if(listVideosDelete != null && listVideosDelete.size() > 0){
//            String[] listVideoIds = new String[listVideosDelete.size()];
//            for (int i = 0; i < listVideosDelete.size(); i++) {
//                listVideoIds[i] = listVideosDelete.get(i).getVideoId();
//            }
//            boolean result = false;
//            openDatabase();
//            String query = DbDefines.VIDEO_ID + " IN (" + new String(new char[listVideoIds.length-1]).replace("\0", "?,") + "?)";
//            int number = mDb.delete(DbDefines.TABLE_VIDEOS, query , listVideoIds);
//            this.close();
//
//            return number;
//        }else {
//            return 0;
//        }
        int result = 0;
        try{
            for (int i = 0; i < listVideosDelete.size(); i++) {
                deleteVideoById(listVideosDelete.get(i).getVideoId());
                result ++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }



    public int getTotalVideos(){
//        openDatabase();
//        Cursor results = null;
//        try {
//            // Query
//            results = mDb.rawQuery("SELECT " + DbDefines.Video_URL + " FROM " + DbDefines.TABLE_VIDEOS, null);
//            if (results != null ) {
//                return results.getCount();
//            }
//        } catch (SQLiteException e) {
//            LogUtils.logI(TAG, e.toString());
//        } finally {
//            if (results != null) {
//                results.close();
//            }
//            // close the cursor and the database
//            this.close();
//        }
//        // value return
//        return Constants.ZERO_NUMBER;

        return new Select().from(VideoObject.class).count();
    }


    public List<VideoObject> getListVideosBaseOnTime(long time){
//        ArrayList<VideoObject> listVideos = new ArrayList<VideoObject>();
//        String query = "SELECT * FROM " + DbDefines.TABLE_VIDEOS + " WHERE " + DbDefines.Time + " < " + time + " ORDER BY " + DbDefines.Time + " DESC LIMIT " + Constants.NUMBER_LOAD_VIDEO;
//
//        Cursor results = null;
//        openDatabase();
//        try{
//            results = mDb.rawQuery(query, null);
//            if(results != null && results.getCount() > 0){
//                for (int i = 0; i < results.getCount(); i++) {
//                    results.moveToPosition(i);
//                    VideoObject videoObject = mapMessageData(results);
//                    listVideos.add(videoObject);
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            if(results != null){
//                results.close();
//            }
//            this.close();
//        }
//
//        return listVideos;

        List<VideoObject> listVideos = new ArrayList<VideoObject>();
        String queryString = DbDefines.Time + " < " + time;
        try{
            listVideos = new Select().from(VideoObject.class).where(queryString).orderBy(DbDefines.Time + " DESC").limit(Constants.NUMBER_LOAD_VIDEO).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        return listVideos;

    }



}
