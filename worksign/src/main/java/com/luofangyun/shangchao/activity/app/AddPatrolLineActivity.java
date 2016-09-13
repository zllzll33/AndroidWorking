package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.PatrolLine;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by win7 on 2016/9/12.
 */
public class AddPatrolLineActivity extends  BaseActivity {
    View view;
    String action,empname,linecode,starttime,endtime,contactsname,linename;
    EditText add_line_name,time_begin,time_end;
    TextView contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.act_add_patrol_line);
        initView();
        initData();
    }
    private  void initView()
    {
        contacts=(TextView)view.findViewById(R.id.contacts);
        add_line_name=(EditText)view.findViewById(R.id.add_line_name) ;
        time_begin=(EditText)view.findViewById(R.id.time_begin) ;
        time_end=(EditText)view.findViewById(R.id.time_end) ;
        time_begin.setOnClickListener(this);
        time_end.setOnClickListener(this);
        contacts.setOnClickListener(this);
        right.setVisibility(View.VISIBLE);
        right.setText("保存");
        flAddress.addView(view);
        action=getIntent().getAction();
        if(action.equals("addPatrolLine"))
        {
            titleTv.setText("添加巡检路线");
        }
        else if(action.equals("editPatrolLine"))
        {
            titleTv.setText("编辑巡检路线");
            Bundle bundle=getIntent().getBundleExtra("bundle");
            empname=bundle.getString("empname");
            linecode=bundle.getString("linecode");
            starttime=bundle.getString("starttime");
            endtime=bundle.getString("endtime");
            linename=bundle.getString("linename");
            contactsname=bundle.getString("contacts");
            contacts.setText(empname);
            add_line_name.setText(linename);
            time_begin.setText(starttime.substring(11,16));
            time_end.setText(endtime.substring(11,16));
        }
    }
    private  void initData()
    {

    }
    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.right:
                if(action.equals("addPatrolLine"))
                {
                   save("0");
                }
                else if(action.equals("editPatrolLine"))
                {
                    save(linecode);
                    Log.e("linecode",linecode);
                }
                break;
            case R.id.time_begin:
                DateTimePickDialogUtil dateTimePicKDialog1 = new DateTimePickDialogUtil(
                        this, "");
                dateTimePicKDialog1.isTime=true;
                dateTimePicKDialog1.dateTimePicKDialog(time_begin);
                break;
            case R.id.time_end:
                DateTimePickDialogUtil dateTimePicKDialog2 = new DateTimePickDialogUtil(
                        this, "");
                dateTimePicKDialog2.isTime=true;
                dateTimePicKDialog2.dateTimePicKDialog(time_end);
                break;
            case R.id.contacts:
                Intent intent=new Intent(AddPatrolLineActivity.this,AddPatrolLineEmplayeeActivity.class);
                startActivityForResult(intent,1);
                break;
        }
    }

    private void save(String linecode)
    {
           if(add_line_name.getText().toString().isEmpty())
           {
               UiUtils.ToastUtils("请输入路线名称");
               return;
           }
        if(contacts.getText().toString().isEmpty())
        {
            UiUtils.ToastUtils("请选择紧急联系人");
            return;
        }
        if(time_begin.getText().toString().isEmpty())
        {
            UiUtils.ToastUtils("请选择开始时间");
            return;
        }
        if(time_end.getText().toString().isEmpty())
        {
            UiUtils.ToastUtils("请选择结束时间");
            return;
        }
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "patrol_line_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map=new HashMap<>();
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("linecode", linecode);
            map.put("linename", add_line_name.getText().toString());
            map.put("starttime", time_begin.getText().toString());
            map.put("endtime", time_end.getText().toString());
            map.put("contacts", contactsname);
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            Log.e("map",UiUtils.Map2JsonStr(map));
            request.add(map);
            CallServer.getRequestInstance().add(getApplication(), 0, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            Log.e("addLine",response.get());
            setResult(1);
            finish();
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Bundle bundle=data.getBundleExtra("emp");
        empname=bundle.getString("empname");
        contactsname=bundle.getString("empphone");
        contacts.setText(linename);
    }
}
