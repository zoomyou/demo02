package com.example02.demo02.bean;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example02.demo02.bean.entity.JobMessage;
import com.google.gson.Gson;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息队列的生产者，
 * 在recognition接口被调用，
 * 并将任务数据和产生的唯一id一同封装入JobMessage类中发送到消息队列中。
 */
@Component
public class Producer01 {

    private Log log = LogFactory.get(Producer01.class);

    // 注入用于操作RabbitMQ的对象
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 生产者生产消息的方法
     * @param jobMessage 消息实体对象
     * @return true代表发送成功，false代表发送失败
     */
    public boolean produce(JobMessage jobMessage){

        boolean status = false;

        // 将任务对象放入到消息队列中，并为每条消息设置自己的TTL
        log.info("开始将任务 "+jobMessage.getJob_id()+" 放入消息队列！");

        // 1.先将任务对象序列化为字符串
        String toJson = new Gson().toJson(jobMessage);

        // 2.再将该字符串转化为字节数组
        byte[] msgBytes = toJson.getBytes();

        // 3.设置该消息的TTL
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setExpiration("59");

        // 4.将上述的消息内容和消息属性共同打包成一个消息队列中的消息对象
        Message message = new Message(msgBytes, messageProperties);

        // 5.进行消息发送
        try {
            // 向rabbitMQ中指定的交换机和绑定的路由键中发送消息（在RabbitMQConfig中配置）
            rabbitTemplate.convertAndSend("jobMessage_exchange_01", "jobMessage_routing_key_01", message);
            status = true;
            log.info("任务已经入消息队列！");
        } catch (Exception e) {
            log.error("任务未进入消息队列！出错信息："+e.getMessage());
        }

        return status;
    }
}
