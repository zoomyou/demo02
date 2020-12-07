package com.example02.demo02.util;

/**
 * 全局变量类
 */
public class GlobalVariable {

    // 会话维持时间
    public static final int SESSIONTIME = 300;
    // 任务结果查询时间
    public static final int QUERYTIME = 90;
    // 空闲用户获取时间
    public static final int AVAILUSERTIME = 27;
    // 保存文件夹
    public static final String PATH = "C:\\captcha\\";

    public static final String BASE64URL = "http://127.0.0.1:5000/savebase64";
    public static final String URLURL = "http://127.0.0.1:5000/saveurl";
    public static final String AI_1_URL = "http://127.0.0.1:8000/4numbersandcharacters";
    public static final String AI_2_URL = "";
    public static final String AI_3_URL = "";
    public static final String AI_4_URL = "";
    public static final String CLASSIFYURL = "http://127.0.0.1:6000/image/category";
    public static final String TOPSISURL = "http://127.0.0.1:7000/topsis/human";

}
