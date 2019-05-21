package com.example.daymoon.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.daymoon.R;

import java.util.LinkedList;
import java.util.List;

public class MyPagerAdapter extends PagerAdapter {
    private Context mContext;
    private LinkedList<View> viewList;

    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }
    @Override
    public int getCount(){
        return viewList.size();
    }
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
