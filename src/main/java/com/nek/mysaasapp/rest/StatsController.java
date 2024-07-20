package com.nek.mysaasapp.rest;

import static com.nek.mysaasapp.model.ThymeleafConstants.FIRST_YEAR_THYMELEAF_MODEL_ATTRIBUTE_NAME;
import static com.nek.mysaasapp.model.ThymeleafConstants.LATEST_YEAR_THYMELEAF_MODEL_ATTRIBUTE_NAME;
import static com.nek.mysaasapp.model.ThymeleafConstants.SELECTED_YEAR_THYMELEAF_MODEL_ATTRIBUTE_NAME;
import static com.nek.mysaasapp.model.ThymeleafConstants.STATS_THYMELEAF_MODEL_ATTRIBUTE_NAME;
import static com.nek.mysaasapp.rest.binding.MainControllerBinding.PUBLIC_URL;
import static com.nek.mysaasapp.rest.binding.StatsControllerBinding.STATS_URL;
import static com.nek.mysaasapp.util.TransactionUtil.getFirstTransaction;
import static com.nek.mysaasapp.util.TransactionUtil.getLatestTransaction;

import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.nek.mysaasapp.entities.AppUser;
import com.nek.mysaasapp.entities.TransactionRecord;
import com.nek.mysaasapp.model.MonthlyStats;
import com.nek.mysaasapp.repository.TransactionRepository;
import com.nek.mysaasapp.services.StatsService;
import com.nek.mysaasapp.services.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    @NonNull
    private final StatsService statsService;
    @NonNull
    private final UserService userService;
    @NonNull
    private final TransactionRepository transactionRepository;

    @GetMapping(STATS_URL)
    public String statsEndpoint(@RequestParam(required = false) Integer year, Model model) {
        Optional<AppUser> user = userService.getAuthenticatedUser();
        if (user.isPresent()) {
            List<TransactionRecord> transactions = transactionRepository.findByUser(user.get());
            Optional<TransactionRecord> firstTransaction = getFirstTransaction(transactions);
            Optional<TransactionRecord> latestTransaction = getLatestTransaction(transactions);
            if (firstTransaction.isEmpty() || latestTransaction.isEmpty()) {
                return "redirect:" + PUBLIC_URL;
            }
            int firstYear = firstTransaction.get().getDate().getYear();
            int latestYear = latestTransaction.get().getDate().getYear();
            int selectedYear = year == null ? latestYear : year;
            Map<Month, MonthlyStats> monthlyStatsMap = statsService.calculateMonthlyStats(Year.of(selectedYear));
            model.addAttribute(STATS_THYMELEAF_MODEL_ATTRIBUTE_NAME, monthlyStatsMap);
            model.addAttribute(FIRST_YEAR_THYMELEAF_MODEL_ATTRIBUTE_NAME, firstYear);
            model.addAttribute(LATEST_YEAR_THYMELEAF_MODEL_ATTRIBUTE_NAME, latestYear);
            model.addAttribute(SELECTED_YEAR_THYMELEAF_MODEL_ATTRIBUTE_NAME, selectedYear);
        } else {
            return "redirect:" + PUBLIC_URL;
        }
        return "statistics/stats";
    }
}
