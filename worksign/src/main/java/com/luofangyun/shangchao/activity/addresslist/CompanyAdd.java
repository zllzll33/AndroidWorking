package com.luofangyun.shangchao.activity.addresslist;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.Company;
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
 * 创建团队
 */

public class CompanyAdd extends BaseActivity {
    private View     view;
    private EditText companyName, companyContact, companyPhone, companyMemo;
    private TextView apply;
    private String   companyNameText, companyPhoneText, companyMemoText, companyContactText;
    private Map<String, String> map;
    private String       phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_company_add);
        initView();
        initData();
    }

    private void initData() {
        map = new HashMap<>();
        titleTv.setText("申请加入");
        phoneNumber = PrefUtils.getString(this, "phoneNumber", null);
        titleTv.setText("创建团队");
        flAddress.addView(view);
    }

    private void initView() {
        companyName = (EditText) view.findViewById(R.id.company_name);
        companyContact = (EditText) view.findViewById(R.id.company_contact);
        companyPhone = (EditText) view.findViewById(R.id.company_phone);
        companyMemo = (EditText) view.findViewById(R.id.company_memo);
        apply = (TextView) view.findViewById(R.id.apply);
        apply.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.apply:
                companyNameText = companyName.getText().toString().trim();
                companyContactText = companyContact.getText().toString().trim();
                companyPhoneText = companyPhone.getText().toString().trim();
                companyMemoText = companyMemo.getText().toString().trim();

                PrefUtils.putString(this, "companyNameText", companyNameText);
                PrefUtils.putString(this, "companyContactText", companyContactText);
                PrefUtils.putString(this, "companyPhoneText", companyPhoneText);
                PrefUtils.putString(this, "companyMemoText", companyMemoText);

                if (companyNameText.isEmpty()) {
                    UiUtils.ToastUtils("企业名称不能为空");
                } else if (companyContactText.isEmpty()) {
                    UiUtils.ToastUtils("姓名不能为空");
                } else if (companyPhoneText.isEmpty()) {
                    UiUtils.ToastUtils("电话不能为空");
                } else if (companyMemoText.isEmpty()) {
                    UiUtils.ToastUtils("备注不能为空");
                }  else {
                    getServerCompany(phoneNumber, companyNameText, companyContactText, companyMemoText, companyPhoneText);
                    finish();

                }
                break;
            default:
                break;
        }
    }
    private void getServerCompany(String phoneNumber, String companyNameText, String
            companyContactText, String companyMemoText, String companyPhoneText) {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                            "company_add.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", phoneNumber);
            map.put("companyname", URLDecoder.decode(companyNameText, "UTF-8"));
            map.put("companycontact", URLDecoder.decode(companyContactText, "UTF-8"));
            map.put("companymemo", URLDecoder.decode(companyMemoText, "UTF-8"));
            map.put("companytel", companyPhoneText);
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
            System.out.println("result------------------------------" + result);
            Company company = processData(result);
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };
    private Company processData(String result) {
        Gson gson = new Gson();
        Company company = gson.fromJson(result, Company.class);
        if (!company.status.equals("00000")) {
             UiUtils.ToastUtils("您已经有团队，请勿重新申请");
        } else {
            String companycode = company.result.companycode;
            PrefUtils.putString(this, "companycode", companycode);
        }
        return company;
    }
}