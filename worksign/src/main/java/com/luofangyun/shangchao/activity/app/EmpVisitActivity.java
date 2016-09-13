package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.EmpVisit;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.MD5Encoder;
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
import java.util.Map;

/**
 * 应用→工作→客户拜访
 */
public class EmpVisitActivity extends BaseActivity {
    private View                            view;
    private PullLoadMoreRecyclerView                    visitEmp;
    private MyAdapter                       myAdapter;
    private Map<String, String>             map;
    private ArrayList<EmpVisit.Result.Json> dataList;
    private int j = 1;
    private EmpVisit empVisit;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_visit_emp);
        initView();
        initData();
    }

    private void initView() {
        visitEmp = (PullLoadMoreRecyclerView) view.findViewById(R.id.visit_emp);
    }

    private void initData() {
        titleTv.setText("客户拜访");
        right.setText("填写拜访");
        right.setVisibility(View.VISIBLE);
        map = new HashMap<>();
        visitEmp.setLinearLayout();
        myAdapter = new MyAdapter();
        visitEmp.setAdapter(myAdapter);
        getServerData(1);
        upDownRefurbish();
        flAddress.addView(view);
    }

    private void upDownRefurbish() {
        visitEmp.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
               // getServerData(j);
                UiUtils.upDownRefurbish(visitEmp);
            }
            @Override
            public void onLoadMore() {
                if (j < empVisit.result.totalpage) {
                    //getServerData(j);
                  UiUtils.upDownRefurbish(visitEmp);
                } else {
                    UiUtils.ToastUtils("没有更多数据了");
                    UiUtils.upDownRefurbish(visitEmp);
                }
            }
        });
    }
    private void getServerData(int j) {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "emp_visit_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pindex", String.valueOf(j++));
            map.put("psize", String.valueOf(100));
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
            String result = response.get();
            System.out.println("客户拜访result=" + result);
            empVisit = processDate(result);
            myAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    private EmpVisit processDate(String result) {
        Gson gson = new Gson();
        EmpVisit empVisitText = gson.fromJson(result, EmpVisit.class);
        if (empVisitText.status.equals("00000")) {
            dataList = empVisitText.result.data;

        }
        return empVisitText;
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(getApplication())
                    .inflate(R.layout.emp_visit_item, parent, false));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.visitName.setText(dataList.get(position).custom);
            holder.visitTime.setText(dataList.get(position).visittime);
            holder.leaveAddress.setText(dataList.get(position).visitaddress);
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
        TextView visitName, visitTime, leaveAddress;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), EmpVisitDetailActivity.class);
                    intent.putExtra("visitaddress", dataList.get(getLayoutPosition()).visitaddress);
                    intent.putExtra("visittime", dataList.get(getLayoutPosition()).visittime);
                    intent.putExtra("custom", dataList.get(getLayoutPosition()).custom);
                    intent.putExtra("visitsummary", dataList.get(getLayoutPosition()).visitsummary);
                    intent.setAction("EmpVisitDetailActivity");
                    startActivity(intent);
                }
            });
            visitName = (TextView) itemView.findViewById(R.id.visit_name);
            visitTime = (TextView) itemView.findViewById(R.id.visit_time);
            leaveAddress = (TextView) itemView.findViewById(R.id.leave_address);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.right:
                Intent intent = new Intent(this, EmpVisitDetailActivity.class);
                intent.setAction("EmpVisitWriteActivity");
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
