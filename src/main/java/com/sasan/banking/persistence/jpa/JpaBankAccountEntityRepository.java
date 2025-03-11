package com.sasan.banking.persistence.jpa;

import com.sasan.banking.persistence.jpa.model.BankAccountEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaBankAccountEntityRepository extends JpaRepository<BankAccountEntity, String>{
    @Lock(LockModeType.PESSIMISTIC_READ)
    public Optional<BankAccountEntity> findByAccountNumber(String accountNumber);
}
