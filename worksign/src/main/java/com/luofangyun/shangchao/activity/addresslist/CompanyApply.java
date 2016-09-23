package com.luofangyun.shangchao.activity.addresslist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.MainActivity;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.base.impl.AddressListPager;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.domain.UerEnBean;
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

import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 申请加入团队
 */
public class CompanyApply extends BaseActivity {
    private View view;
    private String companyName1, companyCode1, phoneNumber, result;
    private EditText applyName, applyContact, applyPhone, applyMemo;
    private TextView            apply;
    private Map<String, String> map;
    private String              applyPhoneText;
    private String              applyMemoText;
    private String              applyContactText;
    private ApplyBean           applyBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_company_apply);
        companyName1 = (String) getIntent().getExtras().get("companyname");
        companyCode1 = (String) getIntent().getExtras().get("companycode");
        PrefUtils.putString(this, "companyName1", companyName1);
        PrefUtils.putString(this, "companyCode1", companyCode1);
        phoneNumber = PrefUtils.getString(this, "phoneNumber", null);
        initView();
        initData();
    }

    private void initView() {
        applyName = (EditText) view.findViewById(R.id.apply_name);
        applyContact = (EditText) view.findViewById(R.id.apply_contact);
        applyPhone = (EditText) view.findViewById(R.id.apply_phone);
        applyMemo = (EditText) view.findViewById(R.id.apply_memo);
        apply = (TextView) view.findViewById(R.id.apply);
    }

    private void initData() {
        titleTv.setText("申请加入");
        map = new HashMap<>();
        applyName.setText(companyName1);
        apply.setOnClickListener(this);
        flAddress.addView(view);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.apply) {
            applyContactText = applyContact.getText().toString().trim();
            applyPhoneText = applyPhone.getText().toString().trim();
            applyMemoText = applyMemo.getText().toString().trim();
            if (applyContactText.isEmpty()) {
                UiUtils.ToastUtils("姓名不能为空");
            } else if (applyPhoneText.isEmpty()) {
                UiUtils.ToastUtils("电话不能为空");
            } else if (applyMemoText.isEmpty()) {
                UiUtils.ToastUtils("备注不能为空");
            }else {
                getServerData(phoneNumber, companyCode1, applyContactText, applyMemoText);
            }
        }
    }

    /**
     * 申请加入团队数据请求
     */
    public void getServerData(String phoneNumber, String companyCode, String applyContactText, String applyMemoText) {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "company_apply.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", phoneNumber);
            map.put("companycode", companyCode);
            map.put("empname", URLDecoder.decode(applyContactText, "UTF-8"));
            map.put("remark", URLDecoder.decode(applyMemoText, "UTF-8"));
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request.add(map);
            CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLoginData() {
        try {
         String  passWord=PrefUtils.getString(getApplicationContext(),"sw","");
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "user_login.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map1=new HashMap<>();
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("password", MD5Encoder.encode(passWord));
           String  encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request1.add(map1);
            CallServer.getRequestInstance().add(this, 2, request1, httpListener, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接受响应
     */
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            result = response.get();
            switch (what)
            {
                case 1:
                    System.out.println("申请加入团队result=" + result);
                    processData(result);
                    break;
                case 2:
                    Gson gson = new Gson();
                    uerEnBean = gson.fromJson(result, UerEnBean.class);
                    if (uerEnBean.status.equals("00001")) {
                        UiUtils.ToastUtils("帐号或密码错误，请重新输入");
                    } else {
                        PrefUtils.putString(getApplication(), "phoneNumber", uerEnBean.result.empphone);        //手机号
                        PrefUtils.putString(getApplication(), "empphoto", uerEnBean.result.empphoto);           //头像地址
                        PrefUtils.putString(getApplication(), "companyname", uerEnBean.result.companyname);     //企业名称
                        PrefUtils.putString(getApplication(), "deptname", uerEnBean.result.deptname);           //部门名称
                        PrefUtils.putString(getApplication(), "emppost", uerEnBean.result.emppost);             //职位名称
                        PrefUtils.putString(getApplication(), "empsex", uerEnBean.result.empsex);               //性别
                        PrefUtils.putString(getApplication(), "empbirthday", uerEnBean.result.empbirthday);     //生日
                        PrefUtils.putString(getApplication(), "empaddress", uerEnBean.result.empaddress);       //地址
                        PrefUtils.putString(getApplication(), "deptcode", uerEnBean.result.deptcode);    //部门编码
                        PrefUtils.putString(getApplication(), "companycode", uerEnBean.result.companycode);     //企业编码
                        PrefUtils.putString(getApplication(), "empcode", uerEnBean.result.empcode);             //员工编码
                        PrefUtils.putBoolean(getApplication(),"loginstaus",true);
                        PrefUtils.putInt(getApplication(),"ismng",uerEnBean.result.ismng);
                        AddressListPager.handler.sendEmptyMessage(1);
                        finish();
                    }
                    break;
            }







        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

        }
    };
    //解析数据
    private UerEnBean           uerEnBean;

    private void processData(String json) {
        Gson gson = new Gson();
        applyBean = gson.fromJson(json, ApplyBean.class);
            if (!applyBean.status.equals("00000")) {
                UiUtils.ToastUtils("您已经有团队或错误");
            }
        else {
                UiUtils.ToastUtils("您已申请加入团队");
                getLoginData();
            }

    }
}
