package com.sasan.banking;

public record Money(double amount) {
    public double getAmount() {
        return amount;
    }

    public Money add(Money other) {
        return new Money(this.amount + other.getAmount());
    }

    public Money subtract(Money other) {
        return new Money(this.amount - other.getAmount());
    }
}
