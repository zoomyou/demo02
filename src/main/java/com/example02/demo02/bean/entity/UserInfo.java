package com.example02.demo02.bean.entity;

import java.util.HashMap;
import java.util.Map;

public class UserInfo {

    private String id;
    private double accuracy;
    private double speed;
    private double accr_score;
    private double exception_rate;

    public UserInfo(String id, double accuracy, double speed, double accr_score, double exception_rate) {
        this.id = id;
        this.accuracy = accuracy;
        this.speed = speed;
        this.accr_score = accr_score;
        this.exception_rate = exception_rate;
    }

    public Map<String, Object> remap(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        int acc = (int)(this.accuracy*100);
        map.put("accuracy", acc);
        int sp = (int)this.speed;
        map.put("speed", sp);
        int sco = (int)this.accr_score;
        map.put("accu_score", sco);
        int ex = (int)(this.exception_rate*100);
        map.put("exception_rate", ex);
        return map;
    }
}
