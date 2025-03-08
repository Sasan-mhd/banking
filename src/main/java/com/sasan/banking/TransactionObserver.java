package com.sasan.banking;

public interface TransactionObserver {

    void onTransaction(
            String transactionId,
            String accountNumber,
            TransactionType transactionType,
            double amount
    );
}
