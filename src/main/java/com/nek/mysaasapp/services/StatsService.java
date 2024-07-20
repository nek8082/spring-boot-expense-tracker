package com.nek.mysaasapp.services;

import static java.util.Collections.emptyMap;

import static com.nek.mysaasapp.util.TransactionUtil.getLatestTransaction;
import static com.nek.mysaasapp.util.TransactionUtil.setBalanceAfterForAllEntries;
import static com.nek.mysaasapp.util.TransactionUtil.sumExpenses;
import static com.nek.mysaasapp.util.TransactionUtil.sumIncome;
import static com.nek.mysaasapp.util.TransactionUtil.sumTaxAmount;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import com.nek.mysaasapp.entities.AppUser;
import com.nek.mysaasapp.entities.TransactionRecord;
import com.nek.mysaasapp.model.MonthlyStats;
import com.nek.mysaasapp.repository.TransactionRepository;

import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {

    @NonNull
    private final UserService userService;
    @NonNull
    private final TransactionRepository transactionRepository;

    /**
     * Calculate monthly statistics for the given year.
     *
     * @param year the year for which to calculate the statistics
     * @return a map of monthly statistics
     */
    public Map<Month, MonthlyStats> calculateMonthlyStats(@NonNull Year year) {
        Optional<AppUser> user = userService.getAuthenticatedUser();
        if (user.isPresent()) {
            List<TransactionRecord> transactions = transactionRepository.findByUser(user.get());
            transactions = setBalanceAfterForAllEntries(user.get().getInitialBalance(), transactions);

            Map<Month, MonthlyStats> monthlyStatsMap = new TreeMap<>();
            for (Month month : Month.values()) {
                Optional<MonthlyStats> janStats = calculateStats(transactions, month, year);
                janStats.ifPresent(monthlyStats -> monthlyStatsMap.put(month, monthlyStats));
            }
            return monthlyStatsMap;
        } else {
            return emptyMap();
        }
    }

    private Optional<MonthlyStats> calculateStats(List<TransactionRecord> transactions, Month month, Year year) {
        List<TransactionRecord> monthlyTransactions = transactions.stream()
                                                                  .filter(transaction -> transaction.getDate().getMonth() == month
                                                                      && transaction.getDate().getYear() == year.getValue())
                                                                  .toList();
        double monthlyTotalIncome = sumIncome(monthlyTransactions);
        double monthlyTotalExpenses = sumExpenses(monthlyTransactions);
        double monthlyTotalTaxAmount = sumTaxAmount(monthlyTransactions);
        Optional<TransactionRecord> latestTransaction = getLatestTransaction(monthlyTransactions);
        double monthlyBalanceAfter;
        if (latestTransaction.isPresent()) {
            monthlyBalanceAfter = latestTransaction.get().getBalanceAfter();
        } else {
            return Optional.empty();
        }
        return Optional.of(
            new MonthlyStats(month, monthlyTotalIncome, monthlyTotalExpenses, monthlyTotalTaxAmount, monthlyBalanceAfter));
    }
}
