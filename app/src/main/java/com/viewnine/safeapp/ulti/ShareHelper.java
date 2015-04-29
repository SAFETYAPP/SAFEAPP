package com.viewnine.safeapp.ulti;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by user on 4/30/15.
 */
public class ShareHelper {
    private static ShareHelper ourInstance = new ShareHelper();

    public static ShareHelper getInstance() {
        return ourInstance;
    }

    private ShareHelper() {
    }



}
