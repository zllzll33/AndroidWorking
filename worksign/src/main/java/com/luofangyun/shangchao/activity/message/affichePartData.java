package com.luofangyun.shangchao.activity.message;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.MessageBean;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.MD5Encoder;
import com.luofangyun.shangchao.utils.MyBitmapImageViewTarget;
import com.luofangyun.shangchao.utils.Sign;
import com.luofangyun.shangchao.utils.UiUtils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.util.Date;
import java.util.Map;

/**
 * 公告详细信息
 */

public class affichePartData extends BaseActivity {
    private View                view;
    private Map<String, String> map;
    private MessageBean         affichePart;
    private TextView title;
    private ImageView affMessageIcon;
    private TextView affMessageTime;
    private ImageView affMessagePic;
    private TextView affMessageContent;
    private String notifycode;
    private MessageBean messageBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_message_part);

        initView();
        initData();
    }

    private void initView() {
         title = (TextView) view.findViewById(R.id.title);
         affMessageIcon = (ImageView) view.findViewById(R.id.aff_message_icon);
         affMessageTime = (TextView) view.findViewById(R.id.aff_message_time);
         affMessagePic = (ImageView) view.findViewById(R.id.aff_message_pic);
         affMessageContent = (TextView) view.findViewById(R.id.aff_message_content);
    }
    private void initData() {
        notifycode = getIntent().getStringExtra("notifycode");
        getServerAffiche();
        titleTv.setText("消息");
        flAddress.addView(view);
    }
    private void getServerAffiche() {
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "sysnofiy_detail.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("notifycode", notifycode);
            map.put("notifytype", String.valueOf(2));
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request1.add(map);
            CallServer.getRequestInstance().add(this, 1, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            System.out.println("result=" + result);
            messageBean = new Gson().fromJson(response.get(), MessageBean.class);
            affMessageTime.setText(messageBean.result.notifydate);
            affMessageContent.setText(messageBean.result.notifycontent);
            Glide.with(getApplication()).load("http://139.196.151.162:8888/" + messageBean.result.images).asBitmap().centerCrop().placeholder(R.drawable
                    .moren).into(new MyBitmapImageViewTarget(affMessagePic));
            Glide.with(getApplication()).load("http://139.196.151.162:8888/" + messageBean.result.empphoto).asBitmap()
                    .centerCrop().into(new BitmapImageViewTarget(affMessageIcon) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    affMessageIcon.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };
}
