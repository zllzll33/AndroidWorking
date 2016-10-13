package com.luofangyun.shangchao.activity.addresslist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.message.ConfirmActivity;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.domain.Corpor;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.ui.SwipeLayout;
import com.luofangyun.shangchao.utils.MD5Encoder;
import com.luofangyun.shangchao.utils.PrefUtils;
import com.luofangyun.shangchao.utils.Sign;

import com.luofangyun.shangchao.utils.UiUtils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 企业通讯录
 */
public class CorporActivity extends BaseActivity {
    private TextView addEmpl, addBranch, message, invite;
    private View         view;
    private RecyclerView corporRlv, branchHor;
    private MyAdapter myAdapter;
    private String    addBranchName;
    private Map<String, String> map  = new HashMap<>();
    private Map<String, String> map1 = new HashMap<>();
    private Map<String, String> map3 = new HashMap<>();
    private String                        companyname,branchName,branchCode,parentName,parentCode;
    private Corpor                        corpor;
    private ApplyBean                     applyBean;
    private ArrayList<Corpor.Result.Data> dataList, dataList1;
    public  ArrayList<Corpor.Result.Emps> empsList;
    private final int TYPE1 = 1, TYPE2 = 2;
    private Corpor corpor1;
    private MyAdapter1 myAdapter1;
    private int i;
    private TextView one;
    public static Handler handler;
    LinearLayout ll_pop;
    boolean isBigBranch=true;
    int deletenum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_corpor, null);
        initView();
        initData();
        handler=new Handler()
        {
            public void handleMessage(Message msg)
            {
                getServerData();
            }
        };
    }

    private void initView() {
        corporRlv = (RecyclerView) view.findViewById(R.id.corpor_rlv);
        branchHor = (RecyclerView) view.findViewById(R.id.branch_hor);
        one = (TextView) view.findViewById(R.id.one);
    }
    private void initData() {
//        one.setOnClickListener(this);
        addBranchName = PrefUtils.getString(this, "addBranchName", null);
        titleTv.setText("企业通讯录");
        getServerData();
        corporRlv.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter();
        corporRlv.setAdapter(myAdapter);
        /*corporRlv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                     //SCROLL_STATE_TOUCH_SCROLL
                    for (int i = 0; i < openedSwipeLayout.size(); i++) {
                        openedSwipeLayout.get(i).close();
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });*/
        titleIvRight.setVisibility(View.VISIBLE);
        flAddress.addView(view);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.title_iv_right:
                View view = UiUtils.getPopuwindow(R.layout.corpor_drop_down, titleBackground,
                        screenWidth, 0);
                ll_pop=(LinearLayout) view.findViewById(R.id.ll_pop);
                int ismng=PrefUtils.getInt(getApplicationContext(),"ismng",0);
                        if(ismng==0)
                            ll_pop.setVisibility(View.GONE);
                addEmpl = (TextView) view.findViewById(R.id.add_empl);
                addBranch = (TextView) view.findViewById(R.id.add_branch);
                message = (TextView) view.findViewById(R.id.message);
                invite = (TextView) view.findViewById(R.id.invite);
                addEmpl.setOnClickListener(this);
                addBranch.setOnClickListener(this);
                message.setOnClickListener(this);
                invite.setOnClickListener(this);
                break;
            case R.id.add_empl:            //添加员工
                Intent intent2=new Intent(this, AddEmplActivity.class);

                if(branchCode==null)
                {
                    intent2.putExtra("parentdept", "0");
                    intent2.putExtra("parentname", "");
                }
                else {
                    intent2.putExtra("parentdept",branchCode);
                    intent2.putExtra("parentname", branchName);
                }
                startActivity(intent2);
                UiUtils.popupWindow.dismiss();
                finish();
                break;
            case R.id.add_branch:         //添加部门
                if(isBigBranch==true) {
                    Intent intent = new Intent(this, AddBranchActivity.class);
                    intent.setAction("addBranch");
                    startActivityForResult(intent, 2);
                }
                else
                {
                    Intent intent = new Intent(getApplication(), AddBranchActivity.class);
                    intent.setAction("editBranch");
                    Bundle bundle=new Bundle();
                    bundle.putBoolean("addNewBr",true);
                    intent.putExtra("bundle",bundle);
                    startActivity(intent);
                }
                finish();
                UiUtils.popupWindow.dismiss();
                break;
            case R.id.message:            //编辑部门信息
        /*        if(isBigBranch==true) {
                    Intent  intent2=new Intent(this, AddBranchActivity.class);
                    intent2.setAction("addBranch");
                    startActivityForResult(intent2, 2);
                }
                else*/
                {
                    Intent  intent1=new Intent(this, AddBranchActivity.class);
                    intent1.setAction("editBranch");
                    Bundle bundle=new Bundle();
                    bundle.putBoolean("addNewBr",false);
                    PrefUtils.putString(getApplicationContext(),"branchCode",branchCode);
                    PrefUtils.putString(getApplicationContext(),"branchname",branchName);
                    if(parentCode==null)
                    {
                        bundle.putString("parentdept", "0");
                        bundle.putString("parentname", "");
                    }
                    else {
                        bundle.putString("parentdept",parentCode);
                        bundle.putString("parentname", parentName);
                    }
                    intent1.putExtra("bundle",bundle);
                    startActivityForResult(intent1, 2);
                }
               finish();
                UiUtils.popupWindow.dismiss();
                break;
            case R.id.invite:             //邀请同事
                Intent intent1=new Intent(this,AddressListActivity.class);
                startActivity(intent1);
                UiUtils.popupWindow.dismiss();
                break;
            case R.id.one:
                getServerData();
                branchHor.setAdapter(null);
              //  branchHor.removeAllViews();
              //  myAdapter.notifyDataSetChanged();
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                break;
            case 2:
                getServerData();
                break;
            default:
                break;
        }
    }
    private void getServerData() {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "dept_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pindex", String.valueOf(1));
            map.put("psize", String.valueOf(100));
            map.put("companycode", PrefUtils.getString(getApplication(), "companycode", null));
            map.put("parentcode", String.valueOf(0));
            map.put("showemp", String.valueOf(1));
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request1.add(map);
            CallServer.getRequestInstance().add(this, 0, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getServerNextData(String code) {
        try {
            Request<String> request3 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "dept_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map3.put("access_id", "1234567890");
            map3.put("timestamp", time);
            map3.put("telnum", UiUtils.getPhoneNumber());
            map3.put("pindex", String.valueOf(1));
            map3.put("psize", String.valueOf(20));
            map3.put("companycode", PrefUtils.getString(getApplication(), "companycode", null));
            map3.put("parentcode", code);
            map3.put("showemp", String.valueOf(1));
            String encode = MD5Encoder.encode(Sign.generateSign(map3) +
                    "12345678901234567890123456789011");
            map3.put("sign", encode);
            request3.add(map3);
            CallServer.getRequestInstance().add(this, 2, request3, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getServerDeleteData(String id) {
        try {
            Request<String> request2 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "dept_del.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("deptcode", id);
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request2.add(map1);
            CallServer.getRequestInstance().add(this, 1, request2, httpListener, false, false);
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
                    System.out.println("获取部门信息=" + result);
                    Corpor corpor = processData(result);
                    myAdapter = new MyAdapter();
                    corporRlv.setAdapter(myAdapter);
//                    myAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    getServerData();
 /*                   String result1 = response.get();
                    processDeleteData(result1);
                    System.out.println("删除result1=" + result1);*/
                    break;
                case 2:
                    corpor1 = new Gson().fromJson(response.get(), Corpor.class);
                    if (corpor1.status.equals("00000")) {
                        Corpor corpor2 = processData(response.get());
                        myAdapter.notifyDataSetChanged();
                    }
            }
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    private Corpor processData(String result) {
        Gson gson = new Gson();
        corpor = gson.fromJson(result, Corpor.class);
        if (corpor.status.equals("00000")) {
            empsList = corpor.result.emps;
            dataList = corpor.result.data;
        }
        return corpor;
    }

    private void processDeleteData(String result1) {
        Gson gson = new Gson();
        applyBean = gson.fromJson(result1, ApplyBean.class);
       /*     if (applyBean.status.equals("00000")) {
                dataList.remove(deletenum);
                myAdapter.notifyDataSetChanged();
//                myAdapter.notifyItemRemoved(deletenum);
            } else {
                UiUtils.ToastUtils(applyBean.summary);
            }*/
    }
    private class MyAdapter1 extends RecyclerView.Adapter<MyViewHolder3>{

        @Override
        public MyViewHolder3 onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder3(LayoutInflater.from(getApplication()).inflate(R.layout.corpor_item_hor,parent,false));
        }
        @Override
        public void onBindViewHolder(MyViewHolder3 holder, int position) {
             holder.corpor_tv2.setText(branchName);
            PrefUtils.putString(getApplicationContext(),"branchCode",branchCode);
             PrefUtils.putString(getApplicationContext(),"branchname",branchName);
             isBigBranch=false;

        }
        @Override
        public int getItemCount() {
            return 1;
        }
    }
    class MyViewHolder3 extends RecyclerView.ViewHolder{
        TextView corpor_tv2;
        public MyViewHolder3(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getServerNextData(dataList.get(getLayoutPosition()).deptcode);
                   // myAdapter1.notifyDataSetChanged();
                }
            });
            corpor_tv2 = (TextView) itemView.findViewById(R.id.corpor_tv2);
        }
    }
    private class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE1) {
                return new MyViewHolder1(LayoutInflater.from(getApplication()).inflate(R
                        .layout.corpor_item_ver, parent, false));
            } else {
                return new MyViewHolder2(LayoutInflater.from(getApplication()).inflate(R
                        .layout.branche_item, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyViewHolder1) {
                ((MyViewHolder1) holder).corporTv.setText(dataList.get(position).deptname);
            } else if (holder instanceof MyViewHolder2) {
                ((MyViewHolder2) holder).brancheTv.setText(empsList.get(position - dataList.size
                        ()).empname);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position >= 0 && position < dataList.size() ? TYPE1 : TYPE2;
        }

        @Override
        public long getItemId(int position) {
            i = position;
            return position;
        }

        @Override
        public int getItemCount() {
            if (empsList != null && dataList != null) {
                return empsList.size() + dataList.size();
            } else if (empsList == null && dataList != null) {
                return dataList.size();
            } else if (empsList != null & dataList == null) {
                return empsList.size();
            } else {
                return 0;
            }
        }
    }

    private class MyViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout addressEmptFl1;
        ImageView    corporIv, corporRightIv;
        TextView corporTv, corporDelete;
        SwipeLayout swipeLayout;
        public MyViewHolder1(View inflate) {
            super(inflate);
            addressEmptFl1 = (LinearLayout) inflate.findViewById(R.id.address_empt_fl1);
            corporIv = (ImageView) inflate.findViewById(R.id.corpor_iv);
            corporTv = (TextView) inflate.findViewById(R.id.corpor_tv);
            corporRightIv = (ImageView) inflate.findViewById(R.id.corpor_right_iv);
            corporDelete = (TextView) inflate.findViewById(R.id.corpor_delete);
            swipeLayout = (SwipeLayout) inflate.findViewById(R.id.swipelayout);
            LinearLayout ll_delete=(LinearLayout)inflate.findViewById(R.id.ll_delete);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right,ll_delete);
            swipeLayout.close();
            swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getServerNextData(dataList.get(getLayoutPosition()).deptcode);
                    branchHor.setLayoutManager(new LinearLayoutManager(getApplication()));
                    myAdapter1 = new MyAdapter1();
                    branchName=dataList.get(getLayoutPosition()).deptname;
                    branchCode=dataList.get(getLayoutPosition()).deptcode;
                    parentName=dataList.get(getLayoutPosition()).parentdeptname;
                    parentCode=dataList.get(getLayoutPosition()).parentdept;

                    branchHor.setAdapter(myAdapter1);
                }

        });
            corporDelete.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.corpor_delete:
                    getServerDeleteData(dataList.get(getPosition()).deptcode);
                    deletenum=getPosition();
                   /* if (applyBean != null) {
                        if (applyBean.status.equals("00000")) {
                            myAdapter.notifyItemRemoved(getPosition());
                        } else {
                            UiUtils.ToastUtils(applyBean.summary);
                        }
                    }*/
                    break;
            }
        }
    }
    private class MyViewHolder2 extends RecyclerView.ViewHolder {
        TextView brancheTv;
        public MyViewHolder2(View inflate) {
            super(inflate);
            inflate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getServerData();
                    Intent intent = new Intent(getApplication(), IndiviInfoActivity.class);
                    intent.putExtra("empname", empsList.get(getLayoutPosition() - dataList.size()
                    ).empname);
                    intent.putExtra("empcode", empsList.get(getLayoutPosition() - dataList.size()
                    ).empcode);
                    startActivity(intent);
                }
            });
            brancheTv = (TextView) inflate.findViewById(R.id.branche_tv);
        }
    }
    private ArrayList<SwipeLayout> openedSwipeLayout = new ArrayList<SwipeLayout>();
}