package com.example02.demo02.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example02.demo02.bean.entity.User;
import com.example02.demo02.controller.listener.WebSocketServer;
import com.example02.demo02.mapper.UserMapper;
import com.example02.demo02.util.GlobalVariable;
import com.example02.demo02.util.MySessionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 用户信息查询控制器：
 * 用于返回用户的基本信息。
 */
@RestController
public class UserInfoController {

    private Log log = LogFactory.get(UserInfoController.class);

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/users/{id}")
    public User getUserInfo(@PathVariable Integer id){

        // 会话状态维护
        boolean flag = MySessionMap.sessionMaintain(id+"");

        return userMapper.selectUser(id);
    }
}
