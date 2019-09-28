package com.dude.dms.backend.service;

import com.dude.dms.backend.data.entity.Account;
import com.dude.dms.backend.data.entity.AccountHistory;
import com.dude.dms.backend.repositories.AccountHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountHistoryService implements HistoricalCrudService<Account, AccountHistory> {

    private final AccountHistoryRepository accountHistoryRepository;

    @Autowired
    public AccountHistoryService(AccountHistoryRepository accountHistoryRepository) {
        this.accountHistoryRepository = accountHistoryRepository;
    }

    @Override
    public AccountHistoryRepository getRepository() {
        return accountHistoryRepository;
    }

    @Override
    public List<AccountHistory> getHistory(Account entity) {
        return accountHistoryRepository.findByAccount(entity);
    }
}
