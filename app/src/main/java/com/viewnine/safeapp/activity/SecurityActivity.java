package com.viewnine.safeapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.viewnine.safeapp.manager.SwitchViewManager;

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
