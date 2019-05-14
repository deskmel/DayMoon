package com.example.daymoon.Layout;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.daimajia.swipe.SwipeLayout;

public class CustomSwipeLayout extends SwipeLayout {

    float positionX, positionY;

    OnClickItemListener onClickItemListener;

    public CustomSwipeLayout(Context context) {
        super(context);
    }

    public CustomSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int eventMask = MotionEventCompat.getActionMasked(ev);

        switch (eventMask) {
            case MotionEvent.ACTION_DOWN:
                positionY = ev.getRawY();
                positionX = ev.getRawX();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (positionY == ev.getRawY() && positionX == ev.getRawX()) {
                    if(onClickItemListener!=null)
                        onClickItemListener.onClick(CustomSwipeLayout.this);
                }
                break;

        }

        return super.onTouchEvent(ev);
    }

    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
    }

    public interface OnClickItemListener {
        void onClick(View view);
    }

}