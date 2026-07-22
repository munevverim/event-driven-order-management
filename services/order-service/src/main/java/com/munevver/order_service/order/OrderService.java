package com.munevver.order_service.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.munevver.order_service.outbox.Outbox;
import com.munevver.order_service.outbox.OutboxRepository;
import com.munevver.order_service.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = Order.builder()
                .userId(request.userId())
                .productId(request.productId())
                .quantity(request.quantity())
                .totalPrice(request.totalPrice())
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        // Event payload serialize et
        String payload;
        try {
            payload = objectMapper.writeValueAsString(savedOrder);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Sipariş olayı JSON'a dönüştürülürken hata oluştu", e);
        }

        // Outbox kaydı oluştur ve kaydet
        Outbox outbox = Outbox.builder()
                .aggregateType("Order")
                .aggregateId(savedOrder.getId().toString())
                .eventType("OrderCreated")
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .build();

        outboxRepository.save(outbox);

        return savedOrder;
    }

    public java.util.List<Order> getAllOrders() {
        return orderRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("order.not.found", id));
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        orderRepository.save(order);
    }
}