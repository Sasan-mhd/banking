package com.sasan.banking.domain;

import com.sasan.banking.domain.model.AccountException;

public class ConcurrentTransactionException extends AccountException {
    public ConcurrentTransactionException() {
        super("Concurrent transaction on the account.");
    }
}
