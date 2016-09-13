package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.impl.AppBaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.domain.EmpOvertime;
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
import java.util.Map;

/**
 * 应用→工作→加班
 */
public class EmpOvertimeActivity extends AppBaseActivity {
    private ArrayList<EmpOvertime.Result.Json> dataList, dataList1, dataList2;
    private MyAdapter  myAdapter;
    private MyAdapter1 myAdapter1;
    private MyAdapter2 myAdapter2;
    private Map<String, String> map3 = new HashMap<>();
    private int                 i, j = 1, k = 1, l = 1;

    @Override
    public void initData() {
        super.initData();
        titleTv.setText("加班");
        right.setVisibility(View.VISIBLE);
        myLeaveRb.setText("我的加班");
        leaveExamineRb.setText("我的审批");
        right.setText("写申请");
        right.setOnClickListener(this);
        getMySelf("emp_overtime_list.json", j, httpListener);       //我发起的
        getWaitExamine("emp_overtime_list.json", k, httpListener);  //待审核请求数据
        getExamine("emp_overtime_list.json", l, httpListener);      //已审核请求数据
        myLeaveRlv.setLinearLayout();
        myAdapter = new MyAdapter();
        myLeaveRlv.setAdapter(myAdapter);

        leaveRlv.setLinearLayout();
        myAdapter1 = new MyAdapter1();
        leaveRlv.setAdapter(myAdapter1);

        examineAboptRlv.setLinearLayout();
        myAdapter2 = new MyAdapter2();
        examineAboptRlv.setAdapter(myAdapter2);
        upDownRefurbish();
    }

