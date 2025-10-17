package com.elave.selfservicesupermarketdemo1.utils;

import com.elave.selfservicesupermarketdemo1.model.vo.OrderListVO;

import java.util.HashMap;
import java.util.Map;

public class OrderContextHolder {
    public static Map<String,Map<Integer,Integer>> orderList;
    public static Map<String, OrderListVO> processedOrderList = new HashMap<>();
}
