package com.luofangyun.shangchao.activity.message;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luofangyun.shangchao.R;

/**
 * 注册成功
 */

public class RegistSucess extends Activity implements View.OnClickListener{
    private ImageView titleIv;
    private TextView titleTv;
    private LinearLayout titleLlBack;
    private TextView registSucess;
    private int i = 3;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (i > 0) {
                registSucess.setText(--i + "");
                handler.sendEmptyMessageDelayed(0, 1000);
            } else if (i == 0) {
                startActivity(new Intent(getApplicationContext(), UserEnterActivity.class));
                finish();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_sucess);
        //查找控件
        initView();
        initData();
    }
    private void initView() {
        titleTv = (TextView) findViewById(R.id.title_tv);
        titleLlBack = (LinearLayout) findViewById(R.id.title_ll_back);
        registSucess = (TextView) findViewById(R.id.regist_sucess_tv_num);
        titleLlBack.setOnClickListener(this);
    }

    private void initData() {
        titleLlBack.setVisibility(View.VISIBLE);
        titleTv.setText("注册");
        handler.sendEmptyMessageDelayed(0, 1000);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_ll_back:
                startActivity(new Intent(getApplicationContext(), UserEnterActivity.class));
            break;
            default:
                break;
        }
    }
}
