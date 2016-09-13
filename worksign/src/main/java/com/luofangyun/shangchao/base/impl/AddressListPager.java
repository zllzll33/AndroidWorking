package com.luofangyun.shangchao.base.impl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.addresslist.AddressListActivity;
import com.luofangyun.shangchao.activity.addresslist.CompanyAccede;
import com.luofangyun.shangchao.activity.addresslist.CompanyAdd;
import com.luofangyun.shangchao.activity.addresslist.CorporActivity;
import com.luofangyun.shangchao.activity.addresslist.IndiviInfoActivity;
import com.luofangyun.shangchao.activity.message.ConfirmActivity;
import com.luofangyun.shangchao.base.BasePager;
import com.luofangyun.shangchao.domain.ConnenctBean;
import com.luofangyun.shangchao.domain.SearchBean;
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
import java.util.List;
import java.util.Map;

/**
 * 通讯录
 */
public class AddressListPager extends BasePager implements View.OnClickListener {
    private LinearLayout addressFl1, addressLl, addressList;
    private AlertDialog dialogAddress;
    private TextView    esablish, accede, addressTvTitle, llTvTitle, llTvTitle2, tvDownText2;
    private String   companyNameText, companyname, getCompanycode;
    private Map<String, String> map = new HashMap<>();
    private Map<String, String> map1 = new HashMap<>();
    private SearchBean searchBean;
    ConnenctBean connenctBean;
    private String addBranchName;
    private RecyclerView addressListRlv;
    private MyAdapter myAdapter;
    List<ConnenctBean.Result> dateList=new ArrayList<>();
    public static Handler handler;
    public AddressListPager(Activity activity) {
        super(activity);
    }
    @Override
    public void initData() {
        handler=new Handler()
        {
           public void handleMessage(Message msg)
           {
               getserverTelData();
           }
        };
        companyname = PrefUtils.getString(mActivity, "companyname", null);        //企业名称
        getServerData();
        getserverTelData();
        companyNameText = PrefUtils.getString(mActivity, "companyNameText", null);
        addBranchName = PrefUtils.getString(mActivity, "addBranchName", null);                      //新部门的名称
        View view = View.inflate(mActivity, R.layout.address_list_empt, null);
        llTvTitle = (TextView) view.findViewById(R.id.ll_tv_title);
        addressFl1 = (LinearLayout) view.findViewById(R.id.address_empt_fl1);
        addressList = (LinearLayout) view.findViewById(R.id.address_list);
        addressTvTitle = (TextView) view.findViewById(R.id.address_tv_title);
        addressLl = (LinearLayout) view.findViewById(R.id.address_ll);
        view.findViewById(R.id.papger_sign).setOnClickListener(this);
        addressListRlv = (RecyclerView) view.findViewById(R.id.address_list_rlv);
        addressListRlv.setLayoutManager(new LinearLayoutManager(mActivity));
        myAdapter = new MyAdapter();
        addressListRlv.setAdapter(myAdapter);
        this.llTvTitle = (TextView) view.findViewById(R.id.ll_tv_title);
        addressList.setOnClickListener(this);
        addressFl1.setOnClickListener(this);
        tvTitle.setText("通讯录");
        titleBack.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(companyname)) {
            addressTvTitle.setVisibility(View.GONE);
            addressLl.setVisibility(View.VISIBLE);
            this.llTvTitle.setText(this.companyname);
        }
        flContainer.addView(view);
    }

    private void getserverTelData() {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "emp_favorite.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request1.add(map1);
            CallServer.getRequestInstance().add(mActivity, 2, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getServerData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "company_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("companyname", llTvTitle.getText().toString().trim());
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request.add(map);
            CallServer.getRequestInstance().add(mActivity, 1, request, httpListener, false, false);
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
                    SearchBean searchBean = processData(result);
                break;
                case 2:
                    String result2 = response.get();
                    System.out.println("常用联系人=" + result2);
                    cprocessData(result2);
                default:
                    break;
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };
    private SearchBean processData(String result) {
        Gson gson = new Gson();
        searchBean = gson.fromJson(result, SearchBean.class);
        if (searchBean.result.size() != 0) {
            getCompanycode = searchBean.result.get(0).companycode;
        }
        System.out.println("getCompanycode=" + getCompanycode);
        PrefUtils.putString(mActivity, "getCompanycode", getCompanycode);
        return searchBean;
    }
    private ConnenctBean cprocessData(String result) {
        Gson gson = new Gson();
        connenctBean=gson.fromJson(result, ConnenctBean.class);
        if(connenctBean.result.size()!=0)
        {
            dateList=connenctBean.result;
            myAdapter.notifyDataSetChanged();

        }
        return connenctBean;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.address_empt_fl1:
                if (llTvTitle.getText().toString().trim().isEmpty()) {
                    dialogAddress = UiUtils.getDialog(mActivity, R.layout.address_dialog);
                    esablish = (TextView) dialogAddress.findViewById(R.id.establish);
                    accede = (TextView) dialogAddress.findViewById(R.id.accede);
                    esablish.setOnClickListener(this);
                    accede.setOnClickListener(this);
                } else {
                    Intent intent = new Intent(mActivity, CorporActivity.class);
                    mActivity.startActivity(intent);
                }
                break;
            case R.id.establish:
                mActivity.startActivity(new Intent(mActivity, CompanyAdd.class));
                dialogAddress.dismiss();
                break;
            case R.id.accede:
                mActivity.startActivity(new Intent(mActivity, CompanyAccede.class));
                dialogAddress.dismiss();
                break;
            case R.id.address_list:
                Intent intent=new Intent(mActivity, AddressListActivity.class);
                mActivity.startActivity(intent);
                break;
            case R.id.papger_sign:
                mActivity.startActivity(new Intent(mActivity, ConfirmActivity.class));
                break;
            default:
                break;
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(mActivity).inflate(R
                    .layout.list_item, parent, false));
            return myViewHolder;
        }
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.listTv1.setText(dateList.get(position).empname);
            holder.listTv2.setText(dateList.get(position).empphone);
            holder.index=position;
            holder.ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mActivity, IndiviInfoActivity.class);
                    intent.putExtra("empname", dateList.get(holder.index).empname);
                    intent.putExtra("empcode", dateList.get(holder.index).empcode);
                    mActivity.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dateList.size();
        }
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView listTv1, listTv2;
        LinearLayout ll_item;
        int index;
        public MyViewHolder(View itemView) {
            super(itemView);
            listTv1 = (TextView) itemView.findViewById(R.id.list_tv1);
            listTv2 = (TextView) itemView.findViewById(R.id.list_tv2);
            ll_item=(LinearLayout)itemView.findViewById(R.id.ll_item);
        }
    }
}

