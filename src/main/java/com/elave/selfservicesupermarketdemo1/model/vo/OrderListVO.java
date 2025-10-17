package com.elave.selfservicesupermarketdemo1.model.vo;

import com.elave.selfservicesupermarketdemo1.model.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OrderListVO implements Serializable {
    /**
     * 商品列表
     */
    public ArrayList<OrderItem> orderList;
    /**
     * 订单总价
     */
    public BigDecimal totalPrice;
    /**
     * 订单号
     */
    public String orderSn;
}
