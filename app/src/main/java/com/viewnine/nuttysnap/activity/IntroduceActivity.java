package com.viewnine.nuttysnap.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.viewnine.nuttysnap.Adapter.ViewPagerIntroduceAdapter;
import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.view.Intro1View;
import com.viewnine.nuttysnap.view.Intro2View;
import com.viewnine.nuttysnap.view.Intro3View;
import com.viewnine.nuttysnap.view.Intro4View;
import com.viewnine.nuttysnap.view.Intro5View;

import java.util.ArrayList;

/**
 * Created by user on 6/13/15.
 */
public class IntroduceActivity extends ParentActivity{

    private ViewPager bannerPager;
    private ViewPagerIntroduceAdapter viewPagerRegisterWorkthoughtAdapter;
    private ArrayList<ViewGroup> listWorkthoughtView;
    private ViewGroup lnLayoutIndicator;
    private int INDICATOR_SELECTED = R.drawable.indicator_selected;
    private int INDICATOR_UNSELECTED = R.drawable.indicator_gray;
    private int currentPage = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        setContentView(R.layout.introduce_view);
        bannerPager = (ViewPager) findViewById(R.id.viewPager);
        lnLayoutIndicator = (LinearLayout) findViewById(R.id.linearlayout_indicator);
        Intro1View intro1View = new Intro1View(this);
        Intro2View intro2View = new Intro2View(this);
        Intro3View intro3View = new Intro3View(this);
        Intro4View intro4View = new Intro4View(this);
        Intro5View intro5View = new Intro5View(this);
        listWorkthoughtView = new ArrayList<ViewGroup>();
        listWorkthoughtView.add(intro1View);
        listWorkthoughtView.add(intro2View);
        listWorkthoughtView.add(intro3View);
        listWorkthoughtView.add(intro4View);
        listWorkthoughtView.add(intro5View);
        viewPagerRegisterWorkthoughtAdapter = new ViewPagerIntroduceAdapter(this, listWorkthoughtView);
        bannerPager.setAdapter(viewPagerRegisterWorkthoughtAdapter);
        bannerPager.setOnPageChangeListener(bannerChangeListener);
        setupBannerIndicator();
        setCurrentBannerIndicator(currentPage);


    }

    private ViewPager.OnPageChangeListener bannerChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            setCurrentBannerIndicator(arg0);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }
    };

    private void setupBannerIndicator() {
        for (int i = 0; i < listWorkthoughtView.size(); i++) {
            ImageView imgIndicator = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 0, 10, 0);
            imgIndicator.setLayoutParams(layoutParams);
            imgIndicator.setBackgroundResource(INDICATOR_UNSELECTED);
            lnLayoutIndicator.addView(imgIndicator);
        }
    }

    private void setCurrentBannerIndicator(int currentPosition) {
        currentPage = currentPosition;
        if (currentPosition == 0) {
            lnLayoutIndicator.getChildAt(currentPosition).setBackgroundResource(INDICATOR_SELECTED);
            lnLayoutIndicator.getChildAt(currentPosition + 1).setBackgroundResource(INDICATOR_UNSELECTED);

        } else if (currentPosition > 0 && currentPosition < (lnLayoutIndicator.getChildCount() - 1)) {
            lnLayoutIndicator.getChildAt(currentPosition).setBackgroundResource(INDICATOR_SELECTED);
            lnLayoutIndicator.getChildAt(currentPosition + 1).setBackgroundResource(INDICATOR_UNSELECTED);
            lnLayoutIndicator.getChildAt(currentPosition - 1).setBackgroundResource(INDICATOR_UNSELECTED);

        } else if (currentPosition == (lnLayoutIndicator.getChildCount() - 1)) {
            lnLayoutIndicator.getChildAt(currentPosition).setBackgroundResource(INDICATOR_SELECTED);
            lnLayoutIndicator.getChildAt(currentPosition - 1).setBackgroundResource(INDICATOR_UNSELECTED);
        }
    }
}
