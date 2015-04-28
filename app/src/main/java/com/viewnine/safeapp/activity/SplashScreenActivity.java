package com.viewnine.safeapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.viewnine.safeapp.manager.SwitchViewManager;
import com.viewnine.safeapp.ulti.ValidationHelper;

/**
 * Created by user on 4/18/15.
 */
public class SplashScreenActivity extends ParentActivity {
    Context mContext;
    static int key_exit = 1;
    private String TAG = SplashScreenActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = SplashScreenActivity.this;
    }


    private void handleFirstTimeRunning(){

        addChidlView(R.layout.splashscreen_view);
        showHideHeader(false);
        handler.sendEmptyMessageDelayed(key_exit, 1000);



    }

    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == key_exit) {
                handleGotoNextScreen();
            }
            return false;
        }


    });

    private void handleGotoNextScreen() {

        if(!ValidationHelper.getInstance().alreadySetupEmail()){
            SwitchViewManager.getInstance().gotoRecordSetupScreen(this);
        }else {
            SwitchViewManager.getInstance().gotoRecordForegroundVideoScreen(this);
        }

//        SwitchViewManager.getInstance().gotoSettingsScreen(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        handleFirstTimeRunning();
    }


}
