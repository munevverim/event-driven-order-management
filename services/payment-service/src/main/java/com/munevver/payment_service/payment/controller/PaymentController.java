package com.munevver.payment_service.payment.controller;

import com.munevver.payment_service.payment.entity.PaymentRecord;
import com.munevver.payment_service.payment.entity.UserBalance;
import com.munevver.payment_service.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/balances")
    public List<UserBalance> getBalances() {
        return paymentService.getAllBalances();
    }

    @GetMapping("/history")
    public List<PaymentRecord> getHistory() {
        return paymentService.getPaymentHistory();
    }
}
