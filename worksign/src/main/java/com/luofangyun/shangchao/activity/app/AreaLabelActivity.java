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
import com.luofangyun.shangchao.domain.AttArea;
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
 * 区域标签管理
 */

public class AreaLabelActivity extends BaseActivity {
    public  AttArea                        attArea;
    public  ArrayList<AttArea.Result.Json> dataList;
    private View                           view;
    private RecyclerView                   areaLabelRlv;
    private MyAdapter                      myAdapter;
    private Map<String, String> map1 = new HashMap<>();
    private Map<String, String> map2 = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.area_label);
        initView();
        initData();
    }

    private void initView() {
        areaLabelRlv = (RecyclerView) view.findViewById(R.id.area_label_rlv);
    }

    private void initData() {
        titleTv.setText("区域标签管理");
        right.setVisibility(View.VISIBLE);
        right.setText("添加");
        titleTv.setText("区域标签管理");
        getServerData();
        areaLabelRlv.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter();
        areaLabelRlv.setAdapter(myAdapter);
        flAddress.addView(view);
    }

    public void getServerData() {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "att_area_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("pindex", String.valueOf(1));
            map1.put("psize", String.valueOf(10));
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request1.add(map1);
            CallServer.getRequestInstance().add(this, 1, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getServerDeleteData(int i) {
        try {
            Request<String> request2 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "att_area_del.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map2.put("access_id", "1234567890");
            map2.put("timestamp", time);
            map2.put("telnum", UiUtils.getPhoneNumber());
            map2.put("areacode", dataList.get(i).areacode);
            String encode = MD5Encoder.encode(Sign.generateSign(map2) +
                    "12345678901234567890123456789011");
            map2.put("sign", encode);
            request2.add(map2);
            CallServer.getRequestInstance().add(this, 2, request2, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 1:
                    String result1 = response.get();
                    System.out.println("区域标签展示=" + result1);
                    AttArea attArea1 = processAreaData(result1);
                    myAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    String result2 = response.get();
                    System.out.println("区域标签删除了:" + result2);
                    getServerData();
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

    private AttArea processAreaData(String result1) {
        Gson gson = new Gson();
        attArea = gson.fromJson(result1, AttArea.class);
        System.out.println("attArea.status=" + attArea.status);

        if (attArea.status.equals("00000")) {
            dataList = attArea.result.data;
            System.out.println("集合大小：" + attArea.result.data.size());
        }
        return attArea;
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewholder> {
        @Override
        public MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewholder myViewholder = new MyViewholder(LayoutInflater.from(getApplication())
                    .inflate(R.layout.patrol_point_item, parent, false));
            return myViewholder;
        }

        @Override
        public void onBindViewHolder(MyViewholder holder, int position) {
            holder.leftText.setText(dataList.get(position).areaname);
        }

        @Override
        public int getItemCount() {
            if (dataList != null) {
                return dataList.size();
            }
            return 0;
        }
    }

    class MyViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView  leftText;
        ImageView rightPic;

        public MyViewholder(View itemView) {
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
        if (v.getId() == R.id.right) {
            startActivityForResult(new Intent(this, AreaManagerActivity.class), 1);
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