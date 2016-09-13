package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.utils.UiUtils;

/**
 * 应用→工作
 */
public class WorkActivity extends BaseActivity {

    private View         view;
    private RecyclerView workRecy;
    private String[] leftText = {"请假", "外出", "出差", "客户拜访", "加班",};
    private int[]    leftPic  = {R.mipmap.qingjia, R.mipmap.waichu, R.mipmap.chuchai,R.mipmap.jia, R.mipmap
            .jiaban};
     private Class[] clazz = {LeaveActivity.class, OutActivity.class, EvectionActivity.class,
             EmpVisitActivity.class, EmpOvertimeActivity.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_work);
        initView();
        initData();
    }
    private void initView() {
        workRecy = (RecyclerView) view.findViewById(R.id.work_recy);
    }

    private void initData() {
        titleTv.setText("工作");
        workRecy.setLayoutManager(new LinearLayoutManager(WorkActivity.this));
        workRecy.setAdapter(new MyAdapter());
        flAddress.addView(view);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(getApplication())
                    .inflate(R.layout.work_item, parent, false));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            if (position == leftText.length - 1) {
                holder.workItemLine.setVisibility(View.GONE);
            }
            holder.workItemTv.setText(leftText[position]);
            holder.workItemIv.setBackgroundResource(leftPic[position]);
        }

        @Override
        public int getItemCount() {
            return leftText.length;
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView workItemTv, workItemLine;
        ImageView workItemIv;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   startActivity(new Intent(getApplication(), clazz[getLayoutPosition()]));
                }
            });
            workItemIv = (ImageView) itemView.findViewById(R.id.work_item_iv);
            workItemTv = (TextView) itemView.findViewById(R.id.work_item_tv);
            workItemLine = (TextView) itemView.findViewById(R.id.work_item_line);
        }
    }
}
