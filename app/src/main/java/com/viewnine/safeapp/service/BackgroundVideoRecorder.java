package com.viewnine.safeapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.viewnine.safeapp.activity.HistoryActivity;
import com.viewnine.safeapp.activity.R;
import com.viewnine.safeapp.manager.VideoQueueManager;
import com.viewnine.safeapp.model.VideoObject;
import com.viewnine.safeapp.ulti.Constants;
import com.viewnine.safeapp.ulti.LogUtils;
import com.viewnine.safeapp.ulti.Ulti;

import java.util.Calendar;

public class BackgroundVideoRecorder extends Service implements SurfaceHolder.Callback {
    public static final String TAG = BackgroundVideoRecorder.class.getName();
    private WindowManager windowManager;
    private SurfaceView surfaceView;
    private Camera camera = null;
    private MediaRecorder mediaRecorder = null;
    private String fileName = Constants.EMPTY_STRING;
    private VideoObject videoObject;

    @Override
    public void onCreate() {

        // Start foreground service to avoid unexpected kill 
//        Notification notification = new Notification.Builder(this)
//            .setContentTitle("Background Video Recorder")
//            .setContentText("")
//            .setSmallIcon(R.drawable.lockicon)
//            .build();
//        startForeground(1234, notification);
        initNotificaiton();
        super.onCreate();
    }

    private void initNotificaiton(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.safeapp_system_tray_icon)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.tap_to_end_current_recording))
                .setAutoCancel(true);


        Intent resultIntent = new Intent(this, HistoryActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

        int mNotificationID = 001;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(mNotificationID, mBuilder.build());
    }


    private void initSurface(){
        // Create new SurfaceView, set its size to 1x1, move it to the top left corner and set this service as a callback
        windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        surfaceView = new SurfaceView(this);
        LayoutParams layoutParams = new LayoutParams(
                1, 1,
                LayoutParams.TYPE_SYSTEM_OVERLAY,
                LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(surfaceView, layoutParams);
        surfaceView.getHolder().addCallback(this);
    }

    int startId;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        this.startId = startId;
        initSurface();

        return START_STICKY;
    }

    // Method called right after Surface created (initializing and starting MediaRecorder)
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Ulti.createFolder(Constants.VIDEO_FOLDER);
        long time = Calendar.getInstance().getTimeInMillis();
        fileName = Constants.VIDEO_FOLDER + Constants.PREFIX_VIDEO_NAME + time + Constants.VIDEO_TYPE;
        try {

            camera = Camera.open();
            mediaRecorder = new MediaRecorder();
            camera.unlock();
            try {
                camera.enableShutterSound(false);

            }catch (Exception e){
                e.printStackTrace();
                LogUtils.logE(TAG, "Fail to enable shutter sound: " + e.toString());
            }
            mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
            mediaRecorder.setCamera(camera);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            try {
                mediaRecorder.setProfile(CamcorderProfile.get(Constants.CAMERA_QUALITY));
            }catch (Exception e){
                e.printStackTrace();
            }
            mediaRecorder.setOrientationHint(Constants.POSITIVE_90_DEGREE);

            mediaRecorder.setMaxDuration(Constants.DEFAULT_TIME_TO_RECORDING);
            LogUtils.logD(TAG, "File name: " + fileName);
            mediaRecorder.setOutputFile(fileName);

            mediaRecorder.prepare();

            mediaRecorder.start();

            Toast.makeText(getBaseContext(), "Recording Started", Toast.LENGTH_SHORT).show();

            videoObject = new VideoObject();
            videoObject.setId(Constants.PREFIX_VIDEO_ID + time);
            videoObject.setVideoUrl(fileName);
            videoObject.setTime(time);

        } catch (Exception e) {
            videoObject = null;
            e.printStackTrace();
            Ulti.deleteFile(fileName);
            stopSelf(startId);
        }


    }

    // Stop recording and remove SurfaceView 
    @Override
    public void onDestroy() {

        try {
            if (camera != null && mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();

                camera.lock();
                camera.release();

                windowManager.removeView(surfaceView);

                Toast.makeText(getBaseContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();

                if(videoObject != null && !videoObject.getVideoUrl().isEmpty()){
                    LogUtils.logD(TAG, "Save video starting...");
                    String imageLink = Ulti.extractImageFromVideo(videoObject.getVideoUrl());
                    VideoObject videoObjectDB = new VideoObject();
                    videoObjectDB.setId(videoObject.getId());
                    videoObjectDB.setImageLink(imageLink);
                    videoObjectDB.setVideoUrl(videoObject.getVideoUrl());
                    videoObjectDB.setTime(videoObject.getTime());
                    videoObject = null;
                    VideoQueueManager.getInstance(getBaseContext()).addVideoInQueue(videoObjectDB, true, new VideoQueueManager.ISavingVideoListener() {
                        @Override
                        public void successful(VideoObject videoObject) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_BROADCAST_RECIVER_VIDEO);
                            sendBroadcast(intent);

                            Ulti.showNotificationForEachBackup(getBaseContext());
                        }

                        @Override
                        public void fail() {

                        }
                    });
                }else {
                    LogUtils.logD(TAG, "Fail to save video");
                }
            }
//            stopSelf(startId);



        } catch (Exception e) {
            e.printStackTrace();
            Ulti.deleteFile(fileName);
            stopSelf(startId);
        }



        super.onDestroy();


    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
