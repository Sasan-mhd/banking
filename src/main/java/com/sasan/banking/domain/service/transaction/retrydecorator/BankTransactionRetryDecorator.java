package com.sasan.banking.domain.service.transaction.retrydecorator;

import com.sasan.banking.domain.ConcurrentTransactionException;
import com.sasan.banking.domain.model.AccountException;
import com.sasan.banking.domain.service.transaction.BankTransaction;

public class BankTransactionRetryDecorator implements BankTransaction {

    private final BankTransaction transaction;
    private final int maxRetries;


    public BankTransactionRetryDecorator(BankTransaction transaction, int maxRetries) {
        this.transaction = transaction;
        this.maxRetries = maxRetries;
    }

    @Override
    public void execute() throws AccountException {
        int retries = 0;
        while (retries < maxRetries) {
            try {
                transaction.execute();
                return;
            } catch (ConcurrentTransactionException e) {
                retries++;
//                System.out.println("retries:"+retries);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new RetryFailedException();
    }
}
