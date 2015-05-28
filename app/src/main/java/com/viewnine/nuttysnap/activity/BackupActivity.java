package com.viewnine.nuttysnap.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.manager.SharePreferenceManager;
import com.viewnine.nuttysnap.ulti.AlertHelper;
import com.viewnine.nuttysnap.ulti.Constants;
import com.viewnine.nuttysnap.ulti.DialogUlti;
import com.viewnine.nuttysnap.ulti.KeyboardHelper;
import com.viewnine.nuttysnap.ulti.Ulti;
import com.viewnine.nuttysnap.ulti.ValidationHelper;

import java.util.Map;

/**
 * Created by user on 4/25/15.
 */
public class BackupActivity extends ParentActivity implements View.OnClickListener{


    private Button btnEditPrimaryEmail;
    private Button btnEditSecondaryEmail;
    private Button btnEditDuration;
    private EditText txtPrimaryEmail;
    private EditText txtSecondaryEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViews();
        initData();
    }

    private void setupViews() {
        addChidlView(R.layout.back_up_view);
        addBackButton();
        addTitle(getString(R.string.back_up));
        btnEditPrimaryEmail = (Button) findViewById(R.id.button_edit_primary_email);
        btnEditPrimaryEmail.setOnClickListener(this);
        btnEditSecondaryEmail = (Button) findViewById(R.id.button_edit_secondary_email);
        btnEditSecondaryEmail.setOnClickListener(this);
        btnEditDuration = (Button) findViewById(R.id.button_edit_duration_time);
        btnEditDuration.setOnClickListener(this);
        txtPrimaryEmail = (EditText) findViewById(R.id.primary_email);
        txtSecondaryEmail = (EditText) findViewById(R.id.secondary_email);

    }

    private void initData(){
        txtPrimaryEmail.setText(SharePreferenceManager.getInstance().getPrimaryEmail());
        txtSecondaryEmail.setText(SharePreferenceManager.getInstance().getSecondaryEmail());
        updateCurrentDurationTime();
    }

    private void updateCurrentDurationTime(){
        int currentDurationTimeIndex = SharePreferenceManager.getInstance().getIndexDurationTime();
        Map.Entry entry = Ulti.getEntryOfHashMap(currentDurationTimeIndex, Constants.TIME_INTERVAL_LIST);
        btnEditDuration.setText((String) entry.getKey() + " >");
    }


    @Override
    public void onClick(View v) {
//        super.onClick(v);
        switch (v.getId()){
            case R.id.button_edit_primary_email:
//                showKeyboard(txtPrimaryEmail);
                break;
            case R.id.button_edit_secondary_email:
//                showKeyboard(txtSecondaryEmail);
                break;
            case R.id.button_edit_duration_time:
                openDurationDialog();
                break;
            case R.id.button_back:
                handleBackButton();
                break;
            default:
        }
    }

    private void showKeyboard(EditText editText) {
        KeyboardHelper.showKeyboard(this, editText);
    }

    private void handleBackButton() {
        String primaryEmail = txtPrimaryEmail.getText().toString().trim();
        String secondaryEmail = txtSecondaryEmail.getText().toString().trim();
        boolean isPrimaryEmailValid = ValidationHelper.getInstance().isEmailValid(primaryEmail);
        if(isPrimaryEmailValid){

            if(secondaryEmail.isEmpty() || (!secondaryEmail.isEmpty() && ValidationHelper.getInstance().isEmailValid(secondaryEmail))){
                SharePreferenceManager.getInstance().setPrimaryEmail(primaryEmail);
                SharePreferenceManager.getInstance().setSecondaryEmail(secondaryEmail);
                super.onBackPressed();
            }else {
                AlertHelper.getInstance().showMessageAlert(this, getString(R.string.invalid_email));
            }


        }else {
            AlertHelper.getInstance().showMessageAlert(this, getString(R.string.invalid_email));
        }


    }

    private void openDurationDialog() {
        DialogUlti.getInstance().showDurationVideoTimeDialog(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCurrentDurationTime();
            }
        });
    }
}
