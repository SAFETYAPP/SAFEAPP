package com.viewnine.nuttysnap.activity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.viewnine.nuttysnap.application.SafeAppApplication;
import com.viewnine.nuttysnap.glowpad.GlowPadView;
import com.viewnine.nuttysnap.lockPattern.LockPatternViewEx;
import com.viewnine.nuttysnap.manager.SharePreferenceManager;
import com.viewnine.nuttysnap.manager.SwitchViewManager;
import com.viewnine.nuttysnap.service.BackgroundVideoRecorder;
import com.viewnine.nuttysnap.service.LockScreenService;
import com.viewnine.nuttysnap.ulti.AlertHelper;
import com.viewnine.nuttysnap.ulti.Constants;
import com.viewnine.nuttysnap.ulti.DateHelper;
import com.viewnine.nuttysnap.ulti.LogUtils;
import com.viewnine.nuttysnap.ulti.Ulti;
import com.viewnine.nuttysnap.ulti.ViewUlti;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;


public class LockScreenAppActivity extends Activity implements View.OnClickListener{

    private static final int HOME_TYPE = 0;
    private static final int PATTERN_TYPE = 1;
    private static final int VIDEO_TYPE = 2;
    private GlowPadView glowPadView;
    private TextView lblTime, lblDate;
    private Calendar calendar;
    SimpleDateFormat sdf;
    private RelativeLayout rlLockPattern;
    private LockPatternViewEx lockPatternView;
    private TextView txtWrongPattern;
    private Button btnCancelPattern;
    private String TAG = LockScreenAppActivity.class.getName();
    private TextView lblTimeAMPM;
    private String patternStringSetting;
    private enum GLOWPAD_TYPE{
        PROFILE, VIDEO, HOME
    }

    GLOWPAD_TYPE glowpad_type;

    public WindowManager winManager;
    public RelativeLayout wrapperView;

    View mainView;

    Handler handler = new Handler();
    protected int timeToRecord = Constants.DEFAULT_TIME_TO_RECORDING;
    CallReceiver callReceiver;


    @Override
    public void onAttachedToWindow() {
        // TODO Auto-generated method stub
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG|WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onAttachedToWindow();
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//        if(Constants.DISABLE_SYSTEM_LOCK_SCREEN){
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//        }
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);





//        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams( WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
//                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
//                PixelFormat.TRANSLUCENT);

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams( WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        this.winManager = ((WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE));
        this.wrapperView = new RelativeLayout(getBaseContext());
        getWindow().setAttributes(localLayoutParams);
        mainView = View.inflate(this, R.layout.lockscreen_view, this.wrapperView);
        this.winManager.addView(this.wrapperView, localLayoutParams);



        setupViews();
        getTimeToRecord();
        registerPhoneCall();

    }

    private void setupViews(){
//        addChidlView(R.layout.lockscreen_view);
//        showHideHeader(false);
//        setContentView(R.layout.lockscreen_view);

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        ((LinearLayout) mainView.findViewById(R.id.linearlayout_test)).setBackground(wallpaperDrawable);

        initLockScreen();
        initGlowPad();
        initDateTime();
        initLockPattern();
    }


    private void initLockPattern() {
        rlLockPattern = (RelativeLayout) mainView.findViewById(R.id.relativelayout_pattern);
        lockPatternView = (LockPatternViewEx) mainView.findViewById(R.id.lockpatternview_pattern);
        txtWrongPattern = (TextView) mainView.findViewById(R.id.textview_wrong_pattern);
        btnCancelPattern = (Button) mainView.findViewById(R.id.button_cancel_pattern);
        btnCancelPattern.setOnClickListener(this);
        patternStringSetting = SharePreferenceManager.getInstance().getUnlockPattern();

        btnCancelPattern.setShadowLayer(25, 0, 0, getResources().getColor(R.color.black));
        txtWrongPattern.setShadowLayer(25, 0, 0, getResources().getColor(R.color.black));

        lockPatternView.setOnPatternListener(new LockPatternViewEx.OnPatternListener() {
            @Override
            public void onPatternStart() {

            }

            @Override
            public void onPatternCleared() {

            }

            @Override
            public void onPatternCellAdded(List<LockPatternViewEx.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<LockPatternViewEx.Cell> pattern) {
                handleDrawPatternString(pattern.toString());
            }
        });
    }

    /**
     * if current pattern is equal with setting's pattern -> Goto History screen
     * else clear pattern and notify user: wrong pattern
     * @param patternStringSelected
     */
    private void handleDrawPatternString(String patternStringSelected){
        if(!patternStringSetting.equalsIgnoreCase(patternStringSelected)){
            lockPatternView.clearPattern();
            txtWrongPattern.setVisibility(View.VISIBLE);
        }else {
            switch (glowpad_type){
                case PROFILE:
                    SwitchViewManager.getInstance().gotoHistoryScreen(this);
                    break;
                case VIDEO:
                    startRecordVideobackground();
                    break;
                case HOME:
                    finish();
                    break;
            }


        }
    }

