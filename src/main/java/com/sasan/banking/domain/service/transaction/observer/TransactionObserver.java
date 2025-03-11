package com.sasan.banking.domain.service.transaction.observer;

public interface TransactionObserver {

    void onTransaction(
            String transactionId,
            String accountNumber,
            TransactionType transactionType,
            double amount
    );

}
