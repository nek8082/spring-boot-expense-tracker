package com.nek.mysaasapp.entities;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction_record", schema = "saas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecord {

    public static final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00 €");
    private static final DecimalFormat taxRateFormat = new DecimalFormat("#,##0.00 '%'");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 20)
    private String voucherNumber;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 50)
    private String paymentMethod;

    @Column(nullable = false, length = 50)
    private String counterAccount;

    @Column(nullable = false)
    private double income;

    @Column(nullable = false)
    private double expenses;

    @Column
    private double taxRate;

    @Column
    private String remarks;

    @Transient
    private double balanceAfter;

    public TransactionRecord(AppUser user, LocalDate date, String voucherNumber, String description, String category,
        String paymentMethod, String counterAccount, double income, double expenses, double taxRate, String remarks) {
        this.user = user;
        this.date = date;
        this.voucherNumber = voucherNumber;
        this.description = description;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.counterAccount = counterAccount;
        this.income = income;
        this.expenses = expenses;
        this.taxRate = taxRate;
        this.remarks = remarks;
    }

    @Transient
    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Transient
    public String getFormattedIncome() {
        return moneyFormat.format(income);
    }

    @Transient
    public String getFormattedExpenses() {
        return moneyFormat.format(expenses);
    }

    @Transient
    public String getFormattedTaxRate() {
        return taxRateFormat.format(taxRate);
    }

    @Transient
    public double getTaxAmount() {
        double taxableAmount = income - expenses;
        return taxableAmount * (taxRate / 100);
    }

    @Transient
    public String getFormattedTaxAmount() {
        return moneyFormat.format(getTaxAmount());
    }

    @Transient
    public String getFormattedCurrentAmount() {
        return moneyFormat.format(getBalanceAfter());
    }
}
