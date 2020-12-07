package com.example02.demo02.controller;


import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 心跳控制器：
 * 负责返回服务器的工作状态（如果正常运行则会返回信息，否则不会返回任何信息）
 */
@RestController
public class HeartBeatController {

    // 查看系统运行的状态
    @GetMapping("/heartbeat")
    public JSONObject heartbeat(){
        JSONObject res = new JSONObject();
        res.put("code", 200);
        res.put("message", "系统运行正常！");
        res.put("data", "");

        return res;
    }
}
