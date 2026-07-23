package com.munevver.notification_service.notification.dto;

import java.time.LocalDateTime;

public record NotificationRecord(
        String id,
        Long orderId,
        String message,
        String type, // "INFO", "SUCCESS", "WARNING", "ERROR"
        LocalDateTime createdAt
) {
}
