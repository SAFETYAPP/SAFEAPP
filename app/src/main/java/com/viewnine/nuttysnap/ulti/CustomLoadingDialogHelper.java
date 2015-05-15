package com.viewnine.nuttysnap.ulti;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;

import com.viewnine.nuttysnap.activity.R;


public class CustomLoadingDialogHelper {
	private Context mContext;
	private Dialog mDialog;
	private String message;

	private AsyncTask<Void, Void, Integer> mAsyncTask;
	private String TAG = "CustomLoadingDialogHelper";

	public CustomLoadingDialogHelper(Context ctx) {
		mContext = ctx;
		mDialog = new Dialog(mContext, R.style.ThemeDialogCustom);
		mDialog.setContentView(R.layout.view_custom_progress_dialog);

		mDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		message = mContext.getString(R.string.text_loading);

		mDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				cancelDialog();
			}
		});
		
	}

	public void setAsyncTask(AsyncTask asyncTask) {
		this.mAsyncTask = asyncTask;
	}

	private void cancelDialog() {
		if (mAsyncTask != null) {
			mAsyncTask.cancel(true);
			System.out.println("---cancel asytask");
		}
	}


	private void showDialog(String text) {
//		if (ParentActivity.isOnTop((Activity) mContext)) { // ttnlan ANOMO-4980 take note
			if (!((Activity) mContext).isFinishing() && mDialog != null && !mDialog.isShowing())
				((Activity)mContext).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						try {
							mDialog.show();
						} catch (Exception e) {
							LogUtils.logI(TAG, "showDialog() Exception: " + e.toString());
						}
					}
				});
				
//		}
	}
	

	public void show() {
		showDialog(message);
	}

	public void showLoadingDialog() {
		showDialog(message);
	}
	
	private void showDialog_v2(String text) {
		if (mDialog != null && !mDialog.isShowing())
			try {
				mDialog.show();
			} catch (Exception e) {
				LogUtils.logI(TAG , "showDialog_v2() exception: " + e.toString());
			}
	}
	
	public void showLoadingDialog_v2() {
		showDialog_v2(message);
	}

	// public void showLoadingDialogReval() {
	// mDialog.show();
	// }

	// These are getters methods for getting out the current dialog
	// mod by Hung Pham - 22/2/12
	public Dialog getDialog() {
		return mDialog;
	}

	// end mod

	public void showLoadingDialog(String text) {
		if (text.isEmpty())
			text = mContext.getString(R.string.text_loading);
		showDialog(text);
	}

	public void dismiss() {
		hideDialog();
		
	}

	public void hide() {
		hideDialog();
	}

	public Window getWindow() {
		return mDialog.getWindow();
	}

	public void SetDimissListerner(DialogInterface.OnDismissListener dimiss) {
		mDialog.setOnDismissListener(dimiss);
	}
	public void SetCancelListerner (DialogInterface.OnCancelListener cancel){
		mDialog.setOnCancelListener(cancel);
	}

	public final void hideDialog() {
		if (mDialog != null && mDialog.isShowing()) {
//			if (ParentActivity.isOnTop((Activity) mContext)
//					|| mDialog.isShowing()) {
//				Log.v("NCS", "hideDialog");
//				mDialog.dismiss();
//			}
			try {
				mDialog.dismiss();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public boolean isShowing() {
		return mDialog.isShowing();
	}

	public void cancel() {
		mDialog.cancel();
	}
}