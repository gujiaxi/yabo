package com.weibo.net;

import java.io.IOException;

import android.content.Context;

public class AsyncWeiboRunner {
	
	private Weibo mWeibo;
	
	public AsyncWeiboRunner(Weibo weibo){
		this.mWeibo = weibo;
	}
	
	public void request(final Context context, 
			final String url, 
			final WeiboParameters params, 
			final String httpMethod, 
			final RequestListener listener){
		new Thread(){
			@Override public void run() {
                try {
					String resp = mWeibo.request(context, url, params, httpMethod, mWeibo.getAccessToken());
                    listener.onComplete(resp);
                } catch (WeiboException e) {
                    listener.onError(e);
                }
            }
		}.run();
		
	}
	
	
    public static interface RequestListener {

        public void onComplete(String response);

        public void onIOException(IOException e);

        public void onError(WeiboException e);

    }
	
}
