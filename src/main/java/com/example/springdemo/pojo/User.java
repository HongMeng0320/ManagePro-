package com.example.springdemo.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer userId;    // 用户ID
    private String username;   // 用户名
    private String password;   // 密码（加密）
    private String email;      // 邮箱
    private String phone;      // 电话
    private String role;       // 角色（admin/user）
    private Integer status;    // 状态（1-启用/0-禁用）
    private LocalDateTime createdAt;  // 创建时间
    private LocalDateTime updatedAt;  // 更新时间
} 