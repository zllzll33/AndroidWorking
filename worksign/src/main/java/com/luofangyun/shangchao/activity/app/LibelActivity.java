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
import com.luofangyun.shangchao.domain.LabelBean;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LibelActivity extends BaseActivity {
    private View      view;
    private MyAdapter myAdapter;
    private Map<String, String> map = new HashMap<>();
    private LabelBean                labelBean;
    private PullLoadMoreRecyclerView labelRecy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_libel);
        initView();
        intiData();
    }

    private void initView() {
        labelRecy = (PullLoadMoreRecyclerView) view.findViewById(R.id.label_recy);
    }

    private void intiData() {
        String meetEt1 = getIntent().getStringExtra("meetEt1");
        String meetEt2 = getIntent().getStringExtra("meetEt2");
        String meetEt3 = getIntent().getStringExtra("meetEt3");
        String meetEt4 = getIntent().getStringExtra("meetEt4");
        String meetEt5 = getIntent().getStringExtra("meetEt5");
        getServerLabel();
        labelRecy.setLinearLayout();
        myAdapter = new MyAdapter();
        labelRecy.setAdapter(myAdapter);
        titleTv.setText("标签选择");
        flAddress.addView(view);
    }

    private void getServerLabel() {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "patrol_label_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request1.add(map);
            CallServer.getRequestInstance().add(this, 1, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            System.out.println("标签数据为：" + response.get());
            labelBean = new Gson().fromJson(response.get(), LabelBean.class);
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
                    .label_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.labelTv1.setText(labelBean.result.get(position).labelname);
            if (labelBean.result.get(position).labeltype == 0) {
                holder.labelTv2.setText("NFC标签");
            } else if (labelBean.result.get(position).labeltype == 1) {
                holder.labelTv2.setText("蓝牙标签");
            }
        }

        @Override
        public int getItemCount() {
            if (labelBean != null) {
                return labelBean.result.size();
            } else {
                return 0;
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView labelTv1, labelTv2;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrefUtils.putString(getApplication(), "labelname", labelBean.result.get(getLayoutPosition()).labelname);
                    PrefUtils.putString(getApplication(), "lablecode", labelBean.result.get(getLayoutPosition()).labelcode);
                    setResult(1);
                    finish();
                }
            });
            labelTv1 = (TextView) itemView.findViewById(R.id.label_tv1);
            labelTv2 = (TextView) itemView.findViewById(R.id.label_tv2);
        }
    }
}
