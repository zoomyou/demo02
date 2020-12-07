package com.example02.demo02.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example02.demo02.bean.entity.User;
import com.example02.demo02.mapper.UserMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class UserRedisService {

    private Log log = LogFactory.get(UserRedisService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 将用户从redis中获取出来
     * @param id
     * @return 返回对应的用户
     */
    public User getUserFromRedis(String id){
        User res = null;

        //获取redis操作对象，设置redis中的key值
        String key = "user"+id;
        ValueOperations operations = redisTemplate.opsForValue();

        //判断是否存在key值对应的value
        if (redisTemplate.hasKey(key)){
            //如果存在则获取用户对象
            String redisUser = (String) operations.get(key);
            res = new Gson().fromJson(redisUser, User.class);
        }

        return res;
    }

    /**
     * 将用户加入到redis缓存中
     * @param user
     * @return
     */
    public boolean addUserToRedis(User user){
        boolean res = false;

        //获取redis操作对象，设置redis中的key值
        String key = "user"+user.getUser_id();
        ValueOperations operations = redisTemplate.opsForValue();

        //判断redis中是否存在用户信息
        if (redisTemplate.hasKey(key)){
            //如果存在先将其删除
            redisTemplate.delete(key);

            //再加入之中
            String toJson = new Gson().toJson(user);
            operations.set(key, toJson);

            res = true;
        } else {
            //如果不存在则加入其中
            String toJson = new Gson().toJson(user);
            operations.set(key, toJson);

            res = true;
        }

        return res;
    }

    /**
     * 将用户从redis缓存中删除
     * @param id
     * @return
     */
    public boolean deleteUserFromRedis(String id){
        boolean res = false;

        String key = "user"+id;

        if (redisTemplate.hasKey(key)){
            redisTemplate.delete(key);
            log.info("已经用户id为："+id+"用户从redis中删除。");
            res = true;
        }

        return res;
    }
}
