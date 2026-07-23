package com.munevver.inventory_service.inventory.service;

import com.munevver.inventory_service.inventory.entity.Inventory;
import com.munevver.inventory_service.inventory.entity.ProcessedEvent;
import com.munevver.inventory_service.inventory.repository.InventoryRepository;
import com.munevver.inventory_service.inventory.repository.ProcessedEventRepository;
import com.munevver.inventory_service.kafka.dto.InventoryResultEvent;
import com.munevver.inventory_service.kafka.dto.OrderCreatedPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void reserveInventory(OrderCreatedPayload payload, Long eventId) {
        log.info("Checking inventory for product: {}, quantity: {}, event ID: {}", payload.productId(), payload.quantity(), eventId);

        String eventIdStr = eventId.toString();
        if (processedEventRepository.existsById(eventIdStr)) {
            log.warn("Duplicate event detected, event ID: {}. Skipping stock reservation.", eventId);
            return;
        }

        // Olayı işlendi olarak kaydet
        processedEventRepository.save(new ProcessedEvent(eventIdStr, LocalDateTime.now()));

        Inventory inventory = inventoryRepository.findByProductId(payload.productId())
                .orElse(null);

        if (inventory == null) {
            log.warn("Product inventory not found: {}", payload.productId());
            sendResult(payload, "FAILED", "Ürün envanteri bulunamadı");
            return;
        }

        if (inventory.getStockQuantity() < payload.quantity()) {
            log.warn("Insufficient stock for product: {}. Available: {}, Required: {}",
                    payload.productId(), inventory.getStockQuantity(), payload.quantity());
            sendResult(payload, "FAILED", "Yetersiz stok");
            return;
        }

        // Stok düş
        inventory.setStockQuantity(inventory.getStockQuantity() - payload.quantity());
        inventoryRepository.save(inventory);
        log.info("Inventory reserved successfully. New stock: {}", inventory.getStockQuantity());

        sendResult(payload, "RESERVED", "Stok başarıyla düşüldü");
    }

    @Transactional
    public void rollbackStock(Long productId, Integer quantity, Long orderId) {
        log.info("Rolling back stock for product: {}, quantity: {}, order ID: {}", productId, quantity, orderId);

        String processedKey = "rollback-" + orderId;
        if (processedEventRepository.existsById(processedKey)) {
            log.warn("Duplicate rollback detected for order ID: {}. Skipping.", orderId);
            return;
        }

        // Olayı işlendi olarak kaydet
        processedEventRepository.save(new ProcessedEvent(processedKey, LocalDateTime.now()));

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElse(null);

        if (inventory == null) {
            log.error("Product inventory not found for rollback! Product ID: {}", productId);
            return;
        }

        // Stok iade et
        inventory.setStockQuantity(inventory.getStockQuantity() + quantity);
        inventoryRepository.save(inventory);
        log.info("Stock successfully rolled back. New stock: {}", inventory.getStockQuantity());
    }

    private void sendResult(OrderCreatedPayload payload, String status, String reason) {
        InventoryResultEvent result = new InventoryResultEvent(
                payload.id(),
                payload.userId(),
                payload.productId(),
                payload.quantity(),
                payload.totalPrice(),
                status,
                reason
        );
        log.info("Sending inventory check result to Kafka: {}", result);
        kafkaTemplate.send("inventory-events", payload.id().toString(), result);
    }
}
