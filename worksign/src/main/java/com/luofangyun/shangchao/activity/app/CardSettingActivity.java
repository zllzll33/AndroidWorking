package com.luofangyun.shangchao.activity.app;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.utils.UiUtils;

/**
 * 打卡设置
 */
public class CardSettingActivity extends BaseActivity {
    View view;
    private RecyclerView cardSet;
    private MyAdapter    myAdapter;
    private String[] names = {"NFC打卡", "蓝牙打卡", "GPS打卡"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_card_setting);
        initView();
        initData();
    }

    private void initView() {
        cardSet = (RecyclerView) view.findViewById(R.id.card_set);
    }

    private void initData() {
        cardSet.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter();
        cardSet.setAdapter(myAdapter);
        titleTv.setText("打卡设置");
        flAddress.addView(view);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewholder> {

        @Override
        public MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewholder(LayoutInflater.from(getApplication()).inflate(R.layout
                    .card_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewholder holder, int position) {
            holder.cardSetTv.setText(names[position]);
            if (position == 0) {
                holder.buttonIv.setChecked(true);
            }
        }

        @Override
        public int getItemCount() {
            return names.length;
        }
    }

    class MyViewholder extends RecyclerView.ViewHolder {
        TextView     cardSetTv;
        ToggleButton buttonIv;
        public MyViewholder(View itemView) {
            super(itemView);
            cardSetTv = (TextView) itemView.findViewById(R.id.card_set_tv);
            buttonIv = (ToggleButton) itemView.findViewById(R.id.button_iv);
            buttonIv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                }
            });
        }
    }
}
