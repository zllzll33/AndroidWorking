package com.luofangyun.shangchao.activity.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.MainActivity;
import com.luofangyun.shangchao.activity.app.AttAreaActivity;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.domain.WorkTimeBean;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 打卡
 */

public class ConfirmActivity extends BaseActivity implements Runnable {
    private RelativeLayout    rlConfirm1;
    private BroadcastReceiver broadcastReceiver;
    public static String LOCATION_BCR = "location_bcr";
    private View     view;
    private TextView confirmTime, onDutyTime, confirmName, offDuty, signTime,
            instead, labelTv, confirmTv;
    private String             nowTime;
    private android.os.Handler handler;
    private Map<String, String> map = new HashMap<>();
    private String address;
    private LocationManager locationManager;
    private String locationProvider;
    private Location location;
    private double longitude;
    private double latitude;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_confirm);
        initialize();
        //初始化控件
        initView();
        //初始化数据
        initData();
    }

    private void initialize() {
        registerBroadCastReceiver();
    }
    /**
     * 注册一个广播，监听定位结果
     */
    private void registerBroadCastReceiver() {
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter.addAction(LOCATION_BCR);
        registerReceiver(broadcastReceiver, intentToReceiveFilter);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                address = intent.getStringExtra("addressText");
                System.out.println("监听的地址为:" + address);
                confirmName.setText(address);
            }
        };
    }

    private void initView() {
        confirmTime = (TextView) view.findViewById(R.id.confirm_time);
        confirmTv = (TextView) view.findViewById(R.id.confirm_tv);
        labelTv = (TextView) view.findViewById(R.id.label_tv);
        onDutyTime = (TextView) view.findViewById(R.id.on_duty_time);
        confirmName = (TextView) view.findViewById(R.id.confirm_name);
        offDuty = (TextView) view.findViewById(R.id.off_duty);
        signTime = (TextView) view.findViewById(R.id.sign_time);
        instead = (TextView) view.findViewById(R.id.instead);
        view.findViewById(R.id.rl_confirm).setOnClickListener(this);
        instead.setOnClickListener(this);
        getWorktime();

    }
   private void getWorktime()
   {
       long longtime=System.currentTimeMillis();
       SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
       Date d1=new Date(longtime);
       String attdate=format.format(d1);
       Log.e("attdate",attdate);
       try {
           Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                   "att_schedu_info.json", RequestMethod.POST);
           String time = Long.toString(new Date().getTime());
           map.put("access_id", "1234567890");
           map.put("timestamp", time);
           map.put("telnum", UiUtils.getPhoneNumber());
           map.put("attdate", attdate);
           String encode = MD5Encoder.encode(Sign.generateSign(map) +
                   "12345678901234567890123456789011");
           map.put("sign", encode);
           request.add(map);
           CallServer.getRequestInstance().add(this, 2, request, httpListener, false, false);
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
    private void initData() {
        showLocationText();
        System.out.println("经度：" + longitude);
        System.out.println("纬度：" + latitude);
        String labelTvText = PrefUtils.getString(this, "labelText", null);
        String confirmNameText = PrefUtils.getString(this, "confirmNameText", null);
        String confirmTimeText = PrefUtils.getString(this, "confirmTimeText", null);
        if (TextUtils.isEmpty(labelTvText)) {
            labelTv.setText("NFC标签");
        } else {
            labelTv.setText(labelTvText);
        }

        if (TextUtils.isEmpty(confirmNameText)) {
            confirmName.setText("");
        } else {
            confirmName.setText(confirmNameText);
        }

        if (TextUtils.isEmpty(confirmTimeText)) {
            confirmTime.setText("");
        } else {
            confirmTime.setText(confirmTimeText);
        }
        nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        handler = new android.os.Handler() {
            @Override
            public void handleMessage(Message msg) {
                signTime.setText((String) msg.obj);
            }
        };
        new Thread(this).start();
        titleTv.setText("打卡");
        flAddress.addView(view);
    }

    private void showLocationText() {
        //获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if(providers.contains(LocationManager.GPS_PROVIDER)){
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return ;
        }
        //获取Location
        location = locationManager.getLastKnownLocation(locationProvider);
        if(location  != null){
            //不为空,显示地理位置经纬度
            showLocation(location);
        }
        //监视地理位置变化
        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
    }
    /**
     * 显示地理位置经度和纬度信息
     * @param location
     */
    private void showLocation(Location location){
        longitude = location.getLongitude();       //经度
        latitude = location.getLatitude();         //纬度
        System.out.println(longitude + "=======================" + latitude);
    }

    LocationListener locationListener =  new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {
        }
        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            //如果位置发生变化,重新显示
            showLocation(location);

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationManager!=null){
            //移除监听器
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_confirm:
                if (labelTv.getText().toString().trim().equals("GPS标签：")) {
                    confirmName.setText(address);
                    confirmTime.setText(nowTime);
                    getServerData(longitude + "," + latitude, 3);
                    PrefUtils.putString(this, "confirmNameText", confirmName.getText().toString().trim());
                    PrefUtils.putString(this, "confirmTimeText", confirmTime.getText().toString().trim());
                    System.out.println("我被点击了……");
                } else if (labelTv.getText().toString().trim().equals("NFC标签：")) {
                    getServerData(longitude + "," + latitude, 1);
                    PrefUtils.putString(this, "confirmNameText", confirmName.getText().toString().trim());
                    PrefUtils.putString(this, "confirmTimeText", confirmTime.getText().toString().trim());
                } else if (labelTv.getText().toString().trim().equals("蓝牙标签：")) {
                    getServerData(longitude + "," + latitude, 2);
                    PrefUtils.putString(this, "confirmNameText", confirmName.getText().toString().trim());
                    PrefUtils.putString(this, "confirmTimeText", confirmTime.getText().toString().trim());
                }
                break;
            case R.id.instead:
                View cardSet = UiUtils.getParentPopuwindow(this, R.layout.card_setting);
                cardSet.findViewById(R.id.myinfo_cancel).setOnClickListener(this);
                cardSet.findViewById(R.id.window_rl).setOnClickListener(this);
                cardSet.findViewById(R.id.card_nfc).setOnClickListener(this);
                cardSet.findViewById(R.id.card_bule).setOnClickListener(this);
                cardSet.findViewById(R.id.card_gps).setOnClickListener(this);
                break;
            case R.id.myinfo_cancel:
            case R.id.window_rl:
                UiUtils.parentpopupWindow.dismiss();
                break;
            case R.id.card_nfc:
                labelTv.setText("NFC标签：");
                PrefUtils.putString(this, "confirmNameText", "");
                PrefUtils.putString(this, "confirmTimeText", "");
                confirmName.setText("");
                confirmTime.setText("");
                PrefUtils.putString(this, "labelText", labelTv.getText().toString().trim());
                UiUtils.parentpopupWindow.dismiss();
                break;
            case R.id.card_bule:
                PrefUtils.putString(this, "confirmNameText", "");
                PrefUtils.putString(this, "confirmTimeText", "");
                confirmName.setText("");
                confirmTime.setText("");
                labelTv.setText("蓝牙标签：");
                PrefUtils.putString(this, "labelText", labelTv.getText().toString().trim());
                UiUtils.parentpopupWindow.dismiss();
                break;
            case R.id.card_gps:
                PrefUtils.putString(this, "confirmNameText", "");
                PrefUtils.putString(this, "confirmTimeText", "");
                confirmName.setText(MainActivity.addrStr);
                confirmTime.setText("");
                labelTv.setText("GPS标签：");
                PrefUtils.putString(this, "labelText", labelTv.getText().toString().trim());
                UiUtils.parentpopupWindow.dismiss();
                break;
            case R.id.confirm_tv:
                startActivity(new Intent(this, AttAreaActivity.class));
                break;
            default:
                break;
        }
    }

    private void getServerData(String data, int i) {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "att_record_add.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("atttime", nowTime);
            map.put("atttype", String.valueOf(i));
            map.put("attinfo", data);
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request.add(map);
            CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpListener<String> httpListener = new HttpListener<String>() {
        private ApplyBean applyBean;
        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            Log.e("打卡",result);
            switch (what)
            {
                case 1:

                    System.out.println("打卡信息=" + result);
                    applyBean = new Gson().fromJson(result, ApplyBean.class);
                    UiUtils.ToastUtils(applyBean.summary);
                    break;
                case 2:
                   WorkTimeBean workTimeBean = new Gson().fromJson(result, WorkTimeBean.class);
                    onDutyTime.setText(workTimeBean.result.sbtime);
                    offDuty.setText(workTimeBean.result.xbtime);
                    break;
            }

        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
            UiUtils.ToastUtils(applyBean.summary);
        }
    };

    @Override
    public void run() {
        try {
            while (true) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String str = sdf.format(new Date());
                handler.sendMessage(handler.obtainMessage(100, str));
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}