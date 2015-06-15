package com.viewnine.nuttysnap.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.manager.SwitchViewManager;


public class Intro5View extends RelativeLayout{

	LayoutInflater layoutInflater;
	private Button imgClose;
	public Intro5View(Context context) {
		super(context);
		layoutInflater = LayoutInflater.from(getContext());
		View view = layoutInflater.inflate(R.layout.intro5_view, this, true);

		imgClose = (Button) view.findViewById(R.id.button_ok_i_got_it);
		imgClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SwitchViewManager.getInstance().gotoHistoryScreen(getContext());
			}
		});
	}

	public Intro5View(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}
