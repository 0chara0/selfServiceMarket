package com.elave.selfservicesupermarketdemo1.schedual;

import cn.hutool.extra.mail.MailUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.elave.selfservicesupermarketdemo1.model.entity.LokProduct;
import com.elave.selfservicesupermarketdemo1.model.entity.LokUser;
import com.elave.selfservicesupermarketdemo1.service.LokProductService;
import com.elave.selfservicesupermarketdemo1.service.LokUserService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryChecker {

    @Resource
    private LokProductService lokProductService;
    @Resource
    private LokUserService lokUserService;

    // 每天上午9点执行库存检查
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkInventory() {
        QueryWrapper<LokProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("product_quantity",20);
        List<LokProduct> list = lokProductService.list(queryWrapper);
        if(list.isEmpty()){
            return;
        }
        StringBuilder text = new StringBuilder("检测到以下货物缺货：");
        for ( LokProduct lokProduct :
             list) {
            text.append("货物id：").append(lokProduct.getId()).append(" ").append(lokProduct.getProductName()).append(" 剩余库存：").append(lokProduct.getProductQuantity().toString()).append("\n");
        }
        String s = text.toString();
        QueryWrapper<LokUser> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("usertype",1);
        List<LokUser> lokUsers = lokUserService.list(userQueryWrapper);
        lokUsers.forEach(lokUser -> {
            if (lokUser.getEmail() != null) {
                MailUtil.send(lokUser.getEmail(), "货物消息", s, false);
            }
        });

    }
}
