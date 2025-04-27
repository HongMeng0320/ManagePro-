package com.example.springdemo.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PointLog {
    private Integer logId;            // 日志ID
    private Integer userId;           // 用户ID
    private Integer pointId;          // 积分记录ID
    private String operationType;     // 操作类型：add-增加积分，deduct-扣减积分，modify-修改积分
    private Integer pointsBefore;     // 变动前的积分余额
    private Integer pointsChange;     // 变动的积分数量
    private Integer pointsAfter;      // 变动后的积分余额
    private String operationReason;   // 操作原因
    private LocalDateTime operationTime; // 操作时间
    private Integer operatorId;       // 操作人ID
    private String operatorName;      // 操作人用户名
} 