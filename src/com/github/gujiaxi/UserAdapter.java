package com.github.gujiaxi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserAdapter extends BaseAdapter{

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_user, null);

        ImageView iv = (ImageView) convertView.findViewById(R.id.iconImg);
        TextView tv = (TextView) convertView.findViewById(R.id.showName);
        UserInfo user = userList.get(position);
        try {
            //设置图片显示
            iv.setImageDrawable(user.getUserIcon());
            //设置信息
            tv.setText(user.getUserName());

            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}