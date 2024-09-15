package com.nek.mysaasapp.services.interfaces;

import java.io.PrintWriter;
import java.util.List;

import com.nek.mysaasapp.entities.TransactionRecord;

import lombok.NonNull;

public interface ExportService {

    /**
     * Exports the given list of transactions to a CSV file.
     *
     * @param transactions the list of transactions to export
     * @param writer the PrintWriter to write the CSV file
     */
    void exportTransactionsToCsv(@NonNull List<TransactionRecord> transactions, @NonNull PrintWriter writer);

    /**
     * Exports the given list of transactions to an Excel file.
     *
     * @param transactions the list of transactions to export
     * @return the byte array of the Excel file
     */
    byte[] exportTransactionsToExcel(@NonNull List<TransactionRecord> transactions);

}
