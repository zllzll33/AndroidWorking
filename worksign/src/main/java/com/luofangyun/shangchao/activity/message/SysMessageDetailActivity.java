package com.luofangyun.shangchao.activity.message;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.utils.UiUtils;

/**
 * Created by win7 on 2016/9/18.
 */
public class SysMessageDetailActivity extends BaseActivity{
    private View view;
    TextView sys_title,sys_time,sys_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view= UiUtils.inflateView(R.layout.act_sys_message_detail);
        flAddress.addView(view);
        titleTv.setText("系统消息详情");
        sys_title=(TextView)view.findViewById(R.id.sys_title);
        sys_time=(TextView)view.findViewById(R.id.sys_time);
        sys_content=(TextView)view.findViewById(R.id.sys_content);
    }
}
