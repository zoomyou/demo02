package com.example02.demo02.controller;


import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example02.demo02.bean.entity.User;
import com.example02.demo02.service.AvaiUserListService;
import com.example02.demo02.service.RegisterService;
import com.example02.demo02.service.UserRedisService;
import com.example02.demo02.util.GlobalVariable;
import com.example02.demo02.util.MySessionMap;
import com.example02.demo02.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class RegisterController {

    private Log log = LogFactory.get(RegisterController.class);

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UserRedisService userRedisService;

    // 注册接口
    @PostMapping("/register")
    public JSONObject register(HttpSession session, @RequestBody JSONObject formData){

//        HttpSession session = request.getSession();

        // 1.利用传入的表单数据生成新的用户对象
        String user_name = formData.getString("user_name");
        User newUser = new User(user_name, formData.getString("password"), formData.getString("mail"),
                "正常", TokenUtil.sign(user_name), 0.0);

        // TODO:在此处加入获取json字符串中的手机号字段
        newUser.setPhone(formData.getString("phone"));

        switch (Integer.parseInt(formData.getString("role_id"))){
            case 0:
                newUser.setRole("管理员");
                break;
            case 1:
                newUser.setRole("打码请求者");
                break;
            case 2:
                newUser.setRole("人工打码客户端");
                break;
            default: newUser.setRole("error in role setting");
        }

        // 2.调用注册服务的注册方法返回相应的信息
        JSONObject res = registerService.register(newUser);

        // 3.调用redis服务，将用户的信息放入redis中，以表示登录状态
        if (res.getString("status").equals("200")){
            try {
                userRedisService.addUserToRedis(newUser);
                log.info("用户："+newUser.getUser_name()+"登录成功！");

                // 如果登录成功，则开始维护会话信息
                session.setMaxInactiveInterval(GlobalVariable.SESSIONTIME);
                MySessionMap.addSessionId(newUser.getUser_id()+"", session);

            } catch (Exception e){
                log.info("用户："+newUser.getUser_name()+" 注册成功！登录失败！");
                // 如果登录失败则将状态还原
                userRedisService.deleteUserFromRedis(newUser.getUser_id()+"");
                // 维护反馈信息
                res.put("status", 500);
                res.put("message", "注册成功！登录失败！请直接登录！");
                return res;
            }
        }

        return res;
    }
}
