package com.example.springdemo.controller;

import com.example.springdemo.common.Result;
import com.example.springdemo.mapper.UserMapper;
import com.example.springdemo.pojo.User;
import com.example.springdemo.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 密码重置控制器 - 仅用于开发和测试环境
 */
@RestController
@RequestMapping("/api")
public class ResetPasswordController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 重置用户密码为初始密码
     * 默认密码：
     * - admin: admin123 (0192023a7bbd73250516f069df18b500)
     * - user1: 123456 (e10adc3949ba59abbe56e057f20f883e)
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String newPassword = params.get("password");
        
        System.out.println("收到密码重置请求 - 用户名: " + username);
        
        if (username == null || username.isEmpty()) {
            return Result.error("用户名不能为空");
        }
        
        User user = userMapper.findByUsername(username);
        if (user == null) {
            System.out.println("用户不存在: " + username);
            return Result.error("用户不存在");
        }
        
        System.out.println("找到用户 - ID: " + user.getUserId() + ", 用户名: " + user.getUsername());
        
        // 获取正确的MD5密码
        String password;
        if (newPassword != null && !newPassword.isEmpty()) {
            // 如果提供了新密码，则使用提供的密码
            password = Md5Util.encode(newPassword);
            System.out.println("使用提供的新密码: " + newPassword + " -> MD5: " + password);
        } else if ("admin".equals(username)) {
            password = "0192023a7bbd73250516f069df18b500"; // admin123
            System.out.println("重置管理员密码为默认值 (admin123)");
        } else {
            password = "e10adc3949ba59abbe56e057f20f883e"; // 123456
            System.out.println("重置普通用户密码为默认值 (123456)");
        }
        
        // 更新用户密码
        User updateUser = new User();
        updateUser.setUserId(user.getUserId());
        updateUser.setPassword(password);
        updateUser.setUpdatedAt(LocalDateTime.now());
        
        int result = userMapper.update(updateUser);
        
        if (result > 0) {
            System.out.println("密码重置成功 - 用户: " + username);
            return Result.success();
        } else {
            System.out.println("密码重置失败 - 用户: " + username);
            return Result.error("密码重置失败");
        }
    }
} 