package com.sasan.banking.domain;

import com.sasan.banking.domain.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class Bank {

    private TransactionObserver logger;
    private BankAccountRepository repository;

    @Autowired
    public Bank(TransactionObserver logger, BankAccountRepository repository) {
        this.logger = logger;
        this.repository = repository;
    }

    public BankAccount createAccount(
            AccountNumber accountNumber,
            String accountHolderName,
            Money initialBalance
    ) throws AccountNumberAlreadyExistsException {
        if (repository.exist(accountNumber)) {
            throw new AccountNumberAlreadyExistsException();
        }
        BankAccount account = new BankAccount(logger, accountNumber, accountHolderName, initialBalance);
        repository.save(account);
        return account;
    }

    public void deposit(AccountNumber accountNumber, Money amount) throws AccountDoesNotExistException {
        BankAccount account = repository.get(accountNumber);
        if (account == null) {
            throw new AccountDoesNotExistException();
        }
        account.deposit(UUID.randomUUID().toString(), amount);
        repository.save(account);
    }

    public void withdraw(
            AccountNumber accountNumber,
            Money amount
    ) throws InsufficientAmountException, AccountDoesNotExistException {
        BankAccount account = repository.get(accountNumber);
        if (account == null) {
            throw new AccountDoesNotExistException();
        }
        account.withdraw(UUID.randomUUID().toString(), amount);
        repository.save(account);
    }

    public void transfer(
            AccountNumber senderAccountNumber,
            AccountNumber recipientAccountNumber,
            Money amount
    ) throws InsufficientAmountException, AccountDoesNotExistException {

        BankAccount senderAccount = repository.get(senderAccountNumber);
        BankAccount recipientAccount = repository.get(recipientAccountNumber);

        if (senderAccount == null || recipientAccount == null) {
            throw new AccountDoesNotExistException();
        }
        senderAccount.transfer(UUID.randomUUID().toString(), recipientAccount, amount);
        repository.save(senderAccount);
        repository.save(recipientAccount);
    }


    public BankAccount getAccount(AccountNumber accountNumber) {
        return repository.get(accountNumber);
    }
}
