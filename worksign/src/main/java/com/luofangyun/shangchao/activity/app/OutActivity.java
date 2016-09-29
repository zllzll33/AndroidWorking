package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.domain.OutDone;
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
 * 应用→工作→外出
 */
public class OutActivity extends BaseActivity {

    private View view;
    private ArrayList<String> mTitleList = new ArrayList<>();//页卡标题集合
    private ArrayList<View>   mViewList  = new ArrayList<>();   //页卡视图集合
    private View                     view1;
    private PullLoadMoreRecyclerView myLeaveRlv, leaveRlv, examineAboptRlv;
    private Map<String, String> map, map1, map2, map3;
    private View      view2;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private OutDone   leaveExamine, leaveExamine1, leaveExamine2, leaveExamineText,
            leaveExamineText1, leaveExamineText2;
    private ArrayList<OutDone.Result.Json> dataList, dataList1, dataList2;
    private RadioButton myLeaveRb, leaveExamineRb;
    private ArrayList<String> list = new ArrayList<>();
    private ApplyBean applyBean;
    private int i, j = 1, k = 1, l = 1;
    private MyAdapter myAdapter;
    private MyAdapter1 myAdapter1;
    private MyAdapter2 myAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_all, null);
        view1 = View.inflate(this, R.layout.wait_examine1, null);   //待审核
        view2 = View.inflate(this, R.layout.examine_adopt1, null);  //已审核
        initView();
        initData();
    }

    private void initView() {
        myLeaveRb = (RadioButton) view.findViewById(R.id.my_leave_rb);
        leaveExamineRb = (RadioButton) view.findViewById(R.id.leave_examine_rb);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_view);
        myLeaveRlv = (PullLoadMoreRecyclerView) view.findViewById(R.id.leave_rlv);      //我发起的
        leaveRlv = (PullLoadMoreRecyclerView) view1.findViewById(R.id.leave_rlv1);
        //未审核
        examineAboptRlv = (PullLoadMoreRecyclerView) view2.findViewById(R.id.examine_adopt_rlv);
        //已审核
    }
    private void initData() {
        map = new HashMap<>();
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        map3 = new HashMap<>();
        getWaitExamine();  //待审核请求数据
        getExamine();      //已审核请求数据
        getMySelf();       //我发起的
        myLeaveRb.setOnClickListener(this);
        leaveExamineRb.setOnClickListener(this);
        right.setText("写申请");
        titleTv.setText("外出");
        myLeaveRb.setText("我的外出");
        leaveExamineRb.setText("我的审批");
        myLeaveRlv.setLinearLayout();
        myAdapter = new MyAdapter();
        myLeaveRlv.setAdapter(myAdapter);

        leaveRlv.setLinearLayout();
        myAdapter1 = new MyAdapter1();
        leaveRlv.setAdapter(myAdapter1);

        examineAboptRlv.setLinearLayout();
        myAdapter2 = new MyAdapter2();
        examineAboptRlv.setAdapter(myAdapter2);

        right.setVisibility(View.VISIBLE);
        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        //添加页卡标题
        mTitleList.add("待审核");
        mTitleList.add("已审核");
        mTabLayout.setVisibility(View.GONE);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);             //设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0))); //添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(mAdapter);                         //给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);               //将TabLayout和ViewPager关联起来
        mTabLayout.setTabsFromPagerAdapter(mAdapter);            //给Tabs设置适配器
        upDownRefurbish();
        flAddress.addView(view);
    }

    private void upDownRefurbish() {
        myLeaveRlv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
//                getMySelf(j);
                UiUtils.upDownRefurbish(myLeaveRlv);
            }

            @Override
            public void onLoadMore() {
//                getMySelf(j);
                UiUtils.upDownRefurbish(myLeaveRlv);
            }
        });
        leaveRlv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
