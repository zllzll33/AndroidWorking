package com.luofangyun.shangchao.activity.app;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ReportDetail;
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

public class PeopDetailActivity extends BaseActivity {
    private View                     view;
    private PullLoadMoreRecyclerView peopDetail;
    private MyAdapter                myAdapter;
    private Map<String, String> map = new HashMap<>();
    private int           attstatu;
    private String        repdate;
    private ReportDetail  reportDetail;
    private ArrayList<ReportDetail.Result.Json> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_peop_detail);
        initView();
        initData();
    }

    private void initView() {
        peopDetail = (PullLoadMoreRecyclerView) view.findViewById(R.id.peop_detail);
    }

    private void initData() {
        attstatu = getIntent().getIntExtra("attstatu", 0);
        repdate = getIntent().getStringExtra("repdate");
        getServerData();
        peopDetail.setLinearLayout();
        myAdapter = new MyAdapter();
        peopDetail.setAdapter(myAdapter);
        titleTv.setText("查看人员详情");
        upDownRefurbish();
        flAddress.addView(view);
    }

    private void upDownRefurbish() {
        peopDetail.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                 UiUtils.upDownRefurbish(peopDetail);
            }

            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(peopDetail);
            }
        });
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getApplication()).inflate(R.layout
                    .peop_detail_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.peopDetailTv1.setText(dataList.get(position).empname);
            holder.peopDetailTv2.setText(dataList.get(position).deptcode);
            holder.peopDetailTv3.setText(dataList.get(position).empphone);
            holder.peopDetailTv4.setText(dataList.get(position).deptcode);
        }

        @Override
        public int getItemCount() {
            if (dataList != null) {
                return dataList.size();
            } else {
                return 0;
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView peopDetailTv1, peopDetailTv2, peopDetailTv3, peopDetailTv4;

        public MyViewHolder(View itemView) {
            super(itemView);
            peopDetailTv1 = (TextView) itemView.findViewById(R.id.peop_detail_tv1);
            peopDetailTv2 = (TextView) itemView.findViewById(R.id.peop_detail_tv2);
            peopDetailTv3 = (TextView) itemView.findViewById(R.id.peop_detail_tv3);
            peopDetailTv4 = (TextView) itemView.findViewById(R.id.peop_detail_tv4);
        }
    }

    private void getServerData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "att_report_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("repdate", repdate);
            map.put("pindex", String.valueOf(1));
            map.put("psize", String.valueOf(20));
            map.put("attstatu", String.valueOf(attstatu));
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request.add(map);
            CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            reportDetail = new Gson().fromJson(response.get(), ReportDetail.class);
            dataList = reportDetail.result.data;
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };
}
