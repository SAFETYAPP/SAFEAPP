package com.viewnine.safeapp.application;

import android.app.Application;
import android.content.Context;

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
}
