package com.example.springdemo.service.impl;

import com.example.springdemo.mapper.UserMapper;
import com.example.springdemo.pojo.User;
import com.example.springdemo.service.UserService;
import com.example.springdemo.utils.Md5Util;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// @Autowired：自动注入依赖对象（字段/构造函数注入） @Service：标记服务层组件（业务逻辑层）
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        // 根据用户名查询用户
        User user = userMapper.findByUsername(username);
        
        // 用户不存在或密码不匹配
        if (user == null || !Md5Util.checkPassword(password, user.getPassword())) {
            return null;
        }
        
        // 用户被禁用
        if (user.getStatus() == 0) {
            return null;
        }
        
        // 登录成功，不返回密码
        user.setPassword(null);
        return user;
    }

    @Override
    public boolean register(User user) {
        // 检查用户名是否存在
        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            return false;
        }
        
        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        // 设置默认状态为启用
        user.setStatus(1);
        // 设置默认角色为普通用户
        if (user.getRole() == null) {
            user.setRole("user");
        }
        
        // 密码加密
        user.setPassword(Md5Util.getMD5String(user.getPassword()));
        
        return userMapper.insert(user) > 0;
    }

    @Override
    public User findById(Integer userId) {
        User user = userMapper.findById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public PageInfo<User> findByPage(String username, Integer status, Integer pageNum, Integer pageSize) {
        // 分页查询
        PageHelper.startPage(pageNum, pageSize);
        List<User> users;
        
        if (username != null || status != null) {
            users = userMapper.findByCondition(username, status);
        } else {
            users = userMapper.findAll();
        }
        
        // 清空密码
        users.forEach(user -> user.setPassword(null));
        
        return new PageInfo<>(users);
    }

    @Override
    public List<User> findAll() {
        List<User> users = userMapper.findAll();
        // 清空密码
        users.forEach(user -> user.setPassword(null));
        return users;
    }

    @Override
    public boolean update(User user) {
        // 设置更新时间
        user.setUpdatedAt(LocalDateTime.now());
        
        // 如果密码不为空，进行加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(Md5Util.getMD5String(user.getPassword()));
        }
        
        return userMapper.update(user) > 0;
    }

    @Override
    public boolean updatePassword(Integer userId, String oldPassword, String newPassword) {
        // 查询用户
        User user = userMapper.findById(userId);
        if (user == null) {
            return false;
        }
        
        // 校验旧密码
        if (!Md5Util.checkPassword(oldPassword, user.getPassword())) {
            return false;
        }
        
        // 更新密码
        User updateUser = new User();
        updateUser.setUserId(userId);
        updateUser.setPassword(Md5Util.getMD5String(newPassword));
        updateUser.setUpdatedAt(LocalDateTime.now());
        
        return userMapper.update(updateUser) > 0;
    }

    @Override
    public boolean updateStatus(Integer userId, Integer status) {
        return userMapper.updateStatus(userId, status, LocalDateTime.now()) > 0;
    }
}
