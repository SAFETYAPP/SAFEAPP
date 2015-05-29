package com.viewnine.nuttysnap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.viewnine.nuttysnap.manager.SharePreferenceManager;
import com.viewnine.nuttysnap.manager.SwitchViewManager;
import com.viewnine.nuttysnap.R;
/**
 * Created by user on 4/25/15.
 */
public class SecurityActivity extends ParentActivity implements View.OnClickListener{

    private Button btnScreenUnlock;
    private Button btnNotification;
    private Button btnSecurity;
    private Button btnContactUs;
    private Button btnPrimacyPolicy;
    private Button btnTermAndCondition;
    private LinearLayout lnSecurity;
    private RelativeLayout rlEnableLockScreen;
    private Button btnEnableLockScreen;
    public static final int ENABLE_LOCK_SCREEN_CODE = 1;
    public static final int GENERAL_CODE = 2;
    private boolean enableLockScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViews();
    }

    private void setupViews() {
        addChidlView(R.layout.security_view);
        addBackButton();
        addTitle(getString(R.string.security));
        btnScreenUnlock = (Button) findViewById(R.id.button_screen_unlock);
        btnScreenUnlock.setOnClickListener(this);

        lnSecurity = (LinearLayout) findViewById(R.id.linearlayout_security_lock);
        rlEnableLockScreen = (RelativeLayout) findViewById(R.id.relativelayout_enable_lock_screen);
        rlEnableLockScreen.setOnClickListener(this);
        btnEnableLockScreen = (Button) findViewById(R.id.button_enable_lock_screen);
        btnEnableLockScreen.setOnClickListener(this);

        handleNotifyStatus(SharePreferenceManager.getInstance().isEnableLockScreen());
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.button_screen_unlock:
                SwitchViewManager.getInstance().gotoScreenUnlockScreen(this, GENERAL_CODE, false);
                break;
            case R.id.relativelayout_enable_lock_screen:
            case R.id.button_enable_lock_screen:
                handleClickOnEnableLockScreen(!SharePreferenceManager.getInstance().isEnableLockScreen());
                break;

            default:
        }
    }

    private void handleClickOnEnableLockScreen(boolean status){
        enableLockScreen = status;

        if(SharePreferenceManager.getInstance().getUnlockPattern().isEmpty()){
            startStopLockScreenService(enableLockScreen);
        }else {
            SwitchViewManager.getInstance().gotoScreenUnlockScreen(this, ENABLE_LOCK_SCREEN_CODE, true);
        }
    }

    private void startStopLockScreenService(boolean status){
        handleNotifyStatus(status);
        if(status){
            startLockScreenService();
        }else {
            stopLockScreenService();
        }
    }

    private void handleNotifyStatus(boolean status){
        if(status){
            btnEnableLockScreen.setBackgroundResource(R.drawable.orange_selected);
            lnSecurity.setVisibility(View.VISIBLE);

        }else {
            btnEnableLockScreen.setBackgroundResource(R.drawable.orange_normal);
            lnSecurity.setVisibility(View.GONE);


        }
        SharePreferenceManager.getInstance().setEnableLockScreen(status);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }

        if(requestCode == ENABLE_LOCK_SCREEN_CODE){
            startStopLockScreenService(enableLockScreen);
        }
    }
}
