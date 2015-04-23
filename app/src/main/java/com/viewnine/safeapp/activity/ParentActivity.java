package com.viewnine.safeapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewnine.safeapp.application.SafeAppApplication;
import com.viewnine.safeapp.service.BackgroundVideoRecorder;
import com.viewnine.safeapp.service.LockScreenService;
import com.viewnine.safeapp.ulti.Constants;
import com.viewnine.safeapp.ulti.Ulti;

import java.util.TimerTask;

/**
 * Created by user on 4/19/15.
 */
public abstract class ParentActivity extends Activity implements View.OnClickListener{

    private LayoutInflater inflater;
    private RelativeLayout rlHeader;
    private TextView txtTitle;
    private TextView txtVideoNumber;
    private TextView txtVideos;
    private Button btnSettings;
    private Button btnGotoShare;
    private Button btnShare;
    private Button btnBack;
    private FrameLayout frParent;
    private LinearLayout lnVideoNumber;
    private String TAG = ParentActivity.class.getName();
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkToStartLockScreenService();
        setupParentViews();


    }

    private void checkToStartLockScreenService(){
        String splashScreenClass = SplashScreenActivity.class.getName();
        String setupClass = SetupActivity.class.getName();
        String currentClass = ParentActivity.class.getName();
        boolean isServiceRunning = Ulti.isServiceRunning(this, LockScreenService.class);
        if(!currentClass.equalsIgnoreCase(splashScreenClass) && !currentClass.equalsIgnoreCase(setupClass) && !isServiceRunning){
            try {
                // initialize receiver


                startService(new Intent(this, LockScreenService.class));

  /*      KeyguardManager km =(KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        k1 = km.newKeyguardLock("IN");
        k1.disableKeyguard();*/
                StateListener phoneStateListener = new StateListener();
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);


            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    System.out.println("call Activity off hook");
                    finish();


                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    }

    private void setupParentViews() {

        setContentView(R.layout.parent_view);
        rlHeader = (RelativeLayout) findViewById(R.id.relativelayout_header);
        lnVideoNumber = (LinearLayout) findViewById(R.id.linearlayout_video_title);
        txtTitle = (TextView) findViewById(R.id.textview_title);
        txtVideoNumber = (TextView) findViewById(R.id.textview_videos_number);
        txtVideos = (TextView) findViewById(R.id.videos);
        btnSettings = (Button) findViewById(R.id.button_setting);
        btnGotoShare = (Button) findViewById(R.id.button_goto_share);
        btnShare = (Button) findViewById(R.id.button_share);
        btnBack = (Button) findViewById(R.id.button_back);

        btnBack.setOnClickListener(this);
        btnGotoShare.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnShare.setOnClickListener(this);

        frParent = (FrameLayout) findViewById(R.id.framelayout_parent);

    }

    protected void showHideHeader(boolean isShow){
        if(isShow){
            rlHeader.setVisibility(View.VISIBLE);
        }else {
            rlHeader.setVisibility(View.GONE);
        }
    }
    protected void addChidlView(int viewId){
        inflater = LayoutInflater.from(this);
        View childView = View.inflate(this, viewId, null);

        frParent.removeAllViews();
        frParent.addView(childView);
    }

    protected void addSettingButton(){
        btnSettings.setVisibility(View.VISIBLE);
    }

    protected void addBackButton(){
        btnBack.setVisibility(View.VISIBLE);
    }

    protected void addGoToShareButton(){
        btnGotoShare.setVisibility(View.VISIBLE);
    }

    protected void addShareButton(){
        btnShare.setVisibility(View.VISIBLE);
    }

    protected void addVideoNumber(int videoNumbers){
        if(videoNumbers > Constants.ZERO_NUMBER){
            lnVideoNumber.setVisibility(View.VISIBLE);
            txtVideoNumber.setText(String.valueOf(videoNumbers));
            if(videoNumbers > 1){
                txtVideos.setText(getString(R.string.videos));
            }else {
                txtVideos.setText(getString(R.string.video));
            }

        }else {
            lnVideoNumber.setVisibility(View.GONE);
        }
    }

    protected void addTitle(String title){
        txtTitle.setVisibility(View.VISIBLE);
        txtTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_setting:

                break;
            case R.id.button_back:

                break;
            case R.id.button_goto_share:

                break;
            case R.id.button_share:

                break;
            default:

                break;
        }
    }


    protected void startRecordingInBackgroundThread(){
        Intent intent = new Intent(getApplicationContext(), BackgroundVideoRecorder.class);
        startService(intent);

    }

    protected void stopRecordingInBackgroundThread(){
        Intent intent = new Intent(getApplicationContext(), BackgroundVideoRecorder.class);
        stopService(intent);
    }

    protected void handleRecordingInBackgroundThread() {

        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Log.d(TAG, "Stop recording");
                        stopRecordingInBackgroundThread();
                        for (int i = 0; i < 100000; i++) {

                        }
                        Log.d(TAG, "Start recording");
                        startRecordingInBackgroundThread();

//                        Log.d(TAG, "Start timer");
                    }
                });

            }
        };

        SafeAppApplication.getTimer().schedule(timerTask, Constants.TIME_DELAY, Constants.TIME_TO_RECORDING);


    }

    protected void stopTimerTask(){

        SafeAppApplication.stopTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        stopTimerTask();
        stopRecordingInBackgroundThread();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }


}
