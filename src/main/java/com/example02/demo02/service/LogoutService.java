package com.example02.demo02.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example02.demo02.bean.entity.User;
import com.example02.demo02.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class LogoutService {

    private Log log = LogFactory.get(LogoutService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private AvaiUserListService avaiUserListService;

    public JSONObject logout(User user){

        JSONObject res = new JSONObject();
        int status = 500;
        String msg = "退出失败！";

        // 1.先判断是否有登录状态
        if (ObjectUtils.isEmpty(user)){
            status = 200;
            msg = "用户已退出！";
            log.warn("退出失败，用户已退出，redis缓存区中无该用户存在！");
        } else {
            log.info("用户："+user.getUser_name()+"正在退出 ......");

            // 2.用户已登录，将登录状态进行注销
            try {
                userRedisService.deleteUserFromRedis(user.getUser_id()+"");
                avaiUserListService.deleteUserFromRedisList(user);
            } catch (Exception e) {
                log.warn("用户："+user.getUser_name()+"退出时，redis中用户信息维护出错！");
                userRedisService.addUserToRedis(user);
                res.put("status", status);
                res.put("message", msg);
                return res;
            }
            // 3.判断用户类型，进行打分操作
            if (user.getRole().equals("人工打码客户端")){
                // 如果是打码客户端则维护得分情况
                try {
                    userMapper.mark(user.getMark(), user.getUser_id()+"");
                } catch (Exception e) {
                    log.warn("用户："+user.getUser_name()+" 在数据库中更新分数时，出现错误！");
                }
            }

            log.info("用户："+user.getUser_name()+"退出成功！");
            status = 200;
            msg = "退出成功！";
        }

        res.put("status", status);
        res.put("message", msg);

        return res;

    }
}
