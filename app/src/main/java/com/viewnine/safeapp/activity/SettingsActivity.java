package com.viewnine.safeapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.viewnine.safeapp.manager.EmailManager;
import com.viewnine.safeapp.manager.SwitchViewManager;
import com.viewnine.safeapp.ulti.Constants;

/**
 * Created by user on 4/25/15.
 */
public class SettingsActivity extends ParentActivity implements View.OnClickListener{

    private Button btnBackUp;
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
        addChidlView(R.layout.settings_view);
        addBackButton();
        addTitle(getString(R.string.settings));
        btnBackUp = (Button) findViewById(R.id.button_backup);
        btnBackUp.setOnClickListener(this);
        btnNotification = (Button) findViewById(R.id.button_notification);
        btnNotification.setOnClickListener(this);
        btnSecurity = (Button) findViewById(R.id.button_security);
        btnSecurity.setOnClickListener(this);
        btnContactUs = (Button) findViewById(R.id.button_contact_us);
        btnContactUs.setOnClickListener(this);
        btnPrimacyPolicy = (Button) findViewById(R.id.button_primacy_policy);
        btnPrimacyPolicy.setOnClickListener(this);
        btnTermAndCondition = (Button) findViewById(R.id.button_term_condition);
        btnTermAndCondition.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.button_backup:
                SwitchViewManager.getInstance().gotoBackupScreen(this);
                break;
            case R.id.button_notification:
                SwitchViewManager.getInstance().gotoNotificationsScreen(this);
                break;
            case R.id.button_security:
                SwitchViewManager.getInstance().gotoSecurityScreen(this);
                break;
            case R.id.button_contact_us:
                EmailManager.getInstance().contactUs(this, Constants.CONTACT_EMAIL, getString(R.string.contact_us_subject));
                break;
            case R.id.button_primacy_policy:
                handleClickOnPrivacyPolicy();
                break;
            case R.id.button_term_condition:
                handleClickOnTermAndCondition();
                break;
            default:
        }
    }

    private void handleClickOnTermAndCondition() {
        SwitchViewManager.getInstance().gotoBrowserScreen(this, getString(R.string.term_condition), getString(R.string.privacy_link));
    }

    private void handleClickOnPrivacyPolicy() {
        SwitchViewManager.getInstance().gotoBrowserScreen(this, getString(R.string.primacy_policy), getString(R.string.privacy_link));
    }



    @Override
    protected void onResume() {
        super.onResume();
//        simpleFacebook = SimpleFacebook.getInstance();
    }



}
