package com.luofangyun.shangchao.base.impl;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.PatrolLine;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.MD5Encoder;
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
 * 巡检父类
 */
public class BasePatrolActivity extends BaseActivity {
    private View                              view;
    private RecyclerView                      patLineRlv;
    private Map<String, String>               map;
    private PatrolLine                        patrolLine;
    private ArrayList<PatrolLine.Result.Json> dataList;
    private MyAdapter                         myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_patrol_point);
        initView();
        initData();
    }

    private void initView() {
        patLineRlv = (RecyclerView) view.findViewById(R.id.pat_point_rlv);
    }

    private void initData() {
        map = new HashMap<>();
        getServerData();
        patLineRlv.setLayoutManager(new LinearLayoutManager(getApplication()));
        myAdapter = new MyAdapter();
        patLineRlv.setAdapter(myAdapter);
        titleTv.setText("巡检线路管理");
        right.setVisibility(View.VISIBLE);
        right.setText("添加");
        flAddress.addView(view);
    }

    private void getServerData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "patrol_line_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pindex", String.valueOf(1));
            map.put("psize", String.valueOf(10));
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
            System.out.println("巡检线路展示=" + result);
            PatrolLine patrolLine = processData(result);
            myAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    private PatrolLine processData(String result) {
        Gson gson = new Gson();
        patrolLine = gson.fromJson(result, PatrolLine.class);
        System.out.println("patrolLine.status=" + patrolLine.status);
        if (patrolLine.status.equals("00000")) {
            dataList = patrolLine.result.data;
        }
        return patrolLine;
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(getApplication())
                    .inflate(R.layout.patrol_point_item, parent, false));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.leftText.setText(patrolLine.result.data.get(position).linename);
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
        TextView  leftText;
        ImageView rightPic;
        public MyViewHolder(View itemView) {
            super(itemView);
            leftText = (TextView) itemView.findViewById(R.id.left_text);
            rightPic = (ImageView) itemView.findViewById(R.id.right_pic);
        }
    }
}