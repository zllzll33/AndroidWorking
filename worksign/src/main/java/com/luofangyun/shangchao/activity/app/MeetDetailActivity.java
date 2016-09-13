package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.MeetAdd;
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

public class MeetDetailActivity extends BaseActivity {
    private View         view;
    private LinearLayout meet1, meet2, meet3, meet4, meet5, meet6;
    private TextView meetText1, meetText2, meetText3, meetText4,
            meetText5, meetText6, addTv, meetRightText6;
    private EditText meetEt1, meetEt2, meetEt3, meetEt4, meetEt5;
    private final int MeetDetailActivity = 1;
    private static String labelcode;
    private Map<String, String> map = new HashMap<>();
    private MeetAdd meetAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_meet_detail);
        initView();
        initData();
    }
    private void initView() {
        meet1 = (LinearLayout) view.findViewById(R.id.meet1);
        meetText1 = (TextView) view.findViewById(R.id.meet_text1);
        meetEt1 = (EditText) view.findViewById(R.id.meet_et1);
        meet2 = (LinearLayout) view.findViewById(R.id.meet2);
        meetText2 = (TextView) view.findViewById(R.id.meet_text2);
        meetEt2 = (EditText) view.findViewById(R.id.meet_et2);
        meet3 = (LinearLayout) view.findViewById(R.id.meet3);
        meetText3 = (TextView) view.findViewById(R.id.meet_text3);
        meetEt3 = (EditText) view.findViewById(R.id.meet_et3);
        meet4 = (LinearLayout) view.findViewById(R.id.meet4);
        meetText4 = (TextView) view.findViewById(R.id.meet_text4);
        meetEt4 = (EditText) view.findViewById(R.id.meet_et4);
        meet5 = (LinearLayout) view.findViewById(R.id.meet5);
        meetText5 = (TextView) view.findViewById(R.id.meet_text5);
        meetEt5 = (EditText) view.findViewById(R.id.meet_et5);
        meet6 = (LinearLayout) view.findViewById(R.id.meet6);
        meetText6 = (TextView) view.findViewById(R.id.meet_text6);
        meetRightText6 = (TextView) view.findViewById(R.id.meet_right_text6);
        addTv = (TextView) view.findViewById(R.id.add_tv);

    }

    private void initData() {
        String statu = getIntent().getStringExtra("meetstatu");
        meet6.setOnClickListener(this);
        String action = getIntent().getAction();
        if (action.equals("MeetingActivity1")) {
            titleTv.setText("会议详情");
            getAction();
            if (statu.equals("0")) {
                titleTv.setText("");
            } else if (statu.equals("1")) {
                titleTv.setText("会议详情");
            }
        } else if (action.equals("MeetingActivity2")) {
            titleTv.setText("会议详情");
            getAction();
        } else if (action.equals("MeetingActivity3")) {
            titleTv.setText("");
            meetEt1.setFocusable(true);
            meetEt1.setFocusableInTouchMode(true);
            meetEt2.setFocusable(true);
            meetEt2.setFocusableInTouchMode(true);
            meetEt3.setFocusable(true);
            meetEt3.setFocusableInTouchMode(true);
            meet4.setOnClickListener(this);
            meet5.setOnClickListener(this);
            right.setVisibility(View.VISIBLE);
            right.setText("保存");
        }
        flAddress.addView(view);
    }

    public void getAction() {
        String meetname = getIntent().getStringExtra("meetname");
        String meettheme = getIntent().getStringExtra("meettheme");
        String meetaddres = getIntent().getStringExtra("meetaddress");
        String starttime = getIntent().getStringExtra("starttime");
        String endtime = getIntent().getStringExtra("endtime");
        String labelname = getIntent().getStringExtra("labelname");
        meetEt1.setText(TextUtils.isEmpty(meetname) ? "" : meetname);
        meetEt2.setText(TextUtils.isEmpty(meettheme) ? "" : meettheme);
        meetEt3.setText(meetaddres == null ? "" : meetaddres);
        meetEt4.setText(TextUtils.isEmpty(starttime) ? "" : starttime);
        meetEt5.setText(TextUtils.isEmpty(endtime) ? "" : endtime);
        meetRightText6.setText(labelname);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.right:
                if (TextUtils.isEmpty(meetEt1.getText().toString().trim())) {
                    UiUtils.ToastUtils("会议名称不能为空");
                } else if (TextUtils.isEmpty(meetEt2.getText().toString().trim())) {
                    UiUtils.ToastUtils("会议主题不能为空");
                } else if (TextUtils.isEmpty(meetEt3.getText().toString().trim())) {
                    UiUtils.ToastUtils("会议地点不能为空");
                } else if (TextUtils.isEmpty(meetEt4.getText().toString().trim())) {
                    UiUtils.ToastUtils("开始时间不能为空");
                } else if (TextUtils.isEmpty(meetEt5.getText().toString().trim())) {
                    UiUtils.ToastUtils("结束时间不能为空");
                } else if (TextUtils.isEmpty(meetRightText6.getText().toString())) {
                    UiUtils.ToastUtils("会议标签不能为空");
                }
              else {
                    getServerDate();
                }
                break;
            case R.id.meet4:
                //TODO:时间加载
                DateTimePickDialogUtil dateTimePicKDialog2 = new DateTimePickDialogUtil(
                        this, "");
                dateTimePicKDialog2.dateTimePicKDialog(meetEt4);
                break;
            case R.id.meet5:
                DateTimePickDialogUtil dateTimePicKDialog3 = new DateTimePickDialogUtil(
                        this, "");
                dateTimePicKDialog3.dateTimePicKDialog(meetEt5);
                break;
            case R.id.meet6:
                Intent intent = new Intent(new Intent(this, LibelActivity.class));
                intent.putExtra("meetEt1", TextUtils.isEmpty(meetEt1.getText().toString().trim())
                        ? "" : meetEt1.getText().toString().trim());
                intent.putExtra("meetEt2", TextUtils.isEmpty(meetEt2.getText().toString().trim())
                        ? "" : meetEt2.getText().toString().trim());
                intent.putExtra("meetEt3", TextUtils.isEmpty(meetEt3.getText().toString().trim())
                        ? "" : meetEt3.getText().toString().trim());
                intent.putExtra("meetEt4", TextUtils.isEmpty(meetEt3.getText().toString().trim())
                        ? "" : meetEt4.getText().toString().trim());
                intent.putExtra("meetEt5", TextUtils.isEmpty(meetEt5.getText().toString().trim())
                        ? "" : meetEt5.getText().toString().trim());
                startActivityForResult(intent, MeetDetailActivity);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
     {
           /* meetEt1.setText(getIntent().getStringExtra("meetEt1"));
            meetEt2.setText(getIntent().getStringExtra("meetEt2"));
            meetEt3.setText(getIntent().getStringExtra("meetEt3"));
            meetEt4.setText(getIntent().getStringExtra("meetEt4"));
            meetEt5.setText(getIntent().getStringExtra("meetEt5"));*/

            meetRightText6.setText(PrefUtils.getString(getApplication(), "labelname", ""));
            labelcode = PrefUtils.getString(getApplication(), "labelcode", "");
        }
    }

    private void getServerDate() {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "meet_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("meetcode", "0");
            map.put("meetname", meetEt1.getText().toString().trim());
            map.put("meettheme", meetEt2.getText().toString().trim());
            map.put("meetdate", meetEt4.getText().toString().trim());
            map.put("starttime", meetEt4.getText().toString().trim());
            map.put("endtime", meetEt5.getText().toString().trim());
            map.put("meetaddress", meetEt3.getText().toString().trim());
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request1.add(map);
            CallServer.getRequestInstance().add(this, 0, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {

            meetAdd = new Gson().fromJson(response.get(), MeetAdd.class);
            UiUtils.ToastUtils(meetAdd.summary);
            setResult(1);
            finish();
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };
}
