package com.sasan.banking.domain.service.transaction;

import com.sasan.banking.domain.model.AccountException;
import com.sasan.banking.domain.model.AccountNumber;
import com.sasan.banking.domain.model.BankAccount;
import com.sasan.banking.domain.model.Money;
import com.sasan.banking.domain.BankAccountRepository;

import java.util.UUID;

public class BankTransferTransaction implements BankTransaction {
    private final BankAccountRepository repository;
    private final AccountNumber senderAccountNumber;
    private final AccountNumber recipientAccountNumber;
    private final Money amount;

    public BankTransferTransaction(
            BankAccountRepository repository,
            AccountNumber senderAccountNumber,
            AccountNumber recipientAccountNumber,
            Money amount) {
        this.repository = repository;
        this.senderAccountNumber = senderAccountNumber;
        this.recipientAccountNumber = recipientAccountNumber;
        this.amount = amount;
    }

    @Override
    public void execute() throws AccountException {
        BankAccount senderAccount = repository.get(senderAccountNumber);
        BankAccount recipientAccount = repository.get(recipientAccountNumber);

        if (senderAccount == null || recipientAccount == null) {
            throw new AccountDoesNotExistException();
        }
        senderAccount.transfer(recipientAccount, amount);
        repository.save(senderAccount);
        repository.save(recipientAccount);
    }
}
