package com.sasan.banking;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BankAccountTest {

    @Test
    void shouldCreateAccountWithInitialBalance() {
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(accountNumber, "John Doe", initialBalance);

        assertEquals("12345", account.getAccountNumber().getNumber());
        assertEquals(1000.00, account.getBalance().getAmount());
    }

    @Test
    void shouldDepositMoneyIntoAccount() {
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(accountNumber, "John Doe", initialBalance);

        Money depositAmount = new Money(500.00);
        account.deposit(UUID.randomUUID().toString(), depositAmount);

        assertEquals(1500.00, account.getBalance().getAmount());
    }

    @Test
    void shouldNotDepositNegativeAmount() {
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(accountNumber, "John Doe", initialBalance);

        Money negativeDeposit = new Money(-500.00);
        account.deposit(UUID.randomUUID().toString(), negativeDeposit);

        assertEquals(1000.00, account.getBalance().getAmount());
    }

    @Test
    void shouldWithdrawMoneyFromAccount() {
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(accountNumber, "John Doe", initialBalance);

        Money withdrawalAmount = new Money(500.00);
        try {
            account.withdraw(UUID.randomUUID().toString(), withdrawalAmount);
        } catch (Exception e) {
            fail();
        }

        assertEquals(500.00, account.getBalance().getAmount());
    }

    @Test
    void shouldNotWithdrawMoreThanAvailableBalance() {
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(accountNumber, "John Doe", initialBalance);

        Money excessiveWithdrawal = new Money(1500.00);
        try {
            account.withdraw(UUID.randomUUID().toString(), excessiveWithdrawal);
            fail();
        } catch (InsufficientAmountException e) {
            assertEquals(1000.00, account.getBalance().getAmount());
        }

    }

    @Test
    void shouldTransferMoneyBetweenAccounts() {
        AccountNumber senderAccountNumber = new AccountNumber("12345");
        Money senderInitialBalance = new Money(1000.00);
        BankAccount senderAccount = new BankAccount(senderAccountNumber, "John Doe", senderInitialBalance);

        AccountNumber receiverAccountNumber = new AccountNumber("67890");
        Money receiverInitialBalance = new Money(500.00);
        BankAccount receiverAccount = new BankAccount(receiverAccountNumber, "Jane Smith", receiverInitialBalance);

        Money transferAmount = new Money(300.00);
        try {
            senderAccount.transfer(UUID.randomUUID().toString(), receiverAccount, transferAmount);
        } catch (Exception e) {
            fail();
        }

        assertEquals(700.00, senderAccount.getBalance().getAmount());
        assertEquals(800.00, receiverAccount.getBalance().getAmount());
    }

    @Test
    void shouldNotTransferMoreThanAvailableBalance() {
        AccountNumber senderAccountNumber = new AccountNumber("12345");
        Money senderInitialBalance = new Money(1000.00);
        BankAccount senderAccount = new BankAccount(senderAccountNumber, "John Doe", senderInitialBalance);

        AccountNumber receiverAccountNumber = new AccountNumber("67890");
        Money receiverInitialBalance = new Money(500.00);
        BankAccount receiverAccount = new BankAccount(receiverAccountNumber, "Jane Smith", receiverInitialBalance);

        Money excessiveTransfer = new Money(1500.00);
        try {
            senderAccount.transfer(UUID.randomUUID().toString(), receiverAccount, excessiveTransfer);
            fail();
        } catch (InsufficientAmountException e) {
            assertEquals(1000.00, senderAccount.getBalance().getAmount());
            assertEquals(500.00, receiverAccount.getBalance().getAmount());
        }

    }

    @Test
    void shouldThrowExceptionOnConcurrentWithdrawalsWithInsufficientBalance() throws InterruptedException {

        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(accountNumber, "John Doe", initialBalance);

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
            assertEquals(500, account.getBalance().getAmount());
        } else if (secondThreadExceptionThrown.get()) {
            assertEquals(0, account.getBalance().getAmount());
        } else {
            fail();
        }
    }

    @Test
    void shouldThrowExceptionOnConcurrentWithdrawalAndTransferWithInsufficientBalance() throws InterruptedException {
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(accountNumber, "John Doe", initialBalance);

        AccountNumber recipientAccountNumber = new AccountNumber("67890");
        Money recipientInitialBalance = new Money(500.00);
        BankAccount recipientAccount = new BankAccount(recipientAccountNumber, "Jane Smith", recipientInitialBalance);

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
            assertEquals(0.0, account.getBalance().getAmount(), 0.01);
            assertEquals(1500.00, recipientAccount.getBalance().getAmount(), 0.01);
        } else if (secondThreadExceptionThrown.get()) {
            assertEquals(0.0, account.getBalance().getAmount(), 0.01);
            assertEquals(500.00, recipientAccount.getBalance().getAmount(), 0.01);
        } else {
            fail();
        }
    }


}
