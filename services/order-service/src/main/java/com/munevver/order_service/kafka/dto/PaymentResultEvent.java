package com.munevver.order_service.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
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
