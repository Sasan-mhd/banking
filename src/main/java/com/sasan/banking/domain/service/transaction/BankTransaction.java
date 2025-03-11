package com.sasan.banking.domain.service.transaction;

import com.sasan.banking.domain.model.AccountException;

public interface BankTransaction {
    void execute() throws AccountException;
}
