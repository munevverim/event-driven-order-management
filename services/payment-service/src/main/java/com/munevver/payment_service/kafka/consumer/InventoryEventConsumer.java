package com.munevver.payment_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munevver.payment_service.kafka.dto.InventoryResultEvent;
import com.munevver.payment_service.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "inventory-events",
            groupId = "payment-service-group"
    )
    public void consumeInventoryEvent(String message) {
        log.info("Inventory check result event received in payment-service: {}", message);
        try {
            InventoryResultEvent event = objectMapper.readValue(message, InventoryResultEvent.class);
            log.info("Parsed inventory event: {}", event);

            if ("RESERVED".equals(event.status())) {
                log.info("Stock reserved for order ID: {}. Starting payment check...", event.orderId());
                paymentService.processPayment(event);
            } else {
                log.info("Stock reservation failed for order ID: {}. Skipping payment check.", event.orderId());
            }
        } catch (Exception e) {
            log.error("Failed to process inventory event in payment-service", e);
        }
    }
}
