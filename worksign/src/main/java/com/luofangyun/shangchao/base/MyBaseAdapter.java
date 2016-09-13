package com.luofangyun.shangchao.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * ListView和GridView父类
 */
public class MyBaseAdapter extends BaseAdapter {

    //private Context context;
    private String[] names;

    public MyBaseAdapter(String[] name) {
        //this.context = context;
        this.names = name;
    }
    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Object getItem(int position) {
        return getItemId(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
