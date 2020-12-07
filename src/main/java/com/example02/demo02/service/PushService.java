package com.example02.demo02.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example02.demo02.bean.entity.JobMessage;
import com.example02.demo02.bean.entity.User;
import com.example02.demo02.controller.listener.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 推送服务类：
 * 将相应的验证码推送到派遣的客户端上。
 */
@Service
public class PushService {

    private Log log = LogFactory.get(PushService.class);

    @Autowired
    private JobResRedisService jobResRedisService;

    public boolean pushImgToCLient(JobMessage jobMessage, User user) throws Exception{

        boolean flag = true;
        String srcType = jobMessage.getType();
        String clientId = user.getUser_id()+"";

        JSONObject msg = new JSONObject();
        msg.put("src_type", srcType);

        if (srcType.equals("0")){
            String imgName = jobMessage.getData();
            String returnFileName = imgName.substring(0, imgName.indexOf('.'));

            msg.put("show_data", returnFileName);

            try {
                flag = WebSocketServer.sendInfo(msg, clientId);
            } catch (IOException e) {
                log.error("向客户端推送0类型验证码出错，出错信息："+e.getMessage());
                flag = false;
                jobResRedisService.setRes(jobMessage.getJob_id(), "系统出错");
            }
        } else if (srcType.equals("1")){
            msg.put("show_data", jobMessage.getData());

            try {
                flag = WebSocketServer.sendInfo(msg, clientId);
            } catch (IOException e) {
                log.error("向客户端推送1类型验证码出错，出错信息："+e.getMessage());
                flag = false;
                jobResRedisService.setRes(jobMessage.getJob_id(), "系统出错");
            }
        } else {
            msg.put("show_data", jobMessage.getData());

            try {
                flag = WebSocketServer.sendInfo(msg, clientId);
            } catch (IOException e) {
                log.error("向客户端推送2和3类型验证码出错，出错信息："+e.getMessage());
                flag = false;
                jobResRedisService.setRes(jobMessage.getJob_id(), "系统出错");
            }
        }
        return flag;

    }
}
