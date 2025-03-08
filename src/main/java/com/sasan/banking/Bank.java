package com.sasan.banking;

import java.util.HashMap;
import java.util.Map;

public class Bank {
    private Map<AccountNumber, BankAccount> accounts = new HashMap<>();

    public BankAccount createAccount(AccountNumber accountNumber, String accountHolderName, Money initialBalance) {
        BankAccount account = new BankAccount(accountNumber, accountHolderName, initialBalance);
        accounts.put(accountNumber, account);
        return account;
    }

    public void deposit(AccountNumber accountNumber, Money amount) {
        BankAccount account = accounts.get(accountNumber);
        if (account != null) {
            account.deposit(amount);
        }
    }

    public void withdraw(AccountNumber accountNumber, Money amount) throws InsufficientAmountException {
        BankAccount account = accounts.get(accountNumber);
        if (account != null) {
            account.withdraw(amount);
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
            senderAccount.transfer(recipientAccount, amount);
        }
    }

}
