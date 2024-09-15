package com.nek.mysaasapp.services;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.nek.mysaasapp.entities.TransactionRecord;

import com.nek.mysaasapp.services.interfaces.ExportService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DatevExportService implements ExportService {

    private static final String EXCEL_FILE_GENERATION_ERR_MSG = "Failed to generate Excel file";
    private static final String DOCUMENT_DATE = "Belegdatum";
    private static final String VOUCHER_NUMBER = "Belegnummer";
    private static final String BOOKING_TEXT = "Buchungstext";
    private static final String TURNOVER = "Umsatz";
    private static final String DEBIT_CREDIT = "S/H";
    private static final String ACCOUNT = "Konto";
    private static final String COUNTER_ACCOUNT = "Gegenkonto";
    private static final String TAX_RATE = "Steuersatz";
    private static final String BOOKING_DATE = "Buchungsdatum";

    @Override
    public void exportTransactionsToCsv(@NonNull List<TransactionRecord> transactions, @NonNull PrintWriter writer) {
        writer.println(DOCUMENT_DATE + "," + VOUCHER_NUMBER + "," + BOOKING_TEXT + "," + TURNOVER + "," + DEBIT_CREDIT + ","
            + ACCOUNT + "," + COUNTER_ACCOUNT + "," + TAX_RATE + "," + BOOKING_DATE);

        for (TransactionRecord tr : transactions) {
            String documentDate = tr.getFormattedDate();
            String voucherNumber = tr.getVoucherNumber();
            String bookingText = tr.getDescription();
            double turnover = tr.getIncome() - tr.getExpenses();
            String debitCredit = turnover >= 0 ? "H" : "S";
            String account = tr.getPaymentMethod();
            String counterAccount = tr.getCounterAccount();
            String taxRate = tr.getFormattedTaxRate();
            String bookingDate = tr.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            writer.printf("%s,%s,%s,%.2f,%s,%s,%s,%s,%s\n", documentDate, voucherNumber, bookingText, turnover, debitCredit,
                account, counterAccount, taxRate, bookingDate);
        }
        writer.flush();
    }

    @Override
    public byte[] exportTransactionsToExcel(@NonNull List<TransactionRecord> transactions) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Transactions");

            Row headerRow = sheet.createRow(0);
            String[] columns = { DOCUMENT_DATE, VOUCHER_NUMBER, BOOKING_TEXT, TURNOVER, DEBIT_CREDIT, ACCOUNT, COUNTER_ACCOUNT,
                TAX_RATE, BOOKING_DATE };
            for (int i = 0; i < columns.length; i++) {
                headerRow.createCell(i).setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (TransactionRecord tr : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(tr.getFormattedDate());
                row.createCell(1).setCellValue(tr.getVoucherNumber());
                row.createCell(2).setCellValue(tr.getDescription());
                row.createCell(3).setCellValue(tr.getIncome() - tr.getExpenses());
                row.createCell(4).setCellValue((tr.getIncome() - tr.getExpenses()) >= 0 ? "H" : "S");
                row.createCell(5).setCellValue(tr.getPaymentMethod());
                row.createCell(6).setCellValue(tr.getCounterAccount());
                row.createCell(7).setCellValue(tr.getFormattedTaxRate());
                row.createCell(8).setCellValue(tr.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }

            workbook.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            log.error(EXCEL_FILE_GENERATION_ERR_MSG, e);
            return new byte[0];
        }
    }
}
