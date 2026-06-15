package com.firstcircle.banking;

import com.firstcircle.banking.repository.InMemoryAccountRepository;
import com.firstcircle.banking.service.BankingService;
import com.firstcircle.banking.service.BankingServiceImpl;

import java.math.BigDecimal;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        BankingService bankingService =
                new BankingServiceImpl(new InMemoryAccountRepository());

        UUID nikola = bankingService.createAccount(new BigDecimal("100.00"));
        UUID ana = bankingService.createAccount(new BigDecimal("50.00"));

        System.out.println("Initial balances:");
        System.out.println("Nikola: " + bankingService.getBalance(nikola));
        System.out.println("Ana: " + bankingService.getBalance(ana));

        bankingService.deposit(nikola, new BigDecimal("25.00"));
        bankingService.withdraw(ana, new BigDecimal("10.00"));
        bankingService.transfer(nikola, ana, new BigDecimal("40.00"));

        System.out.println();
        System.out.println("Final balances:");
        System.out.println("Nikola: " + bankingService.getBalance(nikola));
        System.out.println("Ana: " + bankingService.getBalance(ana));

        try {
            bankingService.withdraw(ana, new BigDecimal("1000.00"));
        } catch (Exception exception) {
            System.out.println();
            System.out.println("Expected error: " + exception.getMessage());
        }
    }
}