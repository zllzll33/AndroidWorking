package com.luofangyun.shangchao.base.impl;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.addresslist.CompanyApply;
import com.luofangyun.shangchao.activity.app.LeavetypeListActivity;
import com.luofangyun.shangchao.activity.message.ConfirmActivity;
import com.luofangyun.shangchao.activity.message.JoinTeamActivty;
import com.luofangyun.shangchao.activity.message.MessageSystem;
import com.luofangyun.shangchao.activity.message.affichePartData;
import com.luofangyun.shangchao.base.BasePager;
import com.luofangyun.shangchao.domain.JoinTeamDetailBean;
import com.luofangyun.shangchao.domain.LeaveDetailBean;
import com.luofangyun.shangchao.domain.MessageCenter;
import com.luofangyun.shangchao.domain.OutDetailBean;
import com.luofangyun.shangchao.domain.OverTimeDetailBean;
import com.luofangyun.shangchao.domain.TravellDetailBean;
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
import com.zhy.autolayout.AutoRelativeLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息中心
 */

public class MessageCenterPager extends BasePager {
    private View view, view1, view2, view3;
    private ViewPager           mViewPager;
    private TabLayout           mTabLayout;
    private Map<String, String> map1, map2, map3;
    private int[]             rightPic   = {R.mipmap.to_right, R.mipmap.to_right, R.mipmap
            .to_right, R
            .mipmap.to_right, R.mipmap.to_right};
    private int[]             leftPic    = {R.mipmap.round_point, R.mipmap.round_point, R.mipmap
            .round_point, R.mipmap.round_point, R.mipmap.round_point};
    private ArrayList<String> mTitleList = new ArrayList<>();//页卡标题集合
    private ArrayList<View>   mViewList  = new ArrayList<>();   //页卡视图集合
    private AutoRelativeLayout pagerSign;
    private RecyclerView       workRlv, noticeRlv, messageRlv;

