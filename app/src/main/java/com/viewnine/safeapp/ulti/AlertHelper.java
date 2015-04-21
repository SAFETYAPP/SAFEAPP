package com.viewnine.safeapp.ulti;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.viewnine.safeapp.activity.R;

/**
 * Created by user on 4/19/15.
 */
public class AlertHelper {
    private static AlertHelper ourInstance = new AlertHelper();

    public static AlertHelper getInstance() {
        return ourInstance;
    }

    private AlertHelper() {
    }

    /**
     * Dialog without callback
     * Have one ok button only
     * @param context
     * @param message
     */
    public void showMessageAlert(Context context, String message) {
        if (context != null && !((Activity) context).isFinishing()) {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(context);

            alertbox.setMessage(message);

            alertbox.setPositiveButton(R.string.OK,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            dialog.dismiss();
                        }
                    });

            alertbox.show();

        }

    }

    /**
     * Popup with callback
     * Have Cancel and OK button
     * @param context
     * @param message
     * @param cancelable
     * @param listener
     */
    public void showMessageAlert(Context context, String message, boolean cancelable,
                                        DialogInterface.OnClickListener listener) {

        if(context != null && !((Activity)context).isFinishing()){
            AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
            // alertbox.setTitle("Message");
            alertbox.setMessage(message);
            alertbox.setCancelable(cancelable);
            alertbox.setPositiveButton(R.string.OK, listener);

            alertbox.show();

        }
    }
}