    private void initGlowPad() {
        glowPadView = (GlowPadView) mainView.findViewById(R.id.glow_pad_view);
        glowPadView.setOnTriggerListener(new GlowPadView.OnTriggerListener() {
            @Override
            public void onGrabbed(View v, int handle) {

            }

            @Override
            public void onReleased(View v, int handle) {

            }

            @Override
            public void onTrigger(View v, int target) {
//                Toast.makeText(LockScreenAppActivity.this, "Clicked on position: " + target, Toast.LENGTH_SHORT).show();
                switch (target) {
                    case HOME_TYPE:
                        handleUnlockSelected();
                        break;
                    case VIDEO_TYPE:
                        handleVideoSelected();
                        break;
                    case PATTERN_TYPE:
                        handlePatternSelected();
                        break;
                    default:
//                        finish();
                        break;

                }
            }

            @Override
            public void onGrabbedStateChange(View v, int handle) {

            }

            @Override
            public void onFinishFinalAnimation() {

            }
        });

    }

    private void setCurrentDateTime() {
        calendar = Calendar.getInstance();

        sdf = new SimpleDateFormat(DateHelper.RFC_USA_9);
        String time = sdf.format(calendar.getTime());
        lblTime.setText(time);

        sdf = new SimpleDateFormat(DateHelper.RFC_USA_12);
        lblTimeAMPM.setText(sdf.format(calendar.getTime()));

        lblDate.setText(DateHelper.getDateMessageFullMonth(calendar));
    }

