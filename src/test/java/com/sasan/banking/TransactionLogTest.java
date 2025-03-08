package com.sasan.banking;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionLogTest {

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

    @Test
    void shouldNotifyObserversOnDeposit() {
        BankAccount account = new BankAccount(new AccountNumber("12345"), "John Doe", new Money(1000.00));
        AtomicBoolean notified = new AtomicBoolean(false);
        TransactionObserver logger = new TransactionObserver() {
            @Override
            public void onTransaction(String transactionId, String accountNumber, TransactionType transactionType, double amount) {
                notified.set(true);
            }
        };

        account.addObserver(logger);
        account.deposit(UUID.randomUUID().toString(), new Money(500.00));

        assertTrue(notified.get());
    }
}