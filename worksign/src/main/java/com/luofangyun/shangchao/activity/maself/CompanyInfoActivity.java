package com.luofangyun.shangchao.activity.maself;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.luofangyun.shangchao.R;
import com.luofangyun.shangchao.base.BaseActivity;
import com.luofangyun.shangchao.utils.PrefUtils;
import com.luofangyun.shangchao.utils.UiUtils;

/**
 * 公司信息
 */
public class CompanyInfoActivity extends BaseActivity {

    private View     view;
    private String[] names;
    private RecyclerView companyRlv;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_info_company);
        initView();
        initData();
    }

    private void initView() {
        companyRlv = (RecyclerView) view.findViewById(R.id.company_rlv);
    }

    private void initData() {
        String companyname = PrefUtils.getString(this, "companyname", null);    //企业名称
        String deptname = PrefUtils.getString(this, "deptname", null);          //部门名称
        String emppost = PrefUtils.getString(this, "emppost", null);            //职位
        names = new String[]{"公司：" + (TextUtils.isEmpty(companyname) ? "暂无" : companyname), "部门："
                + (TextUtils.isEmpty(deptname) ? "暂无" : deptname), "职位：" + (TextUtils.isEmpty
                (emppost) ? "暂无" : emppost)};
        companyRlv.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter();
        companyRlv.setAdapter(myAdapter);
        titleTv.setText("公司信息");
        flAddress.addView(view);
    }
    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(getApplication())
                    .inflate(R.layout.company_info_item, parent, false));
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
             holder.infoTv.setText(names[position]);
        }

        @Override
        public int getItemCount() {
            return names.length;
        }
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView infoTv;
        public MyViewHolder(View itemView) {
            super(itemView);
            infoTv = (TextView) itemView.findViewById(R.id.info_tv);
        }
    }
}