    Handler mainhandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            setCurrentDateTime();


        }
    };

    private void initDateTime() {
        lblTime = (TextView) mainView.findViewById(R.id.time);
        lblTimeAMPM = (TextView) mainView.findViewById(R.id.time2);
        lblDate = (TextView) mainView.findViewById(R.id.date);

        lblTime.setShadowLayer(25, 0, 0, getResources().getColor(R.color.black));
        lblTimeAMPM.setShadowLayer(25, 0, 0, getResources().getColor(R.color.black));
        lblDate.setShadowLayer(25, 0, 0, getResources().getColor(R.color.black));

        setCurrentDateTime();

        new Thread(new Runnable() {
            public void run() {

                while (true) {
                    try {

                        Thread.sleep(1000);
                        mainhandler.sendEmptyMessage(0);

                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }

    private void turnOffScreen() {
        PowerManager manager = (PowerManager) getSystemService(Context.POWER_SERVICE);


// Choice 2
        PowerManager.WakeLock wl = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Your Tag");
        wl.acquire();
        wl.release();
    }

    private void initLockScreen() {

        ViewUlti.hideNavigationBar(this);

        if (getIntent() != null && getIntent().hasExtra("kill") && getIntent().getExtras().getInt("kill") == 1) {
            // Toast.makeText(this, "" + "kill activityy", Toast.LENGTH_SHORT).show();
            finish();
        }

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

    ;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        // Don't allow back to dismiss.
        return;
    }

    //only used in lockdown mode
    @Override
    protected void onPause() {
        super.onPause();

        // Don't hang around.
        // finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Don't hang around.
        // finish();
    }



    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
            //Intent i = new Intent(this, NewActivity.class);
            //startActivity(i);
            return false;
        }
        if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {

            System.out.println("alokkkkkkkkkkkkkkkkk");
            return true;
        }
        return false;
    }

    /*public void unloack(){

          finish();

    }*/
    public void onDestroy() {
        // k1.reenableKeyguard();

        this.winManager.removeView(this.wrapperView);
        this.wrapperView.removeAllViews();
        this.unregisterReceiver(callReceiver);
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_cancel_pattern:
                handleCancelPattern();
                break;
            default:
                break;
        }
    }

    private void handleCancelPattern(){
        rlLockPattern.setVisibility(View.GONE);
        glowPadView.setVisibility(View.VISIBLE);
    }

    private void handleVideoSelected() {
        glowpad_type = GLOWPAD_TYPE.VIDEO;

        if(Ulti.checkSDCardFreeSpaceToStartRecording()){
            if(SharePreferenceManager.getInstance().getUnlockPattern().isEmpty()){
                startRecordVideobackground();
            }else {
                txtWrongPattern.setVisibility(View.GONE);
                rlLockPattern.setVisibility(View.VISIBLE);
                glowPadView.setVisibility(View.GONE);
            }
        }else {
            AlertHelper.getInstance().showMessageAlert(this, getResources().getString(R.string.storage_full));
        }


    }

    private void startRecordVideobackground(){

            stopTimerTask();
            handleRecordingInBackgroundThread();
//        SwitchViewManager.getInstance().sendAppToBackground(this);
            finish();

    }


    private void handlePatternSelected() {
        glowpad_type = GLOWPAD_TYPE.PROFILE;
        if(SharePreferenceManager.getInstance().getUnlockPattern().isEmpty()){
            SwitchViewManager.getInstance().gotoHistoryScreen(this);
        }else {
            txtWrongPattern.setVisibility(View.GONE);
            rlLockPattern.setVisibility(View.VISIBLE);
            glowPadView.setVisibility(View.GONE);
        }
    }

    private void handleUnlockSelected(){
        glowpad_type = GLOWPAD_TYPE.HOME;
        if(SharePreferenceManager.getInstance().getUnlockPattern().isEmpty()){
            finish();
        }else {
            txtWrongPattern.setVisibility(View.GONE);
            rlLockPattern.setVisibility(View.VISIBLE);
            glowPadView.setVisibility(View.GONE);
        }
    }


    private void getTimeToRecord(){
        timeToRecord = Ulti.getDurationVideoTime(SharePreferenceManager.getInstance().getIndexDurationTime(), Constants.TIME_INTERVAL_LIST);
    }

    protected void stopTimerTask(){

        SafeAppApplication.stopTimer();
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
                            Toast.makeText(LockScreenAppActivity.this, LockScreenAppActivity.this.getResources().getString(R.string.storage_full), Toast.LENGTH_LONG).show();
                        }


//                        Log.d(TAG, "Start timer");
                    }
                });

            }
        };

        SafeAppApplication.getTimer().schedule(timerTask, Constants.TIME_DELAY, timeToRecord + Constants.TIME_TO_PENDING);


    }

    protected void startRecordingInBackgroundThread(){
        Intent intent = new Intent(getApplicationContext(), BackgroundVideoRecorder.class);
        startService(intent);

    }

    protected void stopRecordingInBackgroundThread(){
        Intent intent = new Intent(getApplicationContext(), BackgroundVideoRecorder.class);
        stopService(intent);
    }

    private void registerPhoneCall(){

//        callReceiver = new CallReceiver(){
//
//            @Override
//            public void inComingCallStarted() {
//                SwitchViewManager.getInstance().sendAppToBackground(LockScreenAppActivity.this);
//
//            }
//        };

        callReceiver = new CallReceiver();

//        IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        intentFilter.setPriority(999);
        this.registerReceiver(callReceiver, intentFilter);
    }


    class CallReceiver extends PhonecallReceiver {

        private final String TAG = CallReceiver.class.getName();

        @Override
        protected void onIncomingCallStarted(Context ctx, String number, Date start) {
            LogUtils.logD(TAG, "IncommingCall Started");
//            stopService(new Intent(ctx, LockScreenService.class));
            SharePreferenceManager.getInstance().setIsExitAppFromLockScreenActivity(true);
            finish();
//            startService(new Intent(ctx, LockScreenService.class));

        }

        @Override
        protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
            LogUtils.logD(TAG, "onOutgoingCallStarted");
        }

        @Override
        protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
            LogUtils.logD(TAG, "onIncomingCallEnded");
        }

        @Override
        protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
            LogUtils.logD(TAG, "onOutgoingCallEnded");
        }

        @Override
        protected void onMissedCall(Context ctx, String number, Date start) {
            LogUtils.logD(TAG, "onMissedCall");
        }

    }



    class PhonecallReceiver extends BroadcastReceiver {

        //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

        private int lastState = TelephonyManager.CALL_STATE_IDLE;
        private Date callStartTime;
        private boolean isIncoming;
        private String savedNumber;  //because the passed incoming is only valid in ringing


        @Override
        public void onReceive(Context context, Intent intent) {

            //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
            }
            else{
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                int state = 0;
                if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                    state = TelephonyManager.CALL_STATE_IDLE;
                }
                else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                }
                else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    state = TelephonyManager.CALL_STATE_RINGING;
                }


                onCallStateChanged(context, state, number);
            }
        }

        //Derived classes should override these to respond to specific events of interest
        protected void onIncomingCallStarted(Context ctx, String number, Date start){}
        protected void onOutgoingCallStarted(Context ctx, String number, Date start){}
        protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end){}
        protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end){}
        protected void onMissedCall(Context ctx, String number, Date start){}

        //Deals with actual events

        //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
        //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
        public void onCallStateChanged(Context context, int state, String number) {
            if(lastState == state){
                //No change, debounce extras
                return;
            }
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    isIncoming = true;
                    callStartTime = new Date();
                    savedNumber = number;
                    onIncomingCallStarted(context, number, callStartTime);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                    if(lastState != TelephonyManager.CALL_STATE_RINGING){
                        isIncoming = false;
                        callStartTime = new Date();
                        onOutgoingCallStarted(context, savedNumber, callStartTime);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                    if(lastState == TelephonyManager.CALL_STATE_RINGING){
                        //Ring but no pickup-  a miss
                        onMissedCall(context, savedNumber, callStartTime);
                    }
                    else if(isIncoming){
                        onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                    }
                    else{
                        onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                    }
                    break;
            }
            lastState = state;
        }
    }





}