package com.munevver.payment_service.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_balances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBalance {

    @Id
    private Long userId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;
}
