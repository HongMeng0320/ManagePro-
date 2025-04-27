package com.example.springdemo.mapper;

import com.example.springdemo.pojo.Point;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PointMapper {
    
    /**
     * 根据用户ID和条件查询积分记录
     * @param userId 用户ID
     * @param pointType 积分类型（可为null）
     * @param startTime 开始时间（可为null）
     * @param endTime 结束时间（可为null）
     * @return 积分记录列表
     */
    @Select("<script>" +
            "SELECT * FROM points WHERE user_id = #{userId} " +
            "<if test='pointType != null'> AND point_type = #{pointType} </if>" +
            "<if test='startTime != null'> AND recorded_at &gt;= #{startTime} </if>" +
            "<if test='endTime != null'> AND recorded_at &lt;= #{endTime} </if>" +
            "ORDER BY recorded_at DESC" +
            "</script>")
    List<Point> findByUserIdAndConditions(@Param("userId") Integer userId, 
                                       @Param("pointType") String pointType,
                                       @Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据条件查询所有积分记录
     * @param pointType 积分类型（可为null）
     * @param startTime 开始时间（可为null）
     * @param endTime 结束时间（可为null）
     * @return 积分记录列表
     */
    @Select("<script>" +
            "SELECT * FROM points " +
            "<where>" +
            "<if test='pointType != null'> point_type = #{pointType} </if>" +
            "<if test='startTime != null'> AND recorded_at &gt;= #{startTime} </if>" +
            "<if test='endTime != null'> AND recorded_at &lt;= #{endTime} </if>" +
            "</where>" +
            "ORDER BY recorded_at DESC" +
            "</script>")
    List<Point> findByConditions(@Param("pointType") String pointType,
                              @Param("startTime") LocalDateTime startTime, 
                              @Param("endTime") LocalDateTime endTime);
    
    /**
     * 新增积分记录
     * @param point 积分记录
     * @return 影响行数
     */
    @Insert("INSERT INTO points(user_id, point_type, points, recorded_at) " +
            "VALUES(#{userId}, #{pointType}, #{points}, #{recordedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "pointId", keyColumn = "point_id")
    int insert(Point point);
    
    /**
     * 查询用户总积分
     * @param userId 用户ID
     * @return 总积分
     */
    @Select("SELECT COALESCE(SUM(points), 0) FROM points WHERE user_id = #{userId}")
    Integer getTotalPointsByUserId(Integer userId);
} 