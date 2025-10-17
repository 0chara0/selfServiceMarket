package com.elave.selfservicesupermarketdemo1.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.elave.selfservicesupermarketdemo1.common.BaseResponse;
import com.elave.selfservicesupermarketdemo1.common.ErrorCode;
import com.elave.selfservicesupermarketdemo1.common.PageRequest;

import com.elave.selfservicesupermarketdemo1.common.ResultUtils;
import com.elave.selfservicesupermarketdemo1.model.dto.LokProductDTO;
import com.elave.selfservicesupermarketdemo1.model.entity.*;
import com.elave.selfservicesupermarketdemo1.service.LokOrderItemService;
import com.elave.selfservicesupermarketdemo1.service.LokOrderService;
import com.elave.selfservicesupermarketdemo1.service.LokProductService;
import com.elave.selfservicesupermarketdemo1.service.LokUserService;
import com.elave.selfservicesupermarketdemo1.utils.ImageHostUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/backend")
@Slf4j
public class AdminController {

    @Resource
    private LokOrderService lokOrderService;
    @Resource
    private LokOrderItemService lokOrderItemService;
    @Resource
    private LokProductService lokProductService;
    @Resource
    private LokUserService userService;
    @Resource
    private ImageHostUtils imageHostUtils;
    /**
     * 查看订单（成交时间、订单号、成交金额）
     * 分页查询
     */
    @PostMapping("/order")
    public BaseResponse getOrder(@RequestBody PageRequest pageRequest){
        long current = pageRequest.getCurrent();
        long size = pageRequest.getPageSize();
        Page<LokOrder> orderPage = lokOrderService.page(new Page<>(current,size));
        return ResultUtils.success(orderPage);
    }

    /**
     * 查看订单详情（订单号，成交时间，成交金额）
     * 商品信息（商品编号，商品图片，商品名称，商品品牌，商品单价，商品数量）
     */
    @PostMapping("/orderDetail")
    public BaseResponse getOrderDetail(@RequestParam String orderId){
        QueryWrapper<LokOrderItem> orderItemQueryWrapper = new QueryWrapper<>();
        orderItemQueryWrapper.eq("order_sn",orderId);
        List<LokOrderItem> list = lokOrderItemService.list(orderItemQueryWrapper);
        return ResultUtils.success(list);
    }

    /**
     * 增删改查商品库存详情（商品编号，名字，品牌，单价，剩余库存）
     */
    @PostMapping("/getProduct")
    public BaseResponse getProduct(@RequestBody PageRequest pageRequest){
        long current = pageRequest.getCurrent();
        long size = pageRequest.getPageSize();
        Page<LokProduct> productPage = lokProductService.page(new Page<>(current,size));
        return ResultUtils.success(productPage);
    }
    @PostMapping("/deleteProduct")
    public BaseResponse deleteProduct(@RequestParam String productId){
        QueryWrapper<LokProduct> productQueryWrapper = new QueryWrapper<>();
        productQueryWrapper.eq("product_sn",productId);
        lokProductService.remove(productQueryWrapper);
        return ResultUtils.success("ok");
    }
    @PostMapping("/updateProduct")
    public BaseResponse updateProduct(@RequestBody LokProduct lokProduct){
        QueryWrapper<LokProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_sn",lokProduct.getProductSn());
        lokProductService.update(lokProduct,queryWrapper);
        return ResultUtils.success("ok");
    }
    @PostMapping("/setProduct")
    public BaseResponse setProduct(@ModelAttribute LokProductDTO lokProductDTO){
        try {
            imageHostUtils.uploadFile(lokProductDTO.getProductPicFile().getResource().getFile(), lokProductDTO.getProductSn());
        } catch (IOException e) {
            e.printStackTrace();
        }
        LokProduct lokProduct = new LokProduct();
        BeanUtils.copyProperties(lokProductDTO,lokProduct);
        lokProduct.setProductPic(ImageHostUtils.PICURL + lokProduct.getProductSn());
        lokProductService.save(lokProduct);
        return ResultUtils.success("ok");
    }
    @PostMapping("/getUser")
    public BaseResponse getUser(@RequestBody PageRequest pageRequest){
        long current = pageRequest.getCurrent();
        long size = pageRequest.getPageSize();
        QueryWrapper<LokUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("usertype",0);
        Page<LokUser> userPage = userService.page(new Page<>(current,size),queryWrapper);
        return ResultUtils.success(userPage);
    }
    @PostMapping("/deleteUser")
    public BaseResponse deleteUser(@RequestParam String account){
        QueryWrapper<LokUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account",account);
        userService.remove(queryWrapper);
        return ResultUtils.success("ok");
    }
    @PostMapping("/updateUser")
    public BaseResponse updateUser(@RequestBody LokUser lokUser){
        QueryWrapper<LokUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account",lokUser.getAccount());
        userService.update(lokUser,queryWrapper);
        return ResultUtils.success("ok");
    }
    @PostMapping("/setUser")
    public  BaseResponse setUser(@RequestBody LokUser lokUser){
        userService.save(lokUser);
        return ResultUtils.success("ok");
    }
}