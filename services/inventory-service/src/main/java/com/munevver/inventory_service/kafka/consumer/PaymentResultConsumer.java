package com.munevver.inventory_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munevver.inventory_service.inventory.service.InventoryService;
import com.munevver.inventory_service.kafka.dto.PaymentResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "payment-events",
            groupId = "inventory-service-group"
    )
    public void consumePaymentResult(String message) {
        log.info("Payment result event received from Kafka: {}", message);
        try {
            PaymentResultEvent event = objectMapper.readValue(message, PaymentResultEvent.class);
            log.info("Parsed payment result in inventory-service: {}", event);

            if ("FAILED".equals(event.status())) {
                log.info("Payment failed for order ID: {}. Initiating stock rollback.", event.orderId());
                inventoryService.rollbackStock(event.productId(), event.quantity(), event.orderId());
            } else {
                log.info("Payment was successful for order ID: {}. No action needed.", event.orderId());
            }
        } catch (Exception e) {
            log.error("Failed to process payment result event", e);
        }
    }
}