//                getWaitExamine(k);
                UiUtils.upDownRefurbish(leaveRlv);
            }

            @Override
            public void onLoadMore() {
//                getWaitExamine(k);
                UiUtils.upDownRefurbish(leaveRlv);
            }
        });
        examineAboptRlv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {


            @Override
            public void onRefresh() {
//                getMySelf(j);
                UiUtils.upDownRefurbish(examineAboptRlv);
            }

            @Override
            public void onLoadMore() {
//                getMySelf(j);
                UiUtils.upDownRefurbish(examineAboptRlv);
            }
        });

    }

    /**
     * 我发起的
     */
    private void getMySelf() {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "emp_out_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pindex", String.valueOf(1));
            map.put("psize", String.valueOf(200));
            map.put("flag", String.valueOf(0));
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request1.add(map);
            CallServer.getRequestInstance().add(this, 0, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getWaitExamine() {
        try {
            Request<String> request2 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "emp_out_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("pindex", String.valueOf(1));
            map1.put("psize", String.valueOf(200));
            map1.put("flag", String.valueOf(1));
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request2.add(map1);
            CallServer.getRequestInstance().add(this, 1, request2, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getExamine() {
        try {
            Request<String> request3 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "emp_out_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map2.put("access_id", "1234567890");
            map2.put("timestamp", time);
            map2.put("telnum", UiUtils.getPhoneNumber());
            map2.put("pindex", String.valueOf(l));
            map2.put("psize", String.valueOf(200));
            map2.put("flag", String.valueOf(2));
            String encode = MD5Encoder.encode(Sign.generateSign(map2) +
                    "12345678901234567890123456789011");
            map2.put("sign", encode);
            request3.add(map2);
            CallServer.getRequestInstance().add(this, 2, request3, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 0:
                    String result = response.get();
                    System.out.println("我发起的外出数据result=" + result);
                    leaveExamine = processData(result);
                    myAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    String result1 = response.get();
                    System.out.println("未审核外出数据result1=" + result1);
                    leaveExamine1 = processData1(result1);
                    myAdapter1.notifyDataSetChanged();
                    break;
                case 2:
                    String result2 = response.get();
                    System.out.println("已审核的result2=" + result2);
                    leaveExamine2 = processData2(result2);
                    myAdapter2.notifyDataSetChanged();
                    break;
                case 3:
                    String result3 = response.get();
                    applyBean = new Gson().fromJson(result3, ApplyBean.class);
                    if(applyBean.status.equals("00000"))
                    {
                        getWaitExamine();  //待审核请求数据
                        getExamine();      //已审核请求数据
                        getMySelf();       //我发起的
                    }
                 /*   dataList.remove(i);
                    myAdapter.notifyDataSetChanged();
                    UiUtils.ToastUtils(applyBean.summary);*/
                default:
                    break;
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    private OutDone processData(String json) {
        Gson gson = new Gson();
        leaveExamineText = gson.fromJson(json, OutDone.class);
        if (leaveExamineText.status.equals("00000")) {
            dataList = leaveExamineText.result.data;
        }
        return leaveExamineText;
    }

    private OutDone processData1(String json) {
        Gson gson = new Gson();
        leaveExamineText1 = gson.fromJson(json, OutDone.class);
        if (leaveExamineText1.status.equals("00000")) {
            dataList1 = leaveExamineText1.result.data;
        }
        return leaveExamineText1;
    }

    private OutDone processData2(String json) {
        Gson gson = new Gson();
        leaveExamineText2 = gson.fromJson(json, OutDone.class);
        if (leaveExamineText2.status.equals("00000")) {
            dataList2 = leaveExamineText2.result.data;
        }
        return leaveExamineText2;
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(getApplication())
                    .inflate(R.layout.app_work_item, parent, false));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.workDays.setText(dataList.get(position).outdays);
            int statu = dataList.get(position).statu;
            if (statu == 0) {
                holder.workState.setText("审批中");
            } else if (statu == 1) {
                holder.workState.setText("审批通过");
                holder.cancleTv.setVisibility(View.GONE);
            } else if (statu == 2) {
                holder.workState.setText("审批拒绝");
                holder.cancleTv.setVisibility(View.GONE);
            }
            holder.starttime.setText(dataList.get(position).stattime);
            holder.endtime.setText(dataList.get(position).endtime);
        }

        @Override
        public long getItemId(int position) {
            i = position;
            return position;
        }

        @Override
        public int getItemCount() {
            if (dataList != null) {
                return dataList.size();
            }
            return 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView workDaysTv, workDays, workState, starttime, endtime, cancleTv;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), LeavetypeListActivity.class);
                    intent.putExtra("stattime", dataList.get(getLayoutPosition()).stattime);
                    intent.putExtra("endtime", dataList.get(getLayoutPosition()).endtime);
                    intent.putExtra("outdays", dataList.get(getLayoutPosition()).outdays);
                    intent.putExtra("statu", dataList.get(getLayoutPosition()).statu + "");
                    intent.putExtra("outreason", dataList.get(getLayoutPosition()).outreason);
                    intent.setAction("outetail");
                    startActivity(intent);
                }
            });
            workDaysTv = (TextView) itemView.findViewById(R.id.work_days_tv);
            workDays = (TextView) itemView.findViewById(R.id.work_days);
            workState = (TextView) itemView.findViewById(R.id.work_state);
            starttime = (TextView) itemView.findViewById(R.id.starttime);
            endtime = (TextView) itemView.findViewById(R.id.endtime);
            cancleTv = (TextView) itemView.findViewById(R.id.cancle_tv);
            cancleTv.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.cancle_tv) {
                getDeleteSever(dataList.get(getLayoutPosition()).outcode);
            }
        }
    }

    private void getDeleteSever(String outcode) {
        try {
            Request<String> request4 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "out_cancel.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map3.put("access_id", "1234567890");
            map3.put("timestamp", time);
            map3.put("telnum", UiUtils.getPhoneNumber());
            map3.put("outcode", outcode);
            String encode = MD5Encoder.encode(Sign.generateSign(map3) +
                    "12345678901234567890123456789011");
            map3.put("sign", encode);
            request4.add(map3);
            CallServer.getRequestInstance().add(this, 3, request4, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyAdapter1 extends RecyclerView.Adapter<MyViewHolder1> {
        @Override
        public MyViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder1 myViewHolder1 = new MyViewHolder1(LayoutInflater.from(getApplication())
                    .inflate(R.layout.app_work_item1, parent, false));
            return myViewHolder1;
        }

        @Override
        public void onBindViewHolder(MyViewHolder1 holder, int position) {
            holder.workDays.setText(dataList1.get(position).outdays);
            int statu = dataList1.get(position).statu;
            if (statu == 0) {
                holder.workState.setText("审批中");
            } else if (statu == 1) {
                holder.workState.setText("审批通过");
            } else if (statu == 2) {
                holder.workState.setText("审批拒绝");
            }
            holder.starttime.setText(dataList1.get(position).stattime);
            holder.endtime.setText(dataList1.get(position).endtime);
        }

        @Override
        public int getItemCount() {
            if (dataList1 != null) {
                System.out.println("待审核=" + dataList1.size());
                return dataList1.size();
            }
            return 0;
        }
    }

    class MyViewHolder1 extends RecyclerView.ViewHolder {
        TextView workDaysTv, workDays, workState, starttime, endtime;

        public MyViewHolder1(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), LeavetypeListActivity.class);
                    intent.putExtra("stattime", dataList1.get(getLayoutPosition()).stattime);
                    intent.putExtra("endtime", dataList1.get(getLayoutPosition()).endtime);
                    intent.putExtra("outdays", dataList1.get(getLayoutPosition()).outdays);
                    intent.putExtra("statu", dataList1.get(getLayoutPosition()).statu + "");
                    intent.putExtra("outreason", dataList1.get(getLayoutPosition()).outreason);
                    intent.putExtra("outcode", dataList1.get(getLayoutPosition()).outcode);
                    intent.setAction("waitoutetail");
                    startActivityForResult(intent,1);
                }
            });
            workDaysTv = (TextView) itemView.findViewById(R.id.work_days_tv);
            workDays = (TextView) itemView.findViewById(R.id.work_days);
            workState = (TextView) itemView.findViewById(R.id.work_state);
            starttime = (TextView) itemView.findViewById(R.id.starttime);
            endtime = (TextView) itemView.findViewById(R.id.endtime);
        }
    }

    private class MyAdapter2 extends RecyclerView.Adapter<MyViewHolder2> {
        @Override
        public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder2 myViewHolder2 = new MyViewHolder2(LayoutInflater.from(getApplication())
                    .inflate(R.layout.app_work_item1, parent, false));
            return myViewHolder2;
        }

        @Override
        public void onBindViewHolder(MyViewHolder2 holder, int position) {
            holder.workDays.setText(dataList2.get(position).outdays);
            int statu = dataList2.get(position).statu;
            if (statu == 0) {
                holder.workState.setText("审批中");
            } else if (statu == 1) {
                holder.workState.setText("审批通过");
            } else if (statu == 2) {
                holder.workState.setText("审批拒绝");
            }
            holder.starttime.setText(dataList2.get(position).stattime);
            holder.endtime.setText(dataList2.get(position).endtime);
        }

        @Override
        public int getItemCount() {
            if (dataList2 != null) {
                System.out.println("已审核=" + dataList2.size());
                return dataList2.size();
            }
            return 0;
        }
    }

    class MyViewHolder2 extends RecyclerView.ViewHolder {
        TextView workDaysTv, workDays, workState, starttime, endtime;

        public MyViewHolder2(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), LeavetypeListActivity.class);
                    intent.putExtra("stattime", dataList2.get(getLayoutPosition()).stattime);
                    intent.putExtra("endtime", dataList2.get(getLayoutPosition()).endtime);
                    intent.putExtra("outdays", dataList2.get(getLayoutPosition()).outdays);
                    intent.putExtra("statu", dataList2.get(getLayoutPosition()).statu + "");
                    intent.putExtra("outreason", dataList2.get(getLayoutPosition()).outreason);
                    intent.setAction("finishoutetail");
                    startActivity(intent);
                }
            });
            workDaysTv = (TextView) itemView.findViewById(R.id.work_days_tv);
            workDays = (TextView) itemView.findViewById(R.id.work_days);
            workState = (TextView) itemView.findViewById(R.id.work_state);
            starttime = (TextView) itemView.findViewById(R.id.starttime);
            endtime = (TextView) itemView.findViewById(R.id.endtime);
        }
    }

    //ViewPager适配器
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.right:
                Intent intent = new Intent(this, LeavetypeListActivity.class);
                intent.setAction("OutActivity");
                startActivityForResult(intent,1);
                break;
            case R.id.my_leave_rb:
                myAdapter.notifyDataSetChanged();
                mTabLayout.setVisibility(View.GONE);
                mViewPager.setVisibility(View.GONE);
                myLeaveRlv.setVisibility(View.VISIBLE);
                leaveRlv.setVisibility(View.GONE);
                break;
            case R.id.leave_examine_rb:
                myAdapter1.notifyDataSetChanged();
                myAdapter2.notifyDataSetChanged();
                mTabLayout.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.VISIBLE);
                myLeaveRlv.setVisibility(View.GONE);
                leaveRlv.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        getWaitExamine();  //待审核请求数据
        getExamine();      //已审核请求数据
        getMySelf();       //我发起的
    }
}