package com.firstcircle.banking.service;

import com.firstcircle.banking.domain.Account;
import com.firstcircle.banking.exception.AccountNotFoundException;
import com.firstcircle.banking.exception.InvalidAmountException;
import com.firstcircle.banking.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.UUID;

public class BankingServiceImpl implements BankingService {

    private final AccountRepository accountRepository;

    public BankingServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UUID createAccount(BigDecimal initialDeposit) {
        Account account = new Account(initialDeposit);
        accountRepository.save(account);
        return account.getId();
    }

    @Override
    public void deposit(UUID accountId, BigDecimal amount) {
        Account account = findAccount(accountId);
        account.deposit(amount);
    }

    @Override
    public void withdraw(UUID accountId, BigDecimal amount) {
        Account account = findAccount(accountId);
        account.withdraw(amount);
    }

    @Override
    public void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        validateTransfer(fromAccountId, toAccountId, amount);

        Account fromAccount = findAccount(fromAccountId);
        Account toAccount = findAccount(toAccountId);

        Account firstLock = getFirstLock(fromAccount, toAccount);
        Account secondLock = getSecondLock(fromAccount, toAccount);

        synchronized (firstLock) {
            synchronized (secondLock) {
                fromAccount.withdraw(amount);
                toAccount.deposit(amount);
            }
        }
    }

    @Override
    public BigDecimal getBalance(UUID accountId) {
        return findAccount(accountId).getBalance();
    }

    private Account findAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private void validateTransfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        if (fromAccountId == null) {
            throw new IllegalArgumentException("Source account id cannot be null");
        }

        if (toAccountId == null) {
            throw new IllegalArgumentException("Target account id cannot be null");
        }

        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive");
        }
    }

    private Account getFirstLock(Account firstAccount, Account secondAccount) {
        return Comparator.comparing(Account::getId)
                .compare(firstAccount, secondAccount) < 0 ? firstAccount : secondAccount;
    }

    private Account getSecondLock(Account firstAccount, Account secondAccount) {
        return Comparator.comparing(Account::getId)
                .compare(firstAccount, secondAccount) < 0 ? secondAccount : firstAccount;
    }
}