    private void upDownRefurbish() {
        myLeaveRlv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                UiUtils.upDownRefurbish(myLeaveRlv);
            }

            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(myLeaveRlv);
            }
        });
        leaveRlv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                UiUtils.upDownRefurbish(leaveRlv);
            }

            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(leaveRlv);
            }
        });
        examineAboptRlv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView
                .PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                UiUtils.upDownRefurbish(leaveRlv);
            }
            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(leaveRlv);
            }
        });
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 0:
                    String result = response.get();
                    System.out.println("我发起的加班result=" + result);
                    EmpOvertime empVisit = processData(result);
                    myAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    String result1 = response.get();
                    EmpOvertime empVisit1 = processData1(result1);
                    System.out.println("加班未审批=" + result1);
                    myAdapter1.notifyDataSetChanged();
                    break;
                case 2:
                    String result2 = response.get();
                    EmpOvertime empVisit2 = processData2(result2);
                    System.out.println("加班已审批=" + result2);
                    myAdapter2.notifyDataSetChanged();
                    break;
                case 3:
                    String result3 = response.get();
                    ApplyBean applyBean = new Gson().fromJson(result3, ApplyBean.class);
                    dataList.remove(i);
                    myAdapter.notifyDataSetChanged();
                    UiUtils.ToastUtils(applyBean.summary);
                default:
                    break;
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    private EmpOvertime processData(String result) {
        Gson gson = new Gson();
        EmpOvertime empovertime = gson.fromJson(result, EmpOvertime.class);
        if (empovertime.status.equals("00000")) {
            dataList = empovertime.result.data;
        }
        return empovertime;
    }

    private EmpOvertime processData1(String result1) {
        Gson gson = new Gson();
        EmpOvertime empovertime1 = gson.fromJson(result1, EmpOvertime.class);
        if (empovertime1.status.equals("00000")) {
            dataList1 = empovertime1.result.data;
        }
        return empovertime1;
    }

    private EmpOvertime processData2(String result2) {
        Gson gson = new Gson();
        EmpOvertime empovertime2 = gson.fromJson(result2, EmpOvertime.class);
        if (empovertime2.status.equals("00000")) {
            dataList2 = empovertime2.result.data;
        }
        return empovertime2;
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
            holder.workDays.setText(dataList.get(position).overdays);
            String statu = dataList.get(position).statu;
            if (statu.equals("0")) {
                holder.workState.setText("待审批");
                holder.cancleTv.setVisibility(View.VISIBLE);
            } else if (statu.equals("1")) {
                holder.workState.setText("已审批");
                holder.cancleTv.setVisibility(View.GONE);
            } else if (statu.equals("2")) {
                holder.workState.setText("已审批");
                holder.cancleTv.setVisibility(View.GONE);
            }
            holder.starttime.setText(dataList.get(position).stattime);
            holder.endtime.setText(dataList.get(position).endtime);
        }

        @Override
        public int getItemCount() {
            if (dataList != null) {
                return dataList.size();
            }
            return 0;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView workDaysTv, workDays, workState, starttime, endtime, cancleTv;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), LeavetypeListActivity.class);
                    intent.putExtra("stattime", dataList.get(getLayoutPosition()).stattime);
                    intent.putExtra("endtime", dataList.get(getLayoutPosition()).endtime);
                    intent.putExtra("overdays", dataList.get(getLayoutPosition()).overdays);
                    intent.putExtra("statu", dataList.get(getLayoutPosition()).statu);
                    intent.putExtra("overreason", dataList.get(getLayoutPosition()).overreason);
                    intent.setAction("overletail");
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
                getDeleteData(dataList.get(getLayoutPosition()).overcode);
            }
        }
    }

    private void getDeleteData(String code) {
        try {
            Request<String> request4 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "over_cancel.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map3.put("access_id", "1234567890");
            map3.put("timestamp", time);
            map3.put("telnum", UiUtils.getPhoneNumber());
            map3.put("overcode", code);
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
            holder.workDays.setText(dataList1.get(position).overdays);
            String statu = dataList1.get(position).statu;
            if (statu.equals("0")) {
                holder.workState.setText("待审批");
            } else if (statu.equals("1")) {
                holder.workState.setText("审批成功");
            } else if (statu.equals("2")) {
                holder.workState.setText("审批拒绝");
            }
            holder.starttime.setText(dataList1.get(position).stattime);
            holder.endtime.setText(dataList1.get(position).endtime);
        }

        @Override
        public int getItemCount() {
            if (dataList1 != null) {
                return dataList1.size();
            }
            return 0;
        }
    }

    private class MyViewHolder1 extends RecyclerView.ViewHolder {
        TextView workDaysTv, workDays, workState, starttime, endtime;

        public MyViewHolder1(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), LeavetypeListActivity.class);
                    intent.putExtra("stattime", dataList1.get(getLayoutPosition()).stattime);
                    intent.putExtra("endtime", dataList1.get(getLayoutPosition()).endtime);
                    intent.putExtra("overdays", dataList1.get(getLayoutPosition()).overdays);
                    intent.putExtra("statu", dataList1.get(getLayoutPosition()).statu);
                    intent.putExtra("overreason", dataList1.get(getLayoutPosition()).overreason);
                    intent.putExtra("overcode", dataList1.get(getLayoutPosition()).overcode);
                    intent.setAction("overwaitletail");
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

    private class MyAdapter2 extends RecyclerView.Adapter<MyViewHolder2> {
        @Override
        public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder2 myViewHolder2 = new MyViewHolder2(LayoutInflater.from(getApplication())
                    .inflate(R.layout.app_work_item1, parent, false));
            return myViewHolder2;
        }

        @Override
        public void onBindViewHolder(MyViewHolder2 holder, int position) {
            holder.workDays.setText(dataList2.get(position).overdays);
            String statu = dataList2.get(position).statu;
            if (statu.equals("0")) {
                holder.workState.setText("待审批");
            } else if (statu.equals("1")) {
                holder.workState.setText("审批成功");
            } else if (statu.equals("2")) {
                holder.workState.setText("审批拒绝");
            }
            holder.starttime.setText(dataList2.get(position).stattime);
            holder.endtime.setText(dataList2.get(position).endtime);
        }

        @Override
        public int getItemCount() {
            if (dataList2 != null) {
                return dataList2.size();
            }
            return 0;
        }
    }
    private class MyViewHolder2 extends RecyclerView.ViewHolder {
        TextView workDaysTv, workDays, workState, starttime, endtime;
        public MyViewHolder2(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), LeavetypeListActivity.class);
                    intent.putExtra("stattime", dataList.get(getLayoutPosition()).stattime);
                    intent.putExtra("endtime", dataList.get(getLayoutPosition()).endtime);
                    intent.putExtra("overdays", dataList.get(getLayoutPosition()).overdays);
                    intent.putExtra("statu", dataList.get(getLayoutPosition()).statu);
                    intent.putExtra("overreason", dataList.get(getLayoutPosition()).overreason);
                    intent.setAction("overfinishletail");
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.right) {
            Intent intent = new Intent(getApplication(), LeavetypeListActivity.class);
            intent.setAction("EmpOvertimeActivity");
            startActivity(intent);
        }
    }
}
