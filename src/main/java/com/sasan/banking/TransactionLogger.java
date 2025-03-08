package com.sasan.banking;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TransactionLogger implements TransactionObserver {

    private final String logFilePath;

    public TransactionLogger(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    @Override
    public void onTransaction(
            String transactionId,
            String accountNumber,
            TransactionType transactionType,
            double amount
    ) {

        try (PrintWriter writer = new PrintWriter(new FileWriter(logFilePath, true))) {
            writer.printf(
                    "TransactionId: %s | AccountNumber: %s | Type: %s | Amount: %.2f%n",
                    transactionId,
                    accountNumber,
                    transactionType,
                    amount);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to transaction log", e);
        }
    }
}
