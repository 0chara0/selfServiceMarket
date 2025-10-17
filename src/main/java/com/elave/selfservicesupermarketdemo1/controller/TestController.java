package com.elave.selfservicesupermarketdemo1.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.BiMap;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeCancelModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.elave.selfservicesupermarketdemo1.common.BaseResponse;
import com.elave.selfservicesupermarketdemo1.common.ErrorCode;
import com.elave.selfservicesupermarketdemo1.common.ResultUtils;
import com.elave.selfservicesupermarketdemo1.configuration.AliPayConfigInfo;
import com.elave.selfservicesupermarketdemo1.model.entity.*;
import com.elave.selfservicesupermarketdemo1.model.vo.OrderListVO;
import com.elave.selfservicesupermarketdemo1.service.LokOrderItemService;
import com.elave.selfservicesupermarketdemo1.service.LokOrderService;
import com.elave.selfservicesupermarketdemo1.service.LokProductService;
import com.elave.selfservicesupermarketdemo1.service.LokUserService;
import com.elave.selfservicesupermarketdemo1.utils.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/test")
public class TestController {
    @Resource
    private LokOrderService lokOrderService;
    @Resource
    private LokOrderItemService lokOrderItemService;
    @GetMapping("/test")
    public void test(){
        QueryWrapper<LokOrder> lokOrderQueryWrapper = new QueryWrapper<>();
        QueryWrapper<LokOrderItem> lokOrderItemQueryWrapper = new QueryWrapper<>();
        lokOrderItemQueryWrapper.eq("id",5);
        LokOrderItem one1 = lokOrderItemService.getOne(lokOrderItemQueryWrapper);
        lokOrderQueryWrapper.eq("order_sn","1904771904827076608");
        LokOrder one = lokOrderService.getOne(lokOrderQueryWrapper);
        System.out.println(one.toString());
    }
    @PostMapping("/testBase64")
    public String test1(@RequestParam MultipartFile file) throws Exception{
        File tempFile = File.createTempFile("temp_", ".png");
        FileCopyUtils.copy(file.getBytes(),tempFile);
        byte[] bytes = FileUtil.readBytes(tempFile);
        String s = Base64.getEncoder().encodeToString(bytes);

        System.out.println(s);

        tempFile.delete();
        HttpResponse message = HttpRequest.post("http://localhost:5000/test1")
                .form("message", "1")
                .execute();
        return s;

    }

    @Autowired
    private LokProductService lokProductService;
    @Autowired
    private AliPayConfigInfo config;
    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private LokUserService lokUserService;

    @PostMapping("/login")
    public BaseResponse login(@RequestParam String account, @RequestParam String passwd){
        QueryWrapper<LokUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account",account);
        LokUser user = lokUserService.getOne(queryWrapper);
        if(user == null){
            return ResultUtils.error(503,"用户不存在");
        }
        if(!user.getPasswd().equals(MD5Utils.MD5(passwd))){
            return ResultUtils.error(403,"错误的密码");
        }
        String sign = TokenUtils.sign(account, user.getUsertype());
        return ResultUtils.success(sign);
    }

    @PostMapping("/getOrder")
    public BaseResponse getOrder(@RequestParam MultipartFile file) throws Exception{
        //在进行之前查询这台机器之前是否还有订单 有则删除

        String id = LoginContextHolder.getId();
        File file1 = new File("E:\\code\\test1\\test.png");
        FileUtil.writeBytes(file.getBytes(),file1);

        BiMap<Integer, Integer> map = ConvertStrAndMapUtils.mapStringToMap("{29=1, 110=1}");

        BigDecimal totalPrice = new BigDecimal("0.00");
        ArrayList<OrderItem> orderlist = new ArrayList<>();
        if(map!=null && !map.isEmpty()){
            /**
             * map.entrySet()是把HashMap类型的数据转换成集合类型
             * map.entrySet().iterator()是去获得这个集合的迭代器，保存在iterator里面。
             */
            Iterator<Map.Entry<Integer, Integer>> iterator = map.entrySet().iterator();
            for(int i=0;i<map.size();i++){
                Map.Entry<Integer, Integer> entry = iterator.next();
                Integer key = entry.getKey();
                Integer value = entry.getValue();
                LokProduct product = lokProductService.getById(key);
                OrderItem build = OrderItem.builder().id(key.longValue())
                        .productBrand(product.getProductBrand())
                        .productAmount(value)
                        .productName(product.getProductName())
                        .productPic(product.getProductPic())
                        .productPrice(product.getProductPrice())
                        .productSn(product.getProductSn())
                        .build();
                orderlist.add(build);
                totalPrice = totalPrice.add(product.getProductPrice().multiply(BigDecimal.valueOf(value)));
            }
        }
        totalPrice = totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
        OrderListVO orderListVO = OrderListVO.builder()
                .orderList(orderlist)
                .totalPrice(totalPrice)
                .orderSn(((Long) IdUtil.getSnowflakeNextId()).toString())
                .build();
        OrderContextHolder.processedOrderList.put(id,orderListVO);
        return ResultUtils.success(orderListVO);
    }

