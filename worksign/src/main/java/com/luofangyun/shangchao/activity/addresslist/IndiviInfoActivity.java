package com.luofangyun.shangchao.activity.addresslist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.base.impl.AddressListPager;
import com.luofangyun.shangchao.domain.IndiviInfo;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.MD5Encoder;
import com.luofangyun.shangchao.utils.Sign;
import com.luofangyun.shangchao.utils.UiUtils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.CacheMode;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 个人信息
 */
public class IndiviInfoActivity extends BaseActivity {

    private View      view;
    private ImageView infoIcon;
    private TextView  infoTv1, infoTv2, infoTv3, infoTv4, infoTv5, infoTv6, infoTv7, infoTv8,
            infoTv9;
    private String empcode, empname;
    private Map<String, String> map = new HashMap<>();
    private IndiviInfo indiviInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_indivi_info);
        initView();
        initData();
    }

    private void initView() {
        infoIcon = (ImageView) view.findViewById(R.id.info_icon);
        infoTv1 = (TextView) view.findViewById(R.id.info_tv1);
        infoTv2 = (TextView) view.findViewById(R.id.info_tv2);
        infoTv3 = (TextView) view.findViewById(R.id.info_tv3);
        infoTv4 = (TextView) view.findViewById(R.id.info_tv4);
        infoTv5 = (TextView) view.findViewById(R.id.info_tv5);
        infoTv6 = (TextView) view.findViewById(R.id.info_tv6);
        infoTv7 = (TextView) view.findViewById(R.id.info_tv7);
        infoTv8 = (TextView) view.findViewById(R.id.info_tv8);
        infoTv9 = (TextView) view.findViewById(R.id.info_tv9);
    }

    private void initData() {
        empcode = getIntent().getStringExtra("empcode");          //部门编码
        empname = getIntent().getStringExtra("empname");         ///部门名称
        System.out.println("点击的员工编号=" + empcode);
        getInfoData();
        infoTv9.setOnClickListener(onClickListener);
        titleTv.setText("个人信息");
        flAddress.addView(view);
    }

    /**
     * 员工详细信息
     */
    public void getInfoData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "employee_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("empcode", empcode);
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request.setCacheMode(CacheMode.ONLY_REQUEST_NETWORK);
            request.add(map);
            CallServer.getRequestInstance().add(this, 0, request, httpListener, false, false);
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
            String result = response.get();
            System.out.println("员工详细信息result=" + result);
            IndiviInfo personal = processData(result);
            infoTv1.setText(indiviInfo.result.companyname);
            infoTv2.setText(indiviInfo.result.deptname);
            infoTv3.setText(indiviInfo.result.emppost);
            infoTv4.setText(indiviInfo.result.empname);
            infoTv5.setText(indiviInfo.result.empphone);
            infoTv6.setText(indiviInfo.result.empsex);
            infoTv7.setText(indiviInfo.result.empbirthday);
            infoTv8.setText(indiviInfo.result.empaddress);
            Glide.with(getApplication()).load("http://139.196.151.162:8888/" + indiviInfo.result.empphoto).asBitmap()
                    .centerCrop().into(new BitmapImageViewTarget(infoIcon) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    infoIcon.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };
    private IndiviInfo processData(String result) {
        Gson gson = new Gson();
        indiviInfo = gson.fromJson(result, IndiviInfo.class);
        return indiviInfo;
    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.CALL");
            intent.setData(Uri.parse("tel:" + indiviInfo.result.empphone));
            savePhone(indiviInfo.result.empphone);
            startActivity(intent);
        }
    };
    private void savePhone(String phone)
    {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "emp_phone.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map=new HashMap<>();
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("phone", phone);
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            Log.e("map",UiUtils.Map2JsonStr(map));
            request.add(map);
            CallServer.getRequestInstance().add(this, 1, request, httpListener1, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private HttpListener<String> httpListener1 = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
//           Log.e("phoneAdd",result);
            AddressListPager.handler.sendEmptyMessage(1)  ;
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };
}
