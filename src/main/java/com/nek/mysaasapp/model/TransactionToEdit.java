package com.nek.mysaasapp.model;

import java.time.LocalDate;

public record TransactionToEdit(Long transactionId, LocalDate date, String voucherNumber, String description, String category,
        String paymentMethod, String counterAccount, double income, double expenses, double taxRate, String remarks) {

}
