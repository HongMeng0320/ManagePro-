package com.example.springdemo.controller;

import com.example.springdemo.common.Result;
import com.example.springdemo.pojo.User;
import com.example.springdemo.service.PointService;
import com.example.springdemo.service.UserService;
import com.example.springdemo.utils.JwtUtil;
import com.example.springdemo.utils.ThreadLocalUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PointService pointService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        // 登录验证
        User user = userService.login(username, password);
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        
        // 生成JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        String token = jwtUtil.generateToken(claims);
        
        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        
        return Result.success(result);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/user/info")
    public Result<User> getUserInfo() {
        Map<String, Object> claims = ThreadLocalUtil.get();
        Integer userId = (Integer) claims.get("userId");
        User user = userService.findById(userId);
        
        // 获取积分信息
        Integer totalPoints = pointService.getUserTotalPoints(userId);
        // 构造返回结果（用户信息 + 积分）
        
        return Result.success(user);
    }

    /**
     * 获取所有用户列表（分页）
     * 仅管理员可访问
     */
    @GetMapping("/admin/users")
    public Result<PageInfo<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        // 权限检查
        Map<String, Object> claims = ThreadLocalUtil.get();
        String role = (String) claims.get("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "权限不足");
        }
        
        PageInfo<User> pageInfo = userService.findByPage(null, null, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    /**
     * 获取用户详情
     * 仅管理员可访问
     */
    @GetMapping("/admin/users/{id}")
    public Result<Map<String, Object>> getUserDetail(@PathVariable("id") Integer userId) {
        // 权限检查
        Map<String, Object> claims = ThreadLocalUtil.get();
        String role = (String) claims.get("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "权限不足");
        }
        
        User user = userService.findById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 获取用户积分信息
        Integer totalPoints = pointService.getUserTotalPoints(userId);
        
        // 构造返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("totalPoints", totalPoints);
        
        return Result.success(result);
    }

    /**
     * 新增用户
     * 仅管理员可访问
     */
    @PostMapping("/admin/users")
    public Result<Void> addUser(@RequestBody User user) {
        // 权限检查
        Map<String, Object> claims = ThreadLocalUtil.get();
        String role = (String) claims.get("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "权限不足");
        }
        
        boolean success = userService.register(user);
        if (!success) {
            return Result.error("用户名已存在");
        }
        
        return Result.success();
    }

    /**
     * 更新用户
     * 仅管理员可访问
     */
    @PutMapping("/admin/users/{id}")
    public Result<Void> updateUser(@PathVariable("id") Integer userId, @RequestBody User user) {
        // 权限检查
        Map<String, Object> claims = ThreadLocalUtil.get();
        String role = (String) claims.get("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "权限不足");
        }
        
        // 设置用户ID
        user.setUserId(userId);
        
        boolean success = userService.update(user);
        if (!success) {
            return Result.error("更新失败");
        }
        
        return Result.success();
    }

    /**
     * 更新用户状态（启用/禁用）
     * 仅管理员可访问
     */
    @PatchMapping("/admin/users/{id}/status")
    public Result<Void> updateUserStatus(
            @PathVariable("id") Integer userId,
            @RequestBody Map<String, Integer> params) {
        // 权限检查
        Map<String, Object> claims = ThreadLocalUtil.get();
        String role = (String) claims.get("role");
        if (!"admin".equals(role)) {
            return Result.error(403, "权限不足");
        }
        
        Integer status = params.get("status");
        if (status == null || (status != 0 && status != 1)) {
            return Result.error("状态参数错误");
        }
        
        boolean success = userService.updateStatus(userId, status);
        if (!success) {
            return Result.error("更新失败");
        }
        
        return Result.success();
    }
}
