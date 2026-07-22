package com.munevver.inventory_service.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderCreatedPayload(
        Long id,
        Long userId,
        Long productId,
        Integer quantity,
        BigDecimal totalPrice,
        String status
) {
}
