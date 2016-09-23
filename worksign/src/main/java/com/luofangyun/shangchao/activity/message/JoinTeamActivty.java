package com.luofangyun.shangchao.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.app.LeavetypeListActivity;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.base.impl.MessageCenterPager;
import com.luofangyun.shangchao.domain.JoinTeamDetailBean;
import com.luofangyun.shangchao.domain.LeaveDetailBean;
import com.luofangyun.shangchao.domain.MessageCenter;
import com.luofangyun.shangchao.domain.OutDetailBean;
import com.luofangyun.shangchao.domain.OverTimeDetailBean;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by win7 on 2016/9/18.
 */
public class JoinTeamActivty extends BaseActivity {
    private View view;
    TextView join_name,join_phone,join_note,agree,reject;
    String name,phone,note,notifyCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view= UiUtils.inflateView(R.layout.act_join_team);
        flAddress.addView(view);
        titleTv.setText("加入团队申请");
        init();
    }
    private void init()
    {
        join_name=(TextView)view.findViewById(R.id.join_name);
        join_phone=(TextView)view.findViewById(R.id.join_phone);
        join_note=(TextView)view.findViewById(R.id.join_note);
        agree=(TextView)view.findViewById(R.id.agree);
        reject=(TextView)view.findViewById(R.id.reject);
        Intent intent=getIntent();
        name=intent.getStringExtra("join_name") ;
        phone=intent.getStringExtra("join_phone");
        note=intent.getStringExtra("join_note");
        notifyCode=intent.getStringExtra("notifyCode") ;
        join_name.setText(name);
        join_phone.setText(phone);
        join_note.setText(note);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinHttp("0");
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinHttp("1");
            }
        });
    }
    private void JoinHttp(String opflag) {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "apply_done.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map=new HashMap<>();
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("opflag", opflag);
            map.put("notifycode", notifyCode);
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
            switch (what) {
                case 0:
                    MessageCenterPager.handler.sendEmptyMessage(1);
                    finish();
                    break;

            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

}
