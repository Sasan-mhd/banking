package com.sasan.banking.domain.model;

public record Money(double amount) {

    public Money add(Money other) {
        return new Money(this.amount + other.amount());
    }

    public Money subtract(Money other) {
        return new Money(this.amount - other.amount());
    }
}
