package com.munevver.order_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munevver.order_service.kafka.dto.InventoryResultEvent;
import com.munevver.order_service.order.OrderService;
import com.munevver.order_service.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryResultConsumer {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "inventory-events",
            groupId = "order-service-group"
    )
    public void consumeInventoryResult(String message) {
        log.info("Inventory result event received from Kafka: {}", message);
        try {
            InventoryResultEvent result = objectMapper.readValue(message, InventoryResultEvent.class);
            log.info("Parsed inventory result: {}", result);

            if ("RESERVED".equals(result.status())) {
                log.info("Inventory reserved for order ID: {}. Waiting for payment...", result.orderId());
            } else if ("FAILED".equals(result.status())) {
                orderService.updateOrderStatus(result.orderId(), OrderStatus.REJECTED);
                log.info("Order status updated to REJECTED for order ID: {}", result.orderId());
            }
        } catch (Exception e) {
            log.error("Failed to process inventory result event", e);
        }
    }
}
