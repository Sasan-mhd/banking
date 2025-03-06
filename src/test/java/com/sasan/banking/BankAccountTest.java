package com.sasan.banking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankAccountTest {

    @Test
    void shouldCreateAccountWithInitialBalance() {
        AccountNumber accountNumber = new AccountNumber("12345");
        Money initialBalance = new Money(1000.00);
        BankAccount account = new BankAccount(accountNumber, "Isac Cristantine", initialBalance);

        assertEquals("12345", account.getAccountNumber().getNumber());
        assertEquals(1000.00, account.getBalance().getAmount());
    }



}
