package com.viewnine.nuttysnap.ulti;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.view.DurationVideoTimeView;
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


    public void showDurationVideoTimeDialog(Context context, final View.OnClickListener onClickListener){
        final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
        DurationVideoTimeView durationVideoTimeView = new DurationVideoTimeView(context, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
                dialog.dismiss();
            }
        });
        dialog.setContentView(durationVideoTimeView);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);


        dialog.show();
    }

    public void showDeleteVideoConfirmationDialog(Context context, final View.OnClickListener onOKClickListener){
        final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
        dialog.setContentView(R.layout.dialog_delete_vide_confirmation);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        Button ok = (Button) dialog.findViewById(R.id.button_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onOKClickListener.onClick(v);
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void showShareOption(Context context, final View.OnClickListener facebookListener, final View.OnClickListener youtubeListener){
        final Dialog dialog = new Dialog(context, R.style.ThemeDialogCustom);
        dialog.setContentView(R.layout.popup_share_layout);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        Button facebook = (Button) dialog.findViewById(R.id.button_share_facebook);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                facebookListener.onClick(v);
            }
        });

        Button youtube = (Button) dialog.findViewById(R.id.button_share_youtube);
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                youtubeListener.onClick(v);
            }
        });

        RelativeLayout rlPopup = (RelativeLayout) dialog.findViewById(R.id.popup);
        rlPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
