package com.weibo.net;

import org.json.JSONException;
import org.json.JSONObject;

public class Oauth2AccessToken extends Token {
	
	public Oauth2AccessToken(String rltString){
	 // { "access_token":"SlAV32hkKG", "expires_in":3600, "refresh_token":"8xLOxBtZp8" } 
	    if(rltString != null){
	        if(rltString.indexOf("{") >= 0){
	            try {
	                JSONObject json = new JSONObject(rltString);
	                setToken(json.optString("access_token"));
	                setExpiresIn(json.optInt("expires_in"));
	                setRefreshToken(json.optString("refresh_token"));
	            } catch (JSONException e) {
	            }
	        }
	    }
	}
	
	public Oauth2AccessToken(String token , String secret){
		super(token, secret);
	}
}