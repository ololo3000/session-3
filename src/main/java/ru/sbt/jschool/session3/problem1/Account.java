package ru.sbt.jschool.session3.problem1;

import java.util.Objects;

/**
 */
public class Account {
    private final long clientID;

    private final long accountID;

    private final Currency currency;

    private volatile AtomicFloat balance = new AtomicFloat(0f);

    public Account(long clientID, long accountID, Currency currency, float balance) {
        this.clientID = clientID;
        this.accountID = accountID;
        this.currency = currency;
        this.balance.set(balance);
    }

    public long getClientID() {
        return clientID;
    }

    public long getAccountID() {
        return accountID;
    }

    public Currency getCurrency() {
        return currency;
    }

    public float getBalance() {
        return balance.get();
    }

    public void setBalance(float balance) {
        this.balance.set(balance);
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Account account = (Account)o;
        return clientID == account.clientID &&
            accountID == account.accountID &&
            Float.compare(account.balance.get(), balance.get()) == 0 &&
            currency == account.currency;
    }

    @Override public int hashCode() {
        return Objects.hash(clientID, accountID, currency, balance);
    }
}
