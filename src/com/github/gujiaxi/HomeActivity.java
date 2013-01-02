package com.github.gujiaxi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class HomeActivity extends Activity{
	private UserInfo user;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        loadList();
	}
	
	private void loadList(){
        if(ConfigHelper.nowUser==null)
        {
            
        }
        else
        {
            user = ConfigHelper.nowUser;
            //显示当前用户名称
            TextView showName=(TextView)findViewById(R.id.showName);
            showName.setText(user.getUserName());
            
            OAuth auth=new OAuth();
            String url = "http://api.t.sina.com.cn/statuses/friends_timeline.json";
            List params=new ArrayList();
            params.add(new BasicNameValuePair("source", auth.consumerKey)); 
            HttpResponse response =auth.SignRequest(user.getToken(), user.getTokenSecret(), url, params);
            if (200 == response.getStatusLine().getStatusCode()){
                try {
                    InputStream is = response.getEntity().getContent();
                    Reader reader = new BufferedReader(new InputStreamReader(is), 4000);
                    StringBuilder buffer = new StringBuilder((int) response.getEntity().getContentLength());
                    try {
                        char[] tmp = new char[1024];
                        int l;
                        while ((l = reader.read(tmp)) != -1) {
                            buffer.append(tmp, 0, l);
                        }
                    } finally {
                        reader.close();
                    }
                    String string = buffer.toString();
                    response.getEntity().consumeContent();
                    JSONArray data=new JSONArray(string);
                    for(int i=0;i<data.length();i++)
                    {
                        JSONObject d=data.getJSONObject(i);
                        if(d!=null){
                            JSONObject u=d.getJSONObject("user");
                            if(d.has("retweeted_status")){
                                JSONObject r=d.getJSONObject("retweeted_status");
                            }
                            
                            //微博id
                            String id=d.getString("id");
                            String userId=u.getString("id");
                            String userName=u.getString("screen_name");
                            String userIcon=u.getString("profile_image_url");
                            Log.e("userIcon", userIcon);
                            String time=d.getString("created_at");
                            String text=d.getString("text");
                            Boolean haveImg=false;
                            if(d.has("thumbnail_pic")){
                                haveImg=true;
                            }
                            
                            Date date=new Date(time);
                            time=ConvertTime(date);
                            if(wbList==null){
                                wbList=new ArrayList<WeiBoInfo>();
                            }
                            WeiBoInfo w=new WeiBoInfo();
                            w.setId(id);
                            w.setUserId(userId);
                            w.setUserName(userName);
                            w.setTime(time);
                            w.setText(text);
                            
                            w.setHaveImage(haveImg);
                            w.setUserIcon(userIcon);
                            wbList.add(w);
                        }
                    }
                    
                }catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } 
            }
            
            if(wbList!=null)
            {
                WeiBoAdapater adapater = new WeiBoAdapater();
                ListView Msglist=(ListView)findViewById(R.id.Msglist);
                Msglist.setOnItemClickListener(new OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,int arg2, long arg3) {
                        Object obj=view.getTag();
                        if(obj!=null){
                            String id=obj.toString();
                            Intent intent = new Intent(HomeActivity.this,ViewActivity.class);
                            Bundle b=new Bundle();
                            b.putString("key", id);
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    }
                    
                });
                Msglist.setAdapter(adapater);
            }
        }
        loadingLayout.setVisibility(View.GONE);
    }
}