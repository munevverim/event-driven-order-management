package com.munevver.order_service.kafka.dto;

public record InventoryResultEvent(
        Long orderId,
        String status, // "RESERVED" or "FAILED"
        String reason
) {
}
