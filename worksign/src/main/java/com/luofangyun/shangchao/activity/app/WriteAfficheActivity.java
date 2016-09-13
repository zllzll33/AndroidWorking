package com.luofangyun.shangchao.activity.app;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.MD5Encoder;
import com.luofangyun.shangchao.utils.MyBitmapImageViewTarget;
import com.luofangyun.shangchao.utils.PrefUtils;
import com.luofangyun.shangchao.utils.SetImage;
import com.luofangyun.shangchao.utils.Sign;
import com.luofangyun.shangchao.utils.UiUtils;
import com.luofangyun.shangchao.utils.XDConstantValue;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.CacheMode;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 公告→发公告→公告
 */
public class WriteAfficheActivity extends BaseActivity {
    private View      view;
    private ImageView writeIv;
    private TextView  writeTv;
    private EditText  title, writeContent;
    private Map<String, String> map = new HashMap<>();
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(1);
        view = UiUtils.inflateView(R.layout.activity_write_affiche);
        initView();
        initData();
    }

    private void initView() {
        writeIv = (ImageView) view.findViewById(R.id.write_iv);
        writeTv = (TextView) view.findViewById(R.id.write_tv);
        title = (EditText) view.findViewById(R.id.title);
        writeContent = (EditText) view.findViewById(R.id.write_content);
    }
    private void initData() {
        right.setText("发送");
        right.setVisibility(View.VISIBLE);
        titleTv.setText("公告");
        writeTv.setOnClickListener(this);
        flAddress.addView(view);
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.write_tv:
                SetImage setImage = new SetImage(this, getWindow().getDecorView());
                setImage.choseHeadImageFromGallery();
                break;
            case R.id.right:
                if (TextUtils.isEmpty(title.toString().trim())) {
                    UiUtils.ToastUtils("标题不能为空");
                } else {
                    UiUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getServerData();
                        }
                    });
                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void getServerData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "notify_add.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("companycode", PrefUtils.getString(getApplication(), "companycode", null));
            map.put("title", title.getText().toString().trim());
            map.put("content", writeContent.getText().toString().trim());
            String encode = MD5Encoder.encode(Sign.generateSign(map) +
                    "12345678901234567890123456789011");
            map.put("sign", encode);
            request.add("imglist", new FileBinary(new File(imagePath)));
            request.add(map);
            request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);
            CallServer.getRequestInstance().add(this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            Log.e("----------", "onSucceed: " + response.get());
            UiUtils.ToastUtils(new Gson().fromJson(response.get(), ApplyBean.class).summary);
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        Bitmap bm = null;
        //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = getContentResolver();
        //此处的用于判断接收的Activity是不是你想要的那个
        if (requestCode == XDConstantValue.CODE_GALLERY_REQUEST) {
            try {
                //获得图片的uri
                Uri originalUri = data.getData();
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //得到bitmap图片]
                //获取图片的路径：
                String[] proj = {MediaStore.Images.Media.DATA};
                //android多媒体数据库的封装接口，具体的看Android文档
                Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                //获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                //将光标移至开头 ，这个很重要，不小心很容易引起越界
                cursor.moveToFirst();
                imagePath = cursor.getString(column_index);
                Glide.with(getApplication()).load(imagePath).asBitmap().centerCrop().placeholder
                        (R.drawable.moren).into(new MyBitmapImageViewTarget(writeIv));
            } catch (IOException e) {

            }
        }
    }
}
