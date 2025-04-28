package com.example.springdemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtAuthInterceptor jwtAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加JWT认证拦截器
        System.out.println("注册JWT拦截器，排除登录和密码重置路径");
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**")  // 拦截所有/api/**请求
                .excludePathPatterns(
                        "/api/login",        // 排除登录接口
                        "/api/register",     // 排除注册接口
                        "/api/reset-password" // 排除密码重置接口
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 添加CORS配置
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
} 