    private MessageCenter centerWork, getAffiche, getSystem;
    private ArrayList<MessageCenter.Result.Json> data, systemData, afficheData;
    private String notifycodeWork, notifycodeAffiche, notifycodeSystem,notifyCode;
    private MyAdapter3 myAdapter3;         // 系统消息
    private MyAdapter1 myAdapter1;         // 工作通知
    private MyAdapter2 myAdapter2;         // 公告
    private int i;
    public static Handler handler;
    public MessageCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        getServerWorkData1();    //获取工作通知的数据
        getServerAffiche();      //获取企业公告的数据
        getServerSystem();       //获取系统消息
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        map3 = new HashMap<>();
        tvTitle.setText("消息");
        titleBack.setVisibility(View.GONE);                         //返回键隐藏
        mViewList.clear();
        view = View.inflate(mActivity, R.layout.pager_layout, null);
        pagerSign = (AutoRelativeLayout) view.findViewById(R.id.message_center_papger_sign);
        mViewPager = (ViewPager) view.findViewById(R.id.vp_view);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);

        view1 = View.inflate(mActivity, R.layout.work_layout, null);
        workRlv = (RecyclerView) view1.findViewById(R.id.work_rlv);    //工作通知
        workRlv.setLayoutManager(new LinearLayoutManager(mActivity));
        myAdapter1 = new MyAdapter1();
        workRlv.setAdapter(myAdapter1);

        view2 = View.inflate(mActivity, R.layout.notice_layout, null);
        noticeRlv = (RecyclerView) view2.findViewById(R.id.notice_rlv);
        noticeRlv.setLayoutManager(new LinearLayoutManager(mActivity));
        myAdapter2 = new MyAdapter2();
        noticeRlv.setAdapter(myAdapter2);

        view3 = View.inflate(mActivity, R.layout.message_layout, null);
        messageRlv = (RecyclerView) view3.findViewById(R.id.message__rlv);
        messageRlv.setLayoutManager(new LinearLayoutManager(mActivity));
        myAdapter3 = new MyAdapter3();
        messageRlv.setAdapter(myAdapter3);

        //添加页卡视图
        mViewList.add(view3);
        mViewList.add(view1);
        mViewList.add(view2);
        //添加页卡标题
        mTitleList.add("系统消息");
        mTitleList.add("工作通知");
        mTitleList.add("企业公告");

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);             //设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0))); //添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(2)));
        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(mAdapter);                         //给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);               //将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);            //给Tabs设置适配器
        pagerSign.setOnClickListener(this);
        flContainer.addView(view);
        handler=new Handler()
        {
            public void handleMessage(Message msg)
            {
                getServerWorkData1();
            }
        };
    }

    /**
     * 获取系统消息的数据
     */

    private void getServerSystem() {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "notify_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("pindex", String.valueOf(1));
            map1.put("psize", String.valueOf(100));
            map1.put("flag", String.valueOf(0));
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request1.add(map1);
            CallServer.getRequestInstance().add(mActivity, 0, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getWorkJoinMessage(String notifycode) {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "apply_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map=new HashMap<>();
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("notifycode", notifycode);
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request1.add(map);
            CallServer.getRequestInstance().add(mActivity, 7, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getWorkChuMessage(String notifycode) {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "travel_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map=new HashMap<>();
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("travelcode", "0");
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("notifycode", notifycode);
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request1.add(map);
            CallServer.getRequestInstance().add(mActivity, 8, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getWorkOutMessage(String notifycode) {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "out_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map=new HashMap<>();
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("outcode", "0");
            map.put("notifycode", notifycode);
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request1.add(map);
            CallServer.getRequestInstance().add(mActivity, 6, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getWorkLeaveMessage(String notifycode) {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "leave_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map=new HashMap<>();
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("leavecode", "0");
            map.put("notifycode", notifycode);
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request1.add(map);
            CallServer.getRequestInstance().add(mActivity, 4, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getWorkOvertimeMessage(String notifycode) {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "overtime_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map=new HashMap<>();
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("overcode", "0");
            map.put("notifycode", notifycode);
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request1.add(map);
            CallServer.getRequestInstance().add(mActivity, 5, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 企业公告数据解析
     */
    private void getServerAffiche() {
        try {
            Request<String> request2 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "notify_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map2.put("access_id", "1234567890");
            map2.put("timestamp", time);
            map2.put("telnum", UiUtils.getPhoneNumber());
            map2.put("pindex", String.valueOf(1));
            map2.put("psize", String.valueOf(100));
            map2.put("flag", String.valueOf(2));
            String encode = MD5Encoder.encode(Sign.generateSign(map2) +
                    "12345678901234567890123456789011");
            map2.put("sign", encode);
            request2.add(map2);
            CallServer.getRequestInstance().add(mActivity, 2, request2, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取工作通知的数据
     */
    private void getServerWorkData1() {
        try {
            Request<String> request3 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "notify_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map3.put("access_id", "1234567890");
            map3.put("timestamp", time);
            map3.put("telnum", UiUtils.getPhoneNumber());
            map3.put("pindex", String.valueOf(1));
            map3.put("psize", String.valueOf(100));
            map3.put("flag", String.valueOf(1));
            String encode = MD5Encoder.encode(Sign.generateSign(map3) +
                    "12345678901234567890123456789011");
            map3.put("sign", encode);
            request3.add(map3);
            CallServer.getRequestInstance().add(mActivity, 1, request3, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 接受响应
     */
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 0:
                    String result1 = response.get();
                    System.out.println("系统消息result1=" + result1);
                    MessageCenter systemData = getSystemData(result1);
                    myAdapter3.notifyDataSetChanged();
                    break;
                case 1:
                    String result2 = response.get();
                    System.out.println("工作通知result2=" + result2);
                    MessageCenter workData1 = getWorkData1(result2);
                    myAdapter1.notifyDataSetChanged();
                    break;
                case 2:
                    String result3 = response.get();
                    System.out.println("企业公告result3=" + result3);
                    MessageCenter afficheData2 = getAfficheData2(result3);
                    myAdapter2.notifyDataSetChanged();
                    break;
                case 4:
                    String result4 = response.get();
                    Log.e("请假详情",result4);
                    Gson gson=new Gson();
                    LeaveDetailBean leaveDetailBean=gson.fromJson(result4,LeaveDetailBean.class);
                    Intent intent = new Intent(mActivity, LeavetypeListActivity.class);
                    intent.putExtra("leavename",leaveDetailBean.result.leavename);
                    intent.putExtra("stattime", leaveDetailBean.result.stattime);
                    intent.putExtra("endtime", leaveDetailBean.result.endtime);
                    intent.putExtra("leavedays", leaveDetailBean.result.leavedays);
                    intent.putExtra("statu", leaveDetailBean.result.statu + "");
                    intent.putExtra("leavereason", leaveDetailBean.result.leavereason);
                    intent.putExtra("leavecode", leaveDetailBean.result.leavecode);
                    intent.setAction("Leavewaitdetail");
                    mActivity.startActivity(intent);
                    break;
                case 5:
                    String result5 = response.get();
                    Gson gson1=new Gson();
                    OverTimeDetailBean overTimeDetailBean=gson1.fromJson(result5,OverTimeDetailBean.class);
                    Log.e("加班详情",result5);
                    Intent intent1 = new Intent(mActivity, LeavetypeListActivity.class);
                    intent1.putExtra("stattime",overTimeDetailBean.result.stattime);
                    intent1.putExtra("endtime", overTimeDetailBean.result.endtime);
                    intent1.putExtra("overdays", overTimeDetailBean.result.overdays);
                    intent1.putExtra("statu", overTimeDetailBean.result.statu);
                    intent1.putExtra("overreason", overTimeDetailBean.result.overreason);
                    intent1.setAction("overletail");
                    mActivity.startActivity(intent1);
                    break;
                case 6:
                    String result6 = response.get();
                    Log.e("外出详情",result6);
                    Gson gson2=new Gson();
                    OutDetailBean outDetailBean=gson2.fromJson(result6,OutDetailBean.class);
                    Intent intent2 = new Intent(mActivity, LeavetypeListActivity.class);
                    intent2.putExtra("stattime", outDetailBean.result.stattime);
                    intent2.putExtra("endtime",outDetailBean.result.endtime);
                    intent2.putExtra("outdays",outDetailBean.result.outdays);
                    intent2.putExtra("statu", outDetailBean.result.statu + "");
                    intent2.putExtra("outreason",outDetailBean.result.outreason);
                    intent2.setAction("outetail");
                   mActivity.startActivity(intent2);
                    break;
                case 7:
                    String result7 = response.get();
                    Log.e("加入团队详情",result7);
                    Gson gson3=new Gson();
                    JoinTeamDetailBean joinDetailBean=gson3.fromJson(result7,JoinTeamDetailBean.class);
                    Intent intent3 = new Intent(mActivity, JoinTeamActivty.class);
                    intent3.putExtra("join_name", joinDetailBean.result.empname);
                    intent3.putExtra("join_phone", joinDetailBean.result.empphone);
                    intent3.putExtra("join_note", joinDetailBean.result.remark);
                    intent3.putExtra("notifyCode", notifyCode);
                    mActivity.startActivity(intent3);
                    break;
                case 8:
                    String result8 = response.get();
                    Log.e("加入出差详情",result8);
                    Gson gson4=new Gson();
                    TravellDetailBean travellDetailBean=gson4.fromJson(result8,TravellDetailBean.class);
                    Intent intent4 = new Intent(mActivity, LeavetypeListActivity.class);
                    intent4.putExtra("traveladdress", travellDetailBean.result.traveladdress);
                    intent4.putExtra("stattime", travellDetailBean.result.stattime);
                    intent4.putExtra("endtime", travellDetailBean.result.endtime);
                    intent4.putExtra("traveldays", travellDetailBean.result.traveldays);
                    intent4.putExtra("statu", travellDetailBean.result.statu + "");
                    intent4.putExtra("travelreason", travellDetailBean.result.travelreason);
                    intent4.setAction("travelletail");
                    mActivity.startActivity(intent4);
                    break;
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    /**
     * 解析系统信息数据
     */
    private MessageCenter getSystemData(String result1) {
        Gson gson = new Gson();
        getSystem = gson.fromJson(result1, MessageCenter.class);
        systemData = getSystem.result.data;
        return getSystem;
    }
    /**
     * 解析公告数据
     */
    private MessageCenter getAfficheData2(String result) {
        Gson gson = new Gson();
        getAffiche = gson.fromJson(result, MessageCenter.class);
        afficheData = getAffiche.result.data;
        return getAffiche;
    }

    public MessageCenter getWorkData1(String json) {
        Gson gson = new Gson();
        centerWork = gson.fromJson(json, MessageCenter.class);
        data = centerWork.result.data;
        return centerWork;
    }
    /**
     * 工作通知
     */
    private class MyAdapter1 extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder MyViewHolder = new MyViewHolder(LayoutInflater.from(mActivity).inflate(R
                    .layout.message_work_item, parent, false));
            return MyViewHolder;
        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.leftText.setText(data.get(position).notifytitle);
            holder.rightTime.setText(data.get(position).notifydate);
        }
        @Override
        public long getItemId(int position) {
            i = position;
            return position;
        }

        @Override
        public int getItemCount() {
            if (data != null) {
                return data.size();
            }
            return 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView leftText, rightTime;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getServerWorkData1();
                    notifyCode=data.get(getLayoutPosition()).notifycode;
//                    if(data.get(getLayoutPosition()).notifytitle.contains("请假"))
                    if(data.get(getLayoutPosition()).notifykind.contains("1"))
                        getWorkLeaveMessage(data.get(getLayoutPosition()).notifycode);
                   else if(data.get(getLayoutPosition()).notifykind.contains("4"))
//                    else if(data.get(getLayoutPosition()).notifytitle.contains("加班"))
                        getWorkOvertimeMessage(data.get(getLayoutPosition()).notifycode);
//                    else if(data.get(getLayoutPosition()).notifytitle.contains("外出"))
                    else if(data.get(getLayoutPosition()).notifykind.contains("2"))
                    getWorkOutMessage(data.get(getLayoutPosition()).notifycode);
//                    else if(data.get(getLayoutPosition()).notifytitle.contains("加入团队"))
                    else if(data.get(getLayoutPosition()).notifykind.contains("5"))
                        getWorkJoinMessage(data.get(getLayoutPosition()).notifycode);
                    else if(data.get(getLayoutPosition()).notifykind.contains("3"))
                        getWorkChuMessage(data.get(getLayoutPosition()).notifycode);

                }
            });
            leftText = (TextView) itemView.findViewById(R.id.left_text);
            rightTime = (TextView) itemView.findViewById(R.id.right_time);
        }
    }

    /**
     * 加载企业公告的数据
     */
    private class MyAdapter2 extends RecyclerView.Adapter<MyViewHolder2> {
        @Override
        public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder2 myViewHolder2 = new MyViewHolder2(LayoutInflater.from(mActivity)
                    .inflate(R.layout.messageaa_affiche_item, parent, false));
            return myViewHolder2;
        }

        @Override
        public void onBindViewHolder(MyViewHolder2 holder, int position) {
            holder.afficheLeftText.setText(afficheData.get(position).notifytitle);
            holder.afficheRightTime.setText(afficheData.get(position).notifydate);
        }

        @Override
        public int getItemCount() {
            if (afficheData != null) {
                return afficheData.size();
            }
            return 0;
        }
    }

    class MyViewHolder2 extends RecyclerView.ViewHolder {
        ImageView afficheLeftPic;
        TextView  afficheLeftText;
        TextView  afficheRightTime;
        ImageView afficheRightPic;

        public MyViewHolder2(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, affichePartData.class);
                    PrefUtils.putString(mActivity, "notifycode", getAffiche.result.data.get(getLayoutPosition()).notifycode);
                    mActivity.startActivity(intent);
                }
            });
            afficheLeftPic = (ImageView) itemView.findViewById(R.id.affiche_left_pic);
            afficheLeftText = (TextView) itemView.findViewById(R.id.affiche_left_text);
            afficheRightTime = (TextView) itemView.findViewById(R.id.affiche_right_time);
            afficheRightPic = (ImageView) itemView.findViewById(R.id.affiche_right_pic);
        }
    }

    /**
     * 系统消息
     */
    private class MyAdapter3 extends RecyclerView.Adapter<MyViewHolder3> {
        @Override
        public MyViewHolder3 onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder3 myViewHolder3 = new MyViewHolder3(LayoutInflater.from(mActivity)
                    .inflate(R.layout.system_message, parent, false));
            return myViewHolder3;
        }

        @Override
        public void onBindViewHolder(MyViewHolder3 holder, int position) {
            holder.systemLeftText.setText(systemData.get(position).notifytitle);
            holder.systemRightTime.setText(systemData.get(position).notifydate);
            if (systemData.size() != 0) {
                notifycodeSystem = getSystem.result.data.get(position).notifycode;
            }
        }

        @Override
        public int getItemCount() {
            if (systemData != null) {
                return systemData.size();
            }
            return 0;
        }
    }

    class MyViewHolder3 extends RecyclerView.ViewHolder {
        ImageView systemLeftPic;
        TextView  systemLeftText;
        TextView  systemRightTime;
        ImageView systemRightPic;
        public MyViewHolder3(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myAdapter1.notifyDataSetChanged();
                    Intent intent = new Intent(mActivity, MessageSystem.class);
                    System.out.println("点击了第" + getPosition() + "个Item");
                      PrefUtils.putString(mActivity, "notifycodeSystem", notifycodeSystem);
                      mActivity.startActivity(intent);
                }
            });
            systemLeftPic = (ImageView) itemView.findViewById(R.id.system_left_pic);
            systemLeftText = (TextView) itemView.findViewById(R.id.system_left_text);
            systemRightTime = (TextView) itemView.findViewById(R.id.system_right_time);
            systemRightPic = (ImageView) itemView.findViewById(R.id.system_right_pic);
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
            return view == object;//官方推荐写法
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
            case R.id.message_center_papger_sign:
                mActivity.startActivity(new Intent(mActivity, ConfirmActivity.class));
                System.out.println("被点击了");
                break;
            default:
                break;
        }
    }
}