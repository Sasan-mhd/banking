package com.sasan.banking;

import com.sasan.banking.domain.model.TransactionObserver;
import com.sasan.banking.domain.model.TransactionType;
import com.sasan.banking.log.TransactionLogger;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionLogTest {

    private TransactionObserver logger;

    @Test
    void shouldLogDepositTransaction() throws IOException {

        String logFilePath = "transaction.log";
        new PrintWriter(logFilePath).close();
        TransactionObserver logger = new TransactionLogger(logFilePath);

        String transactionId = UUID.randomUUID().toString();
        String transactionAccountNumber = "123456";
        TransactionType transactionType = TransactionType.CREDIT;
        double transactionAmount = 100.0;

        logger.onTransaction(transactionId, transactionAccountNumber, transactionType, transactionAmount);

        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line = reader.readLine();
            assertTrue(
                    line.contains(transactionId) &&
                            line.contains(transactionAccountNumber) &&
                            line.contains(transactionType.toString()) &&
                            line.contains(String.valueOf(transactionAmount))
            );
        }
    }

    @Test
    void shouldLogWithdrawalTransaction() throws IOException {
        String logFilePath = "transaction.log";
        new PrintWriter(logFilePath).close();
        TransactionObserver logger = new TransactionLogger(logFilePath);

        String transactionId = UUID.randomUUID().toString();
        String transactionAccountNumber = "123456";
        TransactionType transactionType = TransactionType.DEBIT;
        double transactionAmount = 100.0;

        logger.onTransaction(transactionId, transactionAccountNumber, transactionType, transactionAmount);

        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
            String line = reader.readLine();
            assertTrue(
                    line.contains(transactionId) &&
                            line.contains(transactionAccountNumber) &&
                            line.contains(transactionType.toString()) &&
                            line.contains(String.valueOf(transactionAmount))
            );
        }
    }


}