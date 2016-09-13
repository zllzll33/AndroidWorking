package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.AttAreaBean;
import com.luofangyun.shangchao.domain.MyAttArea;
import com.luofangyun.shangchao.domain.SystemTime;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用→考勤→考勤列表展示
 */

public class AttAreaActivity extends BaseActivity {
    private int  i, j;
    private View view, view1, view2;
    private TextView attAreaDate, attAreaDate2;
    private RecyclerView             attAreaRlv2;
    private PullLoadMoreRecyclerView attAreaRlv;
    private MyAdapter                myAdapter;
    private MyAdapter2               myAdapter2;
    private Map<String, String>      map1, map2, map3;
    private ArrayList<String> mTitleList = new ArrayList<>();//页卡标题集合
    private ArrayList<View>    mViewList = new ArrayList<>();   //页卡视图集合
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private static final int TYPE1  = 1;
    private static final int TYPE2  = 2;
    private static String[] titles = {"上班  9:00", "正常打卡", "迟    到", "区域外打卡", "未打卡", "下班  " +
            "18:00", "正常打卡", "早    退", "区域外打卡", "未打卡", "其    他", "请假人数"};
    private int[]          peops;
    private MyPagerAdapter mAdapter;
    private Calendar       now;
    private String         week;
    private int            sb_normal, sb_late, sb_nosignin, xb_normal, xb_early, xb_nosignin,
            leave, sb_outrange, xb_outrange, allPeops;
    private String opflag;
    private String nowTimeText;
    private int[] attstatus = {0, 0, 1, 2, 3, 0, 4, 5, 6, 7, 0, 8};
    private int          month;
    private MyAttArea    myAttArea;
    private  String maxdate2, mindate2, maxdate1, mindate1,curdate2;
    private Map<String, String> mapSystem = new HashMap<>();
    private Map<String, String> mapSystem1 = new HashMap<>();
    private SystemTime systemTime1, systemTime2;
    private String     attAreaChangeate;
    private String dateTime2;
    private String curdate1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        i = 0;
        j = 0;
        month = 0;
        Date date = new Date();
        view = View.inflate(this, R.layout.activity_meeting, null);
        view1 = View.inflate(this, R.layout.activity_att_area, null);
        view2 = View.inflate(this, R.layout.activity_att_area2, null);
        initView();
        initData();
    }
    private void initView() {
        mTabLayout = (TabLayout) view.findViewById(R.id.meeting_tab);
        mViewPager = (ViewPager) view.findViewById(R.id.meeting_vp);
        view1.findViewById(R.id.left_choice).setOnClickListener(this);
        attAreaDate = (TextView) view1.findViewById(R.id.att_area_date);
        view1.findViewById(R.id.right_choice).setOnClickListener(this);
        attAreaRlv = (PullLoadMoreRecyclerView) view1.findViewById(R.id.att_area_rlv);
        view2.findViewById(R.id.left_choice2).setOnClickListener(this);
        attAreaDate2 = (TextView) view2.findViewById(R.id.att_area_date2);
        view2.findViewById(R.id.right_choice2).setOnClickListener(this);
        attAreaRlv2 = (RecyclerView) view2.findViewById(R.id.att_area_rlv2);
    }
    private void initData() {
        getSystemTime();   //yyyy-mm
        getSystemTime1();  //yyyy-mm-dd
        String attAreaChangeate2 = PrefUtils.getString(this, "attAreaChangeate2", UiUtils
                .refFormatNowDate("yyyy-MM-dd"));
        attAreaChangeate = attAreaDate.getText().toString().trim();
        now = Calendar.getInstance();
        nowTimeText = UiUtils.refFormatNowDate("yyyy-MM-dd");
        Date date = new Date();
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
        week = dateFm.format(date);
        attAreaDate.setText(UiUtils.refFormatNowDate("yyyy-MM"));
        attAreaDate2.setText(UiUtils.refFormatNowDate("yyyy-MM-dd"));
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        map3 = new HashMap<>();
        UiUtils.getFontBehand(month);
        getServerData2(attAreaChangeate2);
        getServerData1();
        attAreaRlv.setLinearLayout();
        myAdapter = new MyAdapter();
        attAreaRlv.setAdapter(myAdapter);
        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        //添加页卡标题
        mTitleList.add("我的考勤");
        mTitleList.add("团队考勤");
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);             //设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0))); //添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        mAdapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(mAdapter);                         //给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);               //将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);            //给Tabs设置适配器
        titleTv.setText("考勤");
        right.setVisibility(View.VISIBLE);
        right.setText("设置班段");
        attAreaRlv.setOnPullLoadMoreListener(listener);
        flAddress.addView(view);
    }
    private void getSystemTime() {
        try {
            Request<String> request3 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "sys_getime.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            mapSystem.put("access_id", "1234567890");
            mapSystem.put("timestamp", time);
            mapSystem.put("telnum", UiUtils.getPhoneNumber());
            mapSystem.put("opflag", "0");
            String encode = MD5Encoder.encode(Sign.generateSign(mapSystem) +
                    "12345678901234567890123456789011");
            mapSystem.put("sign", encode);
            request3.add(mapSystem);
            CallServer.getRequestInstance().add(this, 3, request3, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getSystemTime1() {
        try {
            Request<String> request4 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "sys_getime.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            mapSystem1.put("access_id", "1234567890");
            mapSystem1.put("timestamp", time);
            mapSystem1.put("telnum", UiUtils.getPhoneNumber());
            mapSystem1.put("opflag", "1");
            String encode = MD5Encoder.encode(Sign.generateSign(mapSystem1) +
                    "12345678901234567890123456789011");
            mapSystem1.put("sign", encode);
            request4.add(mapSystem1);
            CallServer.getRequestInstance().add(this, 4, request4, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PullLoadMoreRecyclerView.PullLoadMoreListener listener = new PullLoadMoreRecyclerView
            .PullLoadMoreListener() {
        @Override
        public void onRefresh() {
            getServerData1();
            UiUtils.upDownRefurbish(attAreaRlv);
        }
        @Override
        public void onLoadMore() {
            getServerData1();
            UiUtils.upDownRefurbish(attAreaRlv);
        }
    };

    private void getServerData1() {
        try {

            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "att_record_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("pindex", String.valueOf(1));
            map1.put("psize", String.valueOf(20));
            map1.put("startdate", UiUtils.firstday);
            map1.put("enddate", UiUtils.lastday);
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request1.add(map1);
            CallServer.getRequestInstance().add(this, 1, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void getServerData2(String nowTime) {
        try {

            Request<String> request2 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "att_report_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map2.put("access_id", "1234567890");
            map2.put("timestamp", time);
            map2.put("telnum", UiUtils.getPhoneNumber());
            map2.put("repdate", nowTime);
            String encode = MD5Encoder.encode(Sign.generateSign(map2) +
                    "12345678901234567890123456789011");
            map2.put("sign", encode);
            request2.add(map2);
            CallServer.getRequestInstance().add(this, 2, request2, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 1:
                    System.out.println("我的考勤=" + response.get());
                    myAttArea = new Gson().fromJson(response.get(), MyAttArea.class);
                    myAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    String result2 = response.get();
                    System.out.println("考勤数据获取=" + result2);
                    AttAreaBean attAreaBean = processData(result2);
                    System.out.println(result2);
                    break;
                case 3:
                    System.out.println("系统时间数据=" + response.get());
                    systemTime1 = new Gson().fromJson(response.get(), SystemTime.class);
                    curdate1 = systemTime1.result.curdate;
                    maxdate1 = systemTime1.result.maxdate;
                    mindate1 = systemTime1.result.mindate;
                    break;
                case 4:
                    System.out.println("系统时间数据2=" + response.get());
                    systemTime2 = new Gson().fromJson(response.get(), SystemTime.class);
                    maxdate2 = systemTime2.result.maxdate;
                    mindate2 = systemTime2.result.mindate;
                    curdate2 = systemTime2.result.curdate;
                    break;
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };

    private AttAreaBean processData(String result2) {
        Gson gson = new Gson();
        AttAreaBean attAreaBean = gson.fromJson(result2, AttAreaBean.class);
        if (attAreaBean.status.equals("00000")) {
            sb_normal = attAreaBean.result.sb_normal;
            sb_late = attAreaBean.result.sb_late;
            sb_nosignin = attAreaBean.result.sb_nosignin;
            xb_normal = attAreaBean.result.xb_normal;
            xb_early = attAreaBean.result.xb_early;
            xb_nosignin = attAreaBean.result.xb_nosignin;
            sb_outrange = attAreaBean.result.sb_outrange;
            xb_outrange = attAreaBean.result.xb_outrange;
            leave = attAreaBean.result.leave;
            allPeops = sb_normal + sb_late + sb_nosignin + xb_normal + xb_early + xb_nosignin
                    + leave + xb_nosignin + xb_nosignin;
            peops = new int[]{0, sb_normal, sb_late, sb_outrange, sb_nosignin, 0, xb_normal,
                    xb_early, xb_outrange, xb_nosignin, 0, leave};
            attAreaRlv2.setLayoutManager(new LinearLayoutManager(getApplication()));
            myAdapter2 = new MyAdapter2();
            attAreaRlv2.setAdapter(myAdapter2);
        }
        return attAreaBean;
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(getApplication())
                    .inflate(R.layout.att_area_item, parent, false));
            return myViewHolder;
        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String attdate = myAttArea.result.data.get(position).attdate;
            holder.attAreaTv1.setText(attdate.substring(attdate.length() - 2, attdate.length()));
            holder.attAreaTv2.setText(UiUtils.getWeek(attdate));
            holder.attAreaTv3.setText(myAttArea.result.data.get(position).sbtime);
            holder.attAreaTv5.setText(myAttArea.result.data.get(position).xbtime);
            switch (myAttArea.result.data.get(position).sbstatu) {
                case 0:             //0:上班准时
                    holder.attAreaTv4.setText("正常");
                    break;
                case 1:             //1:上班迟到
                    holder.attAreaTv4.setText("迟到");
                    break;
                case 2:             //2:上班不在打卡区域
                    holder.attAreaTv4.setText("不在区域打卡");
                    break;
                case 3:             //3:上班未打卡
                    holder.attAreaTv4.setText("未打卡");
                    break;
            }
            switch (myAttArea.result.data.get(position).xbstatu) {
                case 4:             //0:下班准时
                    holder.attAreaTv6.setText("正常");
                    break;
                case 5:             //1:下班迟到
                    holder.attAreaTv6.setText("迟到");
                    break;
                case 6:             //2:下班不在打卡区域
                    holder.attAreaTv6.setText("不在区域打卡");
                    break;
                case 7:             //3:下班未打卡
                    holder.attAreaTv6.setText("未打卡");
                    break;
            }
        }

        @Override
        public int getItemCount() {
            if (myAttArea != null) {
                return myAttArea.result.data.size();
            } else {
                return 0;
            }
        }
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout   linearLayout1;
        RelativeLayout relativeLayout1;
        ImageView      attAreaIv3, attAreaIv4;
        TextView attAreaTv1, attAreaTv2, attAreaTv3, attAreaTv4, attAreaTv5, attAreaTv6;
        public MyViewHolder(View itemView) {
            super(itemView);
            linearLayout1 = (LinearLayout) itemView.findViewById(R.id.linearLayout1);
            relativeLayout1 = (RelativeLayout) itemView.findViewById(R.id.relativeLayout1);
            attAreaIv3 = (ImageView) itemView.findViewById(R.id.att_area_iv3);
            attAreaIv4 = (ImageView) itemView.findViewById(R.id.att_area_iv4);
            attAreaTv1 = (TextView) itemView.findViewById(R.id.att_area_tv1);
            attAreaTv2 = (TextView) itemView.findViewById(R.id.att_area_tv2);
            attAreaTv3 = (TextView) itemView.findViewById(R.id.att_area_tv3);
            attAreaTv4 = (TextView) itemView.findViewById(R.id.att_area_tv4);
            attAreaTv5 = (TextView) itemView.findViewById(R.id.att_area_tv5);
            attAreaTv6 = (TextView) itemView.findViewById(R.id.att_area_tv6);
        }
    }

    private class MyAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE1) {
                MyItmeViewHolder1 myItmeViewHolder1 = new MyItmeViewHolder1(LayoutInflater.from
                        (getApplication()
                        ).inflate(R.layout.itme1, parent, false));
                return myItmeViewHolder1;
            } else {
                return new MyItmeViewHolder2(LayoutInflater.from(getApplication()).inflate(R
                        .layout.item2, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyItmeViewHolder1) {
                ((MyItmeViewHolder1) holder).item1Tv.setText(titles[position]);
            } else if (holder instanceof MyItmeViewHolder2) {
                ((MyItmeViewHolder2) holder).item2Title.setText(titles[position]);
                ((MyItmeViewHolder2) holder).item2Peops.setText(peops[position] + "人");
                ((MyItmeViewHolder2) holder).item2Pb.setProgress((int) ((double) peops[1] /
                        allPeops * 100));
                if (position == 4 || position == 8 || position == titles.length - 1) {
                    ((MyItmeViewHolder2) holder).item2Line.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 || position == 5 || position == 10 ? TYPE1 : TYPE2;
        }
    }

    class MyItmeViewHolder1 extends RecyclerView.ViewHolder {
        TextView item1Tv;

        public MyItmeViewHolder1(View inflate) {
            super(inflate);
            item1Tv = (TextView) inflate.findViewById(R.id.item1_tv);
        }
    }

    class MyItmeViewHolder2 extends RecyclerView.ViewHolder {
        TextView item2Title, item2Peops, item2Line;
        ImageView   item2Iv;
        ProgressBar item2Pb;

        public MyItmeViewHolder2(View inflate) {
            super(inflate);
            if (getLayoutPosition() != 0 || getLayoutPosition() != 5 || getLayoutPosition() != 10) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplication(), PeopDetailActivity.class);
                        intent.putExtra("repdate", attAreaDate2.getText().toString().trim());
                        intent.putExtra("attstatu", attstatus[getLayoutPosition()]);
                        startActivity(intent);
                    }
                });
            }
            item2Title = (TextView) inflate.findViewById(R.id.item2_title);
            item2Iv = (ImageView) inflate.findViewById(R.id.item2_iv);
            item2Peops = (TextView) inflate.findViewById(R.id.item2_peops);
            item2Pb = (ProgressBar) inflate.findViewById(R.id.item2_pb);
            item2Line = (TextView) inflate.findViewById(R.id.item2_line);
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.right:
                startActivity(new Intent(getApplication(), ClassActivity.class));
                break;
            case R.id.left_choice:
                dateTime2 = UiUtils.getDateTime(Calendar.MONTH, j--, "yyyy-MM");
                if (UiUtils.timeToMill("yyyy-MM", dateTime2) >= UiUtils.timeToMill("yyyy-MM", mindate1)) {
                    month--;
                    getServerData1();
                    attAreaDate.setText(dateTime2);
                }  else {
                    UiUtils.ToastUtils("没有了,不能查询更久以前的数据了");
                }
                break;
            case R.id.right_choice:
                dateTime2 =  UiUtils.getDateTime(Calendar.MONTH, j++, "yyyy-MM");
                if (UiUtils.timeToMill("yyyy-MM", dateTime2) <= UiUtils.timeToMill("yyyy-MM", maxdate1)) {
                    month++;
                    getServerData1();
                    attAreaDate.setText(dateTime2);
                } else {
                    UiUtils.ToastUtils("没有更新的数据可以查看了");
                }
                break;
            case R.id.left_choice2:
                String dateTime3 = UiUtils.getDateTime(Calendar.DATE, i--, "yyyy-MM-dd");
                if (UiUtils.timeToMill("yyyy-MM-dd", dateTime3) >= UiUtils.timeToMill("yyyy-MM-dd", mindate2)) {
                    getServerData2(dateTime3);
                    attAreaDate2.setText(dateTime3);
                    PrefUtils.putString(this, "attAreaChangeate2", attAreaDate2.getText().toString()
                            .trim());
                } else {
                    UiUtils.ToastUtils("没有更多数据可以查询");
                }
                break;
            case R.id.right_choice2:
                if (UiUtils.timeToMill("yyyy-MM-dd", curdate2) > UiUtils.timeToMill("yyyy-MM-dd",
                        maxdate2)) {
                    return;
                }
                getServerData2(curdate2);
                attAreaDate2.setText(curdate2);
                PrefUtils.putString(this, "attAreaChangeate2", attAreaDate2.getText().toString()
                        .trim());

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
