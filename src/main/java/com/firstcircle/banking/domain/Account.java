package com.firstcircle.banking.domain;

import com.firstcircle.banking.exception.InsufficientFundsException;
import com.firstcircle.banking.exception.InvalidAmountException;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {

    private final UUID id;
    private Money balance;

    public Account(BigDecimal initialDeposit) {
        this.id = UUID.randomUUID();
        this.balance = new Money(initialDeposit);
    }

    public UUID getId() {
        return id;
    }

    public synchronized BigDecimal getBalance() {
        return balance.amount();
    }

    public synchronized void deposit(BigDecimal amount) {
        validatePositiveAmount(amount);
        balance = balance.add(new Money(amount));
    }

    public synchronized void withdraw(BigDecimal amount) {
        validatePositiveAmount(amount);

        Money money = new Money(amount);

        if (balance.isLessThan(money)) {
            throw new InsufficientFundsException();
        }

        balance = balance.subtract(money);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAmountException("Amount cannot be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }
    }
}