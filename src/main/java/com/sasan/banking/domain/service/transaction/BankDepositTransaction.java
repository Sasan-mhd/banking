package com.sasan.banking.domain.service.transaction;

import com.sasan.banking.domain.model.AccountException;
import com.sasan.banking.domain.model.AccountNumber;
import com.sasan.banking.domain.model.BankAccount;
import com.sasan.banking.domain.model.Money;
import com.sasan.banking.domain.BankAccountRepository;

import java.util.UUID;

public class BankDepositTransaction implements BankTransaction {
    private final BankAccountRepository repository;
    private final AccountNumber accountNumber;
    private final Money amount;

    public BankDepositTransaction(BankAccountRepository repository, AccountNumber accountNumber, Money amount) {
        this.repository = repository;
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    @Override
    public void execute() throws AccountException {
        BankAccount account = repository.get(accountNumber);
        if (account == null) {
            throw new AccountDoesNotExistException();
        }
        account.deposit(amount);
        repository.save(account);

    }
}
