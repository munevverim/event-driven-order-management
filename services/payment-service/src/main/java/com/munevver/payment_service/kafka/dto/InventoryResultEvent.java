package com.munevver.payment_service.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
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
