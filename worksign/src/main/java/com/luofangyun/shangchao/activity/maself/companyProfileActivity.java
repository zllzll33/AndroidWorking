package com.luofangyun.shangchao.activity.maself;

import android.os.Bundle;
import android.view.View;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.utils.UiUtils;

/**
 * 公司简介
 */
public class companyProfileActivity extends BaseActivity {

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_company_profile);
        initView();
        initData();
        flAddress.addView(view);
    }
    private void initView() {

    }
    private void initData() {
         titleTv.setText("公司信息");
    }
}
