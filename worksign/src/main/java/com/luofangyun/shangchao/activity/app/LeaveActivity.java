package com.luofangyun.shangchao.activity.app;

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
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.domain.LeaveExamine;
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
 * 请假管理
 */
public class LeaveActivity extends BaseActivity {
    private View view;
    private ArrayList<String> mTitleList = new ArrayList<>();//页卡标题集合
    private ArrayList<View>   mViewList  = new ArrayList<>();   //页卡视图集合
    private View                     view1;
    private PullLoadMoreRecyclerView myLeaveRlv, leaveRlv, examineAboptRlv;
    private Map<String, String> map, map1, map2, map3;
    private View         view2;
    private TabLayout    mTabLayout;
    private ViewPager    mViewPager;
    private LeaveExamine leaveExamine, leaveExamine1, leaveExamine2, leaveExamineText,
            leaveExamineText1, leaveExamineText2;
    private ArrayList<LeaveExamine.Result.Json> dataList, dataList1, dataList2;
    private MyAdapter   myAdapter;
    private RadioButton myLeaveRb, leaveExamineRb;
    private int i;
    private int j = 1, k = 1, l = 1;
    private MyAdapter1 myAdapter1;
    private TextView   leave_examine;
    private ArrayList<LeaveExamine.Result.Json> list1 = new ArrayList<>();
    private ArrayList<LeaveExamine.Result.Json> list2 = new ArrayList<>();
    private ArrayList<LeaveExamine.Result.Json> list3 = new ArrayList<>();
    private MyAdapter2 myAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_all, null);
        view1 = View.inflate(this, R.layout.wait_examine, null);   //待审核
        view2 = View.inflate(this, R.layout.examine_adopt, null);  //已审核
        map = new HashMap<>();
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        map3 = new HashMap<>();
        getWaitExamine();  //待审核请求数据
        getExamine();      //已审核请求数据
        getMySelf();       //我发起的
        initView();
        initData();
    }

    private void initView() {
        myLeaveRb = (RadioButton) view.findViewById(R.id.my_leave_rb);
        leaveExamineRb = (RadioButton) view.findViewById(R.id.leave_examine_rb);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_view);
        myLeaveRlv = (PullLoadMoreRecyclerView) view.findViewById(R.id.leave_rlv);                  //我发起的
        leaveRlv = (PullLoadMoreRecyclerView) view1.findViewById(R.id.leave_rlv1);                  //未审核
        examineAboptRlv = (PullLoadMoreRecyclerView) view2.findViewById(R.id.examine_adopt_rlv);    //已审核
    }

    private void initData() {
        right.setText("写假条");
        right.setVisibility(View.VISIBLE);
        myLeaveRb.setText("我的假条");
        leaveExamineRb.setText("我的审批");
        right.setOnClickListener(this);
        myLeaveRb.setOnClickListener(this);
        leaveExamineRb.setOnClickListener(this);
        titleTv.setText("请假");
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
  private void cancle(String leavecode)
  {
      try {
          Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                  "leave_cancel.json", RequestMethod.POST);
          String time = Long.toString(new Date().getTime());
          map.put("access_id", "1234567890");
          map.put("timestamp", time);
          map.put("telnum", UiUtils.getPhoneNumber());
          map.put("leavecode", leavecode);
          String encode = MD5Encoder.encode(Sign.generateSign(map) +
                  "12345678901234567890123456789011");
          map.put("sign", encode);
          request.add(map);
          CallServer.getRequestInstance().add(this, 4, request, httpListener, false, false);
      } catch (Exception e) {
          e.printStackTrace();
      }
  }
    private void upDownRefurbish() {
        leaveRlv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
//                getWaitExamine();
                UiUtils.upDownRefurbish(leaveRlv);
            }

            @Override
            public void onLoadMore() {
//                getWaitExamine();
                UiUtils.upDownRefurbish(leaveRlv);
            }
        });
        examineAboptRlv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView
                .PullLoadMoreListener() {
            @Override
            public void onRefresh() {
//                getExamine();
                UiUtils.upDownRefurbish(examineAboptRlv);
            }

            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(examineAboptRlv);
            }
        });
        myLeaveRlv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
