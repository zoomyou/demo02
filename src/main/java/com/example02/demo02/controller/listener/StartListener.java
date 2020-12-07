package com.example02.demo02.controller.listener;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.sql.DataSource;
import java.sql.SQLException;

@Component
public class StartListener implements ApplicationListener<ApplicationReadyEvent>{

    private static Log log = LogFactory.get(StartListener.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        log.info("项目已启动，开始测试redis、MySQL和RabbitMQ连接，并清空redis......");

        // 1.在项目启动时先测试redis连接情况，并清空缓存
        Jedis jedis = null;
        try {
            jedis = new Jedis("http://localhost:6379");
            log.info("redis连接成功！！！");
        } catch (Exception e) {
            log.error("redis连接出错！！！");
        }
        if (ObjectUtil.isEmpty(jedis)){
            log.error("没有获取到相应的redis操作对象！！！");
            return;
        }
        try {
            log.info("redis服务器正在运行："+jedis.ping());
            jedis.flushAll();
            jedis.flushDB();
            log.info("redis清空成功！！！");
        } catch (Exception e){
            log.error("redis清空失败！！！");
        }

        // 2.测试数据库连接
        try {
            log.info("数据库连接成功，其连接为："+dataSource.getConnection());
        } catch (SQLException e) {
            log.error("数据库连接失败，出错信息为：");
            log.error("数据库状态为："+e.getSQLState());
            log.error("具体信息为："+e.getMessage());
        }

        // 3.测试RabbitMQ连接
        try {
            log.info("RabbitMQ连接成功，其连接为："+rabbitTemplate.getConnectionFactory());
        } catch (Exception e) {
            log.error("RabbitMQ连接出错，出错信息为：");
            log.error(e.getCause().getMessage());
        }

    }
}
