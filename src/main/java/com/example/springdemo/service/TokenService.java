package com.example.springdemo.service;

import java.util.Map;

/**
 * JWT令牌管理服务
 */
public interface TokenService {
    
    /**
     * 生成令牌
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT令牌
     */
    String generateToken(Integer userId, String username);
    
    /**
     * 验证令牌有效性
     * @param token JWT令牌
     * @return 令牌中的用户信息，验证失败返回null
     */
    Map<String, Object> validateToken(String token);
    
    /**
     * 使令牌无效
     * @param userId 用户ID
     */
    void invalidateUserTokens(Integer userId);
    
    /**
     * 使指定令牌无效
     * @param token JWT令牌
     */
    void invalidateToken(String token);
} 