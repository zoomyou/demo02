package com.example02.demo02.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example02.demo02.bean.entity.User;
import com.example02.demo02.service.LoginService;
import com.example02.demo02.util.MySessionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 登录控制器：
 * 负责调用登录服务和创建会话。
 */
@RestController
public class LoginController {

    private Log log = LogFactory.get(LoginController.class);

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public JSONObject login(HttpSession session, @RequestBody JSONObject params){

//        HttpSession session = request.getSession();

        JSONObject res = new JSONObject();

        // 1.在登录时获取同服务器的连接会话
        // HttpSession session = request.getSession();
        String user = MySessionMap.getId(session);

        // 2.检查获取到的会话对应的用户是否存在
        if (ObjectUtils.isEmpty(user)){

            // 3.利用传入的表单数据生成要登录的用户对象
            User loginUser = new User();
            loginUser.setUser_name(params.getString("username"));
            loginUser.setPassword(params.getString("password"));

            // 4.调用登录服务
            res = loginService.login(loginUser, session);
        } else {
            // 用户已存在就直接返回登录失败
            res.put("status", 500);
            res.put("message", "该会话已被占用请换一个浏览器登录！");
        }

        return res;
    }

}
