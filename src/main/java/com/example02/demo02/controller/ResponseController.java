package com.example02.demo02.controller;


import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example02.demo02.bean.entity.Job;
import com.example02.demo02.bean.entity.User;
import com.example02.demo02.mapper.JobMapper;
import com.example02.demo02.service.JobRedisService;
import com.example02.demo02.service.JobService;
import com.example02.demo02.service.UserRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 打码结果反馈控制器：
 * 负责打码结果的反馈以及打码客户端的数据查询计算。
 */
@RestController
public class ResponseController {

    private Log log = LogFactory.get(ResponseController.class);

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private JobRedisService jobRedisService;

    @Autowired
    private JobService jobService;

    @Autowired
    private UserRedisService userRedisService;

    @GetMapping("/response")
    public JSONObject response(String job_id, String response_code){
        JSONObject res = new JSONObject();
        String status = "202", message = "反馈失败";
        Job job = null;

        try {
            jobMapper.response(response_code, job_id);
            status = "200";
            message = "反馈成功";
        } catch (Exception e) {
            log.error("反馈失败");
        }

        try {
            job = jobRedisService.getJobFromRedis(job_id);
            long cost = job.getFinish_time().getTime() - job.getReceive_time().getTime();
            double accuracy = jobService.currAccAll(job.getReceiver_id());
            User user = userRedisService.getUserFromRedis(job.getReceiver_id());
            double mark = jobService.mark(user, accuracy, response_code, cost);
            res.put("curr_acc", accuracy);
            res.put("speed", cost);
            res.put("mark", mark);
        } catch (Exception e) {
            res.put("curr_acc", 0.0);
            res.put("speed", 0);
            res.put("mark", 0.0);
            message = message + " 准确率、速度和分数获取失败！";
        }

        res.put("status", status);
        res.put("message", message);

        return res;
    }
}
