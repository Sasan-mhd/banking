package com.sasan.banking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        account.deposit(depositAmount);

        assertEquals(1500.00, account.getBalance().getAmount());
    }

    @Test
    void shouldNotDepositNegativeAmount() {
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(accountNumber, "John Doe", initialBalance);

        Money negativeDeposit = new Money(-500.00);
        account.deposit(negativeDeposit);

        assertEquals(1000.00, account.getBalance().getAmount());
    }

    @Test
    void shouldWithdrawMoneyFromAccount() {
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(accountNumber, "John Doe", initialBalance);

        Money withdrawalAmount = new Money(500.00);
        account.withdraw(withdrawalAmount);

        assertEquals(500.00, account.getBalance().getAmount());
    }

    @Test
    void shouldNotWithdrawMoreThanAvailableBalance() {
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(accountNumber, "John Doe", initialBalance);

        Money excessiveWithdrawal = new Money(1500.00);
        account.withdraw(excessiveWithdrawal);

        assertEquals(1000.00, account.getBalance().getAmount());
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
        senderAccount.transfer(receiverAccount, transferAmount);

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
        senderAccount.transfer(receiverAccount, excessiveTransfer);

        assertEquals(1000.00, senderAccount.getBalance().getAmount());
        assertEquals(500.00, receiverAccount.getBalance().getAmount());
    }


}
