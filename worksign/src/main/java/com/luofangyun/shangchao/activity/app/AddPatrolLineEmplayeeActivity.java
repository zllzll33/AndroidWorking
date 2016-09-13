package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.EmplayeeBean;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.MD5Encoder;
import com.luofangyun.shangchao.utils.PrefUtils;
import com.luofangyun.shangchao.utils.Sign;
import com.luofangyun.shangchao.utils.UiUtils;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by win7 on 2016/9/12.
 */
public class AddPatrolLineEmplayeeActivity  extends BaseActivity{
    View view;
    PullLoadMoreRecyclerView pull;
    String companycode,deptcode;
    List<EmplayeeBean.Emplayee> dataList=new ArrayList<>();
    MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.act_add_patrol_line_emplayee);
        initView();
        initData();
    }
    private  void initView()
    {
        titleTv.setText("员工信息");
        flAddress.addView(view);
        pull=(PullLoadMoreRecyclerView)view.findViewById(R.id.pull);
        companycode= PrefUtils.getString(getApplicationContext(),"companycode","");
        deptcode= PrefUtils.getString(getApplicationContext(),"deptcode","");
        pull.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                UiUtils.upDownRefurbish(pull);
            }
            @Override
            public void onLoadMore() {

            }
        });
        pull.setLinearLayout();
        myAdapter = new MyAdapter();
        pull.setAdapter(myAdapter);
    }
    private void emplayeeList()
    {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "emplayee_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String, String> map=new HashMap<>() ;
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pindex","1");
            map.put("psize","100" );
            map.put("companycode",companycode);
            map.put("deptcode",deptcode);
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);

            request.add(map);
            CallServer.getRequestInstance().add(this, 0, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
//            Log.e("员工信息",response.get());
            switch (what) {
                case 0:
                    Gson gson = new Gson();
                    EmplayeeBean emplayeeBean=gson.fromJson(response.get(),EmplayeeBean.class);
                    if (emplayeeBean.status.equals("00000")) {
                        dataList=emplayeeBean.result.data;
                        myAdapter.notifyDataSetChanged();
                    }
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
    private  void initData()
    {
        emplayeeList();
    }
    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(getApplication())
                    .inflate(R.layout.act_emplayees, parent, false));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.empname.setText("姓名:"+dataList.get(position).empname);
            holder.deptname.setText("部门:"+dataList.get(position).deptname);
        }

        @Override
        public int getItemCount() {
            if (dataList != null) {
                return dataList.size();
            }
            return 0;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView empname, deptname;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent();
                    Bundle bundle=new Bundle();
                    bundle.putString("empname",dataList.get(getLayoutPosition()).empname);
                    bundle.putString("empphone",dataList.get(getLayoutPosition()).empphone);
                    intent.putExtra("emp",bundle);
                    setResult(1,intent);
                    finish();
                }
            });
            empname = (TextView) itemView.findViewById(R.id.name);
            deptname = (TextView) itemView.findViewById(R.id.deptname);

        }
    }

}
