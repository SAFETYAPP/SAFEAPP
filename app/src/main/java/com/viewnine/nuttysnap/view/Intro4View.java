package com.viewnine.nuttysnap.view;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewnine.nuttysnap.R;


public class Intro4View extends RelativeLayout{

	LayoutInflater layoutInflater;
	private ImageView imgClose;
	public Intro4View(Context context) {
		super(context);
		layoutInflater = LayoutInflater.from(getContext());
		View view = layoutInflater.inflate(R.layout.intro4_view, this, true);
		TextView lblIntro = (TextView) view.findViewById(R.id.text_intro);
		lblIntro.setText(Html.fromHtml(getResources().getString(R.string.howto_4)));


	}

	public Intro4View(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}
