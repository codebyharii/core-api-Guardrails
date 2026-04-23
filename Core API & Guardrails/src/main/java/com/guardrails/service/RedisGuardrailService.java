package com.guardrails.service;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.guardrails.exception.GuardrailViolationException;

@Service
public class RedisGuardrailService {

    private static final long BOT_REPLY_CAP = 100L;
    private static final long COOLDOWN_MILLIS = 10 * 60 * 1000L;
    private static final DefaultRedisScript<Long> BOT_GUARD_SCRIPT = new DefaultRedisScript<>("""
            local cooldownKey = KEYS[1]
            local botCountKey = KEYS[2]
            local cap = tonumber(ARGV[1])
            local cooldownMillis = tonumber(ARGV[2])

            if cooldownKey ~= '' and redis.call('EXISTS', cooldownKey) == 1 then
                return -1
            end

            local current = tonumber(redis.call('GET', botCountKey) or '0')
            if current >= cap then
                return -2
            end

            redis.call('INCR', botCountKey)
            if cooldownKey ~= '' then
                redis.call('SET', cooldownKey, '1', 'PX', cooldownMillis)
            end

            return current + 1
            """, Long.class);

    private final StringRedisTemplate redisTemplate;

    public RedisGuardrailService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public long reserveBotInteraction(Long postId, Long botId, Long humanId) {
        String botCountKey = "post:" + postId + ":bot_count";
        String cooldownKey = humanId == null ? "" : "cooldown:bot_" + botId + ":human_" + humanId;
        Long result = redisTemplate.execute(
                BOT_GUARD_SCRIPT,
                List.of(cooldownKey, botCountKey),
                String.valueOf(BOT_REPLY_CAP),
                String.valueOf(COOLDOWN_MILLIS)
        );

        if (result == null) {
            throw new GuardrailViolationException("Unable to evaluate bot guardrails");
        }
        if (result == -1L) {
            throw new GuardrailViolationException("Bot cooldown active for this human");
        }
        if (result == -2L) {
            throw new GuardrailViolationException("Post has reached the bot reply cap");
        }
        return result;
    }
}
