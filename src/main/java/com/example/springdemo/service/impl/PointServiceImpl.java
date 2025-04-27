package com.example.springdemo.service.impl;

import com.example.springdemo.mapper.PointMapper;
import com.example.springdemo.pojo.Point;
import com.example.springdemo.service.PointLogService;
import com.example.springdemo.service.PointService;
import com.example.springdemo.utils.ThreadLocalUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PointServiceImpl implements PointService {

    @Autowired
    private PointMapper pointMapper;
    
    @Autowired
    private PointLogService pointLogService;

    @Override
    public PageInfo<Point> getUserPoints(Integer userId, String pointType, 
                                       LocalDateTime startTime, LocalDateTime endTime, 
                                       Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Point> points = pointMapper.findByUserIdAndConditions(userId, pointType, startTime, endTime);
        return new PageInfo<>(points);
    }

    @Override
    public PageInfo<Point> getAllPoints(String pointType, 
                                      LocalDateTime startTime, LocalDateTime endTime, 
                                      Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Point> points = pointMapper.findByConditions(pointType, startTime, endTime);
        return new PageInfo<>(points);
    }

    @Override
    @Transactional
    public boolean addPoint(Point point) {
        // 设置记录时间
        if (point.getRecordedAt() == null) {
            point.setRecordedAt(LocalDateTime.now());
        }
        
        // 获取当前操作人信息
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer operatorId = (Integer) claims.get("userId");
        String operatorName = (String) claims.get("username");
        
        // 获取用户当前总积分
        Integer currentPoints = pointMapper.getTotalPointsByUserId(point.getUserId());
        if (currentPoints == null) {
            currentPoints = 0;
        }
        
        // 添加积分记录
        boolean result = pointMapper.insert(point) > 0;
        
        if (result) {
            // 计算变动后积分
            Integer pointsAfter = currentPoints + point.getPoints();
            
            // 确定操作类型
            String operationType = point.getPoints() > 0 ? "add" : "deduct";
            
            // 确保有积分记录ID
            if (point.getPointId() == null) {
                throw new RuntimeException("积分记录ID不能为空");
            }
            
            // 记录积分变动日志
            pointLogService.logPointChange(
                point.getUserId(),
                point.getPointId(),
                operationType,
                currentPoints,
                point.getPoints(),
                pointsAfter,
                "积分" + (point.getPoints() > 0 ? "奖励" : "消费"),
                operatorId,
                operatorName
            );
        }
        
        return result;
    }

    @Override
    public Integer getUserTotalPoints(Integer userId) {
        return pointMapper.getTotalPointsByUserId(userId);
    }
} 