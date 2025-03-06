package com.sasan.banking;

public class BankAccount {

    private AccountNumber accountNumber;
    private Money balance;

    public BankAccount(AccountNumber accountNumber, Money initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }


    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public Money getBalance() {
        return balance;
    }
}
