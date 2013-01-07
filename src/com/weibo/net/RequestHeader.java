package com.weibo.net;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class RequestHeader extends HttpHeaderFactory {

    @Override
    public WeiboParameters generateSignatureList(WeiboParameters bundle) {
        if (bundle == null || bundle.size() == 0) {
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
        key = "oauth_token";
        pp.add(key, bundle.getValue(key));
        key = "oauth_version";
        pp.add(key, bundle.getValue(key));
        key = "source";
        pp.add(key, bundle.getValue(key));
        // key = "status";
        int postParamLocation = bundle.getLocation("source");
        for (int i = postParamLocation - 1; i < bundle.size(); i++) {
            key = bundle.getKey(i);
            pp.add(key, bundle.getValue(key));
        }
        return pp;
    }

    @Override
    public String generateSignature(String data, Token token) throws WeiboException {
        byte[] byteHMAC = null;
        try {
            Mac mac = Mac.getInstance(HttpHeaderFactory.CONST_HMAC_SHA1);
            SecretKeySpec spec = null;
            if (null == token.getSecretKeySpec()) {
                String oauthSignature = encode(Weibo.getAppSecret()) + "&"
                        + encode(token.getSecret());
                spec = new SecretKeySpec(oauthSignature.getBytes(),
                        HttpHeaderFactory.CONST_HMAC_SHA1);
                token.setSecretKeySpec(spec);
            }
            spec = token.getSecretKeySpec();
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
        // TODO Auto-generated method stub

    }

}
