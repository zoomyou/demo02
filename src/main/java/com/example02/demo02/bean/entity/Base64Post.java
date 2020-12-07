package com.example02.demo02.bean.entity;

public class Base64Post {

    private String str;
    private String path;

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Base64Post(String str, String path) {
        this.str = str;
        this.path = path;
    }

}
