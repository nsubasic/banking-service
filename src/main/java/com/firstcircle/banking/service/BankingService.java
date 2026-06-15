package com.firstcircle.banking.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface BankingService {

    UUID createAccount(BigDecimal initialDeposit);

    void deposit(UUID accountId, BigDecimal amount);

    void withdraw(UUID accountId, BigDecimal amount);

    void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount);

    BigDecimal getBalance(UUID accountId);
}