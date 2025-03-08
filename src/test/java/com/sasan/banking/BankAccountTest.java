package com.sasan.banking;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class BankAccountTest {

    @Test
    void shouldCreateAccountWithInitialBalance() {
        TransactionObserver logger = (
                transactionId,
                accountNumber,
                transactionType,
                amount
        ) -> {
        };
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);

        BankAccount account = new BankAccount(logger, accountNumber, "John Doe", initialBalance);

        assertEquals("12345", account.getAccountNumber().number());
        assertEquals(1000.00, account.getBalance().amount());
    }

    @Test
    void shouldDepositMoneyIntoAccount() {
        TransactionObserver logger = (
                transactionId,
                accountNumber,
                transactionType,
                amount
        ) -> {
        };
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(logger, accountNumber, "John Doe", initialBalance);

        Money depositAmount = new Money(500.00);
        account.deposit(UUID.randomUUID().toString(), depositAmount);

        assertEquals(1500.00, account.getBalance().amount());
    }

    @Test
    void shouldNotDepositNegativeAmount() {
        TransactionObserver logger = (
                transactionId,
                accountNumber,
                transactionType,
                amount
        ) -> {
        };
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(logger, accountNumber, "John Doe", initialBalance);

        Money negativeDeposit = new Money(-500.00);
        account.deposit(UUID.randomUUID().toString(), negativeDeposit);

        assertEquals(1000.00, account.getBalance().amount());
    }

    @Test
    void shouldWithdrawMoneyFromAccount() {
        TransactionObserver logger = (
                transactionId,
                accountNumber,
                transactionType,
                amount
        ) -> {
        };
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(logger, accountNumber, "John Doe", initialBalance);

        Money withdrawalAmount = new Money(500.00);
        try {
            account.withdraw(UUID.randomUUID().toString(), withdrawalAmount);
        } catch (Exception e) {
            fail();
        }

        assertEquals(500.00, account.getBalance().amount());
    }

    @Test
    void shouldNotWithdrawMoreThanAvailableBalance() {
        TransactionObserver logger = (
                transactionId,
                accountNumber,
                transactionType,
                amount
        ) -> {
        };
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(logger, accountNumber, "John Doe", initialBalance);

        Money excessiveWithdrawal = new Money(1500.00);
        try {
            account.withdraw(UUID.randomUUID().toString(), excessiveWithdrawal);
            fail();
        } catch (InsufficientAmountException e) {
            assertEquals(1000.00, account.getBalance().amount());
        }

    }

    @Test
    void shouldTransferMoneyBetweenAccounts() {
        TransactionObserver logger = (
                transactionId,
                accountNumber,
                transactionType,
                amount
        ) -> {
        };

        AccountNumber senderAccountNumber = new AccountNumber("12345");
        Money senderInitialBalance = new Money(1000.00);
        BankAccount senderAccount = new BankAccount(
                logger,
                senderAccountNumber,
                "John Doe",
                senderInitialBalance
        );

        AccountNumber receiverAccountNumber = new AccountNumber("67890");
        Money receiverInitialBalance = new Money(500.00);
        BankAccount receiverAccount = new BankAccount(
                logger,
                receiverAccountNumber,
                "Jane Smith",
                receiverInitialBalance
        );

        Money transferAmount = new Money(300.00);
        try {
            senderAccount.transfer(UUID.randomUUID().toString(), receiverAccount, transferAmount);
        } catch (Exception e) {
            fail();
        }

        assertEquals(700.00, senderAccount.getBalance().amount());
        assertEquals(800.00, receiverAccount.getBalance().amount());
    }

    @Test
    void shouldNotTransferMoreThanAvailableBalance() {
        TransactionObserver logger = (
                transactionId,
                accountNumber,
                transactionType,
                amount
        ) -> {
        };

        AccountNumber senderAccountNumber = new AccountNumber("12345");
        Money senderInitialBalance = new Money(1000.00);
        BankAccount senderAccount = new BankAccount(
                logger,
                senderAccountNumber,
                "John Doe",
                senderInitialBalance
        );

        AccountNumber receiverAccountNumber = new AccountNumber("67890");
        Money receiverInitialBalance = new Money(500.00);
        BankAccount receiverAccount = new BankAccount(
                logger,
                receiverAccountNumber,
                "Jane Smith",
                receiverInitialBalance
        );

        Money excessiveTransfer = new Money(1500.00);
        try {
            senderAccount.transfer(UUID.randomUUID().toString(), receiverAccount, excessiveTransfer);
            fail();
        } catch (InsufficientAmountException e) {
            assertEquals(1000.00, senderAccount.getBalance().amount());
            assertEquals(500.00, receiverAccount.getBalance().amount());
        }

    }

    @Test
    void shouldThrowExceptionOnConcurrentWithdrawalsWithInsufficientBalance() throws InterruptedException {

        TransactionObserver logger = (
                transactionId,
                accountNumber,
                transactionType,
                amount
        ) -> {
        };

        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(logger, accountNumber, "John Doe", initialBalance);

        Money firstWithdrawalAmount = new Money(1000.00);
        Money secondWithdrawalAmount = new Money(500.00);

        AtomicBoolean firstThreadExceptionThrown = new AtomicBoolean(false);
        AtomicBoolean secondThreadExceptionThrown = new AtomicBoolean(false);

        Thread thread1 = new Thread(() -> {
            try {
                account.withdraw(UUID.randomUUID().toString(), firstWithdrawalAmount);
            } catch (InsufficientAmountException e) {
                firstThreadExceptionThrown.set(true);
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                account.withdraw(UUID.randomUUID().toString(), secondWithdrawalAmount);
            } catch (InsufficientAmountException e) {
                secondThreadExceptionThrown.set(true);
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        if (firstThreadExceptionThrown.get()) {
            assertEquals(500, account.getBalance().amount());
        } else if (secondThreadExceptionThrown.get()) {
            assertEquals(0, account.getBalance().amount());
        } else {
            fail();
        }
    }

    @Test
    void shouldThrowExceptionOnConcurrentWithdrawalAndTransferWithInsufficientBalance() throws InterruptedException {
        TransactionObserver logger = (
                transactionId,
                accountNumber,
                transactionType,
                amount
        ) -> {
        };
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(logger, accountNumber, "John Doe", initialBalance);

        AccountNumber recipientAccountNumber = new AccountNumber("67890");
        Money recipientInitialBalance = new Money(500.00);
        BankAccount recipientAccount = new BankAccount(
                logger,
                recipientAccountNumber,
                "Jane Smith",
                recipientInitialBalance
        );

        AtomicBoolean firstThreadExceptionThrown = new AtomicBoolean(false);
        AtomicBoolean secondThreadExceptionThrown = new AtomicBoolean(false);

        Thread thread1 = new Thread(() -> {
            try {
                account.withdraw(UUID.randomUUID().toString(), new Money(1000.00)); // Reduce balance to 0
            } catch (InsufficientAmountException e) {
                firstThreadExceptionThrown.set(true);
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                account.transfer(UUID.randomUUID().toString(), recipientAccount, new Money(1000.00));
            } catch (InsufficientAmountException e) {
                secondThreadExceptionThrown.set(true);
            }
        });


        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        if (firstThreadExceptionThrown.get()) {
            assertEquals(0.0, account.getBalance().amount(), 0.01);
            assertEquals(1500.00, recipientAccount.getBalance().amount(), 0.01);
        } else if (secondThreadExceptionThrown.get()) {
            assertEquals(0.0, account.getBalance().amount(), 0.01);
            assertEquals(500.00, recipientAccount.getBalance().amount(), 0.01);
        } else {
            fail();
        }
    }

    @Test
    void shouldNotifyObserversOnDeposit() {
        AtomicBoolean notified = new AtomicBoolean(false);
        TransactionObserver logger = (
                transactionId,
                accountNumber,
                transactionType,
                amount
        ) -> {
            notified.set(true);
        };

        BankAccount account = new BankAccount(
                logger,
                new AccountNumber("12345"),
                "John Doe",
                new Money(1000.00)
        );
        account.deposit(UUID.randomUUID().toString(), new Money(500.00));

        assertTrue(notified.get());
    }


}
