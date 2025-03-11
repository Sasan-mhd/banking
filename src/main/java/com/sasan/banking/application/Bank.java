package com.sasan.banking.application;

import com.sasan.banking.domain.BankAccountRepository;
import com.sasan.banking.domain.model.AccountException;
import com.sasan.banking.domain.model.AccountNumber;
import com.sasan.banking.domain.model.BankAccount;
import com.sasan.banking.domain.model.Money;
import com.sasan.banking.domain.service.transaction.*;
import com.sasan.banking.domain.service.transaction.observer.TransactionObserver;
import com.sasan.banking.domain.service.transaction.observer.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class Bank {

    private TransactionObserver observer;
    private BankAccountRepository repository;

    @Autowired
    public Bank(TransactionObserver observer, BankAccountRepository repository) {
        this.observer = observer;
        this.repository = repository;
    }

    @Transactional
    public BankAccount createAccount(
            AccountNumber accountNumber,
            String accountHolderName,
            Money initialBalance
    ) throws AccountException {
        if (repository.exist(accountNumber)) {
            throw new AccountNumberAlreadyExistsException();
        }
        BankAccount account = BankAccount.create(accountNumber, accountHolderName);
        account.deposit(initialBalance);
        repository.save(account);
        observer.onTransaction(
                UUID.randomUUID().toString(),
                accountNumber.number(),
                TransactionType.CREDIT,
                initialBalance.amount()
        );
        return account;
    }

//    public void deposit(
//            AccountNumber accountNumber,
//            Money amount
//    ) throws AccountException {
////        synchronized (accountNumber.number()) {
//            System.out.println("Thread:" + Thread.currentThread().getId() + " Bank.deposit started");
//            BankTransaction transaction = new BankDepositTransaction(this.repository, accountNumber, amount);
//            BankTransactionRetryDecorator retryDecorator = new BankTransactionRetryDecorator(transaction, 30);
//            retryDecorator.execute();
//            String transactionId = UUID.randomUUID().toString();
//            observer.onTransaction(
//                    transactionId,
//                    accountNumber.number(),
//                    TransactionType.CREDIT,
//                    amount.amount()
//            );
//            System.out.println("Thread:" + Thread.currentThread().getId() + " Bank.deposit ended ===> transactionId:" + transactionId + " " + repository.get(accountNumber).getBalance());

    /// /        }
//    }
    @Transactional
    public void deposit(
            AccountNumber accountNumber,
            Money amount
    ) throws AccountException {
        BankTransaction transaction = new BankDepositTransaction(this.repository, accountNumber, amount);
        transaction.execute();
        String transactionId = UUID.randomUUID().toString();
        observer.onTransaction(
                transactionId,
                accountNumber.number(),
                TransactionType.CREDIT,
                amount.amount()
        );

    }

    @Transactional
    public void withdraw(
            AccountNumber accountNumber,
            Money amount
    ) throws AccountException {

        BankTransaction transaction = new BankWithdrawTransaction(this.repository, accountNumber, amount);
        transaction.execute();
        String transactionId = UUID.randomUUID().toString();
        observer.onTransaction(
                transactionId,
                accountNumber.number(),
                TransactionType.DEBIT,
                amount.amount()
        );
    }
//    public void withdraw(
//            AccountNumber accountNumber,
//            Money amount
//    ) throws AccountException {
////        synchronized (accountNumber.number()) {
//            System.out.println("Thread:" + Thread.currentThread().getId() + " Bank.withdraw started");
//            BankTransaction transaction = new BankWithdrawTransaction(this.repository, accountNumber, amount);
//            BankTransactionRetryDecorator retryDecorator = new BankTransactionRetryDecorator(transaction, 30);
//            retryDecorator.execute();
//            String transactionId = UUID.randomUUID().toString();
//            observer.onTransaction(
//                    transactionId,
//                    accountNumber.number(),
//                    TransactionType.DEBIT,
//                    amount.amount()
//            );
//            System.out.println("Thread:" + Thread.currentThread().getId() + " Bank.withdraw ended ===> transactionId:" + transactionId + " " + repository.get(accountNumber).getBalance());

    /// /        }
//    }
    @Transactional
    public void transfer(
            AccountNumber senderAccountNumber,
            AccountNumber recipientAccountNumber,
            Money amount
    ) throws AccountException {

        String firstAccountLocked;
        String secondAccountLocked;

        if (senderAccountNumber.number().compareTo(recipientAccountNumber.number()) > 0) {
            firstAccountLocked = senderAccountNumber.number();
            secondAccountLocked = recipientAccountNumber.number();
        } else {
            secondAccountLocked = senderAccountNumber.number();
            firstAccountLocked = recipientAccountNumber.number();
        }
        synchronized (firstAccountLocked) {
            synchronized (secondAccountLocked) {

                BankTransaction transaction = new BankTransferTransaction(
                        repository,
                        senderAccountNumber,
                        recipientAccountNumber,
                        amount
                );
                transaction.execute();
                String transactionId = UUID.randomUUID().toString();
                observer.onTransaction(
                        transactionId,
                        senderAccountNumber.number(),
                        TransactionType.DEBIT,
                        amount.amount()
                );
                observer.onTransaction(
                        transactionId,
                        recipientAccountNumber.number(),
                        TransactionType.CREDIT,
                        amount.amount()
                );
            }
        }

    }

    @Transactional
    public BankAccount getAccount(AccountNumber accountNumber) {
        return repository.get(accountNumber);
    }

}
