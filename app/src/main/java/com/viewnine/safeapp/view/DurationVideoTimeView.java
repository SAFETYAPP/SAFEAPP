package com.viewnine.safeapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.viewnine.safeapp.activity.R;

import spinnerwheel.adapters.AbstractWheel;
import spinnerwheel.adapters.AbstractWheelTextAdapter;

/**
 * Created by user on 4/26/15.
 */
public class DurationVideoTimeView extends RelativeLayout{
    // Scrolling flag
    private boolean scrolling = false;

    private int mActiveCities[] = new int[] {
            1, 1, 1, 1
    };
    private int mActiveCountry;

    LayoutInflater inflater;
    public DurationVideoTimeView(Context context) {
        super(context);
        inflater = LayoutInflater.from(context);
        initViews();
    }

    public DurationVideoTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initViews(){
        inflater.inflate(R.layout.dialog_video_duration, this , true);

        final AbstractWheel country = (AbstractWheel) findViewById(R.id.video_time);
        country.setVisibleItems(3);
        country.setViewAdapter(new CountryAdapter(getContext()));
        country.setCurrentItem(1);
    }

    /**
     * Adapter for countries
     */
    private class CountryAdapter extends AbstractWheelTextAdapter {
        // Countries names
        private String countries[] =
                new String[] {"USA", "Canada", "Ukraine", "France"};
        // Countries flags
        private int flags[] =
                new int[] {R.drawable.ic_item_camera, R.drawable.ic_item_camera, R.drawable.ic_item_camera, R.drawable.ic_item_camera};

        /**
         * Constructor
         */
        protected CountryAdapter(Context context) {
            super(context, R.layout.country_item, NO_RESOURCE);

            setItemTextResource(R.id.country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            ImageView img = (ImageView) view.findViewById(R.id.flag);
            img.setImageResource(flags[index]);
            return view;
        }

        @Override
        public int getItemsCount() {
            return countries.length;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return countries[index];
        }
    }
}
