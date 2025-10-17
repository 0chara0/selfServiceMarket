package com.elave.selfservicesupermarketdemo1.utils;

import cn.hutool.core.map.BiMap;

import java.util.HashMap;
import java.util.Map;

public class ConvertStrAndMapUtils {
    public static BiMap<Integer,Integer> mapStringToMap(String str){
        str = str.substring(1, str.length()-1);
        String[] strs = str.split(",");
        BiMap<Integer,Integer> map = new BiMap<>(new HashMap<Integer, Integer>());
        for (String string : strs) {
            String key = string.split("=")[0];
            String value = string.split("=")[1];
            // 去掉头部空格
            Integer key1 = Integer.parseInt(key.trim());
            Integer value1 = Integer.parseInt(value.trim());
            map.put(key1, value1);
        }
        return map;
    }

}
