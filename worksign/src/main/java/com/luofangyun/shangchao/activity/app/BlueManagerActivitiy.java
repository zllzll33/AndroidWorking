package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.MD5Encoder;
import com.luofangyun.shangchao.utils.Sign;
import com.luofangyun.shangchao.utils.UiUtils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.yunliwuli.beacon.kit.data.BluetoothDeviceAndRssi;
import com.yunliwuli.beacon.kit.manager.YlwlManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 蓝牙管理页面
 */

public class BlueManagerActivitiy extends BaseActivity {
    private View     view;
    private EditText blueManagerEt1, blueManagerEt2;
    private ArrayList<BluetoothDeviceAndRssi> deviceList = new
            ArrayList<>();
    private Map<String, String> map = new HashMap<>();
    private String number;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_OK);
        view = UiUtils.inflateView(R.layout.blue_manager);
        initView();
        initData();
    }

    private void initView() {
        number = getIntent().getStringExtra("number");
        blueManagerEt1 = (EditText) view.findViewById(R.id.blue_manager_et1);
        blueManagerEt2 = (EditText) view.findViewById(R.id.blue_manager_et2);
    }

    private void initData() {
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(this);
        right.setText("保存");
        titleTv.setText("蓝牙添加");
        flAddress.addView(view);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_ll_back:
                startActivity(new Intent(this, BlueLabelActivity.class));
                break;
            case R.id.right:
                if (TextUtils.isEmpty(blueManagerEt1.getText().toString().trim())) {
                    UiUtils.ToastUtils("标签名称不能为空");
                } else if (TextUtils.isEmpty(blueManagerEt2.getText().toString().trim())) {
                    UiUtils.ToastUtils("标签备注不能为空");
                }  else {
                    getServerData();
                    startActivity(new Intent(this, BlueLabelActivity.class));
                }
                break;

            default:
                break;
        }
    }
    private void getServerData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "label_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("labelcode", "0");
            map.put("labelname", blueManagerEt1.getText().toString().trim());
            map.put("labelsn", number);
            map.put("labeltype", String.valueOf(1));
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request.add(map);
            CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            System.out.println("蓝牙增加标签=" + result);
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YlwlManager ylwlmanager = YlwlManager.getInstance(this);
        /**
         * 释放资源
         */
        ylwlmanager.unbindService();
        /**
         * 关闭扫描
         */
        ylwlmanager.scanLeDevice(false);
    }
}