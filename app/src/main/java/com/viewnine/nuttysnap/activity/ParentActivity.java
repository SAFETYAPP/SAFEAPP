package com.viewnine.nuttysnap.activity;

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
import android.widget.Toast;

import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.application.SafeAppApplication;
import com.viewnine.nuttysnap.manager.SharePreferenceManager;
import com.viewnine.nuttysnap.manager.SwitchViewManager;
import com.viewnine.nuttysnap.service.BackgroundVideoRecorder;
import com.viewnine.nuttysnap.service.LockScreenService;
import com.viewnine.nuttysnap.ulti.Constants;
import com.viewnine.nuttysnap.ulti.LogUtils;
import com.viewnine.nuttysnap.ulti.Ulti;

import java.util.TimerTask;

import butterknife.ButterKnife;

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
    public String TAG = ParentActivity.class.getName();
    Handler handler = new Handler();
    protected int timeToRecord = Constants.DEFAULT_TIME_TO_RECORDING;
    private LinearLayout lnEdit;
    private LinearLayout lnDelete;
    private RelativeLayout rlSettings;
    private RelativeLayout rlGotoShare;
    private RelativeLayout rlShare;
    private RelativeLayout rlBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SafeAppApplication.pushToSStackActivity(this);
        checkToStartLockScreenService();
        getTimeToRecord();
        setupParentViews();


    }

    private void getTimeToRecord(){
        timeToRecord = Ulti.getDurationVideoTime(SharePreferenceManager.getInstance().getIndexDurationTime(), Constants.TIME_INTERVAL_LIST);
    }

    protected void checkToStartLockScreenService(){
        String splashScreenClass = SplashScreenActivity.class.getName();
        String setupClass = SetupActivity.class.getName();
        String currentClass = ParentActivity.class.getName();
        boolean isServiceRunning = Ulti.isServiceRunning(this, LockScreenService.class);
        boolean enableLockScreen = SharePreferenceManager.getInstance().isEnableLockScreen();
        if(enableLockScreen && !currentClass.equalsIgnoreCase(splashScreenClass) && !currentClass.equalsIgnoreCase(setupClass) && !isServiceRunning){
            try {
                // initialize receiver


               startLockScreenService();

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

    protected void startLockScreenService(){
        LogUtils.logI(TAG, "Start lockscreen service");
        startService(new Intent(this, LockScreenService.class));
    }

    protected void stopLockScreenService(){
        LogUtils.logI(TAG, "Stop lockscreen service");
        stopService(new Intent(this, LockScreenService.class));
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
        ButterKnife.bind(this);
        rlHeader = (RelativeLayout) findViewById(R.id.relativelayout_header);
        lnVideoNumber = (LinearLayout) findViewById(R.id.linearlayout_video_title);
        txtTitle = (TextView) findViewById(R.id.textview_title);
        txtVideoNumber = (TextView) findViewById(R.id.textview_videos_number);
        txtVideos = (TextView) findViewById(R.id.videos);
        btnSettings = (Button) findViewById(R.id.button_setting);
        rlSettings = (RelativeLayout) findViewById(R.id.relativelayout_setting);
        btnGotoShare = (Button) findViewById(R.id.button_goto_share);
        rlGotoShare = (RelativeLayout) findViewById(R.id.relativelayout_goto_share);
        btnShare = (Button) findViewById(R.id.button_share);
        rlShare = (RelativeLayout) findViewById(R.id.relativelayout_share);
        btnBack = (Button) findViewById(R.id.button_back);
        rlBack = (RelativeLayout) findViewById(R.id.relativelayout_back);
        lnEdit = (LinearLayout) findViewById(R.id.linearlayout_edit_mode);
        lnDelete = (LinearLayout) findViewById(R.id.linearlayout_delete);

        btnBack.setOnClickListener(this);
        btnGotoShare.setOnClickListener(this);
        btnSettings.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        lnEdit.setOnClickListener(this);
        lnDelete.setOnClickListener(this);
        rlSettings.setOnClickListener(this);
        rlGotoShare.setOnClickListener(this);
        rlShare.setOnClickListener(this);
        rlBack.setOnClickListener(this);

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

    protected void showEditButton(Boolean isShow){
        if(isShow){
            lnEdit.setVisibility(View.VISIBLE);
        }else {
            lnEdit.setVisibility(View.GONE);
        }
    }

    protected void setInDeleteModeInHistoryScreen(boolean isInEditMode){
        if(isInEditMode){
            rlSettings.setVisibility(View.GONE);
//            rlGotoShare.setVisibility(View.GONE);
            addTitle(getString(R.string.edit));
            lnVideoNumber.setVisibility(View.GONE);
            lnEdit.setVisibility(View.GONE);

            rlBack.setVisibility(View.VISIBLE);
            lnDelete.setVisibility(View.VISIBLE);

        }else {
            rlSettings.setVisibility(View.VISIBLE);
//            rlGotoShare.setVisibility(View.VISIBLE);
            lnVideoNumber.setVisibility(View.VISIBLE);
            lnEdit.setVisibility(View.VISIBLE);

            txtTitle.setVisibility(View.GONE);
            rlBack.setVisibility(View.GONE);
            lnDelete.setVisibility(View.GONE);
        }
    }

    protected void showDeleteButton(Boolean isShow){
        if(isShow){
            lnDelete.setVisibility(View.VISIBLE);
        }else {
            lnDelete.setVisibility(View.GONE);
        }
    }

    protected void addSettingButton(){
        rlSettings.setVisibility(View.VISIBLE);
    }

    protected void addBackButton(){
        rlBack.setVisibility(View.VISIBLE);
    }

    protected void addGoToShareButton(){
        rlGotoShare.setVisibility(View.VISIBLE);
    }

    protected void addShareButton(){
        rlBack.setVisibility(View.VISIBLE);
    }

    protected void addVideoNumber(int videoNumbers, boolean isInDeleteMode){
        if(videoNumbers > Constants.ZERO_NUMBER){
            lnVideoNumber.setVisibility(View.VISIBLE);
            txtVideoNumber.setText(String.valueOf(videoNumbers));
            if(videoNumbers > 1){
                txtVideos.setText(getString(R.string.videos));
            }else {
                txtVideos.setText(getString(R.string.video));
            }

        }else {
            lnVideoNumber.setVisibility(View.VISIBLE);
            txtVideoNumber.setText(String.valueOf(videoNumbers));
            txtVideos.setText(getString(R.string.video));
        }

        if(isInDeleteMode){
            lnVideoNumber.setVisibility(View.GONE);
        }else {
            lnVideoNumber.setVisibility(View.VISIBLE);
        }
    }

    protected void addTitle(String title){
        txtTitle.setVisibility(View.VISIBLE);
        txtTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.relativelayout_setting:
            case R.id.button_setting:
                SwitchViewManager.getInstance().gotoSettingsScreen(this);
                break;
            case R.id.relativelayout_back:
            case R.id.button_back:
                onBackPressed();
                break;
            case R.id.relativelayout_goto_share:
            case R.id.button_goto_share:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.relativelayout_share:
            case R.id.button_share:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
                break;
            default:

                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (SafeAppApplication.getStackActivity() != null
                && SafeAppApplication.getStackActivity().size() > 0) {
//            SafeAppApplication.hideKeyboard(this);
            SafeAppApplication.getStackActivity().pop().finish();
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.push_down_out);

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
                        if(Ulti.checkSDCardFreeSpaceToStartRecording()){
                            Log.d(TAG, "Stop recording");
                            stopRecordingInBackgroundThread();
                            for (int i = 0; i < 100000; i++) {

                            }
                            Log.d(TAG, "Start recording");
                            startRecordingInBackgroundThread();

                        }else {
                            stopTimerTask();
                            Toast.makeText(ParentActivity.this, ParentActivity.this.getResources().getString(R.string.storage_full), Toast.LENGTH_LONG).show();
                        }


//                        Log.d(TAG, "Start timer");
                    }
                });

            }
        };

        SafeAppApplication.getTimer().schedule(timerTask, Constants.TIME_DELAY, timeToRecord + Constants.TIME_TO_PENDING);


    }

    protected void stopTimerTask(){

        SafeAppApplication.stopTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        stopTimerTask();
//        stopRecordingInBackgroundThread();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
