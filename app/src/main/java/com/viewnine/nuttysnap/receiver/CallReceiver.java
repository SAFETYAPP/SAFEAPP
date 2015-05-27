package com.viewnine.nuttysnap.receiver;

import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import com.viewnine.nuttysnap.activity.LockScreenAppActivity;
import com.viewnine.nuttysnap.application.SafeAppApplication;
import com.viewnine.nuttysnap.manager.SharePreferenceManager;
import com.viewnine.nuttysnap.ulti.LogUtils;
import com.viewnine.nuttysnap.ulti.Ulti;

import java.util.Date;

/**
 * Created by user on 5/19/15.
 */
public class CallReceiver extends PhonecallReceiver {

    private final String TAG = CallReceiver.class.getName();

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        LogUtils.logD(TAG, "IncommingCall Started");
//        Intent intent11 = new Intent(ctx, TestActivity.class);
//        intent11.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        ctx.startActivity(intent11);

    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        LogUtils.logD(TAG, "onOutgoingCallStarted");
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        LogUtils.logD(TAG, "onIncomingCallEnded");
        gotoLockScreen(ctx);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        LogUtils.logD(TAG, "onOutgoingCallEnded");
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        LogUtils.logD(TAG, "onMissedCall");
        gotoLockScreen(ctx);

    }

    private void gotoLockScreen(Context ctx){
        if(SharePreferenceManager.getInstance().getIsExitAppFromLockSCreenActivity()){
            boolean isAppInBackground = Ulti.isAppRunningBackground(ctx);
            SafeAppApplication.finishAllPreviousActivity();
            Intent intent11 = new Intent(ctx, LockScreenAppActivity.class);
            intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent11.addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);

            ctx.startActivity(intent11);
        }

        SharePreferenceManager.getInstance().setIsExitAppFromLockScreenActivity(false);
    }

}