package com.luofangyun.shangchao.base.impl;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.maself.CompanyInfoActivity;
import com.luofangyun.shangchao.activity.maself.HelpActivity;
import com.luofangyun.shangchao.activity.maself.PersonalDataActivity;
import com.luofangyun.shangchao.activity.maself.SystemSettingActivity;
import com.luofangyun.shangchao.base.BasePager;
import com.luofangyun.shangchao.domain.Personal;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.MD5Encoder;
import com.luofangyun.shangchao.utils.PrefUtils;
import com.luofangyun.shangchao.utils.Sign;
import com.luofangyun.shangchao.utils.UiUtils;
import com.luofangyun.shangchao.wxapi.ZWXShare;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.CacheMode;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 我
 */
public class MySelfPager extends BasePager {

    private View         view;
    private LinearLayout upPic;
    private RecyclerView mySelfRlv;
    private MyAdapter    myAdapter;
    private int[] pics = {R.mipmap.comp_name, R.mipmap.recommend,
            R.mipmap.novice_help, R.mipmap.system_set};
    private String[] names;
    private Map<String, String> map = new HashMap<>();
    private String empphoto;
    private ImageView mySelfIcon;
    private String username;
    private TextView mySelfTv;
    View cardSet;
   public static  Handler handler;
    private String title="上朝",content="提高办公水平,同事沟通,值得你拥有",url="http://139.196.151.162:8888/help/index.jsp";
    public MySelfPager(Activity activity) {
        super(activity);
    }
    @Override
    public void initData() {
        getPersonalData();
        String companyname = PrefUtils.getString(mActivity, "companyname", null);
        names = new String[]{TextUtils.isEmpty(companyname) ? "暂无" : companyname, "推荐给好友",
                "新手帮助", "系统设置"};
        view = View.inflate(mActivity, R.layout.my_self, null);
        upPic = (LinearLayout) view.findViewById(R.id.my_self_pic);
        mySelfRlv = (RecyclerView) view.findViewById(R.id.my_self_rlv);
        mySelfIcon = (ImageView) view.findViewById(R.id.my_self_icon);
        mySelfTv = (TextView) view.findViewById(R.id.my_self_tv);
        tvTitle.setVisibility(View.GONE);
        titleBack.setVisibility(View.GONE);
        upPic.setOnClickListener(this);
        mySelfRlv.setLayoutManager(new LinearLayoutManager(mActivity));
        myAdapter = new MyAdapter();
        mySelfRlv.addItemDecoration(new SpacesItemDecoration(UiUtils.dip2px(10)));
        mySelfRlv.setAdapter(myAdapter);
        flContainer.addView(view);
        handler=new Handler()
        {
            public void handleMessage(Message msg)
            {
                getPersonalData();
            }
        };

    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private MyViewHolder myViewHolder;
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            myViewHolder = new MyViewHolder(LayoutInflater.from(mActivity).inflate(R
                    .layout.my_self_item, parent, false));
            return myViewHolder;
        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.mySelfIv.setBackgroundResource(pics[position]);
            holder.mySelfTv.setText(names[position]);
        }
        @Override
        public int getItemCount() {
            return names.length;
        }
    }

    /**
     * RecycleyView item间距
     */
    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildPosition(view) != 0)
                outRect.top = space;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView    mySelfIv;
        TextView     mySelfTv;
        LinearLayout mySelfLl;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (getLayoutPosition()) {
                        case 0:
                            mActivity.startActivity(new Intent(mActivity, CompanyInfoActivity.class));
                            break;
                        case 1:
                            cardSet = UiUtils.getParentPopuwindow(mActivity, R.layout.pop_share);
                            cardSet.findViewById(R.id.myinfo_cancel).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    UiUtils.parentpopupWindow.dismiss();
                                }
                            });
                            cardSet.findViewById(R.id.window_rl).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    UiUtils.parentpopupWindow.dismiss();
                                }
                            });
                            cardSet.findViewById(R.id.friend).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    UiUtils.parentpopupWindow.dismiss();
                                    ZWXShare.getInstance().shareWX(title,content,R.mipmap.sharelogo,url);
                                }
                            });
                            cardSet.findViewById(R.id.circle).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    UiUtils.parentpopupWindow.dismiss();
                                    ZWXShare.getInstance().shareCircle(title,content,R.mipmap.sharelogo,url);
                                }
                            });
                            break;
                        case 2:
                            mActivity.startActivity(new Intent(mActivity, HelpActivity.class));
                            break;
                        case 3:
                            mActivity.startActivity(new Intent(mActivity, SystemSettingActivity
                                    .class));
                            break;
                        default:
                            break;
                    }
                }
            });
            mySelfIv = (ImageView) itemView.findViewById(R.id.my_self_iv);
            mySelfTv = (TextView) itemView.findViewById(R.id.my_self_tv);
            mySelfLl = (LinearLayout) itemView.findViewById(R.id.my_self_ll);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.my_self_pic:

                mActivity.startActivityForResult(new Intent(mActivity, PersonalDataActivity.class),1);
                break;
            default:
                break;
        }
    }

    /**
     * 获取个人信息
     */
    public void getPersonalData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "useinfo_query.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request.setCacheMode(CacheMode.NONE_CACHE_REQUEST_NETWORK);
            request.add(map);
            CallServer.getRequestInstance().add(mActivity, 0, request, httpListener, false, false);
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
            switch (what) {
                case 0:
                    String result = response.get();
                    Personal personal = processPerData(result);
                    System.out.println("个人信息数据result=" + result);
                    System.out.println("访问网络成功-----------------------Personal");
                    System.out.println("----------------------------personal=" + personal);
                    mySelfTv.setText(username);
                    Glide.with(mActivity).load("http://98.143.158.85:8888/" + empphoto).asBitmap
                            ().centerCrop().into(new BitmapImageViewTarget(mySelfIcon) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(mActivity.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            mySelfIcon.setImageDrawable(circularBitmapDrawable);
                        }
                    });

                    break;
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    private Personal processPerData(String result) {
        Gson gson = new Gson();
        Personal personal = gson.fromJson(result, Personal.class);
        PrefUtils.putString(mActivity, "username", personal.result.username);
        //头像
        empphoto = personal.result.empphoto;
        username = personal.result.username;
        return personal;
    }
}