package com.viewnine.safeapp.application;

import android.app.Application;
import android.content.Context;

/**
 * Created by user on 4/18/15.
 */
public class SafeAppApplication extends Application {

    private static SafeAppApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getInstance(){
        return instance;
    }

}
