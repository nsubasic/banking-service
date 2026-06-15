package com.firstcircle.banking.repository;

import com.firstcircle.banking.domain.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(UUID accountId);
}