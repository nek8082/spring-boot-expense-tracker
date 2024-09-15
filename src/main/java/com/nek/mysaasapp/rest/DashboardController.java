package com.nek.mysaasapp.rest;

import com.nek.mysaasapp.entities.AppUser;
import com.nek.mysaasapp.entities.TransactionRecord;
import com.nek.mysaasapp.repository.TransactionRepository;
import com.nek.mysaasapp.services.SpringSecurityBasedUserService;
import com.nek.mysaasapp.services.interfaces.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static com.nek.mysaasapp.entities.TransactionRecord.moneyFormat;
import static com.nek.mysaasapp.model.ThymeleafConstants.CURRENT_AMOUNT_THYMELEAF_MODEL_ATTRIBUTE_NAME;
import static com.nek.mysaasapp.model.ThymeleafConstants.INITIAL_BALANCE_THYMELEAF_MODEL_ATTRIBUTE_NAME;
import static com.nek.mysaasapp.model.ThymeleafConstants.TOTAL_EXPENSE_THYMELEAF_MODEL_ATTRIBUTE_NAME;
import static com.nek.mysaasapp.model.ThymeleafConstants.TOTAL_INCOME_THYMELEAF_MODEL_ATTRIBUTE_NAME;
import static com.nek.mysaasapp.model.ThymeleafConstants.TOTAL_TAX_AMOUNT_THYMELEAF_MODEL_ATTRIBUTE_NAME;
import static com.nek.mysaasapp.model.ThymeleafConstants.TRANSACTION_THYMELEAF_MODEL_ATTRIBUTE_NAME;
import static com.nek.mysaasapp.rest.binding.DashboardControllerBinding.PREMIUM_URL;
import static com.nek.mysaasapp.rest.binding.MainControllerBinding.PUBLIC_URL;
import static com.nek.mysaasapp.util.TransactionUtil.calculateCurrentAmount;
import static com.nek.mysaasapp.util.TransactionUtil.setBalanceAfterForAllEntries;
import static com.nek.mysaasapp.util.TransactionUtil.sumExpenses;
import static com.nek.mysaasapp.util.TransactionUtil.sumIncome;
import static com.nek.mysaasapp.util.TransactionUtil.sumTaxAmount;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    @NonNull
    private final TransactionRepository transactionRepository;
    @NonNull
    private final UserService springSecurityBasedUserService;

    @GetMapping(PREMIUM_URL)
    public String premiumEndpoint(Model model) {
        return handleDashboardRequest(model);
    }

    @PostMapping(PREMIUM_URL)
    public String premiumFilterEndpoint(@RequestParam(required = false) String dateFilter, Model model) {
        if (dateFilter == null || dateFilter.isEmpty()) {
            return "redirect:" + PUBLIC_URL;
        }
        return handleDashboardRequest(model, dateFilter);
    }

    private String handleDashboardRequest(Model model, String... dateFilter) {
        Optional<AppUser> authenticatedUser = springSecurityBasedUserService.getAuthenticatedUser();
        if (authenticatedUser.isEmpty()) {
            log.info("Could not find authenticated user, redirecting back /public");
            return "redirect:" + PUBLIC_URL;
        }

        AppUser appUser = authenticatedUser.get();
        List<TransactionRecord> transactions = transactionRepository.findByUser(appUser);
        transactions = setBalanceAfterForAllEntries(appUser.getInitialBalance(), transactions);
        addCurrentAmountAttribute(model, appUser, transactions);
        if (dateFilter.length > 0 && !dateFilter[0].isEmpty()) {
            try {
                YearMonth filterYearMonth = YearMonth.parse(dateFilter[0], DateTimeFormatter.ofPattern("yyyy-MM"));
                transactions = transactions.stream()
                        .filter(transactionRecord -> transactionRecord.getDate() != null
                                && YearMonth.from(transactionRecord.getDate()).equals(filterYearMonth))
                        .toList();
            } catch (DateTimeParseException e) {
                return "redirect:" + PUBLIC_URL;
            }
        }
        addCommonAttributes(model, appUser, transactions);
        return "premium";
    }

    private void addCommonAttributes(Model model, AppUser appUser, List<TransactionRecord> transactions) {
        double totalIncome = sumIncome(transactions);
        double totalExpense = sumExpenses(transactions);
        double totalTaxAmount = sumTaxAmount(transactions);

        model.addAttribute(INITIAL_BALANCE_THYMELEAF_MODEL_ATTRIBUTE_NAME, appUser.getFormattedInitialBalance());
        model.addAttribute(TRANSACTION_THYMELEAF_MODEL_ATTRIBUTE_NAME, transactions);
        model.addAttribute(TOTAL_INCOME_THYMELEAF_MODEL_ATTRIBUTE_NAME, moneyFormat.format(totalIncome));
        model.addAttribute(TOTAL_EXPENSE_THYMELEAF_MODEL_ATTRIBUTE_NAME, moneyFormat.format(totalExpense));
        model.addAttribute(TOTAL_TAX_AMOUNT_THYMELEAF_MODEL_ATTRIBUTE_NAME, moneyFormat.format(totalTaxAmount));
    }

    private void addCurrentAmountAttribute(Model model, AppUser appUser, List<TransactionRecord> transactions) {
        double currentAmount = calculateCurrentAmount(appUser.getInitialBalance(), transactions);
        model.addAttribute(CURRENT_AMOUNT_THYMELEAF_MODEL_ATTRIBUTE_NAME, moneyFormat.format(currentAmount));
    }
}
