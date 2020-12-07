package com.example02.demo02.bean.entity;

/**
 * 对应于数据库user表中的实体类
 */
public class User {

    //对应于数据库user表中的各个字段
    private int user_id;
    private String user_name;
    private String password;
    private String mail;
    private String role;
    private String status;
    private String token;
    private double mark;
    // TODO:是否持有手机
    private String hasPhone;

    public String getHasPhone() {
        return hasPhone;
    }

    public void setHasPhone(String hasPhone) {
        this.hasPhone = hasPhone;
    }

    // TODO:增加电话号码字段
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public User() {
    }

    public User(String user_name, String password, String mail, String status, String token, double mark) {
        this.user_name = user_name;
        this.password = password;
        this.mail = mail;
        this.status = status;
        this.token = token;
        this.mark = mark;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", user_name='" + user_name + '\'' +
                ", password='" + password + '\'' +
                ", mail='" + mail + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", token='" + token + '\'' +
                ", mark=" + mark +
                '}';
    }
}
