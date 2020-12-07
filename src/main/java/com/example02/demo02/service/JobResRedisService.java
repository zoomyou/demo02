package com.example02.demo02.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Service
public class JobResRedisService {

    private static HashMap<String, String> jobIdUserId = new HashMap<>();
    private static HashMap<String, String> userIdJobId = new HashMap<>();

    @Autowired
    private RedisTemplate redisTemplate;

    // 任务结果缓存
    public String getRes(String id){
        String res = null;

        //
        String key = "jobId"+id;
        ValueOperations operations = redisTemplate.opsForValue();

        if (redisTemplate.hasKey(key)){
            res = (String) operations.get(key);
        }

        return res;
    }
    public boolean setRes(String id, String res){
        boolean flag = false;

        String key = "jobId"+id;
        ValueOperations operations = redisTemplate.opsForValue();

        if (redisTemplate.hasKey(key)){
            redisTemplate.delete(key);

            operations.set(key, res, 30, TimeUnit.SECONDS);
            flag = true;
        } else {
            operations.set(key, res, 30, TimeUnit.SECONDS);
            flag = true;
        }

        return flag;
    }
    public void delRes(String id){

        String key = "jobId"+id;

        if (redisTemplate.hasKey(key)){
            redisTemplate.delete(key);
        }

    }


    // 任务id和用户id映射
    public String getUserId(String id){
        String res = null;

        if (id!=null || !id.isEmpty()){
            res = jobIdUserId.get(id);
        }

        return res;
    }
    public boolean setUserId(String id, String user){
        boolean res = false;

        if ((id!=null && !id.isEmpty()) && (user!=null && !user.isEmpty())){
            jobIdUserId.put(id, user);
            res = true;
        }

        return res;
    }
    public boolean delUserId(String id){
        boolean res = false;

        if (id!=null && !id.isEmpty()){
            jobIdUserId.remove(id);
            res = true;
        }

        return res;
    }


    // 用户id和任务id映射
    public String getJobId(String user){
        String res = null;

        if (user!=null && !user.isEmpty()){
            res = userIdJobId.get(user);
        }

        return res;
    }
    public boolean setJobId(String user, String job){
        boolean res = false;

        if ((job!=null && !job.isEmpty()) && (user!=null && !user.isEmpty())){
            userIdJobId.put(user, job);
            res = true;
        }

        return res;
    }
    public boolean delJobId(String user){
        boolean res = false;

        if (user!=null && !user.isEmpty()){
            userIdJobId.remove(user);
            res = true;
        }

        return res;
    }

}
