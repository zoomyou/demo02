package com.example02.demo02.bean.entity;

import java.sql.Timestamp;

/**
 * 对应于数据库表job的实体类
 */
public class Job {

    //数据库中job表的各个字段
    private String job_id;
    private String job_name;
    private String requester_id;
    private String receiver_id;
    private String job_status;
    private Timestamp request_time;
    private Timestamp receive_time;
    private Timestamp finish_time;
    private String captcha_src;
    private String captcha_result;
    private String response_code;
    private String subtype_id;

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getJob_name() {
        return job_name;
    }

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public String getRequester_id() {
        return requester_id;
    }

    public void setRequester_id(String requester_id) {
        this.requester_id = requester_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getJob_status() {
        return job_status;
    }

    public void setJob_status(String job_status) {
        this.job_status = job_status;
    }

    public Timestamp getRequest_time() {
        return request_time;
    }

    public void setRequest_time(Timestamp request_time) {
        this.request_time = request_time;
    }

    public Timestamp getReceive_time() {
        return receive_time;
    }

    public void setReceive_time(Timestamp receive_time) {
        this.receive_time = receive_time;
    }

    public Timestamp getFinish_time() {
        return finish_time;
    }

    public void setFinish_time(Timestamp finish_time) {
        this.finish_time = finish_time;
    }

    public String getCaptcha_src() {
        return captcha_src;
    }

    public void setCaptcha_src(String captcha_src) {
        this.captcha_src = captcha_src;
    }

    public String getCaptcha_result() {
        return captcha_result;
    }

    public void setCaptcha_result(String captcha_result) {
        this.captcha_result = captcha_result;
    }

    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }

    public String getSubtype_id() {
        return subtype_id;
    }

    public void setSubtype_id(String subtype_id) {
        this.subtype_id = subtype_id;
    }

    @Override
    public String toString() {
        return "Job{" +
                "job_id=" + job_id +
                ", job_name='" + job_name + '\'' +
                ", requester_id='" + requester_id + '\'' +
                ", receiver_id='" + receiver_id + '\'' +
                ", job_status='" + job_status + '\'' +
                ", request_time=" + request_time +
                ", receive_time=" + receive_time +
                ", finish_time=" + finish_time +
                ", captcha_src='" + captcha_src + '\'' +
                ", captcha_result='" + captcha_result + '\'' +
                ", response_code='" + response_code + '\'' +
                ", subtype_id='" + subtype_id + '\'' +
                '}';
    }
}
