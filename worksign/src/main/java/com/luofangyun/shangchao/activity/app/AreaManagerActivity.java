package com.luofangyun.shangchao.activity.app;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.MainActivity;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.MD5Encoder;
import com.luofangyun.shangchao.utils.Sign;
import com.luofangyun.shangchao.utils.UiUtils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 区域管理
 */
public class AreaManagerActivity extends BaseActivity {
    private View view;
    private Map<String, String> map = new HashMap<>();
    private MapView  mapview;
    private EditText areaTv;
    private TextView areaTv1;
    private SeekBar  areaSeekbar;
    BaiduMap baiduMap;
    int range=50;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.area_manager, null);
        initView();
        initData();
    }
    private void initView() {
        mapview = (MapView) view.findViewById(R.id.mapview);
        baiduMap=mapview.getMap();
        areaTv = (EditText) view.findViewById(R.id.area_et);
        areaTv1 = (TextView) view.findViewById(R.id.area_tv);
        areaSeekbar = (SeekBar) view.findViewById(R.id.area_seekbar);
        areaSeekbar.setProgress(0);
        LatLng  location = new LatLng(MainActivity.latitude, MainActivity.longitude);
        OverlayOptions Circle = new CircleOptions().fillColor(0x004d73b3)
                .center(location).stroke(new Stroke(3, 0x784d73b3))
                .radius(50);
        baiduMap.addOverlay(Circle);
        areaTv.setText(MainActivity.addrStr);
        areaSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                baiduMap.clear();
                LatLng  location = new LatLng(MainActivity.latitude, MainActivity.longitude);
                OverlayOptions Circle = new CircleOptions().fillColor(0x004d73b3)
                        .center(location).stroke(new Stroke(3, 0x784d73b3))
                        .radius(progress+50);
                baiduMap.addOverlay(Circle);
                range=progress+50;
                areaTv1.setText("滑动"+String.valueOf(range)+"米");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        LatLng point = new LatLng(MainActivity.latitude,MainActivity.longitude);
//        LatLng point = new LatLng(lat,lng);
        //设置中心位置、图层level  level小  图缩小

        baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().target(point).zoom(16).build()));
    }

    private void initData() {
        setResult(RESULT_OK);
        titleTv.setText("添加区域标签");
        right.setText("保存");
        right.setVisibility(View.VISIBLE);
        right.setOnClickListener(this);
        flAddress.addView(view);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.right) {
            getServerData();
        }
    }

    private void getServerData() {
        try {
            if(areaTv.getText().toString().isEmpty())
            {
                UiUtils.ToastUtils("请输入打卡区域");
                return;
            }
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "att_area_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("areaname", areaTv.getText().toString());
            map.put("areacode", "0");
            map.put("areax", String.valueOf(MainActivity.latitude));
            map.put("areay", String.valueOf(MainActivity.longitude));
            map.put("arearange", String.valueOf(range));
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
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 1:
                    String result1 = response.get();
                    System.out.println("区域数据result1=" + result1);
                    finish();
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
}
