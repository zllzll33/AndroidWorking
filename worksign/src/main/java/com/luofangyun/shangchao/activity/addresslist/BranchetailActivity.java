package com.luofangyun.shangchao.activity.addresslist;

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
import com.luofangyun.shangchao.domain.Corpor;
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
 * 企业部门详情
 */
public class BranchetailActivity extends BaseActivity {
    private View         view;
    private RecyclerView branchetailRlv, branchHor;
    private MyAdapter myAdapter;
    private Map<String, String> map = new HashMap<>();
    private Corpor                        corpor;
    private ArrayList<Corpor.Result.Emps> dataList;
    private ArrayList<Corpor.Result.Data> deptDataList;
    private static final int TYPE1 = 1, TYPE2 = 2;
    private MyAdapterHor myAdapterHor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_branchetail);
        initView();
        initData();
    }

    private void initView() {
        branchetailRlv = (RecyclerView) view.findViewById(R.id.branchetail_rlv);
        branchHor = (RecyclerView) view.findViewById(R.id.branch_hor);
    }

    private void initData() {
        titleTv.setText("企业通讯录");
        getServerData();
        branchetailRlv.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter();
        branchetailRlv.setAdapter(myAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        branchHor.setLayoutManager(manager);
        myAdapterHor = new MyAdapterHor();
        branchHor.setAdapter(myAdapterHor);
        flAddress.addView(view);
    }

    private void getServerData() {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "dept_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pindex", String.valueOf(1));
            map.put("psize", String.valueOf(20));
            map.put("companycode", PrefUtils.getString(getApplication(), "companycode", null));
            map.put("parentcode", getIntent().getStringExtra("deptcode"));
            map.put("showemp", String.valueOf(1));
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request1.add(map);
            CallServer.getRequestInstance().add(this, 0, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            System.out.println("部门的详细信息=" + result);
            Corpor corpor = processData(result);
            myAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    private Corpor processData(String result) {
        Gson gson = new Gson();
        corpor = gson.fromJson(result, Corpor.class);
        if (corpor.status.equals("00000")) {
            deptDataList = corpor.result.data;
            dataList = corpor.result.emps;
        }
        return corpor;
    }
   private class MyAdapterHor extends RecyclerView.Adapter<MyViewHolder>{
       @Override
       public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           return new MyViewHolder(LayoutInflater.from(getApplication()).inflate(R.layout.hor_item, parent, false));
       }

       @Override
       public void onBindViewHolder(MyViewHolder holder, int position) {
              holder.hor_item_tv.setText(getIntent().getStringExtra("deptname"));
       }

       @Override
       public int getItemCount() {
           return 1;
       }
   }
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView hor_item_tv;
        public MyViewHolder(View itemView) {
            super(itemView);
            hor_item_tv = (TextView) itemView.findViewById(R.id.hor_item_tv);
        }
    }
    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE1) {
                return new MyViewHolder1(LayoutInflater.from(getApplication()).inflate(R.layout
                        .branche_name_item, parent, false));
            } else {
                return new MyViewHolder2(LayoutInflater.from(getApplication()).inflate(R.layout
                        .branche_item, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyViewHolder1) {
                ((MyViewHolder1) holder).brancheTv.setText(deptDataList.get(position).deptname);
            } else if (holder instanceof MyViewHolder2) {
                ((MyViewHolder2) holder).brancheTv.setText(dataList.get(position - deptDataList
                        .size()).empname);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position >= 0 && position < deptDataList.size() ? TYPE1 : TYPE2;
        }

        @Override
        public int getItemCount() {
            if (deptDataList != null && dataList != null) {
                return deptDataList.size() + dataList.size();
            } else if (deptDataList != null && dataList == null) {
                return deptDataList.size();
            } else if (deptDataList == null && dataList != null) {
                return dataList.size();
            }
            return 0;
        }
    }

    class MyViewHolder1 extends RecyclerView.ViewHolder {
        TextView brancheTv;

        public MyViewHolder1(View itemView) {
            super(itemView);
            brancheTv = (TextView) itemView.findViewById(R.id.branche_tv);
        }
    }

    private class MyViewHolder2 extends RecyclerView.ViewHolder {
        ImageView brancheIv, brancheRightIv;
        TextView brancheTv;

        public MyViewHolder2(View inflate) {
            super(inflate);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), IndiviInfoActivity.class);
                    intent.putExtra("empname", dataList.get(getPosition()).empname);
                    intent.putExtra("empcode", dataList.get(getPosition()).empcode);
                    startActivity(intent);
                }
            });
            brancheIv = (ImageView) inflate.findViewById(R.id.branche_iv);
            brancheTv = (TextView) inflate.findViewById(R.id.branche_tv);
            brancheRightIv = (ImageView) inflate.findViewById(R.id.branche_right_iv);
        }
    }
}