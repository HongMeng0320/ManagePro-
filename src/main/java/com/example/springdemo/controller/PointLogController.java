package com.example.springdemo.controller;

import com.example.springdemo.common.Result;
import com.example.springdemo.pojo.PointLog;
import com.example.springdemo.service.PointLogService;
import com.example.springdemo.utils.ThreadLocalUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/pointlogs")
public class PointLogController {

    @Autowired
    private PointLogService pointLogService;

    /**
     * 查询积分变动日志
     * 根据用户角色判断：管理员可查看所有日志，普通用户只能查看自己的日志
     */
    @GetMapping
    public Result<PageInfo<PointLog>> getPointLogs(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        // 获取当前用户信息
        Map<String, Object> claims = ThreadLocalUtil.get();
        String role = (String) claims.get("role");
        Integer currentUserId = (Integer) claims.get("userId");
        
        // 权限判断
        if ("admin".equals(role)) {
            // 管理员可查看所有用户积分日志或指定用户积分日志
            if (userId == null) {
                // 查询所有用户积分日志
                PageInfo<PointLog> pageInfo = pointLogService.getAllPointLogs(
                    startTime, endTime, pageNum, pageSize);
                return Result.success(pageInfo);
            } else {
                // 查询指定用户积分日志
                PageInfo<PointLog> pageInfo = pointLogService.getUserPointLogs(
                    userId, startTime, endTime, pageNum, pageSize);
                return Result.success(pageInfo);
            }
        } else {
            // 普通用户只能查看自己的积分日志
            if (userId != null && !userId.equals(currentUserId)) {
                return Result.error(403, "权限不足，只能查看自己的积分日志");
            }
            
            PageInfo<PointLog> pageInfo = pointLogService.getUserPointLogs(
                currentUserId, startTime, endTime, pageNum, pageSize);
            return Result.success(pageInfo);
        }
    }
} 