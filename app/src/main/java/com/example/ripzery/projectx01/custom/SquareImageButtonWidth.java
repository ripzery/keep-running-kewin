package com.example.ripzery.projectx01.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by oakraw90 on 11/2/2014.
 */
public class SquareImageButtonWidth extends ImageButton {
    public SquareImageButtonWidth(Context context) {
        super(context);
    }

    public SquareImageButtonWidth(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageButtonWidth(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
}
