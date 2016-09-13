package com.luofangyun.shangchao.activity.addresslist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
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
 * 编辑部门名称
 */
public class EditBranchActivity extends BaseActivity {

    private View view;
    private EditText editEt;
    private Map<String, String> map = new HashMap<>();
    private String getCompanycode;
    private String deptname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_edit_branch);
        initView();
        initData();
    }
    private void initView() {
        editEt = (EditText) view.findViewById(R.id.edit_et);
    }
    private void initData() {
        setResult(1);
        deptname = PrefUtils.getString(this, "deptname", null);      //部门名称
        getCompanycode = PrefUtils.getString(this, "getCompanycode", null);
        right.setVisibility(View.VISIBLE);
        right.setText("保存");
        titleTv.setText("编辑部门信息");
        flAddress.addView(view);
    }
    private void getServerData() {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "dept_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("deptname", editEt.getText().toString().trim());
            map.put("parentdept", "0");
            map.put("deptcode", "0");
            map.put("companycode", getCompanycode);
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request1.add(map);
            CallServer.getRequestInstance().add(this, 0, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            System.out.println("编辑部门=" + result);
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.right:
                if (editEt.getText().toString().trim().isEmpty()) {
                    UiUtils.ToastUtils("部门名称不能为空");
                }else {
                    getServerData();
                    PrefUtils.putString(this, "branchName", editEt.getText().toString().trim());
                    Intent intent = new Intent(this, AddBranchActivity.class);
                    intent.putExtra("branchName", editEt.getText().toString().trim());
                    startActivity(intent);
                    finish();
                }
            break;

            default:
                break;
        }
    }
}
