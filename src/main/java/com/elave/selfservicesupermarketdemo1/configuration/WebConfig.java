package com.elave.selfservicesupermarketdemo1.configuration;

import com.elave.selfservicesupermarketdemo1.interceptor.UserInterceptor;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Resource
    private UserInterceptor userInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(loginInterceptor)
//                .addPathPatterns("/**")   //默认对所有请求进行拦截
//                .excludePathPatterns("/login/**","/static/**","/backend/**","/register");
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/cashier/**")
                .excludePathPatterns("/cashier/login");
    }


}