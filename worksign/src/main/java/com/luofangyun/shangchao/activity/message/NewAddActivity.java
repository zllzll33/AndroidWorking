package com.luofangyun.shangchao.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
 * 班段添加
 */
public class NewAddActivity extends BaseActivity {
    private View     view;
    private EditText newAddName;
    private EditText newAddStarttime, newAddEndtime;
    private LinearLayout newAddLlInfo, newAddLlPeop, starttimeLl, endtimeLl;
    private Map<String, String> map = new HashMap<>();
    private ClassBean   classBean;
    private AttTimeBean attTimeBean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_new_add);
        initview();
        initData();
    }

    private void initview() {
        newAddName = (EditText) view.findViewById(R.id.new_add_name);
        newAddStarttime = (EditText) view.findViewById(R.id.new_add_starttime);
        newAddEndtime = (EditText) view.findViewById(R.id.new_add_endtime);
        newAddLlInfo = (LinearLayout) view.findViewById(R.id.new_add_ll_info);
        newAddLlPeop = (LinearLayout) view.findViewById(R.id.new_add_ll_peop);
        starttimeLl = (LinearLayout) view.findViewById(R.id.starttime_ll);
        endtimeLl = (LinearLayout) view.findViewById(R.id.endtime_ll);
    }

    private void initData() {
        newAddStarttime.setOnClickListener(this);
        newAddEndtime.setOnClickListener(this);
        starttimeLl.setOnClickListener(this);
        endtimeLl.setOnClickListener(this);
        newAddLlInfo.setOnClickListener(this);
        newAddLlPeop.setOnClickListener(this);
        right.setVisibility(View.VISIBLE);
        titleTv.setText("班段添加");
        right.setText("保存");
        flAddress.addView(view);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.right:
                String newAddNameText = newAddName.getText().toString().trim();
                String newAddStarttimeText = newAddStarttime.getText().toString().trim();
                String newAddEndtimeText = newAddEndtime.getText().toString().trim();
                if (TextUtils.isEmpty(newAddNameText)) {
                    UiUtils.ToastUtils("班次名称不能为空");
                } else if (TextUtils.isEmpty(newAddStarttimeText)) {
                    UiUtils.ToastUtils("上班时间不能为空");
                } else if (TextUtils.isEmpty(newAddEndtimeText)) {
                    UiUtils.ToastUtils("下班时间不能为空");
                } else if (UiUtils.timeToMill("HH:mm", newAddStarttimeText) > UiUtils.timeToMill
                        ("HH:mm", newAddEndtimeText)) {
                    UiUtils.ToastUtils("上班时间不能小于下班时间");
                } else {
                    getServerData();
                }
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
                if (attTimeBean != null) {
                    if (attTimeBean.result.timecode != null) {
                        Intent intent = new Intent(new Intent(this, LableInfoActivity.class));
                        intent.putExtra("timecode", attTimeBean.result.timecode);
                        startActivity(intent);
                    }
                } else {
                    UiUtils.ToastUtils("请先保存信息");
                }
                break;
            case R.id.new_add_ll_peop:
                if (attTimeBean != null) {
                    if (attTimeBean.result.timecode != null) {
                        Intent intent1 = new Intent(new Intent(this, AttPeopActivity.class));
                        intent1.putExtra("timecode", attTimeBean.result.timecode);
                        startActivity(intent1);
                    }
                } else {
                    UiUtils.ToastUtils("请先保存信息");
                }
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
                    if(attTimeBean.status.equals("00000"))
                    UiUtils.ToastUtils("请设置标签和人员");
                    Log.e("增加的数据为:", attTimeBean.result.timecode);
                    setResult(1);
//                    finish();
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
