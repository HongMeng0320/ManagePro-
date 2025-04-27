package com.example.springdemo.service;

import com.example.springdemo.pojo.Point;
import com.github.pagehelper.PageInfo;

import java.time.LocalDateTime;

public interface PointService {
    
    /**
     * 查询用户积分记录
     * @param userId 用户ID
     * @param pointType 积分类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageInfo<Point> getUserPoints(Integer userId, String pointType, 
                                 LocalDateTime startTime, LocalDateTime endTime,
                                 Integer pageNum, Integer pageSize);
    
    /**
     * 查询所有积分记录
     * @param pointType 积分类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageInfo<Point> getAllPoints(String pointType, 
                               LocalDateTime startTime, LocalDateTime endTime,
                               Integer pageNum, Integer pageSize);
    
    /**
     * 添加积分记录
     * @param point 积分记录
     * @return 是否成功
     */
    boolean addPoint(Point point);
    
    /**
     * 获取用户总积分
     * @param userId 用户ID
     * @return 总积分
     */
    Integer getUserTotalPoints(Integer userId);
} 