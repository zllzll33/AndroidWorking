package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.MyCaptureActivity;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.utils.UiUtils;

public class NfcActivity extends BaseActivity {

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        view = UiUtils.inflateView(R.layout.activity_nfc);
        initData();
        initView();
    }

    private void initView() {

    }

    private void initData() {
        startActivityForResult(new Intent(this, MyCaptureActivity.class), 10);
        titleTv.setText("二维码扫描");
        flAddress.addView(view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 10) {
            String stringExtra = data.getStringExtra(MyCaptureActivity.EXTRA_RESULT_SUCCESS_STRING);
            System.out.println("扫描的结果为:" + stringExtra);
        }
    }
}
