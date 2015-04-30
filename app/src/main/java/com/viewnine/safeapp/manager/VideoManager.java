package com.viewnine.safeapp.manager;

import android.content.Context;
import android.util.Log;

import com.viewnine.safeapp.database.VideoDBAdapter;
import com.viewnine.safeapp.model.VideoObject;
import com.viewnine.safeapp.ulti.BaseAsyncTaskV2;
import com.viewnine.safeapp.ulti.Constants;
import com.viewnine.safeapp.ulti.LogUtils;
import com.viewnine.safeapp.ulti.Ulti;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by user on 4/21/15.
 */
public class VideoManager {

    public static ConcurrentLinkedQueue<VideoObject> listVideoNeedInsertToDB;

    private String TAG = VideoManager.class.getName();
    boolean insertMsgThreadIsRunning = false;

    private static VideoManager ourInstance = new VideoManager();
    private static Context context;

    public interface ISavingVideoListener{
        public void successful(VideoObject videoObject);
        public void fail();
    }
    private ISavingVideoListener savingVideoListener;

    public interface IDeleteVideoListener{
        public void deleteSpecificVideoSuccessful(VideoObject videoObject);
        public void deleteListVideoSuccessful(ArrayList<VideoObject> listVideoObject);
        public void fail();
    }

    public static VideoManager getInstance(Context context) {
        if(ourInstance == null){
            ourInstance = new VideoManager();
        }
        VideoManager.context = context;
        return ourInstance;
    }

    private VideoManager() {
    }

    // Add message in queue to insert into DB, if thread is running -> just add
    // video in queue else start new thread to insert DB
    public void addVideoInQueue(VideoObject videoObject,
                                  boolean doNotification, ISavingVideoListener savingVideoListener) {

        this.savingVideoListener = savingVideoListener;
        Log.d(TAG, "Do notification: " + doNotification);

        if (listVideoNeedInsertToDB == null)
            listVideoNeedInsertToDB = new ConcurrentLinkedQueue<VideoObject>();


        LogUtils.logI(TAG, "Thread is running: " + insertMsgThreadIsRunning);

        if(videoObject != null){
            if (!insertMsgThreadIsRunning) {
                listVideoNeedInsertToDB.add(videoObject);

                new Thread(insertVideoRunnable).start();
            } else {
                listVideoNeedInsertToDB.add(videoObject);

            }
        }

    }

    // Insert video into DB and notify update UI, send message notifiy that
    // video is delivered and show popup in-app notification
    Runnable insertVideoRunnable = new Runnable() {

        @Override
        public void run() {
            insertMsgThreadIsRunning = true;
            LogUtils.logD("HANDLERTEST", "is listVideoNeedInsertToDB null: " + (listVideoNeedInsertToDB == null));
            LogUtils.logD("HANDLERTEST", "Size of the list: " + listVideoNeedInsertToDB.size());

            if (listVideoNeedInsertToDB != null
                    && listVideoNeedInsertToDB.size() > 0) {
                while (listVideoNeedInsertToDB.size() > 0) {
                    try {
                        VideoObject videoObject = listVideoNeedInsertToDB.poll();
                        boolean results = insertVideoIntoDB(videoObject);

                        if (!results) { // insert fail -> retry again 3 times
                            int retryNumber = 0;
                            while (!results && retryNumber < 3) {
                                results = insertVideoIntoDB(videoObject);
                                retryNumber++;
                            }
                            if (results) {
                                notifySignalUpdateUI(videoObject);
                            }
                        } else {
                            notifySignalUpdateUI(videoObject);
                        }

                        LogUtils.logD(TAG, "Insert Video successfull ? " + results + " : "+ videoObject.getVideoUrl());
                    } catch (NoSuchElementException e) {
//                        FlurryHelper.logException("insertVideoRunnable", MessageQueueManager.class.getName(), e);
                        e.printStackTrace();
                    }

                }
//                ChatsManager.getInstance().isChatRefreshNeeded = true;
//                ChatsManager.getInstance().sendMessageToUI(Constants.MSG_UPDATE_COMPLETE);
            }
            insertMsgThreadIsRunning = false;
        }
    };

    private void notifySignalUpdateUI(VideoObject videoObject) {
        if(savingVideoListener != null){
            savingVideoListener.successful(videoObject);
        }
    }

