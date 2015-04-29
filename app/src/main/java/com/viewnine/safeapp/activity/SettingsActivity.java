package com.viewnine.safeapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.viewnine.safeapp.manager.EmailManager;
import com.viewnine.safeapp.manager.SwitchViewManager;
import com.viewnine.safeapp.ulti.Constants;
import com.viewnine.safeapp.ulti.LogUtils;

import java.util.Arrays;
import java.util.Collection;

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
        FacebookSdk.sdkInitialize(getApplicationContext());
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

                break;
            case R.id.button_term_condition:

                break;
            default:
        }
    }

    CallbackManager callbackManager;
    private void handleClickOnContactus() {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();


        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), loginResult.getAccessToken().toString(), Toast.LENGTH_SHORT).show();
                LogUtils.logE(SettingsActivity.class.getName(), "Facebook token: " + loginResult.getAccessToken().toString());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                LogUtils.logE(SettingsActivity.class.getName(), "Facebook error: " + e.toString());
            }
        });

        Collection<String> permission = Arrays.asList("user_friends");

        LoginManager.getInstance().logInWithPublishPermissions(this, null);
//        LoginManager.getInstance().logInWithReadPermissions(this, null);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
