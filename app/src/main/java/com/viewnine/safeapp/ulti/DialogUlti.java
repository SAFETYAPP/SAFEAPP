package com.viewnine.safeapp.ulti;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;

import com.viewnine.safeapp.activity.R;
import com.viewnine.safeapp.view.DurationVideoTimeView;

/**
 * Created by user on 4/26/15.
 */
public class DialogUlti {
    private static DialogUlti ourInstance = new DialogUlti();

    public static DialogUlti getInstance() {
        return ourInstance;
    }

    private DialogUlti() {
    }


    public void showDurationVideoTimeDialog(Context context){
        Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
        DurationVideoTimeView durationVideoTimeView = new DurationVideoTimeView(context);
        dialog.setContentView(durationVideoTimeView);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);


        dialog.show();
    }
}
