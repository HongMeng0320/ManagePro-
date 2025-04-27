package com.example.springdemo.service;

import com.example.springdemo.pojo.PointLog;
import com.github.pagehelper.PageInfo;

import java.time.LocalDateTime;

public interface PointLogService {
    
    /**
     * 记录积分变动
     * @param userId 用户ID
     * @param pointId 积分记录ID
     * @param operationType 操作类型
     * @param pointsBefore 变动前积分
     * @param pointsChange 变动积分
     * @param pointsAfter 变动后积分
     * @param operationReason 操作原因
     * @param operatorId 操作人ID
     * @param operatorName 操作人名称
     * @return 是否成功
     */
    boolean logPointChange(Integer userId, Integer pointId, String operationType,
                        Integer pointsBefore, Integer pointsChange, Integer pointsAfter,
                        String operationReason, Integer operatorId, String operatorName);
    
    /**
     * 查询用户积分变动日志
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageInfo<PointLog> getUserPointLogs(Integer userId, LocalDateTime startTime, 
                                     LocalDateTime endTime, Integer pageNum, Integer pageSize);
    
    /**
     * 查询所有积分变动日志
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageInfo<PointLog> getAllPointLogs(LocalDateTime startTime, 
                                    LocalDateTime endTime, Integer pageNum, Integer pageSize);
} 