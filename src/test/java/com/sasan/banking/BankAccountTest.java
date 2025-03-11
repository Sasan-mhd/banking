package com.sasan.banking;

import com.sasan.banking.domain.model.AccountNumber;
import com.sasan.banking.domain.model.BankAccount;
import com.sasan.banking.domain.model.InsufficientAmountException;
import com.sasan.banking.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class BankAccountTest {

    @Test
    void shouldCreateAccountWithInitialBalance() {
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);

        BankAccount account = BankAccount.create(accountNumber, "John Doe");
        account.deposit(initialBalance);

        assertEquals("12345", account.getAccountNumber().number());
        assertEquals(1000.00, account.getBalance().amount());
    }

    @Test
    void shouldDepositMoneyIntoAccount() {

        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = BankAccount.create(accountNumber, "John Doe");
        account.deposit(initialBalance);

        Money depositAmount = new Money(500.00);
        account.deposit(depositAmount);

        assertEquals(1500.00, account.getBalance().amount());
    }

    @Test
    void shouldNotDepositNegativeAmount() {

        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = BankAccount.create(accountNumber, "John Doe");
        account.deposit(initialBalance);

        Money negativeDeposit = new Money(-500.00);
        account.deposit(negativeDeposit);

        assertEquals(1000.00, account.getBalance().amount());
    }

    @Test
    void shouldWithdrawMoneyFromAccount() {

        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = BankAccount.create(accountNumber, "John Doe");
        account.deposit(initialBalance);
        Money withdrawalAmount = new Money(500.00);
        try {
            account.withdraw(withdrawalAmount);
        } catch (Exception e) {
            fail();
        }

        assertEquals(500.00, account.getBalance().amount());
    }

    @Test
    void shouldNotWithdrawMoreThanAvailableBalance() {
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = BankAccount.create(accountNumber, "John Doe");
        account.deposit(initialBalance);
        Money excessiveWithdrawal = new Money(1500.00);
        try {
            account.withdraw(excessiveWithdrawal);
            fail();
        } catch (InsufficientAmountException e) {
            assertEquals(1000.00, account.getBalance().amount());
        }

    }

    @Test
    void shouldTransferMoneyBetweenAccounts() {
        AccountNumber senderAccountNumber = new AccountNumber("12345");
        Money senderInitialBalance = new Money(1000.00);
        BankAccount senderAccount = BankAccount.create(senderAccountNumber, "John Doe");
        senderAccount.deposit(senderInitialBalance);

        AccountNumber receiverAccountNumber = new AccountNumber("67890");
        Money receiverInitialBalance = new Money(500.00);
        BankAccount receiverAccount = BankAccount.create(receiverAccountNumber, "Jane Smith");
        receiverAccount.deposit(receiverInitialBalance);

        Money transferAmount = new Money(300.00);
        try {
            senderAccount.transfer(receiverAccount, transferAmount);
        } catch (Exception e) {
            fail();
        }

        assertEquals(700.00, senderAccount.getBalance().amount());
        assertEquals(800.00, receiverAccount.getBalance().amount());
    }

    @Test
    void shouldNotTransferMoreThanAvailableBalance() {
        AccountNumber senderAccountNumber = new AccountNumber("12345");
        Money senderInitialBalance = new Money(1000.00);
        BankAccount senderAccount = BankAccount.create(senderAccountNumber, "John Doe");
        senderAccount.deposit(senderInitialBalance);

        AccountNumber receiverAccountNumber = new AccountNumber("67890");
        Money receiverInitialBalance = new Money(500.00);
        BankAccount receiverAccount = BankAccount.create(receiverAccountNumber, "Jane Smith");
        receiverAccount.deposit(receiverInitialBalance);

        Money excessiveTransfer = new Money(1500.00);
        try {
            senderAccount.transfer(receiverAccount, excessiveTransfer);
            fail();
        } catch (InsufficientAmountException e) {
            assertEquals(1000.00, senderAccount.getBalance().amount());
            assertEquals(500.00, receiverAccount.getBalance().amount());
        }

    }

    @Test
    void shouldThrowExceptionOnConcurrentWithdrawalsWithInsufficientBalance() throws InterruptedException {
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = BankAccount.create(accountNumber, "John Doe");
        account.deposit(initialBalance);

        Money firstWithdrawalAmount = new Money(1000.00);
        Money secondWithdrawalAmount = new Money(500.00);

        AtomicBoolean firstThreadExceptionThrown = new AtomicBoolean(false);
        AtomicBoolean secondThreadExceptionThrown = new AtomicBoolean(false);

        Thread thread1 = new Thread(() -> {
            try {
                account.withdraw(firstWithdrawalAmount);
            } catch (InsufficientAmountException e) {
                firstThreadExceptionThrown.set(true);
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                account.withdraw(secondWithdrawalAmount);
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
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = BankAccount.create(accountNumber, "John Doe");
        account.deposit(initialBalance);

        AccountNumber recipientAccountNumber = new AccountNumber("67890");
        Money recipientInitialBalance = new Money(500.00);
        BankAccount recipientAccount = BankAccount.create(recipientAccountNumber, "Jane Smith");
        recipientAccount.deposit(recipientInitialBalance);

        AtomicBoolean firstThreadExceptionThrown = new AtomicBoolean(false);
        AtomicBoolean secondThreadExceptionThrown = new AtomicBoolean(false);

        Thread thread1 = new Thread(() -> {
            try {
                account.withdraw(new Money(1000.00)); // Reduce balance to 0
            } catch (InsufficientAmountException e) {
                firstThreadExceptionThrown.set(true);
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                account.transfer(recipientAccount, new Money(1000.00));
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

}
