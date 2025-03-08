package com.sasan.banking;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BankTest {

    @Test
    void shouldCreateNewAccount() {
        Bank bank = new Bank(new TransactionLogger("transaction.log"));
        AccountNumber accountNumber = new AccountNumber("12345");
        String accountHolderName = "John Doe";
        Money initialBalance = new Money(1000.00);

        BankAccount account = bank.createAccount(accountNumber, accountHolderName, initialBalance);

        assertNotNull(account);
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(accountHolderName, account.getAccountHolderName());
        assertEquals(initialBalance, account.getBalance());
    }

    @Test
    void shouldDepositIntoAccount() {

        Bank bank = new Bank(new TransactionLogger("transaction.log"));
        AccountNumber accountNumber = new AccountNumber("12345");
        String accountHolderName = "John Doe";
        Money initialBalance = new Money(1000.00);
        Money depositAmount = new Money(500.00);

        BankAccount account = bank.createAccount(accountNumber, accountHolderName, initialBalance);

        bank.deposit(accountNumber, depositAmount);

        assertEquals(initialBalance.add(depositAmount), account.getBalance());
    }

    @Test
    void shouldWithdrawFromAccount() {
        Bank bank = new Bank(new TransactionLogger("transaction.log"));
        AccountNumber accountNumber = new AccountNumber("12345");
        String accountHolderName = "John Doe";
        Money initialBalance = new Money(1000.00);
        Money withdrawalAmount = new Money(500.00);

        BankAccount account = bank.createAccount(accountNumber, accountHolderName, initialBalance);

        try {
            bank.withdraw(accountNumber, withdrawalAmount);
        } catch (InsufficientAmountException e) {
            fail();
        }

        assertEquals(initialBalance.subtract(withdrawalAmount), account.getBalance());
    }

    @Test
    void shouldTransferBetweenAccounts() {
        Bank bank = new Bank(new TransactionLogger("transaction.log"));
        AccountNumber senderAccountNumber = new AccountNumber("12345");
        String senderAccountHolderName = "John Doe";
        Money senderInitialBalance = new Money(1000.00);

        AccountNumber recipientAccountNumber = new AccountNumber("67890");
        String recipientAccountHolderName = "Jane Smith";
        Money recipientInitialBalance = new Money(500.00);

        Money transferAmount = new Money(500.00);

        BankAccount senderAccount = bank.createAccount(senderAccountNumber, senderAccountHolderName, senderInitialBalance);
        BankAccount recipientAccount = bank.createAccount(recipientAccountNumber, recipientAccountHolderName, recipientInitialBalance);

        try {
            bank.transfer(senderAccountNumber, recipientAccountNumber, transferAmount);
        } catch (InsufficientAmountException e) {
            fail();
        }

        assertEquals(senderInitialBalance.subtract(transferAmount), senderAccount.getBalance());
        assertEquals(recipientInitialBalance.add(transferAmount), recipientAccount.getBalance());
    }




}