//                getMySelf();
                UiUtils.upDownRefurbish(myLeaveRlv);
            }

            @Override
            public void onLoadMore() {
//                getMySelf();
                UiUtils.upDownRefurbish(myLeaveRlv);
            }
        });
    }

    /**
     * 我发起的
     */
    private void getMySelf() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "emp_leave_list.json", RequestMethod.POST);
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
            request.add(map);
            CallServer.getRequestInstance().add(this, 0, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getWaitExamine() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "emp_leave_list.json", RequestMethod.POST);
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
            request.add(map1);
            CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getExamine() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "emp_leave_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map2.put("access_id", "1234567890");
            map2.put("timestamp", time);
            map2.put("telnum", UiUtils.getPhoneNumber());
            map2.put("pindex", String.valueOf(1));
            map2.put("psize", String.valueOf(200));
            map2.put("flag", String.valueOf(2));
            String encode = MD5Encoder.encode(Sign.generateSign(map2) +
                    "12345678901234567890123456789011");
            map2.put("sign", encode);
            request.add(map2);
            CallServer.getRequestInstance().add(this, 2, request, httpListener, false, false);
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
                    System.out.println("我发起的121212=" + result);
                    leaveExamine = processData(result);
                    myAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    String result1 = response.get();
                    System.out.println("未审核的请假数据result1=" + result1);
                    leaveExamine1 = processData1(result1);
                    myAdapter1.notifyDataSetChanged();
                    break;
                case 2:
                    String result2 = response.get();
                    System.out.println("+++请假数据result2=" + result2);
                    leaveExamine2 = processData2(result2);
                    myAdapter2.notifyDataSetChanged();
                    break;
                case 3:
                    String result3 = response.get();
                    System.out.println("撤消=" + result3);
                    UiUtils.ToastUtils(new Gson().fromJson(result3, ApplyBean.class).summary);
                    dataList.remove(i);
                    myAdapter.notifyDataSetChanged();
                    break;
                case 4:
