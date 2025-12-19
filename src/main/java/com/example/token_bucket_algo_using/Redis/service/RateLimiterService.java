package com.example.token_bucket_algo_using.Redis.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> tokenBucketScript;

    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        tokenBucketScript = new DefaultRedisScript<>();
        tokenBucketScript.setLocation(
                new ClassPathResource("token_bucket.lua")
        );
        tokenBucketScript.setResultType(Long.class);
    }

    public boolean allowRequest(String key, int capacity, int refillRate) {

        long now = System.currentTimeMillis() / 1000;

        Long result = redisTemplate.execute(
                tokenBucketScript,
                List.of(key),
                String.valueOf(capacity),
                String.valueOf(refillRate),
                String.valueOf(now)
        );

        return result != null && result == 1;
    }
}
