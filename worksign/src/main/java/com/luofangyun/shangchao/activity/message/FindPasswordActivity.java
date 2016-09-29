package com.luofangyun.shangchao.activity.message;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.domain.Personal;
import com.luofangyun.shangchao.domain.VerificationCode;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.MD5Encoder;
import com.luofangyun.shangchao.utils.NumberSend;
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
 * 忘记密码页面
 */
public class FindPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText findPasswordEtPhone, findPasswordEtPassword,
            findPasswordEtPasswordConfirmation, findPasswordEtVerificationCode;
    private TextView findPasswordTvGetVerificationCode, findPasswordTvRegist, titleTv;
    private LinearLayout    titleLlBack;
    private String          phoneNum;
    private Request<String> request1, request2;
    private Map<String, String> map1, map2;
    private static String vercode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        //查找相关的控件
        initView();
        //初始化数据
        initData();
    }

    private void initView() {

        findPasswordEtPhone = (EditText) findViewById(R.id.find_password_et_phone);
        //注册时手机号
        findPasswordEtPassword = (EditText) findViewById(R.id.find_password__et_password);
        //输入新密码
        findPasswordEtPasswordConfirmation = (EditText) findViewById(R.id
                .find_password_et_password_confirmation);          //再次输入密码
        findPasswordEtVerificationCode = (EditText) findViewById(R.id
                .find_password_et_verification_code);                  //收到的验证码
        findPasswordTvGetVerificationCode = (TextView) findViewById(R.id
                .find_password_tv_send_verification_code);          //发送验证码
        findPasswordTvRegist = (TextView) findViewById(R.id.find_password_tv_regist);
        //重置
        titleTv = (TextView) findViewById(R.id.title_tv);
        //找回密码标头
        titleLlBack = (LinearLayout) findViewById(R.id.title_ll_back);
        //返回
        findPasswordTvGetVerificationCode.setOnClickListener(this);
        findPasswordTvRegist.setOnClickListener(this);
        titleLlBack.setOnClickListener(this);
    }

    private void initData() {

        map1 = new HashMap<>();
        titleLlBack.setVisibility(View.VISIBLE);       //显示返回的按钮
        titleTv.setText("找回密码");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find_password_tv_send_verification_code:
                phoneNum = findPasswordEtPhone.getText().toString().trim();   //手机号
                if (phoneNum.isEmpty()) {
                    UiUtils.ToastUtils("手机号码不给为空");
                } else if (phoneNum.length() != 11) {
                    UiUtils.ToastUtils("非法手机号，请重新输入");
                } else {
                    requestServerData(phoneNum);                      //从服务器访问数据
                    UiUtils.ToastUtils("短信已发送，请注意查收……");
                }
                break;
            case R.id.find_password_tv_regist:
                String passWord = findPasswordEtPassword.getText().toString().trim();
                String passWordCon = findPasswordEtPasswordConfirmation.getText().toString().trim();
                String VerCode = findPasswordEtVerificationCode.getText().toString().trim();
                phoneNum = findPasswordEtPhone.getText().toString().trim();   //手机号
                if (phoneNum.isEmpty()) {
                    UiUtils.ToastUtils("手机号不能为空");
                } else if (passWord.isEmpty()) {
                    UiUtils.ToastUtils("密码不能为空");
                } else if (passWordCon.isEmpty()) {
                    UiUtils.ToastUtils("密码不能为空");
                } else if (VerCode.isEmpty()) {
                    UiUtils.ToastUtils("验证码不能为空");
                } /*else if (!VerCode.equals(vercode)) {
                    UiUtils.ToastUtils("验证码错误，请重新输入");
                } */else {
                    requestResServerData(phoneNum, passWord, VerCode);
                }
                break;
            case R.id.title_ll_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void requestResServerData(String telnum, String newpwd, String vercode) {
        try {
            request2 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL + "password_reset" +
                    ".json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map2=new HashMap<>();
            map2.put("access_id", "1234567890");
            map2.put("timestamp", time);
            map2.put("telnum", telnum);
            map2.put("newpwd", MD5Encoder.encode(newpwd));
            map2.put("vercode", vercode);
            String encode = MD5Encoder.encode(Sign.generateSign(map2) +
                    "12345678901234567890123456789011");
            map2.put("sign", encode);
            Log.e("map",UiUtils.Map2JsonStr(map2));
            request2.add(map2);
            CallServer.getRequestInstance().add(this, 1, request2, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestServerData(String telnum) {
        /**
         * 请求对象
         */
        try {
            request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL
                    + "vercode_get" +
                    ".json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", telnum);
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request1.add(map1);
            // 发起请求
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
                    String result = response.get();
                    System.out.println("修改密码返回的数据result=" + result);
                    VerificationCode verificationCode = processData(result);
                    break;
                case 1:
                    String resultPassWord = response.get();
                    System.out.println("resultPassWord=" + resultPassWord);
                    ApplyBean personal=new Gson().fromJson(resultPassWord,ApplyBean.class);
                    if(personal.status.equals("00000"))
                    {
                        UiUtils.ToastUtils("修改密码成功");
                        finish();
                    }
                    else
                    UiUtils.ToastUtils(personal.summary);
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

    private Personal processpassWordData(String resultPassWord) {
        Gson gson = new Gson();
        Personal resVerCode = gson.fromJson(resultPassWord, Personal.class);
        return resVerCode;
    }

    private VerificationCode processData(String result) {
        Gson gson = new Gson();
        VerificationCode verificationCode = gson.fromJson(result, VerificationCode.class);
//        Log.e("vercode",verificationCode.result.vercode);
        vercode=verificationCode.result.vercode;
        return verificationCode;
    }
}
