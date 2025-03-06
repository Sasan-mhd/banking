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
            this.balance = this.balance.add(amount);
        }
    }

    public void withdraw(Money amount) {
        if (amount.getAmount() > 0 && balance.getAmount() >= amount.getAmount()) {
            this.balance = this.balance.subtract(amount);
        }
    }

    public void transfer(BankAccount recipientAccount, Money amount) {
        if (amount.getAmount() > 0 && this.balance.getAmount() >= amount.getAmount()) {
            this.withdraw(amount);
            recipientAccount.deposit(amount);
        }
    }
}
