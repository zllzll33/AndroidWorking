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
import com.yunliwuli.beacon.kit.data.BluetoothDeviceAndRssi;
import com.yunliwuli.beacon.kit.manager.YlwlManager;
import com.yunliwuli.beacon.kit.manager.YlwlManagerListener;

import java.util.ArrayList;
import java.util.Collections;

public class BlueListActivitiy extends BaseActivity {

    private View view;
    private ArrayList<BluetoothDeviceAndRssi> deviceList = new ArrayList<>();
    private RecyclerView listRecy;
    private MyAdapter    myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = UiUtils.inflateView(R.layout.activity_blue_list_activitiy);
        initView();
        initData();
    }

    private void initView() {
        listRecy = (RecyclerView) view.findViewById(R.id.blue_list_recy);
    }

    private void initData() {
        final YlwlManager ylwlmanager = YlwlManager.getInstance(this);
        /**
         * 设置启用云服务 (上传传感器数据，如电量、rssi等)。如果不设置，默认为关闭状态。
         **/
        ylwlmanager.setCloudEnable(true);
        /**
         *  开启服务
         */
        ylwlmanager.startService();
        /**
         *  扫描
         */
        ylwlmanager.scanLeDevice(true);
        YlwlManagerListener lis = new YlwlManagerListener() {
            @Override
            public void onUpdateBeacon(final ArrayList<BluetoothDeviceAndRssi>
                                               beacons) {
                /**
                 * 传多个beacon过来   已经做好了排序  ，  距离      连接状态(BluetoothDeviceAndRssi
                 * isConn方法) 也随时改变
                 */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        deviceList = beacons;
                        Collections.sort(deviceList);
                        //距离排序
                        System.out.println("uuid=" + deviceList.get(0).getUuid());
                        System.out.println("蓝牙名称=" + deviceList.get(0)
                                .getBluetoothdevice().getName());
                        listRecy.setLayoutManager(new LinearLayoutManager(getApplication()));
                        myAdapter = new MyAdapter();
                        listRecy.setAdapter(myAdapter);
                    }
                });
            }

            @Override
            public void onNewBeacon(BluetoothDeviceAndRssi beacon) {
                /**
                 * 传单个beacon过来
                 */
            }

            @Override
            public void onNewBeaconDataChang(BluetoothDeviceAndRssi beacon) {
                /**
                 * 传单个beacon过来           而且是mac地址不变     距离变      连接状态变
                 */
            }
        };
        ylwlmanager.setYlwlManagerListener(lis);

        titleTv.setText("蓝牙设备");
        flAddress.addView(view);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(getApplication()).inflate(R.layout
                    .blue_list, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.blue_list_name.setText(deviceList.get(position).getBluetoothdevice().getName());
        }

        @Override
        public int getItemCount() {
            return deviceList.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView blue_list_name;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(new Intent(getApplication(), BlueManagerActivitiy
                            .class));
                    intent.putExtra("number", deviceList.get(getLayoutPosition()).getName());
                    startActivity(intent);
                }
            });
            blue_list_name = (TextView) itemView.findViewById(R.id.blue_list_name);
        }
    }
}
