package com.example02.demo02.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example02.demo02.bean.Producer01;
import com.example02.demo02.bean.entity.*;
import com.example02.demo02.mapper.JobMapper;
import com.example02.demo02.mapper.UserMapper;
import com.example02.demo02.service.HttpRequest;
import com.example02.demo02.service.JobResRedisService;
import com.example02.demo02.util.GlobalVariable;
import com.example02.demo02.util.MySessionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * 识别控制器：
 * 负责将任务发送到消息队列中，并监听redis中的任务结果。
 */
@RestController
public class RecognitionController {

    private Log log = LogFactory.get(RecognitionController.class);

    @Autowired
    private Producer01 producer01;

    @Autowired
    private JobResRedisService jobResRedisService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private HttpRequest httpRequest;

    @PostMapping("/recognition")
    public JSONObject recognition(@RequestBody JSONObject params,@RequestHeader("token") String token) throws InterruptedException{

        JSONObject res = new JSONObject();
        res.put("class", " ");

        // 1.先调用数据库服务，查看身份是否合法
        User requestUser = userMapper.selectUser(Integer.parseInt(params.getString("user_id")));
        if (ObjectUtils.isEmpty(requestUser) ||
                (!ObjectUtils.isEmpty(requestUser) && !requestUser.getToken().equals(token))){
            res.put("status", "500");
            res.put("message", "非法请求者！");
            return res;
        }

        System.out.println("shenfen");

        // 会话状态维护
        boolean flag = MySessionMap.sessionMaintain(requestUser.getUser_id()+"");

        // 2.判断数据是否为空（直接实现）
        if (params.getString("data").isEmpty() || params.getString("data")==null){
            res.put("status", "500");
            res.put("message", "数据内容不可为空！");
            return res;
        }

        // 3.生成job对象，并先分配唯一的id（直接实现，或调用jobService），再设置job对象
        String tempJobId = UUID.randomUUID().toString();
        Job newJob = new Job();
        newJob.setRequester_id(params.getString("user_id"));
        newJob.setRequest_time(new Timestamp(System.currentTimeMillis()));
        newJob.setJob_status("0");

        // TODO:保存图片
        if (params.getString("src_type").equals("1")){
            String tempbase64 = params.getString("data");
            int end = tempbase64.indexOf(",");
            if (end>0){
                tempbase64 = tempbase64.substring(end+1);
            }
            Base64Post base64Post = new Base64Post(tempbase64, GlobalVariable.PATH + tempJobId + ".png");
            JSONObject object = (JSONObject) JSONObject.toJSON(base64Post);
            JSONObject isSave = httpRequest.getRes(GlobalVariable.BASE64URL, object);
            if (ObjectUtils.isEmpty(isSave)){
                res.put("status", "500");
                res.put("message", "系统出错！");
                return res;
            } else {
                if (isSave.getString("status").equals("400")){
                    res.put("status", "500");
                    res.put("message", "系统出错！");
                    return res;
                }
            }
        }
        System.out.println("图片保存");
        if (params.getString("src_type").equals("2")){
            UrlPost urlPost = new UrlPost(params.getString("data"), GlobalVariable.PATH + tempJobId + ".png");
            JSONObject object = (JSONObject) JSONObject.toJSON(urlPost);
            JSONObject isSave = httpRequest.getRes(GlobalVariable.URLURL, object);
            if (ObjectUtils.isEmpty(isSave)){
                res.put("status", "500");
                res.put("message", "系统出错！");
                return res;
            } else {
                if (isSave.getString("status").equals("400")){
                    res.put("status", "500");
                    res.put("message", "系统出错！");
                    return res;
                }
            }
        }

        // TODO:验证码分类
        String captchaType = "";
        if (params.getString("src_type").equals("1") || params.getString("src_type").equals("2")){
            JSONObject object = new JSONObject();
            object.put("path", GlobalVariable.PATH + tempJobId + ".png");
            JSONObject isClass = httpRequest.getRes(GlobalVariable.CLASSIFYURL, object);
            if (ObjectUtils.isEmpty(isClass)){
                // 人来分
                captchaType = "未知";
            } else {
                if (isClass.getString("code").equals("200")){
                    String temptype = isClass.getString("type");
                    switch (temptype) {
                        case "0":
                            captchaType = "12306点选型";
                            break;
                        case "1":
                            captchaType = "字符型";
                            break;
                        case "2":
                            captchaType = "点选字符型";
                            break;
                        case "3":
                            captchaType = "谷歌reCaptcha型";
                            break;
                        case "4":
                            captchaType = "拖动型";
                            break;
                        case "5":
                            captchaType = "轨迹型";
                            break;
                        case "6":
                            captchaType = "旋转型";
                            break;
                        default:
                            captchaType = "未知";
                    }
                } else {
                    captchaType = "未知";
                }
            }
        } else {
            captchaType = "未知";
        }

        System.out.println("fenlei");

        res.put("class", captchaType);

        if (!captchaType.equals("字符型")){
            // 4.先将任务打包为消息对象，再将任务发送到消息队列中（需要注入producer01Bean对象）
            JobMessage jobMessage = new JobMessage(tempJobId, params.getString("src_type"), params.getString("data"), "");
            if (!producer01.produce(jobMessage)){
                res.put("status", "500");
                res.put("message", "系统出错！");
                return res;
            }
            newJob.setReceive_time(new Timestamp(System.currentTimeMillis()));
            newJob.setJob_status("1");

            // 5.开始忙等任务的结果（循环忙等，调用JobResRedisService来获取结果，根据任务缓存区的内容来生成结果）
            int controltemp = 0;
            String result = null, availUser = null;
            while (true){
                result = jobResRedisService.getRes(tempJobId);
                if (controltemp>GlobalVariable.QUERYTIME || result!=null){
                    break;
                }
                Thread.sleep(1000);
            }
            if (result!=null){
                log.info("取得打码结果："+result);
                res.put("bypass_result", result);

                availUser = jobResRedisService.getUserId(tempJobId);

                res.put("client", availUser);

                newJob.setFinish_time(new Timestamp(System.currentTimeMillis()));
                newJob.setCaptcha_result(result);
                newJob.setJob_status("2");
                newJob.setReceiver_id(availUser);

                if (result.equals("timeout") || result.equals("giveup") || result.equals("closesocket") || result.equals("无空闲用户在线")){
                    res.put("status", "501");
                    res.put("message", "打码客户端放弃任务或打码超时，请重新提交任务！");
                } else if (result.equals("系统出错")){
                    res.put("status", "500");
                    res.put("message", "系统出错");
                } else {
                    res.put("status", "200");
                    res.put("message", "识别成功");
                }
            } else {
                res.put("status", "500");
                res.put("message", "系统出错");
            }
        } else {
            // TODO:AI自动识别
            JSONObject object = new JSONObject();
            object.put("path",GlobalVariable.PATH+tempJobId+".png");
            newJob.setReceive_time(new Timestamp(System.currentTimeMillis()));
            JSONObject isAI = httpRequest.getRes(GlobalVariable.AI_1_URL, object);
            if (ObjectUtils.isEmpty(isAI)){
                res.put("status", "500");
                res.put("message", "系统出错");
            } else {
                if (isAI.getString("status").equals("")){
                    res.put("status", "500");
                    res.put("message", "系统出错");
                }else {
                    newJob.setFinish_time(new Timestamp(System.currentTimeMillis()));
                    res.put("bypass_result",isAI.getString("value"));
                    res.put("status", "200");
                    res.put("message", "识别成功");
                    newJob.setJob_status("2");
                    newJob.setReceiver_id("12");
                    res.put("client", "12");
                }
            }
        }

        // 6.将任务的最终数据放入数据库，并复制一份放入redis以供反馈接口快速查询
        try {
            newJob.setJob_id(null);
            jobMapper.addJob(newJob);
        } catch (Exception e) {
            log.error("任务已结束，数据库出错未记录。出错信息："+e.getMessage());
        }

        res.put("job_id", newJob.getJob_id());

        return res;

    }
}
