package com.sasan.banking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class Bank {

    private Map<AccountNumber, BankAccount> accounts = new HashMap<>();

    private TransactionObserver logger;

    @Autowired
    public Bank(TransactionObserver logger) {
        this.logger = logger;
    }

    public BankAccount createAccount(AccountNumber accountNumber, String accountHolderName, Money initialBalance) {
        //TODO: check for existence of accountNumber. . .
        BankAccount account = new BankAccount(logger, accountNumber, accountHolderName, initialBalance);
        accounts.put(accountNumber, account);
        return account;
    }

    public void deposit(AccountNumber accountNumber, Money amount) {
        BankAccount account = accounts.get(accountNumber);
        if (account != null) {
            account.deposit(UUID.randomUUID().toString(), amount);
        }//TODO: else throw exception. . .
    }

    public void withdraw(AccountNumber accountNumber, Money amount) throws InsufficientAmountException {
        BankAccount account = accounts.get(accountNumber);
        if (account != null) {
            account.withdraw(UUID.randomUUID().toString(), amount);
        }//TODO: else throw exception . . .
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
        }//TODO: else throw exception. . .
    }


    public BankAccount getAccount(AccountNumber accountNumber) {
        return accounts.get(accountNumber);
    }
}
