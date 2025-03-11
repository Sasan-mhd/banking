package com.sasan.banking.domain.model;

import com.sasan.banking.domain.service.transaction.observer.TransactionObserver;
import com.sasan.banking.domain.service.transaction.observer.TransactionType;

import java.util.Objects;

public class BankAccount {

    private AccountNumber accountNumber;
    private final String accountHolderName;
    private Money balance;

    private BankAccount(AccountNumber accountNumber, String accountHolderName, Money balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
    }

    private BankAccount(AccountNumber accountNumber, String accountHolderName) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
    }

    public static BankAccount create(AccountNumber accountNumber, String accountHolderName) {
        return new BankAccount(accountNumber, accountHolderName);
    }

    public static BankAccount load(AccountNumber accountNumber, String accountHolderName, Money balance) {
        return new BankAccount(accountNumber, accountHolderName, balance);
    }


    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public Money getBalance() {
        return balance;
    }

    public synchronized void deposit(Money amount) {
        if (amount.amount() > 0) {
            if (balance == null) {
                this.balance = amount;
            } else {
                this.balance = this.balance.add(amount);
            }

        }
    }

    public synchronized void withdraw(Money amount) throws InsufficientAmountException {
        if (amount.amount() > 0) {
            if (balance.amount() < amount.amount()) {
                throw new InsufficientAmountException();
            }
            this.balance = this.balance.subtract(amount);
        }
    }

    public void transfer(BankAccount recipientAccount, Money amount) throws InsufficientAmountException {
        withdraw(amount);
        recipientAccount.deposit(amount);
    }


    public String getAccountHolderName() {
        return accountHolderName;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BankAccount that)) return false;
        return Objects.equals(accountNumber, that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountNumber);
    }
}
