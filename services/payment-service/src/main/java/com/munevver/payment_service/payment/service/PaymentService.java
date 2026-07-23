package com.munevver.payment_service.payment.service;

import com.munevver.payment_service.kafka.dto.InventoryResultEvent;
import com.munevver.payment_service.kafka.dto.PaymentResultEvent;
import com.munevver.payment_service.payment.entity.PaymentRecord;
import com.munevver.payment_service.payment.entity.UserBalance;
import com.munevver.payment_service.payment.repository.PaymentRecordRepository;
import com.munevver.payment_service.payment.repository.UserBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService implements CommandLineRunner {

    private final UserBalanceRepository userBalanceRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void run(String... args) throws Exception {
        if (userBalanceRepository.count() == 0) {
            log.info("Seeding initial user balances...");
            userBalanceRepository.save(new UserBalance(1L, new BigDecimal("100.00"))); // Müşteri #1
            userBalanceRepository.save(new UserBalance(2L, new BigDecimal("500.00"))); // Müşteri #2
            userBalanceRepository.save(new UserBalance(3L, new BigDecimal("50.00")));  // Müşteri #3
            log.info("User balance seeding completed.");
        }
    }

    @Transactional
    public void processPayment(InventoryResultEvent event) {
        log.info("Processing payment for order ID: {}, User ID: {}, Amount: {}", 
                event.orderId(), event.userId(), event.totalPrice());

        // Idempotency check
        if (paymentRecordRepository.existsByOrderId(event.orderId())) {
            log.warn("Payment already processed for order ID: {}. Skipping.", event.orderId());
            return;
        }

        // Get user balance, create default if not exists
        UserBalance userBalance = userBalanceRepository.findById(event.userId())
                .orElseGet(() -> userBalanceRepository.save(
                        UserBalance.builder()
                                .userId(event.userId())
                                .balance(new BigDecimal("100.00"))
                                .build()
                ));

        PaymentRecord paymentRecord;
        String status;
        String reason = null;

        if (userBalance.getBalance().compareTo(event.totalPrice()) >= 0) {
            // Success flow
            userBalance.setBalance(userBalance.getBalance().subtract(event.totalPrice()));
            userBalanceRepository.save(userBalance);

            status = "SUCCESS";
            paymentRecord = PaymentRecord.builder()
                    .orderId(event.orderId())
                    .userId(event.userId())
                    .productId(event.productId())
                    .quantity(event.quantity())
                    .amount(event.totalPrice())
                    .status(status)
                    .createdAt(LocalDateTime.now())
                    .build();

            log.info("Payment successful. Deducted: {}. New Balance: {}", event.totalPrice(), userBalance.getBalance());
        } else {
            // Failed flow (insufficient balance)
            status = "FAILED";
            reason = "Yetersiz bakiye (Gerekli: $" + event.totalPrice() + ", Mevcut: $" + userBalance.getBalance() + ")";
            paymentRecord = PaymentRecord.builder()
                    .orderId(event.orderId())
                    .userId(event.userId())
                    .productId(event.productId())
                    .quantity(event.quantity())
                    .amount(event.totalPrice())
                    .status(status)
                    .reason(reason)
                    .createdAt(LocalDateTime.now())
                    .build();

            log.warn("Payment failed for order ID: {}. Reason: {}", event.orderId(), reason);
        }

        paymentRecordRepository.save(paymentRecord);

        // Send payment result event to Kafka
        PaymentResultEvent resultEvent = new PaymentResultEvent(
                event.orderId(),
                event.userId(),
                event.productId(),
                event.quantity(),
                event.totalPrice(),
                status,
                reason
        );

        log.info("Publishing PaymentResultEvent: {}", resultEvent);
        kafkaTemplate.send("payment-events", event.orderId().toString(), resultEvent);
    }

    public List<UserBalance> getAllBalances() {
        return userBalanceRepository.findAll();
    }

    public List<PaymentRecord> getPaymentHistory() {
        return paymentRecordRepository.findAllByOrderByCreatedAtDesc();
    }
}
