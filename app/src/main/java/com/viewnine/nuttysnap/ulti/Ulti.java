package com.viewnine.nuttysnap.ulti;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.netcompss.loader.LoadJNI;
import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.activity.HistoryActivity;
import com.viewnine.nuttysnap.activity.LockScreenAppActivity;
import com.viewnine.nuttysnap.manager.SharePreferenceManager;
import com.viewnine.nuttysnap.view.CameraPreview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 * Created by user on 4/19/15.
 */
public class Ulti {

    private static final String TAG = Ulti.class.getName();

    public static boolean isAppRunningBackground(Context context) {
        if (context == null) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getApplicationContext()
                .getSystemService(Service.ACTIVITY_SERVICE);
        // get the info from the currently running task

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;

        // get the name of the package:
        String packageName = componentInfo.getPackageName();

        // Log.v(TAG, "TESTING value = " + "2012-2-27 10:46:00"
        // .compareTo("2012-2-29 10:14:00"));

        // check if the app's just come back from background
        LogUtils.logI(TAG , "Current packageName: " + packageName + ". Current Activity name: " + context.getClass().getSimpleName());
        if (!packageName.equals(context.getApplicationContext().getPackageName())) {
            return true;
        }
        return false;
    }

    public static boolean isAppOnTop(Context context){
        if (context == null) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getApplicationContext()
                .getSystemService(Service.ACTIVITY_SERVICE);
        // get the info from the currently running task

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        ComponentName componentInfo = taskInfo.get(0).topActivity;

        // get the name of the package:
        String packageName = componentInfo.getPackageName();

        LogUtils.logI(TAG , "Current packageName: " + packageName + ". Current Activity name: " + componentInfo.getShortClassName());
        if(context.getApplicationContext().getPackageName().equalsIgnoreCase(packageName) && componentInfo.getShortClassName().contains(LockScreenAppActivity.class.getSimpleName())){
            return true;
        }else {
            return false;
        }

    }

    public static void createFolder(String folderName){
        File mkDir = new File(folderName);
        if(!mkDir.exists()){
            mkDir.mkdirs();
        }
    }



    public static int[] getScreenSize(Context context){
        int[] sizeOfScreen = new int[2];
        Point size = new Point();
        WindowManager w = ((Activity)context).getWindowManager();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)    {
            w.getDefaultDisplay().getSize(size);
            sizeOfScreen[0] = size.x;
            sizeOfScreen[1] = size.y;
        }else{
            Display d = w.getDefaultDisplay();
            sizeOfScreen[0] = d.getWidth();
            sizeOfScreen[1] = d.getHeight();
        }

