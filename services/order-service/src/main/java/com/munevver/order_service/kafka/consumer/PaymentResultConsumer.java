package com.munevver.order_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.munevver.order_service.kafka.dto.PaymentResultEvent;
import com.munevver.order_service.order.OrderService;
import com.munevver.order_service.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "payment-events",
            groupId = "order-service-group"
    )
    public void consumePaymentResult(String message) {
        log.info("Payment result event received from Kafka: {}", message);
        try {
            PaymentResultEvent result = objectMapper.readValue(message, PaymentResultEvent.class);
            log.info("Parsed payment result in order-service: {}", result);

            if ("SUCCESS".equals(result.status())) {
                orderService.updateOrderStatus(result.orderId(), OrderStatus.PAID);
                log.info("Order status updated to PAID for order ID: {}", result.orderId());
            } else if ("FAILED".equals(result.status())) {
                orderService.updateOrderStatus(result.orderId(), OrderStatus.PAYMENT_FAILED);
                log.info("Order status updated to PAYMENT_FAILED for order ID: {}", result.orderId());
            }
        } catch (Exception e) {
            log.error("Failed to process payment result event", e);
        }
    }
}
