package com.sasan.banking;

import com.sasan.banking.application.Bank;
import com.sasan.banking.domain.model.AccountException;
import com.sasan.banking.domain.model.AccountNumber;
import com.sasan.banking.domain.model.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "ui.enabled=false")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ConcurrencyLoadTest {

    @Autowired
    private Bank bank;

    @Test
    void testConcurrentDepositsAndWithdrawals() throws InterruptedException, ExecutionException, AccountException {
        AccountNumber accountNumber = new AccountNumber("88888");
        bank.createAccount(accountNumber, "Test User", new Money(1000));

        int numThreads = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        List<Future<Void>> futures = new ArrayList<>();

        for (int i = 0; i < numThreads / 2; i++) {
            int finalI = i;
            futures.add(executor.submit(() -> {
                System.out.println("Thread:"+Thread.currentThread().getId()+"future_deposit"+finalI+"started.");
                bank.deposit(accountNumber, new Money(10));
                System.out.println("Thread:"+Thread.currentThread().getId()+"future_deposit"+finalI+"ended.");
                latch.countDown();
                return null;
            }));
            futures.add(executor.submit(() -> {
                System.out.println("Thread:"+Thread.currentThread().getId()+"future_withdraw"+finalI+"started.");
                bank.withdraw(accountNumber, new Money(10));
                System.out.println("Thread:"+Thread.currentThread().getId()+"future_withdraw"+finalI+"ended.");
                latch.countDown();
                return null;
            }));
        }

        latch.await(10, TimeUnit.SECONDS);

        for (Future<Void> future : futures) {
            future.get();
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        assertEquals(new Money(1000), bank.getAccount(accountNumber).getBalance(),
                "Balance should remain unchanged after equal concurrent deposits and withdrawals");
    }

    @Test
    void testConcurrentTransfers() throws InterruptedException, ExecutionException, AccountException {
        AccountNumber account1 = new AccountNumber("12345");
        AccountNumber account2 = new AccountNumber("67890");
        bank.createAccount(account1, "User 1", new Money(1000));
        bank.createAccount(account2, "User 2", new Money(1000));

        int numTransfers = 600;
        ExecutorService executor = Executors.newFixedThreadPool(6);
        CountDownLatch latch = new CountDownLatch(numTransfers * 2);

        for (int i = 0; i < numTransfers; i++) {
            executor.submit(() -> {
                try {
                    bank.transfer(account1, account2, new Money(1));
                } catch (AccountException e) {
                    throw new RuntimeException(e);
                }
                latch.countDown();
            });
            executor.submit(() -> {
                try {
                    bank.transfer(account2, account1, new Money(1));
                } catch (AccountException e) {
                    throw new RuntimeException(e);
                }
                latch.countDown();
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        assertEquals(new Money(1000), bank.getAccount(account1).getBalance(),
                "Account 1 balance should remain unchanged after equal back-and-forth transfers");
        assertEquals(new Money(1000), bank.getAccount(account2).getBalance(),
                "Account 2 balance should remain unchanged after equal back-and-forth transfers");
    }

//    @Test
//    void testConcurrentAccountCreation() throws InterruptedException, ExecutionException {
//        int numAccounts = 1000;
//        ExecutorService executor = Executors.newFixedThreadPool(10);
//        CountDownLatch latch = new CountDownLatch(numAccounts);
//
//        List<Future<Boolean>> futures = new ArrayList<>();
//
//        for (int i = 0; i < numAccounts; i++) {
//            int finalI = i;
//            futures.add(executor.submit(() -> {
//                try {
//                    bank.createAccount(new AccountNumber(String.valueOf(finalI)), "User " + finalI, new Money(100));
//                    latch.countDown();
//                    return true;
//                } catch (AccountNumberAlreadyExistsException e) {
//                    return false;
//                }
//            }));
//        }
//
//        latch.await(30, TimeUnit.SECONDS);
//        executor.shutdown();
//        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
//
//        long successfulCreations = futures.stream().filter(f -> {
//            try {
//                return f.get();
//            } catch (Exception e) {
//                return false;
//            }
//        }).count();
//
//        assertEquals(numAccounts, successfulCreations, "All account creations should be successful");
//        assertEquals(numAccounts, bank.getAccountCount(), "Bank should have the correct number of accounts");
//    }
}

