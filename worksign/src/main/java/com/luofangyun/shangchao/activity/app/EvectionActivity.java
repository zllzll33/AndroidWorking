package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.impl.AppBaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.domain.EmpTravel;
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
 * 应用→工作→出差
 */

public class EvectionActivity extends AppBaseActivity {

    private EmpTravel empTravelText, empTravelText1, empTravelText2;
    private MyAdapter                        myAdapter;
    private MyAdapter1                       myAdapter1;
    private MyAdapter2                       myAdapter2;
    private ArrayList<EmpTravel.Result.Json> dataList2;
    private ArrayList<EmpTravel.Result.Json> dataList1;
    private ArrayList<EmpTravel.Result.Json> dataList;
    private int i, j = 1, k = 1, l = 1;
    private Map<String, String> map3 = new HashMap<>();
    private EmpTravel empTravel;
    private EmpTravel empTravel3;
    private EmpTravel empTravel1;
    private EmpTravel empTravel2;
   public static  Handler hander ;
    @Override
    public void initData() {
        super.initData();
        getMySelf("emp_travel_list.json", j, httpListener);       //我发起的
        getWaitExamine("emp_travel_list.json", k, httpListener);  //待审批请求数据
        getExamine("emp_travel_list.json", l, httpListener);      //已审批请求数据
        myLeaveRlv.setLinearLayout();
        myAdapter = new MyAdapter();
        myLeaveRlv.setAdapter(myAdapter);
        leaveRlv.setLinearLayout();
        myAdapter1 = new MyAdapter1();
        leaveRlv.setAdapter(myAdapter1);
        examineAboptRlv.setLinearLayout();
        myAdapter2 = new MyAdapter2();
        examineAboptRlv.setAdapter(myAdapter2);

        titleTv.setText("出差");
        right.setVisibility(View.VISIBLE);
        myLeaveRb.setText("我的出差");
        leaveExamineRb.setText("我的审批");
        right.setText("写申请");
        right.setOnClickListener(this);
        upDownRefurbish();
        hander=new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                getMySelf("emp_travel_list.json", j, httpListener);       //我发起的
                getWaitExamine("emp_travel_list.json", k, httpListener);  //待审批请求数据
                getExamine("emp_travel_list.json", l, httpListener);      //已审批请求数据
            }
        };
    }
    private void upDownRefurbish() {
        myLeaveRlv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
               // getMySelf("emp_travel_list.json", j, httpListener);
                UiUtils.upDownRefurbish(myLeaveRlv);
            }

            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(myLeaveRlv);
               /* if (j < empTravel3.result.totalpage) {
                    getMySelf("emp_travel_list.json", ++j, httpListener);
                    UiUtils.upDownRefurbish(myLeaveRlv);
                } else {
                    UiUtils.ToastUtils("没有更多数据了");
                    UiUtils.upDownRefurbish(myLeaveRlv);
                }*/
            }
        });
        leaveRlv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
