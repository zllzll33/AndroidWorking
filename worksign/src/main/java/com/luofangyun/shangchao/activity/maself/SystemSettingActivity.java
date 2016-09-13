package com.luofangyun.shangchao.activity.maself;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.activity.message.UserEnterActivity;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.utils.DataCleanManager;
import com.luofangyun.shangchao.utils.PrefUtils;
import com.luofangyun.shangchao.utils.UiUtils;

/**
 * 系统设置
 */
public class SystemSettingActivity extends BaseActivity {

    private View         view;
    private RecyclerView systemRlv;
    private TextView systemTv;
    private MyAdapter    myAdapter;
    private String[] names = {"修改密码", "清理缓存"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_system_setting);
        initView();
        initData();
    }

    private void initView() {
        systemRlv = (RecyclerView) view.findViewById(R.id.system_rlv);
        systemTv = (TextView) view.findViewById(R.id.system_tv);
    }

    private void initData() {
        systemRlv.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter();
        systemRlv.setAdapter(myAdapter);
        titleTv.setText("设置");
        systemTv.setOnClickListener(onClickListener);
        flAddress.addView(view);
    }
   private View.OnClickListener onClickListener = new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           AlertDialog dialog = new AlertDialog.Builder(SystemSettingActivity.this).setTitle("您确定要退出登录吗？")
                   .setPositiveButton("确定", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   startActivity(new Intent(SystemSettingActivity.this, UserEnterActivity.class));
                   PrefUtils.putBoolean(getApplication(),"loginstaus",false);
                   finish();
               }
           }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {

               }
           }).show();

       }
   };
    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(getApplication())
                    .inflate(R.layout.system_item, parent, false));
            return myViewHolder;
        }
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.systemTv.setText(names[position]);
            if (position == names.length - 1) {
                holder.systemLineTv.setVisibility(View.GONE);
            }
        }
        @Override
        public int getItemCount() {
            return names.length;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlt;
        TextView       systemTv, systemLineTv;
        ImageView systemIv;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (getLayoutPosition()) {
                        case 0:
                            startActivity(new Intent(SystemSettingActivity.this, ModifyPasswordActivity.class));
                            break;
                        case 1:
                            try {
                                String totalCacheSize = DataCleanManager.getTotalCacheSize
                                        (getApplicationContext());
                                System.out.println("缓存大小=" + totalCacheSize);
                                DataCleanManager.clearAllCache(getApplicationContext());
                                UiUtils.ToastUtils("清理缓存" + totalCacheSize);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
            rlt = (RelativeLayout) itemView.findViewById(R.id.rlt);
            systemTv = (TextView) itemView.findViewById(R.id.system_tv);
            systemIv = (ImageView) itemView.findViewById(R.id.system_iv);
            systemLineTv = (TextView) itemView.findViewById(R.id.system_line_tv);
        }
    }

}
