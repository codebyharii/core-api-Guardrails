package com.guardrails.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.guardrails.service.NotificationService;

@Component
public class NotificationSweeper {

    private final NotificationService notificationService;

    public NotificationSweeper(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRate = 300000)
    public void sweep() {
        notificationService.sweepPendingNotifications();
    }
}
