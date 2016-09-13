package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.utils.UiUtils;

/**
 * 考勤设置
 */
public class AttSettingctivity extends BaseActivity {

    private View         view;
    private RecyclerView attSetRlv;
    private MyAdapter    myAdapter;
    private String[] names = {"班段设置", "考勤统计", "打卡设置"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_att_settingctivity);
        initView();
        initData();
    }
    private void initView() {
        attSetRlv = (RecyclerView) view.findViewById(R.id.att_set_rlv);
    }

    private void initData() {
        attSetRlv.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter();
        attSetRlv.setAdapter(myAdapter);
        titleTv.setText("考勤设置");
        flAddress.addView(view);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getApplication()).inflate(R.layout
                    .att_set_item1, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.attSetTv.setText(names[position]);
        }

        @Override
        public int getItemCount() {
            return names.length;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView attSetTv;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (getLayoutPosition()) {
                        case 0:
                            startActivity(new Intent(AttSettingctivity.this, ClassActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(AttSettingctivity.this, AttAreaActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(AttSettingctivity.this, CardSettingActivity.class));
                            break;
                        default:
                            break;
                    }
                }
            });
            attSetTv = (TextView) itemView.findViewById(R.id.att_set_tv);
        }
    }

}
