package com.example02.demo02.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.HashMap;
import java.util.Map;

/**
 * token令牌工具类：
 * 1、根据用户名生成相应的令牌（sign方法）
 */
public class TokenUtil {

    private static final String TOKEN_SECRET = "ZCEQIUBFKSJBFJH2020BQWE";

    public static String sign(String username){
        try {
            //设置过期时间
            //私钥加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            //设置头部信息
            Map<String, Object> head = new HashMap<>(2);
            head.put("Type","Jwt");
            head.put("alg","HS256");
            //返回token字符串
            return JWT.create().withHeader(head).withClaim("username",username).sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String verify(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String username = jwt.getClaim("username").asString();
            return username;
        } catch (Exception e) {
            return null;
        }
    }


    public static void main(String[] args) {
        String a = "fdasfdsf,fdsafsd";
        for (int i = 0 ; i < 14 ; i++ ){
            System.out.println(a.indexOf(","));
        }
    }


}
