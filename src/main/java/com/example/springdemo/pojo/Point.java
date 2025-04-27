package com.example.springdemo.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Point {
    private Integer pointId;       // 积分ID
    private Integer userId;        // 用户ID
    private String pointType;      // 积分类型（reward/consume - 奖励/消费）
    private Integer points;        // 积分值
    private LocalDateTime recordedAt;  // 记录时间
} 