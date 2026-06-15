package com.firstcircle.banking.service;

import com.firstcircle.banking.exception.AccountNotFoundException;
import com.firstcircle.banking.exception.InsufficientFundsException;
import com.firstcircle.banking.exception.InvalidAmountException;
import com.firstcircle.banking.repository.InMemoryAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankingServiceImplTest {

    private BankingService bankingService;

    @BeforeEach
    void setUp() {
        bankingService = new BankingServiceImpl(new InMemoryAccountRepository());
    }

    @Test
    void shouldCreateAccountWithInitialDeposit() {
        UUID accountId = bankingService.createAccount(new BigDecimal("100.00"));

        assertThat(bankingService.getBalance(accountId))
                .isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void shouldAllowCreatingAccountWithZeroInitialDeposit() {
        UUID accountId = bankingService.createAccount(BigDecimal.ZERO);

        assertThat(bankingService.getBalance(accountId))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldDepositMoney() {
        UUID accountId = bankingService.createAccount(new BigDecimal("100.00"));

        bankingService.deposit(accountId, new BigDecimal("50.00"));

        assertThat(bankingService.getBalance(accountId))
                .isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    void shouldWithdrawMoney() {
        UUID accountId = bankingService.createAccount(new BigDecimal("100.00"));

        bankingService.withdraw(accountId, new BigDecimal("40.00"));

        assertThat(bankingService.getBalance(accountId))
                .isEqualByComparingTo(new BigDecimal("60.00"));
    }

    @Test
    void shouldTransferMoneyBetweenAccounts() {
        UUID fromAccountId = bankingService.createAccount(new BigDecimal("100.00"));
        UUID toAccountId = bankingService.createAccount(new BigDecimal("20.00"));

        bankingService.transfer(fromAccountId, toAccountId, new BigDecimal("30.00"));

        assertThat(bankingService.getBalance(fromAccountId))
                .isEqualByComparingTo(new BigDecimal("70.00"));

        assertThat(bankingService.getBalance(toAccountId))
                .isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void shouldNotAllowOverdraftOnWithdrawal() {
        UUID accountId = bankingService.createAccount(new BigDecimal("100.00"));

        assertThatThrownBy(() ->
                bankingService.withdraw(accountId, new BigDecimal("150.00"))
        ).isInstanceOf(InsufficientFundsException.class);

        assertThat(bankingService.getBalance(accountId))
                .isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void shouldNotTransferWhenSourceHasInsufficientFunds() {
        UUID fromAccountId = bankingService.createAccount(new BigDecimal("20.00"));
        UUID toAccountId = bankingService.createAccount(new BigDecimal("100.00"));

        assertThatThrownBy(() ->
                bankingService.transfer(fromAccountId, toAccountId, new BigDecimal("50.00"))
        ).isInstanceOf(InsufficientFundsException.class);

        assertThat(bankingService.getBalance(fromAccountId))
                .isEqualByComparingTo(new BigDecimal("20.00"));

        assertThat(bankingService.getBalance(toAccountId))
                .isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void shouldRejectNegativeInitialDeposit() {
        assertThatThrownBy(() ->
                bankingService.createAccount(new BigDecimal("-1.00"))
        ).isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void shouldRejectZeroDeposit() {
        UUID accountId = bankingService.createAccount(new BigDecimal("100.00"));

        assertThatThrownBy(() ->
                bankingService.deposit(accountId, BigDecimal.ZERO)
        ).isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void shouldRejectNegativeDeposit() {
        UUID accountId = bankingService.createAccount(new BigDecimal("100.00"));

        assertThatThrownBy(() ->
                bankingService.deposit(accountId, new BigDecimal("-10.00"))
        ).isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void shouldRejectZeroWithdrawal() {
        UUID accountId = bankingService.createAccount(new BigDecimal("100.00"));

        assertThatThrownBy(() ->
                bankingService.withdraw(accountId, BigDecimal.ZERO)
        ).isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void shouldRejectNegativeWithdrawal() {
        UUID accountId = bankingService.createAccount(new BigDecimal("100.00"));

        assertThatThrownBy(() ->
                bankingService.withdraw(accountId, new BigDecimal("-10.00"))
        ).isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void shouldRejectTransferToSameAccount() {
        UUID accountId = bankingService.createAccount(new BigDecimal("100.00"));

        assertThatThrownBy(() ->
                bankingService.transfer(accountId, accountId, new BigDecimal("10.00"))
        ).isInstanceOf(IllegalArgumentException.class);

        assertThat(bankingService.getBalance(accountId))
                .isEqualByComparingTo(new BigDecimal("100.00"));
    }

    @Test
    void shouldRejectMissingAccount() {
        UUID missingAccountId = UUID.randomUUID();

        assertThatThrownBy(() ->
                bankingService.getBalance(missingAccountId)
        ).isInstanceOf(AccountNotFoundException.class);
    }
}