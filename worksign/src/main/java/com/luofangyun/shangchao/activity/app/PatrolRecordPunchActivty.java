package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.domain.WorkTimeBean;
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
 * Created by win7 on 2016/9/19.
 */
public class PatrolRecordPunchActivty extends BaseActivity {
    View view;
    String blueToothNum;
    TextView line_name,num,status,sign_time;
    RelativeLayout rl_confirm;
    String nowTime;
    public  static String pointname,lineStatus,linecode,pointcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view= UiUtils.inflateView(R.layout.act_patrol_record_punch);
        flAddress.addView(view);
        titleTv.setText("巡检点打卡");
        String blueNum = getIntent().getStringExtra("blueNum");
        blueToothNum = blueNum.substring(blueNum.length()-5, blueNum.length());
        num=(TextView)view.findViewById(R.id.num);
        line_name=(TextView)view.findViewById(R.id.line_name);
        status=(TextView)view.findViewById(R.id.status);
        sign_time=(TextView)view.findViewById(R.id.sign_time);
        rl_confirm=(RelativeLayout)view.findViewById(R.id.rl_confirm);
        line_name.setText("巡检点名称:"+pointname);
        num.setText("蓝牙标签编码:"+blueToothNum);
        if(lineStatus.equals("0"))
            status.setText("巡检状态:未巡检");
        else
            status.setText("巡检状态:已巡检");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String data = sdf.format(new Date());
        nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        sign_time.setText(data);
        rl_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getServerData();
            }
        });
    }
    private void getServerData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "patrol_record_add.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map=new HashMap<>();
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("atttime", nowTime);
            map.put("atttype", "2");
            map.put("linecode",linecode);
            map.put("pointcode",pointcode);
            map.put("attinfo", blueToothNum);
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            Log.e("巡检打卡",UiUtils.Map2JsonStr(map));
            request.add(map);
            CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        private ApplyBean applyBean;
        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            Log.e("打卡结果",result);
            switch (what)
            {
                case 1:
                    applyBean = new Gson().fromJson(result, ApplyBean.class);
                    if(applyBean.status.equals("00000")) {
                        PatrolRecordDetailActivity.handler.sendEmptyMessage(1);
                        finish();
                    }
                    break;

            }

        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
            UiUtils.ToastUtils(applyBean.summary);
        }
    };

}
