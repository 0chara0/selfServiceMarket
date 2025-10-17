package com.elave.selfservicesupermarketdemo1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.elave.selfservicesupermarketdemo1.model.entity.LokUser;
import com.elave.selfservicesupermarketdemo1.service.LokUserService;
import com.elave.selfservicesupermarketdemo1.mapper.LokUserMapper;
import org.springframework.stereotype.Service;

/**
* @author sanbing
* @description 针对表【lok_user】的数据库操作Service实现
* @createDate 2025-03-20 22:09:20
*/
@Service
public class LokUserServiceImpl extends ServiceImpl<LokUserMapper, LokUser>
    implements LokUserService{

}




