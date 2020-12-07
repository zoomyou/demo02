package com.example02.demo02.bean;


import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example02.demo02.bean.entity.JobMessage;
import com.example02.demo02.service.JobResRedisService;
import com.example02.demo02.service.ThreadService;
import com.google.gson.Gson;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * 消息队列的消费者；
 * 会寻找空闲用户，
 * 并将相应的打码任务推送给相应的客户端。
 */
@Component
public class Consumer01 {

    private static Log log = LogFactory.get(Consumer01.class);

    @Autowired
    private ThreadService threadService;

    @Autowired
    private JobResRedisService jobResRedisService;

    /**
     * 消费者消费消息的方法
     * @param message 从队列中接收到的消息对象
     */
    @RabbitHandler
    @RabbitListener(queues = "jobMessage_queue_01")
    public void jobMessageProcess01(Message message){

        // 1.将获取的message对象转化为byte数组，再将byte数组转化为字符串
        byte[] msgByte = message.getBody();
        String strMsg = new String(msgByte);

        // 2.将序列化的字符串转化为所需要的对象
        JobMessage jobMessage = new Gson().fromJson(strMsg, JobMessage.class);
        log.info("消息"+jobMessage.getJob_id()+"出队");

        // 3.利用多线程的服务进行任务调度
        try {
            threadService.schedule(jobMessage);
        } catch (Exception e) {
            // 如果出错往任务结果返回区内返回出错信息
            log.error("新开线程出错！");
            jobResRedisService.setRes(jobMessage.getJob_id(), "系统出错");
        }

    }
}
