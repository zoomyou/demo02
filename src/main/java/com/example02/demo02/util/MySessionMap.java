package com.example02.demo02.util;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

public class MySessionMap {

    private static Log log = LogFactory.get(MySessionMap.class);

    private static HashMap<String, HttpSession> idSessionMap = new HashMap<>();
    private static HashMap<HttpSession, String> sessionIdMap = new HashMap<>();

    public static void addSessionId(String user_id, HttpSession session){
        if (!ObjectUtils.isEmpty(session) && !ObjectUtils.isEmpty(user_id)){
            idSessionMap.put(user_id, session);
            sessionIdMap.put(session, user_id);
        }
    }

    public static void delSession(String id){
        if (!ObjectUtils.isEmpty(id)){
            HttpSession session = idSessionMap.remove(id);
            if (!ObjectUtils.isEmpty(session)){
                sessionIdMap.remove(session);
            }
        }
    }

    public static HttpSession getSession(String user_id){
        if (user_id.isEmpty()){
            return null;
        }
        return idSessionMap.get(user_id);
    }

    public static String getId(HttpSession session){
        if (ObjectUtils.isEmpty(session)){
            return null;
        }
        return sessionIdMap.get(session);
    }

    public static boolean sessionMaintain(String id){
        boolean flag = false;

        HttpSession session = getSession(id);
        if (ObjectUtils.isEmpty(session)){
            log.warn("会话状态维护失败！");
        } else {
            session.setMaxInactiveInterval(GlobalVariable.SESSIONTIME);
            flag = true;
            log.info("用户 "+id+" 会话维护成功！");
        }

        return flag;
    }

}
