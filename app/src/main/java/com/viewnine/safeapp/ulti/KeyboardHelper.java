package com.viewnine.safeapp.ulti;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by user on 4/26/15.
 */
public class KeyboardHelper {

    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

            return;
        }
    }

    public static void hideKeyboard(Context context, EditText e) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInputFromInputMethod(activity
                            .getCurrentFocus().getWindowToken(),
                    InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {

            return;
        }
    }

    public static void showKeyboardNew(Context context) {
        InputMethodManager in = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        in.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void showKeyboardTest(Activity activity){
        InputMethodManager in = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static boolean isKeyboardIsShowingUp(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText()) {
            return true;
        } else {
            return false;
        }
    }
    public static void hideKeyboardNew(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
    }
}
