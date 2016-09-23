package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.AfficheBean;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.MD5Encoder;
import com.luofangyun.shangchao.utils.MyBitmapImageViewTarget;
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
 * 应用→公告
 */
public class AfficheActivity extends BaseActivity {
    private View                     view;
    private PullLoadMoreRecyclerView afficheRecy;
    private Map<String, String> map = new HashMap<>();
    private AfficheBean                        afficheBean;
    private ArrayList<AfficheBean.Result.Json> dataList;
    private MyAdapter                          myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_affiche);
        initView();
        initData();
    }

    private void initView() {
        afficheRecy = (PullLoadMoreRecyclerView) view.findViewById(R.id.affiche_recy);
    }
    private void initData() {
        getServerData();
        afficheRecy.setLinearLayout();
        myAdapter = new MyAdapter();
        afficheRecy.setAdapter(myAdapter);
        titleTv.setText("公告");
        right.setVisibility(View.VISIBLE);
        right.setText("发公告");
        refurbish();
        flAddress.addView(view);
    }

    private void refurbish() {
        afficheRecy.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
//                getServerData();
                UiUtils.upDownRefurbish(afficheRecy);
            }
            @Override
            public void onLoadMore() {
                UiUtils.upDownRefurbish(afficheRecy);
            }
        });

    }

    private void getServerData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "com_notify_list.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("pindex", String.valueOf(1));
            map.put("psize", String.valueOf(200));
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
            System.out.println("公告数据:" + response.get());
            afficheBean = new Gson().fromJson(response.get(), AfficheBean.class);
            dataList = afficheBean.result.data;
            myAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.right:
                startActivityForResult(new Intent(this, WriteAfficheActivity.class), 1);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            getServerData();
            myAdapter.notifyDataSetChanged();
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from
                    (getApplicationContext()).inflate(R.layout.affiche_item, null));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.title.setText(dataList.get(position).notifytitle);
            holder.affiche_time.setText(dataList.get(position).notifydate);

            Glide.with(getApplication()).load("http://139.196.151.162:8888/" + dataList.get
                    (position).images.get(0)).asBitmap().centerCrop().placeholder(R.drawable
                    .moren).into(new MyBitmapImageViewTarget(holder.affiche_pic));

            holder.affiche_content.setText(dataList.get(position).notifycontent);
            holder.affiche_look_all_content.setText("查看全文");
            Glide.with(getApplication()).load("http://139.196.151.162:8888/" + dataList.get
                    (position).empphoto).asBitmap()
                    .centerCrop().into(new BitmapImageViewTarget(holder.affiche_icon) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    holder.affiche_icon.setImageDrawable(circularBitmapDrawable);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (dataList != null) {
                return dataList.size();
            } else {
                return 0;
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, affiche_time, affiche_content, affiche_look_all_content;
        ImageView affiche_pic, affiche_icon;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplication(), AfficheNextActivity.class);
                    intent.putExtra("notifytitle", dataList.get(getLayoutPosition()).notifytitle);
                    intent.putExtra("notifydate", dataList.get(getLayoutPosition()).notifydate);
                    intent.putExtra("images", "http://139.196.151.162:8888/" + dataList.get
                            (getLayoutPosition()).images.get(0));
                    intent.putExtra("notifycontent", dataList.get(getLayoutPosition())
                            .notifycontent);
                    intent.putExtra("empphoto", "http://139.196.151.162:8888/" + dataList.get
                            (getLayoutPosition()).empphoto);
                    startActivity(intent);
                }
            });
            title = (TextView) view.findViewById(R.id.title);
            affiche_time = (TextView) view.findViewById(R.id.affiche_time);
            affiche_pic = (ImageView) view.findViewById(R.id.affiche_pic);
            affiche_content = (TextView) view.findViewById(R.id.affiche_content);
            affiche_look_all_content = (TextView) view.findViewById(R.id.affiche_look_all_content);
            affiche_icon = (ImageView) view.findViewById(R.id.affiche_icon);
        }
    }
}
