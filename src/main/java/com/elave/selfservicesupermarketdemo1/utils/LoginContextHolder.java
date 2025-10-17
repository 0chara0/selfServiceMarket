package com.elave.selfservicesupermarketdemo1.utils;

public class LoginContextHolder {
    //会员用户id
    private static ThreadLocal<String> id = new ThreadLocal<String>();

    public static String getId(){
        return id.get();
    }

    public static void setUserId(String _userId){
        id.set(_userId);
    }
}
