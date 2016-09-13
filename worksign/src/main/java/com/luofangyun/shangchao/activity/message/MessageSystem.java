package com.luofangyun.shangchao.activity.message;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
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
 * 系统消息→详细信息
 */
public class MessageSystem extends BaseActivity {

    private View     view;
    private TextView systemeTitle, systemeTime, systemeContent;
    private Map<String, String> map;
    private String              phoneNumber, notifycodeSysteme;
    private MessageBean SystemeBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_message_system);
        phoneNumber = PrefUtils.getString(this, "phoneNumber", null);
        notifycodeSysteme = PrefUtils.getString(MessageSystem.this, "notifycodeSystem", null);
        initView();
        initData();
    }

    private void initView() {
        systemeTitle = (TextView) view.findViewById(R.id.systeme_title);
        systemeTime = (TextView) view.findViewById(R.id.systeme_time);
        systemeContent = (TextView) view.findViewById(R.id.systeme_content);
    }

    private void initData() {
        map = new HashMap<>();
        getServerSystem(phoneNumber, notifycodeSysteme);
        flAddress.addView(view);
    }

    private void getServerSystem(String phoneNumber, String notifycodeSysteme) {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "sysnofiy_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", phoneNumber);
            map.put("notifycode", notifycodeSysteme);
            map.put("notifytype", String.valueOf(0));
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
            MessageBean messageBean = processSystem(result);
            System.out.println("resultSystem=" + result);
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };
    private MessageBean processSystem(String result) {
        Gson gson = new Gson();
        SystemeBean = gson.fromJson(result, MessageBean.class);
        String notifytitle = SystemeBean.result.notifytitle;
        String notifycontent = SystemeBean.result.notifycontent;
        String notifydate = SystemeBean.result.notifydate;
        System.out.println("notifytitle=" + notifytitle);
        System.out.println("notifycontent=" + notifycontent);
        System.out.println("notifydate=" + notifydate);
        systemeTitle.setText(notifytitle);
        systemeTime.setText(notifydate);
        systemeContent.setText(notifycontent);
        return SystemeBean;
    }
}
