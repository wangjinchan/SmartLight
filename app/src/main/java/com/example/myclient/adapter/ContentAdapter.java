package com.example.myclient.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import java.util.List;

public class ContentAdapter extends PagerAdapter {

    private List<View> views;

    public ContentAdapter(List<View> views) {
        this.views = views;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, @NonNull Object arg1) {
        return arg0 == arg1;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView(views.get(position));
    }

}

