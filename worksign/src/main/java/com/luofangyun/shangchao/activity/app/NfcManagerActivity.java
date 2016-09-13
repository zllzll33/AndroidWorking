package com.luofangyun.shangchao.activity.app;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.global.GlobalConstants;
import com.luofangyun.shangchao.nohttp.CallServer;
import com.luofangyun.shangchao.nohttp.HttpListener;
import com.luofangyun.shangchao.utils.CardManager;
import com.luofangyun.shangchao.utils.MD5Encoder;
import com.luofangyun.shangchao.utils.Sign;
import com.luofangyun.shangchao.utils.UiUtils;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.xml.sax.XMLReader;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Nfc管理页面
 */
public class NfcManagerActivity extends BaseActivity implements Html.ImageGetter, Html.TagHandler {
    private View     view;
    private EditText nfcManagerEt1, nfcManagerEt2;
    private Map<String, String> map = new HashMap<>();
    private NfcAdapter    nfcAdapter;
    private   PendingIntent pendingIntent;
    private   Resources     res;
    private Intent Nfcintent;
    private String nfcId;
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.nfc_manager);
        final Resources res = getResources();
        this.res = res;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        onNewIntent(getIntent());
        initView();
        initData();
    }

    private void initView() {
        nfcManagerEt1 = (EditText) view.findViewById(R.id.nfc_manager_et1);
        nfcManagerEt2 = (EditText) view.findViewById(R.id.nfc_manager_et2);
    }

    private void initData() {
        right.setVisibility(View.VISIBLE);
        right.setText("保存");
        right.setOnClickListener(this);
        titleTv.setText("NFC管理");
        flAddress.addView(view);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.right) {
            if (TextUtils.isEmpty(nfcManagerEt1.getText().toString().trim())) {
                UiUtils.ToastUtils("NFC名称不能为空");
            } else if (TextUtils.isEmpty(nfcManagerEt2.getText().toString().trim())) {
                UiUtils.ToastUtils("NFC备注不能为空");
            } else {
                NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
                if (nfcAdapter == null) {
                    UiUtils.ToastUtils("设备不支持NFC！");
                    return;
                } else if (nfcAdapter!=null&&!nfcAdapter.isEnabled()) {
                    UiUtils.ToastUtils("请在系统设置中先启用NFC功能！");
                    return;
                }
                // 获取标签卡id
                byte[] byteArrayExtra = getIntent().getByteArrayExtra(
                        NfcAdapter.EXTRA_ID);
                //Nfc序列号
                nfcId = byteArrayToHexString(byteArrayExtra);
                System.out.println("Nfc序列号=" + nfcId);
                //finish();
                if (!TextUtils.isEmpty(nfcId)) {
                    getServerData();
                    finish();
                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null)
            nfcAdapter.enableForegroundDispatch(this, pendingIntent,
                    CardManager.FILTERS, CardManager.TECHLISTS);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        showData("onNewIntent");
        Nfcintent = intent;
        final Parcelable p = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        showData((p != null) ? CardManager.load(p, res) : null);
    }

    private void showData(String data) {
        if (data == null || data.length() == 0) {
            return;
        }
    }

    private Drawable spliter;

    @Override
    public Drawable getDrawable(String source) {
        final Resources res = this.res;
        Drawable ret = null;
        if (source.startsWith("spliter")) {
            if (spliter == null) {
                final int w = res.getDisplayMetrics().widthPixels;
                final int h = (int) (res.getDisplayMetrics().densityDpi / 72f + 0.5f);
                final int[] pix = new int[w * h];
                spliter = new BitmapDrawable(Bitmap.createBitmap(pix, w, h,
                        Bitmap.Config.ARGB_8888));
                spliter.setBounds(0, 3 * h, w, 4 * h);
            }
            ret = spliter;

        } else {
            ret = null;
        }
        return ret;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output,
                          XMLReader xmlReader) {
        if (!opening && "version".equals(tag)) {
            try {
                output.append(getPackageManager().getPackageInfo(
                        getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
    }

    /**
     * 将标签卡获取的原始id进行转化
     * */
    private String byteArrayToHexString(byte[] inarray)
    { // converts byte arrays to string
        int i, j, in;
        String[] hex =
                { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D",
                        "E", "F" };
        String out = "";

        for (j = 0; j < inarray.length; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    private void getServerData() {
        try {
            Request<String> request = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "label_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            map.put("access_id", "1234567890");
            map.put("timestamp", time);
            map.put("telnum", UiUtils.getPhoneNumber());
            map.put("labelcode", "0");
            map.put("labelname", nfcManagerEt1.getText().toString().trim());
            map.put("labelsn", nfcId);
            map.put("labeltype", String.valueOf(0));
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
            ApplyBean applyBean =  new Gson().fromJson(result, ApplyBean.class);
            System.out.println("Nfc获取数据=" + result);
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {
        }
    };

}