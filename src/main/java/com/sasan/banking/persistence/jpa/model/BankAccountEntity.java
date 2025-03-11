package com.sasan.banking.persistence.jpa.model;

import jakarta.persistence.*;

@Entity
public class BankAccountEntity {
    @Id
    private String accountNumber;
    private String accountHolderName;
    private double balance;
    @Version
    private Long version;

    private BankAccountEntity(){}
    public BankAccountEntity(String accountNumber, String accountHolderName, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public double getBalance() {
        return balance;
    }

    public Long getVersion() {
        return version;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}