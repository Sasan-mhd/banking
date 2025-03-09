package com.sasan.banking.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankAccount {

    private AccountNumber accountNumber;
    private final String accountHolderName;
    private Money balance;
    private final List<TransactionObserver> observers = new ArrayList<>();


    public BankAccount(
            TransactionObserver observer,
            AccountNumber accountNumber,
            String accountHolderName,
            Money initialBalance) {
        this.addObserver(observer);
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        deposit(UUID.randomUUID().toString(), initialBalance);
    }

    private void addObserver(TransactionObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String transactionId, TransactionType transactionType, double amount) {
        for (TransactionObserver observer : observers) {
            observer.onTransaction(transactionId, accountNumber.number(), transactionType, amount);
        }
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public Money getBalance() {
        return balance;
    }

    public synchronized void deposit(String transactionId, Money amount) {
        if (amount.amount() > 0) {
            if (balance == null) {
                this.balance = amount;
            } else {
                this.balance = this.balance.add(amount);
            }
            notifyObservers(transactionId, TransactionType.CREDIT, amount.amount());
        }
    }

    public synchronized void withdraw(String transactionId, Money amount) throws InsufficientAmountException {
        if (amount.amount() > 0) {
            if (balance.amount() < amount.amount()) {
                throw new InsufficientAmountException();
            }
            this.balance = this.balance.subtract(amount);
            notifyObservers(transactionId, TransactionType.DEBIT, amount.amount());

        }
    }

    public void transfer(String transactionId, BankAccount recipientAccount, Money amount) throws InsufficientAmountException {
        withdraw(transactionId, amount);
        recipientAccount.deposit(transactionId, amount);
    }


    public String getAccountHolderName() {
        return accountHolderName;
    }
}
