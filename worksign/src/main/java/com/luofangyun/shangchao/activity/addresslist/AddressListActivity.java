package com.luofangyun.shangchao.activity.addresslist;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.base.impl.AddressListPager;
import com.luofangyun.shangchao.domain.ConnenctBean;
import com.luofangyun.shangchao.domain.SearchBean;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by win7 on 2016/9/13.
 */
public class AddressListActivity extends BaseActivity {
    private View view;
    RecyclerView recyclerView;
    List<ConnenctBean.Result>  dateList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleTv.setText("联系人");
        view=  UiUtils.inflateView(R.layout.act_address_list);
        recyclerView=(RecyclerView)view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        flAddress.addView(view);
        ReadAllContacts();
        MyAdapter myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
    }
    public void ReadAllContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        int contactIdIndex = 0;
        int nameIndex = 0;

        if(cursor.getCount() > 0) {
            contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }
        while(cursor.moveToNext()) {
            String contactId = cursor.getString(contactIdIndex);
            String name = cursor.getString(nameIndex);
            Log.e("phonename", name);
            ConnenctBean.Result connects=new ConnenctBean.Result();
            connects.empname=name;
            Cursor phones = AddressListActivity.this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                    null, null);
            int phoneIndex = 0;
            if(phones.getCount() > 0) {
                phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            }
            while(phones.moveToNext()) {
                String phoneNumber = phones.getString(phoneIndex);
                connects.empphone=phoneNumber;
                Log.e("phone", phoneNumber);
            }
            dateList.add(connects);
        }
        Log.e("size",String.valueOf(dateList.size()));

    }
    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(AddressListActivity.this).inflate(R
                    .layout.list_item, parent, false));
            return myViewHolder;
        }
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            holder.listTv1.setText(dateList.get(position).empname);
            Log.e("list_name",dateList.get(position).empname);
                try{
                holder.listTv2.setText(dateList.get(position).empphone);
                Log.e("list_phone",dateList.get(position).empphone);
            }catch (Exception e)
            {
//                UiUtils.ToastUtils(dateList.get(position).empname+"该用户未添加号码");
            }

            holder.index=position;
            holder.ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        String tel=dateList.get(holder.index).empphone;
                        if(TextUtils.isEmpty(tel))
                            UiUtils.ToastUtils("该用户未添加号码");
                        else {
                            Uri data = Uri.parse("tel:" + tel);
                            intent.setData(data);
                            startActivity(intent);
                            savePhone(dateList.get(holder.index).empphone);
                        }
                    }catch (Exception e)
                    {
                    UiUtils.ToastUtils("该用户未添加号码");
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return dateList.size();
        }
    }
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
            request.add(map);
            CallServer.getRequestInstance().add(AddressListActivity.this, 1, request, httpListener, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private HttpListener<String> httpListener = new HttpListener<String>() {
        @Override
        public void onSucceed(int what, Response<String> response) {
            String result = response.get();
            AddressListPager.handler.sendEmptyMessage(1)  ;
         Log.e("phoneAdd",result);
        }
        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int
                responseCode, long networkMillis) {

        }
    };
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView listTv1, listTv2;
        LinearLayout ll_item;
        int index;
        public MyViewHolder(View itemView) {
            super(itemView);
            listTv1 = (TextView) itemView.findViewById(R.id.list_tv1);
            listTv2 = (TextView) itemView.findViewById(R.id.list_tv2);
            ll_item=(LinearLayout)itemView.findViewById(R.id.ll_item);
        }
    }
}
