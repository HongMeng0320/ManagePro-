package com.example.springdemo.mapper;

import com.example.springdemo.pojo.PointLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PointLogMapper {
    
    /**
     * 添加积分变动日志
     * @param pointLog 积分日志信息
     * @return 影响行数
     */
    @Insert("INSERT INTO point_logs(user_id, point_id, operation_type, points_before, points_change, " +
            "points_after, operation_reason, operation_time, operator_id, operator_name) " +
            "VALUES(#{userId}, #{pointId}, #{operationType}, #{pointsBefore}, #{pointsChange}, " +
            "#{pointsAfter}, #{operationReason}, #{operationTime}, #{operatorId}, #{operatorName})")
    int insert(PointLog pointLog);
    
    /**
     * 根据用户ID查询积分变动日志
     * @param userId 用户ID
     * @param startTime 开始时间（可为null）
     * @param endTime 结束时间（可为null）
     * @return 积分日志列表
     */
    @Select("<script>" +
            "SELECT * FROM point_logs WHERE user_id = #{userId} " +
            "<if test='startTime != null'> AND operation_time &gt;= #{startTime} </if>" +
            "<if test='endTime != null'> AND operation_time &lt;= #{endTime} </if>" +
            "ORDER BY operation_time DESC" +
            "</script>")
    List<PointLog> findByUserId(@Param("userId") Integer userId, 
                             @Param("startTime") LocalDateTime startTime, 
                             @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询所有积分变动日志
     * @param startTime 开始时间（可为null）
     * @param endTime 结束时间（可为null）
     * @return 积分日志列表
     */
    @Select("<script>" +
            "SELECT * FROM point_logs " +
            "<where>" +
            "<if test='startTime != null'> operation_time &gt;= #{startTime} </if>" +
            "<if test='endTime != null'> AND operation_time &lt;= #{endTime} </if>" +
            "</where>" +
            "ORDER BY operation_time DESC" +
            "</script>")
    List<PointLog> findAll(@Param("startTime") LocalDateTime startTime, 
                        @Param("endTime") LocalDateTime endTime);
} 