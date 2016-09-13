package com.luofangyun.shangchao.base;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luofangyun.shangchao.R;

/**
 * 4个标签页的基类
 * 共性: 子类都有标题栏, 可以直接在父类中加载布局页面
 */

public class BasePager implements View.OnClickListener{
    public Activity    mActivity;
    public TextView    tvTitle;
    public FrameLayout flContainer;//空的帧布局, 由子类动态填充布局
    public View        mRootView;//当前页面的根布局
    public TextView   right;
    public LinearLayout titleBack;
    public ImageView logo, moreRight;
    public BasePager(Activity activity) {
        mActivity = activity;
        //在页面对象创建时就初始化了布局
        mRootView = initViews();
    }

    //初始化布局
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.base_pager, null);
        tvTitle = (TextView) view.findViewById(R.id.title_tv);                          //标题
        right = (TextView) view.findViewById(R.id.right);
        titleBack = (LinearLayout) view.findViewById(R.id.title_ll_back);
        flContainer = (FrameLayout) view.findViewById(R.id.fl_container);
        logo = (ImageView) view.findViewById(R.id.logo);
        moreRight = (ImageView) view.findViewById(R.id.more_right);
        titleBack.setOnClickListener(this);
        moreRight.setOnClickListener(this);
        right.setOnClickListener(this);
        return view;
    }

    //初始化数据，具体由子类实现
    public void initData() {
    }

    @Override
    public void onClick(View v) {

    }
}
