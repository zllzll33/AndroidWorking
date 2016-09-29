package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.MainActivity;
import com.luofangyun.shangchao.activity.MyCaptureActivity;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.utils.PrefUtils;
import com.luofangyun.shangchao.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 打卡设置
 */
public class CardSettingActivity extends BaseActivity {
    View view;
    private RecyclerView cardSet;
    private MyAdapter    myAdapter;
    private String[] names = {"NFC打卡", "蓝牙打卡", "GPS打卡"};
    private NfcAdapter nfcAdapter;
    private List<Card> cards=new ArrayList<>();
    public class Card{
        public Card(String name,boolean isOpen)
        {
            this.name=name;
            this.isOpen=isOpen;
        }
        private String name;
        private boolean isOpen;
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_card_setting);
        Card card1=new Card("NFC打卡",false);
        Card card2=new Card("蓝牙打卡",false);
        Card card3=new Card("GPS打卡",false);
        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        initView();
        initData();
    }

    private void initView() {
        cardSet = (RecyclerView) view.findViewById(R.id.card_set);
    }
    private void initData() {
        cardSet.setLayoutManager(new LinearLayoutManager(this));
        String labelTvText = PrefUtils.getString(CardSettingActivity.this, "confirmNameText", null);
        if (TextUtils.isEmpty(labelTvText)) {
             cards.get(2).setOpen(true);
        } else {
            if (labelTvText.contains("GPS")) {
                cards.get(2).setOpen(true);
            } else if (labelTvText.contains("NFC")) {
                cards.get(0).setOpen(true);
            } else if (labelTvText.contains("蓝牙")) {
                cards.get(1).setOpen(true);
            }
        }
        myAdapter = new MyAdapter();
        cardSet.setAdapter(myAdapter);
        titleTv.setText("打卡设置");
        flAddress.addView(view);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewholder> {
        @Override
        public MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewholder(LayoutInflater.from(getApplication()).inflate(R.layout
                    .card_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final MyViewholder holder,final int position) {
            holder.cardSetTv.setText(cards.get(position).getName());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(cards.get(position).isOpen==true) {
                        holder.buttonIv.setChecked(true);
                        holder.buttonIv.setClickable(false);
                    }
                    else if(cards.get(position).isOpen==false) {
                        holder.buttonIv.setChecked(false);
                        holder.buttonIv.setClickable(true);
                    }
                }
            }, 50);
        }

        @Override
        public int getItemCount() {
            return cards.size();
        }
    }
    class MyViewholder extends RecyclerView.ViewHolder {
        TextView     cardSetTv;
        ToggleButton buttonIv;
        public MyViewholder(View itemView) {
            super(itemView);
            cardSetTv = (TextView) itemView.findViewById(R.id.card_set_tv);
            buttonIv = (ToggleButton) itemView.findViewById(R.id.button_iv);
            buttonIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.e("positon", String.valueOf(getLayoutPosition()));
                    if(cards.get(getLayoutPosition()).isOpen==false) {
                   /*     for (int i = 0; i < cards.size(); i++) {
                            cards.get(i).setOpen(false);
                        }*/
//                        cards.get(getLayoutPosition()).setOpen(true);
                        if(getLayoutPosition()==0)
                        {
                            if (nfcAdapter == null) {
                                UiUtils.ToastUtils("此设备不支持NFC");
                                return;
                            }else {
                                PrefUtils.putString(CardSettingActivity.this, "confirmNameText", "NFC");
                                for (int i = 0; i < cards.size(); i++) {
                                    cards.get(i).setOpen(false);
                                }
                                cards.get(getLayoutPosition()).setOpen(true);
                            }
//
                        }
                        else if(getLayoutPosition()==1) {
                            PrefUtils.putString(CardSettingActivity.this, "confirmNameText", "蓝牙");
                            for (int i = 0; i < cards.size(); i++) {
                                cards.get(i).setOpen(false);
                            }
                            cards.get(getLayoutPosition()).setOpen(true);
                        }
                        else if(getLayoutPosition()==2) {
                            PrefUtils.putString(CardSettingActivity.this, "confirmNameText", "GPS");
                            for (int i = 0; i < cards.size(); i++) {
                                cards.get(i).setOpen(false);
                            }
                            cards.get(getLayoutPosition()).setOpen(true);
                        }
                        myAdapter.notifyDataSetChanged();
                    }
                }
            });

        }
    }
}
