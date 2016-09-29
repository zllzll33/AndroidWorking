package com.luofangyun.shangchao.activity.addresslist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * 添加部门
 */
public class AddBranchActivity extends BaseActivity {
    private View view;
    private LinearLayout addBranchLl;
    private EditText addBranchName;
    private String branchName,branchCode;
    private TextView addBranchTv;
    private String companyname;
    private String addNewBranchName;

    private String companycode,action,parentdept,parentname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_add_branch, null);
        initView();
        initData();
    }
    private void initView() {
        branchCode="0";
        parentdept="0";
        addBranchLl = (LinearLayout) view.findViewById(R.id.add_branch_ll);
        addBranchName = (EditText) view.findViewById(R.id.add_branch_name);
        addBranchTv = (TextView) view.findViewById(R.id.add_branch_tv);
        companycode = PrefUtils.getString(this, "companycode", "");
        action=getIntent().getAction();
//        addBranchLl.setOnClickListener(this);
        if(action.equals("editBranch")) {
            Bundle bundle = getIntent().getBundleExtra("bundle");
            parentdept=bundle.getString("parentdept");
            parentname=bundle.getString("parentname");
            boolean addNew=bundle.getBoolean("addNewBr");
            if(addNew) {
                titleTv.setText("添加部门");
                parentdept=PrefUtils.getString(getApplicationContext(),"branchCode","");
                parentname=PrefUtils.getString(getApplicationContext(),"branchname","");
                addBranchTv.setText(parentname);
                addBranchLl.setClickable(false);
        /*        addBranchName.setText(branchName);
                String nowname=PrefUtils.getString(getApplicationContext(),"nowname","");
                addBranchName.setText(nowname);*/
            }
            else {
                titleTv.setText("编辑部门");
                branchCode=PrefUtils.getString(getApplicationContext(),"branchCode","");
                branchName=PrefUtils.getString(getApplicationContext(),"branchname","");
                addBranchName.setText(branchName);
            }
        }
        else if(action.equals("addBranch"))
        {
            titleTv.setText("添加部门");
        }
    }
    private void initData() {
        addNewBranchName = addBranchName.getText().toString().trim();           //新部门名称
        if (TextUtils.isEmpty(parentname)) {
            addBranchTv.setText("暂无");
        }else{
            addBranchTv.setText(parentname);
        }

        right.setVisibility(View.VISIBLE);
        right.setText("保存");
        flAddress.addView(view);
    }
    private void getServerData() {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "dept_mng.json", RequestMethod.POST);
            Map<String, String> map = new HashMap<>();
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("parentdept", parentdept);
            map.put("deptname", addBranchName.getText().toString().trim());
            map.put("deptcode", branchCode);
            map.put("companycode", companycode);
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            Log.e("map",UiUtils.Map2JsonStr(map));
            request1.add(map);
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
                   String result0 = response.get();
                   System.out.println("部门信息查看=" + result0);
                   PrefUtils.putString(getApplication(), "addBranchName", addBranchName.getText().toString().trim());        //新部门的名称
                   Intent intent =new Intent(AddBranchActivity.this,CorporActivity.class);
                   startActivity(intent);
                   finish();
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_ll_back:
                finish();
                break;
            case R.id.right:
                    getServerData();   //添加部门
                break;
            case R.id.add_branch_ll:
//                PrefUtils.putString(getApplicationContext(),"nowname",addBranchName.getText().toString().trim());
                startActivityForResult(new Intent(this, CorporBranchActivity1.class), 1);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

        }
    }
}
