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
        long delay = 100;
        long maxDelay=1000;
        while (retries < maxRetries) {
            try {
                transaction.execute();
                return;
            } catch (ConcurrentTransactionException e) {
                retries++;
                System.out.println("retries:"+retries);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                delay = Math.min(delay * 2, maxDelay); // Exponential backoff

            }
        }
        throw new RetryFailedException();
    }
}
