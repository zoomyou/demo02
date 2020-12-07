package com.example02.demo02.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example02.demo02.bean.entity.Job;
import com.example02.demo02.mapper.JobMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 任务结果服务类：
 * 1、将任务结果返回redis中供recognition接口查询；
 * 2、从redis中删除任务结果；
 * 3、从redis中查询任务结果。
 */
@Service
public class JobRedisService {

    private Log log = LogFactory.get(JobRedisService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JobMapper jobMapper;

    public boolean addJobToRedis(Job job){
        boolean res = false;

        String key = "jobObject"+job.getJob_id();
        ValueOperations operations = redisTemplate.opsForValue();

        String toJson = new Gson().toJson(job);

        if (redisTemplate.hasKey(key)){
            redisTemplate.delete(key);
            operations.set(key, toJson, 60, TimeUnit.SECONDS);
            res = true;
        } else {
            operations.set(key, toJson, 60, TimeUnit.SECONDS);
            res = true;
        }

        return res;
    }

    public void deleteJobFromRedis(Job job){

        String key = "jobObject"+job.getJob_id();
        ValueOperations operations = redisTemplate.opsForValue();

        if (redisTemplate.hasKey(key)){
            redisTemplate.delete(key);
        }
    }

    public Job getJobFromRedis(String id){
        Job job = null;

        String key = "jobObject"+id;
        ValueOperations operations = redisTemplate.opsForValue();

        if (redisTemplate.hasKey(key)){
            String fromJson = (String) operations.get(key);
            job = new Gson().fromJson(fromJson, Job.class);
        } else {
            try {
                job = jobMapper.findById(id);
            } catch (Exception e) {
                log.error("从数据库中查询任务记录出错！出错信息："+e.getMessage());
                job = null;
            }
        }

        return job;
    }

}
