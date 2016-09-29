package com.luofangyun.shangchao.activity.app;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.domain.LeaveType;
import com.luofangyun.shangchao.domain.WorkDetail;
import com.luofangyun.shangchao.domain.WorkLeaveDetail;
import com.luofangyun.shangchao.domain.WorkNotifyDetail;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.DatePickerPopWindow;
import com.luofangyun.shangchao.utils.DateTimePickDialogUtil;
import com.luofangyun.shangchao.utils.MD5Encoder;
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
 * 审核页面
 */

public class LeavetypeListActivity extends BaseActivity {
    private LinearLayout leaveType1, leaveType2, leaveType3, leaveType4, leaveType5, leaveType6;
    private EditText leaveTypeEt1, leaveTypeEt2, leaveTypeEt3, leaveTypeEt4, leaveTypeEt5,
            leaveTypeEt6;
    private TextView typeTvCancel, typeTvConfirm, accede, negate, leavetypeText1,
            leavetypeText2, leavetypeText3, leavetypeText4, leavetypeText5, leavetypeText6;
    private View                view;
    private AlertDialog         leaveType;
    private RecyclerView        typeRlv;
    private Map<String, String> map, map1, map2, map3, map4, map5, map6, map7, map8,
            workMessMap1, workMessMap2, workMessMap3, workMessMap4, workMessMap5, workMessMap6,
            workMessMap7;
    private MyAdapter                   myAdapter;
    private ArrayList<LeaveType.Result> dataList;
    private LinearLayout                leavetypeLl;
    private TextView                    leaveTypePut;
    private String                      action;
    private LocationManager             locationManager;
    private double latitude  = 0;           //经度
    private double longitude = 0;          //纬度
    private double            longitudeText;
    private double            latitudeText;
    private BroadcastReceiver broadcastReceiver;
    public static String LOCATION_BCR = "location_bcr";
    private String              address;    //获取地址
    private int                 id,leaveId;
    private DatePickerPopWindow dppw, dppw1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            double[] data = (double[]) msg.obj;
            System.out.println("经度：" + data[0] + "\t纬度:" + data[1]);
            latitudeText = data[0];
            longitudeText = data[1];
            initialize();
            finish();
        }
    };
    private String         notifycode;
    private WorkNotifyDetail workNotify1 , workNotify3, workNotify4, workNotify5,
            workNotify6, workNotify7;
    private WorkLeaveDetail workNotify2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_leavetype_list);
        action = getIntent().getAction();
        System.out.println("action=" + this.action);
        locationManager = (LocationManager) getSystemService(Context
                .LOCATION_SERVICE);
        initView();
        initData();
    }
    private void initView() {
        leaveTypePut = (TextView) view.findViewById(R.id.leavetype_put);
        leavetypeLl = (LinearLayout) view.findViewById(R.id.leavetype_ll);
        leavetypeText1 = (TextView) view.findViewById(R.id.leavetype_text1);
        leavetypeText2 = (TextView) view.findViewById(R.id.leavetype_text2);
        leavetypeText3 = (TextView) view.findViewById(R.id.leavetype_text3);
        leavetypeText4 = (TextView) view.findViewById(R.id.leavetype_text4);
        leavetypeText5 = (TextView) view.findViewById(R.id.leavetype_text5);
        leavetypeText6 = (TextView) view.findViewById(R.id.leavetype_text6);
        leaveType1 = (LinearLayout) view.findViewById(R.id.leavetype1);
        leaveTypeEt1 = (EditText) view.findViewById(R.id.leavetype_et1);
        leaveType2 = (LinearLayout) view.findViewById(R.id.leavetype2);
        leaveTypeEt2 = (EditText) view.findViewById(R.id.leavetype_et2);
        leaveType3 = (LinearLayout) view.findViewById(R.id.leavetype3);
        leaveTypeEt3 = (EditText) view.findViewById(R.id.leavetype_et3);
        leaveType4 = (LinearLayout) view.findViewById(R.id.leavetype4);
        leaveTypeEt4 = (EditText) view.findViewById(R.id.leavetype_et4);
        leaveType5 = (LinearLayout) view.findViewById(R.id.leavetype5);
        leaveTypeEt5 = (EditText) view.findViewById(R.id.leavetype_et5);
        leaveType6 = (LinearLayout) view.findViewById(R.id.leavetype6);
        leaveTypeEt6 = (EditText) view.findViewById(R.id.leavetype_et6);
        accede = (TextView) view.findViewById(R.id.accede);      //同意
        negate = (TextView) view.findViewById(R.id.negate);      //拒绝
    }

    private void initData() {
        leaveTypePut.setOnClickListener(this);
        accede.setOnClickListener(this);
        negate.setOnClickListener(this);
        map = new HashMap<>();
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        map3 = new HashMap<>();
        map4 = new HashMap<>();
        map5 = new HashMap<>();
        map6 = new HashMap<>();
        map7 = new HashMap<>();
        map8 = new HashMap<>();
        workMessMap1 = new HashMap<>();
        workMessMap2 = new HashMap<>();
        workMessMap3 = new HashMap<>();
        workMessMap4 = new HashMap<>();
        workMessMap5 = new HashMap<>();
        workMessMap6 = new HashMap<>();
        workMessMap7 = new HashMap<>();
        if (action != null) {
            if (action.equals("LeaveActivity")) {
                getRight();
                leaveTypeEt1.setHint("请假类型(必填)");
                titleTv.setText("请假申请");
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(leaveTypeEt1.getText().toString().trim())) {
                            UiUtils.ToastUtils("请假类型不能为空");
                        } else if (TextUtils.isEmpty(leaveTypeEt2.getText().toString().trim())) {
                            UiUtils.ToastUtils("开始时间不能为空");
                        } else if (TextUtils.isEmpty(leaveTypeEt3.getText().toString().trim())) {
                            UiUtils.ToastUtils("结束时间不能为空");
                        }
                        else if (UiUtils.timeToMill("yyyy-MM-dd HH:mm", leaveTypeEt2.getText
                                ().toString().trim()) < UiUtils.timeToMill("yyyy-MM-dd HH:mm",
                                UiUtils.refFormatNowDate("yyyy-MM-dd HH:mm"))) {
                            UiUtils.ToastUtils("开始时间不能比当前时间小");
                        } else if (UiUtils.timeToMill("yyyy-MM-dd HH:mm", leaveTypeEt2.getText
                                ().toString().trim()) > UiUtils.timeToMill("yyyy-MM-dd HH:mm",
                                leaveTypeEt3.getText().toString().trim())) {
                            Log.e("会议开始时间",String.valueOf(UiUtils.timeToMill("yyyy-MM-dd HH:mm",leaveTypeEt2.getText().toString().trim())));
                            Log.e("会议结束时间",String.valueOf(UiUtils.timeToMill("yyyy-MM-dd HH:mm",leaveTypeEt3.getText().toString().trim())));
                            UiUtils.ToastUtils("开始时间不能大于结束时间");
                        } else if (TextUtils.isEmpty(leaveTypeEt4.getText().toString().trim())) {
                            Log.e("会议开始时间",String.valueOf(UiUtils.timeToMill("yyyy-MM-dd HH:mm",leaveTypeEt2.getText().toString().trim())));
                            Log.e("会议结束时间",String.valueOf(UiUtils.timeToMill("yyyy-MM-dd HH:mm",leaveTypeEt3.getText().toString().trim())));
                            UiUtils.ToastUtils("请假天数不能为空");
                        } else if (TextUtils.isEmpty(leaveTypeEt5.getText().toString().trim())) {
                            UiUtils.ToastUtils("理由不能为空");
                        }else if (leaveTypeEt5.getText().toString().trim().length() > 40) {
                            UiUtils.ToastUtils("字数不能超过40个字");
                        } else {
                            getServerApplyLeave();
                        }
                    }
                });
            } else if (action.equals("OutActivity")) {
                getRight();
                leavetypeText4.setText("外出天数");
                titleTv.setText("外出申请");
                leaveTypeEt4.setHint("请填写外出天数");
                leaveType1.setVisibility(View.GONE);
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(leaveTypeEt2.getText().toString().trim())) {
                            UiUtils.ToastUtils("开始时间不能为空");
                        } else if (TextUtils.isEmpty(leaveTypeEt3.getText().toString().trim())) {
                            UiUtils.ToastUtils("结束时间不能为空");
                        } else if (TextUtils.isEmpty(leaveTypeEt4.getText().toString().trim())) {
                            UiUtils.ToastUtils("外出天数不能为空");
                        } else if (TextUtils.isEmpty(leaveTypeEt5.getText().toString().trim())) {
                            UiUtils.ToastUtils("理由不能为空");
                        } else if (UiUtils.timeToMill("yyyy-MM-dd HH:mm", leaveTypeEt2.getText
                                ().toString().trim()) < UiUtils.timeToMill("yyyy-MM-dd HH:mm",
                                UiUtils.refFormatNowDate("yyyy-MM-dd HH:mm"))) {
                            UiUtils.ToastUtils("开始时间不能比当前时间小");
                        } else if (UiUtils.timeToMill("yyyy-MM-dd HH:mm", leaveTypeEt2.getText
                                ().toString().trim()) > UiUtils.timeToMill("yyyy-MM-dd HH:mm",
                                leaveTypeEt3.getText().toString().trim())) {
                            UiUtils.ToastUtils("开始时间不能大于或等于结束时间");
                        } else if (leaveTypeEt5.getText().toString().trim().length() > 40) {
                            UiUtils.ToastUtils("字数不能超过40个字");
                        } else {
                            getServerOutDone();
                        }
                    }
                });
            } else if (action.equals("EvectionActivity")) {
                getRight();
                leaveTypeEt1.setHint("填写出差地址");
                leaveTypeEt4.setHint("请填写出差天数");
                leaveTypeEt1.setFocusable(true);
                leaveTypeEt1.setFocusableInTouchMode(true);
                leavetypeText1.setText("出差地点");
                leavetypeText4.setText("出差天数");
                titleTv.setText("出差申请");
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(leaveTypeEt1.getText().toString().trim())) {
                            UiUtils.ToastUtils("出差地点不能为空");
                        } else if (TextUtils.isEmpty(leaveTypeEt2.getText().toString().trim())) {
                            UiUtils.ToastUtils("开始时间不能为空");
                        } else if (TextUtils.isEmpty(leaveTypeEt3.getText().toString().trim())) {
                            UiUtils.ToastUtils("结束时间不能为空");
                        } else if (TextUtils.isEmpty(leaveTypeEt4.getText().toString().trim())) {
                            UiUtils.ToastUtils("出差天数不能为空");
                        } else if (TextUtils.isEmpty(leaveTypeEt5.getText().toString().trim())) {
                            UiUtils.ToastUtils("理由不能为空");
                        } else if (UiUtils.timeToMill("yyyy-MM-dd HH:mm", leaveTypeEt2.getText
                                ().toString().trim()) < UiUtils.timeToMill("yyyy-MM-dd HH:mm",
                                UiUtils.refFormatNowDate("yyyy-MM-dd HH:mm"))) {
                            UiUtils.ToastUtils("开始时间不能比当前时间小");
                        } else if (UiUtils.timeToMill("yyyy-MM-dd HH:mm", leaveTypeEt2.getText
                                ().toString().trim()) > UiUtils.timeToMill("yyyy-MM-dd HH:mm",
                                leaveTypeEt3.getText().toString().trim())) {
                            UiUtils.ToastUtils("开始时间不能大于或等于结束时间");
                        } else if (leaveTypeEt5.getText().toString().trim().length() > 40) {
                            UiUtils.ToastUtils("字数不能超过40个字");
                        } else {
                            getServerEvection();
                        }
                    }
                });
            } else if (action.equals("EmpOvertimeActivity")) {
                getRight();
                leaveTypeEt4.setHint("请填写加班天数");
                leaveTypeEt1.setFocusable(true);
                leaveTypeEt1.setFocusableInTouchMode(true);
                leaveType1.setVisibility(View.GONE);
                leavetypeText4.setText("加班天数");
                titleTv.setText("加班申请");
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(leaveTypeEt2.getText().toString().trim())) {
                            UiUtils.ToastUtils("开始时间不能为空");
                        } else if (TextUtils.isEmpty(leaveTypeEt3.getText().toString().trim())) {
                            UiUtils.ToastUtils("结束时间不能为空");
                        } else if (TextUtils.isEmpty(leaveTypeEt4.getText().toString().trim())) {
                            UiUtils.ToastUtils("加班天数不能为空");
                        } else if (TextUtils.isEmpty(leaveTypeEt5.getText().toString().trim())) {
                            UiUtils.ToastUtils("理由不能为空");
                        } else if (UiUtils.timeToMill("yyyy-MM-dd HH:mm", leaveTypeEt2.getText
                                ().toString().trim()) < UiUtils.timeToMill("yyyy-MM-dd HH:mm",
                                UiUtils.refFormatNowDate("yyyy-MM-dd HH:mm"))) {
                            UiUtils.ToastUtils("开始时间不能比当前时间小");
                        } else if (UiUtils.timeToMill("yyyy-MM-dd HH:mm", leaveTypeEt2.getText
                                ().toString().trim()) > UiUtils.timeToMill("yyyy-MM-dd HH:mm",
                                leaveTypeEt3.getText().toString().trim())) {
                            UiUtils.ToastUtils("开始时间不能大于或等于结束时间");
                        } else if (leaveTypeEt5.getText().toString().trim().length() > 40) {
                            UiUtils.ToastUtils("字数不能超过40个字");
                        } else {
                            getServerEmpOvertime();
                        }
                    }
                });
            } else if (action.equals("Leavedetail") || action.equals("Leavewaitdetail") || action
                    .equals("passedLeavedetail")) {
                leavetypeLl.setVisibility(View.GONE);
                if (action.equals("Leavewaitdetail")) {
                    if(getIntent().getBooleanExtra("isMy",false)==false)
                    leavetypeLl.setVisibility(View.VISIBLE);
                    else
                        leavetypeLl.setVisibility(View.GONE);
                    accede.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getApprovalServer(0, getIntent().getStringExtra("leavecode"), 1);

                        }
                    });
                    negate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getApprovalServer(0, getIntent().getStringExtra("leavecode"), 0);

                        }
                    });
                }
                leaveTypeEt1.setText(getIntent().getStringExtra("leavename"));
                leaveTypeEt2.setText(getIntent().getStringExtra("stattime"));
                leaveTypeEt3.setText(getIntent().getStringExtra("endtime"));
                leaveTypeEt4.setText(getIntent().getStringExtra("leavedays"));
                leaveTypeEt5.setText(getIntent().getStringExtra("leavereason"));
                String statu = getIntent().getStringExtra("statu");
                if (statu.equals("0")) {
                    leaveTypeEt6.setText("审批中");
                } else if (statu.equals("1")) {
                    leaveTypeEt6.setText("审批通过");
                } else if (statu.equals("2")) {
                    leaveTypeEt6.setText("审批拒绝");
                }
                titleTv.setText("请假详情");
            } else if (action.equals("outetail") || action.equals("waitoutetail") || action
                    .equals("finishoutetail")) {
                leavetypeLl.setVisibility(View.GONE);
                if (action.equals("waitoutetail")) {
                    leavetypeLl.setVisibility(View.VISIBLE);
                    accede.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getApprovalServer(1, getIntent().getStringExtra("outcode"), 1);
                            finish();
                        }
                    });
                    negate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getApprovalServer(1, getIntent().getStringExtra("outcode"), 0);
                            finish();
                        }
                    });
                }
                leaveType1.setVisibility(View.GONE);
                leavetypeText4.setText("外出天数");
                leaveTypeEt2.setText(getIntent().getStringExtra("stattime"));
                leaveTypeEt3.setText(getIntent().getStringExtra("endtime"));
                leaveTypeEt4.setText(getIntent().getStringExtra("outdays"));
                leaveTypeEt5.setText(getIntent().getStringExtra("outreason"));
                String statu = getIntent().getStringExtra("statu");
                if (statu.equals("0")) {
                    leaveTypeEt6.setText("审批中");
                } else if (statu.equals("1")) {
                    leaveTypeEt6.setText("审批通过");
                } else if (statu.equals("2")) {
                    leaveTypeEt6.setText("审批拒绝");
                }
                titleTv.setText("外出详情");
            } else if (action.equals("travelletail") || action.equals("travelwaitletail") ||
                    action.equals("travelfinishletail")) {
                leavetypeLl.setVisibility(View.GONE);
                leavetypeText1.setText("出差地点");
                leaveTypeEt1.setHint("填写出差地址");
                if (action.equals("travelwaitletail")) {
                    leavetypeLl.setVisibility(View.VISIBLE);
                    accede.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getApprovalServer(2, getIntent().getStringExtra("travelcode"), 1);
                            finish();
                        }
                    });
                    negate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getApprovalServer(2, getIntent().getStringExtra("travelcode"), 0);
                            finish();
                        }
                    });
                }
                leaveTypeEt1.setText(TextUtils.isEmpty(getIntent().getStringExtra
                        ("traveladdress")) ? "" : getIntent().getStringExtra("traveladdress"));
                leaveTypeEt2.setText(getIntent().getStringExtra("stattime"));
                leaveTypeEt3.setText(getIntent().getStringExtra("endtime"));
                leaveTypeEt4.setText(getIntent().getStringExtra("traveldays"));
                String statu = getIntent().getStringExtra("statu");
                if (statu.equals("0")) {
                    leaveTypeEt6.setText("审批中");
                } else if (statu.equals("1")) {
                    leaveTypeEt6.setText("审批通过");
                } else if (statu.equals("2")) {
                    leaveTypeEt6.setText("审批拒绝");
                }
                leaveTypeEt5.setText("travelreason");
                titleTv.setText("出差详情");
                leavetypeText4.setText("出差天数");
            } else if (action.equals("overletail") || action.equals("overwaitletail") || action
                    .equals("overfinishletail")) {
                leavetypeLl.setVisibility(View.GONE);
                leavetypeText4.setText("加班天数");
                if (action.equals("overwaitletail")) {
                    leavetypeLl.setVisibility(View.VISIBLE);
                    accede.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getApprovalServer(3, getIntent().getStringExtra("overcode"), 1);
                            finish();
                        }
                    });
                    negate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getApprovalServer(3, getIntent().getStringExtra("overcode"), 0);
                            finish();
                        }
                    });
                }
                leaveType1.setVisibility(View.GONE);
                leaveTypeEt2.setText(TextUtils.isEmpty(getIntent().getStringExtra("stattime")) ?
                        "" : getIntent().getStringExtra("stattime"));
                leaveTypeEt3.setText(TextUtils.isEmpty(getIntent().getStringExtra("endtime")) ?
                        "" : getIntent().getStringExtra("endtime"));
                leaveTypeEt4.setText(TextUtils.isEmpty(getIntent().getStringExtra("overdays")) ?
                        "" : getIntent().getStringExtra("overdays"));
                String statu = getIntent().getStringExtra("statu");
                if (statu.equals("0")) {
                    leaveTypeEt6.setText("审批中");
                } else if (statu.equals("1")) {
                    leaveTypeEt6.setText("审批通过");
                } else if (statu.equals("2")) {
                    leaveTypeEt6.setText("审批拒绝");
                }
                leaveTypeEt5.setText(TextUtils.isEmpty(getIntent().getStringExtra("overreason"))
                        ? "" : getIntent().getStringExtra("overreason"));

                titleTv.setText("加班详情");
            } else if (action.equals("OutActivity")) {
                leaveType1.setVisibility(View.GONE);
                titleTv.setText("外出管理");
                leavetypeLl.setVisibility(View.VISIBLE);
                leavetypeText4.setText("外出天数");
                leavetypeText5.setText("外出事由");
            } else if (action.equals("EvectionActivity")) {
                titleTv.setText("出差管理");
                leaveType1.setOnClickListener(null);
                leavetypeText1.setText("出差地点");
                leaveTypeEt1.setHint("请输入地点(必填)");
                leavetypeText4.setText("出差天数");
                leavetypeText5.setText("出差事由");
            } else if (action.equals("EmpOvertimeActivity")) {
                titleTv.setText("加班管理");
                leaveType1.setVisibility(View.GONE);
                leavetypeText4.setText("加班天数");
                leavetypeText5.setText("加班原因");
                leaveTypeEt5.setHint("加班原因(必填)");
                leaveTypePut.setVisibility(View.VISIBLE);
                leavetypeLl.setVisibility(View.GONE);
            } else if (action.equals("MessageCenterPager")) {
                leavetypeLl.setVisibility(View.GONE);
                //TODO:工作通知详情
                notifycode = getIntent().getStringExtra("notifycode");
                String notifykind = getIntent().getStringExtra("notifykind");
                if (notifykind.equals("0")) {              //企业邀请通知
                    titleTv.setText("企业详细信息");
                } else if (notifykind.equals("1")) {       //请假申请通知
                    getServerWorkData2();
                    titleTv.setText("请假详情");
                } else if (notifykind.equals("2")) {       //外出申请通知
                    getServerWorkData3();
                    leaveType1.setVisibility(View.GONE);
                    leaveTypeEt2.setText(workNotify3.result.stattime);
                    leaveTypeEt3.setText(workNotify3.result.endtime);
                    leaveTypeEt4.setText(workNotify3.result.outdays);
                    leaveTypeEt5.setText(workNotify3.result.outreason);
                    if (workNotify3.result.statu == 0) {
                        leaveTypeEt6.setText("审批中");
                    } else if (workNotify3.result.statu == 1) {
                        leaveTypeEt6.setText("审批通过");
                    } else if (workNotify3.result.statu == 2) {
                        leaveTypeEt6.setText("审批拒绝");
                    }
                    titleTv.setText("外出详细信息");
                } else if (notifykind.equals("3")) {       //出差申请通知
                    getServerWorkData4();
                    leaveTypeEt1.setText("出差地址");
                    leaveTypeEt2.setText(workNotify4.result.stattime);
                    leaveTypeEt3.setText(workNotify4.result.endtime);
                    leaveTypeEt4.setText(workNotify4.result.traveldays);
                    leaveTypeEt5.setText(workNotify4.result.travelreason);
                    if (workNotify4.result.statu == 0) {
                        leaveTypeEt6.setText("审批中");
                    } else if (workNotify4.result.statu == 1) {
                        leaveTypeEt6.setText("审批通过");
                    } else if (workNotify4.result.statu == 2) {
                        leaveTypeEt6.setText("审批拒绝");
                    }
                    titleTv.setText("出差详细信息");
                } else if (notifykind.equals("4")) {       //加班申请通知
                    getServerWorkData5();
                    leaveType1.setVisibility(View.GONE);
                    leaveTypeEt2.setText(workNotify5.result.stattime);
                    leaveTypeEt3.setText(workNotify5.result.endtime);
                    leaveTypeEt4.setText(workNotify5.result.overdays);
                    leaveTypeEt5.setText(workNotify5.result.overreason);
                    if (workNotify5.result.statu == 0) {
                        leaveTypeEt6.setText("审批中");
                    } else if (workNotify5.result.statu == 1) {
                        leaveTypeEt6.setText("审批通过");
                    } else if (workNotify5.result.statu == 2) {
                        leaveTypeEt6.setText("审批拒绝");
                    }
                    titleTv.setText("加班详细信息");
                } else if (notifykind.equals("5")) {       //申请加入团队通知
                    titleTv.setText("申请加入团队详细信息");
                } else if (notifykind.equals("6")) {       //团队申请处理结果通知
                    titleTv.setText("团队申请处理结果详细信息");
                }
            }
        }
        flAddress.addView(view);
    }
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            if(action.equals("travelletail"))
            EvectionActivity.hander.sendEmptyMessage(1);
            switch (what) {
                case 1:
                    String result1 = response.get();
                    System.out.println("请假类别result=" + result1);
                    LeaveType leaveType = processTypeData(result1);
                    myAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    String result2 = response.get();
                    System.out.println("请假申请result2=" + result2);
                    LeaveType leaveType2 = new Gson().fromJson(result2, LeaveType.class);
                    setResult(1);
                    finish();
                    UiUtils.ToastUtils(leaveType2.summary);
                    break;
                case 3:
                    String result3 = response.get();
                    System.out.println("外出result3=" + result3);
                    break;
                case 4:
                    ApplyBean applyBean2 = new Gson().fromJson(response.get(), ApplyBean.class);
                    UiUtils.ToastUtils(applyBean2.summary);
                    if (applyBean2.status.equals("00000")) {
                        EvectionActivity.hander.sendEmptyMessage(1);
                        finish();
                    }
                    break;
                case 5:
                    ApplyBean applyBean3 = new Gson().fromJson(response.get(), ApplyBean.class);
                    UiUtils.ToastUtils(applyBean3.summary);
                    if (applyBean3.status.equals("00000")) {
                        setResult(1);
                        finish();
                    }
                    break;
                case 6:
                    ApplyBean applyBean1 = new Gson().fromJson(response.get(), ApplyBean.class);
                    UiUtils.ToastUtils(applyBean1.summary);
                    if (applyBean1.status.equals("00000")) {
                        setResult(1);
                        finish();
                    }
                    break;
                case 7:
                    ApplyBean applyBean = new Gson().fromJson(response.get(), ApplyBean.class);
                    UiUtils.ToastUtils(applyBean.summary);
                    if(applyBean.status.equals("00000")) {
                        setResult(1);
                        finish();
                    }
                    break;
                case 8:
                    System.out.println("详细信息为：" + response.get());
                    WorkDetail workDetail = new Gson().fromJson(response.get(), WorkDetail.class);
                    Log.e("详细信息为：", "onSucceed: 详细信息为：" + response.get());
                    break;
                //TODO：解析数据
                case 12:
                    System.out.println("请假详情:" + response.get());
                    workNotify2 = new Gson().fromJson(response.get(), WorkLeaveDetail
                            .class);
                    Log.e("workNotify2", "onSucceed: " + response.get());
                 /*   leaveTypeEt1.setText(workNotify2.result.leavename);
                    leaveTypeEt2.setText(workNotify2.result.stattime);
                    leaveTypeEt3.setText(workNotify2.result.endtime);
                    leaveTypeEt4.setText(workNotify2.result.leavedays);
                    leaveTypeEt5.setText(workNotify2.result.leavereason);*/
                    if (workNotify2.result.statu == 0) {
                        leaveTypeEt6.setText("审批中");
                    } else if (workNotify2.result.statu == 1) {
                        leaveTypeEt6.setText("审批通过");
                    } else if (workNotify2.result.statu == 2) {
                        leaveTypeEt6.setText("审批拒绝");
                    }
                    break;
                case 13:
                    System.out.println("外出详情:" + response.get());
                    workNotify3 = new Gson().fromJson(response.get(), WorkNotifyDetail.class);
                    UiUtils.ToastUtils(workNotify3.summary);
                    break;
                case 14:
                    System.out.println("出差详情:" + response.get());
                    workNotify4 = new Gson().fromJson(response.get(), WorkNotifyDetail.class);
                    UiUtils.ToastUtils(workNotify4.summary);
                    break;
                case 15:
                    System.out.println("加班详情:" + response.get());
                    workNotify5 = new Gson().fromJson(response.get(), WorkNotifyDetail.class);
                    UiUtils.ToastUtils(workNotify5.summary);
                    break;
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
            System.out.println("请求失败");
        }
    };
    private void getServerWorkData5() {
        try {
            Request<String> request15 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "overtime_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            workMessMap5.put("access_id", "1234567890");
            workMessMap5.put("timestamp", time);
            workMessMap5.put("telnum", UiUtils.getPhoneNumber());
            workMessMap5.put("overcode", notifycode);
            workMessMap5.put("notifycode", "0");
            String encode = MD5Encoder.encode(Sign.generateSign(workMessMap5) +
                    "12345678901234567890123456789011");
            workMessMap5.put("sign", encode);
            request15.add(workMessMap5);
            CallServer.getRequestInstance().add(this, 15, request15, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getServerWorkData4() {
        try {
            Request<String> request14 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "travel_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            workMessMap4.put("access_id", "1234567890");
            workMessMap4.put("timestamp", time);
            workMessMap4.put("telnum", UiUtils.getPhoneNumber());
            workMessMap4.put("travelcode", notifycode);
            workMessMap4.put("notifycode", "0");
            String encode = MD5Encoder.encode(Sign.generateSign(workMessMap4) +
                    "12345678901234567890123456789011");
            workMessMap4.put("sign", encode);
            request14.add(workMessMap4);
            CallServer.getRequestInstance().add(this, 14, request14, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getServerWorkData3() {
        try {
            Request<String> request13 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "out_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            workMessMap3.put("access_id", "1234567890");
            workMessMap3.put("timestamp", time);
            workMessMap3.put("telnum", UiUtils.getPhoneNumber());
            workMessMap3.put("outcode", notifycode);
            workMessMap3.put("notifycode", "0");
            String encode = MD5Encoder.encode(Sign.generateSign(workMessMap3) +
                    "12345678901234567890123456789011");
            workMessMap3.put("sign", encode);
            request13.add(workMessMap3);
            CallServer.getRequestInstance().add(this, 13, request13, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getServerWorkData2() {
        try {
            Request<String> request12 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "leave_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            workMessMap2.put("access_id", "1234567890");
            workMessMap2.put("timestamp", time);
            workMessMap2.put("telnum", UiUtils.getPhoneNumber());
            workMessMap2.put("leavecode", "0");
            workMessMap2.put("notifycode", notifycode);
            String encode = MD5Encoder.encode(Sign.generateSign(workMessMap2) +
                    "12345678901234567890123456789011");
            workMessMap2.put("sign", encode);
            request12.add(workMessMap2);
            CallServer.getRequestInstance().add(this, 12, request12, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getRight() {
        leaveType6.setVisibility(View.GONE);
        leaveTypeEt1.setOnClickListener(this);
        leaveTypeEt2.setOnClickListener(this);
        leaveTypeEt3.setOnClickListener(this);
        leaveTypeEt4.setFocusable(true);
        leaveTypeEt4.setFocusableInTouchMode(true);
        leaveTypeEt5.setFocusable(true);
        leaveTypeEt5.setFocusableInTouchMode(true);
        leavetypeLl.setVisibility(View.GONE);
        right.setVisibility(View.VISIBLE);
        right.setText("提交");
    }

    private void getApprovalServer(int approvaltype, String code, int statu) {
        try {
            Request<String> request7 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "approval_done.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map6.put("access_id", "1234567890");
            map6.put("timestamp", time);
            map6.put("telnum", UiUtils.getPhoneNumber());
            map6.put("approvaltype", String.valueOf(approvaltype));
            map6.put("approvalcode", code);
            map6.put("statu", String.valueOf(statu));
            map6.put("reason", "");
            String encode = MD5Encoder.encode(Sign.generateSign(map6) +
                    "12345678901234567890123456789011");
            map6.put("sign", encode);
            request7.add(map6);
            // 发起请求
            CallServer.getRequestInstance().add(this, 7, request7, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        registerBroadCastReceiver();
    }

    /**
     * 注册一个广播，监听定位结果
     */
    private void registerBroadCastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                address = intent.getStringExtra("address");
            }
        };
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter.addAction(LOCATION_BCR);
        registerReceiver(broadcastReceiver, intentToReceiveFilter);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.leavetype_et1:
                if (action != null) {
                    if (action.equals("LeaveActivity")) {
                        View leaveType = UiUtils.getParentPopuwindow(this, R.layout
                                .leave_type);
                        typeRlv = (RecyclerView) leaveType.findViewById(R.id.type_rlv);
                        typeTvCancel = (TextView) leaveType.findViewById(R.id.type_tv_cancel);
                        typeTvConfirm = (TextView) leaveType.findViewById(R.id.type_tv_confirm);
                        getServerType();
                        typeRlv.setLayoutManager(new LinearLayoutManager(this));
                        myAdapter = new MyAdapter();
                        typeRlv.setAdapter(myAdapter);
                        typeTvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UiUtils.parentpopupWindow.dismiss();
                            }
                        });
                        typeTvConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UiUtils.parentpopupWindow.dismiss();
                            }
                        });
                    } else if (action.equals("OutActivity")) {                 //外出

                    } else if (action.equals("EvectionActivity")) {

                    } else if (action.equals("EmpOvertimeActivity")) {

                    } else if (action.equals("EmpVisitActivity")) {
                    }
                }
                break;
            case R.id.type_tv_cancel:
                if (action != null) {
                    if (action.equals("LeaveActivity")) {

                    } else if (action.equals("OutActivity")) {

                    } else if (action.equals("EvectionActivity")) {

                    } else if (action.equals("EmpOvertimeActivity")) {

                    } else if (action.equals("EmpVisitActivity")) {

                    }
                }
                break;
            case R.id.type_tv_confirm:
                if (action != null) {
                    if (action.equals("LeaveActivity")) {

                    } else if (action.equals("OutActivity")) {
                    } else if (action.equals("EvectionActivity")) {
                    } else if (action.equals("EmpOvertimeActivity")) {
                    } else if (action.equals("EmpVisitActivity")) {
                    }
                }
                break;
            case R.id.leavetype_et2:
                DateTimePickDialogUtil dateTimePicKDialog1 = new DateTimePickDialogUtil(
                        this, "");
                dateTimePicKDialog1.dateTimePicKDialog(leaveTypeEt2);
                break;
            case R.id.leavetype_et3:
                DateTimePickDialogUtil dateTimePicKDialog2 = new DateTimePickDialogUtil(
                        this, "");
                dateTimePicKDialog2.dateTimePicKDialog(leaveTypeEt3);
                break;
            case R.id.leavetype_put:
                if (action != null) {
                    if (action.equals("LeaveActivity")) {

                    } else if (action.equals("OutActivity")) {

                    } else if (action.equals("EvectionActivity")) {

                    } else if (action.equals("EmpOvertimeActivity")) {
                        if (leaveTypeEt2.getText().toString().trim().isEmpty()) {
                            UiUtils.ToastUtils("开始时间不能为空");
                        } else if (leaveTypeEt3.getText().toString().trim().isEmpty()) {
                            UiUtils.ToastUtils("结束不能为空");
                        } else if (leaveTypeEt4.getText().toString().trim().isEmpty()) {
                            UiUtils.ToastUtils("加班原因不能为空");
                        } else {
                            UiUtils.ToastUtils("提交成功");
                            getServerEmpOvertime();

                        }
                    }
                }
                break;
            default:
                break;
        }
    }
    /**
     * 加班提交
     */
    private void getServerEmpOvertime() {
        try {
            Request<String> request5 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "overtime_done.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map4.put("access_id", "1234567890");
            map4.put("timestamp", time);
            map4.put("telnum", UiUtils.getPhoneNumber());
            map4.put("stattime", leaveTypeEt2.getText().toString().trim());
            map4.put("endtime", leaveTypeEt3.getText().toString().trim());
            map4.put("overdays", leaveTypeEt4.getText().toString().trim());
            map4.put("overreason", leaveTypeEt5.getText().toString().trim());
            String encode = MD5Encoder.encode(Sign.generateSign(map4) +
                    "12345678901234567890123456789011");
            map4.put("sign", encode);
            request5.add(map4);
            System.out.println("加班时间为：" + leaveTypeEt2.getText().toString().trim());
            System.out.println("时间：" + leaveTypeEt3.getText().toString().trim());
            CallServer.getRequestInstance().add(this, 5, request5, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 出差提交请求
     */
    private void getServerEvection() {
        try {
            Request<String> request4 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "travel_done.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map3.put("access_id", "1234567890");
            map3.put("timestamp", time);
            map3.put("telnum", UiUtils.getPhoneNumber());
            map3.put("traveladdress", leaveTypeEt1.getText().toString().trim());
            map3.put("stattime", leaveTypeEt2.getText().toString().trim());
            map3.put("endtime", leaveTypeEt3.getText().toString().trim());
            map3.put("traveldays", leaveTypeEt4.getText().toString().trim());
            map3.put("travelreason", leaveTypeEt5.getText().toString().trim());
            String encode = MD5Encoder.encode(Sign.generateSign(map3) +
                    "12345678901234567890123456789011");
            map3.put("sign", encode);
            request4.add(map3);
            System.out.println("出差时间为：" + leaveTypeEt2.getText().toString().trim());
            CallServer.getRequestInstance().add(this, 4, request4, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 请假类别
     */
    public void getServerType() {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "leavetype_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request1.add(map);
            CallServer.getRequestInstance().add(this, 1, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 申请请假
     */
    private void getServerApplyLeave() {
        try {
            Request<String> request2 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "leave_done.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("leavetype", String.valueOf(leaveId));
            map1.put("stattime", leaveTypeEt2.getText().toString().trim());
            map1.put("endtime", leaveTypeEt3.getText().toString().trim());
            map1.put("leavedays", leaveTypeEt4.getText().toString().trim());
            map1.put("leavereason", leaveTypeEt5.getText().toString().trim());
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request2.add(map1);
            CallServer.getRequestInstance().add(this, 2, request2, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 外出申请
     */
    private void getServerOutDone() {
        try {
            Request<String> request8 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "out_done.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map7.put("access_id", "1234567890");
            map7.put("timestamp", time);
            map7.put("telnum", UiUtils.getPhoneNumber());
            map7.put("stattime", leaveTypeEt2.getText().toString().trim());
            map7.put("endtime", leaveTypeEt3.getText().toString().trim());
            map7.put("outdays", leaveTypeEt4.getText().toString().trim());
            map7.put("outreason", leaveTypeEt5.getText().toString().trim());
            String encode = MD5Encoder.encode(Sign.generateSign(map7) +
                    "12345678901234567890123456789011");
            map7.put("sign", encode);
            request8.add(map7);
            CallServer.getRequestInstance().add(this, 6, request8, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private LeaveType processTypeData(String json) {
        Gson gson = new Gson();
        LeaveType leaveType1 = gson.fromJson(json, LeaveType.class);
        dataList = leaveType1.result;
        System.out.println("dataList.size()=" + dataList.size());
        return leaveType1;
    }


    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(getApplication())
                    .inflate(R.layout.leave_type_item, parent, false));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.typeItemName.setText(dataList.get(position).leavename);
            if (position == dataList.size() - 1) {
                holder.typeItemTv.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            if (dataList != null) {
                return dataList.size();
            }
            return 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView typeItemName, typeItemTv;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                private int id;
                @Override
                public void onClick(View v) {
                    String leavename = dataList.get(getLayoutPosition()).leavename;
                    leaveTypeEt1.setText(TextUtils.isEmpty(leavename) ? "" : leavename);
                    id = dataList.get(getLayoutPosition()).id;
                    leaveId=dataList.get(getLayoutPosition()).id;
                    UiUtils.parentpopupWindow.dismiss();
                }
            });
            typeItemName = (TextView) itemView.findViewById(R.id.type_item_name);
            typeItemTv = (TextView) itemView.findViewById(R.id.type_item_tv);
        }
    }
}
