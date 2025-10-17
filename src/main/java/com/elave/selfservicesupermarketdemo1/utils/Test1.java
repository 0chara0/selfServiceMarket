package com.elave.selfservicesupermarketdemo1.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.elave.selfservicesupermarketdemo1.SelfservicesupermarketDemo1Application;
import com.elave.selfservicesupermarketdemo1.model.entity.LokOrder;
import com.elave.selfservicesupermarketdemo1.service.LokOrderService;
import com.elave.selfservicesupermarketdemo1.service.impl.LokOrderServiceImpl;
import jakarta.annotation.Resource;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;





public class Test1 {
    @Autowired
    private LokOrderService lokOrderService;
    @Test
    public void test2(){
        QueryWrapper<LokOrder> lokOrderQueryWrapper = new QueryWrapper<>();
        lokOrderQueryWrapper.eq("order_sn","1904771904827076608");
        LokOrder one = lokOrderService.getOne(lokOrderQueryWrapper);
        System.out.println(one.toString());
    }
}
