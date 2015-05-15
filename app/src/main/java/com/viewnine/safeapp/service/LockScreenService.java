package com.viewnine.safeapp.service;


import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.viewnine.safeapp.receiver.lockScreenReceiver;
import com.viewnine.safeapp.ulti.Constants;
import com.viewnine.safeapp.ulti.LogUtils;

public class LockScreenService extends Service {
    BroadcastReceiver mReceiver;
    KeyguardManager.KeyguardLock k1;

    // Intent myIntent;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onCreate() {


        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        disableKeyguard(true);

        LogUtils.logD(LockScreenService.class.getName(), "Start lockscreen service");


//    ((KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE)).newKeyguardLock("IN").disableKeyguard();

     /*try{
     StateListener phoneStateListener = new StateListener();
     TelephonyManager telephonyManager =(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
     telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
     }catch(Exception e){
    	 System.out.println(e);
     }*/

    /* myIntent = new Intent(LockScreenService.this,LockScreenAppActivity.class);
     myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     Bundle myKillerBundle = new Bundle();
     myKillerBundle.putInt("kill",1);
     myIntent.putExtras(myKillerBundle);*/

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        mReceiver = new lockScreenReceiver();
        registerReceiver(mReceiver, filter);


        super.onCreate();


    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub

        super.onStart(intent, startId);
    }

/*class StateListener extends PhoneStateListener{
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        super.onCallStateChanged(state, incomingNumber);
        switch(state){
            case TelephonyManager.CALL_STATE_RINGING:
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                System.out.println("call Activity off hook");
            	getApplication().startActivity(myIntent);



                break;
            case TelephonyManager.CALL_STATE_IDLE:
                break;
        }
    }
};*/


    @Override
    public void onDestroy() {
        LogUtils.logD(LockScreenService.class.getName(), "Stop lockscreen service");
        unregisterReceiver(mReceiver);

        disableKeyguard(false);
        super.onDestroy();
    }

    private void disableKeyguard(boolean isDisable) {
        if (Constants.DISABLE_SYSTEM_LOCK_SCREEN) {
            if (isDisable) {

                KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                k1 = km.newKeyguardLock(KEYGUARD_SERVICE);
                k1.disableKeyguard();


            } else if (k1 != null) {
                k1.reenableKeyguard();
                k1 = null;
            }
        }


    }
}
