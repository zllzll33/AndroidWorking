package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.PatrolPoint;
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
 * 巡检点设置
 */
public class PatrolPointActivity extends BaseActivity {
    private View                view;
    private RecyclerView        patPointRlv;
    private Map<String, String> map;
    private PatrolPoint         patrolPoint;
    private ArrayList<PatrolPoint.Result.Json> dataList;
    private MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_patrol_point);
        initView();
        initData();
    }
    private void initView() {
        patPointRlv = (RecyclerView) view.findViewById(R.id.pat_point_rlv);
    }
    private void initData() {
        map = new HashMap<>();
        getServerData();
        patPointRlv.setLayoutManager(new LinearLayoutManager(getApplication()));
        myAdapter = new MyAdapter();
        patPointRlv.setAdapter(myAdapter);
        titleTv.setText("巡检点");
        right.setVisibility(View.VISIBLE);
        right.setText("添加");
        flAddress.addView(view);
        right.setOnClickListener(this);
    }
    private void getServerData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "patrol_point_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pindex", String.valueOf(1));
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
    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.right:
                Intent intent=new Intent(PatrolPointActivity.this,AddPatrolPointActivity.class);
                intent.setAction("addPatrolPoint");
                startActivityForResult(intent,1);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        getServerData();
    }
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {

            switch (what)
            {
                case 0:
                    String result = response.get();
                    System.out.println("巡检点展示=" + result);
                    PatrolPoint patrolPoint = processData(result);
                    myAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    getServerData();
                    break;
                default:break;
            }

        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    private PatrolPoint processData(String result) {
        Gson gson = new Gson();
        patrolPoint = gson.fromJson(result, PatrolPoint.class);
        System.out.println("patrolPoint.status=" + patrolPoint.status);
        if (patrolPoint.status.equals("00000")) {
            dataList = patrolPoint.result.data;
        }
        return patrolPoint;
    }
private void delete(String  pointcode)
{
    try {
        Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                "patrol_point_del.json", RequestMethod.POST);
        String time = Long.toString(new Date().getTime());
        map.put("access_id", "1234567890");
        map.put("timestamp", time);
        map.put("telnum", UiUtils.getPhoneNumber());
        map.put("pointcode",pointcode );
        String encode = MD5Encoder.encode(Sign.generateSign(map) +
                "12345678901234567890123456789011");
        map.put("sign", encode);
        request.add(map);
        CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
    } catch (Exception e) {
        e.printStackTrace();
    }
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
               holder.leftText.setText(patrolPoint.result.data.get(position).pointname);
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
            rightPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(dataList.get(getPosition()).pointcode);
                }
            });
            leftText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(PatrolPointActivity.this,AddPatrolPointActivity.class);
                    intent.setAction("editPatrolPoint");
                    Bundle bundle=new Bundle();
                    bundle.putString("pointcode",dataList.get(getPosition()).pointcode);
                    bundle.putString("pointname",dataList.get(getPosition()).pointname);
                    bundle.putString("labelcode",dataList.get(getPosition()).labelcode);
                    bundle.putString("labelname",dataList.get(getPosition()).labelname);
                    intent.putExtra("point",bundle);
                    startActivityForResult(intent,1);
                }
            });

        }
    }
}
