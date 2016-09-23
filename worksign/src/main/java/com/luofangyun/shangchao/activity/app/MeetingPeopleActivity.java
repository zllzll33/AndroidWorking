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
import com.luofangyun.shangchao.domain.MeetingPeopleBean;
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
 * Created by win7 on 2016/9/14.
 */
public class MeetingPeopleActivity extends BaseActivity {
    public static String meetcode,meetstatu;
    public View view, view1, view2;
    public TabLayout mTabLayout;
    public ViewPager mViewPager;
    public ArrayList<String> mTitleList = new ArrayList<>();//页卡标题集合
    public ArrayList<View>   mViewList  = new ArrayList<>();   //页卡视图集合
    public PullLoadMoreRecyclerView recy1, recy2;
    List<MeetingPeopleBean.MeetingPeole> dataList1,dateList2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view= View.inflate(this,R.layout.act_meeting_people,null);
        view1 = View.inflate(this, R.layout.activity_meeting1, null);
        view2 = View.inflate(this, R.layout.activity_meeting2, null);
        flAddress.addView(view);
        titleTv.setText("会议人员");
        mTabLayout = (TabLayout) view.findViewById(R.id.meeting_tab);
        mViewPager = (ViewPager) view.findViewById(R.id.meeting_vp);
        recy1 = (PullLoadMoreRecyclerView) view1.findViewById(R.id.meet_rlv1);
        recy2 = (PullLoadMoreRecyclerView) view2.findViewById(R.id.meet_rlv2);
        recy1.setLinearLayout();
        recy2.setLinearLayout();
        mViewList.add(view1);
        mViewList.add(view2);
        mTitleList.add("参会人员");
        mTitleList.add("未参会人员");
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);             //设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0))); //添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(mAdapter);                         //给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);               //将TabLayout和ViewPager关联起来
        mTabLayout.setTabsFromPagerAdapter(mAdapter);            //给Tabs设置适配器
        upDownRefurbish();
        getGoPeople();
        getNoGoPeople();
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
private void getGoPeople()
{
    try {
        Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                "meet_person_list.json", RequestMethod.POST);
        String time = Long.toString(new Date().getTime());
        Map<String,String> map2=new HashMap<>();
        map2.put("access_id", "1234567890");
        map2.put("timestamp", time);
        map2.put("telnum", UiUtils.getPhoneNumber());
        map2.put("pindex", String.valueOf(1));
        map2.put("psize", String.valueOf(200));
        map2.put("meetstatu", meetstatu);
        map2.put("meetcode", meetcode);
        String encode = MD5Encoder.encode(Sign.generateSign(map2) +
                "12345678901234567890123456789011");
        map2.put("sign", encode);
        request.add(map2);
        CallServer.getRequestInstance().add(this, 0, request, httpListener, false, false);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    private void getNoGoPeople()
    {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "meet_user_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map2=new HashMap<>();
            map2.put("access_id", "1234567890");
            map2.put("timestamp", time);
            map2.put("telnum", UiUtils.getPhoneNumber());
            map2.put("pindex", String.valueOf(1));
            map2.put("psize", String.valueOf(200));
            map2.put("meetcode", meetcode);
            String encode = MD5Encoder.encode(Sign.generateSign(map2) +
                    "12345678901234567890123456789011");
            map2.put("sign", encode);
            request.add(map2);
            CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 0:
                    String result1 = response.get();
                    Gson gson1=new Gson();
                    MeetingPeopleBean meetingPeopleBean1=gson1.fromJson(result1,MeetingPeopleBean.class);
                    dataList1=meetingPeopleBean1.result.data;
                    MyAdapter1 myAdapter1=new MyAdapter1();
                    recy2.setAdapter(myAdapter1);
//                  Log.e("result1",result1);
                    break;
                case 1:
                    String result2 = response.get();
                    Gson gson=new Gson();
                    MeetingPeopleBean meetingPeopleBean=gson.fromJson(result2,MeetingPeopleBean.class);
                    dateList2=meetingPeopleBean.result.data;
                    MyAdapter2 myAdapter2=new MyAdapter2();
                    recy2.setAdapter(myAdapter2);
//                    Log.e("result2",result2);
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
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {


            default:
                break;
        }
    }
    public class MyAdapter1 extends RecyclerView.Adapter<MyViewHolder1> {
        @Override
        public MyViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder1 myViewHolder1 = new MyViewHolder1(LayoutInflater.from(getApplication())
                    .inflate(R.layout.item_meeting_people, parent, false));
            return myViewHolder1;
        }
        @Override
        public void onBindViewHolder(MyViewHolder1 holder, int position) {
            holder.name.setText(dataList1.get(position).empname);
        }

        @Override
        public int getItemCount() {
            return dataList1.size();
        }
    }
    public class MyAdapter2 extends RecyclerView.Adapter<MyViewHolder2> {
        @Override
        public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder2 myViewHolder2 = new MyViewHolder2(LayoutInflater.from(getApplication())
                    .inflate(R.layout.item_meeting_people, parent, false));
            return myViewHolder2;
        }
        @Override
        public void onBindViewHolder(MyViewHolder2 holder, int position) {
            holder.name.setText(dateList2.get(position).empname);
        }

        @Override
        public int getItemCount() {
            return dateList2.size();
        }
    }
    class MyViewHolder1 extends RecyclerView.ViewHolder {
        TextView name;
        public MyViewHolder1(View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.name);
        }
    }
    class MyViewHolder2 extends RecyclerView.ViewHolder {
        TextView name;
        public MyViewHolder2(View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.name);
        }
    }

}
