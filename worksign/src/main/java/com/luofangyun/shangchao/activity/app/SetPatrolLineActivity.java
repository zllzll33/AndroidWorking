package com.luofangyun.shangchao.activity.app;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.AttPeopBean;
import com.luofangyun.shangchao.domain.SetPatrolLineBean;
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
import java.util.List;
import java.util.Map;

/**
 * Created by win7 on 2016/9/18.
 */
public class SetPatrolLineActivity extends BaseActivity {
    private View view,view1,view2;
    private TabLayout           mTabLayout;
    private PullLoadMoreRecyclerView         lableRecy1;
    private PullLoadMoreRecyclerView         lableRecy2;
    private ArrayList<SetPatrolLineBean.Result.Json> dataList1,dataList2;
    private ViewPager      mViewPager;
    private MyPagerAdapter mAdapter;
    MyAdapter1 myAdapter1;
    MyAdapter2 myAdapter2;
 private SetPatrolLineBean setPatrolLineBean1,setPatrolLineBean2;
    private ArrayList<String> mTitleList = new ArrayList<>();//页卡标题集合
    private ArrayList<View>   mViewList  = new ArrayList<>();   //页卡视图集合
    String linecode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.act_set_patrol_line, null);
        view1 = View.inflate(this, R.layout.lable_info1, null);
        view2 = View.inflate(this, R.layout.lable_info2, null);
        flAddress.addView(view);
        titleTv.setText("设置巡检路线");
        initView();

        lableRecy1.setLinearLayout();
        myAdapter1 = new MyAdapter1();
        lableRecy1.setAdapter(myAdapter1);
        lableRecy2.setLinearLayout();
        myAdapter2 = new MyAdapter2();
        lableRecy2.setAdapter(myAdapter2);
        getServerData1();
        getServerData2();

    }
    private void initView() {
        linecode=getIntent().getStringExtra("linecode");
        mTabLayout = (TabLayout) view.findViewById(R.id.meeting_tab);
        mViewPager = (ViewPager) view.findViewById(R.id.meeting_vp);
        lableRecy1 = (PullLoadMoreRecyclerView) view1.findViewById(R.id.lable_recy1);
        lableRecy2 = (PullLoadMoreRecyclerView) view2.findViewById(R.id.lable_recy2);
        mTabLayout = (TabLayout) view.findViewById(R.id.meeting_tab);
        mViewPager = (ViewPager) view.findViewById(R.id.meeting_vp);
        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        //添加页卡标题
        mTitleList.add("已选");
        mTitleList.add("未选");
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);             //设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0))); //添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        mAdapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(mAdapter);                         //给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);               //将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);            //给Tabs设置适配器

    }
    private void getServerData1() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "line_point_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map1=new HashMap<>();
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("pindex", String.valueOf(1));
            map1.put("psize", String.valueOf(200));
            map1.put("linecode", linecode);
            map1.put("flag", String.valueOf(2));
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request.add(map1);
            CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getServerData2() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "line_point_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map1=new HashMap<>();
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("pindex", String.valueOf(1));
            map1.put("psize", String.valueOf(200));
            map1.put("linecode", linecode);
            map1.put("flag", String.valueOf(1));
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request.add(map1);
            CallServer.getRequestInstance().add(this, 2, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getServer(String opflag,String point) {
        try {
            Request<String> request3 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "line_point_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map3=new HashMap<>();
            map3.put("access_id", "1234567890");
            map3.put("timestamp", time);
            map3.put("telnum", UiUtils.getPhoneNumber());
            map3.put("linecode", linecode);
            map3.put("opflag", opflag);
            map3.put("points", point);
            String encode = MD5Encoder.encode(Sign.generateSign(map3) +
                    "12345678901234567890123456789011");
            map3.put("sign", encode);
//            Log.e("addmap",UiUtils.Map2JsonStr(map3));
            request3.add(map3);
            CallServer.getRequestInstance().add(this, 3, request3, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 1:
//                    Log.e("巡检已选", "onSucceed: " + response.get());
                    setPatrolLineBean1 = new Gson().fromJson(response.get(), SetPatrolLineBean.class);
                    dataList1 = setPatrolLineBean1.result.data;
                    myAdapter1.notifyDataSetChanged();
                    break;
                case 2:
//                    Log.e("巡检未选", "onSucceed: " + response.get());
                    setPatrolLineBean2 = new Gson().fromJson(response.get(), SetPatrolLineBean.class);
                    dataList2 = setPatrolLineBean2.result.data;
                    myAdapter2.notifyDataSetChanged();
                    break;
                case 3:
//                    Log.e("巡检地点add",response.get());
                    getServerData1();
                    getServerData2();
                    break;
                case 4:
//                    Log.e("巡检地点delete",response.get());
                    getServerData1();
                    getServerData2();
                    break;
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };
    class MyPagerAdapter extends PagerAdapter {
        List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();    //页卡数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));       //添加页卡
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));   //删除页卡
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);                 //页卡标题
        }
    }
    private class MyAdapter1 extends RecyclerView.Adapter<MyViewHolder1> {
        @Override
        public MyViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder1(LayoutInflater.from(getApplication()).inflate(R.layout
                    .att_peop_item1, parent, false));
        }
        @Override
        public void onBindViewHolder(MyViewHolder1 holder, int position) {
            holder.attPeopItemTv.setText(dataList1.get(position).pointname);
            holder.attPeopPhone.setVisibility(View.GONE);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            if (dataList1 != null) {
                return dataList1.size();
            }
            return 0;
        }
    }
    class MyViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView attPeopItemTv, attPeopPhone, attPeopAdd;

        public MyViewHolder1(View itemView) {
            super(itemView);
            attPeopItemTv = (TextView) itemView.findViewById(R.id.att_peop_item_tv);
            attPeopPhone = (TextView) itemView.findViewById(R.id.att_peop_phone);
            attPeopAdd = (TextView) itemView.findViewById(R.id.att_peop_add);
            attPeopAdd.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.att_peop_add) {        //删除
                getServer("1",dataList1.get(getLayoutPosition()).pointcode);
            }
        }
    }
    private class MyAdapter2 extends RecyclerView.Adapter<MyViewHolder2> {
        @Override
        public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder2(LayoutInflater.from(getApplication()).inflate(R.layout
                    .att_peop_item2, parent, false));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder2 holder, int position) {
            holder.attPeopItemTv.setText(dataList2.get(position).pointname);
            holder.attPeopPhone.setVisibility(View.GONE);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            if (dataList2 != null) {
                return dataList2.size();
            }
            return 0;
        }
    }

    class MyViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox attPeopItemIcon;
        TextView attPeopItemTv, attPeopPhone, attPeopAdd;
        public MyViewHolder2(View itemView) {
            super(itemView);
            attPeopItemIcon = (CheckBox) itemView.findViewById(R.id.att_peop_item_icon);
            attPeopItemTv = (TextView) itemView.findViewById(R.id.att_peop_item_tv);
            attPeopPhone = (TextView) itemView.findViewById(R.id.att_peop_phone);
            attPeopAdd = (TextView) itemView.findViewById(R.id.att_peop_add);
            attPeopAdd.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.att_peop_add) {
                //添加
                getServer("0",dataList2.get(getLayoutPosition()).pointcode);
            }
        }
    }
}
