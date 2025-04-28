package com.example.springdemo.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.springdemo.pojo.User;
import com.example.springdemo.service.TokenService;
import com.example.springdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private long expiration;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private UserService userService;
    
    // Redis中存储失效令牌的前缀
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";
    
    // Redis中存储用户令牌的前缀
    private static final String USER_TOKEN_PREFIX = "jwt:user:";
    
    @Override
    public String generateToken(Integer userId, String username) {
        // 创建JWT令牌ID (JTI)
        String tokenId = UUID.randomUUID().toString();
        
        // 获取用户信息
        User user = userService.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 创建用户声明信息
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", user.getRole()); // 添加用户角色
        
        // 生成JWT令牌
        String token = JWT.create()
                .withJWTId(tokenId)
                .withClaim("claims", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC256(secret));
        
        // 将令牌存储到Redis，以便后续可以使其失效
        String userTokenKey = USER_TOKEN_PREFIX + userId;
        redisTemplate.opsForHash().put(userTokenKey, tokenId, token);
        redisTemplate.expire(userTokenKey, expiration, TimeUnit.MILLISECONDS);
        
        return token;
    }
    
    @Override
    public Map<String, Object> validateToken(String token) {
        try {
            // 检查令牌是否在黑名单中
            String jti = JWT.decode(token).getId();
            if (Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + jti))) {
                return null;
            }
            
            // 验证令牌
            return JWT.require(Algorithm.HMAC256(secret))
                    .build()
                    .verify(token)
                    .getClaim("claims")
                    .asMap();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
    
    @Override
    public void invalidateUserTokens(Integer userId) {
        // 获取用户所有的令牌
        String userTokenKey = USER_TOKEN_PREFIX + userId;
        Map<Object, Object> userTokens = redisTemplate.opsForHash().entries(userTokenKey);
        
        // 将所有令牌加入黑名单
        for (Map.Entry<Object, Object> entry : userTokens.entrySet()) {
            String tokenId = (String) entry.getKey();
            redisTemplate.opsForValue().set(BLACKLIST_PREFIX + tokenId, true, expiration, TimeUnit.MILLISECONDS);
        }
        
        // 删除用户的令牌集合
        redisTemplate.delete(userTokenKey);
    }
    
    @Override
    public void invalidateToken(String token) {
        try {
            // 将令牌加入黑名单
            String jti = JWT.decode(token).getId();
            Date expiry = JWT.decode(token).getExpiresAt();
            
            // 计算剩余有效期
            long remainingTime = expiry.getTime() - System.currentTimeMillis();
            if (remainingTime > 0) {
                redisTemplate.opsForValue().set(BLACKLIST_PREFIX + jti, true, remainingTime, TimeUnit.MILLISECONDS);
            }
            
            // 从用户的令牌集合中移除
            Map<String, Object> claims = JWT.decode(token).getClaim("claims").asMap();
            Integer userId = (Integer) claims.get("userId");
            if (userId != null) {
                String userTokenKey = USER_TOKEN_PREFIX + userId;
                redisTemplate.opsForHash().delete(userTokenKey, jti);
            }
        } catch (Exception e) {
            // 无效令牌，忽略
        }
    }
} 