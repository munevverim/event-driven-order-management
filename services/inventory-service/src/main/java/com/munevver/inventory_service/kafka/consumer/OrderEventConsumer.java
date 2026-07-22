package com.munevver.inventory_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munevver.inventory_service.inventory.service.InventoryService;
import com.munevver.inventory_service.kafka.dto.DebeziumOutboxEvent;
import com.munevver.inventory_service.kafka.dto.OrderCreatedPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "order-service.public.outbox",
            groupId = "inventory-service-group"
    )
    public void consumeOrderEvent(String message) throws Exception {
        log.info("Order event received from Kafka: {}", message);
        // Debezium CDC zarfını çözümlüyoruz
        DebeziumOutboxEvent outboxEvent = objectMapper.readValue(message, DebeziumOutboxEvent.class);
        
        if (outboxEvent.payload() != null && outboxEvent.payload().after() != null && "OrderCreated".equals(outboxEvent.payload().after().eventType())) {
            // Outbox kaydı içindeki asıl sipariş JSON'ını çözümlüyoruz
            String payloadStr = outboxEvent.payload().after().payload();
            OrderCreatedPayload orderPayload = objectMapper.readValue(payloadStr, OrderCreatedPayload.class);
            
            log.info("Parsed order payload: {}", orderPayload);
            inventoryService.reserveInventory(orderPayload, outboxEvent.payload().after().id());
        }
    }
}