package com.example.springdemo.service;

import com.example.springdemo.pojo.User;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface UserService {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回用户信息，失败返回null
     */
    User login(String username, String password);
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册成功返回true，失败返回false
     */
    boolean register(User user);
    
    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    User findById(Integer userId);
    
    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新成功返回true，失败返回false
     */
    boolean update(User user);
    
    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改成功返回true，失败返回false
     */
    boolean updatePassword(Integer userId, String oldPassword, String newPassword);
    
    /**
     * 修改用户状态
     * @param userId 用户ID
     * @param status 状态（1-启用/0-禁用）
     * @return 修改成功返回true，失败返回false
     */
    boolean updateStatus(Integer userId, Integer status);
    
    /**
     * 分页查询用户列表
     * @param username 用户名（可选）
     * @param status 状态（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageInfo<User> findByPage(String username, Integer status, Integer pageNum, Integer pageSize);
    
    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    List<User> findAll();
} 