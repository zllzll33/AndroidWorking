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
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.MeetingBean;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会议
 */
public class MeetingActivity extends BaseActivity {
    public View view, view1, view2;
    public TabLayout mTabLayout;
    public ViewPager mViewPager;
    public ArrayList<String> mTitleList = new ArrayList<>();//页卡标题集合
    public ArrayList<View>   mViewList  = new ArrayList<>();   //页卡视图集合
    public PullLoadMoreRecyclerView recy1, recy2;
    public MyAdapter1          myAdapter1;
    public MyAdapter2          myAdapter2;
    public Map<String, String> map1, map2;
    public String      phoneNumber;
    public MeetingBean meetingBean1, meetingBean2;
    public MeetingBean meetingBean1Text, meetingBean2Text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_meeting, null);
        view1 = View.inflate(this, R.layout.activity_meeting1, null);
        view2 = View.inflate(this, R.layout.activity_meeting2, null);
        phoneNumber = PrefUtils.getString(this, "phoneNumber", null);
        //找到相关的控件
        initView();
        //加载数据
        initData();
    }

    public void initView() {
        mTabLayout = (TabLayout) view.findViewById(R.id.meeting_tab);
        mViewPager = (ViewPager) view.findViewById(R.id.meeting_vp);
        recy1 = (PullLoadMoreRecyclerView) view1.findViewById(R.id.meet_rlv1);
        recy2 = (PullLoadMoreRecyclerView) view2.findViewById(R.id.meet_rlv2);
    }
    public void initData() {
        right.setVisibility(View.VISIBLE);
        right.setText("添加");
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        getServerSend();     //我发起的
        getServerAttach();   //我参与的
        recy1.setLinearLayout();
        myAdapter1 = new MyAdapter1();
        recy1.setAdapter(myAdapter1);
        recy2.setLinearLayout();
        myAdapter2 = new MyAdapter2();
        recy2.setAdapter(myAdapter2);
        if (meetingBean2Text != null) {
            if (meetingBean2.status.equals("00000")) {
                myAdapter2 = new MyAdapter2();
                recy2.setLinearLayout();
                recy2.setAdapter(myAdapter2);
            }
        }
        titleTv.setText("会议");
        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        //添加页卡标题
        mTitleList.add("我发起的");
        mTitleList.add("我参与的");
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
        recy1.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                UiUtils.upDownRefurbish(recy1);
            }

            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(recy1);
            }
        });
        recy2.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                UiUtils.upDownRefurbish(recy2);
            }

            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(recy2);
            }
        });
    }

    public void getServerSend() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "meet_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", phoneNumber);
            map1.put("pindex", String.valueOf(1));
            map1.put("psize", String.valueOf(100));
            map1.put("meetstatu", String.valueOf(0));
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request.add(map1);
            CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getServerAttach() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "meet_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map2.put("access_id", "1234567890");
            map2.put("timestamp", time);
            map2.put("telnum", phoneNumber);
            map2.put("pindex", String.valueOf(1));
            map2.put("psize", String.valueOf(100));
            map2.put("meetstatu", String.valueOf(1));
            String encode = MD5Encoder.encode(Sign.generateSign(map2) +
                    "12345678901234567890123456789011");
            map2.put("sign", encode);
            request.add(map2);
            CallServer.getRequestInstance().add(this, 2, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 1:
                    String result1 = response.get();
                    System.out.println("我发起的会议获取数据=" + result1);
                    meetingBean1 = processData1(result1);
                    myAdapter1.notifyDataSetChanged();
                    break;
                case 2:
                    String result2 = response.get();
                    System.out.println("result2=" + result2);
                    meetingBean2 = processData2(result2);
                    if (meetingBean2.status.equals("00000")) {
                        myAdapter2.notifyDataSetChanged();
                    }
                    break;
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    public MeetingBean processData1(String result1) {
        Gson gson = new Gson();
        meetingBean1Text = gson.fromJson(result1, MeetingBean.class);
        return meetingBean1Text;
    }

    public MeetingBean processData2(String result2) {
        Gson gson = new Gson();
        meetingBean2Text = gson.fromJson(result2, MeetingBean.class);
        return meetingBean2Text;
    }

    public class MyAdapter1 extends RecyclerView.Adapter<MyViewHolder1> {
        @Override
        public MyViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder1 myViewHolder1 = new MyViewHolder1(LayoutInflater.from(getApplication())
                    .inflate(R.layout.meeting_item1, parent, false));
            return myViewHolder1;
        }

        @Override
        public void onBindViewHolder(MyViewHolder1 holder, int position) {
            holder.meetTitle1.setText("会议主题：" + meetingBean1.result.data.get(position).meetname);
            holder.meetArea.setText("会议地点：" + meetingBean1.result.data.get(position).meetaddress);
            holder.meetStartTime.setText("会议开始时间：" + meetingBean1.result.data.get(position)
                    .starttime);
            holder.meetEndTime.setText("会议结束时间：" + meetingBean1.result.data.get(position).endtime);
            if (meetingBean1.result.data.get(position).statu == 1) {
                holder.meetRight1.setVisibility(View.VISIBLE);
            }else if (meetingBean1.result.data.get(position).statu == 0) {
                holder.meetRight1.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            if (meetingBean1 != null) {
                if (meetingBean1.result.data != null) {
                    return meetingBean1.result.data.size();
                }
            }
            return 0;
        }
    }

    class MyViewHolder1 extends RecyclerView.ViewHolder {
        TextView meetTitle1, meetArea, meetStartTime, meetEndTime, meetCancle;
        ImageView meetRight1;
        public MyViewHolder1(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), MeetDetailActivity.class);
                    Log.e("meetstatu", "onClick: "+ meetingBean1.result.data.get(getLayoutPosition()).statu);
                    intent.putExtra("meetname",meetingBean1.result.data.get(getLayoutPosition()).meetname);
                    intent.putExtra("meettheme",meetingBean1.result.data.get(getLayoutPosition()).meettheme);
                    intent.putExtra("meetaddress",meetingBean1.result.data.get(getLayoutPosition()).meetaddress);
                    intent.putExtra("starttime",meetingBean1.result.data.get(getLayoutPosition()).starttime);
                    intent.putExtra("endtime",meetingBean1.result.data.get(getLayoutPosition()).endtime);
                    intent.putExtra("labelname",meetingBean1.result.data.get(getLayoutPosition()).labelname);
                    intent.putExtra("meetstatu",meetingBean1.result.data.get(getLayoutPosition()).statu + "");
                    intent.setAction("MeetingActivity1");
                    startActivity(intent);
                }
            });
            meetTitle1 = (TextView) itemView.findViewById(R.id.meet_title1);
            meetArea = (TextView) itemView.findViewById(R.id.meet_area);
            meetStartTime = (TextView) itemView.findViewById(R.id.meet_start_time);
            meetEndTime = (TextView) itemView.findViewById(R.id.meet_end_time);
            meetCancle = (TextView) itemView.findViewById(R.id.meet_cancle);
            meetRight1 = (ImageView) itemView.findViewById(R.id.meet_right1);
        }
    }

    public class MyAdapter2 extends RecyclerView.Adapter<MyViewHolder2> {

        @Override
        public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder2 myViewHolder2 = new MyViewHolder2(LayoutInflater.from(getApplication())
                    .inflate(R.layout.meeting_item2, parent, false));
            return myViewHolder2;
        }

        @Override
        public void onBindViewHolder(MyViewHolder2 holder, int position) {
            holder.meetTitle2.setText("会议主题：" + meetingBean2.result.data.get(position).meetname);
            holder.meetArea2.setText("会议地点：" + meetingBean2.result.data.get(position).meetaddress);
            holder.meetStartTime2.setText("会议开始时间：" + meetingBean2.result.data.get(position).starttime);
            holder.meetEndTime2.setText("会议结束时间：" + meetingBean2.result.data.get(position).endtime);
        }

        @Override
        public int getItemCount() {
            if (meetingBean2 != null) {
                if (meetingBean2.result.data != null) {
                    return meetingBean2.result.data.size();
                }
            }
            return 0;
        }
    }

    class MyViewHolder2 extends RecyclerView.ViewHolder {
        TextView meetTitle2, meetArea2, meetStartTime2, meetEndTime2;

        public MyViewHolder2(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(getApplication(), MeetDetailActivity.class);
                    intent1.putExtra("meetname",meetingBean2.result.data.get(getLayoutPosition()).meetname);
                    intent1.putExtra("meettheme",meetingBean2.result.data.get(getLayoutPosition()).meettheme);
                    intent1.putExtra("meetaddress",meetingBean2.result.data.get(getLayoutPosition()).meetaddress);
                    intent1.putExtra("starttime",meetingBean2.result.data.get(getLayoutPosition()).starttime);
                    intent1.putExtra("endtime",meetingBean2.result.data.get(getLayoutPosition()).endtime);
                    intent1.putExtra("labelname",meetingBean2.result.data.get(getLayoutPosition()).labelname);
                    intent1.putExtra("statu",meetingBean2.result.data.get(getLayoutPosition()).statu + "");
                    intent1.setAction("MeetingActivity2");
                    startActivity(intent1);
                }
            });
            meetTitle2 = (TextView) itemView.findViewById(R.id.meet_title2);
            meetArea2 = (TextView) itemView.findViewById(R.id.meet_area2);
            meetStartTime2 = (TextView) itemView.findViewById(R.id.meet_start_time2);
            meetEndTime2 = (TextView) itemView.findViewById(R.id.meet_end_time2);
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
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.right:
                Intent intent = new Intent(new Intent(this, MeetDetailActivity.class));
                intent.setAction("MeetingActivity3");
                startActivityForResult(intent,1);
                break;
            default:
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if(requestCode==1)
         {
             getServerSend();     //我发起的
             getServerAttach();   //我参与的
         }
    }

}
