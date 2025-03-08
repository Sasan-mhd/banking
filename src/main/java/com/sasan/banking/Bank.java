package com.sasan.banking;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Bank {

    private Map<AccountNumber, BankAccount> accounts = new HashMap<>();
    private final TransactionObserver logger;

    public Bank(String logFilePath) {
        this.logger = new TransactionLogger(logFilePath);
    }

    public BankAccount createAccount(AccountNumber accountNumber, String accountHolderName, Money initialBalance) {
        BankAccount account = new BankAccount(accountNumber, accountHolderName, initialBalance);
        account.addObserver(logger);
        accounts.put(accountNumber, account);
        return account;
    }

    public void deposit(AccountNumber accountNumber, Money amount) {
        BankAccount account = accounts.get(accountNumber);
        if (account != null) {
            account.deposit(UUID.randomUUID().toString(), amount);
        }
    }

    public void withdraw(AccountNumber accountNumber, Money amount) throws InsufficientAmountException {
        BankAccount account = accounts.get(accountNumber);
        if (account != null) {
            account.withdraw(UUID.randomUUID().toString(), amount);
        }
    }

    public void transfer(
            AccountNumber senderAccountNumber,
            AccountNumber recipientAccountNumber,
            Money amount
    ) throws InsufficientAmountException {

        BankAccount senderAccount = accounts.get(senderAccountNumber);
        BankAccount recipientAccount = accounts.get(recipientAccountNumber);

        if (senderAccount != null && recipientAccount != null) {
            senderAccount.transfer(UUID.randomUUID().toString(), recipientAccount, amount);
        }
    }

}
