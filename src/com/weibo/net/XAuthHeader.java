package com.weibo.net;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.text.TextUtils;

public class XAuthHeader extends HttpHeaderFactory {

    @Override
    public WeiboParameters generateSignatureList(WeiboParameters bundle) {
        if (bundle == null || (bundle.size() == 0)) {
            return null;
        }
        WeiboParameters pp = new WeiboParameters();
        String key = "oauth_consumer_key";
        pp.add(key, bundle.getValue(key));
        key = "oauth_nonce";
        pp.add(key, bundle.getValue(key));
        key = "oauth_signature_method";
        pp.add(key, bundle.getValue(key));
        key = "oauth_timestamp";
        pp.add(key, bundle.getValue(key));
        key = "oauth_version";
        pp.add(key, bundle.getValue(key));
        key = "source";
        pp.add(key, Weibo.getAppKey());
        key = "x_auth_mode";
        pp.add(key, "client_auth");
        key = "x_auth_password";
        pp.add(key, bundle.getValue(key));
        key = "x_auth_username";
        pp.add(key, bundle.getValue(key));
        return pp;
    }

    public String generateSignature(String data, Token token) throws WeiboException {
        byte[] byteHMAC = null;
        try {
            Mac mac = Mac.getInstance(HttpHeaderFactory.CONST_HMAC_SHA1);
            SecretKeySpec spec = null;
            String oauthSignature = encode(Weibo.getAppSecret()) + "&";
            spec = new SecretKeySpec(oauthSignature.getBytes(), HttpHeaderFactory.CONST_HMAC_SHA1);
            mac.init(spec);
            byteHMAC = mac.doFinal(data.getBytes());
        } catch (InvalidKeyException e) {
            throw new WeiboException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new WeiboException(e);
        }
        return String.valueOf(Utility.base64Encode(byteHMAC));
    }

    @Override
    public void addAdditionalParams(WeiboParameters des, WeiboParameters src) {
        if (!TextUtils.isEmpty(src.getValue("x_auth_password"))
                && !TextUtils.isEmpty(src.getValue("x_auth_username"))) {
            des.add("x_auth_password", src.getValue("x_auth_password"));
            des.add("x_auth_username", src.getValue("x_auth_username"));
            des.add("x_auth_mode", "client_auth");
        }

    }

}
