package com.dude.dms.backend.data.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class AccountHistory extends History {

    @Id
    @GeneratedValue
    private Long accountHistoryId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "accountId")
    private Account account;

    public AccountHistory() {

    }

    public AccountHistory(Account account, User historyUser, String text, boolean created, boolean edited, boolean deleted) {
        super(historyUser, text, created, edited, deleted);
        this.account = account;
    }

    public Long getAccountHistoryId() {
        return accountHistoryId;
    }

    @Override
    public Long getId() {
        return accountHistoryId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
