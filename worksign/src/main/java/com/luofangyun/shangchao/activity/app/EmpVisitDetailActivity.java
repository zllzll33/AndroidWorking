package com.luofangyun.shangchao.activity.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.MainActivity;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户拜访详情和填写内容
 */
public class EmpVisitDetailActivity extends BaseActivity{
    private EditText timeTv, name, address, cause;
    private View view;
    private Map<String, String> map = new HashMap<>();
    private String addrStr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.emp_visit);
        initView();
        initData();
    }
    private void initView() {
        address = (EditText) view.findViewById(R.id.address);
        timeTv = (EditText) view.findViewById(R.id.time);
        name = (EditText) view.findViewById(R.id.name);
        address = (EditText) view.findViewById(R.id.address);
        cause = (EditText) view.findViewById(R.id.cause);
    }

    private void initData() {
        if (MainActivity.addrStr != null) {
            System.out.println("获取的地址=" + MainActivity.addrStr);
            addrStr = MainActivity.addrStr;
        }
        titleTv.setText("客户拜访");
        //页面跳转过来改变页面
        changeData();
        flAddress.addView(view);

    }

    private void changeData() {
        String action = getIntent().getAction();
        if (action.equals("EmpVisitDetailActivity")) {
            address.setText(TextUtils.isEmpty(getIntent().getStringExtra("visitaddress")) ? "" :
                    getIntent().getStringExtra("visitaddress"));
            timeTv.setText(TextUtils.isEmpty(getIntent().getStringExtra("visittime")) ? "" :
                    getIntent().getStringExtra("visittime"));
            name.setText(TextUtils.isEmpty(getIntent().getStringExtra("custom")) ? "" : getIntent
                    ().getStringExtra("custom"));
            cause.setText(TextUtils.isEmpty(getIntent().getStringExtra("visitsummary")) ? "" :
                    getIntent().getStringExtra("visitsummary"));
        } else if (action.equals("EmpVisitWriteActivity")) {
            mLocationClient.start();
            name.setFocusable(true);
            name.setFocusableInTouchMode(true);
            cause.setFocusable(true);
            cause.setFocusableInTouchMode(true);
            address.setText(addrStr);
            SimpleDateFormat sDateFormat    =   new    SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String    date    =    sDateFormat.format(new java.util.Date());
            timeTv.setText(date);
            right.setVisibility(View.VISIBLE);
            right.setText("提交");
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(name.getText().toString().trim())) {
                        UiUtils.ToastUtils("姓名不能为空");
                    } else if (TextUtils.isEmpty(timeTv.getText().toString().trim())) {
                        UiUtils.ToastUtils("时间不能为空");
                    } else if (TextUtils.isEmpty(address.getText().toString().trim())) {
                        UiUtils.ToastUtils("地点不能为空");
                    } else {
                        getServerEmpEmpVisit();

                    }
                }
            });
        }
    }


    /**
     * 拜访提交
     */
    private void getServerEmpEmpVisit() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "visit_record_add.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("visitcode", "0");
            map.put("visittime", timeTv.getText().toString());
            map.put("visitsummary", cause.getText().toString().trim());
            map.put("custom", name.getText().toString().trim());
            map.put("visitx", String.valueOf(longitude));
            map.put("visity", String.valueOf(latitude));
            map.put("visitaddress", addrStr);
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            Log.e("map",UiUtils.Map2JsonStr(map));
            request.add(map);
            CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
//            Log.e("拜访提交",response.get());
            if(new Gson().fromJson(response.get(), ApplyBean.class).status.equals("00000"))
            {
                setResult(1);
                finish();
            }
//            UiUtils.ToastUtils();
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };
}
