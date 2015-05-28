package com.viewnine.nuttysnap.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.viewnine.nuttysnap.R;

import com.viewnine.nuttysnap.manager.SharePreferenceManager;

/**
 * Created by user on 4/25/15.
 */
public class NotificationsActivity extends ParentActivity implements View.OnClickListener{

    private Button btnNotify;
    private TextView lblNotifiyMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViews();
    }

    private void setupViews() {
        addChidlView(R.layout.notifications_view);
        addBackButton();
        addTitle(getString(R.string.notifications));
        btnNotify = (Button) findViewById(R.id.button_notification);
        btnNotify.setOnClickListener(this);
        lblNotifiyMe = (TextView) findViewById(R.id.textview_notify_me);
        lblNotifiyMe.setOnClickListener(this);

        handleNotifyStatus(SharePreferenceManager.getInstance().isEnableNotificationForEachBackup());
    }

    private void handleNotifyStatus(boolean status){
        if(status){
            btnNotify.setBackgroundResource(R.drawable.orange_selected);
        }else {
            btnNotify.setBackgroundResource(R.drawable.orange_normal);
        }
        SharePreferenceManager.getInstance().setEnableNotificationForEachBackup(status);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.textview_notify_me:
            case R.id.button_notification:
                handleNotifyStatus(!SharePreferenceManager.getInstance().isEnableNotificationForEachBackup());
                break;


            default:
        }
    }
}
