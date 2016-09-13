package com.luofangyun.shangchao.base;

import android.app.Activity;
import android.view.View;

/**
 * 消息中心里面的三个基类
 */
public abstract  class BaseMessagePager {

	public Activity MyActivity;
	public View mRootView;
	public BaseMessagePager(Activity activity) {
		MyActivity = activity;
		mRootView = initViews();
	}

	//必须由子类实现
	public abstract View initViews();

	//初始化数据
	public void initData() {

	}
}
