package com.viewnine.nuttysnap.Adapter;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by user on 6/13/15.
 */
public class ViewPagerIntroduceAdapter extends PagerAdapter {

    Activity mActivity;
    ArrayList<ViewGroup> listViewGroup;

    public ViewPagerIntroduceAdapter(Activity act, ArrayList<ViewGroup> listViewGroup) {
        this.listViewGroup = listViewGroup;
        mActivity = act;
    }

    public int getCount() {
        if(listViewGroup != null && listViewGroup.size() > 0){
            return listViewGroup.size();
        }
        return listViewGroup.size();
    }

    public Object instantiateItem(View collection, final int position) {

        ((ViewPager) collection).addView(listViewGroup.get(position), 0);


        return listViewGroup.get(position);
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
