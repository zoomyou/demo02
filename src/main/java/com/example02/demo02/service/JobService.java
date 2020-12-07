package com.example02.demo02.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example02.demo02.bean.entity.Job;
import com.example02.demo02.bean.entity.User;
import com.example02.demo02.mapper.JobMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 任务服务类：
 * 1、计算本次任务得分；
 * 2、获取用户准确率；
 * 3、向数据库中修改任务的反馈码。
 */
@Service
public class JobService {

    private Log log = LogFactory.get(JobService.class);

    @Autowired
    private JobMapper jobMapper;

    // 计算本次任务分数
    public double mark(User user, double accuracy, String response_code, long cost){
        double res = 0.0;

        if (response_code.equals("0")) {
            user.setMark(user.getMark() - 2);
            res = -2;
        } else {
            if ((accuracy * 100 - cost/1000)>1) {
                user.setMark(user.getMark()+Math.sqrt(accuracy*100-cost/1000));
                res = accuracy * 100 - cost/1000;
            } else {
                user.setMark(user.getMark()+0.5);
                res = 1;
            }
        }

        return res;
    }

    public double currAccAll(String clientId){
        double accuracy = 0.0;
        double rightNum = jobMapper.selectSuccessJobsFromReceiver(clientId);
        double totalNum = jobMapper.selectAllJobsFromReceiver(clientId);
        double nullNum = jobMapper.selectNullResJobsFromReceiver(clientId);

        if (nullNum > 0){
            accuracy = (rightNum / ((totalNum - nullNum) + Math.log(nullNum)));
        } else {
            accuracy = (rightNum / (totalNum - nullNum));
        }

        log.info("用户 "+clientId+" 的准确率为："+accuracy);

        return accuracy;
    }

    public double currExcALL(String clientId){
        double excep = 0.0;
        double rightNum = jobMapper.selectSuccessJobsFromReceiver(clientId);
        double totalNum = jobMapper.selectAllJobsFromReceiver(clientId);
        double nullNum = jobMapper.selectNullResJobsFromReceiver(clientId);

        if (nullNum > 0){
            excep = ((totalNum-rightNum-nullNum) / ((totalNum - nullNum) + Math.log(nullNum)));
        } else {
            excep = ((totalNum-rightNum-nullNum) / (totalNum - nullNum));
        }

        log.info("用户 "+clientId+" 的准确率为："+excep);

        return excep;
    }

    public double speed(String clientId){
        int speed = 0;

        List<Job> list = jobMapper.selectspeed(clientId);

        for (int i = 0; i < list.size(); i++){
            Job temp = list.get(i);
            speed += (temp.getFinish_time().getTime() - temp.getReceive_time().getTime())/1000;
        }

        speed /= list.size();

        return speed;
    }

    public boolean response(String job_id, String response_code){
        boolean flag = false;

        try {
            jobMapper.response(response_code, job_id);
            flag = true;
        } catch (Exception e) {
            log.error("任务反馈失败，向数据库插入数据失败！");
        }

        return flag;
    }
}
