package com.example02.demo02.controller;

import com.alibaba.fastjson.JSONObject;
import com.example02.demo02.service.HttpRequest;
import com.example02.demo02.util.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    @Autowired
    private HttpRequest httpRequest;

    @PostMapping("/testrequest")
    public JSONObject test(){
        JSONObject res = new JSONObject();

        JSONObject object = new JSONObject();
        object.put("path", "C:\\Users\\10842\\captchas\\Vaptcha\\vaptcha_0000.png");

        res = httpRequest.getRes(GlobalVariable.CLASSIFYURL, object);

        return res;
    }
}
