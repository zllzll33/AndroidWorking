package com.luofangyun.shangchao.activity.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.utils.MyBitmapImageViewTarget;
import com.luofangyun.shangchao.utils.UiUtils;

public class AfficheNextActivity extends BaseActivity {
    private View view;
    private TextView title;
    private TextView affiche_time;
    private ImageView affiche_pic;
    private TextView affiche_content;
    private ImageView affiche_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.affiche_next);
        initView();
        initData();
    }

    private void initView() {
        title = (TextView) view.findViewById(R.id.title);
        affiche_time = (TextView) view.findViewById(R.id.affiche_time);
        affiche_pic = (ImageView) view.findViewById(R.id.affiche_pic);
        affiche_content = (TextView) view.findViewById(R.id.affiche_content);
        affiche_icon = (ImageView) view.findViewById(R.id.affiche_icon);
    }

    private void initData() {
        titleTv.setText("公告详情");
        String notifytitle = getIntent().getStringExtra("notifytitle");
        String notifydate = getIntent().getStringExtra("notifydate");
        String images = getIntent().getStringExtra("images");
        String notifycontent = getIntent().getStringExtra("notifycontent");
        String empphoto = getIntent().getStringExtra("empphoto");
        title.setText(notifytitle);
        affiche_time.setText(notifydate);
        Glide.with(getApplication()).load(images).asBitmap().centerCrop().placeholder(R.drawable.moren)
                .into(new MyBitmapImageViewTarget(affiche_pic));
        affiche_content.setText(notifycontent);
        Glide.with(getApplication()).load(empphoto).asBitmap()
                .centerCrop().into(new BitmapImageViewTarget(affiche_icon) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                affiche_icon.setImageDrawable(circularBitmapDrawable);
            }
        });
        flAddress.addView(view);
    }
}
