package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.Label;
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
 * Created by win7 on 2016/9/14.
 */
public class AddNFCTagActivity extends BaseActivity{
    private View view;
    EditText tag_name,tag_note;
    String nfcid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    private  void init()
    {
        Intent intent=getIntent();
        nfcid=intent.getStringExtra("nfcid");
        titleTv.setText("添加NFC标签");
        right.setText("提交");
        right.setVisibility(View.VISIBLE);
        view= UiUtils.inflateView(R.layout.act_add_nfc_tag);
        flAddress.addView(view);
        tag_name=(EditText)view.findViewById(R.id.tag_name);
        tag_note=(EditText)view.findViewById(R.id.tag_note);
        right.setOnClickListener(this);
    }
    @Override
    public  void onClick(View v)
    {
        super.onClick(v);
        if(v.getId()==R.id.right)
        saveNFCtag();
    }
    private void saveNFCtag()
    {
        if(tag_name.getText().toString().isEmpty())
        {
            UiUtils.ToastUtils("请输入NFC标签名称");
            return;
        }
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "label_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map1=new HashMap<>();
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("labelcode", "0");
            map1.put("labeltype", "0");
            map1.put("labelsn", nfcid);
            map1.put("labelname",tag_name.getText().toString() );
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
//                    Log.e("nfcadd",response.get());
                  NfcLabelActivity.handler.sendEmptyMessage(1);
                    finish();
                 break;
                case 2:
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
