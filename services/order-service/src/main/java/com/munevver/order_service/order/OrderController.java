package com.munevver.order_service.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "Sipariş yönetimi ve oluşturulması için REST API uç noktaları")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Yeni sipariş oluşturur", description = "Verilen kullanıcı ID, ürün ID, miktar ve fiyat bilgileri ile siparişi kaydeder ve Transactional Outbox olayını tetikler.")
    public Order createOrder(@RequestBody @Valid CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @GetMapping
    @Operation(summary = "Tüm siparişleri listeler", description = "Veri tabanındaki tüm siparişleri oluşturulma tarihine göre tersten sıralı şekilde döner.")
    public java.util.List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Sipariş detaylarını getirir", description = "Verilen sipariş benzersiz kimliğine (ID) göre sipariş detaylarını veri tabanından sorgulayarak döner.")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }
}