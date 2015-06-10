package com.viewnine.nuttysnap.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.manager.SwitchViewManager;
import com.viewnine.nuttysnap.manager.VideoManager;
import com.viewnine.nuttysnap.ulti.Constants;
import com.viewnine.nuttysnap.ulti.Ulti;
import com.viewnine.nuttysnap.ulti.ValidationHelper;

import io.fabric.sdk.android.Fabric;
/**
 * Created by user on 4/18/15.
 */
public class SplashScreenActivity extends ParentActivity {
    Context mContext;
    static int key_exit = 1;
    private String TAG = SplashScreenActivity.class.getName();
    private Button btnStartRecordingNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        mContext = SplashScreenActivity.this;

        Ulti.saveDrawableToSdCard(this, R.drawable.watermark, Constants.WATERMARK_NAME_BACK_CAMERA);
        Ulti.saveDrawableToSdCard(this, R.drawable.watermark2, Constants.WATERMARK_NAME_FRONT_CAMERA);
        handleFirstTimeRunning();
    }


    private void handleFirstTimeRunning() {

        addChidlView(R.layout.splashscreen_view);
        showHideHeader(false);

        btnStartRecordingNow = (Button) findViewById(R.id.button_start_recording_now);
        btnStartRecordingNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchViewManager.getInstance().gotoRecordSetupScreen(SplashScreenActivity.this);
            }
        });

        if(ValidationHelper.getInstance().alreadySetupEmail()){
            handler.sendEmptyMessageDelayed(key_exit, 1000);
            btnStartRecordingNow.setVisibility(View.GONE);
        }else {
            btnStartRecordingNow.setVisibility(View.VISIBLE);
        }



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
            deleteVideosExpiredDay();
            SwitchViewManager.getInstance().gotoRecordForegroundVideoScreen(this);
        }

//        SwitchViewManager.getInstance().gotoLockScreen(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        handleFirstTimeRunning();
    }

    /**
     * Remove videos after 7days
     */
    private void deleteVideosExpiredDay(){
        VideoManager.getInstance(this).deleteVideosExpiredDay();
    }


}
