package com.nek.mysaasapp.rest;

import static com.nek.mysaasapp.rest.binding.MainControllerBinding.PUBLIC_URL;
import static com.nek.mysaasapp.rest.binding.TransactionControllerBinding.TRANSACTION_DELETE_URL;
import static com.nek.mysaasapp.rest.binding.TransactionControllerBinding.TRANSACTION_EDIT_GET_URL;
import static com.nek.mysaasapp.rest.binding.TransactionControllerBinding.TRANSACTION_EDIT_URL;
import static com.nek.mysaasapp.rest.binding.TransactionControllerBinding.TRANSACTION_SAVE_URL;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.nek.mysaasapp.entities.AppUser;
import com.nek.mysaasapp.entities.TransactionRecord;
import com.nek.mysaasapp.model.TransactionToDelete;
import com.nek.mysaasapp.model.TransactionToEdit;
import com.nek.mysaasapp.model.TransactionToSave;
import com.nek.mysaasapp.repository.TransactionRepository;
import com.nek.mysaasapp.services.SpringSecurityBasedUserService;

import com.nek.mysaasapp.services.interfaces.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    @NonNull
    private final TransactionRepository transactionRepository;
    @NonNull
    private final UserService springSecurityBasedUserService;

    @GetMapping(TRANSACTION_SAVE_URL)
    public String getSaveTransaction() {
        return "transaction/save";
    }

    @GetMapping(TRANSACTION_EDIT_GET_URL)
    public String getEditTransaction(@PathVariable Long transactionId, Model model) {
        Optional<AppUser> appUser = springSecurityBasedUserService.getAuthenticatedUser();
        if (appUser.isPresent()) {
            List<TransactionRecord> transactions = transactionRepository.findByUser(appUser.get());
            Optional<TransactionRecord> record = transactionRepository.findById(transactionId);
            if (record.isPresent() && transactions.contains(record.get())) {
                model.addAttribute("transaction", record.get());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                model.addAttribute("date", record.get().getDate().format(formatter));
                model.addAttribute("voucherNumber", record.get().getVoucherNumber());
                model.addAttribute("description", record.get().getDescription());
                model.addAttribute("category", record.get().getCategory());
                model.addAttribute("paymentMethod", record.get().getPaymentMethod());
                model.addAttribute("counterAccount", record.get().getCounterAccount());
                model.addAttribute("income", record.get().getIncome());
                model.addAttribute("expenses", record.get().getExpenses());
                model.addAttribute("taxRate", record.get().getTaxRate());
                model.addAttribute("remarks", record.get().getRemarks());
            }
        }
        return "transaction/edit";
    }

    @PostMapping(TRANSACTION_EDIT_URL)
    public String postEditTransaction(@ModelAttribute TransactionToEdit transaction) {
        Optional<AppUser> user = springSecurityBasedUserService.getAuthenticatedUser();
        if (user.isPresent()) {
            List<TransactionRecord> transactions = transactionRepository.findByUser(user.get());
            Optional<TransactionRecord> record = transactionRepository.findById(transaction.transactionId());
            if (record.isPresent() && transactions.contains(record.get())) {
                record.get().setDate(transaction.date());
                record.get().setVoucherNumber(transaction.voucherNumber());
                record.get().setDescription(transaction.description());
                record.get().setCategory(transaction.category());
                record.get().setPaymentMethod(transaction.paymentMethod());
                record.get().setCounterAccount(transaction.counterAccount());
                record.get().setCounterAccount(transaction.counterAccount());
                record.get().setIncome(transaction.income());
                record.get().setExpenses(transaction.expenses());
                record.get().setTaxRate(transaction.taxRate());
                record.get().setRemarks(transaction.remarks());
                transactionRepository.save(record.get());
            }
        }
        return "redirect:" + PUBLIC_URL;
    }

    @PostMapping(TRANSACTION_SAVE_URL)
    public String saveTransaction(@ModelAttribute TransactionToSave transaction) {
        Optional<AppUser> user = springSecurityBasedUserService.getAuthenticatedUser();
        if (user.isPresent()) {
            TransactionRecord record = new TransactionRecord(user.get(), transaction.date(), transaction.voucherNumber(),
                transaction.description(), transaction.category(), transaction.paymentMethod(), transaction.counterAccount(),
                transaction.income(), transaction.expenses(), transaction.taxRate(), transaction.remarks());
            transactionRepository.save(record);
        }
        return "redirect:" + PUBLIC_URL;
    }

    @PostMapping(TRANSACTION_DELETE_URL)
    public String deleteTransaction(@ModelAttribute TransactionToDelete transaction) {
        Optional<AppUser> user = springSecurityBasedUserService.getAuthenticatedUser();
        if (user.isPresent()) {
            transactionRepository.deleteById(transaction.id());
        }
        return "redirect:" + PUBLIC_URL;
    }
}
