package com.luofangyun.shangchao.activity.message;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.luofangyun.shangchao.R;

/**
 * 忘记密码页面
 */
public class forgetPasswordActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        //查找相关的控件
        initView();
        //添加数据
        initData();
    }
    private void initView() {

    }
    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            default:
                break;
        }
    }
}
