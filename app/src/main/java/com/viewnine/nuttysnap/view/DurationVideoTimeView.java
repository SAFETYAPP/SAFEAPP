package com.viewnine.nuttysnap.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.viewnine.nuttysnap.R;
import com.viewnine.nuttysnap.manager.SharePreferenceManager;
import com.viewnine.nuttysnap.ulti.Constants;
import com.viewnine.nuttysnap.ulti.LogUtils;
import com.viewnine.nuttysnap.ulti.Ulti;

import java.util.Map;

import spinnerwheel.adapters.AbstractWheel;
import spinnerwheel.adapters.AbstractWheelTextAdapter;
import spinnerwheel.adapters.OnWheelChangedListener;
/**
 * Created by user on 4/26/15.
 */
public class DurationVideoTimeView extends RelativeLayout {
    private static final String TAG = DurationVideoTimeView.class.getName();
    // Scrolling flag
    private boolean scrolling = false;
    private int currentIndexDurationTime = 0;


    LayoutInflater inflater;
    OnClickListener okListener;
    public DurationVideoTimeView(Context context, OnClickListener okListener){
        super(context);
        this.okListener = okListener;
        inflater = LayoutInflater.from(context);
        initViews();
    }


    private DurationVideoTimeView(Context context) {
        super(context);
        inflater = LayoutInflater.from(context);
        initViews();
    }

    private DurationVideoTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initViews() {
        inflater.inflate(R.layout.dialog_video_duration, this, true);
        currentIndexDurationTime = SharePreferenceManager.getInstance().getIndexDurationTime();
        final AbstractWheel durationWheelView = (AbstractWheel) findViewById(R.id.video_time);
        durationWheelView.setVisibleItems(Constants.TIME_INTERVAL_LIST.size());
        durationWheelView.setViewAdapter(new DurationAdapter(getContext()));
        durationWheelView.setCurrentItem(currentIndexDurationTime);
        durationWheelView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                LogUtils.logD(TAG, "Old value: " + oldValue + ". New Value: " + newValue);
                currentIndexDurationTime = newValue;
            }
        });

        Button btnOK = (Button) findViewById(R.id.button_ok);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePreferenceManager.getInstance().setIndexDurationTime(currentIndexDurationTime);
                okListener.onClick(v);
            }
        });

    }

    /**
     * Adapter for duration
     */
    private class DurationAdapter extends AbstractWheelTextAdapter {

        /**
         * Constructor
         */
        protected DurationAdapter(Context context) {
            super(context, R.layout.duration_item, NO_RESOURCE);

            setItemTextResource(R.id.country_name);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);

            return view;
        }

        @Override
        public int getItemsCount() {
            int count = Constants.TIME_INTERVAL_LIST.size();
            return count;

        }

        @Override
        protected CharSequence getItemText(int index) {

            Map.Entry entry = Ulti.getEntryOfHashMap(index, Constants.TIME_INTERVAL_LIST);
            String key = (String) entry.getKey();
            return key;

        }
    }

}
