package com.luofangyun.shangchao.activity.maself;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
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
 * 修改密码页面
 */
public class ModifyPasswordActivity extends BaseActivity implements View.OnClickListener {

    private View     view;
    private EditText etPhone, etOldPassword, etNewPassword;
    private Map<String, String> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_modify_password);
        //查找相关的控件
        initView();
        //添加数据
        initData();
    }

    private void initView() {
        etPhone = (EditText) view.findViewById(R.id.et_phone);
        etOldPassword = (EditText) view.findViewById(R.id.et_old_password);
        etNewPassword = (EditText) view.findViewById(R.id.et_new_password);
        view.findViewById(R.id.tv_modify_password).setOnClickListener(this);
    }

    private void initData() {
        titleTv.setText("修改密码");
        flAddress.addView(view);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_modify_password:
                if (TextUtils.isEmpty(etPhone.getText().toString().trim())) {
                    UiUtils.ToastUtils("旧密码不能为空");
                } else if (TextUtils.isEmpty(etOldPassword.getText().toString().trim())) {
                    UiUtils.ToastUtils("密码不能为空");
                } else if (etNewPassword.getText().toString().trim().isEmpty()) {
                    UiUtils.ToastUtils("密码不能为空");
                } else if (!etOldPassword.getText().toString().trim().equals(etNewPassword
                        .getText().toString().trim().isEmpty())) {
                    UiUtils.ToastUtils("两次密码不一致");
                } else if (!(etOldPassword.getText().toString().trim().length() >= 6 && etOldPassword.getText().toString().trim().length() <= 16)) {
                    UiUtils.ToastUtils("密码长度不能小于6位或大于16位");
                } else {
                    getModifServerData();
                }
        }
    }

    /**
     * 修改密码
     */
    private void getModifServerData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "password_set.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("oldpwd", MD5Encoder.encode(etPhone.getText().toString().trim()));
            map.put("newpwd", MD5Encoder.encode(etNewPassword.getText().toString().trim()));
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request.add(map);
            // 发起请求
            CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        private ApplyBean applyBean;

        @Override
        public void onSucceed(int what, Response<String> response) {
            ApplyBean applyBean = new Gson().fromJson(response.get(), ApplyBean.class);
            if (applyBean.status.equals("00000")) {
                UiUtils.ToastUtils(applyBean.summary);
                finish();
            } else if (!applyBean.status.equals("00000")) {
                UiUtils.ToastUtils("旧密码不正确，请重新输入");
                etPhone.setText("");
            }

        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };
}
