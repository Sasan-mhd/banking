package com.sasan.banking;

public record Money(double amount) {
    public double getAmount() {
        return amount;
    }
}
