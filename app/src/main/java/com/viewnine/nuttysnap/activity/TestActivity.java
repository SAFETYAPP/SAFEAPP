package com.viewnine.nuttysnap.activity;

import android.app.Activity;
import android.os.Bundle;

import com.viewnine.nuttysnap.application.SafeAppApplication;
/**
 * Created by user on 5/19/15.
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SafeAppApplication.finishAllPreviousActivity();
    }
}
