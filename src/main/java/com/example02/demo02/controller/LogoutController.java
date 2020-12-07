package com.example02.demo02.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example02.demo02.bean.entity.User;
import com.example02.demo02.service.LogoutService;
import com.example02.demo02.service.UserRedisService;
import com.example02.demo02.util.MySessionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class LogoutController {

    private Log log = LogFactory.get(LoginController.class);

    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private LogoutService logoutService;

    @GetMapping("/logout")
    public JSONObject logout(@RequestParam Integer user_id){

        JSONObject res1 = new JSONObject();

        // 先从缓存中查看是否有该用户的登录状态存在
        User logoutUser = null;
        try {
            logoutUser = userRedisService.getUserFromRedis(user_id+"");
        } catch (Exception e) {
            log.warn("id为："+user_id+" 的用户退出失败，查询redis的在线用户时出错！"+e.getCause().getMessage());
            res1.put("status", "500");
            res1.put("msg", "退出失败，系统出错！");
            return res1;
        }

        // 调用登出服务
        JSONObject res2 = logoutService.logout(logoutUser);

        HttpSession session = MySessionMap.getSession(user_id+"");
        if (!ObjectUtils.isEmpty(session)){
            session.invalidate();
        }

        return res2;

    }
}
