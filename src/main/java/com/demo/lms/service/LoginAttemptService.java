package com.demo.lms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    // Changed RedisTemplate generic type from <String, Integer> to <String, Object>
    private final RedisTemplate<String, Object> redisTemplate;
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_TIME_SECONDS = 300; // 5 dakika

    public void loginFailed(String email) {
        String key = "login_attempt:" + email;
        // increment returns Long, which is compatible with Object
        Long attempts = redisTemplate.opsForValue().increment(key);
        if (attempts == null || attempts == 1) {
            redisTemplate.expire(key, BLOCK_TIME_SECONDS, TimeUnit.SECONDS);
        }
    }

    public boolean isBlocked(String email) {
        String key = "login_attempt:" + email;
        // get returns Object, cast to Integer
        Object val = redisTemplate.opsForValue().get(key);
        int attempts = val == null ? 0 : Integer.parseInt(val.toString());
        return attempts >= MAX_ATTEMPTS;
    }

    public void loginSucceeded(String email) {
        redisTemplate.delete("login_attempt:" + email);
    }
}
