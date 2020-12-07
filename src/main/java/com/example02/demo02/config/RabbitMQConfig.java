package com.example02.demo02.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ的配置类，
 * 可以配置相应的队列、交换机和绑定。
 */
@Configuration
public class RabbitMQConfig {

    // 创建一个任务消息队列，消息队列的名称为：jobMessage_queue_01
    @Bean
    public Queue jobMessageQueue01(){
        // 第一个参数是创建的queue的名字，第二个参数是是否支持持久化
        return new Queue("jobMessage_queue_01", true);
    }

    // 为上面配置的任务队列配置direct交换机，交换机名称为：jobMessage_exchange_01
    @Bean
    public DirectExchange jobMessageExchange01(){
        // 一共有三种构造方法，可以只传exchange的名字， 第二种，可以传exchange名字，是否支持持久化，是否可以自动删除，
        // 第三种在第二种参数上可以增加Map，Map中可以存放自定义exchange中的参数
        return new DirectExchange("jobMessage_exchange_01", true, false);
    }

    // 将配置的消息队列和交换机绑定在一起，绑定键为：jobMessage_routing_key_01
    @Bean
    public Binding jobMessageBinding(){
        return BindingBuilder.bind(jobMessageQueue01()).to(jobMessageExchange01()).with("jobMessage_routing_key_01");
    }
}
