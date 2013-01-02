package com.github.gujiaxi;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginActivity extends Activity {
	private DataHelper dbHelper;
	private List<UserInfo> userList;
	private View diaView;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        LinearLayout layout=(LinearLayout)findViewById(R.id.layout);
        //背景自动适应
        AndroidHelper.AutoBackground(this, layout, R.drawable.ic_launcher, R.drawable.ic_launcher);
        
        ImageButton iconSelectBtn=(ImageButton)findViewById(R.id.iconSelectBtn);
        iconSelectBtn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                diaView=View.inflate(LoginActivity.this, R.layout.dialog2, null);
                Dialog dialog=new Dialog(LoginActivity.this,R.style.dialog2);
                dialog.setContentView(diaView);
                dialog.show();
            }
            
        });
        
        ImageButton login=(ImageButton)findViewById(R.id.login);
        login.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                GoHome();
            }
            
        });
	}
	
	private void GoHome(){
        if(userList!=null)
        {
            String name = iconSelect.getText().toString();
            UserInfo u = GetUserByName(name);
            if(u!=null)
            {
                ConfigHelper.nowUser=u;//获取当前选择的用户并且保存
            }
        }
        if(ConfigHelper.nowUser!=null)
        {
                        //进入用户首页
            Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
        }
    }
	
	private void initUser(){
        //获取账号列表
        dbHelper=new DataHelper(this);
        userList = dbHelper.GetUserList(false);
        if(userList.isEmpty()) {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, AuthorizeActivity.class);
            startActivity(intent);
        }
        else {
            SharedPreferences preferences = getSharedPreferences(Select_Name, Activity.MODE_PRIVATE);
            String str= preferences.getString("name", "");
            UserInfo user=null;
            if(str!="") {
                user=GetUserByName(str);
            }
            if(user==null) {
                user=userList.get(0);
            }
            icon.setImageDrawable(user.getUserIcon());
            iconSelect.setText(user.getUserName());
        }
    }
	
	ImageButton iconSelectBtn=(ImageButton)findViewById(R.id.iconSelectBtn);
	iconSelectBtn.setOnClickListener(new OnClickListener(){
	            @Override
	            public void onClick(View v) {
	                dialog.show();
	                
	                UserAdapter adapater = new UserAdapter();
	                ListView listview=(ListView)diaView.findViewById(R.id.list);
	                listview.setVerticalScrollBarEnabled(false);// ListView去掉下拉条
	                listview.setAdapter(adapater);
	                listview.setOnItemClickListener(new OnItemClickListener(){
	                    @Override
	                    public void onItemClick(AdapterView<?> arg0, View view,int arg2, long arg3) {
	                        TextView tv=(TextView)view.findViewById(R.id.showName);
	                        iconSelect.setText(tv.getText());
	                        ImageView iv=(ImageView)view.findViewById(R.id.iconImg);
	                        icon.setImageDrawable(iv.getDrawable());
	                        dialog.dismiss();
	                    }
	                    
	                });
	            }
	            
	        });
	@Override
    protected void onStop() {
        //获得SharedPreferences对象
        SharedPreferences MyPreferences = getSharedPreferences(Select_Name, Activity.MODE_PRIVATE);
        //获得SharedPreferences.Editor对象
        SharedPreferences.Editor editor = MyPreferences.edit();
        //保存组件中的值
        editor.putString("name", iconSelect.getText().toString());
        editor.commit();
        super.onStop();
    }
}
