package com.elave.selfservicesupermarketdemo1.controller;

import com.alipay.api.internal.util.AlipaySignature;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.elave.selfservicesupermarketdemo1.common.BaseResponse;
import com.elave.selfservicesupermarketdemo1.common.ResultUtils;
import com.elave.selfservicesupermarketdemo1.configuration.AliPayConfigInfo;
import com.elave.selfservicesupermarketdemo1.mapper.LokOrderMapper;
import com.elave.selfservicesupermarketdemo1.model.entity.LokOrder;
import com.elave.selfservicesupermarketdemo1.model.entity.LokOrderItem;
import com.elave.selfservicesupermarketdemo1.model.entity.LokProduct;
import com.elave.selfservicesupermarketdemo1.model.vo.OrderListVO;
import com.elave.selfservicesupermarketdemo1.service.LokOrderItemService;
import com.elave.selfservicesupermarketdemo1.service.LokOrderService;
import com.elave.selfservicesupermarketdemo1.service.LokProductService;
import com.elave.selfservicesupermarketdemo1.utils.OrderContextHolder;
import com.elave.selfservicesupermarketdemo1.websocket.WebSocketServer;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@Slf4j
public class PayController {
 
    @Resource
    private AliPayConfigInfo config;
    @Resource
    private LokOrderService lokOrderService;
    @Resource
    private LokOrderMapper lokOrderMapper;
    @Resource
    private LokOrderItemService lokOrderItemService;
    @Resource
    private LokProductService lokProductService;


    @GetMapping("/test")
    public BaseResponse<String> test(){
        return ResultUtils.success("hello");
    }
    /**
     * 给支付宝的回调接口
     */
    @PostMapping("/notify")
    public void notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> params = new HashMap<>();
        //获取支付宝POST过来反馈信息，将异步通知中收到的待验证所有参数都存放到map中
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (String name : parameterMap.keySet()) {
            String[] values = parameterMap.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决
            valueStr = new String(valueStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            params.put(name, valueStr);
        }
        //验签



        Boolean signResult = AlipaySignature.rsaCheckV1(params,config.getAlipayPublicKey(),"utf-8",config.getSignType());


        if (true) {
            log.info("收到支付宝发送的支付结果通知");
            String out_trade_no = request.getParameter("out_trade_no");
            log.info("交易流水号：{}", out_trade_no);
            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            //交易成功
            switch (trade_status) {
                case "TRADE_SUCCESS":
                    //支付成功的业务逻辑，比如落库，开vip权限等
                    String userId = new String(request.getParameter("subject").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    String buyerId = new String(request.getParameter("buyer_id").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    String orderId = new String(request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    String total = new String(request.getParameter("total_amount").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    OrderListVO orderListVO = OrderContextHolder.processedOrderList.get(orderId);

                    log.info("订单号：" + orderId);



                    UpdateWrapper<LokOrder> orderUpdateWrapper = new UpdateWrapper<>();
                    orderUpdateWrapper.eq("order_sn",orderId);
                    orderUpdateWrapper.set("pay_amount",new BigDecimal(total));
                    orderUpdateWrapper.set("payment_time",new Date(System.currentTimeMillis()));
                    orderUpdateWrapper.set("pay_type",1);
                    orderUpdateWrapper.set("status",3);

                    lokOrderService.update(orderUpdateWrapper);

                    QueryWrapper<LokOrderItem> orderItemQueryWrapper = new QueryWrapper<>();
                    orderItemQueryWrapper.eq("order_sn",orderId);
                    List<LokOrderItem> lokOrderItemList = lokOrderItemService.list(orderItemQueryWrapper);
                    lokOrderItemList.forEach(lokOrderItem -> {

                        UpdateWrapper<LokProduct> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.eq("id",lokOrderItem.getProductId());
                        updateWrapper.setDecrBy("product_quantity",lokOrderItem.getProductQuantity());
                        lokProductService.update(updateWrapper);
                    });


                    WebSocketServer.sendMessage(userId,"已支付");

                    log.info("订单：{} 交易成功", out_trade_no);
                    break;
                case "TRADE_FINISHED":
                    log.info("交易结束，不可退款");
                    //其余业务逻辑
                    break;
                case "TRADE_CLOSED":
                    log.info("超时未支付，交易已关闭，或支付完成后全额退款");
                    //其余业务逻辑
                    break;
                case "WAIT_BUYER_PAY":
                    log.info("交易创建，等待买家付款");
                    //其余业务逻辑
                    break;
            }
            response.getWriter().write("success");   //返回success给支付宝，表示消息我已收到，不用重调
 
        } else {
            response.getWriter().write("fail");   ///返回fail给支付宝，表示消息我没收到，请重试
        }
    }
}