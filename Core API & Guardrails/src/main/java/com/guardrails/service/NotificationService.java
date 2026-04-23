package com.guardrails.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final String PENDING_USERS_SET = "notifications:pending_users";
    private static final long NOTIFICATION_COOLDOWN_SECONDS = 15 * 60L;

    private final StringRedisTemplate redisTemplate;

    public NotificationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void recordBotInteraction(Long userId, String notificationText) {
        if (userId == null) {
            return;
        }

        String cooldownKey = "notification:user:" + userId + ":cooldown";
        String pendingListKey = "user:" + userId + ":pending_notifs";

        Boolean cooldownExists = redisTemplate.hasKey(cooldownKey);
        if (Boolean.TRUE.equals(cooldownExists)) {
            redisTemplate.opsForList().rightPush(pendingListKey, notificationText);
            redisTemplate.opsForSet().add(PENDING_USERS_SET, String.valueOf(userId));
            return;
        }

        log.info("Push Notification Sent to User {}", userId);
        redisTemplate.opsForValue().set(cooldownKey, "1", java.time.Duration.ofSeconds(NOTIFICATION_COOLDOWN_SECONDS));
    }

    public List<String> sweepPendingNotifications() {
        Set<String> userIds = redisTemplate.opsForSet().members(PENDING_USERS_SET);
        List<String> summaries = new ArrayList<>();

        if (userIds == null || userIds.isEmpty()) {
            return summaries;
        }

        for (String userId : userIds) {
            String pendingListKey = "user:" + userId + ":pending_notifs";
            List<String> notifications = redisTemplate.opsForList().range(pendingListKey, 0, -1);
            if (notifications == null || notifications.isEmpty()) {
                redisTemplate.opsForSet().remove(PENDING_USERS_SET, userId);
                continue;
            }

            String lead = extractLeadBotName(notifications.get(0));
            int total = notifications.size();
            String summary = "Summarized Push Notification: " + lead + " and " + Math.max(0, total - 1) + " others interacted with your posts.";
            log.info(summary);
            summaries.add(summary);

            redisTemplate.delete(pendingListKey);
            redisTemplate.opsForSet().remove(PENDING_USERS_SET, userId);
        }

        return summaries;
    }

    private String extractLeadBotName(String notification) {
        if (notification == null || notification.isBlank()) {
            return "A bot";
        }
        if (notification.startsWith("Bot ")) {
            int index = notification.indexOf(' ' , 4);
            if (index > 0) {
                return notification.substring(0, index);
            }
        }
        int replyIndex = notification.indexOf(" replied");
        if (replyIndex > 0) {
            return notification.substring(0, replyIndex);
        }
        return "A bot";
    }
}
