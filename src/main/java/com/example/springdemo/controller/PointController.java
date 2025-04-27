package com.example.springdemo.controller;

import com.example.springdemo.common.Result;
import com.example.springdemo.pojo.Point;
import com.example.springdemo.service.PointService;
import com.example.springdemo.utils.ThreadLocalUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/points")
public class PointController {

    @Autowired
    private PointService pointService;

    /**
     * 查询积分明细
     * 根据用户角色判断：管理员可查看所有积分，普通用户只能查看自己的积分
     */
    @GetMapping
    public Result<PageInfo<Point>> getPoints(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String pointType,
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
            // 管理员可查看所有用户积分或指定用户积分
            if (userId == null) {
                // 查询所有用户积分
                PageInfo<Point> pageInfo = pointService.getAllPoints(
                    pointType, startTime, endTime, pageNum, pageSize);
                return Result.success(pageInfo);
            } else {
                // 查询指定用户积分
                PageInfo<Point> pageInfo = pointService.getUserPoints(
                    userId, pointType, startTime, endTime, pageNum, pageSize);
                return Result.success(pageInfo);
            }
        } else {
            // 普通用户只能查看自己的积分
            if (userId != null && !userId.equals(currentUserId)) {
                return Result.error(403, "权限不足，只能查看自己的积分");
            }
            
            PageInfo<Point> pageInfo = pointService.getUserPoints(
                currentUserId, pointType, startTime, endTime, pageNum, pageSize);
            return Result.success(pageInfo);
        }
    }

    /**
     * 添加积分记录（仅管理员可操作）
     */
    @PostMapping
    public Result<Void> addPoint(@RequestBody Point point) {
        // 权限检查
        Map<String, Object> claims = ThreadLocalUtil.get();
        String role = (String) claims.get("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "权限不足");
        }
        
        // 设置记录时间
        if (point.getRecordedAt() == null) {
            point.setRecordedAt(LocalDateTime.now());
        }
        
        boolean success = pointService.addPoint(point);
        if (!success) {
            return Result.error("添加积分记录失败");
        }
        
        return Result.success();
    }

    /**
     * 获取用户总积分
     */
    @GetMapping("/total")
    public Result<Integer> getTotalPoints(@RequestParam(required = false) Integer userId) {
        // 获取当前用户信息
        Map<String, Object> claims = ThreadLocalUtil.get();
        String role = (String) claims.get("role");
        Integer currentUserId = (Integer) claims.get("userId");
        
        // 权限判断
        if (!"admin".equals(role) && (userId != null && !userId.equals(currentUserId))) {
            return Result.error(403, "权限不足，只能查看自己的积分");
        }
        
        // 如果未指定用户ID，则查询当前用户的积分
        if (userId == null) {
            userId = currentUserId;
        }
        
        Integer totalPoints = pointService.getUserTotalPoints(userId);
        return Result.success(totalPoints);
    }
} 