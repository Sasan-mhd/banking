package com.sasan.banking;

public record AccountNumber(String number) {

    public String getNumber() {
        return number;
    }
}
