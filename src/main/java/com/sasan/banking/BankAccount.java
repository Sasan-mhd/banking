package com.sasan.banking;

import java.util.ArrayList;
import java.util.List;

public class BankAccount {

    private AccountNumber accountNumber;
    private final String accountHolderName;
    private Money balance;
    private final List<TransactionObserver> observers = new ArrayList<>();


    public BankAccount(AccountNumber accountNumber, String accountHolderName, Money initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
    }

    public void addObserver(TransactionObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String transactionId, TransactionType transactionType, double amount) {
        for (TransactionObserver observer : observers) {
            observer.onTransaction(transactionId, accountNumber.getNumber(), transactionType, amount);
        }
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public Money getBalance() {
        return balance;
    }

    public synchronized void deposit(String transactionId, Money amount) {
        if (amount.getAmount() > 0) {
            this.balance = this.balance.add(amount);
            notifyObservers(transactionId, TransactionType.CREDIT, amount.getAmount());
        }
    }

    public synchronized void withdraw(String transactionId, Money amount) throws InsufficientAmountException {
        if (amount.getAmount() > 0) {
            if (balance.getAmount() < amount.getAmount()) {
                throw new InsufficientAmountException();
            }
            this.balance = this.balance.subtract(amount);
            notifyObservers(transactionId, TransactionType.DEBIT, amount.getAmount());

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
