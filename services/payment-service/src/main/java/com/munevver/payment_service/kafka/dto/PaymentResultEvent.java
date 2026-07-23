package com.munevver.payment_service.kafka.dto;

import java.math.BigDecimal;

public record PaymentResultEvent(
        Long orderId,
        Long userId,
        Long productId,
        Integer quantity,
        BigDecimal amount,
        String status, // "SUCCESS" or "FAILED"
        String reason
) {
}
