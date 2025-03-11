package com.sasan.banking.domain.service.transaction.retrydecorator;

import com.sasan.banking.domain.model.AccountException;

public class RetryFailedException extends AccountException {
    public RetryFailedException() {
        super("The retry mechanism has failed. Please try again or contact support if the issue persists.");
    }
}
