package com.weibo.net;

public class AccessToken extends Token {
	
	public AccessToken(String rlt){
		super(rlt);
	}
	
	public AccessToken(String token , String secret){
		super(token, secret);
	}
}