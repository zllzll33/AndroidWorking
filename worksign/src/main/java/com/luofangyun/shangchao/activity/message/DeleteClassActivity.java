package com.luofangyun.shangchao.activity.message;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luofangyun.shangchao.R;

/**
 * 删除该班段
 */
public class DeleteClassActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView                titleTv;
    private LinearLayout            titleLlBack;
    private TextView                right;
    private TextView                deleteClass;
    private android.app.AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_class);
        initView();
        initData();
    }

    private void initView() {
        titleTv = (TextView) findViewById(R.id.title_tv);
        titleLlBack = (LinearLayout) findViewById(R.id.title_ll_back);
        right = (TextView) findViewById(R.id.right);
        deleteClass = (TextView) findViewById(R.id.delete_class);
        titleTv.setVisibility(View.GONE);
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(this);
        deleteClass.setOnClickListener(this);
        titleLlBack.setOnClickListener(this);
    }

    private void initData() {
        right.setText("保存");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right:
                startActivity(new Intent(this, NewAddActivity.class));
                break;
            case R.id.title_ll_back:
               startActivity(new Intent(this, NewAddActivity.class));
            break;
            case R.id.delete_class:
                dialog = new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_TRADITIONAL).create();     //AlertDialog.THEME_TRADITIONAL表示默认的背景为透明
                dialog.show();
                dialog.setContentView(R.layout.delete_layout);
                TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
                TextView noCancel = (TextView) dialog.findViewById(R.id.nocancel);
                cancel.setOnClickListener(this);
                noCancel.setOnClickListener(this);
                break;
            case R.id.cancel:
                startActivity(new Intent(this, NewAddActivity.class));
            case R.id.nocancel:
                dialog.dismiss();
            default:
                break;
        }
    }
}
