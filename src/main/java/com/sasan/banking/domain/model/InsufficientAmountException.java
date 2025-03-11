package com.sasan.banking.domain.model;

public class InsufficientAmountException extends AccountException {
    public InsufficientAmountException() {
        super("The amount in your account is insufficient.");
    }
}
