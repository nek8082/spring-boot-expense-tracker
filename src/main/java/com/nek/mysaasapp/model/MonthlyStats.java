package com.nek.mysaasapp.model;

import java.time.Month;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MonthlyStats {

    @NonNull
    private final Month month;
    private final double totalIncome;
    private final double totalExpenses;
    private final double totalTaxAmount;
    private final double balanceAfter;
    private double resultingBalance = 0;

    public double getResultingBalance() {
        return totalIncome - totalExpenses;
    }
}
