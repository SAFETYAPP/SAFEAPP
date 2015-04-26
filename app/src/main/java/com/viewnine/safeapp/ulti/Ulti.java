package com.viewnine.safeapp.ulti;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.viewnine.safeapp.activity.LockScreenAppActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static void deleteFile(String fileStr){
        File file = new File(fileStr);
        if(file.exists()){
            file.delete();
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


}
