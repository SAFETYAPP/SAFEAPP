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
import com.viewnine.nuttysnap.manager.SwitchViewManager;


public class Intro3View extends RelativeLayout{

	LayoutInflater layoutInflater;
	private ImageView imgClose;
	public Intro3View(Context context) {
		super(context);
		layoutInflater = LayoutInflater.from(getContext());
		View view = layoutInflater.inflate(R.layout.intro3_view, this, true);
		TextView lblIntro = (TextView) view.findViewById(R.id.text_intro);
		lblIntro.setText(Html.fromHtml(getResources().getString(R.string.howto_3)));

		imgClose = (ImageView) view.findViewById(R.id.close);
		imgClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SwitchViewManager.getInstance().gotoHistoryScreen(getContext());
			}
		});
	}

	public Intro3View(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}
