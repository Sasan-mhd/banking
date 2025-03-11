package com.sasan.banking.domain.service.transaction;

import com.sasan.banking.domain.model.AccountException;

public class AccountDoesNotExistException extends AccountException {
    public AccountDoesNotExistException() {
        super("The account does not exist.");
    }
}