//                getWaitExamine("emp_travel_list.json", k, httpListener);
                UiUtils.upDownRefurbish(leaveRlv);
            }

            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(myLeaveRlv);
               /* if (k < empTravel1.result.totalpage) {
                    getWaitExamine("emp_travel_list.json", ++k, httpListener);
                    UiUtils.upDownRefurbish(leaveRlv);
                }
                else {
                    UiUtils.upDownRefurbish(leaveRlv);
                    UiUtils.ToastUtils("没有更多数据了");
                }*/
            }
        });
        examineAboptRlv.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView
                .PullLoadMoreListener() {
            @Override
            public void onRefresh() {
//                getExamine("emp_overtime_list.json", l, httpListener);
                UiUtils.upDownRefurbish(examineAboptRlv);
            }
            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(myLeaveRlv);
              /*  if (k < empTravel2.result.totalpage) {
                    getExamine("emp_overtime_list.json", ++l, httpListener);
                    UiUtils.upDownRefurbish(examineAboptRlv);
                }
                else {
                    UiUtils.upDownRefurbish(examineAboptRlv);
                    UiUtils.ToastUtils("没有更多数据了");
                }*/
            }
        });
    }
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 0:
                    String result = response.get();
                    System.out.println("我发起的出差result=" + result);
                    empTravel3 = processData(result);
                    myAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    String result1 = response.get();
                    System.out.println("未审批的出差result1=" + result1);
                    empTravel1 = processData1(result1);
                    myAdapter1.notifyDataSetChanged();
                    break;
                case 2:
                    String result2 = response.get();
                    System.out.println("已审批的出差result2=" + result2);
                    empTravel2 = processData2(result2);
                    myAdapter2.notifyDataSetChanged();
                    break;
                case 3:
                    ApplyBean applyBean = new Gson().fromJson(response.get(), ApplyBean.class);
                    dataList.remove(i);
                    myAdapter.notifyDataSetChanged();
                    UiUtils.ToastUtils(applyBean.summary);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    private EmpTravel processData2(String json) {
        Gson gson = new Gson();
        empTravelText2 = gson.fromJson(json, EmpTravel.class);
        if (empTravelText2.status.equals("00000")) {
            dataList2 = empTravelText2.result.data;
        }
        return empTravelText2;
    }

    private EmpTravel processData1(String json) {
        Gson gson = new Gson();
        empTravelText1 = gson.fromJson(json, EmpTravel.class);
        if (empTravelText1.status.equals("00000")) {
            dataList1 = empTravelText1.result.data;
        }
        return empTravelText1;
    }

    private EmpTravel processData(String json) {
        Gson gson = new Gson();
        empTravelText = gson.fromJson(json, EmpTravel.class);
        if (empTravelText.status.equals("00000")) {
            dataList = empTravelText.result.data;
        }
        return empTravelText;
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
            holder.workDays.setText(dataList.get(position).traveldays);
            if (dataList.get(position).statu.equals("0")) {
                holder.workState.setText("审批中");
            } else if (dataList.get(position).statu.equals("1")) {
                holder.workState.setText("审批通过");
                holder.workDaysTv.setVisibility(View.GONE);
            } else if (dataList.get(position).statu.equals("2")) {
                holder.workState.setText("审批拒绝");
                holder.workDaysTv.setVisibility(View.GONE);
            }
            holder.cancleTv.setVisibility(View.VISIBLE);
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

    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView workDaysTv, workDays, workState, starttime, endtime, cancleTv;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), LeavetypeListActivity.class);
                    intent.putExtra("traveladdress", dataList.get(getLayoutPosition())
                            .traveladdress);
                    intent.putExtra("stattime", dataList.get(getLayoutPosition()).stattime);
                    intent.putExtra("endtime", dataList.get(getLayoutPosition()).endtime);
                    intent.putExtra("traveldays", dataList.get(getLayoutPosition()).traveldays);
                    intent.putExtra("statu", dataList.get(getLayoutPosition()).statu + "");
                    intent.putExtra("travelreason", dataList.get(getLayoutPosition()).travelreason);
                    intent.setAction("travelletail");
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
                getDeleteData(dataList.get(getLayoutPosition()).travelcode);
            }
        }
    }

    private void getDeleteData(String code) {
        try {
            Request<String> request4 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "travel_cancel.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map3.put("access_id", "1234567890");
            map3.put("timestamp", time);
            map3.put("telnum", UiUtils.getPhoneNumber());
            map3.put("travelcode", code);
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
            holder.workDays.setText(dataList1.get(position).traveldays);
            if (dataList1.get(position).statu.equals("0")) {
                holder.workState.setText("审批中");
            } else if (dataList1.get(position).statu.equals("1")) {
                holder.workState.setText("审批通过");
            } else if (dataList1.get(position).statu.equals("2")) {
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
                    intent.putExtra("traveladdress", dataList1.get(getLayoutPosition())
                            .traveladdress);
                    intent.putExtra("stattime", dataList1.get(getLayoutPosition()).stattime);
                    intent.putExtra("endtime", dataList1.get(getLayoutPosition()).endtime);
                    intent.putExtra("traveldays", dataList1.get(getLayoutPosition()).traveldays);
                    intent.putExtra("statu", dataList1.get(getLayoutPosition()).statu + "");
                    intent.putExtra("travelreason", dataList1.get(getLayoutPosition())
                            .travelreason);
                    intent.putExtra("travelcode", dataList1.get(getLayoutPosition()).travelcode);
                    intent.setAction("travelwaitletail");
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
            holder.workDays.setText(dataList2.get(position).traveldays);
            if (dataList2.get(position).statu.equals("0")) {
                holder.workState.setText("审批中");
            } else if (dataList2.get(position).statu.equals("1")) {
                holder.workState.setText("审批通过");
            } else if (dataList2.get(position).statu.equals("2")) {
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
                    intent.putExtra("traveladdress", dataList2.get(getLayoutPosition())
                            .traveladdress);
                    intent.putExtra("stattime", dataList2.get(getLayoutPosition()).stattime);
                    intent.putExtra("endtime", dataList2.get(getLayoutPosition()).endtime);
                    intent.putExtra("traveldays", dataList2.get(getLayoutPosition()).traveldays);
                    intent.putExtra("statu", dataList2.get(getLayoutPosition()).statu + "");
                    intent.putExtra("travelreason", dataList2.get(getLayoutPosition())
                            .travelreason);
                    intent.setAction("travelfinishletail");
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
        switch (v.getId()) {
            case R.id.right:
                Intent intent = new Intent(this, LeavetypeListActivity.class);
                intent.setAction("EvectionActivity");
                startActivity(intent);
                break;
            default:
                break;
        }
    }

}
