package com.sasan.banking;

import com.sasan.banking.application.Bank;
import com.sasan.banking.domain.model.AccountException;
import com.sasan.banking.domain.model.AccountNumber;
import com.sasan.banking.domain.model.BankAccount;
import com.sasan.banking.domain.model.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "ui.enabled=false")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BankTest {

    @Autowired
    private Bank bank;

    @Test
    void shouldCreateNewAccount() {
        AccountNumber accountNumber = new AccountNumber("12345");
        String accountHolderName = "John Doe";
        Money initialBalance = new Money(1000.00);

        BankAccount account = null;
        try {
            account = bank.createAccount(accountNumber, accountHolderName, initialBalance);
        } catch (AccountException e) {
            fail();
        }

        assertNotNull(account);
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(accountHolderName, account.getAccountHolderName());
        assertEquals(initialBalance, account.getBalance());
    }

    @Test
    void shouldDepositIntoAccount() {

        AccountNumber accountNumber = new AccountNumber("12345");
        String accountHolderName = "John Doe";
        Money initialBalance = new Money(1000.00);
        Money depositAmount = new Money(500.00);

        try {
            bank.createAccount(accountNumber, accountHolderName, initialBalance);
        } catch (AccountException e) {
            fail();
        }

        try {
            bank.deposit(accountNumber, depositAmount);
        } catch (AccountException e) {
            fail();
        }

        BankAccount account = bank.getAccount(accountNumber);
        assertEquals(initialBalance.add(depositAmount), account.getBalance());
    }

    @Test
    void shouldWithdrawFromAccount() {
        AccountNumber accountNumber = new AccountNumber("12345");
        String accountHolderName = "John Doe";
        Money initialBalance = new Money(1000.00);
        Money withdrawalAmount = new Money(500.00);

        try {
            bank.createAccount(accountNumber, accountHolderName, initialBalance);
        } catch (AccountException e) {
            fail();
        }

        try {
            bank.withdraw(accountNumber, withdrawalAmount);
        } catch (AccountException e) {
            fail();
        }

        BankAccount account = bank.getAccount(accountNumber);
        assertEquals(initialBalance.subtract(withdrawalAmount), account.getBalance());
    }

    @Test
    void shouldTransferBetweenAccounts() {
        AccountNumber senderAccountNumber = new AccountNumber("12345");
        String senderAccountHolderName = "John Doe";
        Money senderInitialBalance = new Money(1000.00);

        AccountNumber recipientAccountNumber = new AccountNumber("67890");
        String recipientAccountHolderName = "Jane Smith";
        Money recipientInitialBalance = new Money(500.00);

        Money transferAmount = new Money(500.00);

        try {
            bank.createAccount(senderAccountNumber, senderAccountHolderName, senderInitialBalance);
            bank.createAccount(recipientAccountNumber, recipientAccountHolderName, recipientInitialBalance);
        } catch (AccountException e) {
            fail();
        }

        try {
            bank.transfer(senderAccountNumber, recipientAccountNumber, transferAmount);
        } catch (AccountException e) {
            fail();
        }

        BankAccount senderAccount = bank.getAccount(senderAccountNumber);
        BankAccount recipientAccount = bank.getAccount(recipientAccountNumber);
        assertEquals(senderInitialBalance.subtract(transferAmount), senderAccount.getBalance());
        assertEquals(recipientInitialBalance.add(transferAmount), recipientAccount.getBalance());
    }




}
