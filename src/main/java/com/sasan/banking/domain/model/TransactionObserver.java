package com.sasan.banking.domain.model;

public interface TransactionObserver {

    void onTransaction(
            String transactionId,
            String accountNumber,
            TransactionType transactionType,
            double amount
    );
}
