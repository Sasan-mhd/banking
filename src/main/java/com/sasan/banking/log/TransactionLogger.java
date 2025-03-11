package com.sasan.banking.log;

import com.sasan.banking.domain.service.transaction.observer.TransactionObserver;
import com.sasan.banking.domain.service.transaction.observer.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@Service
public class TransactionLogger implements TransactionObserver {

    private final String logFilePath;

    @Autowired
    public TransactionLogger(@Value("${log-file-path}") String logFilePath) {
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
