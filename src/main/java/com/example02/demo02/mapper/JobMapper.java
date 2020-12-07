package com.example02.demo02.mapper;

import com.example02.demo02.bean.entity.Job;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface JobMapper {

    /**
     * 添加任务记录
     * @param job
     */
    @Insert("insert into job values(#{job_id},#{job_name},#{requester_id},#{receiver_id}," +
            "#{job_status},#{request_time},#{receive_time},#{finish_time},#{captcha_src},#{captcha_result}," +
            "#{response_code},#{subtype_id})")
    @Options(useGeneratedKeys = true, keyProperty = "job_id")
    public int addJob(Job job);

    @Select("select * from job where job_id=#{job_id}")
    public Job findById(@Param("job_id") String job_id);

    @Select("select count(*) from job where receiver_id=#{receiver_id} and response_code='1'")
    public Integer selectSuccessJobsFromReceiver(@Param("receiver_id") String receiver_id);

    @Select("select count(*) from job where receiver_id=#{receiver_id}")
    public Integer selectAllJobsFromReceiver(@Param("receiver_id") String receiver_id);

    @Select("select count(*) from job where receiver_id=#{receiver_id} and response_code is null")
    public Integer selectNullResJobsFromReceiver(@Param("receiver_id") String receiver_id);

    @Update("update job set response_code=#{response_code} where job_id=#{job_id}")
    public void response(@Param("response_code") String response_code, @Param("job_id") String job_id);

    @Select("select * from job where receiver_id=#{receiver_id} and job_status='2'")
    public List<Job> selectspeed(@Param("receiver_id") String receiver_id);
}
