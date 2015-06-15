package com.viewnine.nuttysnap.manager;

import android.content.Context;
import android.util.Log;

import com.viewnine.nuttysnap.database.VideoDBHelper;
import com.viewnine.nuttysnap.model.VideoObject;
import com.viewnine.nuttysnap.ulti.BaseAsyncTaskV2;
import com.viewnine.nuttysnap.ulti.Constants;
import com.viewnine.nuttysnap.ulti.LogUtils;
import com.viewnine.nuttysnap.ulti.Ulti;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by user on 4/21/15.
 */
public class VideoManager {

    public static ConcurrentLinkedQueue<VideoObject> listVideoNeedInsertToDB;
    public static ConcurrentLinkedQueue<VideoObject> listVideoNeedAddWatermark;

    private String TAG = VideoManager.class.getName();
    boolean insertMsgThreadIsRunning = false;
    boolean addingWatermarkThreadIsRunning = false;

    private static VideoManager ourInstance = new VideoManager();
    private static Context context;

    public interface ISavingVideoListener{
        public void successful(VideoObject videoObject);
        public void fail();
    }

    public interface IAddWatermarkListener{
        public void successful(VideoObject videoObject);
        public void fail();
    }
    private ISavingVideoListener savingVideoListener;
    private IAddWatermarkListener addWatermarkListener;

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
    public void addVideoInQueueToInsertDB(VideoObject videoObject,
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
        VideoDBHelper videoDBAdapter = new VideoDBHelper();
        return videoDBAdapter.insertVideo(videoObject);
    }

    public boolean updateVideoDB(VideoObject videoObject){
        VideoDBHelper videoAdapter = new VideoDBHelper();
        return videoAdapter.updateVideo(videoObject);
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


            VideoDBHelper videoDBAdapter = new VideoDBHelper();
            boolean isDelete = videoDBAdapter.deleteVideoById(videoObject.getVideoId());
            if(isDelete){
                Ulti.deleteFile(videoObject.getImageLink(), context); //Delete Video
                Ulti.deleteFile(videoObject.getVideoUrl(), context); //Delete Image
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


            return deleteListVideos(context, listVideoObject);

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

    private int deleteListVideos(Context context, List<VideoObject> listVideoObject){
        VideoDBHelper videoDBAdapter = new VideoDBHelper();
        int numberVideoDeleted = videoDBAdapter.deleteListVideos(listVideoObject);
        if(numberVideoDeleted > 0){

            for(VideoObject videoObject : listVideoObject){
                Ulti.deleteFile(videoObject.getImageLink(), context); //Delete Video
                Ulti.deleteFile(videoObject.getVideoUrl(), context); //Delete Image
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
            VideoDBHelper videoDBAdapter = new VideoDBHelper();
            List<VideoObject> listVideos = videoDBAdapter.getListVideosBaseOnTime(calendar.getTimeInMillis());
            deleteListVideos(context.getApplicationContext(), listVideos);
        }
    }


    public void addWatermarkInQueue(VideoObject videoObject, IAddWatermarkListener addWatermarkListener) {

        this.addWatermarkListener = addWatermarkListener;

        if (listVideoNeedAddWatermark == null)
            listVideoNeedAddWatermark = new ConcurrentLinkedQueue<VideoObject>();


        LogUtils.logI(TAG, "Thread is running: " + addingWatermarkThreadIsRunning);

        if(videoObject != null){
            if (!addingWatermarkThreadIsRunning) {
                listVideoNeedAddWatermark.add(videoObject);

                new Thread(addingWatermarkRunnable).start();
            } else {
                listVideoNeedAddWatermark.add(videoObject);

            }
        }

    }

    Runnable addingWatermarkRunnable = new Runnable() {

        @Override
        public void run() {
            addingWatermarkThreadIsRunning = true;
            LogUtils.logD("HANDLERTEST", "is listVideoAddingWatermark null: " + (listVideoNeedAddWatermark == null));
            LogUtils.logD("HANDLERTEST", "Size of the list: " + listVideoNeedAddWatermark.size());

            if (listVideoNeedAddWatermark != null
                    && listVideoNeedAddWatermark.size() > 0) {
                while (listVideoNeedAddWatermark.size() > 0) {
                    try {
                        VideoObject videoObject = listVideoNeedAddWatermark.poll();

                        LogUtils.logD(TAG, "Adding watermark...");
                        String waterMarkVideoLink = Ulti.addWaterMark(VideoManager.context.getApplicationContext(), videoObject.getVideoUrl(), videoObject.getCameraMode());
                        if(!waterMarkVideoLink.isEmpty()){
                            videoObject.setVideoUrl(waterMarkVideoLink);
                            videoObject.setIsAddedWatermark(1);
                            LogUtils.logD(TAG, "Watermark is added");
                            boolean result = updateVideoDB(videoObject);
                            if(addWatermarkListener != null){
                                if(result){
                                    Ulti.addVideoToMediaStore(VideoManager.context, new File(videoObject.getVideoUrl()));
                                    addWatermarkListener.successful(videoObject);
                                }else {
                                    addWatermarkListener.fail();
                                }
                            }
                        }else {
                            LogUtils.logD(TAG, "Failed to add watermark");
                            if(addWatermarkListener != null){
                                addWatermarkListener.fail();
                            }
                        }
                    } catch (NoSuchElementException e) {
                        e.printStackTrace();
                        if(addWatermarkListener != null){
                            addWatermarkListener.fail();
                        }
                    }

                }
            }
            addingWatermarkThreadIsRunning = false;
        }
    };

}
