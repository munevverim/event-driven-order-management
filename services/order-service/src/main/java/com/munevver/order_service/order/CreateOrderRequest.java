package com.munevver.order_service.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest(

        @NotNull(message = "{order.user.required}")
        Long userId,

        @NotNull(message = "{order.product.required}")
        Long productId,

        @NotNull(message = "{order.quantity.required}")
        @Min(value = 1, message = "{order.quantity.min}")
        Integer quantity,

        @NotNull(message = "{order.price.required}")
        @DecimalMin(value = "0.01", message = "{order.price.min}")
        BigDecimal totalPrice

) {
}