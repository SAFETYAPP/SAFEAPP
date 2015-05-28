package com.viewnine.nuttysnap.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.viewnine.nuttysnap.R;

import com.viewnine.nuttysnap.ulti.Constants;

public class InAppBrowserActivity extends ParentActivity{
	public static final String CONTENT_URL = "content_url";
	public static final String TITLE = "title";
	protected static final String TAG = InAppBrowserActivity.class.getName();

	private Activity mActivity;
	private WebView webView;
	String url = Constants.EMPTY_STRING;
	String title = Constants.EMPTY_STRING;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;
		getBundle();
		setupView();
	}
	
	private void getBundle(){
		Bundle bundle = getIntent().getExtras();
		title = bundle.getString(TITLE);
	}

	private void setupView() {
		addChidlView(R.layout.activity_info);
		addBackButton();

		url = mActivity.getIntent().getStringExtra(CONTENT_URL);
		
		if(title != null && !TextUtils.isEmpty(title)){
			addTitle(title);
		}else {
			addTitle(url);
		}

		
		webView = (WebView) findViewById(R.id.webView);
//		webView.getSettings().setPluginState(PluginState.ON);
		webView.getSettings().setJavaScriptEnabled(true);
//		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
//		webView.setInitialScale(1);
//		webView.getSettings().setLoadWithOverviewMode(true);
//		webView.getSettings().setUseWideViewPort(true);
		
		if(!url.substring(0, 4).equalsIgnoreCase("http")){
			url = "http://" + url;
		}

		webView.loadUrl(url);
		
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}
			
		});
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		com.facebook.Settings.publishInstallAsync(mActivity,mActivity.getString(R.string.app_id));
	}
}
