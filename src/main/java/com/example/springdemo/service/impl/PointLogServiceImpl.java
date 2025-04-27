package com.example.springdemo.service.impl;

import com.example.springdemo.mapper.PointLogMapper;
import com.example.springdemo.pojo.PointLog;
import com.example.springdemo.service.PointLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PointLogServiceImpl implements PointLogService {

    @Autowired
    private PointLogMapper pointLogMapper;

    @Override
    public boolean logPointChange(Integer userId, Integer pointId, String operationType,
                                Integer pointsBefore, Integer pointsChange, Integer pointsAfter,
                                String operationReason, Integer operatorId, String operatorName) {
        
        PointLog pointLog = new PointLog();
        pointLog.setUserId(userId);
        pointLog.setPointId(pointId);
        pointLog.setOperationType(operationType);
        pointLog.setPointsBefore(pointsBefore);
        pointLog.setPointsChange(pointsChange);
        pointLog.setPointsAfter(pointsAfter);
        pointLog.setOperationReason(operationReason);
        pointLog.setOperationTime(LocalDateTime.now());
        pointLog.setOperatorId(operatorId);
        pointLog.setOperatorName(operatorName);
        
        return pointLogMapper.insert(pointLog) > 0;
    }

    @Override
    public PageInfo<PointLog> getUserPointLogs(Integer userId, LocalDateTime startTime, 
                                            LocalDateTime endTime, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PointLog> logs = pointLogMapper.findByUserId(userId, startTime, endTime);
        return new PageInfo<>(logs);
    }

    @Override
    public PageInfo<PointLog> getAllPointLogs(LocalDateTime startTime, 
                                           LocalDateTime endTime, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PointLog> logs = pointLogMapper.findAll(startTime, endTime);
        return new PageInfo<>(logs);
    }
} 