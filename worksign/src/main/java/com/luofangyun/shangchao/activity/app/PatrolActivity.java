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
 * 应用→工作→巡检
 */
public class PatrolActivity extends BaseActivity {
    public View         view;
    public RecyclerView patrolRecy;
    public String[] names = {"巡检点设置", "巡检路线设置", "巡检记录"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_patrol);
        initView();
        initData();
    }

    public void initView() {
        patrolRecy = (RecyclerView) view.findViewById(R.id.patrol_recy);
    }

    public void initData() {
        patrolRecy.setLayoutManager(new LinearLayoutManager(this));
        patrolRecy.setAdapter(new MyAdapter());
        titleTv.setText("巡检");
        flAddress.addView(view);
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
                            startActivity(new Intent(getApplication(), PatrolPointActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(getApplication(), PatrolLineActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(getApplication(), PatrolRecordActivity.class));
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
