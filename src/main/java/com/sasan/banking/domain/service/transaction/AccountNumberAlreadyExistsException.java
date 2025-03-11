package com.sasan.banking.domain.service.transaction;

import com.sasan.banking.domain.model.AccountException;

public class AccountNumberAlreadyExistsException extends AccountException {
    public AccountNumberAlreadyExistsException() {
        super("The account number already exists.");
    }
}
