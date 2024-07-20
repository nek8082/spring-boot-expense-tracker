package com.nek.mysaasapp.util;

import static java.util.Comparator.comparing;

import java.util.List;
import java.util.Optional;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

import com.nek.mysaasapp.entities.TransactionRecord;

import lombok.NonNull;

public class TransactionUtil {

    /**
     * Calculates the total income from a list of transaction records.
     *
     * @param transactions A non-null list of TransactionRecord objects from which to sum the income.
     * @return The sum of the income values from the provided list of transactions.
     */
    public static double sumIncome(@NonNull List<TransactionRecord> transactions) {
        return calculateSum(transactions, TransactionRecord::getIncome);
    }

    /**
     * Calculates the total expenses from a list of transaction records.
     *
     * @param transactions A non-null list of TransactionRecord objects from which to sum the expenses.
     * @return The sum of the expenses values from the provided list of transactions.
     */
    public static double sumExpenses(@NonNull List<TransactionRecord> transactions) {
        return calculateSum(transactions, TransactionRecord::getExpenses);
    }

    /**
     * Calculates the total tax amount from a list of transaction records.
     *
     * @param transactions A non-null list of TransactionRecord objects from which to sum the tax amount.
     * @return The sum of the tax amount values from the provided list of transactions.
     */
    public static double sumTaxAmount(@NonNull List<TransactionRecord> transactions) {
        return calculateSum(transactions, TransactionRecord::getTaxAmount);
    }

    /**
     * Sets the balance after each transaction based on the initial amount and updates each transaction record accordingly.
     *
     * @param initialAmount The starting balance before any transactions are processed.
     * @param transactions A non-null list of TransactionRecord objects to be processed.
     * @return A list of TransactionRecord objects with updated balance after each transaction.
     */
    public static List<TransactionRecord> setBalanceAfterForAllEntries(double initialAmount,
                                                                       @NonNull List<TransactionRecord> transactions) {
        transactions = sortTransactions(transactions);
        double currentAmount = initialAmount;
        for (TransactionRecord transaction : transactions) {
            currentAmount = currentAmount + transaction.getIncome() - transaction.getExpenses() - transaction.getTaxAmount();
            transaction.setBalanceAfter(currentAmount);
        }
        return transactions;
    }

    /**
     * Retrieves the most recent transaction from a sorted list of transaction records.
     *
     * @param transactions A non-null list of TransactionRecord objects to be evaluated.
     * @return An Optional containing the latest transaction if the list is not empty; otherwise, an empty Optional.
     */
    public static Optional<TransactionRecord> getLatestTransaction(@NonNull List<TransactionRecord> transactions) {
        List<TransactionRecord> transactionRecords = sortTransactions(transactions);
        if (transactionRecords.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(transactionRecords.get(transactionRecords.size() - 1));
    }

    /**
     * Retrieves the first transaction from a sorted list of transaction records.
     *
     * @param transactions A non-null list of TransactionRecord objects to be evaluated.
     * @return An Optional containing the first transaction if the list is not empty; otherwise, an empty Optional.
     */
    public static Optional<TransactionRecord> getFirstTransaction(@NonNull List<TransactionRecord> transactions) {
        List<TransactionRecord> transactionRecords = sortTransactions(transactions);
        if (transactionRecords.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(transactionRecords.get(0));
    }

    /**
     * Calculates the current balance by adding total income and subtracting total expenses and tax amounts from the initial amount.
     *
     * @param initialAmount The initial amount before processing the transactions.
     * @param transactions A non-null list of TransactionRecord objects from which to calculate the current amount.
     * @return The calculated current amount after processing all transactions.
     */
    public static double calculateCurrentAmount(double initialAmount, @NonNull List<TransactionRecord> transactions) {
        double totalIncome = sumIncome(transactions);
        double totalExpenses = sumExpenses(transactions);
        double totalTaxAmount = sumTaxAmount(transactions);
        return initialAmount + totalIncome - totalExpenses - totalTaxAmount;
    }


    private static List<TransactionRecord> sortTransactions(List<TransactionRecord> transactions) {
        return transactions.stream()
                           .sorted(comparing(TransactionRecord::getDate).thenComparing(TransactionRecord::getId))
                           .collect(Collectors.toList());
    }

    private static double calculateSum(List<TransactionRecord> transactions, ToDoubleFunction<TransactionRecord> mapper) {
        List<TransactionRecord> sortedTransactions = sortTransactions(transactions);
        return sortedTransactions.stream().mapToDouble(mapper).sum();
    }
}
