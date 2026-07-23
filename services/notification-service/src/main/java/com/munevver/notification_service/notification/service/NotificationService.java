package com.munevver.notification_service.notification.service;

import com.munevver.notification_service.notification.dto.NotificationRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {

    private final List<NotificationRecord> notifications = Collections.synchronizedList(new ArrayList<>());
    private static final int MAX_NOTIFICATIONS = 50;

    public void addNotification(Long orderId, String message, String type) {
        NotificationRecord record = new NotificationRecord(
                UUID.randomUUID().toString(),
                orderId,
                message,
                type,
                LocalDateTime.now()
        );

        synchronized (notifications) {
            if (notifications.size() >= MAX_NOTIFICATIONS) {
                notifications.remove(notifications.size() - 1); // Remove oldest
            }
            notifications.add(0, record); // Add newest at start
        }

        log.info("New notification added: {} - {}", type, message);
    }

    public List<NotificationRecord> getAllNotifications() {
        synchronized (notifications) {
            return new ArrayList<>(notifications);
        }
    }
}
