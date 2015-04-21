package com.viewnine.safeapp.manager;

import android.content.Context;
import android.util.Log;

import com.viewnine.safeapp.database.VideoDBAdapter;
import com.viewnine.safeapp.model.VideoObject;
import com.viewnine.safeapp.ulti.LogUtils;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by user on 4/21/15.
 */
public class VideoQueueManager {

    public static ConcurrentLinkedQueue<VideoObject> listVideoNeedInsertToDB;

    private String TAG = VideoQueueManager.class.getName();
    boolean insertMsgThreadIsRunning = false;

    private static VideoQueueManager ourInstance = new VideoQueueManager();
    private static Context context;
    public static VideoQueueManager getInstance(Context context) {
        if(ourInstance == null){
            ourInstance = new VideoQueueManager();
        }
        VideoQueueManager.context = context;
        return ourInstance;
    }

    private VideoQueueManager() {
    }

    // Add message in queue to insert into DB, if thread is running -> just add
    // video in queue else start new thread to insert DB
    public void addVideoInQueue(VideoObject videoObject,
                                  boolean doNotification) {

        Log.d(TAG, "Do notification: " + doNotification);

        if (listVideoNeedInsertToDB == null)
            listVideoNeedInsertToDB = new ConcurrentLinkedQueue<VideoObject>();


        LogUtils.logI(TAG, "Thread is running: " + insertMsgThreadIsRunning);
        if (!insertMsgThreadIsRunning) {
            listVideoNeedInsertToDB.add(videoObject);

            new Thread(insertVideoRunnable).start();
        } else {
            listVideoNeedInsertToDB.add(videoObject);

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
//                                notifySignalUpdateUI(videoObject);
                            }
                        } else {
//                            notifySignalUpdateUI(videoObject);
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

    public boolean insertVideoIntoDB(VideoObject videoObject){
        VideoDBAdapter videoDBAdapter = new VideoDBAdapter(context);
        return videoDBAdapter.insertVideo(videoObject);
    }
}
