package com.munevver.notification_service.notification.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.munevver.notification_service.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "order-service.public.outbox",
            groupId = "notification-service-group"
    )
    public void consumeOrderEvent(String message) {
        log.info("Received order event for notifications: {}", message);
        try {
            JsonNode node = objectMapper.readTree(message);
            JsonNode afterNode = node.path("payload").path("after");
            if (!afterNode.isMissingNode() && "OrderCreated".equals(afterNode.path("eventType").asText())) {
                long orderId = afterNode.path("aggregateId").asLong();
                notificationService.addNotification(orderId, "Sipariş #" + orderId + " alındı. Envanter kontrol ediliyor...", "INFO");
            }
        } catch (Exception e) {
            log.error("Failed to process order event for notification", e);
        }
    }

    @KafkaListener(
            topics = "inventory-events",
            groupId = "notification-service-group"
    )
    public void consumeInventoryEvent(String message) {
        log.info("Received inventory event for notifications: {}", message);
        try {
            JsonNode event = objectMapper.readTree(message);
            long orderId = event.path("orderId").asLong();
            String status = event.path("status").asText();
            String reason = event.path("reason").asText();

            if ("FAILED".equals(status)) {
                notificationService.addNotification(orderId, "Sipariş #" + orderId + " iptal edildi. Gerekçe: " + reason, "ERROR");
            } else if ("RESERVED".equals(status)) {
                notificationService.addNotification(orderId, "Sipariş #" + orderId + " envanteri rezerve edildi. Ödeme bekleniyor.", "INFO");
            }
        } catch (Exception e) {
            log.error("Failed to process inventory event for notification", e);
        }
    }

    @KafkaListener(
            topics = "payment-events",
            groupId = "notification-service-group"
    )
    public void consumePaymentEvent(String message) {
        log.info("Received payment event for notifications: {}", message);
        try {
            JsonNode event = objectMapper.readTree(message);
            long orderId = event.path("orderId").asLong();
            String status = event.path("status").asText();
            String reason = event.path("reason").asText();

            if ("SUCCESS".equals(status)) {
                notificationService.addNotification(orderId, "Sipariş #" + orderId + " ödemesi başarıyla alındı. Sipariş onaylandı!", "SUCCESS");
            } else if ("FAILED".equals(status)) {
                notificationService.addNotification(orderId, "Sipariş #" + orderId + " ödemesi alınamadı. Gerekçe: " + reason, "WARNING");
            }
        } catch (Exception e) {
            log.error("Failed to process payment event for notification", e);
        }
    }
}
