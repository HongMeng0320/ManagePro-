package com.example.springdemo.mapper;

import com.example.springdemo.pojo.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {
    
    /**
     * 根据用户ID查询用户
     * @param userId 用户ID
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    User findById(Integer userId);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);
    
    /**
     * 查询所有用户
     * @return 用户列表
     */
    @Select("SELECT * FROM users ORDER BY user_id DESC")
    List<User> findAll();
    
    /**
     * 条件查询用户
     * @param username 用户名（可为null）
     * @param status 状态（可为null）
     * @return 用户列表
     */
    @Select("<script>" +
            "SELECT * FROM users " +
            "<where>" +
            "<if test='username != null'> username LIKE CONCAT('%', #{username}, '%') </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "</where>" +
            "ORDER BY user_id DESC" +
            "</script>")
    List<User> findByCondition(@Param("username") String username, @Param("status") Integer status);
    
    /**
     * 插入用户
     * @param user 用户信息
     * @return 影响行数
     */
    @Insert("INSERT INTO users(username, password, email, phone, role, status, created_at, updated_at) " +
            "VALUES(#{username}, #{password}, #{email}, #{phone}, #{role}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
    int insert(User user);
    
    /**
     * 更新用户
     * @param user 用户信息
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE users " +
            "<set>" +
            "<if test='email != null'>email = #{email},</if>" +
            "<if test='phone != null'>phone = #{phone},</if>" +
            "<if test='password != null'>password = #{password},</if>" +
            "<if test='role != null'>role = #{role},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "updated_at = #{updatedAt}" +
            "</set>" +
            "WHERE user_id = #{userId}" +
            "</script>")
    int update(User user);
    
    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 状态
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE users SET status = #{status}, updated_at = #{updatedAt} WHERE user_id = #{userId}")
    int updateStatus(@Param("userId") Integer userId, @Param("status") Integer status, @Param("updatedAt") LocalDateTime updatedAt);

    // 更新头像
    @Update("UPDATE user SET user_pic = #{avatarUrl} , update_time =now() WHERE id = #{id}")
    void updateAvatar(String avatarUrl, Integer id);

    // 更新密码
    @Update("UPDATE user SET password = #{md5String}, update_time =now() WHERE id = #{id}")
    void updatePwd(String md5String, Integer id);
}