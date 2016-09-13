package com.luofangyun.shangchao.base.impl;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.app.AfficheActivity;
import com.luofangyun.shangchao.activity.app.AttAreaActivity;
import com.luofangyun.shangchao.activity.app.AttSettingctivity;
import com.luofangyun.shangchao.activity.app.LabelActiviity;
import com.luofangyun.shangchao.activity.app.PatrolActivity;
import com.luofangyun.shangchao.activity.app.WorkActivity;
import com.luofangyun.shangchao.activity.app.MeetingActivity;
import com.luofangyun.shangchao.base.BasePager;
import com.luofangyun.shangchao.utils.PrefUtils;

/**
 * 应用
 */

public class ApplicationPager extends BasePager {
    private int[]    pics  = {R.mipmap.biaoqian, R.mipmap.kaoqin, R.mipmap.huiyi,
            R.mipmap.gonggao, R.mipmap.xunjian,
            R.mipmap.gongzuo};
    private String[] names = {"标签", "考勤", "会议", "公告", "巡检", "工作"};
    private RecyclerView recy1, recy2;
    private boolean isSingRecyView;

    public ApplicationPager(Activity activity) {
        super(activity);
    }
    @Override
    public void initData() {
        LayoutInflater layout = mActivity.getLayoutInflater();
        View view = layout.inflate(R.layout.app_pager, null);
        logo.setVisibility(View.VISIBLE);
        titleBack.setVisibility(View.GONE);
        moreRight.setVisibility(View.VISIBLE);
        tvTitle.setText("应用");
        recy1 = (RecyclerView) view.findViewById(R.id.recy1);
        recy2 = (RecyclerView) view.findViewById(R.id.recy2);
        boolean mIsSingRv = PrefUtils.getBoolean(mActivity, "isSingRecyView", isSingRecyView);
        System.out.println("获取状态为：" + mIsSingRv);
        if (mIsSingRv) {
            flContainer.removeAllViews();
            recy2.setVisibility(View.GONE);
            recy1.setVisibility(View.VISIBLE);
            recy1.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager
                    .VERTICAL));                               //设置RecyclerView布局管理器为2列垂直排布
            recy1.setAdapter(new MyAdapter1());
        } else {
            flContainer.removeAllViews();
            recy1.setVisibility(View.GONE);
            recy2.setVisibility(View.VISIBLE);
            recy2.setLayoutManager(new LinearLayoutManager(mActivity));
            recy2.setAdapter(new MyAdapter2());
        }
        flContainer.addView(view);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.more_right:
                if (isSingRecyView) {
                    recy1.setVisibility(View.GONE);
                    recy2.setVisibility(View.VISIBLE);
                    recy2.setLayoutManager(new LinearLayoutManager(mActivity));
                    recy2.setAdapter(new MyAdapter2());
                    isSingRecyView = false;
                    PrefUtils.putBoolean(mActivity, "isSingRecyView", isSingRecyView);

                } else {
                    recy2.setVisibility(View.GONE);
                    recy1.setVisibility(View.VISIBLE);
                    recy1.setLayoutManager(new StaggeredGridLayoutManager(3,
                            StaggeredGridLayoutManager
                                    .VERTICAL));                             //设置RecyclerView布局管理器为3列垂直排布
                    recy1.setAdapter(new MyAdapter1());
                    isSingRecyView = true;
                    PrefUtils.putBoolean(mActivity, "isSingRecyView", isSingRecyView);
                }
                break;
            default:
                break;
        }
    }
    private class MyAdapter1 extends RecyclerView.Adapter<MyViewHolder1> {
        @Override
        public MyViewHolder1 onCreateViewHolder(ViewGroup viewGroup, int i) {
            MyViewHolder1 myViewHolder1 = new MyViewHolder1(LayoutInflater.from(mActivity
                    .getApplicationContext()).inflate(R
                    .layout.item_layout, null));
            return myViewHolder1;
        }
        @Override
        public void onBindViewHolder(MyViewHolder1 myViewHolder, int i) {
            myViewHolder.itemIv.setBackgroundResource(pics[i]);
            myViewHolder.itemTv.setText(names[i]);
        }
        @Override
        public int getItemCount() {
            return pics.length;
        }
    }

    class MyViewHolder1 extends RecyclerView.ViewHolder {
        ImageView itemIv;
        TextView  itemTv;

        public MyViewHolder1(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (getPosition()) {
                        case 0:
                            mActivity.startActivity(new Intent(mActivity, LabelActiviity.class));
                            break;
                        case 1:
                            mActivity.startActivity(new Intent(mActivity, AttSettingctivity.class));
                            break;
                        case 2:
                            mActivity.startActivity(new Intent(mActivity, MeetingActivity.class));
                            break;
                        case 3:
                            mActivity.startActivity(new Intent(mActivity, AfficheActivity.class));
                            break;
                        case 4:
                            mActivity.startActivity(new Intent(mActivity, PatrolActivity.class));
                            break;
                        case 5:
                            mActivity.startActivity(new Intent(mActivity, WorkActivity.class));
                            break;
                    }
                }
            });
            itemIv = (ImageView) itemView.findViewById(R.id.item_iv);
            itemTv = (TextView) itemView.findViewById(R.id.item_tv);
        }
    }

    private class MyAdapter2 extends RecyclerView.Adapter<MyViewHolder2> {
        @Override
        public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder2 myViewHolder2 = new MyViewHolder2(LayoutInflater.from(mActivity)
                    .inflate(R.layout.item_listview_layout, parent, false));
            return myViewHolder2;
        }

        @Override
        public void onBindViewHolder(MyViewHolder2 holder, int position) {
            holder.leftIcon.setBackgroundResource(pics[position]);
            holder.leftIconText.setText(names[position]);
        }

        @Override
        public int getItemCount() {
            return pics.length;
        }
    }

    class MyViewHolder2 extends RecyclerView.ViewHolder {
        ImageView leftIcon;
        TextView  leftIconText;

        public MyViewHolder2(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (getPosition()) {
                        case 0:
                            mActivity.startActivity(new Intent(mActivity, LabelActiviity.class));
                            break;
                        case 1:
                            mActivity.startActivity(new Intent(mActivity, AttAreaActivity.class));
                            break;
                        case 2:
                            mActivity.startActivity(new Intent(mActivity, MeetingActivity.class));
                            break;
                        case 3:
                            mActivity.startActivity(new Intent(mActivity, AfficheActivity.class));
                            break;
                        case 4:
                            mActivity.startActivity(new Intent(mActivity, PatrolActivity.class));
                            break;
                        case 5:
                            mActivity.startActivity(new Intent(mActivity, WorkActivity.class));
                            break;
                    }
                }
            });
            leftIcon = (ImageView) itemView.findViewById(R.id.left_icon);
            leftIconText = (TextView) itemView.findViewById(R.id.left_icon_text);
        }
    }
}