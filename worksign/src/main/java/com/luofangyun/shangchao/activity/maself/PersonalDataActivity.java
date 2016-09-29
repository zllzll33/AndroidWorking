package com.luofangyun.shangchao.activity.maself;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.base.impl.MySelfPager;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.domain.Gender;
import com.luofangyun.shangchao.domain.IconBean;
import com.luofangyun.shangchao.domain.Personal;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.DatePickerPopWindow;
import com.luofangyun.shangchao.utils.MD5Encoder;
import com.luofangyun.shangchao.utils.PrefUtils;
import com.luofangyun.shangchao.utils.SetImage;
import com.luofangyun.shangchao.utils.Sign;
import com.luofangyun.shangchao.utils.UiUtils;
import com.luofangyun.shangchao.utils.XDConstantValue;
import com.luofangyun.shangchao.utils.YearMonthDayDialogUtil;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.CacheMode;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 个人资料
 */

public class PersonalDataActivity extends BaseActivity {
    private View                view;
    private Request<String>     request1;
    private Map<String, String> map, map1, map2, map3, map4, map5;
    private TextView perConfirm, perCancel, perEmpsex, perArea, perPhone, perName, man,
            women, bigIcon, insteadIcon, saveIcon;
    private AlertDialog dialog, birDialog, addressDialog;
    private String berEtnameText, empphoto, telnum, empaddress="", empbirthday, username,
            empsex, empphone, personName, manText;
    private EditText perEtName, birEtname, addressName, perDate;
    private ImageView personalPic;
    private TextView  addressCancel, addressConfirm;
    private String addressText, rectifyAddress, getAddressText, birthdayText, picturePath,
            imagePath;
    private AlertDialog     dialog1;
    private FileInputStream imageStream;
    private IconBean        iconBean;
    private Uri             originalUri;
    private String          womenText;
    private SetImage        setImage;
    private LinearLayout    address, headPic, genderll, personal2, birthday;
    private LinearLayout        phone;
    private DatePickerPopWindow timePopuwindow;
    CharSequence  text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_personal_data);
        getAddressText = PrefUtils.getString(getApplication(), "rectifyAddress", null);
        birthdayText = PrefUtils.getString(this, "birthdayText", null);
        initView();
        initData();
    }

    private void initView() {
        personalPic = (ImageView) view.findViewById(R.id.per_empphoto);           //头像
        perPhone = (TextView) view.findViewById(R.id.per_phone);                  //电话
        perDate = (EditText) view.findViewById(R.id.per_empbirthday);             //生日
        perArea = (TextView) view.findViewById(R.id.per_empaddress);              //地区
        perName = (TextView) view.findViewById(R.id.per_username);                //姓名
        perEmpsex = (TextView) view.findViewById(R.id.per_empsex);                //性别
        phone = (LinearLayout) view.findViewById(R.id.phone);
        //地址
        address = (LinearLayout) view.findViewById(R.id.address);
        headPic = (LinearLayout) view.findViewById(R.id.personal_head_pic);
        genderll = (LinearLayout) view.findViewById(R.id.personal_gender);
        personal2 = (LinearLayout) view.findViewById(R.id.personal2);
        birthday = (LinearLayout) view.findViewById(R.id.birthday);
    }

    private void initData() {
        map = new HashMap<>();
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        map3 = new HashMap<>();
        map4 = new HashMap<>();
        map5 = new HashMap<>();
        getPersonalData();
        titleTv.setText("个人资料");
        birthday.setOnClickListener(this);
        headPic.setOnClickListener(this);
        perEmpsex.setOnClickListener(this);
        genderll.setOnClickListener(this);
        personal2.setOnClickListener(this);
        address.setOnClickListener(this);
        phone.setOnClickListener(this);
        flAddress.addView(view);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.personal_head_pic:
                dialog1 = UiUtils.getDialog(this, R.layout.personal_pic);
                bigIcon = (TextView) dialog1.findViewById(R.id.big_icon);
                insteadIcon = (TextView) dialog1.findViewById(R.id.instead_icon);
                saveIcon = (TextView) dialog1.findViewById(R.id.save_icon);
                bigIcon.setOnClickListener(this);
                insteadIcon.setOnClickListener(this);
                saveIcon.setOnClickListener(this);
                break;
            case R.id.big_icon:       //查看大头像
                System.out.println("被点击了");
                AlertDialog dialog = UiUtils.getDialog(this, R.layout.icon);
                ImageView iconIv = (ImageView) dialog.findViewById(R.id.icon_iv);
                Glide.with(this).load("http://139.196.151.162:8888/" + empphoto).into(iconIv);
                dialog1.dismiss();
                break;
            case R.id.instead_icon:
                setImage = new SetImage(this, getWindow().getDecorView());
                setImage.showPopupWindow("headPersonal");
                dialog1.dismiss();
                break;
            case R.id.save_icon:
                dialog1.dismiss();
              /*Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                System.out.println("picturePath=" + picturePath);
                personalPic.setImageBitmap(bitmap);*/
                break;
            case R.id.personal_gender:
                this.dialog = UiUtils.getDialog(this, R.layout.personal_gender);
                man = (TextView) this.dialog.findViewById(R.id.man);
                women = (TextView) this.dialog.findViewById(R.id.women);
                man.setOnClickListener(this);
                women.setOnClickListener(this);
                break;
            case R.id.man:
                manText = man.getText().toString().trim();
                getGenderData(manText);
                perEmpsex.setText(manText);
                this.dialog.dismiss();
                break;
            case R.id.women:
                womenText = women.getText().toString().trim();
                getGenderData(womenText);
                perEmpsex.setText(womenText);
                this.dialog.dismiss();
                break;
            case R.id.personal2:
                this.dialog = UiUtils.getDialog(this, R.layout.per_name_dialog);
                perEtName = (EditText) this.dialog.findViewById(R.id.per_et_name);
            /*    perEtName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        text=s.toString();
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                           if(text.length()>=15)
                           {
                               UiUtils.ToastUtils("字数小于15个");
                               perEtName.setText(text);
                           }
                    }
                });*/
                perConfirm = (TextView) this.dialog.findViewById(R.id.per_confirm);
                perCancel = (TextView) this.dialog.findViewById(R.id.per_cancel);
                perConfirm.setOnClickListener(this);
                perCancel.setOnClickListener(this);
                break;
            case R.id.per_confirm:
                personName = perEtName.getText().toString().trim();
                if (!personName.isEmpty()) {
                    getNameData(personName);
                    perName.setText(personName);
                }
                this.dialog.dismiss();
                break;
            case R.id.per_cancel:
                this.dialog.dismiss();
                break;
            case R.id.birthday:
                YearMonthDayDialogUtil yearMonthDayDialogUtil1 = new YearMonthDayDialogUtil(this,
                        "");
                yearMonthDayDialogUtil1.dateTimePicKDialog(perDate);
                yearMonthDayDialogUtil1.setDataListener(new YearMonthDayDialogUtil.DataListener() {
                    @Override
                    public void OnData(String data) {
                        getBirData(data);
                    }
                });
                break;
            case R.id.phone:
//                UiUtils.ToastUtils("手机号不可修改");
                break;
            case R.id.address:
                addressDialog = UiUtils.getDialog(this, R.layout.address_empt_dialog);
                addressName = (EditText) addressDialog.findViewById(R.id.address_et_name);
                addressName.setText(empaddress);
                addressCancel = (TextView) addressDialog.findViewById(R.id.address_cancel);
                addressConfirm = (TextView) addressDialog.findViewById(R.id.address_confirm);
                addressCancel.setOnClickListener(this);
                addressConfirm.setOnClickListener(this);
                break;
            case R.id.address_cancel:
                addressDialog.dismiss();
                break;
            case R.id.address_confirm:
                addressText = this.addressName.getText().toString().trim();
                getAddressData(addressText);
                addressDialog.dismiss();
                break;
            default:
                break;
        }
    }
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
                originalUri = data.getData();
                setImage.cropRawPhoto(originalUri);
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
                System.out.println("图片路径：" + imagePath);
                imageStream = new FileInputStream(imagePath);
                // Glide.with(this).load(imagePath).into(personalPic);

                Glide.with(this).load(imagePath).asBitmap().centerCrop().into(new BitmapImageViewTarget(personalPic) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        personalPic.setImageDrawable(circularBitmapDrawable);
                    }
                });


                getServerIcon();
            } catch (IOException e) {
            }
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
            request.add(map);
            CallServer.getRequestInstance().add(this, 0, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改姓名
     */
    public void getNameData(String name) {
        try {
            request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL + "useinfo_edt.json",
                    RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("ufield", String.valueOf(0));
            map1.put("value", name);
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request1.add(map1);
            CallServer.getRequestInstance().add(this, 1, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改性别
     */
    public void getGenderData(String gender) {
        try {
            Request<String> request2 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "useinfo_edt.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map2.put("access_id", "1234567890");
            map2.put("timestamp", time);
            map2.put("telnum", UiUtils.getPhoneNumber());
            map2.put("ufield", String.valueOf(1));
            map2.put("value", gender);
            String encode = MD5Encoder.encode(Sign.generateSign(map2) +
                    "12345678901234567890123456789011");
            map2.put("sign", encode);
            request2.add(map2);
            CallServer.getRequestInstance().add(this, 2, request2, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改生日
     */

    public void getBirData(String data) {
        try {
            Request<String> request3 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL
                    + "useinfo_edt.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map3.put("access_id", "1234567890");
            map3.put("timestamp", time);
            map3.put("telnum", UiUtils.getPhoneNumber());
            map3.put("ufield", Integer.toString(2));
            map3.put("value", data);
            String encode = MD5Encoder.encode(Sign.generateSign(map3) +
                    "12345678901234567890123456789011");
            map3.put("sign", encode);
            request3.add(map3);
            CallServer.getRequestInstance().add(this, 3, request3, httpListener, false, false);
            String birthday = URLDecoder.decode(URLDecoder.decode(data, "UTF-8"), "GBK");
            PrefUtils.putString(this, "birthdayText", birthday);
            perDate.setText(birthday);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改地址
     */
    public void getAddressData(String address) {
        try {
            Request<String> request4 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL
                    + "useinfo_edt.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map4.put("access_id", "1234567890");
            map4.put("timestamp", time);
            map4.put("telnum", UiUtils.getPhoneNumber());
            map4.put("ufield", String.valueOf(3));
            map4.put("value", address);
            String encode = MD5Encoder.encode(Sign.generateSign(map4) +
                    "12345678901234567890123456789011");
            map4.put("sign", encode);
            request4.add(map4);
            CallServer.getRequestInstance().add(this, 4, request4, httpListener, false, false);
            rectifyAddress = address;
            PrefUtils.putString(this, "rectifyAddress", rectifyAddress);
            perArea.setText(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更改头像
     */

    private void getServerIcon() {
        try {
            Request<String> request5 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "photo_edt.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map5.put("access_id", "1234567890");
            map5.put("timestamp", time);
            map5.put("telnum", UiUtils.getPhoneNumber());
            String encode = MD5Encoder.encode(Sign.generateSign(map5) +
                    "12345678901234567890123456789011");
            map5.put("sign", encode);
            // 上传文件
            request5.add("imgfile", new FileBinary(new File(imagePath)));
            request5.setCacheMode(CacheMode.NONE_CACHE_REQUEST_NETWORK);
            request5.add(map5);
            CallServer.getRequestInstance().add(this, 5, request5, httpListener, false, false);
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


                    Glide.with(getApplication()).load("http://139.196.151.162:8888/" + empphoto)
                            .asBitmap().centerCrop().into(new BitmapImageViewTarget(personalPic) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            personalPic.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                    perPhone.setText(empphone);
                    perName.setText(personal.result.username);
                    perArea.setText(empaddress);
                    perDate.setText(empbirthday);
                    perEmpsex.setText(empsex);
                    break;
                case 1:
                    String result1 = response.get();
                    System.out.println("修改姓名result1=" + result1);
//                    MySelfPager.handler.sendEmptyMessage(1);
                    processNameData(result1);
                    break;
                case 2:
                    String result2 = response.get();
                    System.out.println("进来了");
                    System.out.println("修改性别result2=" + result2);
                    processGender(result2);
                    break;
                case 3:
                    String result3 = response.get();
                    System.out.println("修改生日result4=" + result3);
                    break;
                case 4:
                    String result4 = response.get();
                    System.out.println("修改地址result4=" + result4);
                    break;
                case 5:
                    String result5 = response.get();
                    System.out.println("修改成功了……");
                    System.out.println("修改头像result5=" + result5);
                    IconBean iconBean = processIcon(result5);
                default:
                    break;
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

    /**
     * 更改头像
     */
    private IconBean processIcon(String json) {
        Gson gson = new Gson();
        iconBean = gson.fromJson(json, IconBean.class);
        return iconBean;
    }

    /**
     * 个人信息解析数据
     */
    private Personal processPerData(String result) {
        try {
            Gson gson = new Gson();
            Personal personal = gson.fromJson(result, Personal.class);
            empphoto = personal.result.empphoto;         //头像
            telnum = personal.result.telnum;             //用户名
            empaddress = personal.result.empaddress;     //地址
            empbirthday = personal.result.empbirthday;   //生日日期
            username = personal.result.username;         //姓名
            empsex = personal.result.empsex;             //性别
            empphone = personal.result.empphone;         //手机号
            return personal;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 性别数据解析
     */
    private void processGender(String result2) {
        Gson gson = new Gson();
        Gender gender = gson.fromJson(result2, Gender.class);
    }

    private void processNameData(String json) {
        Gson gson = new Gson();
        ApplyBean personData = gson.fromJson(json, ApplyBean.class);
        System.out.println("状态码=" + personData.status);
    }
}
