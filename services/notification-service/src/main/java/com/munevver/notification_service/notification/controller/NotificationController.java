package com.munevver.notification_service.notification.controller;

import com.munevver.notification_service.notification.dto.NotificationRecord;
import com.munevver.notification_service.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationRecord> getNotifications() {
        return notificationService.getAllNotifications();
    }
}
