package com.viewnine.nuttysnap.ulti;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;



public abstract class BaseAsyncTaskV2 extends AsyncTask<Void, Void, Integer> {

	Context mContext;
	CustomLoadingDialogHelper mDialogHelper;
	private String TAG = "BaseAsyncTask";
	private boolean isNeedToShowLoadingDialog = true;
	
	public void needToShowDialog(boolean isNeed){
		isNeedToShowLoadingDialog = isNeed;
	}
	
	public BaseAsyncTaskV2(Context context){
		this.mContext = context;
		isNeedToShowLoadingDialog = true;
	}

	@Override
	protected void onPreExecute() {
		if(isNeedToShowLoadingDialog){
			if (mDialogHelper == null){
				mDialogHelper = new CustomLoadingDialogHelper(mContext);
			}
			mDialogHelper.SetCancelListerner(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					BaseAsyncTaskV2.this.cancel(true);
				}
			});
			mDialogHelper.showLoadingDialog();
		}
		
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		if(isNeedToShowLoadingDialog){
			dismissProgressDialog();
		}
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
		LogUtils.logI(TAG , "Cancelled current asynctask");
	}
	
	protected void dismissProgressDialog() {
		if(!((Activity)mContext).isFinishing() && mDialogHelper != null && mDialogHelper.isShowing()){
			mDialogHelper.dismiss();
		}
	}

}
