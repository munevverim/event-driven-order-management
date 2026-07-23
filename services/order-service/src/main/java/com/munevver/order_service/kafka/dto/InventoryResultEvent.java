package com.munevver.order_service.kafka.dto;

import java.math.BigDecimal;

public record InventoryResultEvent(
        Long orderId,
        Long userId,
        Long productId,
        Integer quantity,
        BigDecimal totalPrice,
        String status, // "RESERVED" or "FAILED"
        String reason
) {
}
