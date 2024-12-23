package com.nek.mysaasapp.entities;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "app_user", schema = "saas")
@Data
public class AppUser {

    private static final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00 €");

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email;

    private LocalDateTime premiumValidTo;

    @Column(length = 50, nullable = false)
    private String userRole;

    @Column
    private String subscriptionId;

    @Column(nullable = false)
    private double initialBalance = 0.0;

    @Transient
    public String getFormattedInitialBalance() {
        return moneyFormat.format(initialBalance);
    }
}
