package com.luofangyun.shangchao.activity.addresslist;

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
import com.luofangyun.shangchao.domain.ApplyBean;
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
 * 添加员工
 */
public class AddEmplActivity extends BaseActivity {
    private TextView addEmplSave, addTv;
    private View         view;
    private LinearLayout addEmp;
    private String       deptName, deptCode;
    private EditText emplEtName, emplEtPhone, emplEtChoce;
    private Map<String, String> map = new HashMap<>();
    private ApplyBean applyBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(1);
        view = UiUtils.inflateView(R.layout.activity_add_empl);
        initView();
        initData();
    }

    private void initView() {
        addEmplSave = (TextView) view.findViewById(R.id.add_empl_save);
        emplEtName = (EditText) view.findViewById(R.id.empl_et_name);
        emplEtPhone = (EditText) view.findViewById(R.id.empl_et_phone);
        emplEtChoce = (EditText) view.findViewById(R.id.empl_et_choce);
        addTv = (TextView) view.findViewById(R.id.add_tv);
        addEmp = (LinearLayout) view.findViewById(R.id.add_empl_ll);
    }
    private void initData() {
        deptName = getIntent().getStringExtra("parentname");               //点击企业部门传来的部门名称
        deptCode = getIntent().getStringExtra("parentdept");               //点击企业部门传来的部门编码
        if (!TextUtils.isEmpty(deptName)) {
            addTv.setText(deptName);
        }
        else
        {
            addTv.setText("暂无");
        }
        addEmplSave.setOnClickListener(this);
        addEmp.setOnClickListener(this);
        titleTv.setText("添加员工");
        right.setVisibility(View.VISIBLE);
        right.setText("保存");
        flAddress.addView(view);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_ll_back:
//                PrefUtils.putBoolean(AddEmplActivity.this, "addNewEt", false);
                finish();
//                startActivity(new Intent(this, CorporActivity.class));
                break;
            case R.id.add_empl_save:
                getServerData(1);
                break;
            case R.id.add_empl_ll:
         /*       PrefUtils.putBoolean(AddEmplActivity.this, "addNewEt", true);
                PrefUtils.putString(this, "emplEtNameText", emplEtName.getText().toString().trim());
                PrefUtils.putString(this, "emplEtPhoneText", emplEtPhone.getText().toString().trim());
                PrefUtils.putString(this, "emplEtChoceText", emplEtChoce.getText().toString().trim());
                startActivity(new Intent(this, CorporBranchActivity.class));
                finish();*/
                break;
            case R.id.right:
                if (TextUtils.isEmpty(emplEtName.getText().toString().trim())) {
                    UiUtils.ToastUtils("名字不能为空");
                }  else if (TextUtils.isEmpty(emplEtPhone.getText().toString().trim())) {
                    UiUtils.ToastUtils("电话号码不能为空");
                }else if (emplEtPhone.getText().toString().trim().length()!=11) {
                      UiUtils.ToastUtils("非法手机号");
                }
                else {
                    getServerData(0);
//                    startActivity(new Intent(getApplication(), CorporActivity.class));
                }
                break;
            default:
                break;
        }
    }

    private void getServerData(int index) {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "employee_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("empname", emplEtName.getText().toString().trim());
            map.put("empdept", deptCode);
            map.put("empphone", emplEtPhone.getText().toString().trim());
            map.put("empcode", "0");
            map.put("emppost", emplEtChoce.getText().toString().trim());
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request1.add(map);
            CallServer.getRequestInstance().add(this, index, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            System.out.println("添加员工=" + result);
            switch (what)
            {
                case 0:
                    processData(result);
                    if(applyBean.status.equals("00000")) {
                        CorporActivity.handler.sendEmptyMessage(1);
                        finish();
                    }
                    else
                    {
                        UiUtils.ToastUtils(applyBean.summary);
                    }
                    break;
                case 1:
                    processData(result);
                    if(applyBean.status.equals("00000"))
                    {
                        CorporActivity.handler.sendEmptyMessage(1);
                        emplEtName.setText("");
                        emplEtPhone.setText("");
                        addTv.setText("");
                        emplEtChoce.setText("");
                    }
                    else
                        UiUtils.ToastUtils(applyBean.summary);
                    break;
            }


        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
            UiUtils.ToastUtils("添加失败");
        }
    };
    private void processData(String result) {
        Gson gson = new Gson();
        applyBean = gson.fromJson(result, ApplyBean.class);
    }
}