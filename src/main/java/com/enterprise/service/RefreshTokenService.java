package com.enterprise.service;


import com.enterprise.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    // Refresh Token在Redis中的key前缀
    private static final String REFRESH_TOKEN_KEY_PREFIX = "jwt:refresh:token:";

    /**
     * 存储Refresh Token到Redis（键：token，值：用户名，设置过期时间）
     */
    public void saveRefreshToken(String refreshToken, String username) {
        String redisKey = REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        // 设置与Refresh Token相同的过期时间（确保Redis自动清理过期Token）
        long expireSeconds = jwtTokenProvider.getRefreshExpirationInSeconds();
        redisTemplate.opsForValue().set(redisKey, username, expireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 验证Refresh Token是否有效（存在于Redis且未过期）
     */
    public boolean validateRefreshToken(String refreshToken) {
        String redisKey = REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        String username = redisTemplate.opsForValue().get(redisKey);
        // 1. Redis中存在该Token  2. JWT本身有效（未被篡改）
        return username != null && jwtTokenProvider.validateRefreshToken(refreshToken);
    }

    /**
     * 删除Refresh Token（刷新成功后使旧Token失效）
     */
    public void deleteRefreshToken(String refreshToken) {
        String redisKey = REFRESH_TOKEN_KEY_PREFIX + refreshToken;
        redisTemplate.delete(redisKey);
    }

}
