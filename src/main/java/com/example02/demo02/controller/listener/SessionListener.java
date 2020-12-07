package com.example02.demo02.controller.listener;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example02.demo02.bean.SpringJobBeanFactory;
import com.example02.demo02.bean.entity.User;
import com.example02.demo02.mapper.UserMapper;
import com.example02.demo02.service.AvaiUserListService;
import com.example02.demo02.service.UserRedisService;
import com.example02.demo02.util.MySessionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;
import java.util.HashSet;

/**
 * session会话监听类，
 * 要监听session会话的建立和销毁，
 * 也要监听附加到session会话的属性键值对的增删改。
 */
@WebListener
public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {

    private Log log = LogFactory.get(SessionListener.class);

    @Override
    public void attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent){
        HttpSession session = httpSessionBindingEvent.getSession();
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {
        System.out.println("-----attributeRemoved-----");
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent) {
        System.out.println("-----attributeReplaced-----");
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext application = session.getServletContext();

        // 在application范围由一个HashSet集保存所有的session
        @SuppressWarnings("unchecked")
        HashSet<HttpSession> sessions = (HashSet<HttpSession>) application.getAttribute("sessions");
        if (sessions == null) {
            sessions = new HashSet<HttpSession>();
            application.setAttribute("sessions", sessions);
        }
        // 新创建的session均添加到HashSet集中
        sessions.add(session);
        // 可以在别处从application范围中取出sessions集合
        // 然后使用sessions.size()获取当前活动的session数，即为“在线人数”
        log.info("-----sessionCreated-----");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) throws ClassCastException {

        // 1.获取断开会话的会话对象
        log.warn("-----sessionDestroyed-----");
        HttpSession session = event.getSession();

        // 2.查看映射表得到相应会话的用户的id
        String id = MySessionMap.getId(session);
        log.info("断开会话的用户的id为："+id+"!");

        // 先将会话注销
        MySessionMap.delSession(id);

        // 3.如果取得的id不为空
        if (!ObjectUtils.isEmpty(id)){

            // 这里需要先对登录状态进行判断，如果没有登录状态只需要将数据注销即可
            // 4.首先利用spring工厂容器获取容器中的Bean对象
            UserRedisService userRedisService = SpringJobBeanFactory.getBean(UserRedisService.class);
            UserMapper userMapper = SpringJobBeanFactory.getBean(UserMapper.class);

            // 5.查看用户的登录状态
            User overUser = userRedisService.getUserFromRedis(id);
            if (!ObjectUtils.isEmpty(overUser)){

                // 先断开websocket连接再进行
                WebSocketServer webSocketServer = WebSocketServer.getWebSocket(id);
                if (!ObjectUtils.isEmpty(webSocketServer)){
                    try {
                        webSocketServer.onClose();
                        log.info("websocket通信断开成功！");
                    } catch (Exception e) {
                        log.error("websocket通信断开失败！");
                    }
                }

                // 6.如果用户处于登录状态，则将其注销
                // 包含了：redis状态注销、用户的数据库分数更新；如果websocket还在连通状态则关闭。
                userRedisService.deleteUserFromRedis(id);
                userMapper.mark(overUser.getMark(), overUser.getUser_id()+"");
                log.info("已从redis中删除用户状态，并更新数据库中状态！");
            }
            log.info("会话状态断开成功！");
        }

        ServletContext application = session.getServletContext();
        HashSet<?> sessions = (HashSet<?>) application.getAttribute("sessions");
        // 销毁的session均从HashSet集中移除
        sessions.remove(session);

        log.info("session注销完成！！！");
    }
}