    @PostMapping("/withdrawOrder")
    public BaseResponse withdrawOrder(){
        String id = LoginContextHolder.getId();

        OrderContextHolder.processedOrderList.remove(id);
        return ResultUtils.success("ok");
    }

    @PostMapping("/getpay")
    public BaseResponse getPay(HttpServletResponse response) {
        String id = LoginContextHolder.getId();

        OrderListVO orderListVO = OrderContextHolder.processedOrderList.get(id);
        LokOrder lokOrder = new LokOrder();
        String orderSn = orderListVO.getOrderSn();
        lokOrder.setOrderSn(orderSn);
        lokOrder.setTotalAmount(orderListVO.totalPrice);
        lokOrder.setCreateTime(new Date(System.currentTimeMillis()));
        lokOrder.setOrderType(0);
        lokOrder.setStatus(0);
        lokOrder.setSourceType(0);
        lokOrder.setPayType(0);




        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi-sandbox.dl.alipaydev.com/gateway.do",config.getAppId(), config.getMerchantPrivateKey(),"JSON","utf-8", config.getAlipayPublicKey(), config.getSignType());
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setOutTradeNo(orderSn);
        model.setTotalAmount(orderListVO.totalPrice.toString());
        model.setSubject(id);
        request.setBizModel(model);
        request.setNotifyUrl(config.getNotifyUrl());
        AlipayTradePrecreateResponse alipayResponse = null;
        try {
            alipayResponse = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }
        String qrCode = alipayResponse.getQrCode();

        lokOrderService.save(lokOrder);
        orderListVO.orderList.forEach(orderItem -> {
            LokOrderItem lokOrderItem = new LokOrderItem();
            lokOrderItem.setOrderSn(orderSn);
            lokOrderItem.setOrderId(lokOrder.getId());
            lokOrderItem.setProductBrand(orderItem.getProductBrand());
            lokOrderItem.setProductId(orderItem.getId());
            lokOrderItem.setProductName(orderItem.getProductName());
            lokOrderItem.setProductPic(orderItem.getProductPic());
            lokOrderItem.setProductPrice(orderItem.getProductPrice());
            lokOrderItem.setProductQuantity(orderItem.getProductAmount());
            lokOrderItem.setProductSn(orderItem.getProductSn());
            lokOrderItemService.save(lokOrderItem);
        });

        try {
            QrCodeUtil.generate(qrCode, 300, 300, "png", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultUtils.success("ok");
    }

    @PostMapping("/withdrawPay")
    public BaseResponse withdrawPay() throws Exception {
        String id = LoginContextHolder.getId();
        OrderListVO orderListVO = OrderContextHolder.processedOrderList.get(id);
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        AlipayTradeCancelModel model = new AlipayTradeCancelModel();
        model.setOutTradeNo(orderListVO.orderSn);
        request.setBizModel(model);

        AlipayTradeCancelResponse response = alipayClient.execute(request);
        System.out.println(response.getBody());
        QueryWrapper<LokOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_sn",orderListVO.orderSn);
        lokOrderService.remove(queryWrapper);
        if (response.isSuccess()) {
            System.out.println("调用成功");

        } else {
            System.out.println("调用失败");
        }
        OrderContextHolder.processedOrderList.remove(id);
        return ResultUtils.success("success");
    }
}
