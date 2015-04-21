package com.viewnine.safeapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.viewnine.safeapp.manager.SharePreferenceManager;
import com.viewnine.safeapp.manager.SwitchViewManager;
import com.viewnine.safeapp.ulti.AlertHelper;
import com.viewnine.safeapp.ulti.ValidationHelper;
import com.viewnine.safeapp.ulti.ViewUlti;

/**
 * Created by user on 4/18/15.
 */
public class SetupActivity extends ParentActivity {


    private TextView txtPrimaryEmail;
    private TextView txtSecondaryEmail;
    private Button btnNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        addChidlView(R.layout.setupscreen_view);
        addTitle(getString(R.string.set_up));

        txtPrimaryEmail = (TextView) findViewById(R.id.primary_email);
        txtSecondaryEmail = (TextView) findViewById(R.id.secondary_email);
        btnNext = (Button) findViewById(R.id.next);
        txtPrimaryEmail.clearFocus();
        txtSecondaryEmail.clearFocus();
        btnNext.setOnClickListener(this);

        ViewUlti.preventKeyboardShowingAutomatically(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.next:
                handleClickNextButton();
                break;
        }
    }

    private void handleClickNextButton() {
        String primaryEmail = txtPrimaryEmail.getText().toString().trim();
        String secondaryEmail = txtSecondaryEmail.getText().toString().trim();
        boolean isPrimaryEmailValid = ValidationHelper.getInstance().isEmailValid(primaryEmail);
        if(isPrimaryEmailValid){

            if(secondaryEmail.isEmpty() || (!secondaryEmail.isEmpty() && ValidationHelper.getInstance().isEmailValid(secondaryEmail))){
                SharePreferenceManager.getInstance().setPrimaryEmail(primaryEmail);
                SharePreferenceManager.getInstance().setSecondaryEmail(secondaryEmail);
                SwitchViewManager.getInstance().gotoLockScreen(this);
            }else {
                AlertHelper.getInstance().showMessageAlert(this, getString(R.string.invalid_email));
            }


        }else {
            AlertHelper.getInstance().showMessageAlert(this, getString(R.string.invalid_email));
        }
    }


}
