package com.example.springdemo.config;

import com.example.springdemo.service.TokenService;
import com.example.springdemo.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的token
        String token = request.getHeader("Authorization");
        
        // 如果token为空，返回未授权状态码
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }
        
        // 移除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        try {
            // 验证token
            Map<String, Object> claims = tokenService.validateToken(token);
            
            if (claims == null) {
                // token无效，返回未授权状态码
                response.setStatus(401);
                return false;
            }
            
            // 将用户信息存入ThreadLocal，供后续使用
            ThreadLocalUtil.set(claims);
            
            // 放行
            return true;
        } catch (Exception e) {
            // token解析失败，返回未授权状态码
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成后，清除ThreadLocal中的数据，防止内存泄漏
        ThreadLocalUtil.remove();
    }
} 