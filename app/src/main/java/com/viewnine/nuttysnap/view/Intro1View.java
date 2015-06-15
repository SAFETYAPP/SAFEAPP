package com.viewnine.nuttysnap.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.viewnine.nuttysnap.R;


public class Intro1View extends RelativeLayout{

	LayoutInflater layoutInflater;
	public Intro1View(Context context) {
		super(context);
		layoutInflater = LayoutInflater.from(getContext());
		View view = layoutInflater.inflate(R.layout.intro1_view, this, true);

	}
	
	public Intro1View(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}
