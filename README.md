# Banking Service

A lightweight in-memory banking service built in Java that simulates core banking operations such as account creation, deposits, withdrawals, transfers, and balance management.

## Overview

This project was built as a software module focused on clean architecture, correctness, and thread safety.

The goal is to simulate basic banking operations while reflecting real-world constraints such as:

* Preventing overdrafts
* Validating monetary input
* Ensuring atomic transfers
* Supporting concurrent access safely

This implementation uses in-memory storage and does not expose an API, as per the exercise requirements.

---

## Features

* Create accounts with an initial deposit
* Deposit funds into an account
* Withdraw funds from an account
* Transfer funds between accounts
* Retrieve account balances
* Thread-safe account operations
* Atomic transfer execution


---

## Design Decisions

### BigDecimal for Money

All monetary values use `BigDecimal` to avoid floating-point precision issues.

### Value Object for Money

Money validation and normalization are centralized in a dedicated value object.

### Repository Abstraction

Storage is abstracted through a repository interface, allowing easy replacement with a database-backed implementation in the future.

### Thread Safety

Account mutations (`deposit`, `withdraw`) are synchronized.

Transfers lock both accounts in deterministic order to prevent deadlocks.

### Atomic Transfers

Transfers either complete fully or fail without partial updates.

---

## Assumptions

* Account IDs are generated using UUID
* Initial deposit may be zero
* Deposits and withdrawals must be greater than zero
* Negative amounts are not allowed
* Currency handling is out of scope
* Transaction history is out of scope

---

## Running the Project

### Prerequisites

* Java 21
* Maven 3+

### Build

```bash
mvn clean install
```

### Run

```bash
mvn exec:java
```

Or run `Main.java` directly from IDE.

---

## Example Usage

```java
BankingService bankingService =
        new BankingServiceImpl(new InMemoryAccountRepository());

UUID accountA = bankingService.createAccount(new BigDecimal("100.00"));
UUID accountB = bankingService.createAccount(new BigDecimal("50.00"));

bankingService.deposit(accountA, new BigDecimal("20.00"));
bankingService.withdraw(accountB, new BigDecimal("10.00"));
bankingService.transfer(accountA, accountB, new BigDecimal("30.00"));

System.out.println(bankingService.getBalance(accountA));
System.out.println(bankingService.getBalance(accountB));
```

---

## Testing

Unit tests cover:

* Account creation
* Deposits
* Withdrawals
* Overdraft prevention
* Transfers
* Invalid inputs
* Concurrent operations
