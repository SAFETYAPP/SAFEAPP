package com.viewnine.safeapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.viewnine.safeapp.ulti.Constants;
import com.viewnine.safeapp.ulti.LogUtils;
import com.viewnine.safeapp.ulti.Ulti;

import java.util.Calendar;

public class BackgroundVideoRecorder extends Service implements SurfaceHolder.Callback{
    private static final String TAG = BackgroundVideoRecorder.class.getName();
	private WindowManager windowManager;
    private SurfaceView surfaceView;
    private Camera camera = null; 
    private MediaRecorder mediaRecorder = null;

 
    @Override 
    public void onCreate() { 
 
        // Start foreground service to avoid unexpected kill 
//        Notification notification = new Notification.Builder(this)
//            .setContentTitle("Background Video Recorder")
//            .setContentText("")
//            .setSmallIcon(R.drawable.lockicon)
//            .build();
//        startForeground(1234, notification);


 
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
 
    // Method called right after Surface created (initializing and starting MediaRecorder) 
    @Override 
    public void surfaceCreated(SurfaceHolder surfaceHolder) {


            try {
                camera = Camera.open();
                mediaRecorder = new MediaRecorder();
                camera.unlock();

                mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
                mediaRecorder.setCamera(camera);
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));

                Ulti.createFolder(Constants.VIDEO_FOLDER);
                String fileName = Constants.VIDEO_FOLDER + Constants.PREFIX_VIDEO_NAME + Calendar.getInstance().getTimeInMillis() + Constants.VIDEO_TYPE;

                LogUtils.logD(TAG, "File name: " + fileName);
                mediaRecorder.setOutputFile(fileName);

                try { mediaRecorder.prepare(); } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaRecorder.start();

                Toast.makeText(getBaseContext(), "Recording Started", Toast.LENGTH_SHORT).show();

            }catch (Exception e){
                e.printStackTrace();
            }


    } 
 
    // Stop recording and remove SurfaceView 
    @Override 
    public void onDestroy() { 

        try {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();

            camera.lock();
            camera.release();

            windowManager.removeView(surfaceView);

            Toast.makeText(getBaseContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();


        }catch (Exception e){
            e.printStackTrace();
        }

    } 
 
    @Override 
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {}
 
    @Override 
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {}
 
    @Override 
    public IBinder onBind(Intent intent) { return null; }
}
