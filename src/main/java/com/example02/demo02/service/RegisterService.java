package com.example02.demo02.service;


import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example02.demo02.bean.entity.User;
import com.example02.demo02.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * 注册服务类：
 * 用户的注册。
 */
@Service
public class RegisterService {

    private static Log log = LogFactory.get(RegisterService.class);

    @Autowired
    private UserMapper userMapper;

    /**
     * 注册方法
     * @param user
     * @return
     */
    public JSONObject register(User user){

        // 请求接口反馈给请求端的信息
        JSONObject res = new JSONObject();
        String message = "";
        int status = 500;

        // 1.查看用户名是否已被占用
        User exitUser = userMapper.selectUserByName(user.getUser_name());

        // 查看手机号是否被注册过
        User phoneUser = userMapper.selectUserByPhone(user.getPhone());

        if (!ObjectUtils.isEmpty(phoneUser)){
            message = "手机号已被注册！";
            user.setUser_id(-1);
            res.put("data", user);
            res.put("status", status);
            res.put("message", message);
            return res;
        }

        if (ObjectUtils.isEmpty(exitUser)){
            // 2.如果没有占用，将数据插入数据库
            try {
                userMapper.addUser01(user);
                log.info("用户："+user.getUser_name()+" 向数据库中插入成功！其id为："+user.getUser_id()+" 。");
                status = 200;
                message = "注册成功！！！";
            } catch (Exception e) {
                user.setUser_id(-1);
                log.info("用户："+user.getUser_name()+" 注册失败！向数据库插入数据失败！");
                message = "注册失败，数据插入异常！";
            }
        } else {
            user.setUser_id(-1);
            log.info("用户："+user.getUser_name()+"注册失败！用户名已被占用！");
            message = "注册失败，用户名已被占用！";
        }

        // 向反馈信息中插入信息
        res.put("data", user);
        res.put("status", status);
        res.put("message", message);

        return res;
    }

}
