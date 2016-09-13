package com.luofangyun.shangchao.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义ViewPager，为了拦截ViewPager左右滑动事件
 */

public class NoScrollViewPager extends ViewPager {

    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 事件拦截
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;     //此处返回false表示ViewPager不拦截子控件
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;     //重写父类onTouchEvent, 此处什么都不做, 从而达到禁用事件的目的
    }
}
