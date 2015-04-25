package com.viewnine.safeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewnine.safeapp.glowpad.GlowPadView;
import com.viewnine.safeapp.lockPattern.LockPatternViewEx;
import com.viewnine.safeapp.manager.SharePreferenceManager;
import com.viewnine.safeapp.manager.SwitchViewManager;
import com.viewnine.safeapp.service.LockScreenService;
import com.viewnine.safeapp.ulti.DateHelper;
import com.viewnine.safeapp.ulti.ViewUlti;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class LockScreenAppActivity extends ParentActivity implements View.OnClickListener{

    private static final int HOME_TYPE = 0;
    private static final int VIDEO_TYPE = 1;
    private static final int PATTERN_TYPE = 2;
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


    @Override
    public void onAttachedToWindow() {
        // TODO Auto-generated method stub
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG|WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onAttachedToWindow();
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        setupViews();

    }

    private void setupViews(){
        addChidlView(R.layout.lockscreen_view);
        showHideHeader(false);
//        setContentView(R.layout.lockscreen_view);

        initLockScreen();
        initGlowPad();
        initDateTime();
        initLockPattern();
    }


    private void initLockPattern() {
        rlLockPattern = (RelativeLayout) findViewById(R.id.relativelayout_pattern);
        lockPatternView = (LockPatternViewEx) findViewById(R.id.lockpatternview_pattern);
        txtWrongPattern = (TextView) findViewById(R.id.textview_wrong_pattern);
        btnCancelPattern = (Button) findViewById(R.id.button_cancel_pattern);
        btnCancelPattern.setOnClickListener(this);
        patternStringSetting = SharePreferenceManager.getInstance().getUnlockPattern();

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
            SwitchViewManager.getInstance().gotoHistoryScreen(this);
        }
    }

    private void initGlowPad() {
        glowPadView = (GlowPadView) findViewById(R.id.glow_pad_view);
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
                        finish();
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
        lblTime = (TextView) findViewById(R.id.time);
        lblTimeAMPM = (TextView) findViewById(R.id.time2);
        lblDate = (TextView) findViewById(R.id.date);

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
//        Toast.makeText(LockScreenAppActivity.this, "Start record video", Toast.LENGTH_SHORT).show();
        handleRecordingInBackgroundThread();
//        SwitchViewManager.getInstance().sendAppToBackground(this);
        finish();
    }


    private void handlePatternSelected() {
        if(SharePreferenceManager.getInstance().getUnlockPattern().isEmpty()){
            SwitchViewManager.getInstance().gotoHistoryScreen(this);
        }else {
            txtWrongPattern.setVisibility(View.GONE);
            rlLockPattern.setVisibility(View.VISIBLE);
            glowPadView.setVisibility(View.GONE);
        }
    }



}