package com.munevver.payment_service.payment.repository;

import com.munevver.payment_service.payment.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
    boolean existsByOrderId(Long orderId);
    List<PaymentRecord> findAllByOrderByCreatedAtDesc();
}
