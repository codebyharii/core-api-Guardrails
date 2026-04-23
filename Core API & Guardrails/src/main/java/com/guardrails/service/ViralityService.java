package com.guardrails.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ViralityService {

    private final StringRedisTemplate redisTemplate;

    public ViralityService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public long incrementVirality(Long postId, long delta) {
        String key = "post:" + postId + ":virality_score";
        Long score = redisTemplate.opsForValue().increment(key, delta);
        return score == null ? 0L : score;
    }

    public long currentViralityScore(Long postId) {
        String value = redisTemplate.opsForValue().get("post:" + postId + ":virality_score");
        if (value == null) {
            return 0L;
        }
        return Long.parseLong(value);
    }
}
