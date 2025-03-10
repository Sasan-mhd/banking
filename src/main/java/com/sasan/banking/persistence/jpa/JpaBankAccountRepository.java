package com.sasan.banking.persistence.jpa;

import com.sasan.banking.domain.*;
import com.sasan.banking.domain.model.AccountNumber;
import com.sasan.banking.domain.model.BankAccount;
import com.sasan.banking.domain.model.Money;
import com.sasan.banking.domain.model.TransactionObserver;
import com.sasan.banking.persistence.jpa.model.BankAccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaBankAccountRepository implements BankAccountRepository {

    private final JpaBankAccountEntityRepository jpaRepository;
    private final TransactionObserver observer;

    @Autowired
    public JpaBankAccountRepository(JpaBankAccountEntityRepository jpaRepository, TransactionObserver observer) {
        this.jpaRepository = jpaRepository;
        this.observer = observer;
    }


    @Override
    public BankAccount get(AccountNumber accountNumber) {
        Optional<BankAccountEntity> jpaEntity = jpaRepository.findByAccountNumber(accountNumber.number());
        return jpaEntity.map(entity ->
                new BankAccount(
                        observer,
                        new AccountNumber(entity.getAccountNumber()),
                        entity.getAccountHolderName(),
                        new Money(entity.getBalance())
                )
        ).orElse(null);
    }

    @Override
    public void save(BankAccount bankAccount) {
        jpaRepository.save(new BankAccountEntity(
                        bankAccount.getAccountNumber().number(),
                        bankAccount.getAccountHolderName(),
                        bankAccount.getBalance().amount()
                )
        );
    }

    @Override
    public boolean exist(AccountNumber accountNumber) {
        return jpaRepository.existsById(accountNumber.number());
    }
}
