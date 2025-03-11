package com.sasan.banking.persistence.jpa;

import com.sasan.banking.domain.BankAccountRepository;
import com.sasan.banking.domain.ConcurrentTransactionException;
import com.sasan.banking.domain.model.AccountNumber;
import com.sasan.banking.domain.model.BankAccount;
import com.sasan.banking.domain.model.Money;
import com.sasan.banking.persistence.jpa.model.BankAccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaBankAccountRepository implements BankAccountRepository {

    private final JpaBankAccountEntityRepository jpaRepository;

    @Autowired
    public JpaBankAccountRepository(JpaBankAccountEntityRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public BankAccount get(AccountNumber accountNumber) {
        Optional<BankAccountEntity> jpaEntity = jpaRepository.findByAccountNumber(accountNumber.number());
        return jpaEntity.map(entity ->
                BankAccount.load(
                        new AccountNumber(entity.getAccountNumber()),
                        entity.getAccountHolderName(),
                        new Money(entity.getBalance())
                )
        ).orElse(null);
    }

    @Override
    public void save(BankAccount bankAccount) throws ConcurrentTransactionException {
        Optional<BankAccountEntity> existingAccount = jpaRepository.findByAccountNumber(
                bankAccount.getAccountNumber().number()
        );

        if (existingAccount.isPresent()) {
            update(bankAccount, existingAccount.get());
        } else {
            jpaRepository.save(new BankAccountEntity(
                            bankAccount.getAccountNumber().number(),
                            bankAccount.getAccountHolderName(),
                            bankAccount.getBalance().amount()
                    )
            );
        }
    }

    private void update(BankAccount bankAccount, BankAccountEntity existingAccount) throws ConcurrentTransactionException {

        existingAccount.setBalance(bankAccount.getBalance().amount());
        try {
            jpaRepository.save(existingAccount);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ConcurrentTransactionException();
        } catch (Exception e){
            System.out.println("FUCK"+e.getMessage());
            throw new RuntimeException();
        }
    }

    @Override
    public boolean exist(AccountNumber accountNumber) {
        return jpaRepository.existsById(accountNumber.number());
    }
}