//                    Log.e("cancel_result",response.get());
                    getWaitExamine();  //待审核请求数据
                    getExamine();      //已审核请求数据
                    getMySelf();       //我发起的
                    break;
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    private LeaveExamine processData(String json) {
        Gson gson = new Gson();
        leaveExamineText = gson.fromJson(json, LeaveExamine.class);
        if (leaveExamineText.status.equals("00000")) {
            dataList = leaveExamineText.result.data;
        }
        return leaveExamineText;
    }

    private LeaveExamine processData1(String json) {
        Gson gson = new Gson();
        leaveExamineText1 = gson.fromJson(json, LeaveExamine.class);
        if (leaveExamineText1.status.equals("00000")) {
            dataList1 = leaveExamineText1.result.data;
        }
        return leaveExamineText1;
    }

    private LeaveExamine processData2(String json) {
        Gson gson = new Gson();
        leaveExamineText2 = gson.fromJson(json, LeaveExamine.class);
        if (leaveExamineText2.status.equals("00000")) {
            dataList2 = leaveExamineText2.result.data;
        }
        return leaveExamineText2;
    }
    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getApplication()).inflate(R.layout.leave_item,parent,false));
        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.leave_message.setText(dataList.get(position).leavename);
            holder.leave_days.setText(dataList.get(position).leavedays);
            if (dataList.get(position).statu == 0) {
                holder.leave_examine.setText("审批中");
                holder.cancle_tv.setVisibility(View.VISIBLE);
            } else if (dataList.get(position).statu == 1) {
                holder.leave_examine.setText("审批通过");
                holder.cancle_tv.setVisibility(View.GONE);
            } else if (dataList.get(position).statu == 2) {
                holder.leave_examine.setText("审批拒绝");
                holder.cancle_tv.setVisibility(View.GONE);
            }
            holder.leave_starttime.setText(dataList.get(position).stattime);
            holder.leave_endtime.setText(dataList.get(position).endtime);
        }
        @Override
        public long getItemId(int position) {
            j = position;
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
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView leave_message,leave_days,leave_starttime,leave_endtime,cancle_tv,leave_examine;
        ImageView leave_rigth;
        public MyViewHolder(View itemView) {
            super(itemView);
            leave_message = (TextView) itemView.findViewById(R.id.leave_message);
            leave_days = (TextView) itemView.findViewById(R.id.leave_days);
            leave_starttime = (TextView) itemView.findViewById(R.id.leave_starttime);
            leave_endtime = (TextView) itemView.findViewById(R.id.leave_endtime);
            leave_rigth = (ImageView) itemView.findViewById(R.id.leave_rigth);
            cancle_tv = (TextView) itemView.findViewById(R.id.cancle_tv);
            leave_examine = (TextView) itemView.findViewById(R.id.leave_examine);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), LeavetypeListActivity.class);
                    intent.putExtra("leavename", dataList.get(getLayoutPosition()).leavename);
                    intent.putExtra("stattime", dataList.get(getLayoutPosition()).stattime);
                    intent.putExtra("endtime", dataList.get(getLayoutPosition()).endtime);
                    intent.putExtra("leavedays", dataList.get(getLayoutPosition()).leavedays);
                    intent.putExtra("statu", dataList.get(getLayoutPosition()).statu + "");
                    intent.putExtra("leavereason", dataList.get(getLayoutPosition()).leavereason);
                    intent.putExtra("leavecode", dataList.get(getLayoutPosition()).leavecode);
                    intent.putExtra("isMy", true);
                    intent.setAction("Leavewaitdetail");
                    startActivity(intent);
                }
            });
            cancle_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.e("请假撤回",dataList.get(getLayoutPosition()).leavecode);
                    cancle(dataList.get(getLayoutPosition()).leavecode);
                }
            });
        }

    }

    private class MyAdapter1 extends RecyclerView.Adapter<MyViewHolder1> {
        @Override
        public MyViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder1(LayoutInflater.from(getApplication()).inflate(R.layout
                    .leave_item, parent, false));
        }
        @Override
        public void onBindViewHolder(MyViewHolder1 holder, int position) {
            holder.leave_message1.setText(dataList1.get(position).leavename);
            holder.leave_days1.setText(dataList1.get(position).leavedays);
            holder.leave_examine1.setText("待审核");
            holder.leave_starttime1.setText(dataList1.get(position).stattime);
            holder.leave_endtime1.setText(dataList1.get(position).endtime);
            holder.cancle_tv.setVisibility(View.GONE);

        }
        @Override
        public int getItemCount() {
            if (dataList1 != null) {
                return dataList1.size();
            }
            return 0;
        }
    }
    class MyViewHolder1 extends RecyclerView.ViewHolder {
        TextView leave_message1, leave_days1, leave_examine1, leave_starttime1, leave_endtime1,cancle_tv;
        ImageView leave_rigth1;
        public MyViewHolder1(View itemView) {
            super(itemView);
            leave_message1 = (TextView) itemView.findViewById(R.id.leave_message);
            leave_days1 = (TextView) itemView.findViewById(R.id.leave_days);
            leave_examine1 = (TextView) itemView.findViewById(R.id.leave_examine);
            leave_starttime1 = (TextView) itemView.findViewById(R.id.leave_starttime);
            leave_endtime1 = (TextView) itemView.findViewById(R.id.leave_endtime);
            leave_rigth1 = (ImageView) itemView.findViewById(R.id.leave_rigth);
            cancle_tv = (TextView) itemView.findViewById(R.id.cancle_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), LeavetypeListActivity.class);
                    intent.putExtra("leavename", dataList1.get(getLayoutPosition()).leavename);
                    intent.putExtra("stattime", dataList1.get(getLayoutPosition()).stattime);
                    intent.putExtra("endtime", dataList1.get(getLayoutPosition()).endtime);
                    intent.putExtra("leavedays", dataList1.get(getLayoutPosition()).leavedays);
                    intent.putExtra("statu", dataList1.get(getLayoutPosition()).statu + "");
                    intent.putExtra("leavereason", dataList1.get(getLayoutPosition()).leavereason);
                    intent.putExtra("leavecode", dataList1.get(getLayoutPosition()).leavecode);
                    intent.setAction("Leavewaitdetail");
                    startActivityForResult(intent,1);
                }
            });
            cancle_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancle(dataList1.get(getLayoutPosition()).leavecode);
                }
            });
        }

    }
    private class MyAdapter2 extends RecyclerView.Adapter<MyViewHolder2> {
        @Override
        public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder2(LayoutInflater.from(getApplication()).inflate(R.layout
                    .leave_item1, parent, false));
        }
        @Override
        public void onBindViewHolder(MyViewHolder2 holder, int position) {
            holder.leaveMessage.setText(dataList2.get(position).leavename);
            holder.leaveDays.setText(dataList2.get(position).leavedays);
            if (dataList2.get(position).statu == 0) {
                holder.examine.setText("审批中");
            } else if (dataList2.get(position).statu == 1) {
                holder.examine.setText("审批通过");
            } else if (dataList2.get(position).statu == 2) {
                holder.examine.setText("审批拒绝");
            }
            holder.leaveStarttime.setText(dataList2.get(position).stattime);
            holder.leaveEndtime.setText(dataList2.get(position).endtime);
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
    class MyViewHolder2 extends RecyclerView.ViewHolder {
        TextView leaveMessage, leaveDays, examine, leaveStarttime, leaveEndtime;
        ImageView leaveRigth;
        public MyViewHolder2(View itemView) {
            super(itemView);
            leaveMessage = (TextView) itemView.findViewById(R.id.leave_message);
            leaveDays = (TextView) itemView.findViewById(R.id.leave_days);
            examine = (TextView) itemView.findViewById(R.id.leave_examine);
            leaveStarttime = (TextView) itemView.findViewById(R.id.leave_starttime);
            leaveEndtime = (TextView) itemView.findViewById(R.id.leave_endtime);
            leaveRigth = (ImageView) itemView.findViewById(R.id.leave_rigth);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), LeavetypeListActivity.class);
                    intent.putExtra("leavename", dataList2.get(getLayoutPosition()).leavename);
                    intent.putExtra("stattime", dataList2.get(getLayoutPosition()).stattime);
                    intent.putExtra("endtime", dataList2.get(getLayoutPosition()).endtime);
                    intent.putExtra("leavedays", dataList2.get(getLayoutPosition()).leavedays);
                    intent.putExtra("statu", dataList2.get(getLayoutPosition()).statu + "");
                    intent.putExtra("leavereason", dataList2.get(getLayoutPosition()).leavereason);
                    intent.putExtra("leavecode", dataList2.get(getLayoutPosition()).leavecode);
                    intent.setAction("Leavewaitdetail");
                    startActivity(intent);
                }
            });
        }
    }
    private void getDeleteServerData(String code) {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "leave_cancel.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map3.put("access_id", "1234567890");
            map3.put("timestamp", time);
            map3.put("telnum", UiUtils.getPhoneNumber());
            map3.put("leavecode", code);
            String encode = MD5Encoder.encode(Sign.generateSign(map3) +
                    "12345678901234567890123456789011");
            map3.put("sign", encode);
            request1.add(map3);
            CallServer.getRequestInstance().add(this, 3, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
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
                intent.setAction("LeaveActivity");
                startActivityForResult(intent,1);
                break;
            case R.id.my_leave_rb:
                mTabLayout.setVisibility(View.GONE);
                mViewPager.setVisibility(View.GONE);
                myLeaveRlv.setVisibility(View.VISIBLE);
                leaveRlv.setVisibility(View.GONE);
                break;
            case R.id.leave_examine_rb:
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