package com.github.gujiaxi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class AuthorizeActivity extends Activity {
	private OAuth auth;
	private String CallBackUrl = "myapp://AuthorizeActivity";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorize);
        
        View diaView=View.inflate(this, R.layout.dialog, null);
        Dialog dialog=new Dialog(AuthorizeActivity.this,R.style.dialog);
        dialog.setContentView(diaView);
        dialog.show();
        
        ImageButton startBtn=(ImageButton)diaView.findViewById(R.id.btn_start);
        startBtn.setOnClickListener(new OnClickListener(){

            public void onClick(View arg0) {
                auth=new OAuth();
                auth.RequestAccessToken(AuthorizeActivity.this, CallBackUrl);
            }
            
        });
	}
	
	protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //在这里处理获取返回的oauth_verifier参数
        UserInfo user= auth.GetAccessToken(intent);
        if(user!=null){
                    DataHelper helper=new DataHelper(this);
                    String uid=user.getUserId();
                    if(helper.HaveUserInfo(uid))
                    {
                        helper.UpdateUserInfo(user);
                        Log.e("UserInfo", "update");
                    }else
                    {
                        helper.SaveUserInfo(user);
                        Log.e("UserInfo", "add");
                    }
                }
	}
}
