package com.example02.demo02.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpRequest {

    @Autowired
    RestTemplate restTemplate;

    public JSONObject getRes(String url, JSONObject param){

        JSONObject res = null;

        try {
            HttpHeaders headers = new HttpHeaders();
            // headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<String> entity = new HttpEntity(param, headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            String responseBody = responseEntity.getBody();
            res = JSONObject.parseObject(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            res = null;
        }

        System.out.println(res);

        return res;
    }
}
