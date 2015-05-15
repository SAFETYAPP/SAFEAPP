package com.viewnine.nuttysnap.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.viewnine.nuttysnap.manager.SwitchViewManager;
import com.viewnine.nuttysnap.service.LockScreenService;
import com.viewnine.nuttysnap.ulti.Ulti;

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


        boolean isServiceRunning = Ulti.isServiceRunning(this, LockScreenService.class);
        if(isServiceRunning){
            stopService(new Intent(this, LockScreenService.class));
        }else {
            startService(new Intent(this, LockScreenService.class));
        }

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.button_screen_unlock:
                SwitchViewManager.getInstance().gotoScreenUnlockScreen(this);
                break;

            default:
        }
    }
}
