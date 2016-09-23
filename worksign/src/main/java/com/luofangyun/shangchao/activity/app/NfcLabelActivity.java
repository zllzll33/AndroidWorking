package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.luofangyun.shangchao.activity.MyCaptureActivity;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
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
 * NFC标签管理
 */
public class NfcLabelActivity extends BaseActivity {
    public  Label                        lable;
    public  ArrayList<Label.Result.Json> dataList;
    private View                         view;
    private RecyclerView                 nfcLabelRlv;
    private MyAdapter                    myAdapter;
    private Map<String, String> map  = new HashMap<>();
    private Map<String, String> map1 = new HashMap<>();
    private ApplyBean applyBean;
    public static Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.nfc_label);
        handler=new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                getServerData();
            }
        };
        initView();
        initData();
    }

    private void initView() {
        nfcLabelRlv = (RecyclerView) view.findViewById(R.id.nfc_label_rlv);
    }

    public void initData() {
        right.setVisibility(View.VISIBLE);
        right.setText("添加");
        right.setOnClickListener(this);
        titleTv.setText("NFC标签管理");
        getServerData();
        nfcLabelRlv.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter();
        nfcLabelRlv.setAdapter(myAdapter);
        flAddress.addView(view);
    }

    public void getServerData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "label_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pindex", String.valueOf(1));
            map.put("psize", String.valueOf(10));
            map.put("labeltype", String.valueOf(0));
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request.add(map);
            CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 1:
                    String result = response.get();
                    System.out.println("NFC标签管理=" + result);
                    Label label = processNfcData(result);
                    myAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    String result2 = response.get();
                    processDeleteNfcData(result2);
                    System.out.println("删除成功=" + result2);
                    if (applyBean.status.equals("00000")) {
                        getServerData();
                        UiUtils.ToastUtils(applyBean.summary);
                    } else {
                        UiUtils.ToastUtils(applyBean.summary);
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

    private void processDeleteNfcData(String result2) {
        Gson gson = new Gson();
        applyBean = gson.fromJson(result2, ApplyBean.class);
    }

    public Label processNfcData(String result) {
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
            if (dataList.size() != 0) {
                holder.leftText.setText(lable.result.data.get(position).labelname);
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

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
                //TODO:NFC跳转需要做判断
             //   startActivity(new Intent(this, NfcManagerActivity.class));
                startActivityForResult(new Intent(this, MyCaptureActivity.class), 10);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 10) {
            String stringExtra = data.getStringExtra(MyCaptureActivity.EXTRA_RESULT_SUCCESS_STRING);
            System.out.println("扫描的结果为:" + stringExtra);
            int start = stringExtra.indexOf("=");
            String nfcid = stringExtra.substring(start + 1, stringExtra.length());
            Intent intent=new Intent(NfcLabelActivity.this,AddNFCTagActivity.class);
            intent.putExtra("nfcid",nfcid);
            startActivity(intent);
            Log.e("扫描的结果为=====", stringExtra);
        }

    }
}