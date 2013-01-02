package com.github.gujiaxi;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//获取账号列表
        DataHelper dbHelper=new DataHelper(this);
        List<UserInfo> userList= dbHelper.GetUserList(true);
        if(userList.isEmpty())//如果为空说明第一次使用跳到AuthorizeActivity页面进行OAuth认证
        {
               Intent intent = new Intent();
               intent.setClass(MainActivity.this, AuthorizeActivity.class);
               startActivity(intent);
        }
        else//如果不为空读取这些记录的UserID号、Access Token、Access Secret值
            //然后根据这3个值调用新浪的接口获取这些记录对应的用户昵称和用户头像图标等信息。
        {
        	for(UserInfo user:userList) {
            //to-do
        	}
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
