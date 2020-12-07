package com.example02.demo02.mapper;


import com.example02.demo02.bean.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {


//    @Insert("insert into user(user_name, password, mail, role, status, token) value(#{user_name}, #{password}, #{mail}," +
//            "#{role}, #{status}, #{token})")
//    public void addUser(@Param("user_name") String user_name, @Param("password") String password,
//                        @Param("mail") String mail, @Param("role") String role,
//                        @Param("status") String status, @Param("token") String token);

    // TODO:增设手机号字段
    @Insert("insert into user values(#{user_id},#{user_name},#{password},#{role},#{mail},#{status},#{token},#{mark},#{phone},#{hasPhone})")
    @Options(useGeneratedKeys = true, keyProperty = "user_id")
    public int addUser01(User user);

//    @Insert("insert into user values(#{user_id},#{user_name},#{password},#{role},#{mail},#{status},#{token},#{mark})")
//    @Options(useGeneratedKeys = true, keyProperty = "user_id")
//    public int addUser02(User user);

    @Select("select * from user where user_id = #{user_id}")
    public User selectUser(@Param("user_id") int user_id);

    @Select("select * from user where user_name = #{user_name}")
    public User selectUserByName(@Param("user_name") String user_name);

    @Update("update user set mark=#{mark} where user_id=#{user_id}")
    public void mark(@Param("mark") double mark, @Param("user_id") String user_id);

    // TODO:增加通过手机号选择用户
    @Select("select * from user where phone=#{phone}")
    public User selectUserByPhone(@Param("phone") String phone);

    // TODO:选择手机号管理员
    @Select("select * from user where hasPhone='1' order by mark")
    public List<User> selectHasPhoneUser();

}
