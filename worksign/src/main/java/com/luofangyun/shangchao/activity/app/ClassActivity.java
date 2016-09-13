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
import com.luofangyun.shangchao.activity.message.ClassDetailActivity;
import com.luofangyun.shangchao.activity.message.NewAddActivity;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ClassSetting;
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
 * 班段管理
 */
public class ClassActivity extends BaseActivity {
    private View         view;
    private PullLoadMoreRecyclerView attenSetRlv;
    private MyAdapter    myAdapter;
    private Map<String, String> map = new HashMap<>();
    private ClassSetting classSetting;
    private int j;
    private ArrayList<ClassSetting.Result.Json> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_atten_setting);
        j = 1;
        initView();
        initData();
    }

    private void initView() {
        attenSetRlv = (PullLoadMoreRecyclerView) view.findViewById(R.id.atten_set_rlv);
    }
    private void initData() {
        getServerData(j);
        attenSetRlv.setLinearLayout();
        myAdapter = new MyAdapter();
        attenSetRlv.setAdapter(myAdapter);
        right.setVisibility(View.VISIBLE);
        titleTv.setText("班段管理");
        right.setText("新增");

        attenSetRlv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getServerData(j);
                attenSetRlv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        attenSetRlv.setPullLoadMoreCompleted();
                    }
                }, 500);
            }
            @Override
            public void onLoadMore() {
                getServerData(j++);
                attenSetRlv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        attenSetRlv.setPullLoadMoreCompleted();
                    }
                }, 500);
            }
        });
        flAddress.addView(view);
    }
    private void getServerData(int j) {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "att_time_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pindex", String.valueOf(j));
            map.put("psize",String.valueOf(20));
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
            classSetting = new Gson().fromJson(response.get(), ClassSetting.class);
            dataList = classSetting.result.data;
            System.out.println("班段设置=" + response.get());
            myAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getApplication()).inflate(R.layout
                    .class_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
              holder.classTv.setText(dataList.get(position).timename);
        }

        @Override
        public int getItemCount() {
            if (classSetting != null) {
                return dataList.size();
            }else {
                return 0;
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView classTv;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(new Intent(getApplication(), ClassDetailActivity.class)) ;
                    intent.putExtra("timename", dataList.get(getLayoutPosition()).timename);
                    intent.putExtra("timesb", dataList.get(getLayoutPosition()).timesb);
                    intent.putExtra("timexb", dataList.get(getLayoutPosition()).timexb);
                    intent.putExtra("timecode", dataList.get(getLayoutPosition()).timecode);
                    startActivity(intent);
                }
            });
            classTv = (TextView) itemView.findViewById(R.id.class_tv);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.right:
                startActivity(new Intent(this, NewAddActivity.class));
                break;
            default:
                break;
        }
    }
}
