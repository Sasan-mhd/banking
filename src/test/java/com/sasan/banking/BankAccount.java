package com.sasan.banking;

public class BankAccount {

    private AccountNumber accountNumber;
    private final String accountHolderName;
    private Money balance;

    public BankAccount(AccountNumber accountNumber, String accountHolderName, Money initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
    }


    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public Money getBalance() {
        return balance;
    }

    public void deposit(Money amount) {
        if (amount.getAmount() > 0) {
            this.balance = new Money(amount.getAmount() + balance.getAmount());
        }
    }
}
