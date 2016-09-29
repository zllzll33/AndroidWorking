package com.luofangyun.shangchao.activity.app;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.domain.ApplyBean;
import com.luofangyun.shangchao.domain.Label;
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
 * Created by win7 on 2016/9/14.
 */
public class AddNFCTagActivity extends BaseActivity{
    private View view;
    EditText tag_name,tag_note;
    private NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    String rfid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    private  void init()
    {
        titleTv.setText("添加NFC标签");
        right.setText("提交");
        right.setVisibility(View.VISIBLE);
        view= UiUtils.inflateView(R.layout.act_add_nfc_tag);
        flAddress.addView(view);
        tag_name=(EditText)view.findViewById(R.id.tag_name);
        tag_note=(EditText)view.findViewById(R.id.tag_note);
        right.setOnClickListener(this);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            UiUtils.ToastUtils("此设备不支持NFC");
            return;
        }else if (!nfcAdapter.isEnabled()) {
            UiUtils.ToastUtils("请打开NFC");
            startActivity(new Intent("android.settings.NFC_SETTINGS"));
            return;
        }else {
            pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                    getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
            ndef.addCategory("*/*");
            mFilters = new IntentFilter[]{ndef};// 过滤器
            mTechLists = new String[][]{
                    new String[]{MifareClassic.class.getName()},
                    new String[]{NfcA.class.getName()}};// 允许扫描的标签类型
        }
    }
    @Override
    public  void onClick(View v)
    {
        super.onClick(v);
        if(v.getId()==R.id.right)
        saveNFCtag();
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, mFilters,
                mTechLists);

    }
    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            String result = processIntent(intent);
//            Log.e("nfcresult", result);

        }
        if (nfcAdapter != null && nfcAdapter.isEnabled())
        {
            // 获取标签卡id
            byte[] byteArrayExtra = intent.getByteArrayExtra(
                    NfcAdapter.EXTRA_ID);

            if(byteArrayExtra == null)
                return;
            rfid = byteArrayToHexString(byteArrayExtra);
//            Log.e("rfid", rfid);
            UiUtils.ToastUtils("感应成功");
        }
    }
    private String processIntent(Intent intent) {
        Parcelable[] rawmsgs = intent
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawmsgs[0];
        NdefRecord[] records = msg.getRecords();
        String resultStr = new String(records[0].getPayload());
        return resultStr;
    }
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
    private void saveNFCtag()
    {
        if(tag_name.getText().toString().isEmpty())
        {
            UiUtils.ToastUtils("请输入NFC标签名称");
            return;
        }else if(TextUtils.isEmpty(rfid))
        {
            UiUtils.ToastUtils("请感应NFC卡");
            return;
        }
        try {
            Request<String> request1 = NoHttp.createStringRequest(GlobalConstants.SERVER_URL +
                    "label_mng.json", RequestMethod.POST);
            String time = Long.toString(new Date().getTime());
            Map<String,String> map1=new HashMap<>();
            map1.put("access_id", "1234567890");
            map1.put("timestamp", time);
            map1.put("telnum", UiUtils.getPhoneNumber());
            map1.put("labelcode", "0");
            map1.put("labeltype", "0");
            map1.put("labelsn",rfid);
            map1.put("labelname",tag_name.getText().toString() );
            String encode = MD5Encoder.encode(Sign.generateSign(map1) +
                    "12345678901234567890123456789011");
            map1.put("sign", encode);
            request1.add(map1);
            CallServer.getRequestInstance().add(this, 1, request1, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            switch (what) {
                case 1:
//                    Log.e("nfcadd",response.get());
                    ApplyBean applyBean=new Gson().fromJson(response.get(),ApplyBean.class);
                    if(applyBean.status.equals("00000")) {
                        NfcLabelActivity.handler.sendEmptyMessage(1);
                        finish();
                    }
                    else
                    UiUtils.ToastUtils(applyBean.summary);
                 break;
                case 2:
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
