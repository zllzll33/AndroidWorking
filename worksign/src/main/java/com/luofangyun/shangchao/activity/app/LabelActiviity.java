package com.luofangyun.shangchao.activity.app;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luofangyun.shangchao.R;

/**
 * 标签设置
 */
public class LabelActiviity extends PatrolActivity {
    private String[] names = {"NFC标签管理", "蓝牙标签管理", "区域标签管理"};
    @Override
    public void initData() {
        super.initData();
        titleTv.setText("标签设置");
        patrolRecy.setLayoutManager(new LinearLayoutManager(this));
        patrolRecy.setAdapter(new MyAdapter());
    }
    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(getApplication())
                    .inflate(R.layout.search_item, parent, false));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.leftText.setText(names[position]);
        }

        @Override
        public int getItemCount() {
            return names.length;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView leftText;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (getPosition()) {
                        case 0:
                            startActivity(new Intent(getApplication(), NfcLabelActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(getApplication(), BlueLabelActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(getApplication(), AreaLabelActivity.class));
                            break;
                        default:
                            break;
                    }
                }
            });
            leftText = (TextView) itemView.findViewById(R.id.left_text);
        }
    }
}
