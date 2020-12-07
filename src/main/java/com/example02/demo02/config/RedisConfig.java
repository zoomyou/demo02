package com.example02.demo02.config;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis配置类
 */
//@EnableWebMvc
@Configuration
@EnableAutoConfiguration
public class RedisConfig {

    // 将redis的配置属性注入容器
    @Bean
    @ConfigurationProperties(prefix = "spring.redis.pool")
    public JedisPoolConfig getRedisConfig(){
        JedisPoolConfig config = new JedisPoolConfig();
        return config;
    }

    // 将redis的连接对象注入容器
    @Bean
    @ConfigurationProperties(prefix = "spring.redis")
    public JedisConnectionFactory getConnectionFactory(){
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setUsePool(true);
        JedisPoolConfig config = getRedisConfig();
        factory.setPoolConfig(config);
        return factory;
    }

    // 将redis的操作对象注入容器，以供服务层和控制层使用
    @Bean
    public RedisTemplate<?, ?> getRedisTemplate(){
        JedisConnectionFactory factory = getConnectionFactory();
        RedisTemplate<?, ?> template = new StringRedisTemplate(factory);
        return template;
    }
}
