package com.ftibw.demo.utils;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import sun.security.rsa.RSAPublicKeyImpl;

import java.io.StringReader;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * @author : Ftibw
 * @date : 2019/7/6 21:25
 */
public class TokenUtils {

    /**
     * @param keyValue jwt publicKey value
     */
    public static RSAPublicKey parsePublicKey(String keyValue) {
        try {
            PemReader pemReader = new PemReader(new StringReader(keyValue));
            PemObject pem = pemReader.readPemObject();
            return new RSAPublicKeyImpl(pem.getContent());
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * to get jwt publicKey from http://auth-server/oauth/token_key
     * header:{
     * "Authorization":"basic ${headerValue}"
     * }
     *
     * @param clientId     app_id
     * @param clientSecret app_secret
     * @return headerValue of Authorization
     */
    public static String getClientBasicAuthorization(String clientId, String clientSecret) {
        return "Basic " + new String(Base64.getEncoder().encode((clientId + ":" + clientSecret).getBytes()));
    }
}
