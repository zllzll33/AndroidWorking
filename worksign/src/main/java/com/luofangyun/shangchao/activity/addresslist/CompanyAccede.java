package com.luofangyun.shangchao.activity.addresslist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
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
import java.util.Map;
/**
 * 加入团队搜索界面
 */
public class CompanyAccede extends BaseActivity {
    private View                         view;
    private ImageView                    search;
    private EditText                     ediText;
    private String                       findEdiText, phoneNumber, companycode;
    private Map<String, String>          map;
    private RecyclerView                 recyaccedeRlv;
    private SearchBean                   searchBean;
    private ArrayList<SearchBean.Result> dataList;
    private MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_company_accede);
        phoneNumber = PrefUtils.getString(this, "phoneNumber", null);
        companycode = PrefUtils.getString(this, "companycode", null);
        initView();
        initData();
    }

    private void initView() {
        ediText = (EditText) view.findViewById(R.id.editText);
        search = (ImageView) view.findViewById(R.id.search);
        recyaccedeRlv = (RecyclerView) view.findViewById(R.id.recyaccede);
        search.setOnClickListener(this);
    }
    private void initData() {
        titleTv.setText("搜索");
        map = new HashMap<>();
        flAddress.addView(view);
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.search:
                findEdiText = this.ediText.getText().toString().trim();
                myAdapter = new MyAdapter();
                getServerData(phoneNumber, findEdiText);
                recyaccedeRlv.setLayoutManager(new LinearLayoutManager(getApplication()));
                recyaccedeRlv.setAdapter(myAdapter);
                break;
            default:
                break;
        }
    }
    private void getServerData( String phoneNumber, String findEdiText) {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "company_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", phoneNumber);
            map.put("companyname", findEdiText);
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
            String result = response.get();
            SearchBean searchBean = processData(result);

            myAdapter.notifyDataSetChanged();


        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };

    private SearchBean processData(String json) {
        Gson gson = new Gson();
        searchBean = gson.fromJson(json, SearchBean.class);
        dataList = searchBean.result;
        return searchBean;
    }
    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from
                    (getApplicationContext()).inflate(R.layout.search_item, parent, false));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
              holder.leftText.setText(dataList.get(position).companyname);
        }

        @Override
        public int getItemCount() {
            if (dataList != null) {
                return dataList.size();
            } else {
                return 0;
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView leftText;
        ImageView rightPic;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), CompanyApply.class);
                    System.out.println("返回的companyname=" + searchBean.result.get(getPosition()).companyname);
                    System.out.println("返回的companycode=" + searchBean.result.get(getPosition()).companycode);
                    intent.putExtra("companyname", searchBean.result.get(getPosition()).companyname);
                    intent.putExtra("companycode", searchBean.result.get(getPosition()).companycode);
                    startActivity(intent);
                }
            });
            leftText = (TextView) itemView.findViewById(R.id.left_text);
            rightPic = (ImageView) itemView.findViewById(R.id.right_pic);
        }
    }
}
