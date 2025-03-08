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

    public synchronized void deposit(Money amount) {
        if (amount.getAmount() > 0) {
            this.balance = this.balance.add(amount);
        }
    }

    public synchronized void withdraw(Money amount) throws InsufficientAmountException {
        if (amount.getAmount() > 0) {
            if (balance.getAmount() < amount.getAmount()) {
                throw new InsufficientAmountException();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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
}
