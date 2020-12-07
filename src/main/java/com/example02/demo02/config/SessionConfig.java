package com.example02.demo02.config;


import com.example02.demo02.controller.listener.SessionListener;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *session监听器配置类
 */
@Configuration
public class SessionConfig extends WebMvcConfigurerAdapter{

    // 将session的监听对象注入容器
    @Bean
    public ServletListenerRegistrationBean<SessionListener> servletListenerRegistrationBean(){
        ServletListenerRegistrationBean<SessionListener> slrBean = new ServletListenerRegistrationBean<>();
        slrBean.setListener(new SessionListener());
        return slrBean;
    }
}
