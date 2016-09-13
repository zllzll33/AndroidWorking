package com.luofangyun.shangchao.activity.message;

import android.os.Bundle;
import android.view.View;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.MessageBean;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
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
 * 工作通知→详细信息
*/
public class messageWork extends BaseActivity {

    private View   view;
    private String notifycodeWork, phoneNumber;
    private Map<String, String> map1;
    private MessageBean         workBean;
   // private TextView            workTitle, workTime, workContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_message_part);
        phoneNumber = PrefUtils.getString(this, "phoneNumber", null);
        notifycodeWork = (String) getIntent().getExtras().get("notifycodeWork");
        initView();
        initData();
    }

    private void initView() {
        titleTv.setText("消息");
        //workTitle = (TextView) view.findViewById(R.id.part_title);
        //workTime = (TextView) view.findViewById(R.id.part_time);
        //workContent = (TextView) view.findViewById(R.id.part_content);
        titileBack.setVisibility(View.GONE);
    }

    private void initData() {
        map1 = new HashMap<>();
        getServerWork(notifycodeWork);
        flAddress.addView(view);
    }

    public void getServerWork(String notifycodeWork) {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "sysnofiy_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", phoneNumber);
            map1.put("notifycode", notifycodeWork);
            map1.put("notifytype", String.valueOf(1));
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
            switch (what) {
                case 1:
                    String result = response.get();
                    System.out.println("resultWork=" + result);
                   // MessageBean messageBean = processWork(result);
                    break;
                default:
                    break;
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };
}
