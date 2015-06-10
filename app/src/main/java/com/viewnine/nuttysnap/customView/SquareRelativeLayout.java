package com.viewnine.nuttysnap.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by user on 6/10/15.
 */
public class SquareRelativeLayout extends RelativeLayout {

    public SquareRelativeLayout(Context context)
    {
        super(context);
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SquareRelativeLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
