package com.luofangyun.shangchao.activity.message;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.domain.LableInfo;
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
 * 考勤设置→班段管理→班段详情→标签信息
 */

public class LableInfoActivity extends BaseActivity {
    private View view, view1, view2;
    private ArrayList<String> mTitleList = new ArrayList<>();//页卡标题集合
    private ArrayList<View>   mViewList  = new ArrayList<>();   //页卡视图集合
    private TabLayout           mTabLayout;
    private ViewPager           mViewPager;
    private MyPagerAdapter      mAdapter;
    private Map<String, String> map, map1, map2, map3, map4;
    private PullLoadMoreRecyclerView         lableRecy1;
    private PullLoadMoreRecyclerView         lableRecy2;
    private LableInfo                        lableInfo1;
    private LableInfo                        lableInfo2;
    private MyAdapter1                       myAdapter1;
    private MyAdapter2                       myAdapter2;
    private String                           timecode;
    private int                              i;
    private ArrayList<LableInfo.Result.Json> dataList;
    private ArrayList<LableInfo.Result.Json> dataList1;
    private ArrayList<LableInfo.Result.Json> dataList2;
    private ApplyBean                        applyBean2;
    private int                              k, l;
    private ApplyBean                        applyBean1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_meeting, null);
        view1 = View.inflate(this, R.layout.lable_info1, null);
        view2 = View.inflate(this, R.layout.lable_info2, null);
        initView();
        initData();
    }

    private void initView() {
        mTabLayout = (TabLayout) view.findViewById(R.id.meeting_tab);
        mViewPager = (ViewPager) view.findViewById(R.id.meeting_vp);
        lableRecy1 = (PullLoadMoreRecyclerView) view1.findViewById(R.id.lable_recy1);
        lableRecy2 = (PullLoadMoreRecyclerView) view2.findViewById(R.id.lable_recy2);
    }

    private void initData() {
        timecode = getIntent().getStringExtra("timecode");
        map = new HashMap<>();
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        map3 = new HashMap<>();
        map4 = new HashMap<>();
        getServerData1();
        lableRecy1.setLinearLayout();
        myAdapter1 = new MyAdapter1();
        lableRecy1.setAdapter(myAdapter1);
        getServerData2();
        lableRecy2.setLinearLayout();
        myAdapter2 = new MyAdapter2();
        lableRecy2.setAdapter(myAdapter2);
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
        titleTv.setText("考勤标签");
        right.setVisibility(View.GONE);
        right.setText("保存");
        upDownRefurbish();
        flAddress.addView(view);
    }
    private void upDownRefurbish() {
        lableRecy1.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                UiUtils.upDownRefurbish(lableRecy1);
            }

            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(lableRecy1);
            }
        });
        lableRecy2.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                UiUtils.upDownRefurbish(lableRecy2);
            }
            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(lableRecy2);
            }
        });
    }

    private void getServerData1() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "time_label_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("timecode", timecode);
            map.put("opflag", String.valueOf(1));
            map.put("pindex", String.valueOf(1));
            map.put("psize", String.valueOf(200));
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request.add(map);
            CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getServerData2() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "time_label_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("timecode", timecode);
            map.put("opflag", String.valueOf(2));
            map.put("pindex", String.valueOf(1));
            map.put("psize", String.valueOf(200));
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request.add(map);
            CallServer.getRequestInstance().add(this, 2, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAddServer(String labelsName) {
        try {
            Request<String> request3 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "time_label_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map3.put("access_id", "1234567890");
            map3.put("timestamp", time);
            map3.put("telnum", UiUtils.getPhoneNumber());
            map3.put("timecode", timecode);
            map3.put("opflag", "0");
            map3.put("labels", labelsName);
            String encode = MD5Encoder.encode(Sign.generateSign(map3) +
                    "12345678901234567890123456789011");
            map3.put("sign", encode);
            Log.e("考勤标签add",UiUtils.Map2JsonStr(map3));
            request3.add(map3);
            CallServer.getRequestInstance().add(this, 3, request3, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getDeleteServer(String labelsName) {
        try {
            Request<String> request4 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "time_label_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map4.put("access_id", "1234567890");
            map4.put("timestamp", time);
            map4.put("telnum", UiUtils.getPhoneNumber());
            map4.put("timecode", timecode);
            map4.put("opflag", String.valueOf(1));
            map4.put("labels", labelsName);
            String encode = MD5Encoder.encode(Sign.generateSign(map4) +
                    "12345678901234567890123456789011");
            map4.put("sign", encode);
            request4.add(map4);
            CallServer.getRequestInstance().add(this, 4, request4, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 1:
                    Log.e("i == 1", "onSucceed: " + response.get());
                    lableInfo1 = new Gson().fromJson(response.get(), LableInfo.class);   //已选
                    dataList1 = lableInfo1.result.data;
                    myAdapter1.notifyDataSetChanged();
                    break;
                case 2:
                    Log.e("i == 2", "onSucceed: " + response.get());
                    lableInfo2 = new Gson().fromJson(response.get(), LableInfo.class);   //未选
                    dataList2 = lableInfo2.result.data;
                    myAdapter2.notifyDataSetChanged();
                    break;
                case 3:
                    Log.e("add",response.get());
                    getServerData1();
                    getServerData2();
                  /*  applyBean1 = new Gson().fromJson(response.get(), ApplyBean.class);
                    dataList1.remove(k);
                    UiUtils.ToastUtils(applyBean1.summary);
                    myAdapter1.notifyDataSetChanged();
                    myAdapter2.notifyDataSetChanged();*/
                    break;
                case 4:
                    Log.e("delete",response.get());
                    getServerData1();
                    getServerData2();
                /*    applyBean2 = new Gson().fromJson(response.get(), ApplyBean.class);
                    UiUtils.ToastUtils(applyBean2.summary);
                    dataList2.remove(l);
                    myAdapter1.notifyDataSetChanged();
                    myAdapter2.notifyDataSetChanged();*/
                    break;
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };
    private class MyAdapter1 extends RecyclerView.Adapter<MyViewHolder1> {
        @Override
        public MyViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder1(LayoutInflater.from(getApplication()).inflate(R.layout
                    .lable_item1, parent, false));
        }
        @Override
        public void onBindViewHolder(MyViewHolder1 holder, int position) {
            holder.lableItemTv.setText(dataList1.get(position).labelname);
            switch (dataList1.get(position).labeltype) {
                case 0:
                    holder.lableItemIcon.setBackgroundResource(R.drawable.nfc);
                    break;
                case 1:
                    holder.lableItemIcon.setBackgroundResource(R.drawable.blue);
                    break;
                case 2:
                    holder.lableItemIcon.setBackgroundResource(R.drawable.gps);
                    break;
            }
        }

        @Override
        public long getItemId(int position) {
            k = position;
            return position;
        }

        @Override
        public int getItemCount() {
            if (dataList1 != null) {
                return dataList1.size();
            } else {
                return 0;
            }
        }
    }

    class MyViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView lableItemIcon;
        TextView  lableItemTv, lableAdd;
        public MyViewHolder1(View itemView) {
            super(itemView);
            lableItemIcon = (ImageView) itemView.findViewById(R.id.lable_item_icon);
            lableItemTv = (TextView) itemView.findViewById(R.id.lable_item_tv);
            lableAdd = (TextView) itemView.findViewById(R.id.lable_add);
            lableAdd.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.lable_add) {
                getDeleteServer(dataList1.get(getLayoutPosition()).labelcode);
            }
        }
    }

    private class MyAdapter2 extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getApplication()).inflate(R.layout
                    .lable_item2, parent, false));
        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.lableItemTv.setText(dataList2.get(position).labelname);
            switch (dataList2.get(position).labeltype) {
                case 0:
                    holder.lableItemIcon.setBackgroundResource(R.drawable.nfc);
                    break;
                case 1:
                    holder.lableItemIcon.setBackgroundResource(R.drawable.blue);
                    break;
                case 2:
                    holder.lableItemIcon.setBackgroundResource(R.drawable.gps);
                    break;
            }
        }
        @Override
        public long getItemId(int position) {
            l = position;
            return position;
        }

        @Override
        public int getItemCount() {
            if (dataList2 != null) {
                return dataList2.size();
            } else {
                return 0;
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView lableItemIcon;
        TextView  lableItemTv, lableAdd;

        public MyViewHolder(View itemView) {
            super(itemView);
            lableItemIcon = (ImageView) itemView.findViewById(R.id.lable_item_icon);
            lableItemTv = (TextView) itemView.findViewById(R.id.lable_item_tv);
            lableAdd = (TextView) itemView.findViewById(R.id.lable_add);
            lableAdd.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.lable_add) {
                getAddServer(dataList2.get(getLayoutPosition()).labelcode);
            }
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

    /**
     * ViewPager适配器
     */
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
}