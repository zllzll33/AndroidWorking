package com.luofangyun.shangchao.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.AttTimeBean;
import com.luofangyun.shangchao.domain.ClassBean;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.HourMinuteDialogUtil;
import com.luofangyun.shangchao.utils.MD5Encoder;
import com.luofangyun.shangchao.utils.Sign;
import com.luofangyun.shangchao.utils.UiUtils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Item跳转来的班段管理
 */
public class ClassDetailActivity extends BaseActivity {
    private View     view;
    private EditText newAddName;
    private EditText newAddStarttime, newAddEndtime;
    private LinearLayout newAddLlInfo, newAddLlPeop, starttimeLl, endtimeLl;
    private Map<String, String> map = new HashMap<>();
    private ClassBean   classBean;
    private AttTimeBean attTimeBean;
    private String timename;
    private String timesb;
    private String timexb;
    private String timecode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_new_add);
        initview();
        initData();
    }
    private void initview() {
        timename = getIntent().getStringExtra("timename");
        timesb = getIntent().getStringExtra("timesb");
        timexb = getIntent().getStringExtra("timexb");
        timecode = getIntent().getStringExtra("timecode");
        newAddName = (EditText) view.findViewById(R.id.new_add_name);
        newAddStarttime = (EditText) view.findViewById(R.id.new_add_starttime);
        newAddEndtime = (EditText) view.findViewById(R.id.new_add_endtime);
        newAddLlInfo = (LinearLayout) view.findViewById(R.id.new_add_ll_info);
        newAddLlPeop = (LinearLayout) view.findViewById(R.id.new_add_ll_peop);
        starttimeLl = (LinearLayout) view.findViewById(R.id.starttime_ll);
        endtimeLl = (LinearLayout) view.findViewById(R.id.endtime_ll);
    }
    private void initData() {
        newAddLlInfo.setOnClickListener(this);
        newAddLlPeop.setOnClickListener(this);
        newAddName.setText(timename);
        newAddStarttime.setText(timesb);
        newAddEndtime.setText(timexb);
        right.setVisibility(View.VISIBLE);
        titleTv.setText("班段管理");
        right.setText("保存");
        flAddress.addView(view);
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.right:
                getServerData();
                break;
            case R.id.new_add_starttime:
                HourMinuteDialogUtil hourMinute = new HourMinuteDialogUtil(this, "");
                hourMinute.dateTimePicKDialog(newAddStarttime);
                break;
            case R.id.new_add_endtime:
                HourMinuteDialogUtil hourMinute1 = new HourMinuteDialogUtil(this, "");
                hourMinute1.dateTimePicKDialog(newAddEndtime);
                break;
            case R.id.new_add_ll_info:
                Intent intent = new Intent(new Intent(this, LableInfoActivity.class));
                intent.putExtra("timecode", timecode);
                startActivity(intent);
                break;
            case R.id.new_add_ll_peop:
                Intent intent1 = new Intent(new Intent(this, AttPeopActivity.class));
                intent1.putExtra("timecode", timecode);
                startActivity(intent1);
                break;
        }
    }

    private void getServerData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "att_time_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("timecode", "0");
            map.put("timename", newAddName.getText().toString().trim());
            map.put("timesb", newAddStarttime.getText().toString().trim());
            map.put("timexb", newAddEndtime.getText().toString().trim());
            map.put("labelname", newAddName.getText().toString().trim());
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request.add(map);
            CallServer.getRequestInstance().add(this, 0, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 0:
                    System.out.println("添加的数据为=" + response.get());
                    attTimeBean = new Gson().fromJson(response.get(), AttTimeBean.class);
                    UiUtils.ToastUtils(attTimeBean.summary);
                    Log.e("增加的数据为:", attTimeBean.result.timecode);
                    break;
                case 1:
                    break;
                case 2:
                    break;
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };
}
