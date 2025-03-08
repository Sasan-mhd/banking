package com.sasan.banking;

public interface BankAccountRepository {
    public BankAccount get(AccountNumber accountNumber);
    public void save(BankAccount bankAccount);
    public boolean exist(AccountNumber accountNumber);
}
