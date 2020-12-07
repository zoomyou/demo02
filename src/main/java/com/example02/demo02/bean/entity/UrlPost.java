package com.example02.demo02.bean.entity;

public class UrlPost {

    private String url;
    private String path;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public UrlPost(String url, String path) {
        this.url = url;
        this.path = path;
    }
}
