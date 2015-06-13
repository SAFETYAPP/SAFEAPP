package com.viewnine.nuttysnap.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.manager.SwitchViewManager;


public class Intro4View extends RelativeLayout{

	LayoutInflater layoutInflater;
	private ImageView imgClose;
	public Intro4View(Context context) {
		super(context);
		layoutInflater = LayoutInflater.from(getContext());
		View view = layoutInflater.inflate(R.layout.intro4_view, this, true);

		imgClose = (ImageView) view.findViewById(R.id.close);
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SwitchViewManager.getInstance().gotoHistoryScreen(getContext());
			}
		});
	}

	public Intro4View(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}
