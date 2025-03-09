package com.sasan.banking.domain;

import com.sasan.banking.domain.model.AccountNumber;
import com.sasan.banking.domain.model.BankAccount;

public interface BankAccountRepository {
    public BankAccount get(AccountNumber accountNumber);
    public void save(BankAccount bankAccount);
    public boolean exist(AccountNumber accountNumber);
}
