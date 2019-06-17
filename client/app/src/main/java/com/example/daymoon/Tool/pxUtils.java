package com.example.daymoon.Tool;

import android.content.Context;

public class pxUtils {

    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dpValue * scale;
    }
}
