package com.example02.demo02.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example02.demo02.bean.entity.Job;
import com.example02.demo02.bean.entity.User;
import com.example02.demo02.bean.entity.UserInfo;
import com.example02.demo02.mapper.UserMapper;
import com.example02.demo02.util.GlobalVariable;
import com.example02.demo02.util.ListUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 空闲用户列表维护类
 */
@Service
public class AvaiUserListService {

    private Log log = LogFactory.get(AvaiUserListService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JobService jobService;

    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HttpRequest httpRequest;

    /**
     * 从可用用户列表中获取分数最高的用户
     * @return 用户对象
     */
    // TODO:将这一个修改为加入TOPSIS即可
    public synchronized User getGreatUserFromRedisList(String jobType){
        User res = null;

        //获取redis操作对象，声明redis中的key值
        ValueOperations operations = redisTemplate.opsForValue();
        String key = "availUserList";

        //判断redis中是否存在key值对应的value值
        if (redisTemplate.hasKey(key)){
            //获取当前可用用户的列表
            String redisList = (String) operations.get(key);
            Type type = new TypeToken<List<User>>() {}.getType();
            List<User> list = new Gson().fromJson(redisList, type);

            //TODO:生成list用于调用TOPSIS接口
            List<Map<String, Object>> list1 = new ArrayList<>();
            if (!list.isEmpty()){

//                for (int i = 0; i<list.size(); i++){
//                    User a = list.get(i);
//                    UserInfo b = new UserInfo(a.getUser_id()+"", jobService.currAccAll(a.getUser_id()+""),
//                            jobService.speed(a.getUser_id()+""), a.getMark(), jobService.currExcALL(a.getUser_id()+""));
//                    list1.add(b.remap());
//                }
//
//                JSONObject object = new JSONObject();
//                object.put("weights", "0.8, 0.1, 0.06, 0.04");
//                object.put("benefit", "1, 0, 1, 0");
//                object.put("alternatives", list1);
//                JSONObject avauser = httpRequest.getRes(GlobalVariable.TOPSISURL, object);
//                System.out.println(avauser);

//                if (ObjectUtils.isEmpty(avauser)){
//
//                } else {
//                    res = ListUtil.deleteFromList(list, avauser.getString("id"));
//                    System.out.println(res);
//                }
//                System.out.println("当前空闲用户列表大小为：" + list.size());
                //获取第一个用户
                res = list.remove(0);
                redisTemplate.delete(key);
                log.warn("已取得一个空闲用户："+res.getUser_name() + ", 此时空闲用户列表大小为：" + list.size());

            }

            //判断当前列表是否为空
            if (!list.isEmpty()){
                String toJson = new Gson().toJson(list);
                operations.set(key, toJson);
            }

        }

        return res;
    }

    /**
     * 将参数中的用户对象加入到可用用户列表中
     * @param user
     * @return 返回是否成功的信息
     */
    public synchronized boolean addUserToRedisList(User user){
        boolean res = false;

        //获取redis操作对象，声明redis中的key值
        ValueOperations operations = redisTemplate.opsForValue();
        String key = "availUserList";

        if (redisTemplate.hasKey(key)){
            //获取当前可用用户的列表
            String redisList = (String) operations.get(key);
            Type type = new TypeToken<List<User>>() {}.getType();
            List<User> list = new Gson().fromJson(redisList, type);

            //将用户插入到对应的位置
            ListUtil.addToList(list, user);

            //将列表插回缓冲区
            String toJson = new Gson().toJson(list);
            operations.set(key, toJson);
            res = true;
        } else {
            //创建可用用户列表
            List<User> list = new LinkedList<>();
            list.add(user);

            //将列表插回缓冲区
            String toJson = new Gson().toJson(list);
            operations.set(key, toJson);
            res = true;
        }

        log.warn("已将空闲用户 "+user.getUser_name()+" 加入到空闲用户列表！！！");

        return res;
    }

    public synchronized boolean deleteUserFromRedisList(User user){
        boolean res = false;

        //获取redis操作对象，设置redis中的key值
        ValueOperations operations = redisTemplate.opsForValue();
        String key = "availUserList";

        //判断redis中是否存在key值对应的value值
        if (redisTemplate.hasKey(key)){
            //获取当前可用用户的列表
            String redisList = (String) operations.get(key);
            Type type = new TypeToken<List<User>>() {}.getType();
            List<User> list = new Gson().fromJson(redisList, type);

            //从可用用户列表中删除用户
            if (ListUtil.deleteFromList(list, user)){
                //将列表插回缓冲区
                String toJson = new Gson().toJson(list);
                operations.set(key, toJson);
                res = true;
            } else {
                return false;
            }

        }

        log.warn("已将用户 "+user.getUser_name()+" 从空闲用户列表中删除！！！");

        return res;
    }
}
