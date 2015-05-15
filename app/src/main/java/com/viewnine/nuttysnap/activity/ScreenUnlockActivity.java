package com.viewnine.nuttysnap.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViews();
    }

    private void setupViews() {
        addChidlView(R.layout.screen_unlock_view);
        addBackButton();
        addTitle(getString(R.string.screen_unlock));

        initLockPattern();

    }

    private void initLockPattern() {
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
    }

    private void handleSetupPattern(List<LockPatternViewEx.Cell> pattern){
        String currentPattern = pattern.toString();
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


    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.button_screen_unlock:

                break;

            default:
        }
    }
}
