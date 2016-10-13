package com.luofangyun.shangchao.activity.message;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.LoginBean;
import com.luofangyun.shangchao.domain.VerificationCode;
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
 * 用户注册
 */

public class UserLoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText loginEtUserNum, VerEdiCode, loginEtPassword, loginEtPasswordConfirmation;
    private TextView loginGetVerificationCode, loginTvRegist;
    private Request<String>  request1, request2;
    private String           phoneNum;
    private VerificationCode verCode;
    private String           passWord;
    private Map<String, String> map1, map2;
    private String              passWordCon;
    private String              vercode="";
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_user_login);
        //查找相关的控件
        initView();
        //获取数据
        initData();
    }

    private void initView() {
        loginEtUserNum = (EditText) view.findViewById(R.id.user_login_et_phone); //手机号
        VerEdiCode = (EditText) view.findViewById(R.id.user_login_et_verification_code); //验证码
        loginGetVerificationCode = (TextView) view.findViewById(R.id
                .user_login_tv_get_verification_code);    //获取验证码
        loginEtPassword = (EditText) view.findViewById(R.id.user_login_et_password);  //密码
        loginEtPasswordConfirmation = (EditText) view.findViewById(R.id
                .user_login_et_password_confirmation); //确认密码
        loginTvRegist = (TextView) view.findViewById(R.id.user_login_tv_regist);  //注册
        loginGetVerificationCode.setOnClickListener(this);
        loginTvRegist.setOnClickListener(this);
    }

    private void initData() {
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        titleTv.setText("用户注册");
        flAddress.addView(view);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.user_login_tv_get_verification_code:                   //获取验证码
                try {
                    phoneNum = loginEtUserNum.getText().toString().trim();   //手机号
                    if (phoneNum.isEmpty()) {
                        UiUtils.ToastUtils("手机号码不给为空");
                    } else if (phoneNum.length() != 11) {
                        UiUtils.ToastUtils("非法手机号，请重新输入");
                    } else {
                        requestServerData(phoneNum);                             //从服务器访问数据
                        UiUtils.ToastUtils("短信已发送，请注意查收……");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.user_login_tv_regist:                                                 //立即注册
                String verEdiCode = VerEdiCode.getText().toString().trim();            //验证码
                passWord = loginEtPassword.getText().toString().trim();                 //密码
                passWordCon = loginEtPasswordConfirmation.getText().toString().trim();  //重新输入密码
                phoneNum = loginEtUserNum.getText().toString().trim();   //手机号
                passWord = loginEtPassword.getText().toString().trim();  //密码
                try {
                    if (phoneNum.isEmpty()) {
                        UiUtils.ToastUtils("手机号不能为空");
                    } else if (TextUtils.isEmpty(passWord)) {
                        UiUtils.ToastUtils("密码不能为空");
                    } else if (verEdiCode.isEmpty()) {
                        UiUtils.ToastUtils("验证码不能空");
                    } else if (TextUtils.isEmpty(passWordCon)) {
                        UiUtils.ToastUtils("密码不能为空");
                    } else if (phoneNum.length() != 11) {
                        UiUtils.ToastUtils("非法手机号，请重新输入");
                    } else if (!passWord.equals(passWordCon)) {
                        UiUtils.ToastUtils("密码输入不一致，请重新输入");
                    } else if (passWord.length() < 6 || passWord.length() > 16) {
                        UiUtils.ToastUtils("密码不能少于6位并且不能大于16位");
                    } else {
                        requestLoginServerData(phoneNum, passWord);
                      /*  if (!verEdiCode.equals(vercode)) {
                            UiUtils.ToastUtils("验证码不正确");
                        }
                        else {
                            requestLoginServerData(phoneNum, passWord);
                        }*/
                    }
                    System.out.println("立即注册");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            default:
                break;
        }
    }

    /**
     * 注册数据获取
     */
    private void requestLoginServerData(String telnum, String passWord) {
        try {
            request2 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL + "user_reg" +
                    ".json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map2.put("access_id", "1234567890");
            map2.put("timestamp", time);
            map2.put("telnum", telnum);
            map2.put("password", MD5Encoder.encode(passWord));
            map2.put("vercode", VerEdiCode.getText().toString().trim());
            String encode = MD5Encoder.encode(Sign.generateSign(map2) +
                    "12345678901234567890123456789011");
            map2.put("sign", encode);
            map2.put("password", MD5Encoder.encode(passWord));
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
            request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL + "vercode_get" +
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
                    System.out.println("获取验证码=" + result);
                    VerificationCode code = processData(result);
                    break;
                case 1:
                    String loginResult = response.get();
                    System.out.println("loginBean" + loginResult);
                    LoginBean loginBean = processLoginData(loginResult);
                    if(loginBean.status.equals("00000"))
                    {
                        UiUtils.ToastUtils("注册成功");
                        Intent intent = new Intent(UserLoginActivity.this, RegistSucess.class);
                        PrefUtils.putString(UserLoginActivity.this, "phoneNum", phoneNum);
                        PrefUtils.putString(UserLoginActivity.this, "passWord", passWord);
                        startActivity(intent);
                    }
                    else
                    {
                        UiUtils.ToastUtils(loginBean.summary);
                    }
                    break;
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };
    private LoginBean processLoginData(String loginResult) {
        Gson gson = new Gson();
        LoginBean loginBean = gson.fromJson(loginResult, LoginBean.class);
        String status = loginBean.status;
        System.out.println("status" + status);
        return loginBean;
    }

    /**
     * 获取验证码解析数据
     */
    private VerificationCode processData(String json) {
        Gson gson = new Gson();
        //通过json和对象类,生成一个对象
        verCode = gson.fromJson(json, VerificationCode.class);
        System.out.println();
        vercode=verCode.result.vercode;
        return verCode;
    }
}