        return sizeOfScreen;
    }

    public static int getDimenValueFromDimenXML(Context context, int dimenId){
        return (int) (context.getResources().getDimension(dimenId) / context.getResources().getDisplayMetrics().density);
    }
    // dip to px
    public static int convertDensityToPixel(Context context, int dip) {
//		return (int) (dip * context.getResources().getDisplayMetrics().density);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dip * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    // px to dip
    public static int convertPixelToDensity(Context context, int pixel) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                pixel, context.getResources().getDisplayMetrics());
    }

    public static int convertPixelsToDIP(Context context, int pixels) {
        // Resources resources = context.getResources();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float dp = pixels / (metrics.densityDpi / 160f);
        return (int) dp;
        // DisplayMetrics displayMetrics =
        // context.getResources().getDisplayMetrics();
        // return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
        // pixels, displayMetrics);

    }

    // px to dp
    public static int convertPixelToDp(Context context, int pixel) {
        DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        return (int) ((pixel / displayMetrics.density) + 0.5);
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }

        return false;
    }

    public static boolean deleteFile(String filePath, Context context){
        File file = new File(filePath);
        if(file.exists()){
            boolean isDeleted = file.delete();
            // request scan
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(Uri.fromFile(new File(filePath)));
            context.sendBroadcast(scanIntent);
            return isDeleted;
        }else {
            return true;
        }
    }

    public static boolean saveBitmapToSDCard(Bitmap bitmap, String filePath){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
            releaseBitmap(bitmap);
        } finally {
            try {
                if (out != null) {
                    out.close();
                    releaseBitmap(bitmap);
                    return true;
                }
            } catch (IOException e) {
                releaseBitmap(bitmap);
                e.printStackTrace();
            }
        }

        return false;
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public static void releaseBitmap(Bitmap bitmap){
        try{
            if(bitmap != null && !bitmap.isRecycled()){
                bitmap.recycle();
                bitmap = null;
            }
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    public static String extractImageFromVideo(String videoFile){
        LogUtils.logI(TAG, "Image extracting...");
        Ulti.createFolder(Constants.IMAGE_FOLDER);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        String picturePath = Constants.EMPTY_STRING;
        try {
            retriever.setDataSource(videoFile);
//            Bitmap bitmap = RotateBitmap(retriever.getFrameAtTime(-1), rotateAngle);
            Bitmap bitmap = retriever.getFrameAtTime(-1);
            String fileTmp = Constants.IMAGE_FOLDER + Calendar.getInstance().getTimeInMillis() + ".png";
            boolean isSaveSuccessfull = saveBitmapToSDCard(bitmap, fileTmp);
            if(isSaveSuccessfull){
                picturePath = fileTmp;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                retriever.release();
                return picturePath;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return picturePath;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    public static Map.Entry getEntryOfHashMap(int index, LinkedHashMap<?, ?> hashMap){

        Iterator iterator = hashMap.entrySet().iterator();
        int n = 0;
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            if(n == index){
                return entry;
            }
            n ++;
        }
        return null;
    }

    public static int getDurationVideoTime(int index, LinkedHashMap<?, ?> hashMap){

        Iterator iterator = hashMap.entrySet().iterator();
        int n = 0;
        while (iterator.hasNext()){
            Map.Entry entry = (Map.Entry) iterator.next();
            if(n == index){
                return (int)entry.getValue();

            }
            n ++;
        }
        return Constants.DEFAULT_TIME_TO_RECORDING;
    }

    public static void showNotificationForEachBackup(Context context){
        if(SharePreferenceManager.getInstance().isEnableNotificationForEachBackup()){
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.safeapp_system_tray_icon)
                    .setLargeIcon(((BitmapDrawable)context.getResources().getDrawable(R.drawable.ic_launcher)).getBitmap())
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText(context.getResources().getString(R.string.back_up_completed))
                    .setAutoCancel(true);


            Intent resultIntent = new Intent(context, HistoryActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

            Random randomNotification = new Random();
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(randomNotification.nextInt(), mBuilder.build());
        }

    }

    public static void showNotificationBackupStarting(Context context){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.safeapp_system_tray_icon)
                .setLargeIcon(((BitmapDrawable)context.getResources().getDrawable(R.drawable.ic_launcher)).getBitmap())
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getResources().getString(R.string.tap_to_end_current_recording))
                .setAutoCancel(true);


        Intent resultIntent = new Intent(context, HistoryActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

        int mNotificationID = 001;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mNotificationID, mBuilder.build());
    }

    public static void generateKeyHash(Context context){
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo("com.viewnine.safeapp",  PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        for (android.content.pm.Signature signature : info.signatures)
        {
            MessageDigest md = null;
            try {
                md = MessageDigest.getInstance("SHA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            md.update(signature.toByteArray());

            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static boolean checkSDCardFreeSpaceToStartRecording(){

        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());

        long sdAvailSize = 0;

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2){
            sdAvailSize = (long)stat.getAvailableBlocks()
                    * (long)stat.getBlockSize();
        }else {
            sdAvailSize = (long)stat.getAvailableBlocksLong()
                    * (long)stat.getBlockSizeLong();

        }
        long megAvailable = sdAvailSize / (1024 * 1024);


        if(megAvailable <= Constants.MINIMUM_STORAGE_SPACE){
            return false;
        }else {
            return true;
        }

     }

    public static void initRecorder(MediaRecorder mediaRecorder, SurfaceHolder surfaceHolder, Camera camera, int mCameraId, String fileName, Camera.Size sizeOfCamera){

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

        int rotateVideo = Constants.POSITIVE_90_DEGREE;
        int videoBitRate = 0;
        if(mCameraId != CameraPreview.DEFAULT_CAMERA){
            rotateVideo = Constants.DEGREE_270;
            videoBitRate = Constants.FRONT_CAMERA_BIT_RATE;
        }else {
            videoBitRate = Constants.BACK_CAMERA_BIT_RATE;
        }

        mediaRecorder.setVideoEncodingBitRate(videoBitRate);
        mediaRecorder.setOrientationHint(rotateVideo);
        mediaRecorder.setMaxDuration(Constants.DEFAULT_TIME_TO_RECORDING);
        LogUtils.logD(TAG, "File name: " + fileName);
        mediaRecorder.setOutputFile(fileName);

        if(sizeOfCamera != null){
            LogUtils.logI(TAG, "Cam width: " + sizeOfCamera.width + ".Cam height: " + sizeOfCamera.height);
            mediaRecorder.setVideoSize(sizeOfCamera.width, sizeOfCamera.height);
        }

    }


    public static String addWaterMark(Context context, String videoFilePath){
        Log.i(TAG, "ffmpeg4android adding watermark");
        long time = Calendar.getInstance().getTimeInMillis();
        String videoOut = Constants.VIDEO_FOLDER + Constants.PREFIX_VIDEO_NAME + time + Constants.VIDEO_TYPE;
        String[] commandStr = {"ffmpeg","-y" ,"-i", videoFilePath,"-strict","experimental",
                "-vf", "movie=/sdcard/watermark.png [watermark]; [in][watermark] overlay=main_w-overlay_w-10:10 [out]",
                "-s", "320x240","-r", "30", "-b", "15496k", "-vcodec", "mpeg4","-ab", "48000", "-ac", "2", "-ar", "22050",
                videoOut};
        LoadJNI vk = new LoadJNI();
        try {
            String workFolder = context.getApplicationContext().getFilesDir().getAbsolutePath();
            String[] complexCommand = {"ffmpeg","-i", "/sdcard/videokit/in.mp4"};
//            vk.run(GeneralUtils.utilConvertToComplex(commandStr), workFolder, getContext());
            vk.run(commandStr, workFolder, context);
            Log.i(TAG, "ffmpeg4android finished successfully");

            Ulti.deleteFile(videoFilePath, context);
            return videoOut;
        } catch (Throwable e) {
            Log.e(TAG, "vk run exception.", e);
        }

        return Constants.EMPTY_STRING;

    }

    public static Uri addVideoToMediaStore(Context context, File videoFile) {
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Video.Media.TITLE, "My video title");
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.Video.Media.DATA, videoFile.getAbsolutePath());
        return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }
}
