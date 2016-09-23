package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.PatrolRecordDetailBean;
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
 * Created by win7 on 2016/9/19.
 */
public class PatrolRecordDetailActivity extends BaseActivity {
    View view;
    private RecyclerView                      patLineRlv;
  String   patroldate,linecode;
    private PatrolRecordDetailBean                        patrolLine;
    private ArrayList<PatrolRecordDetailBean.Result.Json> dataList;
    private MyAdapter                         myAdapter;
    public static Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view= UiUtils.inflateView(R.layout.activity_patrol_point);
        flAddress.addView(view);
        titleTv.setText("巡检记录详情");
        patLineRlv = (RecyclerView) view.findViewById(R.id.pat_point_rlv);
        patLineRlv.setLayoutManager(new LinearLayoutManager(getApplication()));
        Intent intent=getIntent();
        patroldate=intent.getStringExtra("patroldate");
        linecode=intent.getStringExtra("linecode");
        myAdapter = new MyAdapter();
        patLineRlv.setAdapter(myAdapter);
        handler=new Handler(){
            public void handleMessage(Message msg)
            {
                getServerData();
            }
        };
        getServerData();
    }
    private void getServerData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "patrol_record_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
             Map<String, String> map=new HashMap<>();
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pindex", String.valueOf(1));
            map.put("patroldate",patroldate);
            map.put("linecode", linecode);
            map.put("psize", String.valueOf(200));
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
            System.out.println("巡检线路详情展示=" + result);
            Gson gson = new Gson();
            patrolLine = gson.fromJson(result, PatrolRecordDetailBean.class);
            System.out.println("patrolLine.status=" + patrolLine.status);
            if (patrolLine.status.equals("00000")) {
                dataList = patrolLine.result.data;
                myAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(getApplication())
                    .inflate(R.layout.act_patrol_record_detail, parent, false));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.line_name.setText("巡检点名字:"+patrolLine.result.data.get(position).linename);
            if(patrolLine.result.data.get(position).equals("0"))
            holder.status.setText("巡检状态:未巡检");
            else
                holder.status.setText("巡检状态:已巡检");
            if(patrolLine.result.data.get(position).starttime!=null) {
                holder.start_time.setText(patrolLine.result.data.get(position).starttime);
                holder.end_time.setText(patrolLine.result.data.get(position).endtime);
            }
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
        TextView line_name,status,start_time,end_time;
        public MyViewHolder(View itemView) {
            super(itemView);
            line_name = (TextView) itemView.findViewById(R.id.line_name);
            status = (TextView) itemView.findViewById(R.id.status);
            start_time = (TextView) itemView.findViewById(R.id.start_time);
            end_time = (TextView) itemView.findViewById(R.id.start_time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 Intent intent=new Intent(PatrolRecordDetailActivity.this,BlueListActivitiy.class);
                    PatrolRecordPunchActivty.pointname =dataList.get(getPosition()).pointname;
                    PatrolRecordPunchActivty.lineStatus=dataList.get(getPosition()).status;
                    PatrolRecordPunchActivty.pointcode=dataList.get(getPosition()).pointcode;
                    PatrolRecordPunchActivty.linecode=linecode;
                    intent.setAction("PatrolRecordDetailActivity")  ;
                    startActivity(intent);
                }
            });

        }
    }


}
