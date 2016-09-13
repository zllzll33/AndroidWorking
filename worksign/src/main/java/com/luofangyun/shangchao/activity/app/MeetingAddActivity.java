package com.luofangyun.shangchao.activity.app;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.DateTimePickDialogUtil;
import com.luofangyun.shangchao.utils.MD5Encoder;
import com.luofangyun.shangchao.utils.PrefUtils;
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
 * 会议增加
 */
public class MeetingAddActivity extends BaseActivity {
    private EditText addName, addMotif, meetAddress, meetStartTime, meetEndTime;
    private LinearLayout startTime, endTime;
    private TextView     addTv;
    private RecyclerView addRv;
    private View         view;
    private String       addNameEdi, addMotifEdi, meetStartTimeEdi, meetEndTimeEdi, meetAddressEdi;
    private Map<String, String> map1;
    private String              phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_meeting_add);
        phoneNumber = PrefUtils.getString(this, "phoneNumber", null);
        initView();
        initData();
    }
    private void initView() {
        addName = (EditText) view.findViewById(R.id.add_name);
        addMotif = (EditText) view.findViewById(R.id.add_motif);
        startTime = (LinearLayout) view.findViewById(R.id.start_time);
        meetStartTime = (EditText) view.findViewById(R.id.meet_start_time);
        endTime = (LinearLayout) view.findViewById(R.id.end_time);
        meetEndTime = (EditText) view.findViewById(R.id.meet_end_time);
        meetAddress = (EditText) view.findViewById(R.id.meet_address);
        addRv = (RecyclerView) view.findViewById(R.id.add_rv);
        addTv = (TextView) view.findViewById(R.id.add_tv);
    }
    private void initData() {
        map1 = new HashMap<>();
        addTv.setOnClickListener(this);
        startTime.setOnClickListener(this);
        endTime.setOnClickListener(this);
        right.setText("保存");
        right.setVisibility(View.VISIBLE);
        titleTv.setText("会议");
        flAddress.addView(view);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.add_tv:
                break;
            case R.id.right:
                addNameEdi = addName.getText().toString().trim();
                addMotifEdi = addMotif.getText().toString().trim();
                meetStartTimeEdi = meetStartTime.getText().toString().trim();
                meetEndTimeEdi = meetEndTime.getText().toString().trim();
                meetAddressEdi = meetAddress.getText().toString().trim();
                if (addNameEdi.isEmpty()) {
                    UiUtils.ToastUtils("会议名称不能为空");
                } else if (addMotifEdi.isEmpty()) {
                    UiUtils.ToastUtils("会议主题不能为空");
                } else if (meetStartTimeEdi.isEmpty()) {
                    UiUtils.ToastUtils("开始时间不能为空");
                } else if (meetEndTimeEdi.isEmpty()) {
                    UiUtils.ToastUtils("结束时间不能为空");
                } else if (meetAddressEdi.isEmpty()) {
                    UiUtils.ToastUtils("会议地点不能为空");
                } else if (UiUtils.timeToMill("yyyy-MM-dd HH:mm", meetStartTimeEdi) > UiUtils
                        .timeToMill("yyyy-MM-dd HH:mm", meetEndTimeEdi)) {
                    UiUtils.ToastUtils("开始时间不能大于结束时间");
                } else {
                    getServerSave();
                    finish();
                }
                break;
            case R.id.start_time:
                DateTimePickDialogUtil dateTimePicKDialog1 = new DateTimePickDialogUtil(
                        this, "");
                dateTimePicKDialog1.dateTimePicKDialog(meetStartTime);
                break;
            case R.id.end_time:
                DateTimePickDialogUtil dateTimePicKDialog2 = new DateTimePickDialogUtil(
                        this, "");
                dateTimePicKDialog2.dateTimePicKDialog(meetEndTime);
                break;
            default:
                break;
        }
    }

    private void getServerSave() {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "meet_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", phoneNumber);
            map1.put("meetcode", "0");
            map1.put("meetname", addNameEdi);
            map1.put("meettheme", addMotifEdi);
            map1.put("meetdate", meetStartTimeEdi);
            map1.put("starttime", meetStartTimeEdi);
            map1.put("endtime", meetEndTimeEdi);
            map1.put("meetaddress", meetAddressEdi);
            map1.put("phones", "110,119,120");
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request1.add(map1);
             CallServer.getRequestInstance().add(this, 1, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            String result1 = response.get();
            System.out.println("result1=" + result1);
            processAddData(result1);
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };

    private void processAddData(String result1) {
        Gson gson = new Gson();
        ApplyBean meetAddBean = gson.fromJson(result1, ApplyBean.class);
    }
}
