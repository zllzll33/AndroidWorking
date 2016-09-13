package com.luofangyun.shangchao.activity.addresslist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.Corpor;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 企业部门
 */
public class CorporBranchActivity1 extends BaseActivity {
    private View         view;
    private RecyclerView corporRlv;
    private MyAdapter    myAdapter;
    private String       addBranchName;
    private ArrayList<String>   list = new ArrayList<>();
    private Map<String, String> map  = new HashMap<>();
    private String companyname;
    private Corpor corpor;
    private ArrayList<Corpor.Result.Data> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(1);
        view = View.inflate(this, R.layout.activity_corpor, null);
        initView();
        initData();
    }

    private void initView() {
        corporRlv = (RecyclerView) view.findViewById(R.id.corpor_rlv);
    }

    private void initData() {
        addBranchName = PrefUtils.getString(this, "addBranchName", null);
        titleTv.setText("企业通讯录");
        getServerData();
        corporRlv.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter();
        corporRlv.setAdapter(myAdapter);
        flAddress.addView(view);
    }
    private void getServerData() {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "dept_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pindex", String.valueOf(1));
            map.put("psize", String.valueOf(100));
            map.put("companycode", PrefUtils.getString(getApplication(), "companycode", null));
            map.put("parentcode", String.valueOf(0));
            map.put("showemp", String.valueOf(1));
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
            System.out.println("企业通讯录=" + result);
            Corpor corpor = processData(result);
            myAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };
    private Corpor processData(String result) {
        Gson gson = new Gson();
        corpor = gson.fromJson(result, Corpor.class);
        if (corpor.status.equals("00000")) {
            dataList = corpor.result.data;
        }
        return corpor;
    }
    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(getApplication())
                    .inflate(R.layout.corpor_item_ver, parent, false));
            return myViewHolder;
        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.corporTv.setText(dataList.get(position).deptname);
        }
        @Override
        public int getItemCount() {
            if (dataList != null) {
                return dataList.size();
            }
            return 0;
        }
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout addressEmptFl1;
        ImageView    corporIv, corporRightIv;
        TextView corporTv;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("我被点击了" + getPosition());
                    PrefUtils.putBoolean(CorporBranchActivity1.this, "addNewBr", true);
                    Intent intent = new Intent(getApplication(), AddBranchActivity.class);
                    intent.setAction("editBranch");
                    Bundle bundle=new Bundle();
                    bundle.putString("parentdept", dataList.get(getPosition()).deptcode);
                    bundle.putString("parentname", dataList.get(getPosition()).deptname);
                    intent.putExtra("bundle",bundle);
                    startActivity(intent);
                    finish();
                }
            });
            addressEmptFl1 = (LinearLayout) itemView.findViewById(R.id.address_empt_fl1);
            corporIv = (ImageView) itemView.findViewById(R.id.corpor_iv);
            corporTv = (TextView) itemView.findViewById(R.id.corpor_tv);
            corporRightIv = (ImageView) itemView.findViewById(R.id.corpor_right_iv);
        }
    }
}