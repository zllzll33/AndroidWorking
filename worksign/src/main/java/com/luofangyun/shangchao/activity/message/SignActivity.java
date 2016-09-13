package com.luofangyun.shangchao.activity.message;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.utils.UiUtils;

/**
 * 设置打卡模式
 */
public class SignActivity extends BaseActivity {
    private TextView       signConfirm;
    private ImageView      signRight1;
    private ImageView      signRight2;
    private ImageView      signRight3;
    private RelativeLayout roundSignNfc;
    private RelativeLayout roundBluetooth;
    private RelativeLayout roundGps;
    private View           view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_sign);
        //初始化控件
        initView();
        //初始化数据
        initData();
    }

    private void initView() {
        signConfirm = (TextView) view.findViewById(R.id.sign_confirm);
        signRight1 = (ImageView) view.findViewById(R.id.sign_right1);
        signRight2 = (ImageView) view.findViewById(R.id.sign_right2);
        signRight3 = (ImageView) view.findViewById(R.id.sign_right3);

        roundSignNfc = (RelativeLayout) view.findViewById(R.id.round_nfc);
        roundBluetooth = (RelativeLayout) view.findViewById(R.id.round_bluetooth);
        roundGps = (RelativeLayout) view.findViewById(R.id.round_gps);
        signConfirm.setOnClickListener(this);
        roundSignNfc.setOnClickListener(this);
        roundBluetooth.setOnClickListener(this);
        roundGps.setOnClickListener(this);
    }

    private void initData() {
        titleTv.setText("打卡");
        flAddress.addView(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_ll_back:
                finish();
                break;
            case R.id.sign_confirm:
                startActivity(new Intent(this, ConfirmActivity.class));
                break;
            case R.id.round_nfc:
                signRight1.setVisibility(View.VISIBLE);
                signRight2.setVisibility(View.GONE);
                signRight3.setVisibility(View.GONE);
                break;
            case R.id.round_bluetooth:
                signRight1.setVisibility(View.GONE);
                signRight2.setVisibility(View.VISIBLE);
                signRight3.setVisibility(View.GONE);
                break;
            case R.id.round_gps:
                signRight1.setVisibility(View.GONE);
                signRight2.setVisibility(View.GONE);
                signRight3.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
