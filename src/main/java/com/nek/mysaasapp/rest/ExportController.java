package com.nek.mysaasapp.rest;

import static com.nek.mysaasapp.rest.binding.ExportControllerBinding.DATEV_EXPORT_URL;
import static com.nek.mysaasapp.rest.binding.ExportControllerBinding.EXCEL_EXPORT_URL;
import static com.nek.mysaasapp.rest.binding.ExportControllerBinding.EXPORT_URL;

import java.io.PrintWriter;
import java.util.List;

import com.nek.mysaasapp.entities.TransactionRecord;
import com.nek.mysaasapp.repository.TransactionRepository;
import com.nek.mysaasapp.services.DatevExportService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ExportController {

    @NonNull
    private final DatevExportService datevExportService;
    @NonNull
    private final TransactionRepository transactionRepository;

    @GetMapping(EXPORT_URL)
    public String exportEndpoint() {
        return "export/export";
    }

    @PostMapping(DATEV_EXPORT_URL)
    public void exportTransactionsToCsv(HttpServletResponse response) {
        List<TransactionRecord> transactions = transactionRepository.findAll();

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"transactions.csv\"");

        try (PrintWriter writer = response.getWriter()) {
            datevExportService.exportTransactionsToCsv(transactions, writer);
        } catch (Exception e) {
            log.error("Exception occurred: ", e);
        }
    }

    @PostMapping(EXCEL_EXPORT_URL)
    public ResponseEntity<byte[]> exportTransactionsToExcel() {
        List<TransactionRecord> transactions = transactionRepository.findAll();
        byte[] excelFile = datevExportService.exportTransactionsToExcel(transactions);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"transactions.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelFile);
    }

}
