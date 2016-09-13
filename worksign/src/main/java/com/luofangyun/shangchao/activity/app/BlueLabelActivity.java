package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
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
import com.luofangyun.shangchao.domain.Label;
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
 * 蓝牙标签管理
 */
public class BlueLabelActivity extends BaseActivity {
    private View         view;
    private RecyclerView blueLabelRlv;
    private MyAdapter    myAdapter;
    private Map<String, String> map  = new HashMap<>();
    private Map<String, String> map1 = new HashMap<>();
    private Label lable;
    private ArrayList<Label.Result.Json> dataList;
    private int j;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.blue_label);
        initView();
        initData();
    }

    private void initView() {
        blueLabelRlv = (RecyclerView) view.findViewById(R.id.blue_label_rlv);
    }
    private void initData() {
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(this);
        right.setText("添加");
        titleTv.setText("蓝牙标签管理");
        getServerData();
        blueLabelRlv.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter();
        blueLabelRlv.setAdapter(myAdapter);
        flAddress.addView(view);
    }

    private void getServerDeleteData(int i) {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "label_del.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("labelcode", dataList.get(i).labelcode);
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request1.add(map1);
            CallServer.getRequestInstance().add(this, 2, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getServerData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "label_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pindex", String.valueOf(1));
            map.put("psize", String.valueOf(10));
            map.put("labeltype", String.valueOf(1));
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
            switch (what) {
                case 1:
                    String result = response.get();
                    System.out.println("蓝牙标签管理=" + result);
                    Label label = processNfcData(result);
                    myAdapter.notifyDataSetChanged();
                break;
                case 2:
                    System.out.println("删除标签啦：" + response.get());
                    if (j==0) {
                        dataList.remove(0);
                    }else {
                        dataList.remove(j+1);
                    }
                    myAdapter.notifyDataSetChanged();
                    break;
            }

        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    private Label processNfcData(String result) {
        Gson gson = new Gson();
        lable = gson.fromJson(result, Label.class);
        System.out.println("lable.status=" + lable.status);
        if (lable.status.equals("00000")) {
            dataList = lable.result.data;
        }
        return lable;
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
            holder.leftText.setText(lable.result.data.get(position).labelname);
        }

        @Override
        public long getItemId(int position) {
            j = position;
            return position;
        }

        @Override
        public int getItemCount() {
            if (dataList != null) {
                return dataList.size();
            }
            return 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView  leftText;
        ImageView rightPic;

        public MyViewHolder(View itemView) {
            super(itemView);
            leftText = (TextView) itemView.findViewById(R.id.left_text);
            rightPic = (ImageView) itemView.findViewById(R.id.right_pic);
            rightPic.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.right_pic) {
                getServerDeleteData(getPosition());
            }
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.right:
               // startActivity(new Intent(this, BlueManagerActivitiy.class));
                startActivityForResult(new Intent(this, BlueListActivitiy.class), 1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            getServerData();
        }
    }
}
