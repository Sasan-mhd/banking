package com.sasan.banking.domain.model;

public abstract class AccountException extends Exception{

    public AccountException(String message) {
        super(message);
    }

}
