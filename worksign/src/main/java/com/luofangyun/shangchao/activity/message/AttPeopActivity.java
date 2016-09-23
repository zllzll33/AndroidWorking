package com.luofangyun.shangchao.activity.message;

import android.os.Bundle;
import android.os.Handler;
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
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.domain.AttPeopBean;
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
 * 考勤人员
 */
public class AttPeopActivity extends BaseActivity {
    private View view, view1, view2;
    private ArrayList<String> mTitleList = new ArrayList<>();//页卡标题集合
    private ArrayList<View>   mViewList  = new ArrayList<>();   //页卡视图集合
    private TabLayout      mTabLayout;
    private ViewPager      mViewPager;
    private MyPagerAdapter mAdapter;
    private Map<String, String> map1 = new HashMap<>();
    private Map<String, String> map2 = new HashMap<>();
    private Map<String, String> map3 = new HashMap<>();
    private Map<String, String> map4 = new HashMap<>();
    private String      timecode;
    private AttPeopBean attPeopBean1, attPeopBean2;
    private ArrayList<AttPeopBean.Result.Json> dataList1;
    private ArrayList<AttPeopBean.Result.Json> dataList2;
    private PullLoadMoreRecyclerView           attPeopRecy1;
    private PullLoadMoreRecyclerView           attPeopRecy2;
    private MyAdapter1                         myAdapter1;
    private MyAdapter2                         myAdapter2;
    private TextView                           attPeopChoice;
    private boolean                            isChecked;
    private int                                i, j;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_meeting, null);
        view1 = View.inflate(this, R.layout.att_peop1, null);
        view2 = View.inflate(this, R.layout.att_peop2, null);
        initView();
        initData();
    }

    private void initView() {
        attPeopRecy1 = (PullLoadMoreRecyclerView) view1.findViewById(R.id.att_peop_recy1);
        attPeopRecy2 = (PullLoadMoreRecyclerView) view2.findViewById(R.id.att_peop_recy2);
        attPeopChoice = (TextView) view2.findViewById(R.id.att_peop_choice);
    }

    private void initData() {
        timecode = getIntent().getStringExtra("timecode");
        mTabLayout = (TabLayout) view.findViewById(R.id.meeting_tab);
        mViewPager = (ViewPager) view.findViewById(R.id.meeting_vp);
        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        //添加页卡标题
        mTitleList.add("已排班");
        mTitleList.add("未排班");
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);             //设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0))); //添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        mAdapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(mAdapter);                         //给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);               //将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);            //给Tabs设置适配器
        titleTv.setText("考勤人员");
        right.setVisibility(View.GONE);
        right.setText("保存");
        getServerData1();
        attPeopRecy1.setLinearLayout();
        myAdapter1 = new MyAdapter1();
        attPeopRecy1.setAdapter(myAdapter1);
        getServerData2();
        attPeopRecy2.setLinearLayout();
        myAdapter2 = new MyAdapter2();
        attPeopRecy2.setAdapter(myAdapter2);
        upDownRefurbish();
        flAddress.addView(view);
    }

    private void upDownRefurbish() {
        attPeopRecy1.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                UiUtils.upDownRefurbish(attPeopRecy1);
            }

            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(attPeopRecy1);
            }
        });
        attPeopRecy2.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                UiUtils.upDownRefurbish(attPeopRecy2);
            }
            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(attPeopRecy2);
            }
        });
    }

    private void getServerData1() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "time_emp_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("pindex", String.valueOf(1));
            map1.put("psize", String.valueOf(20));
            map1.put("timecode", timecode);
            map1.put("opflag", String.valueOf(1));
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
                    "time_emp_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map2.put("access_id", "1234567890");
            map2.put("timestamp", time);
            map2.put("telnum", UiUtils.getPhoneNumber());
            map2.put("pindex", String.valueOf(1));
            map2.put("psize", String.valueOf(20));
            map2.put("timecode", timecode);
            map2.put("opflag", String.valueOf(2));
            String encode = MD5Encoder.encode(Sign.generateSign(map2) +
                    "12345678901234567890123456789011");
            map2.put("sign", encode);
            request.add(map2);
            CallServer.getRequestInstance().add(this, 2, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
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
            holder.attPeopItemTv.setText(dataList1.get(position).empname);
            holder.attPeopPhone.setText(dataList1.get(position).empphone);
        }

        @Override
        public long getItemId(int position) {
            i = position;
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
            if (v.getId() == R.id.att_peop_add) {        //添加
                getDeleteServer(dataList1.get(getLayoutPosition()).empphone);

            }
        }
    }

    private void getAddServer(String phone) {
        try {
            Request<String> request3 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "time_person_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map3.put("access_id", "1234567890");
            map3.put("timestamp", time);
            map3.put("telnum", UiUtils.getPhoneNumber());
            map3.put("timecode", timecode);
            map3.put("opflag", String.valueOf(0));
            map3.put("phones", phone);
            String encode = MD5Encoder.encode(Sign.generateSign(map3) +
                    "12345678901234567890123456789011");
            map3.put("sign", encode);
            Log.e("addmap",UiUtils.Map2JsonStr(map3));
            request3.add(map3);
            CallServer.getRequestInstance().add(this, 3, request3, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getDeleteServer(String phone) {
        try {
            Request<String> request4 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "time_person_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map4.put("access_id", "1234567890");
            map4.put("timestamp", time);
            map4.put("telnum", UiUtils.getPhoneNumber());
            map4.put("timecode", timecode);
            map4.put("opflag", String.valueOf(1));
            map4.put("phones", phone);
            String encode = MD5Encoder.encode(Sign.generateSign(map4) +
                    "12345678901234567890123456789011");
            map4.put("sign", encode);
            request4.add(map4);
            CallServer.getRequestInstance().add(this, 4, request4, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
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
            holder.attPeopItemTv.setText(dataList2.get(position).empname);
            holder.attPeopPhone.setText(dataList2.get(position).empphone);
            attPeopChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < dataList2.size(); i++) {
                        holder.attPeopItemIcon.setChecked(true);
                    }
                    myAdapter2.notifyDataSetChanged();
                }
            });
        }

        @Override
        public long getItemId(int position) {
            j = position;
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
            if (v.getId() == R.id.att_peop_add) {                  //添加
                getAddServer(dataList2.get(getLayoutPosition()).empphone);
            }
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 1:
                    Log.e("未排班", "onSucceed: " + response.get());
                    attPeopBean1 = new Gson().fromJson(response.get(), AttPeopBean.class);
                    dataList1 = attPeopBean1.result.data;
                    myAdapter1.notifyDataSetChanged();
                    break;
                case 2:
                    Log.e("已排班", "onSucceed: " + response.get());
                    attPeopBean2 = new Gson().fromJson(response.get(), AttPeopBean.class);
                    dataList2 = attPeopBean2.result.data;

                    myAdapter2.notifyDataSetChanged();
                    break;
                case 3:
                    Log.e("考勤人员add",response.get());
                    getServerData1();
                    getServerData2();
         /*           ApplyBean applyBean1 = new Gson().fromJson(response.get(), ApplyBean.class);
                    UiUtils.ToastUtils(applyBean1.summary);
                    new Handler().postDelayed(new Runnable(){
                        public void run() {
                            dataList1.remove(i);
                            myAdapter1.notifyDataSetChanged();
                        }
                    }, 500);*/
                    break;
                case 4:
                    Log.e("考勤人员delete",response.get());
                    getServerData1();
                    getServerData2();
                  /*  ApplyBean applyBean2 = new Gson().fromJson(response.get(), ApplyBean.class);
                    UiUtils.ToastUtils(applyBean2.summary);
                    new Handler().postDelayed(new Runnable(){
                        public void run() {
                            dataList2.remove(j);
                        }
                    }, 500);
                    myAdapter2.notifyDataSetChanged();*/
                    break;
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

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
