package com.sasan.banking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaBankAccountEntityRepository extends JpaRepository<BankAccountEntity, String>{
    public Optional<BankAccountEntity> findByAccountNumber(String accountNumber);
}
