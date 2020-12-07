package com.example02.demo02.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example02.demo02.bean.entity.User;
import com.example02.demo02.mapper.UserMapper;
import com.example02.demo02.util.GlobalVariable;
import com.example02.demo02.util.MySessionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpSession;

/**
 * 登录服务类：
 * 1、登录的验证（包含用户名是否存在、是否已登录、密码验证以及登录状态的记录）。
 */
@Service
public class LoginService {

    private Log log = LogFactory.get(LoginService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRedisService userRedisService;

    public JSONObject login(User user, HttpSession session){

        log.info("用户："+user.getUser_name()+" 正在尝试登录......");

        // 声明登录接口要返回的数据
        JSONObject res = new JSONObject();
        int status = 500;
        String message = "登录失败，系统出错！";

        // redis缓存暂时登录的某些用户以便近期登录时快速登录
        // 1.从数据库中查询登录的用户信息
        User judgeUser = null;
        try {
            judgeUser = userMapper.selectUserByName(user.getUser_name());
            log.info("正在数据库中查询用户："+user.getUser_name());
        } catch (Exception e){
            log.info("用户："+user.getUser_name()+"登录时，数据库查询出错！"+e.getCause().getMessage());
            res.put("status", status);
            res.put("message", message);
            return res;
        }

        // 2.检查用户是否存在
        if (ObjectUtils.isEmpty(judgeUser)){
            // 用户不存在
            message = "登录失败，用户不存在！";
        } else {
            // 3.获取用户的在线信息
            User onlineUser = null;
            try {
                onlineUser = userRedisService.getUserFromRedis(judgeUser.getUser_id()+"");
                log.info("正在查询用户："+judgeUser.getUser_name()+" 的登录状态！"+onlineUser);
            } catch (Exception e){
                log.info("用户："+user.getUser_name()+"登录时，redis登录状态查询出错！");
                res.put("status", status);
                res.put("message", message);
                return res;
            }

            // 4.用户存在，查看是否已登录
            if (onlineUser == null){
                log.info("用户："+judgeUser.getUser_name()+" 开始登录！");
                // 5.密码验证
                if (user.getPassword().equals(judgeUser.getPassword())){
                    // 6.用户未登录，进行登录状态注册
                    log.info("密码验证通过！");
                    try {
                        // 7.将用户进行登录状态注册
                        userRedisService.addUserToRedis(judgeUser);
                        log.info("用户"+judgeUser.getUser_name()+" 登录成功！");
                        res.put("data", judgeUser);
                        status = 200;
                        message = "登录成功！";
                        // 8.设置会话的持续时间，并将其同相应的用户id放入映射表中
                        session.setMaxInactiveInterval(GlobalVariable.SESSIONTIME);
                        MySessionMap.addSessionId(judgeUser.getUser_id()+"", session);
                    } catch (Exception e) {
                        userRedisService.deleteUserFromRedis(judgeUser.getUser_id()+"");
                        log.info("用户"+judgeUser.getUser_name()+" 登录失败，登录状态维护出错！");
                        status = 500;
                        message = "登录失败，系统出错！";
                    }
                } else {
                    status = 500;
                    message = "登录失败，密码错误！";
                }
            } else {
                message = "用户已登录请勿重复登录！";
            }
        }

        res.put("status", status);
        res.put("message", message);

        return res;
    }
}
