package com.luofangyun.shangchao.base.impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.app.LeavetypeListActivity;
import com.luofangyun.shangchao.base.BaseActivity;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用→工作中5个页面的父类
 */
public class AppBaseActivity extends BaseActivity {
    public View view;
    public ArrayList<String> mTitleList = new ArrayList<>();//页卡标题集合
    public ArrayList<View>   mViewList  = new ArrayList<>();   //页卡视图集合
    public View                     view1;
    public PullLoadMoreRecyclerView myLeaveRlv, leaveRlv, examineAboptRlv;
    public Map<String, String> map, map1, map2;
    public View        view2;
    public TabLayout   mTabLayout;
    public ViewPager   mViewPager;
    public RadioButton myLeaveRb, leaveExamineRb;
    public ArrayList<String> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_all, null);
        view1 = View.inflate(this, R.layout.wait_examine1, null);   //待审核
        view2 = View.inflate(this, R.layout.examine_adopt1, null);  //已审核
        map = new HashMap<>();
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        initView();
        initData();
    }

    public void initView() {
        myLeaveRb = (RadioButton) view.findViewById(R.id.my_leave_rb);           //下面按钮左边文字
        leaveExamineRb = (RadioButton) view.findViewById(R.id.leave_examine_rb); //下面按钮右边文字
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_view);
        myLeaveRlv = (PullLoadMoreRecyclerView) view.findViewById(R.id.leave_rlv);      //我发起的
        leaveRlv = (PullLoadMoreRecyclerView) view1.findViewById(R.id.leave_rlv1);                  //未审核
        examineAboptRlv = (PullLoadMoreRecyclerView) view2.findViewById(R.id.examine_adopt_rlv);   //已审核
    }

    public void initData() {
        myLeaveRb.setOnClickListener(this);
        leaveExamineRb.setOnClickListener(this);
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
        flAddress.addView(view);
    }

    public void getMySelf(String json, int j, HttpListener<String> httpListener) {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    json, RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pindex", String.valueOf(j));
            map.put("psize", String.valueOf(10));
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

    public void getWaitExamine(String json1, int k, HttpListener<String> httpListener) {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    json1, RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("pindex", String.valueOf(k));
            map1.put("psize", String.valueOf(100));
            map1.put("flag", String.valueOf(1));
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request1.add(map1);
            CallServer.getRequestInstance().add(this, 1, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getExamine(String json2, int l, HttpListener<String> httpListener) {
        try {
            Request<String> request2 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    json2, RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map2.put("access_id", "1234567890");
            map2.put("timestamp", time);
            map2.put("telnum", UiUtils.getPhoneNumber());
            map2.put("pindex", String.valueOf(l));
            map2.put("psize", String.valueOf(100));
            map2.put("flag", String.valueOf(2));
            String encode = MD5Encoder.encode(Sign.generateSign(map2) +
                    "12345678901234567890123456789011");
            map2.put("sign", encode);
            request2.add(map2);
            CallServer.getRequestInstance().add(this, 2, request2, httpListener, false, false);
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
                startActivity(new Intent(this, LeavetypeListActivity.class));
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
}
