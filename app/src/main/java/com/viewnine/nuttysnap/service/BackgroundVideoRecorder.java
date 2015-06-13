package com.viewnine.nuttysnap.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.viewnine.nuttysnap.activity.LockScreenAppActivity;
import com.viewnine.nuttysnap.manager.EmailManager;
import com.viewnine.nuttysnap.manager.VideoManager;
import com.viewnine.nuttysnap.manager.base.LocationVideoManger;
import com.viewnine.nuttysnap.model.VideoObject;
import com.viewnine.nuttysnap.ulti.Constants;
import com.viewnine.nuttysnap.ulti.LogUtils;
import com.viewnine.nuttysnap.ulti.Ulti;
import com.viewnine.nuttysnap.view.CameraPreview;

import java.util.Calendar;

public class BackgroundVideoRecorder extends Service implements SurfaceHolder.Callback {
    public static final String TAG = BackgroundVideoRecorder.class.getName();
    private WindowManager windowManager;
    private SurfaceView surfaceView;
    private Camera camera = null;
    private MediaRecorder mediaRecorder = null;
    private String fileName = Constants.EMPTY_STRING;
    private VideoObject videoObject;
    public static final int BACK_CAMERA_ID = 0;
    public static final int FRONT_CAMERA_ID = 1;
    private int cameraId = BACK_CAMERA_ID;

    @Override
    public void onCreate() {

        // Start foreground service to avoid unexpected kill 
//        Notification notification = new Notification.Builder(this)
//            .setContentTitle("Background Video Recorder")
//            .setContentText("")
//            .setSmallIcon(R.drawable.lockicon)
//            .build();
//        startForeground(1234, notification);
//        initNotificaiton();
        super.onCreate();
    }

    private void initNotificaiton(){
       Ulti.showNotificationBackupStarting(this);
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
        try{
            cameraId = intent.getIntExtra(Constants.CAMERA_ID, BACK_CAMERA_ID);
        }catch (Exception e){
            e.printStackTrace();
            //Hardcode here if can not get camera id from intent
            cameraId = LockScreenAppActivity.cameraId;
        }
        initSurface();

        return START_STICKY;
    }

    // Method called right after Surface created (initializing and starting MediaRecorder)
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Ulti.createFolder(Constants.VIDEO_FOLDER);
        long time = Calendar.getInstance().getTimeInMillis();
        String physicalAddress = LocationVideoManger.getPhysicalAddress();
        fileName = Constants.VIDEO_FOLDER + Constants.PREFIX_VIDEO_NAME + time + physicalAddress + Constants.VIDEO_TYPE;
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                if (Camera.getNumberOfCameras() <= cameraId) {
                    cameraId = CameraPreview.DEFAULT_CAMERA;
                }

            } else {
                cameraId = CameraPreview.DEFAULT_CAMERA;
            }
            camera = Camera.open(cameraId);


            Camera.Size sizeOfCamera = null;
            for (int i = 0; i < camera.getParameters().getSupportedPreviewSizes().size(); i++) {
                Camera.Size previewSize = camera.getParameters().getSupportedPreviewSizes().get(i);
                boolean foundEqualSize = false;
                for (int j = 0; j < camera.getParameters().getSupportedPictureSizes().size(); j++) {
                    Camera.Size pictureSize = camera.getParameters().getSupportedPictureSizes().get(j);
                    if(previewSize.width == pictureSize.width && previewSize.height == pictureSize.height){
                        sizeOfCamera = previewSize;
                        foundEqualSize = true;
                        break;
                    }
                }

                if(foundEqualSize) break;
            }
            mediaRecorder = new MediaRecorder();

            Ulti.initRecorder(mediaRecorder, surfaceHolder, camera, cameraId, fileName, sizeOfCamera);

            mediaRecorder.prepare();
            mediaRecorder.start();


            videoObject = new VideoObject();
            videoObject.setId(Constants.PREFIX_VIDEO_ID + time);
            videoObject.setVideoUrl(fileName);
            videoObject.setTime(time);
            videoObject.setCameraMode(cameraId);

            initNotificaiton();



        } catch (Exception e) {
            videoObject = null;
            e.printStackTrace();
            Ulti.deleteFile(fileName, getBaseContext());
            stopSelf(startId);
            LogUtils.logE(TAG, "Surface created error: " + e.toString());
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


                if(videoObject != null && !videoObject.getVideoUrl().isEmpty()){
                    LogUtils.logD(TAG, "Save video starting...");
                    if(Constants.ENABLE_WATER_MARK){
                        String waterMarkVideoLink = Ulti.addWaterMark(getBaseContext(), fileName, videoObject.getCameraMode());
                        if(!waterMarkVideoLink.isEmpty()){
                            fileName = waterMarkVideoLink;
                            videoObject.setVideoUrl(fileName);
                        }
                    }
//                    Ulti.addVideoToMediaStore(getBaseContext(), new File(videoObject.getVideoUrl()));
                    String imageLink = Ulti.extractImageFromVideo(videoObject.getVideoUrl());
                    final VideoObject videoObjectDB = new VideoObject();
                    videoObjectDB.setId(videoObject.getId());
                    videoObjectDB.setImageLink(imageLink);
                    videoObjectDB.setVideoUrl(videoObject.getVideoUrl());
                    videoObjectDB.setTime(videoObject.getTime());
                    videoObjectDB.setCameraMode(videoObject.getCameraMode());
                    videoObject = null;
                    VideoManager.getInstance(getBaseContext()).addVideoInQueueToInsertDB(videoObjectDB, true, new VideoManager.ISavingVideoListener() {
                        @Override
                        public void successful(VideoObject videoObject) {
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTION_BROADCAST_RECIVER_VIDEO);
                            sendBroadcast(intent);

                            Ulti.showNotificationForEachBackup(getBaseContext());

                            //Send email
//                            EmailManager.getInstance().sendMail(Constants.MAIL_SUBJECT, Constants.MAIL_CONTENT, videoObjectDB.getVideoUrl());

                            addWatermark(videoObject);
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
            Ulti.deleteFile(fileName, getBaseContext());
            stopSelf(startId);
        }



        super.onDestroy();


    }

    private void addWatermark(VideoObject videoObject){
        VideoManager.getInstance(getBaseContext()).addWatermarkInQueue(videoObject, new VideoManager.IAddWatermarkListener() {
            @Override
            public void successful(VideoObject videoObject) {

                //Send email
                EmailManager.getInstance().sendMail(Constants.MAIL_SUBJECT, Constants.MAIL_CONTENT, videoObject.getVideoUrl());
            }

            @Override
            public void fail() {

            }
        });
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