    public boolean insertVideoIntoDB(VideoObject videoObject){
        VideoDBAdapter videoDBAdapter = new VideoDBAdapter(context);
        return videoDBAdapter.insertVideo(videoObject);
    }


    public void deleteSpecificVideo(Context context, VideoObject videoObject, IDeleteVideoListener deleteVideoListener){
        new DeleteVideoAsync(context, videoObject, deleteVideoListener).execute();
    }
    private class DeleteVideoAsync extends BaseAsyncTaskV2{
        Context context;
        VideoObject videoObject;
        IDeleteVideoListener deleteVideoListener;
        public DeleteVideoAsync(Context context, VideoObject videoObject, IDeleteVideoListener deleteVideoListener){
            super(context);
            this.context = context;
            this.videoObject = videoObject;
            this.deleteVideoListener = deleteVideoListener;
        }

        private DeleteVideoAsync(Context context){
            super(context);
        }
        @Override
        protected Integer doInBackground(Void... params) {


            VideoDBAdapter videoDBAdapter = new VideoDBAdapter(context);
            boolean isDelete = videoDBAdapter.deleteVideoById(videoObject.getId());
            if(isDelete){
                Ulti.deleteFile(videoObject.getImageLink()); //Delete Video
                Ulti.deleteFile(videoObject.getVideoUrl()); //Delete Image
                return Constants.OK;
            }

            return Constants.ERROR;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if(deleteVideoListener != null){
                switch (result){
                    case Constants.OK:
                        deleteVideoListener.deleteSpecificVideoSuccessful(videoObject);
                        break;
                    default:
                        deleteVideoListener.fail();

                }
            }

        }
    }

    public void deleteListVideos(Context context, ArrayList<VideoObject> listVideoObject, boolean showLoadingDialog, IDeleteVideoListener deleteVideoListener){
        new DeleteListVideosAsync(context, listVideoObject, showLoadingDialog,deleteVideoListener).execute();
    }

    private class DeleteListVideosAsync extends BaseAsyncTaskV2{
        Context context;
        ArrayList<VideoObject> listVideoObject;
        IDeleteVideoListener deleteVideoListener;
        public DeleteListVideosAsync(Context context, ArrayList<VideoObject> listVideoObject, boolean showLoadingDialog, IDeleteVideoListener deleteVideoListener) {
            super(context);
            needToShowDialog(showLoadingDialog);
            this.context = context;
            this.listVideoObject = listVideoObject;
            this.deleteVideoListener = deleteVideoListener;
        }

        private DeleteListVideosAsync(Context context){
            super(context);
        }
        @Override
        protected Integer doInBackground(Void... params) {


            return deleteListVideos(listVideoObject);

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if(deleteVideoListener != null){
                switch (result){
                    case Constants.OK:
                        deleteVideoListener.deleteListVideoSuccessful(listVideoObject);
                        break;
                    default:
                        deleteVideoListener.fail();

                }
            }

        }
    }

    private int deleteListVideos(ArrayList<VideoObject> listVideoObject){
        VideoDBAdapter videoDBAdapter = new VideoDBAdapter(context);
        int numberVideoDeleted = videoDBAdapter.deleteListVideos(listVideoObject);
        if(numberVideoDeleted > 0){

            for(VideoObject videoObject : listVideoObject){
                Ulti.deleteFile(videoObject.getImageLink()); //Delete Video
                Ulti.deleteFile(videoObject.getVideoUrl()); //Delete Image
            }

            return Constants.OK;
        }else {
            return Constants.ERROR;
        }
    }

    public void deleteVideosExpiredDay(){
        if(Constants.ENABLE_CHECK_VIDEO_EXPIRED){
            DeleteVideosExpiredDayRunnable videosExpiredDayRunnable = new DeleteVideosExpiredDayRunnable();
            new Thread(videosExpiredDayRunnable).start();
        }
    }

    private class DeleteVideosExpiredDayRunnable implements Runnable{

        @Override
        public void run() {
            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, currentDay - Constants.VIDEO_EXPIRED_DAY);
            VideoDBAdapter videoDBAdapter = new VideoDBAdapter(context.getApplicationContext());
            ArrayList<VideoObject> listVideos = videoDBAdapter.getListVideosBaseOnTime(calendar.getTimeInMillis());
            deleteListVideos(listVideos);
        }
    }
}
