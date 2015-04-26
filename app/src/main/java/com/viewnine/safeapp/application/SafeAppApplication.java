package com.viewnine.safeapp.application;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.viewnine.safeapp.activity.R;

import java.util.Timer;

/**
 * Created by user on 4/18/15.
 */
public class SafeAppApplication extends Application {

    private static SafeAppApplication instance;
    private static Timer timer;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        initImageLoader(getApplicationContext());
    }

    public static Context getInstance(){
        if(instance == null){
            instance = new SafeAppApplication();
        }
        return instance;
    }


    public static Timer getTimer(){
        if(timer == null){
            timer = new Timer();
        }
        return timer;
    }

    public static void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        stopTimer();
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.


//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .displayer(new CircleBitmapDisplayer(0xFF70C7BE, 2))
//                .showImageOnLoading(R.drawable.avatar)
//                .showImageOnFail(R.drawable.avatar)
//                .showImageForEmptyUri(R.drawable.avatar)
//                .cacheOnDisk(true)
//                .cacheInMemory(false)
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .build();

        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
//                                                    .showImageOnLoading(R.drawable.loading_wheel_freely)
                                                    .showImageOnFail(R.drawable.safeapp_system_tray_icon_gray)
                                                    .build();

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(100 * 1024 * 1024); // 100 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.defaultDisplayImageOptions(displayImageOptions);
        config.writeDebugLogs(); // Remove for release app


        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
}
