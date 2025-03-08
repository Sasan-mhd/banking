package com.sasan.banking;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Scanner;

public class BankingSystemUI {
    private final Bank bank;
    private final Scanner scanner;

    public BankingSystemUI(Bank bank) {
        this.bank = bank;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Check Balance");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline left-over

            switch (option) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    deposit();
                    break;
                case 3:
                    withdraw();
                    break;
                case 4:
                    transfer();
                    break;
                case 5:
                    checkBalance();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }

    private void createAccount() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter account holder name: ");
        String accountHolderName = scanner.nextLine();
        System.out.print("Enter initial balance: ");
        double initialBalance = scanner.nextDouble();
        scanner.nextLine(); // Consume newline left-over

        try {
            bank.createAccount(new AccountNumber(accountNumber), accountHolderName, new Money(initialBalance));
            System.out.println("Account created successfully.");
        } catch (AccountNumberAlreadyExistsException e) {
            System.out.println("Account number already exists.");
        }
    }

    private void deposit() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline left-over

        try {
            bank.deposit(new AccountNumber(accountNumber), new Money(amount));
            System.out.println("Deposit successful.");
        } catch (AccountDoesNotExistException e) {
            System.out.println("Account doesn't exist.");
        }
    }

    private void withdraw() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline left-over

        try {
            bank.withdraw(new AccountNumber(accountNumber), new Money(amount));
            System.out.println("Withdrawal successful.");
        } catch (InsufficientAmountException e) {
            System.out.println("Insufficient balance.");
        } catch (AccountDoesNotExistException e) {
            System.out.println("Account doesn't exist.");
        }
    }

    private void transfer() {
        System.out.print("Enter sender account number: ");
        String senderAccountNumber = scanner.nextLine();
        System.out.print("Enter recipient account number: ");
        String recipientAccountNumber = scanner.nextLine();
        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline left-over

        try {
            bank.transfer(new AccountNumber(senderAccountNumber), new AccountNumber(recipientAccountNumber), new Money(amount));
            System.out.println("Transfer successful.");
        } catch (InsufficientAmountException e) {
            System.out.println("Insufficient balance.");
        } catch (AccountDoesNotExistException e) {
            System.out.println("Account doesn't exist.");
        }
    }

    private void checkBalance() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();

        BankAccount account = bank.getAccount(new AccountNumber(accountNumber));
        if (account != null) {
            System.out.println("Balance: " + account.getBalance().amount());
        } else {
            System.out.println("Account not found.");
        }
    }
}
