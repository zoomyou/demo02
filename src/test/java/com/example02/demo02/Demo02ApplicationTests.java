package com.example02.demo02;

import com.alibaba.fastjson.JSONObject;
import com.example02.demo02.bean.entity.UserInfo;
import com.example02.demo02.service.HttpRequest;
import com.example02.demo02.util.GlobalVariable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
class Demo02ApplicationTests {

	@Autowired
	private HttpRequest httpRequest;

	@Test
	void contextLoads() {

		JSONObject object = new JSONObject();
		object.put("weight", "0.8, 0.1, 0.06, 0.04");
		object.put("benefit", "1, 0, 1, 0");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		UserInfo userInfo1 = new UserInfo("1", 98, 10, 60, 10);
		Map<String, Object> map1 = userInfo1.remap();
		UserInfo userInfo2 = new UserInfo("2", 70, 5, 78, 3);
		Map<String, Object> map2 = userInfo2.remap();
		list.add(map1);
		list.add(map2);

		object.put("alternatives", list);

		JSONObject res = httpRequest.getRes(GlobalVariable.TOPSISURL, object);

		System.out.println(res);

	}

}
