package com.example.gateway.config;

import com.example.gateway.filter.SaTokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<SaTokenFilter> saTokenFilterRegistration() {
        FilterRegistrationBean<SaTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SaTokenFilter());
        registrationBean.addUrlPatterns("/*"); // 拦截所有请求
        registrationBean.setName("saTokenFilter");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE); // 设置最高优先级
        System.out.println("✅ SaTokenFilter 注册成功，拦截所有请求");
        return registrationBean;
    }
}