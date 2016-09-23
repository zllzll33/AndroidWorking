package com.luofangyun.shangchao.activity.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.MainActivity;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 用户登录
 */
public class UserEnterActivity extends Activity implements View.OnClickListener {
    private EditText etUrerNum, etUrerPassword;
    private TextView userEnter, forgetPassword, userLogin, titleTv;
    private Map<String, String> map1;
    private UerEnBean           uerEnBean;
    private String              encode;
    private String status;
    private String statusData,pw;
    private String enterPhoneNumber;
    private String enterPassWord;
    private LinearLayout titleLlBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_enter);
        /**
         * 个推初始化
         */
        PushManager.getInstance().initialize(this.getApplicationContext());
        map1 = new HashMap<>();
        //查找相关的控件
        initView();
        //设置数据
        initData();
    }

    private void initView() {
        etUrerNum = (EditText) findViewById(R.id.et_urer_num);               //帐号
        etUrerPassword = (EditText) findViewById(R.id.et_urer_password);     //密码
        userEnter = (TextView) findViewById(R.id.user_enter);                //用户登录
        forgetPassword = (TextView) findViewById(R.id.forget_password);      //忘记密码
        userLogin = (TextView) findViewById(R.id.user_login);                //注册
        titleTv = (TextView) findViewById(R.id.title_tv);                    //用户登录title
        titleLlBack = (LinearLayout) findViewById(R.id.title_ll_back);
        userEnter.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        userLogin.setOnClickListener(this);
    }
    private void initData() {
        titleLlBack.setVisibility(View.GONE);
        titleTv.setText("用户登录");
    }

    /**
     * 获取网络数据
     */
    private void getServerData(String phone, String passWord) {
        try {
            pw=passWord;
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "user_login.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", phone);
            map1.put("password", MD5Encoder.encode(passWord));
            encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request1.add(map1);
            CallServer.getRequestInstance().add(this, 0, request1, httpListener, false, true);
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
            String result = response.get();
            System.out.println("登录result=" + result);
            UerEnBean userLogBean = processData(result);
            if (userLogBean.status.equals("00001")) {
                UiUtils.ToastUtils("帐号或密码错误，请重新输入");
            } else {
                status = userLogBean.status;
            }
            System.out.println("status=" + status);
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

        }
    };
    //解析数据
    protected UerEnBean processData(String json) {
        Gson gson = new Gson();
        uerEnBean = gson.fromJson(json, UerEnBean.class);
        System.out.println("status用户点击登录了processData" + uerEnBean.status);
        System.out.println("userLogBean" + uerEnBean);
        statusData = uerEnBean.status;
        if (statusData.equals("00000")) {
            PrefUtils.putString(getApplication(), "sw",pw);
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
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        return uerEnBean;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_enter:   //用户登录
                enterPhoneNumber = etUrerNum.getText().toString().trim();
                enterPassWord = etUrerPassword.getText().toString().trim();
                if (enterPhoneNumber.isEmpty() || enterPassWord.isEmpty()) {
                    UiUtils.ToastUtils("帐号密码不能为空");
                } else {
                    getServerData(enterPhoneNumber, enterPassWord);
                }
                break;
            case R.id.forget_password:    //忘记密码
                startActivity(new Intent(getApplicationContext(), FindPasswordActivity.class));
                break;
            case R.id.user_login:         //注册
                startActivity(new Intent(getApplicationContext(), UserLoginActivity.class));
                break;
            default:
                break;
        }
    }
}