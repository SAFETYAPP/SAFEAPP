package com.viewnine.nuttysnap.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.lockPattern.LockPatternViewEx;
import com.viewnine.nuttysnap.manager.SharePreferenceManager;
import com.viewnine.nuttysnap.ulti.Constants;

import java.util.List;

/**
 * Created by user on 4/25/15.
 */
public class ScreenUnlockActivity extends ParentActivity implements View.OnClickListener{

    private Button btnScreenUnlock;
    private Button btnNotification;
    private Button btnSecurity;
    private Button btnContactUs;
    private Button btnPrimacyPolicy;
    private Button btnTermAndCondition;
    private LockPatternViewEx lockPatternView;
    private TextView txtWrongPattern;
    private int default_pattern_color;
    private String firstPattern = Constants.EMPTY_STRING;
    private String patternStringSaved;
    private boolean isNeedToShowUnlockToChangeLockPatternView = false;
    private boolean enableLockScreenChange = false;
    private RelativeLayout rlEnablePattern;
    private Button btnEnablePattern;
    private RelativeLayout rlPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundleData();
        setupViews();
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        enableLockScreenChange = bundle.getBoolean(Constants.ENABLE_LOCK_SCREEN, false);
    }

    private void setupViews() {
        addChidlView(R.layout.screen_unlock_view);
        addBackButton();
        addTitle(getString(R.string.screen_unlock));


        rlEnablePattern = (RelativeLayout) findViewById(R.id.relativelayout_enable_pattern);
        btnEnablePattern = (Button) findViewById(R.id.button_enable_pattern);
        rlEnablePattern.setOnClickListener(this);
        btnEnablePattern.setOnClickListener(this);

        initLockPattern();

    }

    private void initLockPattern() {
        rlPattern = (RelativeLayout) findViewById(R.id.relativelayout_pattern);
        default_pattern_color = getResources().getColor(R.color.Gray);
        lockPatternView = (LockPatternViewEx) findViewById(R.id.lockpatternview_pattern);
        lockPatternView.setColor(default_pattern_color, default_pattern_color, default_pattern_color);
        txtWrongPattern = (TextView) findViewById(R.id.textview_wrong_pattern);
        txtWrongPattern.setText(getString(R.string.choose_a_pattern));
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
                handleSetupPattern(pattern);
            }
        });


        checkToShowUnlockToChangeLockPattern();
    }

    private void checkToShowUnlockToChangeLockPattern(){
        patternStringSaved = SharePreferenceManager.getInstance().getUnlockPattern();
        if(patternStringSaved.isEmpty()){
            if(!SharePreferenceManager.getInstance().isEnablePattern()){
                rlEnablePattern.setVisibility(View.GONE);
            }
            txtWrongPattern.setText(getString(R.string.choose_a_pattern));
            isNeedToShowUnlockToChangeLockPatternView = false;

        }else {
            if(enableLockScreenChange){
                txtWrongPattern.setText(getString(R.string.unlock_to_enable_lock_screen));
            }else {
//                enableDisablePattern(SharePreferenceManager.getInstance().isEnablePattern());
                rlEnablePattern.setVisibility(View.GONE);
                txtWrongPattern.setText(getString(R.string.unlock_to_change_lock_pattern));
            }

            isNeedToShowUnlockToChangeLockPatternView = true;
        }

        if(enableLockScreenChange){
            rlEnablePattern.setVisibility(View.GONE);
        }
    }

    private void handleSetupPattern(List<LockPatternViewEx.Cell> pattern){
        String currentPattern = pattern.toString();
        if(!isNeedToShowUnlockToChangeLockPatternView){                                              // Do not show "Unlock to change lock pattern"

            if(firstPattern.isEmpty() && pattern.size() >= Constants.MIN_NODE_OF_PATTER){            //Set first pattern

                firstPattern = currentPattern;
                txtWrongPattern.setText(getString(R.string.confirm_pattern));
                lockPatternView.clearPattern();

            }else if(!firstPattern.isEmpty() && firstPattern.equalsIgnoreCase(currentPattern)){      //Confirm pattern

                SharePreferenceManager.getInstance().setUnlockPattern(firstPattern);
                Toast.makeText(this, getString(R.string.successful), Toast.LENGTH_LONG).show();
                finish();

            }else if(!firstPattern.isEmpty() && !firstPattern.equalsIgnoreCase(currentPattern)){     //Wrong pattern

                txtWrongPattern.setText(getString(R.string.wrong_pattern));
                lockPatternView.clearPattern();

            }
        }else {                                                                                      // Show "Unlock to change lock pattern
            lockPatternView.clearPattern();
            if(patternStringSaved.equalsIgnoreCase(currentPattern)){

                if(enableLockScreenChange){
                    setResult(RESULT_OK);
                    finish();
                }else {
                    isNeedToShowUnlockToChangeLockPatternView = false;
                    rlEnablePattern.setVisibility(View.VISIBLE);
                    txtWrongPattern.setText(getString(R.string.choose_a_pattern));
                }

            }else {
                isNeedToShowUnlockToChangeLockPatternView = true;
                txtWrongPattern.setText(getString(R.string.wrong_pattern));
            }

        }



    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.button_screen_unlock:

                break;
            case R.id.relativelayout_enable_pattern:
            case R.id.button_enable_pattern:
                enableDisablePattern(!SharePreferenceManager.getInstance().isEnablePattern());
                break;

            default:
        }
    }

    private void enableDisablePattern(boolean status){
        if(status){
            btnEnablePattern.setBackgroundResource(R.drawable.orange_selected);
            rlPattern.setVisibility(View.VISIBLE);
        }else {
            btnEnablePattern.setBackgroundResource(R.drawable.orange_normal);
            rlPattern.setVisibility(View.GONE);
            firstPattern = Constants.EMPTY_STRING;
            SharePreferenceManager.getInstance().setUnlockPattern(Constants.EMPTY_STRING);

            checkToShowUnlockToChangeLockPattern();
        }
        SharePreferenceManager.getInstance().setEnablePattern(status);
    }
}
