package com.example02.demo02.service;


import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example02.demo02.bean.entity.JobMessage;
import com.example02.demo02.bean.entity.User;
import com.example02.demo02.mapper.UserMapper;
import com.example02.demo02.util.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 线程服务类：
 * 为任务分配空闲用户或指定手机号的用户，并推送任务。
 */
@Service
public class ThreadService {

    private Log log = LogFactory.get(ThreadService.class);

    @Autowired
    private AvaiUserListService avaiUserListService;

    @Autowired
    private JobResRedisService jobResRedisService;

    @Autowired
    private PushService pushService;

    @Autowired
    private UserMapper userMapper;

    @Async
    public void schedule(JobMessage jobMessage){

        // 1.分配空闲用户
        User availUser = null;
        // 设置控制变量和查询时间上限
        int i = 0;

        // TODO:增加一个类型4的判断，如果是手机号那么就循环的判断该持手机人的在线情况（WebServer的在线情况），指定推送人
        if (jobMessage.getType().equals("4")){
            /**
             * 先从数据库中查出该手机号的用户；
             * 再循环的从空闲用户列表中删除该用户；
             * 当删除成功时跳出循环或是超时。
             */
            List<User> availUserList = userMapper.selectHasPhoneUser();
            availUser = availUserList.get(0);
            boolean flag = false;
            while (true){
                try {
                    flag = avaiUserListService.deleteUserFromRedisList(availUser);
                } catch (Exception e) {
                    jobResRedisService.setRes(jobMessage.getJob_id(), "系统出错");
                    availUser = null;
                    break;
                }
                if (flag){
                    break;
                }
                if (!flag && i> GlobalVariable.AVAILUSERTIME){
                    availUser = null;
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("线程休眠出错");
                }
                i++;
            }
        } else {
            // TODO:
            while (true){
                // 利用死循环的方式来获取空闲用户
                try {
                    availUser = avaiUserListService.getGreatUserFromRedisList(jobMessage.getJobType());
                    System.out.println("zhengzaihuoqugreatuser");
                } catch (Exception e) {
                    jobResRedisService.setRes(jobMessage.getJob_id(), "系统出错");
                    break;
                }
                // 查看是否跳出循环
                if (!ObjectUtils.isEmpty(availUser) || i>GlobalVariable.AVAILUSERTIME){
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("线程休眠出错");
                }
                i++;
            }
        }

        // 2.对取出的空闲用户进行空值判断
        if (ObjectUtils.isEmpty(availUser)){
            jobResRedisService.setRes(jobMessage.getJob_id(), "无空闲用户在线");
            return;
        }

        // 3.向客户端发送任务，并将任务id和用户id相对应
        try {
            pushService.pushImgToCLient(jobMessage, availUser);
            jobResRedisService.setUserId(jobMessage.getJob_id(), availUser.getUser_id()+"");
            jobResRedisService.setJobId(availUser.getUser_id()+"", jobMessage.getJob_id());
        } catch (Exception e) {
            jobResRedisService.setRes(jobMessage.getJob_id(), "系统出错");
            avaiUserListService.addUserToRedisList(availUser);
        }

    }
}
