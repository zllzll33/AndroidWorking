package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.PatrolPoint;
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
import java.util.Iterator;
import java.util.Map;

/**
 * Created by win7 on 2016/9/12.
 */
public class AddPatrolPointActivity extends BaseActivity {
    private View view;
    EditText add_point_name;
    TextView add_tag_tv;
    String lablecode,lablename;
    String action,pointcode,pointname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.act_add_patrol_point);
        initView();
        initData();
    }
    private void  initView()
    {
        add_point_name=(EditText)view.findViewById(R.id.add_point_name);
        add_tag_tv=(TextView)view.findViewById(R.id.add_tag_tv);
        right.setVisibility(View.VISIBLE);
        right.setText("保存");
        flAddress.addView(view);
        action=getIntent().getAction();
        if(action.equals("addPatrolPoint"))
        {
            titleTv.setText("添加巡检点");
        }
        else if(action.equals("editPatrolPoint"))
        {
            titleTv.setText("编辑巡检点");
            Bundle bundle=getIntent().getBundleExtra("point");
            pointcode=bundle.getString("pointcode");
            lablecode=bundle.getString("labelcode");
            lablename=bundle.getString("labelname");
            pointname=bundle.getString("pointname");
            add_point_name.setText(pointname);
            add_tag_tv.setText(lablename);
        }
    }
    private void initData()
    {
        right.setOnClickListener(this);
        add_tag_tv.setOnClickListener(this);
    }
    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.add_tag_tv:
                Intent intent =new Intent(AddPatrolPointActivity.this,LibelActivity.class);
                        startActivityForResult(intent,2);
                break;
            case R.id.right:
                if(action.equals("addPatrolPoint"))
                {
                    save("0");
                }
                else if(action.equals("editPatrolPoint"))
                {
                    save(pointcode);
                }
                break;
        }
    }

    private void save(String pointcode)
    {
        if(add_point_name.getText().toString().isEmpty())
        {
            UiUtils.ToastUtils("请输入巡检地点");
            return;
        }
        if(add_tag_tv.getText().toString().isEmpty())
        {
            UiUtils.ToastUtils("请选择巡检标签");
            return;
        }
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "patrol_point_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String, String> map=new HashMap<>() ;
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pointcode",pointcode);
            map.put("labelcode",lablecode );
            map.put("pointname",add_point_name.getText().toString() );
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
            Log.e("巡检添加",response.get());
            switch (what) {
                case 0:
                    setResult(1);
                    finish();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        lablename= PrefUtils.getString(getApplicationContext(),"labelname","");
        lablecode=PrefUtils.getString(getApplicationContext(),"lablecode","");
        add_tag_tv.setText(lablename);
    }
}
