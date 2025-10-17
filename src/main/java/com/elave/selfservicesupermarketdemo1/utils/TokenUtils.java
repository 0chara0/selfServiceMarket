package com.elave.selfservicesupermarketdemo1.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenUtils {

    private static final long EXPIRE_TIME= 24*60*60*1000;
    public static final String TOKEN_SECRET="inuitoko";  //密钥盐



    public static String passwd;

    /**
     * 签名生成
     * @return
     */
    public static String sign(String name,int userType){

        String token = null;
        try {
            Date expiresAt = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            token = JWT.create()
                    .withIssuer("auth0").withClaim("id","id")
                    .withClaim("username", name)
                    .withClaim("userType", userType)

                    .withExpiresAt(expiresAt)
                    // 使用了HMAC256加密算法。
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
        } catch (Exception e){
            e.printStackTrace();
        }
        return token;

    }

    /**
     * 签名验证
     * @param token
     * @return
     */
    public static boolean verify(String token){

        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
            DecodedJWT jwt = verifier.verify(token);
            System.out.println("认证通过：");
            System.out.println("issuer: " + jwt.getIssuer());
            System.out.println("username: " + jwt.getClaim("username").asString());
            System.out.println("id"+jwt.getClaim("id").asString());
            System.out.println("过期时间：      " + jwt.getExpiresAt());
            LoginContextHolder.setUserId(jwt.getClaim("username").asString());
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static boolean userVerify(String token){

        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
            DecodedJWT jwt = verifier.verify(token);
            System.out.println("认证通过：");
            System.out.println("issuer: " + jwt.getIssuer());
            System.out.println("username: " + jwt.getClaim("username").asString());
            System.out.println("id"+jwt.getClaim("id").asString());
            System.out.println("过期时间：      " + jwt.getExpiresAt());
            LoginContextHolder.setUserId(jwt.getClaim("username").asString());
            if(jwt.getClaim("userType").asInt().equals(0)) {
                return true;
            }
            return false;
        } catch (Exception e){
            return false;
        }
    }

    public static boolean adminVerify(String token){

        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
            DecodedJWT jwt = verifier.verify(token);
            System.out.println("认证通过：");
            System.out.println("issuer: " + jwt.getIssuer());
            System.out.println("username: " + jwt.getClaim("username").asString());
            System.out.println("id"+jwt.getClaim("id").asString());
            System.out.println("过期时间：      " + jwt.getExpiresAt());
            LoginContextHolder.setUserId(jwt.getClaim("username").asString());
            if(jwt.getClaim("userType").asInt().equals(1)) {
                return true;
            }
            return false;
        } catch (Exception e){
            return false;
        }
    }



    public static String getId(String token){


        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
        DecodedJWT jwt = verifier.verify(token);
        String id = jwt.getClaim("userId").asString();
        return id;

    }
}